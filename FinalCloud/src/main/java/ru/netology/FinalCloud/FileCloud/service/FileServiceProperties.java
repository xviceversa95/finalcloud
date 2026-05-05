package ru.netology.FinalCloud.FileCloud.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class FileServiceProperties {
    @Value("${file.storage.directory}")
    private String STORAGE_DIRECTORY;

    @Value("#{'${file.allowed.mime.types}'.split(',')}")
    private Set<String> allowedMimeTypes;


    public Set<String> getAllowedMimeTypes() {
        return allowedMimeTypes;
    }

    public String getSTORAGE_DIRECTORY() {
        return STORAGE_DIRECTORY;
    }

    public void setAllowedMimeTypes(Set<String> allowedMimeTypes) {
        this.allowedMimeTypes = allowedMimeTypes;
    }

    public void setSTORAGE_DIRECTORY(String STORAGE_DIRECTORY) {
        this.STORAGE_DIRECTORY = STORAGE_DIRECTORY;
    }
}
//TODO: add mime-types и настроить таскание из yaml на нужный префикс и конфиг @ConfigurationProperties