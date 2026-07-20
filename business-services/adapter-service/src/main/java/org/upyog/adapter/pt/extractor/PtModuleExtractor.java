package org.upyog.adapter.pt.extractor;

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
import org.springframework.stereotype.Component;
import org.upyog.adapter.common.constants.Module;
import org.upyog.adapter.config.SchemaMappingConfig;
import org.upyog.adapter.extractor.ModuleExtractor;
import org.upyog.adapter.model.DashboardData;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Property Tax (PT) implementation of {@link ModuleExtractor}.
 * 
 * <p>
 * Encapsulates database metric extraction queries for PT module using
 * {@link SchemaMappingConfig}.
 */
@Component
public class PtModuleExtractor implements ModuleExtractor {

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private SchemaMappingConfig schemaMappingConfig;

	@Autowired
	private ObjectMapper objectMapper;

	@org.springframework.beans.factory.annotation.Value("${adapter.metric.ulb:pg.citya}")
	private String ulb;

	@org.springframework.beans.factory.annotation.Value("${adapter.metric.ward:Block 4}")
	private String ward;

	@org.springframework.beans.factory.annotation.Value("${adapter.metric.region:Test}")
	private String region;

	@org.springframework.beans.factory.annotation.Value("${adapter.metric.state:Punjab}")
	private String state;

	@org.springframework.beans.factory.annotation.Value("${adapter.metric.ulb:pg.citya}")
	private String dbTenantId;

	@Override
	public Module getModule() {
		return Module.PT;
	}

	@Override
	public DashboardData extractData(LocalDate targetDate) {
		String dateStr = targetDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		long startTime = targetDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
		long endTime = targetDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() - 1;
		String tenantId = dbTenantId;

		Map<String, Object> params = Map.of("startTime", startTime, "endTime", endTime, "tenantId", tenantId);

		SchemaMappingConfig.ModuleQueries ptQueries = schemaMappingConfig.getQueriesForModule(Module.PT);
		if (ptQueries == null) {
			throw new IllegalStateException("No query mapping found in schema-mapping.yml for module PT");
		}

		Map<String, Object> metrics = new LinkedHashMap<>();

		// DB CALL 1: Combined scalars + JSON arrays query
		Map<String, Object> combinedResult = namedParameterJdbcTemplate.queryForMap(ptQueries.getCombinedMetricsQuery(),
				params);

		metrics.put("assessments", getIntegerValue(combinedResult.get("assessments")));
		metrics.put("todaysTotalApplications", getIntegerValue(combinedResult.get("todaystotalapplications")));
		metrics.put("todaysClosedApplications", getIntegerValue(combinedResult.get("todaysclosedapplications")));
		metrics.put("noOfPropertiesPaidToday", getIntegerValue(combinedResult.get("noofpropertiespaidtoday")));
		metrics.put("todaysApprovedApplications", getIntegerValue(combinedResult.get("todaysapprovedapplications")));
		metrics.put("todaysApprovedApplicationsWithinSLA",
				getIntegerValue(combinedResult.get("todaysapprovedapplicationswithinsla")));

		Number avgDays = (Number) combinedResult.get("avgdaysforapplicationapproval");
		metrics.put("avgDaysForApplicationApproval", avgDays != null ? avgDays.intValue() : 0);


		// Parse JSON array buckets
		List<Map<String, Object>> propRegBuckets = parseJsonBuckets(combinedResult.get("propertiesregisteredjson"));
		metrics.put("propertiesRegistered", List.of(Map.of("groupBy", "financialYear", "buckets", propRegBuckets)));

		List<Map<String, Object>> assessedBuckets = parseJsonBucketsWithDefaults(
				combinedResult.get("assessedpropertiesjson"),
				new String[] { "RESIDENTIAL", "COMMERCIAL", "INDUSTRIAL" });
		metrics.put("assessedProperties", List.of(Map.of("groupBy", "usageCategory", "buckets", assessedBuckets)));


		// DB CALL 2: Payment and tax account breakdown query
		List<Map<String, Object>> collectionRows = namedParameterJdbcTemplate
				.queryForList(ptQueries.getCollectionMetricsQuery(), params);

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

			String channel = ("ONLINE".equalsIgnoreCase(mode) || "CARD".equalsIgnoreCase(mode)) ? "Digital"
					: "Non Digital";
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

		Map<String, Integer> usageTxnCountMap = new HashMap<>();
		for (Map.Entry<String, Set<String>> entry : usageTxnMap.entrySet()) {
			usageTxnCountMap.put(entry.getKey(), entry.getValue().size());
		}

		String[] categories = new String[] { "RESIDENTIAL", "COMMERCIAL", "INDUSTRIAL" };

		metrics.put("transactions", List.of(Map.of("groupBy", "usageCategory", "buckets",
				formatBucketsWithDefaults(usageTxnCountMap, categories))));

		metrics.put("todaysCollection", List.of(
				Map.of("groupBy", "usageCategory", "buckets",
						formatBucketsWithDefaults(usageCollectionMap, categories)),
				Map.of("groupBy", "paymentChannelType", "buckets",
						formatBucketsWithDefaults(channelCollectionMap, new String[] { "Digital", "Non Digital" }))));

		metrics.put("propertyTax", List
				.of(Map.of("groupBy", "usageCategory", "buckets", formatBucketsWithDefaults(propTaxMap, categories))));
		metrics.put("cess",
				List.of(Map.of("groupBy", "usageCategory", "buckets", formatBucketsWithDefaults(cessMap, categories))));
		metrics.put("rebate", List
				.of(Map.of("groupBy", "usageCategory", "buckets", formatBucketsWithDefaults(rebateMap, categories))));
		metrics.put("penalty", List
				.of(Map.of("groupBy", "usageCategory", "buckets", formatBucketsWithDefaults(penaltyMap, categories))));
		metrics.put("interest", List
				.of(Map.of("groupBy", "usageCategory", "buckets", formatBucketsWithDefaults(interestMap, categories))));

		return DashboardData.builder().date(dateStr).module(getModule().name()).ward(ward).ulb(ulb).region(region)
				.state(state).metrics(metrics).build();
	}

	private Integer getIntegerValue(Object obj) {
		if (obj instanceof Number) {
			return ((Number) obj).intValue();
		}
		return 0;
	}

	private List<Map<String, Object>> parseJsonBuckets(Object obj) {
		if (obj == null)
			return List.of();
		String jsonStr = obj.toString();
		if (jsonStr.isBlank() || "[]".equals(jsonStr))
			return List.of();
		try {
			return objectMapper.readValue(jsonStr, new TypeReference<List<Map<String, Object>>>() {
			});
		} catch (Exception e) {
			return List.of();
		}
	}

	private List<Map<String, Object>> parseJsonBucketsWithDefaults(Object obj, String[] expectedNames) {
		List<Map<String, Object>> parsed = parseJsonBuckets(obj);
		Map<String, Object> bucketMap = new LinkedHashMap<>();
		for (String name : expectedNames) {
			bucketMap.put(name, 0);
		}
		for (Map<String, Object> item : parsed) {
			String name = (String) item.get("name");
			Object val = item.get("value");
			if (name != null && val != null) {
				bucketMap.put(name, val);
			}
		}
		List<Map<String, Object>> buckets = new ArrayList<>();
		for (Map.Entry<String, Object> entry : bucketMap.entrySet()) {
			buckets.add(Map.of("name", entry.getKey(), "value", entry.getValue()));
		}
		return buckets;
	}

	private List<Map<String, Object>> formatBucketsWithDefaults(Map<String, ? extends Number> dataMap,
			String[] expectedNames) {
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
