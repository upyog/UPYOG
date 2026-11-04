package org.upyog.dashboard.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link DashboardPayload}.
 */
class DashboardPayloadTest {

    @Test
    @DisplayName("Builder creates DashboardPayload with data list")
    void builder_createsPayload() {
        DashboardData data = DashboardData.builder()
                .module("PT")
                .ulb("pb.amritsar")
                .metrics(new HashMap<>())
                .build();

        DashboardPayload payload = DashboardPayload.builder()
                .data(Collections.singletonList(data))
                .build();

        assertThat(payload.getData()).hasSize(1);
        assertThat(payload.getData().get(0).getModule()).isEqualTo("PT");
    }

    @Test
    @DisplayName("Jackson serializes Data field as uppercase 'Data'")
    void jackson_serializesDataAsUppercase() throws Exception {
        DashboardData data = DashboardData.builder()
                .module("PT")
                .ulb("pb.amritsar")
                .metrics(Map.of("assessments", 100))
                .build();

        DashboardPayload payload = DashboardPayload.builder()
                .data(Collections.singletonList(data))
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(payload);

        assertThat(json).contains("\"Data\"");
        assertThat(json).contains("\"module\"");
        assertThat(json).contains("\"assessments\"");
    }

    @Test
    @DisplayName("Jackson deserializes from uppercase Data key")
    void jackson_deserializesFromUppercaseDataKey() throws Exception {
        String json = "{\"Data\": [{\"module\": \"PT\", \"ulb\": \"pb.amritsar\", \"metrics\": {\"assessments\": 50}}]}";

        ObjectMapper mapper = new ObjectMapper();
        DashboardPayload payload = mapper.readValue(json, DashboardPayload.class);

        assertThat(payload.getData()).hasSize(1);
        assertThat(payload.getData().get(0).getModule()).isEqualTo("PT");
        assertThat(payload.getData().get(0).getMetrics()).containsEntry("assessments", 50);
    }

    @Test
    @DisplayName("No-args constructor creates empty payload")
    void noArgsConstructor_createsEmptyPayload() {
        DashboardPayload payload = new DashboardPayload();
        assertThat(payload.getData()).isNull();
    }
}