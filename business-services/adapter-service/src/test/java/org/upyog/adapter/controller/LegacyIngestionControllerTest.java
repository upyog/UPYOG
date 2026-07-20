package org.upyog.adapter.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.upyog.adapter.common.constants.Module;
import org.upyog.adapter.model.LegacyIngestionResponse;
import org.upyog.adapter.service.LegacyIngestionService;

@ExtendWith(MockitoExtension.class)
class LegacyIngestionControllerTest {

    @Mock
    private LegacyIngestionService legacyIngestionService;

    @InjectMocks
    private LegacyIngestionController controller;

    @Test
    @DisplayName("ingestRange invokes legacyIngestionService with range")
    void ingestRange_callsService() {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 1, 31);
        LegacyIngestionResponse expectedResponse = LegacyIngestionResponse.builder()
                .totalDatesRequested(31)
                .datesProcessedSuccessfully(31)
                .build();

        when(legacyIngestionService.ingestHistoricalDataForRange(start, end, Module.PT))
                .thenReturn(expectedResponse);

        ResponseEntity<LegacyIngestionResponse> responseEntity = controller.ingestRange(start, end, "PT");

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(expectedResponse);
        verify(legacyIngestionService).ingestHistoricalDataForRange(start, end, Module.PT);
    }

    @Test
    @DisplayName("ingestLastMonths invokes legacyIngestionService for N months")
    void ingestLastMonths_callsService() {
        LegacyIngestionResponse expectedResponse = LegacyIngestionResponse.builder()
                .totalDatesRequested(150)
                .datesSkipped(150)
                .build();

        when(legacyIngestionService.ingestHistoricalDataForLastMonths(5, null))
                .thenReturn(expectedResponse);

        ResponseEntity<LegacyIngestionResponse> responseEntity = controller.ingestLastMonths(5, null);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(expectedResponse);
        verify(legacyIngestionService).ingestHistoricalDataForLastMonths(5, null);
    }
}
