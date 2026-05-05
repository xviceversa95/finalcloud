package ru.netology.FinalCloud.FileCloud.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ModifyRequest {

    @JsonProperty("filename")
    private String newFilename;

    public ModifyRequest(String newFilename) {
        this.newFilename = newFilename;
    }

    public ModifyRequest() {
    };

    public void setNewFilename(String newFilename) {
        this.newFilename = newFilename;
    }

    public String getNewFilename() {
        return newFilename;
    }
}
