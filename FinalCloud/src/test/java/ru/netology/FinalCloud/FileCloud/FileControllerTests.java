package ru.netology.FinalCloud.FileCloud;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.netology.FinalCloud.FileCloud.controller.FileController;
import ru.netology.FinalCloud.FileCloud.controller.GlobalFileExceptionHandler;
import ru.netology.FinalCloud.FileCloud.controller.errors.GeneralRequestException;
import ru.netology.FinalCloud.FileCloud.controller.errors.GeneralServerError;
import ru.netology.FinalCloud.FileCloud.controller.errors.UnauthorizedException;
import ru.netology.FinalCloud.FileCloud.model.FileDto;
import ru.netology.FinalCloud.FileCloud.model.FileMapper;
import ru.netology.FinalCloud.FileCloud.model.ModifyRequest;
import ru.netology.FinalCloud.FileCloud.service.FileMeta;
import ru.netology.FinalCloud.FileCloud.service.FileService;
import ru.netology.FinalCloud.Users.repository.JwtBlackListRepo;
import ru.netology.FinalCloud.Users.repository.UserRepo;
import ru.netology.FinalCloud.Users.service.JWTBlackListService;
import ru.netology.FinalCloud.Users.service.JWTService;
import tools.jackson.databind.ObjectMapper;


import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@WebMvcTest(FileController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalFileExceptionHandler.class)

public class FileControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FileService fileService;

    @MockitoBean
    private JWTService jwtService;

    @MockitoBean
    private JWTBlackListService jwtBlackListService;

    @MockitoBean
    private JwtBlackListRepo jwtBlackListRepo;

    @MockitoBean
    private UserRepo userRepo;

    @MockitoBean
    private AuthenticationManager authManager;

    @MockitoBean
    private FileMapper mapper;

//тесты на контроллер загрузки файла
    @Test
    public void testSuccessfullyFileUploadMethod() throws Exception {
        FileMeta fileMeta = new FileMeta("id", 1, "testfile.txt", "text/plain",
                ".txt", "generatedName.txt", 110, LocalDateTime.now(), "testPath");

        when(fileService.upload(any(), anyString(), anyString())).thenReturn(fileMeta);

        MockMultipartFile file = new MockMultipartFile("file", "testfile.txt",
                "text/plain", "Test Content".getBytes());


        mockMvc.perform(multipart("/file")
                .file(file)
                .param("filename", "test.txt")
                .header("auth-token", "valid_token")
        )
                .andExpect(status().isOk())
                .andExpect(content().string("Success upload"));

    }

    @Test
    public void testFailedFileUploadMethod() throws Exception {

        when(fileService.upload(any(), anyString(), anyString())).thenThrow(new GeneralRequestException("Error input data"));

        MockMultipartFile file = new MockMultipartFile("file", "testfile.txt",
                "text/plain", "Test Content".getBytes());


        mockMvc.perform(multipart("/file")
                        .file(file)
                        .param("filename", "test.txt")
                        .header("auth-token", "valid_token")
                )
                .andExpect(jsonPath("$.message").value("Error input data"))
                .andExpect(jsonPath("$.id").value(400));
    }

    @Test
    public void testUnauthorizedFileUploadMethod() throws Exception {

        when(fileService.upload(any(), anyString(), anyString())).thenThrow(new UnauthorizedException("Unauthorized Error"));

        MockMultipartFile file = new MockMultipartFile("file", "testfile.txt",
                "text/plain", "Test Content".getBytes());


        mockMvc.perform(multipart("/file")
                        .file(file)
                        .param("filename", "test.txt")
                        .header("auth-token", "valid_token")
                )
                .andExpect(jsonPath("$.message").value("Unauthorized Error"))
                .andExpect(jsonPath("$.id").value(401));
    }

