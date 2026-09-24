package ru.nikles.lab1.session;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateSessionRequest(
        @NotBlank(message = "Идентификатор пользователя обязателен")
        String userId,

        @Min(value = 1, message = "Время жизни сессии должно быть не меньше одной секунды")
        @Max(value = 86400, message = "Время жизни сессии не должно превышать 24 часа")
        long ttlSeconds
) {
}
