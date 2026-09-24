package ru.nikles.lab1.session;

public class SessionNotFoundException extends RuntimeException {

    public SessionNotFoundException(String sessionId) {
        super("Сессия с идентификатором " + sessionId + " не найдена");
    }
}
