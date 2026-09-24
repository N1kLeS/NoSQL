package ru.nikles.lab1.counter;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/page-visits")
public class PageVisitCounterController {

    private final PageVisitCounterService counterService;

    public PageVisitCounterController(PageVisitCounterService counterService) {
        this.counterService = counterService;
    }

    @GetMapping("/user-history")
    public Map<String, Object> getUserHistoryPageVisits() {
        return Map.of(
                "page", PageVisitCounterService.USER_HISTORY_PAGE,
                "visits", counterService.getUserHistoryPageVisits()
        );
    }
}
