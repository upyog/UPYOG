package org.upyog.dashboard.service.impl;

import org.upyog.dashboard.constants.DashboardExtractorConstants;
import org.mockito.ArgumentMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.upyog.dashboard.repository.querybuilder.IngestionSummaryQueryBuilder;

@ExtendWith(MockitoExtension.class)
class JdbcIngestionPersistenceServiceImplTest {

    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Mock
    private IngestionSummaryQueryBuilder queryBuilder;

    @InjectMocks
    private JdbcIngestionPersistenceServiceImpl service;

    @BeforeEach
    void setUp() {
        // Mock query builder returns empty strings or dummy strings to avoid NullPointerException
    }

    @Test
    @DisplayName("saveOrUpdateLastSuccessfulDate uses JdbcTemplate update")
    void saveOrUpdateLastSuccessfulDate_usesJdbcTemplate() {
        when(queryBuilder.getUpsertLastSuccessfulDateQuery()).thenReturn("UPSERT SUCCESSFUL DATE");
        LocalDate targetDate = LocalDate.of(2026, 7, 1);
        service.saveOrUpdateLastSuccessfulDate("pg", "PT", targetDate);

        verify(namedParameterJdbcTemplate).update(anyString(), any(SqlParameterSource.class));
    }

    @Test
    @DisplayName("saveOrUpdateLastAttemptedDate uses JdbcTemplate update")
    void saveOrUpdateLastAttemptedDate_usesJdbcTemplate() {
        when(queryBuilder.getUpsertLastAttemptedDateQuery()).thenReturn("UPSERT ATTEMPTED DATE");
        LocalDate targetDate = LocalDate.of(2026, 7, 1);
        service.saveOrUpdateLastAttemptedDate("pg", "PT", targetDate);

        verify(namedParameterJdbcTemplate).update(anyString(), any(SqlParameterSource.class));
    }

    @Test
    @DisplayName("createLegacyJob uses JdbcTemplate update")
    void createLegacyJob_usesJdbcTemplate() {
        when(queryBuilder.getInsertLegacyJobQuery()).thenReturn("INSERT LEGACY JOB");
        LocalDate targetDate = LocalDate.of(2026, 7, 1);
        service.createLegacyJob("job-123", "pg", "PT", targetDate, targetDate, targetDate);

        verify(namedParameterJdbcTemplate).update(anyString(), any(SqlParameterSource.class));
    }

    @Test
    @DisplayName("updateLegacyJobStatus uses JdbcTemplate update")
    void updateLegacyJobStatus_usesJdbcTemplate() {
        when(queryBuilder.getUpdateLegacyJobStatusQuery()).thenReturn("UPDATE LEGACY JOB");
        service.updateLegacyJobStatus("job123", DashboardExtractorConstants.STATUS_SUCCESS, "{}", "{}");

        verify(namedParameterJdbcTemplate).update(anyString(), any(SqlParameterSource.class));
    }
}
