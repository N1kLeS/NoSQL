package ru.nikles.lab1.profile;

import ru.nikles.lab1.user.User;

public record ProfileResult(
        User profile,
        CacheStatus cacheStatus
) {
}
