package ru.netology.FinalCloud.FileCloud.controller.errors;

public class GeneralRequestException extends RuntimeException {
    public GeneralRequestException(String message) {
        super(message);
    }
}
