package org.upyog.as.common;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

/**
 * Provides shared helper methods for date checks, tax calculations, and metric shaping.
 */
@Component
public class HelperClass {
	/**
	 * Checks whether a timestamp column falls within the supplied inclusive range.
	 *
	 * @param row the source row containing the timestamp value
	 * @param col the column name to inspect
	 * @param start the inclusive start timestamp in milliseconds
	 * @param end the inclusive end timestamp in milliseconds
	 * @return true when the value is inside the range
	 */
	public boolean inRange(Map<String, Object> row, String col, long start, long end) {
		Object val = row.get(col);
		if (val == null)
			return false;
		long ts = ((Number) val).longValue();
		return ts >= start && ts <= end;
	}

	/**
	 * Checks whether the update cycle for a row stays within the SLA threshold.
	 *
	 * @param row the source row containing create and modify timestamps
	 * @param slaMillis the SLA window in milliseconds
	 * @return true when the elapsed time is within the SLA
	 */
	public boolean withinSla(Map<String, Object> row, long slaMillis) {
		Long created = toLong(row.get("createdtime"));
		Long modified = toLong(row.get("lastmodifiedtime"));
		if (created == null || modified == null)
			return false;
		return (modified - created) <= slaMillis;
	}

	/**
	 * Computes the number of days between creation and modification timestamps.
	 *
	 * @param row the row containing the timestamps
	 * @return the elapsed day count or zero when timestamps are missing
	 */
	public double daysBetween(Map<String, Object> row) {
		Long created = toLong(row.get("createdtime"));
		Long modified = toLong(row.get("lastmodifiedtime"));
		if (created == null || modified == null)
			return 0;
		return (modified - created) / 86400000.0;
	}

	/**
	 * Calculates a simulated tax value from unit-level assessment fields.
	 *
	 * @param unitRow the unit row containing ARV or built-up area
	 * @return the simulated tax amount
	 */
	public double simulatedTax(Map<String, Object> unitRow) {
		Double arv = toDouble(unitRow.get("arv"));
		if (arv != null)
			return arv;
		Double builtUpArea = toDouble(unitRow.get("builtuparea"));
		return (builtUpArea != null ? builtUpArea : 0) * 10;
	}

	public Map<String, Double> sumByUsage(Map<Object, Double> taxByPropertyId, Map<Object, String> usageByPropertyId) {
		Map<String, Double> result = new LinkedHashMap<>();
		taxByPropertyId.forEach((propertyId, tax) -> {
			String usage = usageByPropertyId.getOrDefault(propertyId, "UNKNOWN");
			result.merge(usage, tax, Double::sum);
		});
		return result;
	}

	public Map<String, Double> scale(Map<String, Double> source, double factor) {
		Map<String, Double> result = new LinkedHashMap<>();
		source.forEach((k, v) -> result.put(k, Math.round(v * factor * 100.0) / 100.0));
		return result;
	}

	public Map<String, Double> zeroed(Map<String, Double> source) {
		Map<String, Double> result = new LinkedHashMap<>();
		source.keySet().forEach(k -> result.put(k, 0.0));
		return result;
	}

	public List<Map<String, Object>> groupedMetric(String groupBy, Map<String, Long> buckets) {
		return List.of(Map.of("groupBy", groupBy, "buckets",
				buckets.entrySet().stream().map(e -> Map.<String, Object>of("name", e.getKey(), "value", e.getValue()))
						.collect(Collectors.toList())));
	}

	public List<Map<String, Object>> groupedMetricDouble(String groupBy, Map<String, Double> buckets) {
		return List.of(Map.of("groupBy", groupBy, "buckets",
				buckets.entrySet().stream().map(e -> Map.<String, Object>of("name", e.getKey(), "value", e.getValue()))
						.collect(Collectors.toList())));
	}

	public Long toLong(Object val) {
		return val == null ? null : ((Number) val).longValue();
	}

	public Double toDouble(Object val) {
		return val == null ? null : ((Number) val).doubleValue();
	}
}
