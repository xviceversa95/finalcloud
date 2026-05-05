package ru.netology.FinalCloud.FileCloud;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.netology.FinalCloud.FileCloud.repository.FileRepository;
import ru.netology.FinalCloud.FileCloud.service.FileChecker;
import ru.netology.FinalCloud.FileCloud.service.FileMeta;
import ru.netology.FinalCloud.FileCloud.service.FileService;
import ru.netology.FinalCloud.FileCloud.service.FileServiceProperties;
import ru.netology.FinalCloud.Users.models.MyUserDetails;
import ru.netology.FinalCloud.Users.models.User;
import ru.netology.FinalCloud.Users.service.JWTService;
import ru.netology.FinalCloud.Users.service.MyUserDetailsService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)

public class FileServiceTests {

    @Mock
    private FileRepository fileRepository;

    @Mock
    private JWTService jwtService;

    @Mock
    private FileServiceProperties properties;

    @Mock
    private FileChecker checker;

    @InjectMocks
    private FileService fileService;

    private MyUserDetails user;

    @BeforeEach
    public void setup() {
        User basicUser = new User("test", "password");
        basicUser.setId(1);
        user = new MyUserDetails(basicUser);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(properties.getAllowedMimeTypes()).thenReturn(Set.of("text/plain", "image/jpeg",
                "image/png", "image/gif", "application/json", "application/pdf",
                "audio/mpeg", "video/mp4", "video/webm", "application/octet-stream"));
        when(properties.getSTORAGE_DIRECTORY()).thenReturn("C:\\Storage");

    }

    @Test
    public void testUploadMethod(){

        MockMultipartFile file =  new MockMultipartFile("filename", "testFile.txt", "text/plain", "test".getBytes(StandardCharsets.UTF_8));
        String filename = "testFile.txt";
        String header = "Bearer valid-token";
        int expectedSize = Math.toIntExact(file.getSize());
        String expectedContentType = "text/plain";

        when(jwtService.validateToken(anyString(), any(MyUserDetails.class))).thenReturn(true);

        ArgumentCaptor<FileMeta> captor = ArgumentCaptor.forClass(FileMeta.class);

        FileMeta testmeta = new FileMeta();
        when(fileRepository.save(any(FileMeta.class))).thenReturn(testmeta);

        FileMeta savedMeta = fileService.upload(file,filename,header);
        verify(fileRepository).save(captor.capture());
        FileMeta createdMeta = captor.getValue();


        assertNotNull(savedMeta);
        assertEquals(filename, createdMeta.getOriginalName());
        assertEquals(expectedSize, createdMeta.getSize());
        assertEquals(expectedContentType, createdMeta.getMimeType());
    }

    @Test
    public void testGetExtensionMethod(){
        String filename = "filename.txt";
        String expectedExtension = ".txt";

        String extension = fileService.getExtension(filename);

        assertNotNull(extension);
        assertEquals(expectedExtension, extension);
    }

    @Test
    public void testGetSizeMethod() throws IOException {
       Path tempPath = Files.createTempFile("testfile", ".txt");
       Files.write(tempPath, "Некоторое содержимое файла".getBytes(StandardCharsets.UTF_8));
       File tempFile = tempPath.toFile();
       int expectedSize = Math.toIntExact(tempFile.length());

       int size = fileService.getSize(tempFile);

       assertEquals(expectedSize, size);
    }

    @Test
    public void testGetPathMethod() throws IOException {

        Path tempPath = Files.createTempFile("testfile", ".txt");
        Files.write(tempPath, "Некоторое содержимое файла".getBytes(StandardCharsets.UTF_8));
        File tempFile = tempPath.toFile();

        String expectedPath = tempFile.getPath();

        String path = fileService.getPath(tempFile);

        assertEquals(expectedPath, path);
    }

    @Test
    public void testGetTokenFromHeaderMethod(){
        String header = "Bearer valid-token";

        String token = fileService.getTokenFromHeader(header);

        assertEquals("valid-token", token);
    }

    @Test
    public void testGetDownloadFileMethod(){
        when(jwtService.validateToken(anyString(), any(MyUserDetails.class))).thenReturn(true);

        String filename = "testFile.txt";
        String header = "Bearer valid-token";

        FileMeta mockedFoundMeta = new FileMeta("id", 1, "testFile.txt", "text/plain",
                ".txt", "generatedName.txt", 110, LocalDateTime.now(), "C:\\Storage\\22626292-e67a-422b-9b15-214ba1f1a850.txt");

        when(fileRepository.findByOriginalNameAndUserId(anyString(), anyInt())).thenReturn(mockedFoundMeta);

        when(checker.isFileAccessible(any())).thenReturn(true);
        when(checker.hasValidDirectory(any())).thenReturn(true);

        File file = fileService.getDownloadFile(filename, header);

        verify(fileRepository).findByOriginalNameAndUserId(filename, 1);
        verify(checker).hasValidDirectory(file);
        verify(checker).isFileAccessible(file);
        assertNotNull(file);
    }

    @Test
    public void testDeleteFileMethod(){
        String filename = "testFile.txt";
        String header = "Bearer valid-token";

        when(jwtService.validateToken(anyString(), any(MyUserDetails.class))).thenReturn(true);

        FileMeta mockedFoundMeta = new FileMeta("id", 1, "testFile.txt", "text/plain",
                ".txt", "generatedName.txt", 110, LocalDateTime.now(), "C:\\Storage\\22626292-e67a-422b-9b15-214ba1f1a850.txt");

        when(fileRepository.findByOriginalNameAndUserId(anyString(), anyInt())).thenReturn(mockedFoundMeta);

        fileService.deleteFile(filename, header);

        verify(fileRepository).findByOriginalNameAndUserId(filename, 1);

    }

    @Test
    public void testGetFileListMethod(){
        when(jwtService.validateToken(anyString(), any(MyUserDetails.class))).thenReturn(true);

        String filename = "testFile.txt";
        String header = "Bearer valid-token";

        FileMeta mockedFoundMeta = new FileMeta("id", 1, "testFile.txt", "text/plain",
                ".txt", "generatedName.txt", 110, LocalDateTime.now(), "C:\\Storage\\22626292-e67a-422b-9b15-214ba1f1a850.txt");

        List<FileMeta> mockFileList = new ArrayList<>();
        mockFileList.add(mockedFoundMeta);

        when(fileRepository.findAllByUserId(user.getId())).thenReturn(mockFileList);

        List<FileMeta> fileList = fileService.getFileList(header);
        assertNotNull(fileList);
        assertEquals(1, fileList.size());
        assertEquals(mockedFoundMeta, fileList.get(0));

    }
}
