package org.upyog.adapter.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link LegacyIngestionData}.
 */
class LegacyIngestionDataTest {

    @Test
    @DisplayName("Builder creates LegacyIngestionData with all fields")
    void builder_createsFullEntity() {
        long now = System.currentTimeMillis();

        LegacyIngestionData data = LegacyIngestionData.builder()
                .moduleIngestionId("uuid-123")
                .moduleDetailId("detail-456")
                .tenantId("pb.amritsar")
                .ulbName("Amritsar")
                .moduleName("PT")
                .pushMonth("2024-01-01")
                .userId("user-1")
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
        assertThat(data.getUlbName()).isEqualTo("Amritsar");
        assertThat(data.getModuleName()).isEqualTo("PT");
        assertThat(data.getPushMonth()).isEqualTo("2024-01-01");
        assertThat(data.getUserId()).isEqualTo("user-1");
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
        LegacyIngestionData data = new LegacyIngestionData();
        data.setModuleIngestionId("uuid-abc");
        data.setIngestionStatus("NOT_STARTED");

        assertThat(data.getModuleIngestionId()).isEqualTo("uuid-abc");
        assertThat(data.getIngestionStatus()).isEqualTo("NOT_STARTED");
    }

    @Test
    @DisplayName("Lombok toString contains key fields")
    void lombok_toStringContainsKeyFields() {
        LegacyIngestionData data = LegacyIngestionData.builder()
                .moduleIngestionId("uuid-1")
                .moduleName("PT")
                .ingestionStatus("FAILURE")
                .build();

        assertThat(data.toString()).contains("uuid-1");
        assertThat(data.toString()).contains("PT");
    }
}