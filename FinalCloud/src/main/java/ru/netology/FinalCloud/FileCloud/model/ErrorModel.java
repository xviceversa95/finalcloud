package ru.netology.FinalCloud.FileCloud.model;


public class ErrorModel {
    String message;
    int id;

    public ErrorModel(String message, int id) {
        this.message = message;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
