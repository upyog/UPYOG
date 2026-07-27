package org.upyog.adapter.extractor.impl;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.upyog.adapter.common.constants.Module;
import org.upyog.adapter.config.SchemaMappingConfig;
import org.upyog.adapter.extractor.ModuleExtractor;
import org.upyog.adapter.model.DashboardData;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Public Grievance Redressal (PGR) implementation of {@link ModuleExtractor}.
 * 
 * <p>Encapsulates database metric extraction queries for the PGR module.
 */
@Slf4j
@Component
public class PgrModuleExtractor implements ModuleExtractor {

	@Autowired(required = false)
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private SchemaMappingConfig schemaMappingConfig;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private org.upyog.adapter.config.AdapterProperties adapterProperties;

	private String ulb;
	private String ward;
	private String region;
	private String state;
	private String dbTenantId;
	private int dbMaxAttempts;
	private long dbBaseDelayMs;
	private long dbMaxDelayMs;

	@jakarta.annotation.PostConstruct
	public void init() {
		this.ulb = adapterProperties.getMetricUlb();
		this.ward = adapterProperties.getMetricWard();
		this.region = adapterProperties.getMetricRegion();
		this.state = adapterProperties.getMetricState();
		this.dbTenantId = adapterProperties.getMetricUlb();
		this.dbMaxAttempts = adapterProperties.getDbMaxAttempts();
		this.dbBaseDelayMs = adapterProperties.getDbBaseDelayMs();
		this.dbMaxDelayMs = adapterProperties.getDbMaxDelayMs();
	}

	@Override
	public Module getModule() {
		return Module.PGR;
	}

