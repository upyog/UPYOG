package org.upyog.dashboard.scheduler;

import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.upyog.dashboard.service.DailyIngestionService;

@ExtendWith(MockitoExtension.class)
class DailyIngestionSchedulerTest {

    @Mock
    private DailyIngestionService ingestionService;

    @InjectMocks
    private DailyIngestionScheduler scheduler;

    @Test
    @DisplayName("executeDailyPTIngestion triggers scheduled ingestion service with cron")
    void executeDailyPTIngestion_delegatesToService() {
        scheduler.executeDailyPTIngestion();
        verify(ingestionService).executeScheduledIngestion(nullable(String.class));
    }
}
