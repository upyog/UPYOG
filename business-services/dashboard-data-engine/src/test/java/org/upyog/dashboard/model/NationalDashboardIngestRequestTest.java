package org.upyog.dashboard.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link NationalDashboardIngestRequest}.
 */
class NationalDashboardIngestRequestTest {

    @Test
    @DisplayName("Builder creates request with RequestInfo and Data")
    void builder_createsFullRequest() {
        RequestInfo requestInfo = RequestInfo.builder()
                .apiId("Rainmaker")
                .authToken("token-123")
                .build();

        DashboardData data = DashboardData.builder()
                .module("PT")
                .ulb("pb.amritsar")
                .metrics(new HashMap<>())
                .build();

        NationalDashboardIngestRequest request = NationalDashboardIngestRequest.builder()
                .requestInfo(requestInfo)
                .data(Collections.singletonList(data))
                .build();

        assertThat(request.getRequestInfo()).isNotNull();
        assertThat(request.getRequestInfo().getApiId()).isEqualTo("Rainmaker");
        assertThat(request.getData()).hasSize(1);
    }

    @Test
    @DisplayName("Jackson serialization uses uppercase keys")
    void jackson_serializesWithUppercaseKeys() throws Exception {
        RequestInfo requestInfo = RequestInfo.builder()
                .apiId("Rainmaker")
                .authToken("token-123")
                .build();

        DashboardData data = DashboardData.builder()
                .module("PT")
                .ulb("pb.amritsar")
                .metrics(new HashMap<>())
                .build();

        NationalDashboardIngestRequest request = NationalDashboardIngestRequest.builder()
                .requestInfo(requestInfo)
                .data(Collections.singletonList(data))
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(request);

        assertThat(json).contains("\"RequestInfo\"");
        assertThat(json).contains("\"Data\"");
        assertThat(json).contains("Rainmaker");
    }

}