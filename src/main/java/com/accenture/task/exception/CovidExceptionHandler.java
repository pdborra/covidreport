package com.accenture.task.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CovidExceptionHandler {

    @ExceptionHandler(value = CovidUserDefinedException.class)
    public ResponseEntity<Object> handleValueNotFound(CovidUserDefinedException exception) {

        return new ResponseEntity<>(exception.getErrorMessage(), HttpStatus.NOT_FOUND);
    }
}
