package ru.nikles.lab1.user;

public class RiakUnavailableException extends RuntimeException {

    public RiakUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
