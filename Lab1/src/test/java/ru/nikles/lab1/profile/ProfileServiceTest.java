package ru.nikles.lab1.profile;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.nikles.lab1.user.NotificationSettings;
import ru.nikles.lab1.user.User;
import ru.nikles.lab1.user.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private RiakProfileCacheRepository cacheRepository;

    @Mock
    private UserService userService;

    private ProfileService profileService;
    private User user;

    @BeforeEach
    void setUp() {
        profileService = new ProfileService(cacheRepository, userService, 60);
        user = new User(
                "user-1",
                "Иван",
                "ivan@example.com",
                NotificationSettings.disabled()
        );
    }

    @Test
    void returnsProfileFromActiveCache() {
        ProfileCacheEntry entry = new ProfileCacheEntry(
                user.id(), user, Instant.now(), Instant.now().plusSeconds(60));
        when(cacheRepository.findByUserId(user.id())).thenReturn(Optional.of(entry));

        ProfileResult result = profileService.getProfile(user.id());

        assertThat(result.profile()).isEqualTo(user);
        assertThat(result.cacheStatus()).isEqualTo(CacheStatus.HIT);
        verifyNoInteractions(userService);
    }

    @Test
    void loadsAndCachesProfileOnMiss() {
        when(cacheRepository.findByUserId(user.id())).thenReturn(Optional.empty());
        when(userService.findById(user.id())).thenReturn(user);

        ProfileResult result = profileService.getProfile(user.id());

        assertThat(result.cacheStatus()).isEqualTo(CacheStatus.MISS);
        verify(cacheRepository).save(any(ProfileCacheEntry.class));
    }

    @Test
    void reloadsExpiredCache() {
        ProfileCacheEntry expiredEntry = new ProfileCacheEntry(
                user.id(), user, Instant.now().minusSeconds(120), Instant.now().minusSeconds(60));
        when(cacheRepository.findByUserId(user.id())).thenReturn(Optional.of(expiredEntry));
        when(userService.findById(user.id())).thenReturn(user);

        ProfileResult result = profileService.getProfile(user.id());

        assertThat(result.cacheStatus()).isEqualTo(CacheStatus.MISS);
        verify(cacheRepository).delete(user.id());
        verify(cacheRepository).save(any(ProfileCacheEntry.class));
    }

    @Test
    void refreshesProfileCache() {
        when(userService.findById(user.id())).thenReturn(user);

        ProfileResult result = profileService.refreshProfile(user.id());

        assertThat(result.cacheStatus()).isEqualTo(CacheStatus.REFRESH);
        verify(cacheRepository).save(any(ProfileCacheEntry.class));
    }
}
