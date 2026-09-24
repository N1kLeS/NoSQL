package ru.nikles.lab1.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record User(
        @NotBlank(message = "Идентификатор пользователя обязателен")
        String id,

        @NotBlank(message = "Имя пользователя обязательно")
        String name,

        @NotBlank(message = "Email пользователя обязателен")
        @Email(message = "Email пользователя имеет неверный формат")
        String email,

        NotificationSettings notificationSettings
) {

    public User {
        if (notificationSettings == null) {
            notificationSettings = NotificationSettings.disabled();
        }
    }
}
