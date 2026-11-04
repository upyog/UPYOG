package org.upyog.dashboard.transformer.impl;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.model.DashboardData;
import org.upyog.dashboard.model.DashboardPayload;
import org.upyog.dashboard.pt.dto.PTCollectionDTO;
import org.upyog.dashboard.pt.dto.PTAggregatedData;
import org.upyog.dashboard.pt.dto.PTDTO;
import org.upyog.dashboard.pt.model.PTMetric;
import org.upyog.dashboard.transformer.ModuleTransformer;
import org.upyog.dashboard.config.DashboardProperties;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Property Tax (PT) implementation of {@link ModuleTransformer}.
 *
 * <p>Responsible for converting raw PT DTO data ({@link PTDTO}) into a normalized
 * {@link DashboardPayload} by building a type-safe {@link PTMetric} object.
 */
@Component
@Slf4j
public class PTTransformer implements ModuleTransformer<PTDTO> {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private DashboardProperties dashboardProperties;

	@Override
	public Module getModule() {
		return Module.PT;
	}

	@Override
	public DashboardPayload transform(PTDTO rawData) {
		PTAggregatedData combined = rawData.getCombinedMetrics();
		List<PTCollectionDTO> collectionRows = rawData.getCollectionMetrics();

		if (combined == null) {
			combined = new PTAggregatedData();
		}
		if (collectionRows == null) {
			collectionRows = List.of();
		}

		// Process Combined Metrics
		Integer assessments = combined.getAssessments() != null ? combined.getAssessments() : 0;
		Integer todaysTotalApplications = combined.getTodaysTotalApplications() != null ? combined.getTodaysTotalApplications() : 0;
		Integer todaysClosedApplications = combined.getTodaysClosedApplications() != null ? combined.getTodaysClosedApplications() : 0;
		Integer noOfPropertiesPaidToday = combined.getNoOfPropertiesPaidToday() != null ? combined.getNoOfPropertiesPaidToday() : 0;
		Integer todaysApprovedApplications = combined.getTodaysApprovedApplications() != null ? combined.getTodaysApprovedApplications() : 0;
		Integer todaysApprovedApplicationsWithinSLA = combined.getTodaysApprovedApplicationsWithinSLA() != null ? combined.getTodaysApprovedApplicationsWithinSLA() : 0;
		Integer avgDaysForApplicationApproval = combined.getAvgDaysForApplicationApproval() != null ? combined.getAvgDaysForApplicationApproval() : 0;

		List<String> categoriesList = dashboardProperties.getPtUsageCategories();
		String[] categories = categoriesList != null && !categoriesList.isEmpty()
				? categoriesList.toArray(new String[0])
				: new String[] { "RESIDENTIAL", "COMMERCIAL", "INDUSTRIAL" };

		// Parse JSON array buckets
		List<Map<String, Object>> propRegBuckets = parseJsonBuckets(combined.getPropertiesRegisteredJson());
		List<Map<String, Object>> assessedBuckets = parseJsonBucketsWithDefaults(
				combined.getAssessedPropertiesJson(),
				categories);

		// Process Collection Metrics
		Map<String, Double> propTaxMap = new HashMap<>();
		Map<String, Double> cessMap = new HashMap<>();
		Map<String, Double> rebateMap = new HashMap<>();
		Map<String, Double> penaltyMap = new HashMap<>();
		Map<String, Double> interestMap = new HashMap<>();
		Map<String, Double> usageCollectionMap = new HashMap<>();
		Map<String, Double> channelCollectionMap = new HashMap<>();
		Map<String, Set<String>> usageTxnMap = new HashMap<>();

		for (PTCollectionDTO ptCollectionDto : collectionRows) {
			String usage = ptCollectionDto.getUsageCategory();
			if (usage == null) usage = "OTHERS";
			String mode = ptCollectionDto.getPaymentMode();
			String paymentId = ptCollectionDto.getPaymentId();
			String taxHead = ptCollectionDto.getTaxHeadCode();
			Double taxHeadAmountValue = ptCollectionDto.getTaxHeadAmount();
			double amount = taxHeadAmountValue != null ? taxHeadAmountValue : 0.0;

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

			boolean isDigital = false;
			if (mode != null && dashboardProperties.getPtDigitalPaymentModes() != null) {
				for (String digitalMode : dashboardProperties.getPtDigitalPaymentModes()) {
					if (digitalMode.equalsIgnoreCase(mode)) {
						isDigital = true;
						break;
					}
				}
			}
			String channel = isDigital ? "Digital" : "Non Digital";
			channelCollectionMap.put(channel, channelCollectionMap.getOrDefault(channel, 0.0) + amount);

			if (dashboardProperties.getPtTaxHeads() != null && dashboardProperties.getPtTaxHeads().contains(taxHead)) {
				propTaxMap.put(usage, propTaxMap.get(usage) + amount);
			} else if (dashboardProperties.getPtCessHeads() != null && dashboardProperties.getPtCessHeads().contains(taxHead)) {
				cessMap.put(usage, cessMap.get(usage) + amount);
			} else if (dashboardProperties.getPtRebateHeads() != null && dashboardProperties.getPtRebateHeads().contains(taxHead)) {
				rebateMap.put(usage, rebateMap.get(usage) + amount);
			} else if (dashboardProperties.getPtPenaltyHeads() != null && dashboardProperties.getPtPenaltyHeads().contains(taxHead)) {
				penaltyMap.put(usage, penaltyMap.get(usage) + amount);
			} else if (dashboardProperties.getPtInterestHeads() != null && dashboardProperties.getPtInterestHeads().contains(taxHead)) {
				interestMap.put(usage, interestMap.get(usage) + amount);
			}
		}

		Map<String, Integer> usageTxnCountMap = new HashMap<>();
		for (Map.Entry<String, Set<String>> entry : usageTxnMap.entrySet()) {
			usageTxnCountMap.put(entry.getKey(), entry.getValue().size());
		}



		// Build type-safe PTMetric (PTTran)
		PTMetric ptMetric = PTMetric.builder()
				.assessments(assessments)
				.todaysTotalApplications(todaysTotalApplications)
				.todaysClosedApplications(todaysClosedApplications)
				.noOfPropertiesPaidToday(noOfPropertiesPaidToday)
				.todaysApprovedApplications(todaysApprovedApplications)
				.todaysApprovedApplicationsWithinSLA(todaysApprovedApplicationsWithinSLA)
				.avgDaysForApplicationApproval(avgDaysForApplicationApproval)
				.propertiesRegistered(List.of(Map.of("groupBy", "financialYear", "buckets", propRegBuckets)))
				.assessedProperties(List.of(Map.of("groupBy", "usageCategory", "buckets", assessedBuckets)))
				.transactions(List.of(Map.of("groupBy", "usageCategory", "buckets", formatBucketsWithDefaults(usageTxnCountMap, categories))))
				.todaysCollection(List.of(
						Map.of("groupBy", "usageCategory", "buckets", formatBucketsWithDefaults(usageCollectionMap, categories)),
						Map.of("groupBy", "paymentChannelType", "buckets", formatBucketsWithDefaults(channelCollectionMap, new String[] { "Digital", "Non Digital" }))
				))
				.propertyTax(List.of(Map.of("groupBy", "usageCategory", "buckets", formatBucketsWithDefaults(propTaxMap, categories))))
				.cess(List.of(Map.of("groupBy", "usageCategory", "buckets", formatBucketsWithDefaults(cessMap, categories))))
				.rebate(List.of(Map.of("groupBy", "usageCategory", "buckets", formatBucketsWithDefaults(rebateMap, categories))))
				.penalty(List.of(Map.of("groupBy", "usageCategory", "buckets", formatBucketsWithDefaults(penaltyMap, categories))))
				.interest(List.of(Map.of("groupBy", "usageCategory", "buckets", formatBucketsWithDefaults(interestMap, categories))))
				.build();

		DashboardData dashboardData = DashboardData.builder()
				.date(rawData.getDate())
				.module(rawData.getModule())
				.ward(rawData.getWard())
				.ulb(rawData.getUlb())
				.region(rawData.getRegion())
				.state(rawData.getState())
				.metrics(ptMetric.toMap())
				.build();

		return DashboardPayload.builder()
				.data(List.of(dashboardData))
				.build();
	}

