package ru.nikles.lab1.history;

import jakarta.validation.constraints.NotBlank;

public record CreateUserActionRequest(
        @NotBlank(message = "Тип действия обязателен")
        String type
) {
}
