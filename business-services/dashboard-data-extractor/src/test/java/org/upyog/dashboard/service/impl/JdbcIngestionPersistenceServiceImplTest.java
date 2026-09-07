package org.upyog.dashboard.service.impl;

import org.upyog.dashboard.constants.DashboardExtractorConstants;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;

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

    @InjectMocks
    private JdbcIngestionPersistenceServiceImpl service;

    @Test
    @DisplayName("saveOrUpdateLastSuccessfulDate uses JdbcTemplate update")
    void saveOrUpdateLastSuccessfulDate_usesJdbcTemplate() {
        LocalDate targetDate = LocalDate.of(2026, 7, 1);
        service.saveOrUpdateLastSuccessfulDate("pg", "PT", targetDate);

        verify(namedParameterJdbcTemplate).update(eq(IngestionSummaryQueryBuilder.UPSERT_LAST_SUCCESSFUL_DATE_QUERY), any(SqlParameterSource.class));
    }

    @Test
    @DisplayName("saveOrUpdateLastAttemptedDate uses JdbcTemplate update")
    void saveOrUpdateLastAttemptedDate_usesJdbcTemplate() {
        LocalDate targetDate = LocalDate.of(2026, 7, 1);
        service.saveOrUpdateLastAttemptedDate("pg", "PT", targetDate);

        verify(namedParameterJdbcTemplate).update(eq(IngestionSummaryQueryBuilder.UPSERT_LAST_ATTEMPTED_DATE_QUERY), any(SqlParameterSource.class));
    }

    @Test
    @DisplayName("createLegacyJob uses JdbcTemplate update")
    void createLegacyJob_usesJdbcTemplate() {
        LocalDate targetDate = LocalDate.of(2026, 7, 1);
        service.createLegacyJob("job-123", "pg", "PT", targetDate, targetDate, targetDate);

        verify(namedParameterJdbcTemplate).update(eq(IngestionSummaryQueryBuilder.INSERT_LEGACY_JOB_QUERY), any(SqlParameterSource.class));
    }

    @Test
    @DisplayName("updateLegacyJobStatus uses JdbcTemplate update")
    void updateLegacyJobStatus_usesJdbcTemplate() {
        service.updateLegacyJobStatus("job123", DashboardExtractorConstants.STATUS_SUCCESS, "{}", "{}");

        verify(namedParameterJdbcTemplate).update(eq(IngestionSummaryQueryBuilder.UPDATE_LEGACY_JOB_STATUS_QUERY), any(SqlParameterSource.class));
    }
}

