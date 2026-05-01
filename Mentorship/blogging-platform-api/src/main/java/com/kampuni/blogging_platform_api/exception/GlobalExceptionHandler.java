package com.kampuni.blogging_platform_api.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)

    public ErrorResponse handleNotFound(ResourceNotFoundException ex){
        return new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)

    public ErrorResponse handleValidation(MethodArgumentNotValidException ex){
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + " " + err.getDefaultMessage())
                .toList();

        return new ErrorResponse("Validation failed",
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
                );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)

    public ErrorResponse handleGeneric(Exception ex){
        return new ErrorResponse("Internal server Error", HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
    }
}
