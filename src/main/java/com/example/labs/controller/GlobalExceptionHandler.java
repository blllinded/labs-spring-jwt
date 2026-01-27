package com.example.labs.controller;

import com.example.labs.exception.ConflictException;
import com.example.labs.exception.NotFoundException;
import com.example.labs.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // DTO validation -> 400 //
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Map<String, Object> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> fields.put(err.getField(), err.getDefaultMessage()));

        return Map.of(
                "error", "validation_failed",
                "fields", fields
        );
    }

    // 404 //
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    public Map<String, Object> handleNotFound(NotFoundException ex) {
        return Map.of("error", "not_found", "message", ex.getMessage());
    }

    // 409 //
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ConflictException.class)
    @ResponseBody
    public Map<String, Object> handleConflict(ConflictException ex) {
        return Map.of("error", "conflict", "message", ex.getMessage());
    }

    // 401 //
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseBody
    public Map<String, Object> handleUnauthorized(UnauthorizedException ex) {
        return Map.of("error", "unauthorized", "message", ex.getMessage());
    }
}
