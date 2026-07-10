package org.upyog.as.core.extractor.impl;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.upyog.as.core.extractor.Extractor;
import org.upyog.as.extractor.record.PTRawData;
import org.upyog.as.model.config.PTSchemaMapping;

/**
 * Extracts property-tax related records for a tenant and reporting day from the configured datasource.
 */
@Component
public class UttarakhandPTExtractor implements Extractor {

	@Autowired
	private PTSchemaMapping mapping;

	/**
	 * Extracts properties, assessments and unit records for the supplied tenant and date window.
	 *
	 * @param jdbc the JDBC template used to run the extraction queries
	 * @param tenantId the tenant identifier to filter on
	 * @param date the reporting day for the extraction window
	 * @return a container object holding the extracted raw data and extraction rules
	 */
	@Override
	public PTRawData extract(JdbcTemplate jdbc, String tenantId, LocalDate date) {
		long startMs = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
		long endMs = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() - 1;

		Map<String, String> propCols = mapping.getProperty().getColumns();
		String propertyQuery = String.format(
				"SELECT %s AS id, %s AS status, %s AS createdtime, %s AS lastmodifiedtime, %s AS creationreason "
						+ "FROM %s WHERE %s = ? AND ((%s BETWEEN ? AND ?) OR (%s BETWEEN ? AND ?))",
				propCols.get("id"), propCols.get("status"), propCols.get("createdTime"),
				propCols.get("lastModifiedTime"), propCols.get("creationReason"), mapping.getProperty().getTable(),
				propCols.get("tenantId"), propCols.get("createdTime"), propCols.get("lastModifiedTime"));

		List<Map<String, Object>> properties = jdbc.queryForList(propertyQuery, tenantId, startMs, endMs, startMs,
				endMs);

		Map<String, String> asmtCols = mapping.getAssessment().getColumns();
		String assessmentQuery = String.format(
				"SELECT %s AS propertyid, %s AS assessmentdate, %s AS financialyear, %s AS channel "
						+ "FROM %s WHERE %s = ? AND %s BETWEEN ? AND ?",
				asmtCols.get("propertyId"), asmtCols.get("assessmentDate"), asmtCols.get("financialYear"),
				asmtCols.get("channel"), mapping.getAssessment().getTable(), asmtCols.get("tenantId"),
				asmtCols.get("assessmentDate"));
		List<Map<String, Object>> assessments = jdbc.queryForList(assessmentQuery, tenantId, startMs, endMs);

		Map<String, String> unitCols = mapping.getUnit().getColumns();
		String unitQuery = String.format(
				"SELECT %s AS propertyid, %s AS arv, %s AS builtuparea, %s AS usagecategory FROM %s WHERE %s = ?",
				unitCols.get("propertyId"), unitCols.get("arv"), unitCols.get("builtUpArea"),
				unitCols.get("usageCategory"), mapping.getUnit().getTable(), unitCols.get("tenantId"));
		List<Map<String, Object>> units = jdbc.queryForList(unitQuery, tenantId);

		return new PTRawData(properties, assessments, units, startMs, endMs, mapping.getRules());
	}
}