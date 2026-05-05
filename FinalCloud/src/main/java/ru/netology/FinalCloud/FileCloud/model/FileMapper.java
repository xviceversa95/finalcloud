package ru.netology.FinalCloud.FileCloud.model;

import org.springframework.stereotype.Component;
import ru.netology.FinalCloud.FileCloud.service.FileMeta;

@Component
public class FileMapper {
    public FileDto toDto(FileMeta meta) {
        return new FileDto(meta);
    }
}
