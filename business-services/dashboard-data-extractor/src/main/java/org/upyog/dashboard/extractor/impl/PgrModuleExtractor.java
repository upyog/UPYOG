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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;
import org.upyog.dashboard.pgr.model.RawPgrMetric;
import org.upyog.dashboard.pgr.mapper.PGRRowMapper;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.config.SchemaMappingConfig;
import org.upyog.dashboard.extractor.ModuleExtractor;
import org.upyog.dashboard.model.DashboardData;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Public Grievance Redressal (PGR) implementation of {@link ModuleExtractor}.
 * 
 * <p>Encapsulates database metric extraction queries for the PGR module.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PgrModuleExtractor implements ModuleExtractor<List<DashboardData>> {

	private static final String GROUP_BY = "groupBy";
	private static final String GROUP_BY_DEPARTMENT = "department";
	private static final String BUCKETS = "buckets";

	private final DatabaseQueryExecutor queryExecutor;
	private final SchemaMappingConfig schemaMappingConfig;
	private final ObjectMapper objectMapper;
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
		return Module.PGR;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Extracts PGR metrics for {@code targetDate} by executing the combined metrics query
	 * and mapping each row to a {@link org.upyog.dashboard.model.DashboardData}.
	 *
	 * @param targetDate the date for which PGR metrics should be extracted
	 * @return a list of {@link org.upyog.dashboard.model.DashboardData} payloads, one per tenant row;
	 *         returns an empty list when no query config is found or a database error occurs
	 */
	@Override
	public List<DashboardData> extractData(LocalDate targetDate) {
		String dateStr = targetDate.format(DateTimeFormatter.ofPattern(DashboardExtractorConstants.DATE_FORMAT));
		long startTime = targetDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
		long endTime = targetDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() - 1;

		MapSqlParameterSource params = new MapSqlParameterSource()
				.addValue(DashboardExtractorConstants.PARAM_START_TIME, startTime)
				.addValue(DashboardExtractorConstants.PARAM_END_TIME, endTime)
				.addValue(DashboardExtractorConstants.PARAM_TENANT_ID, dbTenantId);
		List<DashboardData> results = new ArrayList<>();

		SchemaMappingConfig.ModuleQueries pgrQueries = schemaMappingConfig.getQueriesForModule(Module.PGR);
		if (pgrQueries == null || pgrQueries.getCombinedMetricsQuery() == null) {
			return results;
		}

		try {
			List<RawPgrMetric> combinedResults = queryExecutor.executeQueryWithRetry(pgrQueries.getCombinedMetricsQuery(), params, PGRRowMapper.COMBINED_ROW_MAPPER, "PgrModuleExtractor");
			for (RawPgrMetric combinedResult : combinedResults) {
				results.add(buildDashboardData(combinedResult, dateStr));
			}
		} catch (Exception exception) {
			log.warn("Exception during DB metrics extraction, utilizing empty defaults: {}", exception.getMessage());
		}

		return results;
	}

	/**
	 * Builds a {@link org.upyog.dashboard.model.DashboardData} from a raw combined-metrics result-set row.
	 *
	 * @param combinedResult a single row returned by the combined metrics query, keyed by column alias
	 * @param dateStr        the formatted date string (dd-MM-yyyy) for the extraction target date
	 * @return a fully populated {@link org.upyog.dashboard.model.DashboardData}
	 */
	private DashboardData buildDashboardData(RawPgrMetric combinedResult, String dateStr) {
		String currentTenantId = combinedResult.getTenantid();
		Map<String, String> parsedHierarchy = hierarchyParser.parseTenantId(currentTenantId);

		return DashboardData.builder()
				.date(dateStr)
				.module(getModule().name())
				.ward(parsedHierarchy.get(DashboardExtractorConstants.KEY_WARD))
				.ulb(parsedHierarchy.get(DashboardExtractorConstants.KEY_ULB))
				.region(parsedHierarchy.get(DashboardExtractorConstants.KEY_REGION))
				.state(parsedHierarchy.get(DashboardExtractorConstants.KEY_STATE))
				.metrics(buildMetrics(combinedResult))
				.build();
	}

	/**
	 * Constructs the {@code metrics} map for a single tenant row by reading the JSON columns
	 * returned by the combined metrics query and converting them to structured bucket lists.
	 *
	 * @param combinedResult a single row returned by the combined metrics query
	 * @return a {@link java.util.LinkedHashMap} of metric names to their structured values
	 */
	private Map<String, Object> buildMetrics(RawPgrMetric combinedResult) {
		Map<String, Object> metrics = new LinkedHashMap<>();
		metrics.put("slaAchievement", List.of(Map.of(GROUP_BY, GROUP_BY_DEPARTMENT, BUCKETS,
				parseJsonBuckets(combinedResult.getSlaachievementjson()))));
		metrics.put("completionRate", List.of(Map.of(GROUP_BY, GROUP_BY_DEPARTMENT, BUCKETS,
				parseJsonBuckets(combinedResult.getCompletionratejson()))));
		metrics.put("uniqueCitizens", combinedResult.getUniquecitizens() != null ? combinedResult.getUniquecitizens() : 0);
		metrics.put("todaysComplaints", List.of(
				Map.of(GROUP_BY, "status", BUCKETS, parseJsonBuckets(combinedResult.getComplaintsbystatusjson())),
				Map.of(GROUP_BY, "channel", BUCKETS, parseJsonBuckets(combinedResult.getComplaintsbychanneljson())),
				Map.of(GROUP_BY, GROUP_BY_DEPARTMENT, BUCKETS, parseJsonBuckets(combinedResult.getComplaintsbydepartmentjson())),
				Map.of(GROUP_BY, "category", BUCKETS, parseJsonBuckets(combinedResult.getComplaintsbycategoryjson()))
		));
		metrics.put("todaysReopenedComplaints", List.of(Map.of(GROUP_BY, GROUP_BY_DEPARTMENT, BUCKETS,
				parseJsonBuckets(combinedResult.getTodaysreopenedcomplaintsjson()))));
		metrics.put("todaysOpenComplaints", List.of(Map.of(GROUP_BY, GROUP_BY_DEPARTMENT, BUCKETS,
				parseJsonBuckets(combinedResult.getTodaysopencomplaintsjson()))));
		metrics.put("todaysAssignedComplaints", List.of(Map.of(GROUP_BY, GROUP_BY_DEPARTMENT, BUCKETS,
				parseJsonBuckets(combinedResult.getTodaysassignedcomplaintsjson()))));
		metrics.put("averageSolutionTime", List.of(Map.of(GROUP_BY, GROUP_BY_DEPARTMENT, BUCKETS,
				parseJsonBuckets(combinedResult.getAveragesolutiontimejson()))));
		metrics.put("todaysRejectedComplaints", List.of(Map.of(GROUP_BY, GROUP_BY_DEPARTMENT, BUCKETS,
				parseJsonBuckets(combinedResult.getTodaysrejectedcomplaintsjson()))));
		metrics.put("todaysReassignedComplaints", List.of(Map.of(GROUP_BY, GROUP_BY_DEPARTMENT, BUCKETS,
				parseJsonBuckets(combinedResult.getTodaysreassignedcomplaintsjson()))));
		metrics.put("todaysReassignRequestedComplaints", List.of(Map.of(GROUP_BY, GROUP_BY_DEPARTMENT, BUCKETS,
				parseJsonBuckets(combinedResult.getTodaysreassignrequestedcomplaintsjson()))));
		metrics.put("todaysClosedComplaints", List.of(Map.of(GROUP_BY, GROUP_BY_DEPARTMENT, BUCKETS,
				parseJsonBuckets(combinedResult.getTodaysclosedcomplaintsjson()))));
		metrics.put("todaysResolvedComplaints", List.of(Map.of(GROUP_BY, GROUP_BY_DEPARTMENT, BUCKETS,
				parseJsonBuckets(combinedResult.getTodaysresolvedcomplaintsjson()))));
		return metrics;
	}

	/**
	 * Deserialises a JSON array string into a list of bucket maps. Returns an empty list
	 * when the input is {@code null}, blank, or equals {@code "[]"}.
	 *
	 * @param object the raw value from the query result row; expected to be a JSON array string
	 * @return a list of {@code Map<String, Object>} bucket entries, or an empty list on failure
	 */
	private List<Map<String, Object>> parseJsonBuckets(Object object) {
		if (object == null) {
			return List.of();
		}
		String jsonStr = object.toString();
		if (StringUtils.isBlank(jsonStr) || "[]".equals(jsonStr)) {
			return List.of();
		}
		try {
			return objectMapper.readValue(jsonStr, new TypeReference<List<Map<String, Object>>>() {});
		} catch (Exception exception) {
			log.debug("Failed to parse JSON buckets: {}", exception.getMessage());
			return List.of();
		}
	}
}
