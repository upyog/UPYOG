package org.upyog.dashboard.repository;

import org.upyog.dashboard.repository.querybuilder.IngestionSummaryQueryBuilder;
import org.upyog.dashboard.service.IngestionPersistenceService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.RowMapper;

@ExtendWith(MockitoExtension.class)
class IngestionSummaryRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Mock
    private IngestionPersistenceService persistenceService;

    @InjectMocks
    private IngestionSummaryRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        java.lang.reflect.Field field = IngestionSummaryRepository.class.getDeclaredField("queryBuilder");
        field.setAccessible(true);
        field.set(repository, new IngestionSummaryQueryBuilder());
    }

    @Test
    @DisplayName("findLastSuccessfulDate returns empty when no row exists")
    void findLastSuccessfulDate_returnsEmptyWhenNotFound() {
        org.mockito.Mockito.lenient().when(namedParameterJdbcTemplate.query(anyString(), any(SqlParameterSource.class), any(RowMapper.class)))
                .thenReturn(Collections.emptyList());

        Optional<LocalDate> dateOpt = repository.findLastSuccessfulDate("pg", "PT");

        assertThat(dateOpt).isEmpty();
    }

    @Test
    @DisplayName("findLastSuccessfulDate returns date when row exists")
    void findLastSuccessfulDate_returnsDateWhenFound() {
        LocalDate expected = LocalDate.of(2026, 6, 30);
        org.mockito.Mockito.lenient().when(namedParameterJdbcTemplate.query(anyString(), any(SqlParameterSource.class), any(RowMapper.class)))
                .thenReturn(List.of(Date.valueOf(expected)));

        Optional<LocalDate> dateOpt = repository.findLastSuccessfulDate("pg", "PT");

        assertThat(dateOpt).contains(expected);
    }

    @Test
    @DisplayName("saveOrUpdateLastSuccessfulDate pushes to Kafka topic")
    void saveOrUpdateLastSuccessfulDate_pushesToKafka() {
        LocalDate targetDate = LocalDate.of(2026, 7, 1);
        repository.saveOrUpdateLastSuccessfulDate("pg", "PT", targetDate);

        verify(persistenceService).saveOrUpdateLastSuccessfulDate(eq("pg"), eq("PT"), eq(targetDate));
    }

    @Test
    @DisplayName("saveOrUpdateLastAttemptedDate pushes to Kafka topic")
    void saveOrUpdateLastAttemptedDate_pushesToKafka() {
        LocalDate targetDate = LocalDate.of(2026, 7, 1);
        repository.saveOrUpdateLastAttemptedDate("pg", "PT", targetDate);

        verify(persistenceService).saveOrUpdateLastAttemptedDate(eq("pg"), eq("PT"), eq(targetDate));
    }
}
