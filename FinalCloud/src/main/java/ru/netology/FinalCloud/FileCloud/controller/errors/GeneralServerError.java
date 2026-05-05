package ru.netology.FinalCloud.FileCloud.controller.errors;

public class GeneralServerError extends RuntimeException {
    public GeneralServerError(String message) {
        super(message);
    }
}
