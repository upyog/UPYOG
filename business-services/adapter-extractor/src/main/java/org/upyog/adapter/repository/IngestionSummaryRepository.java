package org.upyog.adapter.repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.upyog.adapter.repository.querybuilder.IngestionSummaryQueryBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Repository managing persistence operations for
 * {@code ingestion_module_summary}.
 *
 * <p>
 * Tracks and updates the last successfully ingested date per tenant and module.
 */
@Slf4j
@Repository
public class IngestionSummaryRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private IngestionSummaryQueryBuilder queryBuilder;
	
	@Autowired
	private org.upyog.adapter.producer.AdapterProducer producer;

	/**
	 * Retrieves the last successfully ingested date for the specified tenant and
	 * module.
	 *
	 * @param tenantId   DIGIT tenant ID (e.g. "pg.citya" or "pg")
	 * @param moduleName module short code (e.g. "PT")
	 * @return Optional containing the last successful date, or empty if no entry
	 *         exists
	 */
	public Optional<LocalDate> findLastSuccessfulDate(String tenantId, String moduleName) {
		try {
			List<Date> dates = jdbcTemplate.query(queryBuilder.getSelectLastSuccessfulDateQuery(),
					(rs, rowNum) -> rs.getDate("last_successful_date"), tenantId, moduleName);

			return dates.stream().filter(Objects::nonNull).map(Date::toLocalDate)
					.filter(date -> !date.equals(LocalDate.EPOCH)) // Ignores 1970-01-01
					.findFirst();

		} catch (Exception e) {
			log.error("IngestionSummaryRepository | Failed to query last successful date for tenant {} module {}",
					tenantId, moduleName, e);
		}
		return Optional.empty();
	}

	/**
	 * Queries all dates within the specified date range that have already been
	 * successfully ingested (via daily or legacy pipelines) for the given tenant
	 * and module.
	 *
	 * @param tenantId   DIGIT tenant ID
	 * @param moduleName module short code
	 * @param startDate  start of range
	 * @param endDate    end of range
	 * @return Set of LocalDate instances that are already marked SUCCESS
	 */
	public java.util.Set<LocalDate> findSuccessfullyIngestedDates(String tenantId, String moduleName,
			LocalDate startDate, LocalDate endDate) {
		java.util.Set<LocalDate> result = new java.util.HashSet<>();
		try {
			Date sDate = Date.valueOf(startDate);
			Date eDate = Date.valueOf(endDate);
			List<Date> dates = jdbcTemplate.query(queryBuilder.getSelectSuccessfulDatesInRangeQuery(),
					(rs, rowNum) -> rs.getDate("push_date"), tenantId, moduleName, sDate, eDate, tenantId, moduleName,
					sDate, eDate);
			for (Date d : dates) {
				if (d != null) {
					result.add(d.toLocalDate());
				}
			}
		} catch (Exception e) {
			log.error(
					"IngestionSummaryRepository | Failed to query successfully ingested dates for tenant {} module {} range [{} to {}]",
					tenantId, moduleName, startDate, endDate, e);
		}
		return result;
	}

	public void saveOrUpdateLastSuccessfulDate(String tenantId, String moduleName, LocalDate successfulDate) {
		try {
			long now = System.currentTimeMillis();
			String id = UUID.randomUUID().toString();
			
			org.upyog.adapter.entity.IngestionModuleSummary summary = org.upyog.adapter.entity.IngestionModuleSummary.builder()
			    .id(id)
			    .tenantId(tenantId)
			    .moduleName(moduleName)
			    .lastSuccessfulDate(successfulDate.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")))
			    .lastAttemptedDate(successfulDate.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")))
			    .createdBy("SYSTEM")
			    .createdTime(now)
			    .lastModifiedBy("SYSTEM")
			    .lastModifiedTime(now)
			    .build();

			java.util.Map<String, Object> msg = new java.util.HashMap<>();
			msg.put("ingestionModuleSummary", java.util.Collections.singletonList(summary));
			producer.push(org.upyog.adapter.common.constants.KafkaTopics.UPDATE_ADAPTER_MODULE_SUMMARY, msg);

			log.info("IngestionSummaryRepository | Pushed update for ingestion_module_summary last_successful_date to {} for tenant {} module {}",
					successfulDate, tenantId, moduleName);

		} catch (Exception e) {
			log.error(
					"IngestionSummaryRepository | Failed to update last successful date to {} for tenant {} module {}",
					successfulDate, tenantId, moduleName, e);
		}
	}

	public void saveOrUpdateLastAttemptedDate(String tenantId, String moduleName, LocalDate attemptedDate) {
		try {
			long now = System.currentTimeMillis();
			String id = UUID.randomUUID().toString();
			LocalDate fallbackSuccessDate = LocalDate.of(1970, 1, 1);

			org.upyog.adapter.entity.IngestionModuleSummary summary = org.upyog.adapter.entity.IngestionModuleSummary.builder()
			    .id(id)
			    .tenantId(tenantId)
			    .moduleName(moduleName)
			    .lastSuccessfulDate(fallbackSuccessDate.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")))
			    .lastAttemptedDate(attemptedDate.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")))
			    .createdBy("SYSTEM")
			    .createdTime(now)
			    .lastModifiedBy("SYSTEM")
			    .lastModifiedTime(now)
			    .build();

			java.util.Map<String, Object> msg = new java.util.HashMap<>();
			msg.put("ingestionModuleSummary", java.util.Collections.singletonList(summary));
			producer.push(org.upyog.adapter.common.constants.KafkaTopics.UPDATE_ADAPTER_MODULE_SUMMARY, msg);

			log.info("IngestionSummaryRepository | Pushed update for ingestion_module_summary last_attempted_date to {} for tenant {} module {}",
					attemptedDate, tenantId, moduleName);
		} catch (Exception e) {
			log.error("IngestionSummaryRepository | Failed to update last attempted date to {} for tenant {} module {}",
					attemptedDate, tenantId, moduleName, e);
		}
	}

	public Set<LocalDate> findRegisteredLegacyJobDates(String tenantId, String moduleName) {
		Set<LocalDate> dates = new HashSet<>();
		try {
			List<Date> results = jdbcTemplate.query(queryBuilder.getSelectLegacyJobDatesQuery(),
					(rs, rowNum) -> rs.getDate("push_date"), tenantId, moduleName);
			for (Date d : results) {
				if (d != null) {
					dates.add(d.toLocalDate());
				}
			}
		} catch (Exception e) {
			log.error("IngestionSummaryRepository | Failed to query legacy job dates for tenant {} module {}", tenantId,
					moduleName, e);
		}
		return dates;
	}

	public void createLegacyJob(String tenantId, String moduleName, LocalDate date) {
		try {
			long now = System.currentTimeMillis();
			String jobId = UUID.randomUUID().toString();
			
			org.upyog.adapter.entity.LegacyIngestionData legacyData = org.upyog.adapter.entity.LegacyIngestionData.builder()
			    .moduleIngestionId(jobId)
			    .tenantId(tenantId)
			    .moduleName(moduleName)
			    .pushDate(date.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")))
			    .ingestionStatus("NOT_STARTED")
			    .createdBy("SYSTEM")
			    .createdTime(now)
			    .lastModifiedBy("SYSTEM")
			    .lastModifiedTime(now)
			    .build();
			    
			java.util.Map<String, Object> msg = new java.util.HashMap<>();
			msg.put("legacyIngestionData", java.util.Collections.singletonList(legacyData));
			producer.push(org.upyog.adapter.common.constants.KafkaTopics.SAVE_LEGACY_INGESTION_DETAIL, msg);

			log.debug("IngestionSummaryRepository | Pushed legacy job for tenant {} module {} date {}", tenantId,
					moduleName, date);
		} catch (Exception e) {
			log.error("IngestionSummaryRepository | Failed to push legacy job for tenant {} module {} date {}",
					tenantId, moduleName, date, e);
		}
	}

	public static class LegacyJob {
		private final String jobId;
		private final LocalDate pushDate;

		public LegacyJob(String jobId, LocalDate pushDate) {
			this.jobId = jobId;
			this.pushDate = pushDate;
		}

		public String getJobId() {
			return jobId;
		}

		public LocalDate getPushDate() {
			return pushDate;
		}
	}

	public List<LegacyJob> findPendingOrFailedLegacyJobs(String tenantId, String moduleName, int limit) {
		try {
			return jdbcTemplate.query(queryBuilder.getSelectPendingOrFailedLegacyJobsQuery(),
					(rs, rowNum) -> new LegacyJob(rs.getString("module_ingestion_id"),
							rs.getDate("push_date").toLocalDate()),
					tenantId, moduleName, limit);
		} catch (Exception e) {
			log.error("IngestionSummaryRepository | Failed to fetch pending/failed legacy jobs for tenant {} module {}",
					tenantId, moduleName, e);
			return List.of();
		}
	}

	public void updateLegacyJobStatus(String jobId, String status, String requestData, String responseData) {
		try {
			long now = System.currentTimeMillis();
			org.upyog.adapter.entity.LegacyIngestionData legacyData = org.upyog.adapter.entity.LegacyIngestionData.builder()
			    .moduleIngestionId(jobId)
			    .responseData(responseData)
			    .ingestionStatus(status)
			    .lastModifiedBy("SYSTEM")
			    .lastModifiedTime(now)
			    .build();
			    
			java.util.Map<String, Object> msg = new java.util.HashMap<>();
			msg.put("legacyIngestionData", java.util.Collections.singletonList(legacyData));
			producer.push(org.upyog.adapter.common.constants.KafkaTopics.UPDATE_LEGACY_INGESTION_DETAIL, msg);

			log.info("IngestionSummaryRepository | Pushed update legacy job {} to status {}", jobId, status);
		} catch (Exception e) {
			log.error("IngestionSummaryRepository | Failed to push update legacy job {} to status {}", jobId, status, e);
		}
	}
}
