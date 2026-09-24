package ru.nikles.lab1.history;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ru.nikles.lab1.counter.PageVisitCounterService;

@RestController
@RequestMapping("/api/users/{userId}/actions")
public class UserActionController {

    private final UserActionService actionService;
    private final PageVisitCounterService counterService;

    public UserActionController(UserActionService actionService,
                                PageVisitCounterService counterService) {
        this.actionService = actionService;
        this.counterService = counterService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserAction add(@PathVariable String userId,
                          @Valid @RequestBody CreateUserActionRequest request) {
        return actionService.add(userId, request);
    }

    @GetMapping
    public List<UserAction> findHistory(@PathVariable String userId) {
        List<UserAction> history = actionService.findHistory(userId);
        counterService.incrementUserHistoryPage();
        return history;
    }
}
