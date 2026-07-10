package org.upyog.as.model.config;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * Defines the schema mapping and metric rules used by the property-tax adapter.
 */
@Data
public class PTSchemaMapping {
	private String tenantId;
	private String module;
	private TableMapping property;
	private TableMapping assessment;
	private TableMapping unit;
	private Rules rules;
	private List<MetricConfig> metrics;

	@Data
	public static class TableMapping {
		private String table;
		private Map<String, String> columns;
	}

	@Data
	public static class Rules {
		private int slaDays;
		private double taxDefaultRate;
	}

	/**
	 * Describes one metric that should be produced for the module payload.
	 */
	@Data
	public static class MetricConfig {
		private String name;
		private String type;
		private String source;
		private String aggregation;
		private String dateField;
		private boolean dateBounded;
		private boolean withinSla;
		private String groupBy;
		private String groupByLabel;
		private double scaleFactor = 1.0;
		private String normalizer;
		private List<FilterConfig> filters;
		private List<MetricConfig> groupings;
	}

	/**
	 * Defines a filter that narrows the candidate rows for a metric.
	 */
	@Data
	public static class FilterConfig {
		private String field;
		private String op;
		private Object value;
	}
}