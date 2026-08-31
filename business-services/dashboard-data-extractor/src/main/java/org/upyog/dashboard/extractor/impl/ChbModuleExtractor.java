package org.upyog.dashboard.extractor.impl;

import java.net.URI;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.beans.BeanUtils;
import org.upyog.dashboard.chb.constants.CHBDatabaseConstants;
import org.upyog.dashboard.chb.dto.CHBAggregatedData;
import org.upyog.dashboard.chb.dto.CHBDTO;
import org.upyog.dashboard.client.UserFeignClient;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.config.DashboardProperties;
import org.upyog.dashboard.config.SchemaMappingConfig;
import org.upyog.dashboard.extractor.ModuleExtractor;
import org.upyog.dashboard.model.UserInfo;
import org.upyog.dashboard.model.UserSearchResponse;
import org.upyog.dashboard.chb.model.RawChbMetric;
import org.upyog.dashboard.service.OAuthTokenService;
import org.upyog.dashboard.util.HierarchyParser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Extracts CHB (Community Hall Booking) metrics for a given date and builds a CHBDTO
 * ready for ingestion into the National Dashboard.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChbModuleExtractor implements ModuleExtractor<List<CHBDTO>> {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SchemaMappingConfig schemaMappingConfig;
    private final DashboardProperties dashboardProperties;
    private final UserFeignClient userFeignClient;
    private final OAuthTokenService oAuthTokenService;
    private final ObjectMapper objectMapper;
    private final HierarchyParser hierarchyParser;

    private String dbTenantId;
    private int dbMaxAttempts;
    private long dbBaseDelayMs;
    private long dbMaxDelayMs;

    /**
     * Initialises the database tenant ID and retry configuration values from
     * {@link DashboardProperties} after bean construction.
     */
    @PostConstruct
    public void init() {
        String state = dashboardProperties.getMetricState();
        this.dbTenantId = (state != null && !state.isBlank()) ? state : dashboardProperties.getTenantId();
        this.dbMaxAttempts = dashboardProperties.getDbMaxAttempts();
        this.dbBaseDelayMs = dashboardProperties.getDbBaseDelayMs();
        this.dbMaxDelayMs = dashboardProperties.getDbMaxDelayMs();
    }

    @Override
    public Module getModule() {
        return Module.CHB;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Extracts CHB metrics for {@code targetDate} by executing the combined metrics query
     * and mapping each row to a {@link CHBDTO}.
     *
     * @param targetDate the date for which CHB metrics should be extracted
     * @return a list of {@link CHBDTO} payloads, one per tenant row returned by the query
     */
    @Override
    public List<CHBDTO> extractData(LocalDate targetDate) {
        String dateStr = targetDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        long startTime = targetDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        long endTime = targetDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() - 1;

        Map<String, Object> params = Map.of("startTime", startTime, "endTime", endTime, "tenantId", dbTenantId);
        List<CHBDTO> results = new ArrayList<>();

        SchemaMappingConfig.ModuleQueries chbQueries = schemaMappingConfig.getQueriesForModule(Module.CHB);
        if (chbQueries == null) {
            throw new IllegalStateException("No query mapping found in chb-schema-mapping.yml for module CHB");
        }

        List<RawChbMetric> combinedResults = executeQueryWithRetry(chbQueries.getCombinedMetricsQuery(), params, RawChbMetric.class);
        for (RawChbMetric row : combinedResults) {
            results.add(buildChbDto(row, dateStr));
        }
        return results;
    }

    /**
     * Builds a {@link CHBDTO} from a raw combined-metrics result-set row.
     *
     * @param row     a single row returned by the combined metrics query, keyed by column alias
     * @param dateStr the formatted date string (dd-MM-yyyy) for the extraction target date
     * @return a fully populated {@link CHBDTO}
     */
    private CHBDTO buildChbDto(RawChbMetric row, String dateStr) {
        String currentTenantId = row.getTenantid();
        Map<String, String> parsedHierarchy = hierarchyParser.parseTenantId(currentTenantId);

        CHBAggregatedData combinedData = new CHBAggregatedData();
        BeanUtils.copyProperties(row, combinedData);
        combinedData.setBookingTypeJson(buildBookingTypeJson(combinedData.getCreatedByListJson()));

        return CHBDTO.builder()
                .date(dateStr)
                .module(getModule().name())
                .ward(parsedHierarchy.get("ward"))
                .ulb(parsedHierarchy.get("ulb"))
                .region(parsedHierarchy.get("region"))
                .state(parsedHierarchy.get("state"))
                .combinedMetrics(combinedData)
                .build();
    }

 

    /**
     * Derives the booking-type classification JSON (online vs. offline) from the
     * creator UUID list. Returns an empty JSON array ({@code "[]"}) when the input is
     * blank or contains no UUIDs.
     *
     * @param createdByListJson a JSON array string of creator-UUID rows, or blank
     * @return a JSON array string such as
     *         {@code [{"name":"Online","value":3},{"name":"Offline","value":2}]}
     */
    private String buildBookingTypeJson(String createdByListJson) {
        if (createdByListJson == null || createdByListJson.isBlank() || "[]".equals(createdByListJson)) {
            return "[]";
        }

        List<String> uuids = extractUuids(createdByListJson);
        if (uuids.isEmpty()) {
            return "[]";
        }

        return classifyUsers(uuids);
    }

    /**
     * Parses the {@code createdByListJson} JSON string and extracts unique creator UUIDs
     * in encounter order.
     *
     * @param createdByListJson a JSON array string of objects containing a {@code createdby} field
     * @return an ordered, deduplicated list of UUID strings; empty on parse failure
     */
    private List<String> extractUuids(String createdByListJson) {
        try {
            List<Map<String, Object>> rows = objectMapper.readValue(createdByListJson,
                    new TypeReference<List<Map<String, Object>>>() {});
            LinkedHashSet<String> uuidSet = new LinkedHashSet<>();
            for (Map<String, Object> row : rows) {
                Object cb = row.get("createdby");
                if (cb != null) {
                    uuidSet.add(cb.toString());
                }
            }
            return List.copyOf(uuidSet);
        } catch (Exception e) {
            log.warn("ChbModuleExtractor | Failed to parse createdByListJson: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Calls the UPYOG user-search API to classify UUIDs as CITIZEN (online) or
     * employee (offline), then returns the result as a JSON array string.
     * Returns an empty JSON array ({@code "[]"}) if the API call fails.
     *
     * @param uuids the list of creator UUIDs to classify
     * @return a JSON array string with {@code Online} and {@code Offline} counts
     */
    private String classifyUsers(List<String> uuids) {
        int online = 0;
        int offline = 0;
        try {
            String token = oAuthTokenService.getToken();
            Map<String, Object> requestBody = Map.of(
                    "RequestInfo", Map.of("apiId", "Rainmaker", "authToken", token),
                    "tenantId", dashboardProperties.getTenantId(),
                    "uuid", uuids);

            String url = dashboardProperties.getOauthHost() + dashboardProperties.getUserSearchPath();
            UserSearchResponse response = userFeignClient.searchUser(URI.create(url), requestBody);

            if (response != null && response.getUser() != null) {
                for (UserInfo user : response.getUser()) {
                    if ("CITIZEN".equalsIgnoreCase(user.getType())) {
                        online++;
                    } else {
                        offline++;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("ChbModuleExtractor | User search failed, bookingType will be empty: {}", e.getMessage());
            return "[]";
        }

        return "[{\"name\":\"Online\",\"value\":" + online + "},{\"name\":\"Offline\",\"value\":" + offline + "}]";
    }

    /**
     * Executes the given named-parameter SQL query against the CHB data source with
     * exponential backoff retry logic. Throws the underlying exception when the maximum
     * number of attempts is exhausted.
     *
     * @param query  the named-parameter SQL query string
     * @param params the named parameters to bind
     * @return the query result as a list of row maps
     */
    private <T> List<T> executeQueryWithRetry(String query, Map<String, Object> params, Class<T> mappedClass) {
        int attempt = 0;
        while (true) {
            attempt++;
            try {
                return namedParameterJdbcTemplate.query(query, params, new BeanPropertyRowMapper<>(mappedClass));
            } catch (Exception e) {
                if (attempt >= dbMaxAttempts) {
                    log.error("ChbModuleExtractor | DB query failed after {} attempts.", attempt, e);
                    throw e;
                }
                long backoff = calculateBackoff(attempt);
                log.warn("ChbModuleExtractor | DB query failed (attempt {}/{}). Retrying in {} ms. Error: {}",
                        attempt, dbMaxAttempts, backoff, e.getMessage());
                sleepWithInterruptHandling(backoff);
            }
        }
    }

    /**
     * Sleeps for the specified duration, restoring the interrupted flag and throwing
     * an {@link IllegalStateException} if the thread is interrupted during sleep.
     *
     * @param backoff the duration to sleep in milliseconds
     */
    private void sleepWithInterruptHandling(long backoff) {
        try {
            Thread.sleep(backoff);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("DB query retry interrupted", ie);
        }
    }

    /**
     * Calculates a jittered exponential back-off delay capped at {@code dbMaxDelayMs}.
     *
     * @param attempt the current attempt number (1-based)
     * @return a random delay in milliseconds within {@code [0, min(dbMaxDelayMs, 2^(attempt-1) * dbBaseDelayMs)]}
     */
    private long calculateBackoff(int attempt) {
        int power = Math.min(attempt - 1, 30);
        long expDelay = dbBaseDelayMs * (1L << power);
        if (expDelay < 0) {
            expDelay = dbMaxDelayMs;
        }
        long currentMax = Math.min(dbMaxDelayMs, expDelay);
        return java.util.concurrent.ThreadLocalRandom.current().nextLong(0, currentMax + 1);
    }
}
