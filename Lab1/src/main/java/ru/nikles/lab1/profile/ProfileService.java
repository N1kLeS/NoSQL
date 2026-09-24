package ru.nikles.lab1.profile;

import java.time.Instant;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ru.nikles.lab1.user.User;
import ru.nikles.lab1.user.UserService;

@Service
public class ProfileService {

    private static final Logger log = LoggerFactory.getLogger(ProfileService.class);

    private final RiakProfileCacheRepository cacheRepository;
    private final UserService userService;
    private final long cacheTtlSeconds;

    public ProfileService(RiakProfileCacheRepository cacheRepository,
                          UserService userService,
                          @Value("${profile.cache-ttl-seconds}") long cacheTtlSeconds) {
        this.cacheRepository = cacheRepository;
        this.userService = userService;
        this.cacheTtlSeconds = cacheTtlSeconds;
    }

    public ProfileResult getProfile(String userId) {
        Optional<ProfileCacheEntry> cachedEntry = cacheRepository.findByUserId(userId);
        if (cachedEntry.isPresent()) {
            ProfileCacheEntry entry = cachedEntry.get();
            if (Instant.now().isBefore(entry.expiresAt())) {
                log.info("CACHE HIT: userId={}", userId);
                return new ProfileResult(entry.profile(), CacheStatus.HIT);
            }

            log.info("CACHE EXPIRED: userId={}", userId);
            cacheRepository.delete(userId);
        }

        log.info("CACHE MISS: userId={}", userId);
        User profile = userService.findById(userId);
        saveToCache(profile);
        return new ProfileResult(profile, CacheStatus.MISS);
    }

    public ProfileResult refreshProfile(String userId) {
        User profile = userService.findById(userId);
        saveToCache(profile);
        log.info("CACHE REFRESH: userId={}", userId);
        return new ProfileResult(profile, CacheStatus.REFRESH);
    }

    private void saveToCache(User profile) {
        Instant cachedAt = Instant.now();
        cacheRepository.save(new ProfileCacheEntry(
                profile.id(),
                profile,
                cachedAt,
                cachedAt.plusSeconds(cacheTtlSeconds)
        ));
    }
}
