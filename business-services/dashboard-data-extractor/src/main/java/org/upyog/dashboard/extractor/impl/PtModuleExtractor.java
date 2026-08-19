package org.upyog.dashboard.extractor.impl;

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
import org.springframework.stereotype.Component;
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
		this.dbTenantId = dashboardProperties.getMetricUlb();
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
	 * <p>Extracts PT metrics for {@code targetDate} by executing both the combined
	 * metrics query and the collection metrics query, then building a {@link PTDTO}
	 * per tenant row.
	 *
	 * @param targetDate the date for which PT metrics should be extracted
	 * @return a list of {@link PTDTO} payloads, one per tenant row returned by the query
	 * @throws IllegalStateException if no query mapping is found in schema-mapping.yml for PT
	 */
	@Override
	public List<PTDTO> extractData(LocalDate targetDate) {
		String dateStr = targetDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		long startTime = targetDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
		long endTime = targetDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() - 1;

		Map<String, Object> params = Map.of("startTime", startTime, "endTime", endTime, "tenantId", dbTenantId);

		SchemaMappingConfig.ModuleQueries ptQueries = schemaMappingConfig.getQueriesForModule(Module.PT);
		if (ptQueries == null) {
			throw new IllegalStateException("No query mapping found in schema-mapping.yml for module PT");
		}

		List<Map<String, Object>> combinedResults = executeQueryWithRetry(ptQueries.getCombinedMetricsQuery(), params);
		List<Map<String, Object>> collectionRowsRaw = executeQueryWithRetry(ptQueries.getCollectionMetricsQuery(), params);
		Map<String, List<PTCollectionDTO>> collectionsByTenant = groupCollectionsByTenant(collectionRowsRaw);

		List<PTDTO> results = new ArrayList<>();
		for (Map<String, Object> row : combinedResults) {
			results.add(buildPtDto(row, dateStr, collectionsByTenant));
		}
		return results;
	}

	/**
	 * Groups collection-metrics rows by tenant ID, converting each raw row into a
	 * {@link PTCollectionDTO}.
	 *
	 * @param collectionRowsRaw the raw rows returned by the collection metrics query
	 * @return a map from tenant ID to the list of {@link PTCollectionDTO} records for that tenant
	 */
	private Map<String, List<PTCollectionDTO>> groupCollectionsByTenant(List<Map<String, Object>> collectionRowsRaw) {
		Map<String, List<PTCollectionDTO>> collectionsByTenant = new HashMap<>();
		for (Map<String, Object> row : collectionRowsRaw) {
			String currentTenant = (String) row.get("tenantid");
			PTCollectionDTO dto = PTCollectionDTO.builder()
					.usageCategory((String) row.get(PTDatabaseConstants.USAGE_CATEGORY))
					.paymentMode((String) row.get(PTDatabaseConstants.PAYMENT_MODE))
					.paymentId((String) row.get(PTDatabaseConstants.PAYMENT_ID))
					.taxHeadCode((String) row.get(PTDatabaseConstants.TAX_HEAD_CODE))
					.taxHeadAmount(getNullableDouble(row, PTDatabaseConstants.TAX_HEAD_AMOUNT))
					.build();
			collectionsByTenant.computeIfAbsent(currentTenant, k -> new ArrayList<>()).add(dto);
		}
		return collectionsByTenant;
	}

	/**
	 * Builds a {@link PTDTO} from a raw combined-metrics result-set row together with
	 * the pre-grouped collection data for the same tenant.
	 *
	 * @param row                 a single row from the combined metrics query, keyed by column alias
	 * @param dateStr             the formatted date string (dd-MM-yyyy) for the target date
	 * @param collectionsByTenant a map from tenant ID to its collection records
	 * @return a fully populated {@link PTDTO}
	 */
	private PTDTO buildPtDto(Map<String, Object> row, String dateStr, Map<String, List<PTCollectionDTO>> collectionsByTenant) {
		String currentTenantId = (String) row.get("tenantid");
		Map<String, String> parsedHierarchy = hierarchyParser.parseTenantId(currentTenantId);

		PTAggregatedData combinedData = PTAggregatedData.builder()
				.assessments(getNullableInt(row, PTDatabaseConstants.ASSESSMENTS))
				.todaysTotalApplications(getNullableInt(row, PTDatabaseConstants.TODAYS_TOTAL_APPLICATIONS))
				.todaysClosedApplications(getNullableInt(row, PTDatabaseConstants.TODAYS_CLOSED_APPLICATIONS))
				.noOfPropertiesPaidToday(getNullableInt(row, PTDatabaseConstants.NO_OF_PROPERTIES_PAID_TODAY))
				.todaysApprovedApplications(getNullableInt(row, PTDatabaseConstants.TODAYS_APPROVED_APPLICATIONS))
				.todaysApprovedApplicationsWithinSLA(getNullableInt(row, PTDatabaseConstants.TODAYS_APPROVED_APPLICATIONS_WITHIN_SLA))
				.avgDaysForApplicationApproval(getNullableInt(row, PTDatabaseConstants.AVG_DAYS_FOR_APPLICATION_APPROVAL))
				.propertiesRegisteredJson(getStringValue(row.get(PTDatabaseConstants.PROPERTIES_REGISTERED_JSON)))
				.assessedPropertiesJson(getStringValue(row.get(PTDatabaseConstants.ASSESSED_PROPERTIES_JSON)))
				.movedApplicationsJson(getStringValue(row.get(PTDatabaseConstants.MOVED_APPLICATIONS_JSON)))
				.build();

		List<PTCollectionDTO> tenantCollections = collectionsByTenant.getOrDefault(currentTenantId, List.of());

		return PTDTO.builder()
				.date(dateStr)
				.module(getModule().name())
				.ward(parsedHierarchy.get("ward"))
				.ulb(parsedHierarchy.get("ulb"))
				.region(parsedHierarchy.get("region"))
				.state(parsedHierarchy.get("state"))
				.combinedMetrics(combinedData)
				.collectionMetrics(tenantCollections)
				.build();
	}

	/**
	 * Extracts a {@code Double} value from a result-set row map, returning {@code null}
	 * when the column value is absent or not a {@link Number}.
	 *
	 * @param row    the row map from the JDBC query result
	 * @param column the column alias key
	 * @return the double value, or {@code null} if the column is missing or SQL {@code NULL}
	 */
	private Double getNullableDouble(Map<String, Object> row, String column) {
		Object value = row.get(column);
		if (value instanceof Number number) {
			return number.doubleValue();
		}
		return null;
	}

	/**
	 * Extracts an {@code Integer} value from a result-set row map, returning {@code null}
	 * when the column value is absent or not a {@link Number}.
	 *
	 * @param row    the row map from the JDBC query result
	 * @param column the column alias key
	 * @return the integer value, or {@code null} if the column is missing or SQL {@code NULL}
	 */
	private Integer getNullableInt(Map<String, Object> row, String column) {
		Object value = row.get(column);
		if (value instanceof Number number) {
			return number.intValue();
		}
		return null;
	}

	/**
	 * Converts the given object to its {@link Object#toString()} representation,
	 * returning {@code null} for {@code null} input.
	 *
	 * @param obj the object to convert; may be {@code null}
	 * @return the string representation, or {@code null}
	 */
	private String getStringValue(Object obj) {
		if (obj == null) {
			return null;
		}
		return obj.toString();
	}

	/**
	 * Executes the given named-parameter SQL query against the PT data source with
	 * exponential backoff retry logic. Throws the underlying exception when the maximum
	 * number of attempts is exhausted.
	 *
	 * @param query  the named-parameter SQL query string
	 * @param params the named parameters to bind
	 * @return the query result as a list of row maps
	 */
	private List<Map<String, Object>> executeQueryWithRetry(String query, Map<String, Object> params) {
		int attempt = 0;
		while (true) {
			attempt++;
			try {
				return namedParameterJdbcTemplate.queryForList(query, params);
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
