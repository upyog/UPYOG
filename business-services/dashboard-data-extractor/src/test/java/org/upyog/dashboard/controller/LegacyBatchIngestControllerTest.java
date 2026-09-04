package org.upyog.dashboard.controller;

import org.upyog.dashboard.constants.DashboardExtractorConstants;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.upyog.dashboard.model.IngestionResult;
import org.upyog.dashboard.model.LegacyIngestionResponse;
import org.upyog.dashboard.service.LegacyBatchIngestionOrchestrator;
import org.upyog.dashboard.service.LegacyBatchIngestRequest;

/**
 * Integration and unit tests for the {@link org.upyog.dashboard.controller.LegacyIngestionController}.
 * <p>
 * Verifies that legacy batch endpoints correctly parse the request payloads and 
 * delegate to the {@link org.upyog.dashboard.service.LegacyBatchIngestionOrchestrator}
 * appropriately, while returning correct HTTP response codes.
 */
@ExtendWith(MockitoExtension.class)
class LegacyBatchIngestControllerTest {

    @Mock
    private LegacyBatchIngestionOrchestrator orchestrator;

    @InjectMocks
    private LegacyIngestionController controller;

    @Test
    @DisplayName("batchIngest endpoint invokes orchestrator and returns OK response")
    void batchIngest_invokesOrchestratorAndReturnsOk() {
        LegacyBatchIngestRequest request = LegacyBatchIngestRequest.builder()
                .moduleName("PT")
                .startDate("2025-01-01")
                .endDate("2025-01-31")
                .async(false)
                .build();

        LegacyIngestionResponse expectedResponse = LegacyIngestionResponse.builder()
                .totalDatesRequested(31)
                .datesProcessedSuccessfully(1)
                .processedResults(java.util.List.of(IngestionResult.builder().ingestionStatus(DashboardExtractorConstants.STATUS_SUCCESS).build()))
                .build();

        when(orchestrator.processLegacyBatchIngest(any(LegacyBatchIngestRequest.class))).thenReturn(expectedResponse);

        ResponseEntity<LegacyIngestionResponse> responseEntity = controller.batchIngest(request);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getTotalDatesRequested()).isEqualTo(31);

        verify(orchestrator).processLegacyBatchIngest(request);
    }
}
