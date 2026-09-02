package org.upyog.dashboard.util;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.upyog.dashboard.config.DashboardProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Reusable utility service for executing named-parameter SQL queries with
 * configured exponential back-off and jitter retry policies using explicit {@link RowMapper}s.
 * <p>
 * Does NOT use reflection, ensuring full transparency of column-to-field mapping and high performance.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseQueryExecutor {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final DashboardProperties dashboardProperties;

    /**
     * Executes the given SQL query against the configured database, mapping rows using the provided {@link RowMapper}
     * with automatic retry on failure.
     *
     * @param <T> the target type
     * @param query the named-parameter SQL query
     * @param params parameters to bind
     * @param rowMapper explicit RowMapper implementation
     * @param callerContext descriptive context for logging (e.g. "PtModuleExtractor")
     * @return list of mapped result items
     */
    public <T> List<T> executeQueryWithRetry(String query, MapSqlParameterSource params, RowMapper<T> rowMapper, String callerContext) {
        int maxAttempts = dashboardProperties.getDbMaxAttempts() > 0 ? dashboardProperties.getDbMaxAttempts() : 3;
        long baseDelayMs = dashboardProperties.getDbBaseDelayMs() > 0 ? dashboardProperties.getDbBaseDelayMs() : 1000;
        long maxDelayMs = dashboardProperties.getDbMaxDelayMs() > 0 ? dashboardProperties.getDbMaxDelayMs() : 5000;

        int attempt = 0;
        while (true) {
            attempt++;
            try {
                return namedParameterJdbcTemplate.query(query, params, rowMapper);
            } catch (Exception exception) {
                if (attempt >= maxAttempts) {
                    log.error("{} | DB query failed after {} attempts.", callerContext, attempt, exception);
                    throw exception;
                }
                long backoff = calculateDbBackoffWithJitter(attempt, baseDelayMs, maxDelayMs);
                log.warn("{} | DB query failed (attempt {}/{}). Retrying in {} ms. Error: {}",
                        callerContext, attempt, maxAttempts, backoff, exception.getMessage());
                sleepWithInterruptHandling(backoff);
            }
        }
    }

    private void sleepWithInterruptHandling(long backoff) {
        try {
            Thread.sleep(backoff);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("DB query retry interrupted", ie);
        }
    }

    private long calculateDbBackoffWithJitter(int attempt, long baseDelayMs, long maxDelayMs) {
        int power = Math.min(attempt - 1, 30);
        long expDelay = baseDelayMs * (1L << power);
        if (expDelay < 0) {
            expDelay = maxDelayMs;
        }
        long currentMaxDelay = Math.min(maxDelayMs, expDelay);
        return ThreadLocalRandom.current().nextLong(0, currentMaxDelay + 1);
    }
}
