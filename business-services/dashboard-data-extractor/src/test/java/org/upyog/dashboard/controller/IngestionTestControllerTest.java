package org.upyog.dashboard.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.upyog.dashboard.model.IngestionResult;
import org.upyog.dashboard.model.IngestionSchedulerDetail;
import org.upyog.dashboard.repository.IngestionSummaryRepository;
import org.upyog.dashboard.service.DailyIngestionService;

@ExtendWith(MockitoExtension.class)
class IngestionTestControllerTest {

    @Mock
    private DailyIngestionService service;

    private IngestionTestController controller;

    @BeforeEach
    void setUp() {
        controller = new IngestionTestController(service);
    }

    @Test
    @DisplayName("pushData without date delegates to service.ingestDailyData()")
    void testPushData_WithoutDate_Success() {
        IngestionResult result = IngestionResult.builder()
                .ingestionStatus("SUCCESS")
                .moduleName("PT")
                .date("08-09-2026")
                .build();
        when(service.ingestDailyData()).thenReturn(List.of(result));

        ResponseEntity<List<IngestionResult>> response = controller.pushData(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(service).ingestDailyData();
    }

    @Test
    @DisplayName("pushData with target date delegates to service.ingestDailyData(date)")
    void testPushData_WithTargetDate_Success() {
        LocalDate date = LocalDate.of(2026, 9, 7);
        IngestionResult result = IngestionResult.builder()
                .ingestionStatus("SUCCESS_ZERO_METRICS")
                .moduleName("PT")
                .date("07-09-2026")
                .build();
        when(service.ingestDailyData(eq(date))).thenReturn(List.of(result));

        ResponseEntity<List<IngestionResult>> response = controller.pushData(date);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(service).ingestDailyData(date);
    }
}
