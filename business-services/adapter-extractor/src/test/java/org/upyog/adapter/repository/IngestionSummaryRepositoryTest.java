package org.upyog.adapter.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

@ExtendWith(MockitoExtension.class)
class IngestionSummaryRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private IngestionSummaryRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        java.lang.reflect.Field field = IngestionSummaryRepository.class.getDeclaredField("queryBuilder");
        field.setAccessible(true);
        field.set(repository, new org.upyog.adapter.repository.querybuilder.IngestionSummaryQueryBuilder());
    }

    @Test
    @DisplayName("findLastSuccessfulDate returns empty when no row exists")
    void findLastSuccessfulDate_returnsEmptyWhenNotFound() {
        when(jdbcTemplate.query(any(String.class), any(RowMapper.class), eq("pg"), eq("PT")))
                .thenReturn(Collections.emptyList());

        Optional<LocalDate> dateOpt = repository.findLastSuccessfulDate("pg", "PT");

        assertThat(dateOpt).isEmpty();
    }

    @Test
    @DisplayName("findLastSuccessfulDate returns date when row exists")
    void findLastSuccessfulDate_returnsDateWhenFound() {
        LocalDate expected = LocalDate.of(2026, 6, 30);
        when(jdbcTemplate.query(any(String.class), any(RowMapper.class), eq("pg"), eq("PT")))
                .thenReturn(List.of(Date.valueOf(expected)));

        Optional<LocalDate> dateOpt = repository.findLastSuccessfulDate("pg", "PT");

        assertThat(dateOpt).contains(expected);
    }

    @Test
    @DisplayName("saveOrUpdateLastSuccessfulDate calls jdbcTemplate update")
    void saveOrUpdateLastSuccessfulDate_executesUpdate() {
        LocalDate targetDate = LocalDate.of(2026, 7, 1);
        repository.saveOrUpdateLastSuccessfulDate("pg", "PT", targetDate);

        verify(jdbcTemplate).update(
                any(String.class),
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
    @DisplayName("saveOrUpdateLastAttemptedDate calls jdbcTemplate update")
    void saveOrUpdateLastAttemptedDate_executesUpdate() {
        LocalDate targetDate = LocalDate.of(2026, 7, 1);
        repository.saveOrUpdateLastAttemptedDate("pg", "PT", targetDate);

        verify(jdbcTemplate).update(
                any(String.class),
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
}
