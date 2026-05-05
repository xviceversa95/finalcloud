package ru.netology.FinalCloud.FileCloud.controller.errors;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
