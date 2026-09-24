package ru.nikles.lab1.session;

public class SessionExpiredException extends RuntimeException {

    public SessionExpiredException(String sessionId) {
        super("Срок действия сессии " + sessionId + " истёк");
    }
}
