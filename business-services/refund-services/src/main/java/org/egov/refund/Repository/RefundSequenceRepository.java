package org.egov.refund.Repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RefundSequenceRepository {

	private final JdbcTemplate jdbcTemplate;

	public RefundSequenceRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Long getNextSequence(String moduleName, String businessService, String consumerCode) {

		if (moduleName == null || moduleName.isBlank()) {
			throw new IllegalArgumentException("Module name is mandatory");
		}

		if (businessService == null || businessService.isBlank()) {
			throw new IllegalArgumentException("Business service is mandatory");
		}

		if (consumerCode == null || consumerCode.isBlank()) {
			throw new IllegalArgumentException("Consumer code is mandatory");
		}

		
		String lockSql = """
				SELECT pg_advisory_xact_lock(
				    hashtextextended(
				        ? || '|' || ? || '|' || ?,
				        0
				    )
				)
				""";

		jdbcTemplate.query(lockSql, rs -> {
			// pg_advisory_xact_lock() returns void.
			// Nothing needs to be read from the result.
		}, moduleName, businessService, consumerCode);

		/*
		 * Find the highest sequence already generated for this exact combination.
		 */
		String sequenceSql = """
				SELECT COALESCE(
				    MAX(
				        CAST(
				            SUBSTRING(refund_no FROM '([0-9]+)$')
				            AS BIGINT
				        )
				    ),
				    0
				) + 1
				FROM eg_refund
				WHERE module_name = ?
				  AND business_service = ?
				  AND consumer_code = ?
				""";

		return jdbcTemplate.queryForObject(sequenceSql, Long.class, moduleName, businessService, consumerCode);
	}
}