	@Override
	public DashboardData extractData(LocalDate targetDate) {
		String dateStr = targetDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		long startTime = targetDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
		long endTime = targetDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() - 1;
		String tenantId = dbTenantId;

		Map<String, Object> params = Map.of("startTime", startTime, "endTime", endTime, "tenantId", tenantId);

		Map<String, Object> metrics = new LinkedHashMap<>();

		int uniqueCitizens = 0;
		List<Map<String, Object>> slaAchievementBuckets = new ArrayList<>();
		List<Map<String, Object>> completionRateBuckets = new ArrayList<>();
		List<Map<String, Object>> statusBuckets = new ArrayList<>();
		List<Map<String, Object>> channelBuckets = new ArrayList<>();
		List<Map<String, Object>> departmentBuckets = new ArrayList<>();
		List<Map<String, Object>> categoryBuckets = new ArrayList<>();
		List<Map<String, Object>> reopenedBuckets = new ArrayList<>();
		List<Map<String, Object>> openBuckets = new ArrayList<>();
		List<Map<String, Object>> assignedBuckets = new ArrayList<>();
		List<Map<String, Object>> avgSolutionBuckets = new ArrayList<>();
		List<Map<String, Object>> rejectedBuckets = new ArrayList<>();
		List<Map<String, Object>> reassignedBuckets = new ArrayList<>();
		List<Map<String, Object>> reassignRequestedBuckets = new ArrayList<>();
		List<Map<String, Object>> closedBuckets = new ArrayList<>();
		List<Map<String, Object>> resolvedBuckets = new ArrayList<>();

		SchemaMappingConfig.ModuleQueries pgrQueries = schemaMappingConfig.getQueriesForModule(Module.PGR);
		if (pgrQueries != null && pgrQueries.getCombinedMetricsQuery() != null && namedParameterJdbcTemplate != null) {
			try {
				Map<String, Object> combinedResult = executeQueryWithRetry(pgrQueries.getCombinedMetricsQuery(), params);
				uniqueCitizens = getIntegerValue(combinedResult.get("uniquecitizens"));
				slaAchievementBuckets = parseJsonBuckets(combinedResult.get("slaachievementjson"));
				completionRateBuckets = parseJsonBuckets(combinedResult.get("completionratejson"));
				statusBuckets = parseJsonBuckets(combinedResult.get("complaintsbystatusjson"));
				channelBuckets = parseJsonBuckets(combinedResult.get("complaintsbychanneljson"));
				departmentBuckets = parseJsonBuckets(combinedResult.get("complaintsbydepartmentjson"));
				categoryBuckets = parseJsonBuckets(combinedResult.get("complaintsbycategoryjson"));
				reopenedBuckets = parseJsonBuckets(combinedResult.get("todaysreopenedcomplaintsjson"));
				openBuckets = parseJsonBuckets(combinedResult.get("todaysopencomplaintsjson"));
				assignedBuckets = parseJsonBuckets(combinedResult.get("todaysassignedcomplaintsjson"));
				avgSolutionBuckets = parseJsonBuckets(combinedResult.get("averagesolutiontimejson"));
				rejectedBuckets = parseJsonBuckets(combinedResult.get("todaysrejectedcomplaintsjson"));
				reassignedBuckets = parseJsonBuckets(combinedResult.get("todaysreassignedcomplaintsjson"));
				reassignRequestedBuckets = parseJsonBuckets(combinedResult.get("todaysreassignrequestedcomplaintsjson"));
				closedBuckets = parseJsonBuckets(combinedResult.get("todaysclosedcomplaintsjson"));
				resolvedBuckets = parseJsonBuckets(combinedResult.get("todaysresolvedcomplaintsjson"));
			} catch (Exception exception) {
				log.warn("PgrModuleExtractor | Exception during DB metrics extraction, utilizing empty defaults: {}", exception.getMessage());
			}
		}

		// Fill metrics exactly matching sample JSON schema
		metrics.put("slaAchievement", List.of(Map.of("groupBy", "department", "buckets", slaAchievementBuckets)));
		metrics.put("completionRate", List.of(Map.of("groupBy", "department", "buckets", completionRateBuckets)));
		metrics.put("uniqueCitizens", uniqueCitizens);

		metrics.put("todaysComplaints", List.of(
				Map.of("groupBy", "status", "buckets", statusBuckets),
				Map.of("groupBy", "channel", "buckets", channelBuckets),
				Map.of("groupBy", "department", "buckets", departmentBuckets),
				Map.of("groupBy", "category", "buckets", categoryBuckets)
		));

		metrics.put("todaysReopenedComplaints", List.of(Map.of("groupBy", "department", "buckets", reopenedBuckets)));
		metrics.put("todaysOpenComplaints", List.of(Map.of("groupBy", "department", "buckets", openBuckets)));
		metrics.put("todaysAssignedComplaints", List.of(Map.of("groupBy", "department", "buckets", assignedBuckets)));
		metrics.put("averageSolutionTime", List.of(Map.of("groupBy", "department", "buckets", avgSolutionBuckets)));
		metrics.put("todaysRejectedComplaints", List.of(Map.of("groupBy", "department", "buckets", rejectedBuckets)));
		metrics.put("todaysReassignedComplaints", List.of(Map.of("groupBy", "department", "buckets", reassignedBuckets)));
		metrics.put("todaysReassignRequestedComplaints", List.of(Map.of("groupBy", "department", "buckets", reassignRequestedBuckets)));
		metrics.put("todaysClosedComplaints", List.of(Map.of("groupBy", "department", "buckets", closedBuckets)));
		metrics.put("todaysResolvedComplaints", List.of(Map.of("groupBy", "department", "buckets", resolvedBuckets)));

		return DashboardData.builder()
				.date(dateStr)
				.module(getModule().name())
				.ward(ward)
				.ulb(ulb)
				.region(region)
				.state(state)
				.metrics(metrics)
				.build();
	}

	private Integer getIntegerValue(Object object) {
		if (object instanceof Number) {
			return ((Number) object).intValue();
		}
		return 0;
	}

	private List<Map<String, Object>> parseJsonBuckets(Object object) {
		if (object == null) return List.of();
		String jsonStr = object.toString();
		if (jsonStr.isBlank() || "[]".equals(jsonStr)) return List.of();
		try {
			return objectMapper.readValue(jsonStr, new TypeReference<List<Map<String, Object>>>() {});
		} catch (Exception exception) {
			return List.of();
		}
	}

	private Map<String, Object> executeQueryWithRetry(String query, Map<String, Object> params) {
		int attempt = 0;
		while (true) {
			attempt++;
			try {
				return namedParameterJdbcTemplate.queryForMap(query, params);
			} catch (Exception exception) {
				if (attempt >= dbMaxAttempts) {
					log.error("PgrModuleExtractor | DB query failed after {} attempts.", attempt, exception);
					throw exception;
				}
				long backoff = calculateDbBackoffWithJitter(attempt);
				log.warn("PgrModuleExtractor | DB query failed (attempt {}/{}). Retrying in {} ms. Error: {}", 
						attempt, dbMaxAttempts, backoff, exception.getMessage());
				try {
					Thread.sleep(backoff);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
					throw new RuntimeException("DB query retry interrupted", ie);
				}
			}
		}
	}

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
