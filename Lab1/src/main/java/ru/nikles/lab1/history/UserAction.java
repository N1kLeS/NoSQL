package ru.nikles.lab1.history;

import java.time.Instant;

public record UserAction(
        String id,
        String userId,
        String type,
        Instant occurredAt
) {
}
