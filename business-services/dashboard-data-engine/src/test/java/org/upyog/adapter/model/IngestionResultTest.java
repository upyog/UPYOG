package org.upyog.adapter.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link IngestionResult}.
 */
class IngestionResultTest {

    @Test
    @DisplayName("Builder creates success result")
    void builder_createsSuccessResult() {
        IngestionResult result = IngestionResult.builder()
                .ingestionStatus("SUCCESS")
                .responseData("{\"status\": \"ok\"}")
                .ingestedAt(1700000000000L)
                .build();

        assertThat(result.getIngestionStatus()).isEqualTo("SUCCESS");
        assertThat(result.getResponseData()).isEqualTo("{\"status\": \"ok\"}");
        assertThat(result.getFailureReason()).isNull();
        assertThat(result.getIngestedAt()).isEqualTo(1700000000000L);
    }

    @Test
    @DisplayName("Builder creates failure result")
    void builder_createsFailureResult() {
        IngestionResult result = IngestionResult.builder()
                .ingestionStatus("FAILURE")
                .failureReason("Connection timeout")
                .ingestedAt(1700000000000L)
                .build();

        assertThat(result.getIngestionStatus()).isEqualTo("FAILURE");
        assertThat(result.getResponseData()).isNull();
        assertThat(result.getFailureReason()).isEqualTo("Connection timeout");
    }

    @Test
    @DisplayName("Lombok toString contains status")
    void lombok_toStringContainsStatus() {
        IngestionResult result = IngestionResult.builder()
                .ingestionStatus("SUCCESS")
                .ingestedAt(1700000000000L)
                .build();

        assertThat(result.toString()).contains("SUCCESS");
    }
}