	/**
	 * Safely deserializes a JSON array string of category bucket maps into a typed List of Maps.
	 *
	 * @param jsonStr raw JSON bucket string from database
	 * @return parsed List of bucket maps or empty list on failure
	 */
	private List<Map<String, Object>> parseJsonBuckets(String jsonStr) {
		if (StringUtils.isBlank(jsonStr) || "[]".equals(jsonStr)) {
			return List.of();
		}
		try {
			return objectMapper.readValue(jsonStr, new TypeReference<List<Map<String, Object>>>() {});
		} catch (Exception exception) {
			log.error("PTTransformer | Failed to parse JSON buckets string: {}", jsonStr, exception);
			return List.of();
		}
	}

	/**
	 * Deserializes JSON bucket arrays ensuring that all expected bucket category names are present
	 * with default zero counts if missing from the raw payload.
	 *
	 * @param jsonStr       raw JSON bucket string from database
	 * @param expectedNames expected standard bucket names
	 * @return formatted bucket list with guaranteed keys
	 */
	private List<Map<String, Object>> parseJsonBucketsWithDefaults(String jsonStr, String[] expectedNames) {
		List<Map<String, Object>> parsed = parseJsonBuckets(jsonStr);
		Map<String, Object> bucketMap = new LinkedHashMap<>();
		for (String name : expectedNames) {
			bucketMap.put(name, 0);
		}
		for (Map<String, Object> item : parsed) {
			String name = (String) item.get("name");
			Object value = item.get("value");
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

	/**
	 * Converts an in-memory metric map into the standard name-value bucket format expected downstream.
	 *
	 * @param dataMap       aggregated metrics map
	 * @param expectedNames expected standard bucket names
	 * @return list of name-value bucket maps
	 */
	private List<Map<String, Object>> formatBucketsWithDefaults(Map<String, ? extends Number> dataMap, String[] expectedNames) {
		List<Map<String, Object>> buckets = new ArrayList<>();
		for (String name : expectedNames) {
			Number num = dataMap.get(name);
			double value = num != null ? num.doubleValue() : 0.0;
			buckets.add(Map.of("name", name, "value", (value == (long) value) ? (long) value : value));
		}
		for (Map.Entry<String, ? extends Number> entry : dataMap.entrySet()) {
			if (!java.util.Arrays.asList(expectedNames).contains(entry.getKey())) {
				double value = entry.getValue().doubleValue();
				buckets.add(Map.of("name", entry.getKey(), "value", (value == (long) value) ? (long) value : value));
			}
		}
		return buckets;
	}
}
