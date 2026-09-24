package ru.nikles.lab1.profile;

import java.time.Instant;

import ru.nikles.lab1.user.User;

public record ProfileCacheEntry(
        String userId,
        User profile,
        Instant cachedAt,
        Instant expiresAt
) {
}
