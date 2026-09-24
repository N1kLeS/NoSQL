package ru.nikles.lab1.session;

import java.time.Instant;

public record UserSession(
        String sessionId,
        String userId,
        Instant createdAt,
        Instant expiresAt
) {
}
