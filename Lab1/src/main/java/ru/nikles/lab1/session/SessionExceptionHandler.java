package ru.nikles.lab1.session;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SessionExceptionHandler {

    @ExceptionHandler(SessionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(SessionNotFoundException exception) {
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler(SessionExpiredException.class)
    @ResponseStatus(HttpStatus.GONE)
    public Map<String, String> handleExpired(SessionExpiredException exception) {
        return Map.of("error", exception.getMessage());
    }
}
