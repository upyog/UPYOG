package org.upyog.adapter.transformer.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.adapter.common.constants.Module;
import org.upyog.adapter.model.DashboardData;
import org.upyog.adapter.model.DashboardPayload;
import org.upyog.adapter.pt.dto.PTCollectionDTO;
import org.upyog.adapter.pt.dto.PTCombinedDTO;
import org.upyog.adapter.pt.dto.PTDTO;
import org.upyog.adapter.pt.model.PTMetric;
import org.upyog.adapter.transformer.ModuleTransformer;
import org.upyog.adapter.config.AdapterProperties;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Property Tax (PT) implementation of {@link ModuleTransformer}.
 *
 * <p>Responsible for converting raw PT DTO data ({@link PTDTO}) into a normalized
 * {@link DashboardPayload} by building a type-safe {@link PTMetric} object.
 */
@Component
public class PTTransformer implements ModuleTransformer<PTDTO> {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private AdapterProperties adapterProperties;

	@Override
	public Module getModule() {
		return Module.PT;
	}

	@Override
	public DashboardPayload transform(PTDTO rawData) {
		PTCombinedDTO combined = rawData.getCombinedMetrics();
		List<PTCollectionDTO> collectionRows = rawData.getCollectionMetrics();

		if (combined == null) {
			combined = new PTCombinedDTO();
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

		List<String> categoriesList = adapterProperties.getPtUsageCategories();
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

		for (PTCollectionDTO row : collectionRows) {
			String usage = row.getUsageCategory();
			if (usage == null) usage = "OTHERS";
			String mode = row.getPaymentMode();
			String paymentId = row.getPaymentId();
			String taxHead = row.getTaxHeadCode();
			Double amtNum = row.getTaxHeadAmount();
			double amount = amtNum != null ? amtNum : 0.0;

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
			if (mode != null && adapterProperties.getPtDigitalPaymentModes() != null) {
				for (String digitalMode : adapterProperties.getPtDigitalPaymentModes()) {
					if (digitalMode.equalsIgnoreCase(mode)) {
						isDigital = true;
						break;
					}
				}
			}
			String channel = isDigital ? "Digital" : "Non Digital";
			channelCollectionMap.put(channel, channelCollectionMap.getOrDefault(channel, 0.0) + amount);

			if (adapterProperties.getPtTaxHeads() != null && adapterProperties.getPtTaxHeads().contains(taxHead)) {
				propTaxMap.put(usage, propTaxMap.get(usage) + amount);
			} else if (adapterProperties.getPtCessHeads() != null && adapterProperties.getPtCessHeads().contains(taxHead)) {
				cessMap.put(usage, cessMap.get(usage) + amount);
			} else if (adapterProperties.getPtRebateHeads() != null && adapterProperties.getPtRebateHeads().contains(taxHead)) {
				rebateMap.put(usage, rebateMap.get(usage) + amount);
			} else if (adapterProperties.getPtPenaltyHeads() != null && adapterProperties.getPtPenaltyHeads().contains(taxHead)) {
				penaltyMap.put(usage, penaltyMap.get(usage) + amount);
			} else if (adapterProperties.getPtInterestHeads() != null && adapterProperties.getPtInterestHeads().contains(taxHead)) {
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

	private List<Map<String, Object>> parseJsonBuckets(String jsonStr) {
		if (jsonStr == null || jsonStr.isBlank() || "[]".equals(jsonStr)) {
			return List.of();
		}
		try {
			return objectMapper.readValue(jsonStr, new TypeReference<List<Map<String, Object>>>() {});
		} catch (Exception e) {
			return List.of();
		}
	}

	private List<Map<String, Object>> parseJsonBucketsWithDefaults(String jsonStr, String[] expectedNames) {
		List<Map<String, Object>> parsed = parseJsonBuckets(jsonStr);
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
