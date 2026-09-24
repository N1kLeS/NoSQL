package ru.nikles.lab1.session;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.nikles.lab1.user.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private RiakSessionRepository sessionRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private SessionService sessionService;

    @Test
    void returnsActiveSession() {
        UserSession activeSession = new UserSession(
                "active-session",
                "user-1",
                Instant.now().minusSeconds(10),
                Instant.now().plusSeconds(60)
        );
        when(sessionRepository.findById("active-session")).thenReturn(Optional.of(activeSession));

        UserSession result = sessionService.findById("active-session");

        assertThat(result).isEqualTo(activeSession);
    }

    @Test
    void deletesAndRejectsExpiredSession() {
        UserSession expiredSession = new UserSession(
                "expired-session",
                "user-1",
                Instant.now().minusSeconds(120),
                Instant.now().minusSeconds(60)
        );
        when(sessionRepository.findById("expired-session")).thenReturn(Optional.of(expiredSession));

        assertThatThrownBy(() -> sessionService.findById("expired-session"))
                .isInstanceOf(SessionExpiredException.class);
        verify(sessionRepository).delete("expired-session");
    }
}
