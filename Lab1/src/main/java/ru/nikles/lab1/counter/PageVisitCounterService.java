package ru.nikles.lab1.counter;

import org.springframework.stereotype.Service;

@Service
public class PageVisitCounterService {

    public static final String USER_HISTORY_PAGE = "user-history";

    private final RiakPageVisitCounterRepository counterRepository;

    public PageVisitCounterService(RiakPageVisitCounterRepository counterRepository) {
        this.counterRepository = counterRepository;
    }

    public void incrementUserHistoryPage() {
        counterRepository.increment(USER_HISTORY_PAGE);
    }

    public long getUserHistoryPageVisits() {
        return counterRepository.getValue(USER_HISTORY_PAGE);
    }
}
