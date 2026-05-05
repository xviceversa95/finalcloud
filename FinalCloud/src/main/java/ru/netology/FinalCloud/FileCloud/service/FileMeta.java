package ru.netology.FinalCloud.FileCloud.service;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("file_metadata")
public class FileMeta {
    @Id
    private String id;
    private int userId;
    private String originalName;
    private String mimeType;
    private String extention;
    private String fileName;
    private int size;
    private LocalDateTime uploadDate;
    private String path;

    public FileMeta() {
    };

    public FileMeta(String id, int userId, String originalName, String mimeType, String extention, String fileName, int size, LocalDateTime uploadDate, String path) {
        this.id = id;
        this.userId = userId;
        this.originalName = originalName;
        this.mimeType = mimeType;
        this.extention = extention;
        this.fileName = fileName;
        this.size = size;
        this.uploadDate = uploadDate;
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public String getExtention() {
        return extention;
    }

    public int getSize() {
        return size;
    }

    public String getId() {
        return id;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getPath() {
        return path;
    }

    public String getOriginalName() {
        return originalName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setExtention(String extention) {
        this.extention = extention;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    @Override
    public String toString() {
        return "FileMeta{" +
                "id='" + id + '\'' +
                ", userId=" + userId +
                ", originalName='" + originalName + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", extention='" + extention + '\'' +
                ", fileName='" + fileName + '\'' +
                ", size=" + size +
                ", uploadDate=" + uploadDate +
                ", path='" + path + '\'' +
                '}';
    }
}
