package org.upyog.dashboard.entity;

import org.upyog.dashboard.util.CommonUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link DailyIngestionData}.
 */
class DailyIngestionDataTest {

    @Test
    @DisplayName("Builder creates DailyIngestionData with all fields")
    void builder_createsFullEntity() {
        long now = CommonUtils.getCurrentEpochMillis();

        DailyIngestionData data = DailyIngestionData.builder()
                .moduleIngestionId("uuid-123")
                .moduleDetailId("detail-456")
                .tenantId("pb.amritsar")
                .moduleName("PT")
                .pushDate("2024-01-15")
                .requestData("{\"key\": \"value\"}")
                .responseData("{\"status\": \"ok\"}")
                .ingestionStatus("SUCCESS")
                .createdBy("SYSTEM")
                .createdTime(now)
                .lastModifiedBy("SYSTEM")
                .lastModifiedTime(now)
                .build();

        assertThat(data.getModuleIngestionId()).isEqualTo("uuid-123");
        assertThat(data.getModuleDetailId()).isEqualTo("detail-456");
        assertThat(data.getTenantId()).isEqualTo("pb.amritsar");
        assertThat(data.getModuleName()).isEqualTo("PT");
        assertThat(data.getPushDate()).isEqualTo("2024-01-15");
        assertThat(data.getRequestData()).isEqualTo("{\"key\": \"value\"}");
        assertThat(data.getResponseData()).isEqualTo("{\"status\": \"ok\"}");
        assertThat(data.getIngestionStatus()).isEqualTo("SUCCESS");
        assertThat(data.getCreatedBy()).isEqualTo("SYSTEM");
        assertThat(data.getCreatedTime()).isEqualTo(now);
        assertThat(data.getLastModifiedBy()).isEqualTo("SYSTEM");
        assertThat(data.getLastModifiedTime()).isEqualTo(now);
    }

    @Test
    @DisplayName("No-args constructor and setters work")
    void noArgsConstructorAndSetters_work() {
        DailyIngestionData data = new DailyIngestionData();
        data.setModuleIngestionId("uuid-abc");
        data.setIngestionStatus("FAILURE");

        assertThat(data.getModuleIngestionId()).isEqualTo("uuid-abc");
        assertThat(data.getIngestionStatus()).isEqualTo("FAILURE");
    }

    @Test
    @DisplayName("Lombok toString contains key fields")
    void lombok_toStringContainsKeyFields() {
        DailyIngestionData data = DailyIngestionData.builder()
                .moduleIngestionId("uuid-1")
                .moduleName("PT")
                .ingestionStatus("SUCCESS")
                .build();

        assertThat(data.toString()).contains("uuid-1");
        assertThat(data.toString()).contains("PT");
    }
}