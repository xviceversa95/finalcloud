package ru.netology.FinalCloud.FileCloud.controller;

import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.FinalCloud.FileCloud.model.FileDto;
import ru.netology.FinalCloud.FileCloud.model.FileMapper;
import ru.netology.FinalCloud.FileCloud.model.ModifyRequest;
import ru.netology.FinalCloud.FileCloud.service.FileMeta;
import ru.netology.FinalCloud.FileCloud.service.FileService;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;


@RestController
public class FileController {


    public FileService service;
    public FileMapper mapper;
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    public FileController(FileService service, FileMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping("/file")
    public ResponseEntity<String> fileUpload(@RequestParam(value = "filename") String filename, @RequestParam MultipartFile file, @RequestHeader("auth-token") String authToken) {
        logger.info("Получен запрос на загрузку файла");
        service.upload(file, filename, authToken);
        return ResponseEntity.ok("Success upload");
    }

    @GetMapping("/file")
    public ResponseEntity<Resource> fileDownload(@RequestParam(value = "filename", required = true) String filename, @RequestHeader("auth-token") String authToken) {
        logger.info("Получен запрос на скачивание файла");
        var fileToDownload = service.getDownloadFile(filename, authToken);

        String mimeType;
        try {
            mimeType = Files.probeContentType(fileToDownload.toPath());
        } catch (IOException e) {
            mimeType = null;
        }

        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .header("X-Message", "Success download")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(mimeType))
                .body(new FileSystemResource(fileToDownload.toPath()));
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileDto>> getFileList(@RequestParam(value = "limit", required = true) Integer limit, @RequestHeader("auth-token") String authToken) throws BadRequestException {
        logger.info("Запрос на контроллер со списком файлов");
        List<FileMeta> fileList = service.getFileList(authToken);
        List<FileDto> dtoList = fileList.stream().map(mapper::toDto).limit(limit).toList();
        logger.info(dtoList.toString());
        return ResponseEntity.ok(dtoList);
    }

    @DeleteMapping("/file")
    public ResponseEntity<String> deleteFile(@RequestParam(value = "filename", required = true) String filename, @RequestHeader("auth-token") String authToken) {
        logger.info("Получен запрос на удаление файла");
        service.deleteFile(filename, authToken);
        return ResponseEntity.ok("Success deleted");
    }

    @PutMapping("/file")
    public ResponseEntity<String> renameFile(@RequestParam(value = "filename", required = true) String filename, @RequestBody ModifyRequest request, @RequestHeader("auth-token") String authToken) throws BadRequestException {
        logger.info("Получен запрос на переименования файла");
        String newFilename = request.getNewFilename();
        FileMeta meta = service.renameFile(filename, newFilename, authToken);
        return ResponseEntity.ok("Success renamed");
    }
}
