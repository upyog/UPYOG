package org.upyog.dashboard.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.upyog.dashboard.common.constants.DashboardConstants;
import org.upyog.dashboard.model.DashboardData;
import org.upyog.dashboard.model.DashboardPayload;
import org.upyog.dashboard.repository.querybuilder.IngestionRecordQueryBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class JdbcIngestionRecordPersistenceServiceImplTest {

    @InjectMocks
    private JdbcIngestionRecordPersistenceServiceImpl service;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Test
    @DisplayName("Verify pushIngestionRecord persists moduleDetailId and schedulerId in detail params")
    void pushIngestionRecord_persistsModuleDetailIdAndSchedulerId() {
        DashboardData data = DashboardData.builder()
                .module("PT")
                .ulb("pg.citya")
                .date("08-09-2026")
                .build();

        DashboardPayload payload = DashboardPayload.builder()
                .data(List.of(data))
                .moduleDetailId("mod-detail-uuid-123")
                .schedulerId("sched-uuid-456")
                .build();

        service.pushIngestionRecord(payload, "{\"request\":\"data\"}", "{\"status\":\"ok\"}", "SUCCESS");

        ArgumentCaptor<MapSqlParameterSource> captor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        verify(namedParameterJdbcTemplate).update(eq(IngestionRecordQueryBuilder.INSERT_INGESTION_DETAIL), captor.capture());

        MapSqlParameterSource params = captor.getValue();
        assertThat(params.getValue(DashboardConstants.PARAM_MODULE_DETAIL_ID)).isEqualTo("mod-detail-uuid-123");
        assertThat(params.getValue(DashboardConstants.PARAM_SCHEDULER_ID)).isEqualTo("sched-uuid-456");
        assertThat(params.getValue(DashboardConstants.PARAM_TENANT_ID)).isEqualTo("pg.citya");
        assertThat(params.getValue(DashboardConstants.PARAM_MODULE_NAME)).isEqualTo("PT");
        assertThat(params.getValue(DashboardConstants.PARAM_INGESTION_STATUS)).isEqualTo("SUCCESS");
    }
}
