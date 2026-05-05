package ru.netology.FinalCloud.FileCloud.service;

import org.springframework.stereotype.Component;
import ru.netology.FinalCloud.FileCloud.controller.errors.GeneralServerError;

import java.io.File;
import java.util.Objects;

@Component
public class FileChecker {

    public FileServiceProperties properties;

    public FileChecker(FileServiceProperties properties) {
        this.properties = properties;
    }

    public boolean isFileAccessible(File file) {
        return file.exists() && file.isFile();
    }

    public boolean hasValidDirectory(File file){
        return Objects.equals(file.getParent(), properties.getSTORAGE_DIRECTORY());
    }
}
