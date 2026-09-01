package org.upyog.dashboard.extractor.impl;

import org.upyog.dashboard.constants.DashboardExtractorConstants;
import org.apache.commons.lang3.StringUtils;
import org.upyog.dashboard.config.DashboardProperties;
import org.upyog.dashboard.util.HierarchyParser;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.upyog.dashboard.pt.model.RawPtMetric;
import org.upyog.dashboard.pt.model.RawPtCollection;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.config.SchemaMappingConfig;
import org.upyog.dashboard.extractor.ModuleExtractor;
import org.upyog.dashboard.pt.constants.PTDatabaseConstants;
import org.upyog.dashboard.pt.dto.PTAggregatedData;
import org.upyog.dashboard.pt.dto.PTCollectionDTO;
import org.upyog.dashboard.pt.dto.PTDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Property Tax (PT) implementation of {@link ModuleExtractor} which extracts
 * raw database metrics and maps them into {@link PTDTO}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PtModuleExtractor implements ModuleExtractor<List<PTDTO>> {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SchemaMappingConfig schemaMappingConfig;
    private final DashboardProperties dashboardProperties;
    private final HierarchyParser hierarchyParser;

    private String dbTenantId;
    private int dbMaxAttempts;
    private long dbBaseDelayMs;
    private long dbMaxDelayMs;

    /**
     * Initialises the database tenant ID and retry configuration values from
     * {@link DashboardProperties} after bean construction.
     */
    @jakarta.annotation.PostConstruct
    public void init() {
        String state = dashboardProperties.getMetricState();
        this.dbTenantId = (StringUtils.isNotBlank(state)) ? state : dashboardProperties.getTenantId();
        this.dbMaxAttempts = dashboardProperties.getDbMaxAttempts();
        this.dbBaseDelayMs = dashboardProperties.getDbBaseDelayMs();
        this.dbMaxDelayMs = dashboardProperties.getDbMaxDelayMs();
    }

    @Override
    public Module getModule() {
        return Module.PT;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Extracts PT metrics for {@code targetDate} by executing both the combined
     * metrics query and the collection metrics query, then building a
     * {@link PTDTO} per tenant row.
     *
     * @param targetDate the date for which PT metrics should be extracted
     * @return a list of {@link PTDTO} payloads, one per tenant row returned by
     * the query
     * @throws IllegalStateException if no query mapping is found in
     * schema-mapping.yml for PT
     */
    @Override
    public List<PTDTO> extractData(LocalDate targetDate) {
        String dateStr = targetDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        long startTime = targetDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        long endTime = targetDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() - 1;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(DashboardExtractorConstants.PARAM_START_TIME, startTime)
                .addValue(DashboardExtractorConstants.PARAM_END_TIME, endTime)
                .addValue(DashboardExtractorConstants.PARAM_TENANT_ID, dbTenantId);

        SchemaMappingConfig.ModuleQueries ptQueries = schemaMappingConfig.getQueriesForModule(Module.PT);
        if (ptQueries == null) {
            throw new IllegalStateException("No query mapping found in schema-mapping.yml for module PT");
        }

        List<RawPtMetric> combinedResults = executeQueryWithRetry(ptQueries.getCombinedMetricsQuery(), params, RawPtMetric.class);
        List<RawPtCollection> collectionRowsRaw = executeQueryWithRetry(ptQueries.getCollectionMetricsQuery(), params, RawPtCollection.class);
        Map<String, List<PTCollectionDTO>> collectionsByTenant = groupCollectionsByTenant(collectionRowsRaw);

        List<PTDTO> results = new ArrayList<>();
        for (RawPtMetric row : combinedResults) {
            results.add(buildPtDto(row, dateStr, collectionsByTenant));
        }
        return results;
    }

    /**
     * Groups collection-metrics rows by tenant ID, converting each raw row into
     * a {@link PTCollectionDTO}.
     *
     * @param collectionRowsRaw the raw rows returned by the collection metrics
     * query
     * @return a map from tenant ID to the list of {@link PTCollectionDTO}
     * records for that tenant
     */
    private Map<String, List<PTCollectionDTO>> groupCollectionsByTenant(List<RawPtCollection> collectionRowsRaw) {
        Map<String, List<PTCollectionDTO>> collectionsByTenant = new HashMap<>();
        for (RawPtCollection row : collectionRowsRaw) {
            String currentTenant = row.getTenantid();
            PTCollectionDTO dto = new PTCollectionDTO();
            BeanUtils.copyProperties(row, dto);
            collectionsByTenant.computeIfAbsent(currentTenant, k -> new ArrayList<>()).add(dto);
        }
        return collectionsByTenant;
    }

    /**
     * Builds a {@link PTDTO} from a raw combined-metrics result-set row
     * together with the pre-grouped collection data for the same tenant.
     *
     * @param row a single row from the combined metrics query, keyed by column
     * alias
     * @param dateStr the formatted date string (dd-MM-yyyy) for the target date
     * @param collectionsByTenant a map from tenant ID to its collection records
     * @return a fully populated {@link PTDTO}
     */
    private PTDTO buildPtDto(RawPtMetric row, String dateStr, Map<String, List<PTCollectionDTO>> collectionsByTenant) {
        String currentTenantId = row.getTenantid();
        Map<String, String> parsedHierarchy = hierarchyParser.parseTenantId(currentTenantId);

        PTAggregatedData combinedData = new PTAggregatedData();
        BeanUtils.copyProperties(row, combinedData);

        List<PTCollectionDTO> tenantCollections = collectionsByTenant.getOrDefault(currentTenantId, List.of());

        return PTDTO.builder()
                .date(dateStr)
                .module(getModule().name())
                .ward(parsedHierarchy.get(DashboardExtractorConstants.KEY_WARD))
                .ulb(parsedHierarchy.get(DashboardExtractorConstants.KEY_ULB))
                .region(parsedHierarchy.get(DashboardExtractorConstants.KEY_REGION))
                .state(parsedHierarchy.get(DashboardExtractorConstants.KEY_STATE))
                .combinedMetrics(combinedData)
                .collectionMetrics(tenantCollections)
                .build();
    }

    /**
     * Executes the given named-parameter SQL query against the PT data source
     * with exponential backoff retry logic. Throws the underlying exception
     * when the maximum number of attempts is exhausted.
     *
     * @param query the named-parameter SQL query string
     * @param params the named parameters to bind as MapSqlParameterSource
     * @return the query result as a list of mapped beans
     */
    private <T> List<T> executeQueryWithRetry(String query, MapSqlParameterSource params, Class<T> mappedClass) {
        int attempt = 0;
        while (true) {
            attempt++;
            try {
                return namedParameterJdbcTemplate.query(query, params, new BeanPropertyRowMapper<>(mappedClass));
            } catch (Exception exception) {
                if (attempt >= dbMaxAttempts) {
                    log.error("PtModuleExtractor | DB query failed after {} attempts.", attempt, exception);
                    throw exception;
                }
                long backoff = calculateDbBackoffWithJitter(attempt);
                log.warn("PtModuleExtractor | DB query failed (attempt {}/{}). Retrying in {} ms. Error: {}",
                        attempt, dbMaxAttempts, backoff, exception.getMessage());
                sleepWithInterruptHandling(backoff);
            }
        }
    }

    /**
     * Sleeps for the specified duration, restoring the interrupted flag and
     * throwing an {@link IllegalStateException} if the thread is interrupted
     * during sleep.
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
     * Calculates a jittered exponential back-off delay capped at
     * {@code dbMaxDelayMs}.
     *
     * @param attempt the current attempt number (1-based)
     * @return a random delay in milliseconds within
     * {@code [0, min(dbMaxDelayMs, 2^(attempt-1) * dbBaseDelayMs)]}
     */
    private long calculateDbBackoffWithJitter(int attempt) {
        int power = Math.min(attempt - 1, 30);
        long expDelay = dbBaseDelayMs * (1L << power);
        if (expDelay < 0) {
            expDelay = dbMaxDelayMs;
        }
        long currentMaxDelay = Math.min(dbMaxDelayMs, expDelay);
        return java.util.concurrent.ThreadLocalRandom.current().nextLong(0, currentMaxDelay + 1);
    }
}
