package ru.netology.FinalCloud.FileCloud.service;

import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.FinalCloud.FileCloud.controller.errors.GeneralRequestException;
import ru.netology.FinalCloud.FileCloud.controller.errors.GeneralServerError;
import ru.netology.FinalCloud.FileCloud.controller.errors.UnauthorizedException;
import ru.netology.FinalCloud.FileCloud.repository.FileRepository;
import ru.netology.FinalCloud.Users.models.MyUserDetails;
import ru.netology.FinalCloud.Users.service.JWTService;
import ru.netology.FinalCloud.Users.service.UserService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Service
public class FileService {

    public FileServiceProperties properties;
    public FileRepository fileRepository;
    public UserService userService;
    public JWTService jwtService;
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);
    public FileChecker checker;

    @Autowired
    public FileService(FileServiceProperties properties, FileRepository fileRepository, UserService userService, JWTService jwtService,
                       FileChecker checker) {
        this.properties = properties;
        this.fileRepository = fileRepository;
        this.userService = userService;
        this.jwtService = jwtService;
        this.checker = checker;
    }

    public FileMeta upload(MultipartFile file, String filename, String header) {

        logger.info("Запрос на загрузку файла получен");

        MyUserDetails user = (MyUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user == null) {
            throw new UnauthorizedException("Unauthorized Error");
        }

        if (!jwtService.validateToken(getTokenFromHeader(header), user)) {
            logger.info("Ошибка валидации токена");
            throw new UnauthorizedException("Unauthorized error");
        }

        logger.info("Валидация успешна");

        if (file == null) {
            throw new GeneralRequestException("Error input data");
        }

        if (filename == null) {
            throw new GeneralRequestException("Error input data");
        }

        String mimeType = file.getContentType();
        String extension = getExtension(filename);

        if (!properties.getAllowedMimeTypes().contains(mimeType)) {
            throw new GeneralRequestException("Error input data");
        }

        String storedName = UUID.randomUUID() + extension;
        File targetFile = new File(properties.getSTORAGE_DIRECTORY() + File.separator + storedName);
        if (!Objects.equals(targetFile.getParent(), properties.getSTORAGE_DIRECTORY())) {
            throw new GeneralRequestException("Error input data");
        }

        try {
            file.transferTo(targetFile);
        } catch (IOException e) {
            throw new GeneralRequestException("Error input data");
        }

        int userId = user.getId();
        FileMeta meta = new FileMeta(generateID(), userId, filename, mimeType, getExtension(targetFile.getName()), targetFile.getName(), getSize(targetFile), LocalDateTime.now(), getPath(targetFile));
        System.out.println(meta);
        return fileRepository.save(meta);
    }


    public String getExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        return index == -1 ? null : fileName.substring(index);
    }

    public int getSize(File file) {
        return Math.toIntExact(file.length());
    }

    public String getPath(File file) {
        return file.getPath();
    }

    public String getTokenFromHeader(String authHeader) {
        return authHeader.substring(7);
    }

    public File getDownloadFile(String filename, String header){

        MyUserDetails user = (MyUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user == null) {
            throw new UnauthorizedException("Unauthorized Error");
        }

        if (!jwtService.validateToken(getTokenFromHeader(header), user)) {
            logger.info("Ошибка валидации токена");
            throw new UnauthorizedException("Unauthorized error");
        }

        logger.info("Валидация успешна");

        int currentUserId = user.getId();

        String downloadFileName = fileRepository.findByOriginalNameAndUserId(filename, currentUserId).getFileName();
        File fileToDownload = new File(properties.getSTORAGE_DIRECTORY() + File.separator + downloadFileName);

        if (!checker.hasValidDirectory(fileToDownload)) {
            throw new GeneralServerError("Error downloading file");
        }
        if (!checker.isFileAccessible(fileToDownload)) {
            throw new GeneralServerError("Error downloading file");
        }
        logger.info("Успешное скачивание файла");
        return fileToDownload;
    }

    public String generateID() {
        return UUID.randomUUID().toString();
    }

    public void deleteFile(String filename, String header) {

        MyUserDetails user = (MyUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user == null) {
            throw new UnauthorizedException("Unauthorized Error");
        }

        if (!jwtService.validateToken(getTokenFromHeader(header), user)) {
            logger.info("Validation failed");
            throw new UnauthorizedException("Unauthorized error");
        }

        logger.info("Валидация успешна");

        int currentUserId = user.getId();

        FileMeta metaToDelete = fileRepository.findByOriginalNameAndUserId(filename, currentUserId);
        Path path = Paths.get(metaToDelete.getPath());
        try {
            if (Files.deleteIfExists(path)) {
                fileRepository.delete(metaToDelete);
                logger.info("Файл успешно удален");
            }
        } catch (IOException e) {
            throw new GeneralServerError("Error deleting file");
        }
    }

    public FileMeta renameFile(String oldFilename, String newFilename, String header) {
        MyUserDetails user = (MyUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (oldFilename == null || newFilename == null) {
            throw new GeneralRequestException("Error input data");
        }

        if (user == null) {
            throw new UnauthorizedException("Unauthorized Error");
        }

        if (!jwtService.validateToken(getTokenFromHeader(header), user)) {
            logger.info("Validation failed");
            throw new UnauthorizedException("Unauthorized error");
        }

        logger.info("Валидация успешна");

        return fileRepository.updateName(oldFilename, newFilename);
    }

    public List<FileMeta> getFileList(String header) {

        if (header == null) {
            throw new GeneralRequestException("Error input data");
        }

        MyUserDetails user = (MyUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user == null) {
            throw new UnauthorizedException("Unauthorized Error");
        }

        if (!jwtService.validateToken(getTokenFromHeader(header), user)) {
            logger.info("Validation failed");
            throw new UnauthorizedException("Unauthorized error");
        }
        logger.info("Валидация успешна");

        int userId = user.getId();
        List<FileMeta> metaList = fileRepository.findAllByUserId(userId);

        if (metaList == null) {
            throw new GeneralServerError("Error getting file list");
        }
        System.out.println(metaList);
        return metaList;
    }
}