//тесты на контроллер загрузки файла с сервера
    @Test
    public void testSuccessfullyFileDownload() throws Exception {
        String filename = "testfile.txt";
        String authToken = "valid_token";

        Path tempPath = Files.createTempFile("testfile", ".txt");
        Files.write(tempPath, "Некоторое содержимое файла".getBytes(StandardCharsets.UTF_8));
        File tempFile = tempPath.toFile();

        when(fileService.getDownloadFile(anyString(), anyString())).thenReturn(tempFile);

        mockMvc.perform(get("/file")
                        .param("filename", filename)
                        .header("auth-token", authToken))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Message", "Success download"))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\""))
                .andExpect(content().contentType("text/plain"));
    }

    @Test
    public void testUnauthorizedFileDownloadMethod() throws Exception {
        String filename = "testfile.txt";
        String authToken = "valid_token";

        when(fileService.getDownloadFile(anyString(), anyString())).thenThrow(new UnauthorizedException("Unauthorized Error"));

        mockMvc.perform(get("/file")
                .param("filename", filename)
                .header("auth-token", authToken)
                )
                .andExpect(jsonPath("$.message").value("Unauthorized Error"))
                .andExpect(jsonPath("$.id").value(401));
    }

    @Test
    public void testFailedDownloadMethod() throws Exception {
        String filename = "testfile.txt";
        String authToken = "valid_token";

        when(fileService.getDownloadFile(anyString(), anyString())).thenThrow(new GeneralServerError("Error downloading file"));

        mockMvc.perform(get("/file")
                .param("filename", filename)
                .header("auth-token", authToken)
                )

                .andExpect(jsonPath("$.message").value("Error downloading file"))
                .andExpect(jsonPath("$.id").value(500));
    }

    @Test
    public void testSuccessfullyFileDeleteMethod() throws Exception {
        String filename = "testfile.txt";
        String authToken = "valid_token";

        mockMvc.perform(delete("/file")
                .param("filename", filename)
                .header("auth-token", authToken)
        )
                .andExpect(status().isOk())
                .andExpect(content().string("Success deleted"));
    }

    @Test
    public void testFailedFileDeleteMethod() throws Exception {
        String filename = "testfile.txt";
        String authToken = "valid_token";

        Mockito.doThrow(new GeneralServerError("Error deleting file"))
                .when(fileService)
                .deleteFile(anyString(), anyString());

        mockMvc.perform(delete("/file")
                        .param("filename", filename)
                        .header("auth-token", authToken)
                )

                .andExpect(jsonPath("$.message").value("Error deleting file"))
                .andExpect(jsonPath("$.id").value(500));

    }

    @Test
    public void testUnauthorizedFileDeleteMethod() throws Exception {
        String filename = "testfile.txt";
        String authToken = "valid_token";

        Mockito.doThrow(new UnauthorizedException("Unauthorized Error"))
                .when(fileService)
                .deleteFile(anyString(), anyString());

        mockMvc.perform(delete("/file")
                        .param("filename", filename)
                        .header("auth-token", authToken)
                )

                .andExpect(jsonPath("$.message").value("Unauthorized Error"))
                .andExpect(jsonPath("$.id").value(401));
    }

    @Test
    public void testSuccessfullyFileRenameFileMethod() throws Exception {
        String authToken = "valid_token";

        FileMeta fileMeta = new FileMeta("id", 1, "newtestfile.txt", "text/plain",
                ".txt", "generatedName.txt", 110, LocalDateTime.now(), "testPath");

        ModifyRequest request = new ModifyRequest("newfilename");
        String jsonRequst = new ObjectMapper().writeValueAsString(request);

        when(fileService.renameFile(anyString(), anyString(), anyString())).thenReturn(fileMeta);

        mockMvc.perform(put("/file")
                .param("filename", "oldfilename")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequst)
                .header("auth-token", authToken)
        )
                .andExpect(status().isOk())
                .andExpect(content().string("Success renamed"));
    }

    @Test
    public void testFailedFileRenameFileMethod() throws Exception {
        String authToken = "valid_token";

        ModifyRequest request = new ModifyRequest("newfilename");
        String jsonRequst = new ObjectMapper().writeValueAsString(request);

        when(fileService.renameFile(anyString(), anyString(), anyString())).thenThrow(new GeneralRequestException("Error input data"));

        mockMvc.perform(put("/file")
                        .param("filename", "oldfilename")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequst)
                        .header("auth-token", authToken)
                )

                .andExpect(jsonPath("$.message").value("Error input data"))
                .andExpect(jsonPath("$.id").value(400));
    }

    @Test
    public void testUnauthorizedFileRenameFileMethod() throws Exception {
        String authToken = "valid_token";

        ModifyRequest request = new ModifyRequest("newfilename");
        String jsonRequst = new ObjectMapper().writeValueAsString(request);

        when(fileService.renameFile(anyString(), anyString(), anyString())).thenThrow(new UnauthorizedException("Unauthorized Error"));

        mockMvc.perform(put("/file")
                        .param("filename", "oldfilename")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequst)
                        .header("auth-token", authToken)
                )

                .andExpect(jsonPath("$.message").value("Unauthorized Error"))
                .andExpect(jsonPath("$.id").value(401));
    }

    @Test
    public void testSuccessfullyGetFileListMethod() throws Exception {
        String authToken = "valid_token";

        FileMeta meta1 = new FileMeta("id", 1, "file1.txt", "text/plain",
                ".txt", "generatedName.txt", 110, LocalDateTime.now(), "testPath");

        FileMeta meta2 = new FileMeta("id", 1, "file2.txt", "text/plain",
                ".txt", "generatedName.txt", 110, LocalDateTime.now(), "testPath");

        FileMeta meta3 = new FileMeta("id", 1, "file3.txt", "text/plain",
                ".txt", "generatedName.txt", 110, LocalDateTime.now(), "testPath");

        FileMeta meta4 = new FileMeta("id", 1, "file4.txt", "text/plain",
                ".txt", "generatedName.txt", 110, LocalDateTime.now(), "testPath");

        FileDto file1 = new FileDto(meta1);
        FileDto file2 = new FileDto(meta2);
        FileDto file3 = new FileDto(meta3);
        FileDto file4 = new FileDto(meta4);

        List<FileMeta> metaList = Arrays.asList(meta1, meta2, meta3, meta4);
        List<FileDto> fileList = Arrays.asList(file1, file2, file3, file4);

        when(fileService.getFileList(anyString())).thenReturn(metaList);
        when(mapper.toDto(meta1)).thenReturn(file1);
        when(mapper.toDto(meta2)).thenReturn(file2);
        when(mapper.toDto(meta3)).thenReturn(file3);
        when(mapper.toDto(meta4)).thenReturn(file4);

        mockMvc.perform(get("/list")
                .param("limit", "3")
                .header("auth-token", authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].filename", is("file1.txt")))
                .andExpect(jsonPath("$[0].size", is(110)))
                .andExpect(jsonPath("$[1].filename", is("file2.txt")))
                .andExpect(jsonPath("$[1].size", is(110)))
                .andExpect(jsonPath("$[2].filename", is("file3.txt")))
                .andExpect(jsonPath("$[2].size", is(110)));

    }

    @Test
    public void testFailedGetFileListMethod() throws Exception {
        String authToken = "valid_token";

        when(fileService.getFileList(anyString())).thenThrow(new GeneralServerError("Error getting file list"));

        mockMvc.perform(get("/list")
                        .param("limit", "3")
                        .header("auth-token", authToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Error getting file list"))
                .andExpect(jsonPath("$.id").value(500));
    }

    @Test
    public void testFailedRequestGetFileListMethod() throws Exception {
        String authToken = "valid_token";

        when(fileService.getFileList(anyString())).thenThrow(new GeneralRequestException("Error input data"));

        mockMvc.perform(get("/list")
                        .param("limit", "3")
                        .header("auth-token", authToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Error input data"))
                .andExpect(jsonPath("$.id").value(400));
    }

    @Test
    public void testUnauthorizedRequestGetFileListMethod() throws Exception {
        String authToken = "valid_token";

        when(fileService.getFileList(anyString())).thenThrow(new UnauthorizedException("Unauthorized Error"));

        mockMvc.perform(get("/list")
                        .param("limit", "3")
                        .header("auth-token", authToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Unauthorized Error"))
                .andExpect(jsonPath("$.id").value(401));
    }
}
