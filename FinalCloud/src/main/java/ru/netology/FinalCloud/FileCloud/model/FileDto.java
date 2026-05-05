package ru.netology.FinalCloud.FileCloud.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.netology.FinalCloud.FileCloud.service.FileMeta;

public class FileDto {

    @JsonProperty("filename")
    public String originalName;

    @JsonProperty("size")
    public int size;

    public FileDto(FileMeta meta) {
        this.originalName = meta.getOriginalName();
        this.size = meta.getSize();
    }

    public String getOriginalName() {
        return originalName;
    }

    public int getSize() {
        return size;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "File{" +
                "originalName='" + originalName + '\'' +
                ", size=" + size +
                '}';
    }
}
