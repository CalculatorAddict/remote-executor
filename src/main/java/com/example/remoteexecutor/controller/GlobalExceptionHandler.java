package com.example.remoteexecutor.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handle(IllegalArgumentException e) {
        return switch (e.getMessage()) {
            case "COMMAND_INVALID", "CPU_REQUEST_INVALID", "CPU_REQUEST_EXCEEDS_CAPACITY" ->
                    new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            case "EXECUTION_NOT_FOUND" -> new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            default -> new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }
}