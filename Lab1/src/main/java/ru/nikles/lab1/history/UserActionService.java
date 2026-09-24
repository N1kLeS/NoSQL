package ru.nikles.lab1.history;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import ru.nikles.lab1.user.UserService;

@Service
public class UserActionService {

    private final RiakUserActionRepository actionRepository;
    private final UserService userService;

    public UserActionService(RiakUserActionRepository actionRepository,
                             UserService userService) {
        this.actionRepository = actionRepository;
        this.userService = userService;
    }

    public UserAction add(String userId, CreateUserActionRequest request) {
        userService.findById(userId);

        UserAction action = new UserAction(
                UUID.randomUUID().toString(),
                userId,
                request.type(),
                Instant.now()
        );
        return actionRepository.save(action);
    }

    public List<UserAction> findHistory(String userId) {
        userService.findById(userId);
        return actionRepository.findByUserId(userId).stream()
                .sorted(Comparator.comparing(UserAction::occurredAt))
                .toList();
    }
}
