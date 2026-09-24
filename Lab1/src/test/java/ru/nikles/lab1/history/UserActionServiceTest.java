package ru.nikles.lab1.history;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.nikles.lab1.user.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserActionServiceTest {

    @Mock
    private RiakUserActionRepository actionRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserActionService actionService;

    @Test
    void returnsUserHistoryInChronologicalOrder() {
        UserAction later = new UserAction(
                "action-2", "user-1", "ORDER_CREATED", Instant.parse("2026-09-24T10:00:00Z"));
        UserAction earlier = new UserAction(
                "action-1", "user-1", "LOGIN", Instant.parse("2026-09-24T09:00:00Z"));
        when(actionRepository.findByUserId("user-1")).thenReturn(List.of(later, earlier));

        List<UserAction> history = actionService.findHistory("user-1");

        assertThat(history).containsExactly(earlier, later);
        verify(userService).findById("user-1");
    }
}
