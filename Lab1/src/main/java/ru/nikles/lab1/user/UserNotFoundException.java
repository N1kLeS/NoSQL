package ru.nikles.lab1.user;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String userId) {
        super("Пользователь с идентификатором " + userId + " не найден");
    }
}
