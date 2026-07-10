package org.upyog.as.core.transformer.impl;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.as.common.HelperClass;
import org.upyog.as.common.PaymentChannelNormalizer;
import org.upyog.as.common.UsageCategoryNormalizer;
import org.upyog.as.core.ExtractionContext;
import org.upyog.as.core.transformer.Transformer;
import org.upyog.as.extractor.record.PTRawData;
import org.upyog.as.model.config.PTSchemaMapping;
import org.upyog.as.model.config.PTSchemaMapping.FilterConfig;
import org.upyog.as.model.config.PTSchemaMapping.MetricConfig;
import org.upyog.as.model.payload.ModuleData;

/**
 * Transforms extracted raw property-tax records into the module payload structure expected by the ingest service.
 */
@Component
public class UttarakhandPTTransformer implements Transformer {

	@Autowired
	private HelperClass helperClass;
	@Autowired
	private UsageCategoryNormalizer usageCategoryNormalizer;
	@Autowired
	private PaymentChannelNormalizer paymentChannelNormalizer;
	@Autowired
	private PTSchemaMapping mapping;

	/**
	 * Builds a module payload from extracted data and the current extraction context.
	 *
	 * @param rawData the extracted source rows
	 * @param ctx the extraction context containing tenant and date information
	 * @return the module payload for ingestion
	 */
	@Override
	public ModuleData transform(PTRawData rawData, ExtractionContext ctx) {
		Map<String, Object> metrics = new LinkedHashMap<>();

		Map<Object, String> usageByPropertyId = rawData.units().stream().filter(u -> u.get("propertyid") != null)
				.collect(Collectors.toMap(u -> u.get("propertyid"),
						u -> usageCategoryNormalizer.normalize((String) u.get("usagecategory")), (a, b) -> a));

		for (MetricConfig cfg : mapping.getMetrics()) {
			switch (cfg.getType()) {
			case "SCALAR" -> metrics.put(cfg.getName(), computeScalar(cfg, rawData));
			case "GROUPED" -> metrics.put(cfg.getName(), helperClass.groupedMetricDouble(cfg.getGroupByLabel(),
					computeGrouped(cfg, rawData, usageByPropertyId)));
			case "MULTI_GROUPED" -> metrics.put(cfg.getName(), computeMultiGrouped(cfg, rawData, usageByPropertyId));
			default -> throw new IllegalStateException("Unknown metric type: " + cfg.getType());
			}
		}

		return ModuleData.builder().date(ctx.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))).module("PT")
				.ulb(ctx.getTenantId()).metrics(metrics).build();
	}

	/**
	 * Computes a single-value metric from the configured source rows.
	 *
	 * @param cfg the metric configuration
	 * @param rawData the extracted source rows
	 * @return the aggregated scalar metric value
	 */
	private Object computeScalar(MetricConfig cfg, PTRawData rawData) {
		List<Map<String, Object>> rows = sourceRows(cfg.getSource(), rawData);
		List<Map<String, Object>> filtered = applyFilters(rows, cfg, rawData);

		return switch (cfg.getAggregation()) {
		case "COUNT" -> (long) filtered.size();
		case "AVG_DAYS" -> {
			double avg = filtered.stream().mapToDouble(this::daysBetween).average().orElse(0);
			yield Math.round(avg * 10.0) / 10.0;
		}
		default -> throw new IllegalStateException("Unknown scalar aggregation: " + cfg.getAggregation());
		};
	}

	/**
	 * Computes a grouped metric from the configured source rows.
	 *
	 * @param cfg the metric configuration
	 * @param rawData the extracted source rows
	 * @param usageByPropertyId normalized usage category by property identifier
	 * @return a grouped metric map keyed by group label
	 */
	private Map<String, Double> computeGrouped(MetricConfig cfg, PTRawData rawData,
			Map<Object, String> usageByPropertyId) {
		List<Map<String, Object>> rows = sourceRows(cfg.getSource(), rawData);
		List<Map<String, Object>> filtered = applyFilters(rows, cfg, rawData);

		return switch (cfg.getAggregation()) {
		case "COUNT" -> toDoubleMap(filtered.stream()
				.collect(Collectors.groupingBy(r -> groupKey(r, cfg, usageByPropertyId), Collectors.counting())));

		case "DISTINCT_PROPERTY_COUNT" -> toDoubleMap(filtered.stream()
				.collect(Collectors.groupingBy(r -> groupKey(r, cfg, usageByPropertyId),
						Collectors.mapping(r -> r.get("propertyid"), Collectors.toSet())))
				.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> (long) e.getValue().size())));

		case "SUM_SIMULATED_TAX" -> {
			Map<Object, Double> taxByProperty = rawData.units().stream().collect(
					Collectors.groupingBy(u -> u.get("propertyid"), Collectors.summingDouble(this::simulatedTax)));
			Map<String, Double> byUsage = new LinkedHashMap<>();
			taxByProperty.forEach((propertyId, tax) -> {
				String key = usageByPropertyId.getOrDefault(propertyId, "UNKNOWN");
				byUsage.merge(key, tax * cfg.getScaleFactor(), Double::sum);
			});
			yield byUsage;
		}

		case "ZERO" -> {
			Map<String, Double> zeroed = new LinkedHashMap<>();
			usageByPropertyId.values().stream().distinct().forEach(usage -> zeroed.put(usage, 0.0));
			yield zeroed;
		}

		default -> throw new IllegalStateException("Unknown grouped aggregation: " + cfg.getAggregation());
		};
	}

	/**
	 * Computes a multi-grouped metric by combining the results of multiple sub-groupings.
	 *
	 * @param cfg the parent metric configuration
	 * @param rawData the extracted source rows
	 * @param usageByPropertyId normalized usage category by property identifier
	 * @return a flattened list of grouped metric payloads
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> computeMultiGrouped(MetricConfig cfg, PTRawData rawData,
			Map<Object, String> usageByPropertyId) {
		return cfg.getGroupings().stream()
				.map(sub -> (Map<String, Object>) helperClass
						.groupedMetricDouble(sub.getGroupByLabel(), computeGrouped(sub, rawData, usageByPropertyId))
						.get(0))
				.collect(Collectors.toList());
	}

	/**
	 * Selects the relevant row set for a metric based on its configured source.
	 *
	 * @param source the source identifier
	 * @param rawData the extracted source rows
	 * @return the matching list of records
	 */
	private List<Map<String, Object>> sourceRows(String source, PTRawData rawData) {
		return switch (source) {
		case "property" -> rawData.properties();
		case "assessment" -> rawData.assessments();
		case "unit" -> rawData.units();
		default -> throw new IllegalStateException("Unknown source: " + source);
		};
	}

	/**
	 * Applies the configured date and filter rules to the candidate rows.
	 *
	 * @param rows the candidate rows before filtering
	 * @param cfg the metric configuration
	 * @param rawData the extracted source rows
	 * @return the filtered row set
	 */
	private List<Map<String, Object>> applyFilters(List<Map<String, Object>> rows, MetricConfig cfg,
			PTRawData rawData) {
		List<Map<String, Object>> result = rows;

		if (cfg.isDateBounded() && cfg.getDateField() != null) {
			result = result.stream()
					.filter(r -> helperClass.inRange(r, cfg.getDateField(), rawData.startMs(), rawData.endMs()))
					.collect(Collectors.toList());
		}

		if (cfg.isWithinSla()) {
			long slaMillis = rawData.rules().getSlaDays() * 86400000L;
			result = result.stream().filter(r -> helperClass.withinSla(r, slaMillis)).collect(Collectors.toList());
		}

		if (cfg.getFilters() != null) {
			for (FilterConfig f : cfg.getFilters()) {
				Predicate<Map<String, Object>> predicate = switch (f.getOp()) {
				case "EQUALS" -> r -> f.getValue().equals(r.get(f.getField()));
				case "IN" -> r -> ((List<?>) f.getValue()).contains(r.get(f.getField()));
				default -> throw new IllegalStateException("Unknown filter op: " + f.getOp());
				};
				result = result.stream().filter(predicate).collect(Collectors.toList());
			}
		}

		return result;
	}

	/**
	 * Derives the group label for a row based on the metric's grouping configuration.
	 *
	 * @param row the input data row
	 * @param cfg the metric configuration
	 * @param usageByPropertyId normalized usage categories by property identifier
	 * @return the computed group key
	 */
	private String groupKey(Map<String, Object> row, MetricConfig cfg, Map<Object, String> usageByPropertyId) {
		if ("usageCategory".equals(cfg.getGroupBy())) {
			return usageByPropertyId.getOrDefault(row.get("propertyid"), "UNKNOWN");
		}
		Object raw = row.get(cfg.getGroupBy());
		if ("channel".equals(cfg.getGroupBy())) {
			return paymentChannelNormalizer.normalize((String) raw);
		}
		return raw != null ? raw.toString() : "UNKNOWN";
	}

	/**
	 * Converts a map of long counts into a map of double values.
	 *
	 * @param longMap the count map to convert
	 * @return the converted double-valued map
	 */
	private Map<String, Double> toDoubleMap(Map<String, Long> longMap) {
		Map<String, Double> result = new LinkedHashMap<>();
		longMap.forEach((k, v) -> result.put(k, v.doubleValue()));
		return result;
	}

	/**
	 * Computes the number of elapsed days between creation and modification timestamps.
	 *
	 * @param row the property row to evaluate
	 * @return the elapsed day count, or zero when timestamps are unavailable
	 */
	private double daysBetween(Map<String, Object> row) {
		Long created = helperClass.toLong(row.get("createdtime"));
		Long modified = helperClass.toLong(row.get("lastmodifiedtime"));
		if (created == null || modified == null)
			return 0;
		return (modified - created) / 86400000.0;
	}

	/**
	 * Derives the simulated property tax value from unit details.
	 *
	 * @param unitRow the unit row to evaluate
	 * @return the simulated tax amount
	 */
	private double simulatedTax(Map<String, Object> unitRow) {
		Double arv = helperClass.toDouble(unitRow.get("arv"));
		if (arv != null)
			return arv;
		Double builtUpArea = helperClass.toDouble(unitRow.get("builtuparea"));
		return (builtUpArea != null ? builtUpArea : 0) * 10;
	}
}