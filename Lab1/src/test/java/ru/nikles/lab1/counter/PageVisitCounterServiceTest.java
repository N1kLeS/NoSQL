package ru.nikles.lab1.counter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PageVisitCounterServiceTest {

    @Mock
    private RiakPageVisitCounterRepository counterRepository;

    @InjectMocks
    private PageVisitCounterService counterService;

    @Test
    void incrementsUserHistoryPageCounter() {
        counterService.incrementUserHistoryPage();

        verify(counterRepository).increment(PageVisitCounterService.USER_HISTORY_PAGE);
    }

    @Test
    void returnsUserHistoryPageCounter() {
        when(counterRepository.getValue(PageVisitCounterService.USER_HISTORY_PAGE)).thenReturn(42L);

        long visits = counterService.getUserHistoryPageVisits();

        assertThat(visits).isEqualTo(42);
    }
}
