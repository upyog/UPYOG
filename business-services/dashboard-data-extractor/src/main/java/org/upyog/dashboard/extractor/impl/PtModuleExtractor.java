package org.upyog.dashboard.extractor.impl;

import org.upyog.dashboard.constants.DashboardExtractorConstants;
import org.apache.commons.lang3.StringUtils;
import org.upyog.dashboard.config.DashboardProperties;
import org.upyog.dashboard.util.HierarchyParser;
import org.upyog.dashboard.util.DatabaseQueryExecutor;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;
import org.upyog.dashboard.pt.model.RawPtMetric;
import org.upyog.dashboard.pt.model.RawPtCollection;
import org.upyog.dashboard.pt.mapper.PTRowmapper;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.config.SchemaMappingConfig;
import org.upyog.dashboard.extractor.ModuleExtractor;
import org.upyog.dashboard.pt.dto.PTAggregatedData;
import org.upyog.dashboard.pt.dto.PTCollectionDTO;
import org.upyog.dashboard.pt.dto.PTDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Property Tax (PT) implementation of {@link ModuleExtractor} which extracts
 * raw database metrics and maps them explicitly into {@link PTDTO} without reflection.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PtModuleExtractor implements ModuleExtractor<List<PTDTO>> {

    private final DatabaseQueryExecutor queryExecutor;
    private final SchemaMappingConfig schemaMappingConfig;
    private final DashboardProperties dashboardProperties;
    private final HierarchyParser hierarchyParser;

    private String dbTenantId;

    /**
     * Initialises the database tenant ID from {@link DashboardProperties} after bean construction.
     */
    @jakarta.annotation.PostConstruct
    public void init() {
        String state = dashboardProperties.getMetricState();
        this.dbTenantId = (StringUtils.isNotBlank(state)) ? state : dashboardProperties.getTenantId();
    }

    @Override
    public Module getModule() {
        return Module.PT;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Connects directly to the Property Tax schema via JDBC to extract daily
     * counts (assessments, applications, payments) across configured ULBs.
     *
     * @param targetDate the date for metric extraction
     * @return the extracted metrics payload wrapped in a {@link PTDTO} list
     */
    @Override
    public List<PTDTO> extractData(LocalDate targetDate) {
        String dateStr = targetDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        log.info("Starting Property Tax (PT) metrics extraction for date: {}", dateStr);

        long startTime = targetDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        long endTime = targetDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() - 1;

        SchemaMappingConfig.ModuleQueries ptQueries = schemaMappingConfig.getQueriesForModule(Module.PT);
        if (ptQueries == null || StringUtils.isAnyBlank(ptQueries.getCombinedMetricsQuery(), ptQueries.getCollectionMetricsQuery())) {
            log.error("Missing SQL query configurations for module: {}", getModule());
            throw new IllegalArgumentException("SQL queries not configured for module " + getModule());
        }

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(DashboardExtractorConstants.PARAM_START_TIME, startTime)
                .addValue(DashboardExtractorConstants.PARAM_END_TIME, endTime)
                .addValue(DashboardExtractorConstants.PARAM_TENANT_ID, dbTenantId);

        List<RawPtMetric> combinedRowsRaw = queryExecutor.executeQueryWithRetry(ptQueries.getCombinedMetricsQuery(), params, PTRowmapper.COMBINED_ROW_MAPPER, "PtModuleExtractor");
        List<RawPtCollection> collectionRowsRaw = queryExecutor.executeQueryWithRetry(ptQueries.getCollectionMetricsQuery(), params, PTRowmapper.COLLECTION_ROW_MAPPER, "PtModuleExtractor");

        return transformToDTO(combinedRowsRaw, collectionRowsRaw, dateStr);
    }

    @Override
    public boolean isZeroMetrics(Object item) {
        if (item == null) {
            return true;
        }
        if (item instanceof PTDTO p) {
            PTAggregatedData combined = p.getCombinedMetrics();
            if (combined != null) {
                if (isPositive(combined.getAssessments())) return false;
                if (isPositive(combined.getTodaysTotalApplications())) return false;
                if (isPositive(combined.getTodaysClosedApplications())) return false;
                if (isPositive(combined.getNoOfPropertiesPaidToday())) return false;
                if (isPositive(combined.getTodaysApprovedApplications())) return false;
                if (isPositive(combined.getTodaysApprovedApplicationsWithinSLA())) return false;
            }
            List<PTCollectionDTO> collections = p.getCollectionMetrics();
            if (collections != null && !collections.isEmpty()) {
                for (PTCollectionDTO col : collections) {
                    if (col.getTaxHeadAmount() != null && col.getTaxHeadAmount() > 0) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    private boolean isPositive(Number num) {
        return num != null && num.doubleValue() > 0;
    }

    private List<PTDTO> transformToDTO(List<RawPtMetric> combinedRowsRaw, List<RawPtCollection> collectionRowsRaw, String dateStr) {
        Map<String, List<PTCollectionDTO>> collectionsByTenant = groupCollectionsByTenant(collectionRowsRaw);

        List<PTDTO> results = new ArrayList<>();
        for (RawPtMetric row : combinedRowsRaw) {
            results.add(buildPtDto(row, dateStr, collectionsByTenant));
        }
        return results;
    }

    private Map<String, List<PTCollectionDTO>> groupCollectionsByTenant(List<RawPtCollection> collectionRowsRaw) {
        Map<String, List<PTCollectionDTO>> collectionsByTenant = new HashMap<>();
        for (RawPtCollection row : collectionRowsRaw) {
            String currentTenant = row.getTenantid();
            PTCollectionDTO dto = PTCollectionDTO.builder()
                    .usageCategory(row.getUsageCategory())
                    .paymentMode(row.getPaymentMode())
                    .paymentId(row.getPaymentId())
                    .taxHeadCode(row.getTaxHeadCode())
                    .taxHeadAmount(row.getTaxHeadAmount())
                    .build();
            collectionsByTenant.computeIfAbsent(currentTenant, k -> new ArrayList<>()).add(dto);
        }
        return collectionsByTenant;
    }

    private PTDTO buildPtDto(RawPtMetric row, String dateStr, Map<String, List<PTCollectionDTO>> collectionsByTenant) {
        String currentTenantId = row.getTenantid();
        Map<String, String> parsedHierarchy = hierarchyParser.parseTenantId(currentTenantId);

        PTAggregatedData combinedData = PTAggregatedData.builder()
                .assessments(row.getAssessments())
                .todaysTotalApplications(row.getTodaysTotalApplications())
                .todaysClosedApplications(row.getTodaysClosedApplications())
                .noOfPropertiesPaidToday(row.getNoOfPropertiesPaidToday())
                .todaysApprovedApplications(row.getTodaysApprovedApplications())
                .todaysApprovedApplicationsWithinSLA(row.getTodaysApprovedApplicationsWithinSLA())
                .avgDaysForApplicationApproval(row.getAvgDaysForApplicationApproval())
                .propertiesRegisteredJson(row.getPropertiesRegisteredJson())
                .assessedPropertiesJson(row.getAssessedPropertiesJson())
                .movedApplicationsJson(row.getMovedApplicationsJson())
                .build();

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
}
