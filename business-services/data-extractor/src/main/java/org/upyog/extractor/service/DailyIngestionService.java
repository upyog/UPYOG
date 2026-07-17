package org.upyog.extractor.service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.upyog.adapter.api.AdapterClient;
import org.upyog.adapter.model.AdapterRequest;
import org.upyog.adapter.model.DashboardData;
import org.upyog.adapter.model.IngestionResult;
import org.upyog.extractor.repository.PTQueryRegistry;

/**
 * Service class that manages Property Tax (PT) daily metrics extraction and ingestion.
 * 
 * <p>Uses {@link NamedParameterJdbcTemplate} to execute combined, optimized SQL queries
 * from {@link PTQueryRegistry} and invokes {@link AdapterClient} to execute the national dashboard pipeline.
 */
/**
 * Class representing the DailyIngestionService class.
 * 
 * <p>Contributes to the core Property Tax metrics ingestion pipeline.
 */
@Service
public class DailyIngestionService {

	@Autowired
	private AdapterClient adapterClient;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	/**
	 * Fetches raw PT data, runs it through the transformer pipeline, and pushes it
	 * to the ingestion target.
	 * 
	 * @return IngestionResult representing success or failure status and response payload
	 */
	public IngestionResult ingestDailyPTData() {
		DashboardData rawPTData = fetchPTDataFromDatabase();

		try {
			AdapterRequest adapterRequest = AdapterRequest.builder()
					.module(org.upyog.adapter.common.constants.Module.PT).rawData(List.of(rawPTData)).build();

			IngestionResult result = adapterClient.execute(adapterRequest);

			System.out.println("Data ingestion executed successfully. Result payload created: " + (result != null));
			return result;
		} catch (Exception e) {
			System.err.println("Failed to ingest PT data: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Simulates fetching metrics from the database and building a DashboardData
	 * instance.
	 */
	private DashboardData fetchPTDataFromDatabase() {
		// Real logic: return fetchPTDataFromDatabase(LocalDate.now().minusDays(1));
		// Temporarily querying active test date 30-06-2026 to fetch rich metrics
		return fetchPTDataFromDatabase(LocalDate.of(2026, 6, 30));
	}

	/**
	 * Queries the database for all 16 metrics for the specified target date.
	 * 
	 * @param targetDate the date to extract metrics for
	 * @return a populated DashboardData instance containing PT metrics
	 */
	public DashboardData fetchPTDataFromDatabase(LocalDate targetDate) {
		String dateStr = targetDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		long startTime = targetDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
		long endTime = targetDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() - 1;
		String tenantId = "pg.citya";

		Map<String, Object> params = Map.of(
				"startTime", startTime,
				"endTime", endTime,
				"tenantId", tenantId
		);

		Map<String, Object> metrics = new LinkedHashMap<>();

		// 1. Fetch all scalar metrics in a single query
		Map<String, Object> scalarResult = namedParameterJdbcTemplate.queryForMap(PTQueryRegistry.SCALAR_METRICS_QUERY, params);
		
		metrics.put("assessments", getIntegerValue(scalarResult.get("assessments")));
		metrics.put("todaysTotalApplications", getIntegerValue(scalarResult.get("todaystotalapplications")));
		metrics.put("todaysClosedApplications", getIntegerValue(scalarResult.get("todaysclosedapplications")));
		metrics.put("noOfPropertiesPaidToday", getIntegerValue(scalarResult.get("noofpropertiespaidtoday")));
		metrics.put("todaysApprovedApplications", getIntegerValue(scalarResult.get("todaysapprovedapplications")));
		metrics.put("todaysApprovedApplicationsWithinSLA", getIntegerValue(scalarResult.get("todaysapprovedapplicationswithinsla")));
		
		Number avgDays = (Number) scalarResult.get("avgdaysforapplicationapproval");
		metrics.put("avgDaysForApplicationApproval", avgDays != null ? avgDays.intValue() : 0);
		metrics.put("pendingApplicationsBeyondTimeline", getIntegerValue(scalarResult.get("pendingapplicationsbeyondtimeline")));

		// 2. propertiesRegistered (historical registered by financial year)
		metrics.put("propertiesRegistered", List.of(Map.of(
				"groupBy", "financialYear",
				"buckets", queryBuckets(PTQueryRegistry.PROPERTIES_REGISTERED_QUERY, params)
		)));

		// 3. assessedProperties (assessed today)
		metrics.put("assessedProperties", List.of(Map.of(
				"groupBy", "usageCategory",
				"buckets", queryBucketsWithDefaults(PTQueryRegistry.ASSESSED_PROPERTIES_QUERY, new String[]{"RESIDENTIAL", "COMMERCIAL", "INDUSTRIAL"}, params)
		)));

		// 4. todaysMovedApplications
		metrics.put("todaysMovedApplications", List.of(Map.of(
				"groupBy", "applicationStatus",
				"buckets", queryBuckets(PTQueryRegistry.MOVED_APPLICATIONS_QUERY, params)
		)));

		// 5. Fetch all daily collection & transaction metrics in a single query and aggregate in Java memory
		List<Map<String, Object>> collectionRows = namedParameterJdbcTemplate.queryForList(PTQueryRegistry.COLLECTION_METRICS_QUERY, params);

		Map<String, Double> propTaxMap = new HashMap<>();
		Map<String, Double> cessMap = new HashMap<>();
		Map<String, Double> rebateMap = new HashMap<>();
		Map<String, Double> penaltyMap = new HashMap<>();
		Map<String, Double> interestMap = new HashMap<>();
		Map<String, Double> usageCollectionMap = new HashMap<>();
		Map<String, Double> channelCollectionMap = new HashMap<>();
		Map<String, Set<String>> usageTxnMap = new HashMap<>();

		for (Map<String, Object> row : collectionRows) {
			String usage = (String) row.get("usage_category");
			String mode = (String) row.get("paymentmode");
			String paymentId = (String) row.get("payment_id");
			String taxHead = (String) row.get("taxheadcode");
			Number amtNum = (Number) row.get("tax_head_amount");
			double amount = amtNum != null ? amtNum.doubleValue() : 0.0;

			propTaxMap.putIfAbsent(usage, 0.0);
			cessMap.putIfAbsent(usage, 0.0);
			rebateMap.putIfAbsent(usage, 0.0);
			penaltyMap.putIfAbsent(usage, 0.0);
			interestMap.putIfAbsent(usage, 0.0);
			usageCollectionMap.putIfAbsent(usage, 0.0);
			usageTxnMap.putIfAbsent(usage, new HashSet<>());

			if (paymentId != null) {
				usageTxnMap.get(usage).add(paymentId);
			}

			usageCollectionMap.put(usage, usageCollectionMap.get(usage) + amount);

			String channel = ("ONLINE".equalsIgnoreCase(mode) || "CARD".equalsIgnoreCase(mode)) ? "Digital" : "Non Digital";
			channelCollectionMap.put(channel, channelCollectionMap.getOrDefault(channel, 0.0) + amount);

			if ("PT_TAX".equals(taxHead)) {
				propTaxMap.put(usage, propTaxMap.get(usage) + amount);
			} else if ("PT_FIRE_CESS".equals(taxHead) || "PT_CANCER_CESS".equals(taxHead)) {
				cessMap.put(usage, cessMap.get(usage) + amount);
			} else if ("PT_TIME_REBATE".equals(taxHead) || "PT_ADHOC_REBATE".equals(taxHead)) {
				rebateMap.put(usage, rebateMap.get(usage) + amount);
			} else if ("PT_TIME_PENALTY".equals(taxHead) || "PT_ADHOC_PENALTY".equals(taxHead)) {
				penaltyMap.put(usage, penaltyMap.get(usage) + amount);
			} else if ("PT_TIME_INTEREST".equals(taxHead)) {
				interestMap.put(usage, interestMap.get(usage) + amount);
			}
		}

		// Convert transaction sets to counts
		Map<String, Integer> usageTxnCountMap = new HashMap<>();
		for (Map.Entry<String, Set<String>> entry : usageTxnMap.entrySet()) {
			usageTxnCountMap.put(entry.getKey(), entry.getValue().size());
		}

		String[] categories = new String[]{"RESIDENTIAL", "COMMERCIAL", "INDUSTRIAL"};

		metrics.put("transactions", List.of(Map.of(
				"groupBy", "usageCategory",
				"buckets", formatBucketsWithDefaults(usageTxnCountMap, categories)
		)));

		metrics.put("todaysCollection", List.of(
				Map.of("groupBy", "usageCategory", "buckets", formatBucketsWithDefaults(usageCollectionMap, categories)),
				Map.of("groupBy", "paymentChannelType", "buckets", formatBucketsWithDefaults(channelCollectionMap, new String[]{"Digital", "Non Digital"}))
		));

		metrics.put("propertyTax", List.of(Map.of("groupBy", "usageCategory", "buckets", formatBucketsWithDefaults(propTaxMap, categories))));
		metrics.put("cess", List.of(Map.of("groupBy", "usageCategory", "buckets", formatBucketsWithDefaults(cessMap, categories))));
		metrics.put("rebate", List.of(Map.of("groupBy", "usageCategory", "buckets", formatBucketsWithDefaults(rebateMap, categories))));
		metrics.put("penalty", List.of(Map.of("groupBy", "usageCategory", "buckets", formatBucketsWithDefaults(penaltyMap, categories))));
		metrics.put("interest", List.of(Map.of("groupBy", "usageCategory", "buckets", formatBucketsWithDefaults(interestMap, categories))));

		return DashboardData.builder()
				.date(dateStr)
				.module("PT")
				.ward("Block 4")
				.ulb("pg.citya")
				.region("Test")
				.state("PG")
				.metrics(metrics)
				.build();
	}

	private Integer getIntegerValue(Object obj) {
		if (obj instanceof Number) {
			return ((Number) obj).intValue();
		}
		return 0;
	}

	private List<Map<String, Object>> queryBuckets(String sql, Map<String, ?> params) {
		List<Map<String, Object>> rows = namedParameterJdbcTemplate.queryForList(sql, params);
		List<Map<String, Object>> buckets = new ArrayList<>();
		for (Map<String, Object> row : rows) {
			String name = (String) row.get("name");
			Number value = (Number) row.get("value");
			buckets.add(Map.of("name", name != null ? name : "", "value", value != null ? value : 0));
		}
		return buckets;
	}

	private List<Map<String, Object>> queryBucketsWithDefaults(String sql, String[] expectedNames, Map<String, ?> params) {
		List<Map<String, Object>> rows = namedParameterJdbcTemplate.queryForList(sql, params);
		Map<String, Object> bucketMap = new LinkedHashMap<>();
		for (String name : expectedNames) {
			bucketMap.put(name, 0);
		}
		for (Map<String, Object> row : rows) {
			String name = (String) row.get("name");
			Number value = (Number) row.get("value");
			if (name != null && value != null) {
				bucketMap.put(name, value);
			}
		}
		List<Map<String, Object>> buckets = new ArrayList<>();
		for (Map.Entry<String, Object> entry : bucketMap.entrySet()) {
			buckets.add(Map.of("name", entry.getKey(), "value", entry.getValue()));
		}
		return buckets;
	}

	private List<Map<String, Object>> formatBucketsWithDefaults(Map<String, ? extends Number> dataMap, String[] expectedNames) {
		List<Map<String, Object>> buckets = new ArrayList<>();
		for (String name : expectedNames) {
			Number num = dataMap.get(name);
			double val = num != null ? num.doubleValue() : 0.0;
			buckets.add(Map.of("name", name, "value", (val == (long) val) ? (long) val : val));
		}
		for (Map.Entry<String, ? extends Number> entry : dataMap.entrySet()) {
			if (!java.util.Arrays.asList(expectedNames).contains(entry.getKey())) {
				double val = entry.getValue().doubleValue();
				buckets.add(Map.of("name", entry.getKey(), "value", (val == (long) val) ? (long) val : val));
			}
		}
		return buckets;
	}
}