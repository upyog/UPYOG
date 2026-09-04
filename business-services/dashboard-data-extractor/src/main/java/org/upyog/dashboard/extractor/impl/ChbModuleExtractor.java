package org.upyog.dashboard.extractor.impl;

import org.upyog.dashboard.constants.DashboardExtractorConstants;
import org.apache.commons.lang3.StringUtils;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;
import org.upyog.dashboard.chb.dto.CHBAggregatedData;
import org.upyog.dashboard.chb.dto.CHBDTO;
import org.upyog.dashboard.chb.mapper.CHBRowMapper;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.config.DashboardProperties;
import org.upyog.dashboard.config.SchemaMappingConfig;
import org.upyog.dashboard.extractor.ModuleExtractor;
import org.upyog.dashboard.chb.model.RawChbMetric;
import org.upyog.dashboard.util.HierarchyParser;
import org.upyog.dashboard.util.DatabaseQueryExecutor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Extracts CHB (Community Hall Booking) metrics for a given date and builds a CHBDTO
 * ready for ingestion into the National Dashboard without reflection.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChbModuleExtractor implements ModuleExtractor<List<CHBDTO>> {

    private final DatabaseQueryExecutor queryExecutor;
    private final SchemaMappingConfig schemaMappingConfig;
    private final DashboardProperties dashboardProperties;
    private final HierarchyParser hierarchyParser;

    private String dbTenantId;

    /**
     * Initialises the database tenant ID from {@link DashboardProperties} after bean construction.
     */
    @PostConstruct
    public void init() {
        String state = dashboardProperties.getMetricState();
        this.dbTenantId = (StringUtils.isNotBlank(state)) ? state : dashboardProperties.getTenantId();
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

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(DashboardExtractorConstants.PARAM_START_TIME, startTime)
                .addValue(DashboardExtractorConstants.PARAM_END_TIME, endTime)
                .addValue(DashboardExtractorConstants.PARAM_TENANT_ID, dbTenantId);
        List<CHBDTO> results = new ArrayList<>();

        SchemaMappingConfig.ModuleQueries chbQueries = schemaMappingConfig.getQueriesForModule(Module.CHB);
        if (chbQueries == null) {
            throw new IllegalStateException("No query mapping found in chb-schema-mapping.yml for module CHB");
        }

        List<RawChbMetric> combinedResults = queryExecutor.executeQueryWithRetry(chbQueries.getCombinedMetricsQuery(), params, CHBRowMapper.COMBINED_ROW_MAPPER, "ChbModuleExtractor");
        for (RawChbMetric row : combinedResults) {
            results.add(buildChbDto(row, dateStr));
        }
        return results;
    }

    /**
     * Converts a single raw row map into a populated {@link CHBDTO}.
     *
     * @param row the raw column values returned by the database
     * @param dateStr the formatted date string (dd-MM-yyyy) for the target date
     * @return a fully populated {@link CHBDTO} instance
     */
    private CHBDTO buildChbDto(RawChbMetric row, String dateStr) {
        String currentTenantId = row.getTenantid();
        Map<String, String> parsedHierarchy = hierarchyParser.parseTenantId(currentTenantId);

        CHBAggregatedData combinedData = new CHBAggregatedData();
        combinedData.setTotalActiveVenueAvailable(row.getTotalActiveVenueAvailable());
        combinedData.setTotalApplicationReceived(row.getTotalApplicationReceived());
        combinedData.setTotalCollections(row.getTotalCollections());
        combinedData.setNoShowBookings(row.getNoShowBookings());
        combinedData.setBookingsJson(row.getBookingsJson());
        combinedData.setCreatedByListJson(row.getCreatedByListJson());

        return CHBDTO.builder()
                .date(dateStr)
                .module(getModule().name())
                .ward(parsedHierarchy.get(DashboardExtractorConstants.KEY_WARD))
                .ulb(parsedHierarchy.get(DashboardExtractorConstants.KEY_ULB))
                .region(parsedHierarchy.get(DashboardExtractorConstants.KEY_REGION))
                .state(parsedHierarchy.get(DashboardExtractorConstants.KEY_STATE))
                .combinedMetrics(combinedData)
                .build();
    }
}
