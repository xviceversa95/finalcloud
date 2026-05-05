package ru.netology.FinalCloud.FileCloud.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.netology.FinalCloud.FileCloud.controller.errors.GeneralRequestException;
import ru.netology.FinalCloud.FileCloud.controller.errors.GeneralServerError;
import ru.netology.FinalCloud.FileCloud.controller.errors.UnauthorizedException;
import ru.netology.FinalCloud.FileCloud.model.ErrorModel;

@ControllerAdvice
public class GlobalFileExceptionHandler {

    @ExceptionHandler(GeneralRequestException.class)
    public ResponseEntity<ErrorModel> handleBadRequest(GeneralRequestException exception) {
        ErrorModel error = new ErrorModel(exception.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GeneralServerError.class)
    public ResponseEntity<ErrorModel> handleRequestException(GeneralServerError exception) {
        ErrorModel error = new ErrorModel(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorModel> handleUnauthorizedError(UnauthorizedException exception) {
        ErrorModel error = new ErrorModel(exception.getMessage(), HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }
}
