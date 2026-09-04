package org.upyog.dashboard.loader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.upyog.dashboard.model.DashboardData;
import org.upyog.dashboard.model.DashboardPayload;
import org.upyog.dashboard.model.IngestionResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the {@link DashboardDataLoader} interface default implementation.
 */
class DashboardDataLoaderTest {

    @Test
    @DisplayName("Default load returns SUCCESS with empty response")
    void defaultLoad_returnsSuccess() {
        DashboardDataLoader loader = new DashboardDataLoader() {
            // Uses the default implementation
        };

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("assessments", 100);
        DashboardData data = DashboardData.builder()
                .module("PT")
                .ulb("pb.amritsar")
                .metrics(metrics)
                .build();

        DashboardPayload payload = DashboardPayload.builder()
                .data(Collections.singletonList(data))
                .build();

        IngestionResult result = loader.load(payload);

        assertThat(result.getIngestionStatus()).isEqualTo("SUCCESS");
        assertThat(result.getResponseData()).isEqualTo("");
        assertThat(result.getFailureReason()).isNull();
        assertThat(result.getIngestedAt()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Default load returns current timestamp")
    void defaultLoad_returnsCurrentTimestamp() {
        DashboardDataLoader loader = new DashboardDataLoader() {
            // Uses the default implementation
        };

        DashboardPayload payload = DashboardPayload.builder()
                .data(Collections.emptyList())
                .build();

        IngestionResult result = loader.load(payload);

        assertThat(result.getIngestedAt()).isGreaterThan(0);
    }
}