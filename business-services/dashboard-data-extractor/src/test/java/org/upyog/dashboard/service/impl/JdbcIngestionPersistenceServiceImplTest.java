package org.upyog.dashboard.service.impl;

import org.mockito.ArgumentMatchers;

import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.upyog.dashboard.repository.querybuilder.IngestionSummaryQueryBuilder;

@ExtendWith(MockitoExtension.class)
class JdbcIngestionPersistenceServiceImplTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

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

        verify(jdbcTemplate).update(
                eq("UPSERT SUCCESSFUL DATE"),
                any(String.class),
                eq("pg"),
                eq("PT"),
                eq(Date.valueOf(targetDate)),
                eq(Date.valueOf(targetDate)),
                eq("SYSTEM"),
                any(Long.class),
                eq("SYSTEM"),
                any(Long.class)
        );
    }

    @Test
    @DisplayName("saveOrUpdateLastAttemptedDate uses JdbcTemplate update")
    void saveOrUpdateLastAttemptedDate_usesJdbcTemplate() {
        when(queryBuilder.getUpsertLastAttemptedDateQuery()).thenReturn("UPSERT ATTEMPTED DATE");
        LocalDate targetDate = LocalDate.of(2026, 7, 1);
        service.saveOrUpdateLastAttemptedDate("pg", "PT", targetDate);

        verify(jdbcTemplate).update(
                eq("UPSERT ATTEMPTED DATE"),
                any(String.class),
                eq("pg"),
                eq("PT"),
                eq(Date.valueOf(LocalDate.of(1970, 1, 1))),
                eq(Date.valueOf(targetDate)),
                eq("SYSTEM"),
                any(Long.class),
                eq("SYSTEM"),
                any(Long.class)
        );
    }

    @Test
    @DisplayName("createLegacyJob uses JdbcTemplate update")
    void createLegacyJob_usesJdbcTemplate() {
        when(queryBuilder.getInsertLegacyJobQuery()).thenReturn("INSERT LEGACY JOB");
        LocalDate targetDate = LocalDate.of(2026, 7, 1);
        service.createLegacyJob("pg", "PT", targetDate);

        verify(jdbcTemplate).update(
                eq("INSERT LEGACY JOB"),
                any(String.class),
                eq("pg"),
                eq("PT"),
                eq(Date.valueOf(targetDate)),
                eq("NOT_STARTED"),
                ArgumentMatchers.isNull(),
                eq("SYSTEM"),
                any(Long.class),
                eq("SYSTEM"),
                any(Long.class)
        );
    }

    @Test
    @DisplayName("updateLegacyJobStatus uses JdbcTemplate update")
    void updateLegacyJobStatus_usesJdbcTemplate() {
        when(queryBuilder.getUpdateLegacyJobStatusQuery()).thenReturn("UPDATE LEGACY JOB");
        service.updateLegacyJobStatus("job123", "SUCCESS", "{}", "{}");

        verify(jdbcTemplate).update(
                eq("UPDATE LEGACY JOB"),
                eq("SUCCESS"),
                eq("{}"),
                eq("{}"),
                any(Long.class),
                eq("job123")
        );
    }
}
