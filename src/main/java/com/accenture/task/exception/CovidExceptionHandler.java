package com.accenture.task.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CovidExceptionHandler {

    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity<Object> handleValueNotFound(CustomException exception) {

        return new ResponseEntity<>(exception.getErrorMessage(), HttpStatus.NOT_FOUND);
    }
}
