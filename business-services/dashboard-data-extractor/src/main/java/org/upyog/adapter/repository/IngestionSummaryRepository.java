package org.upyog.adapter.repository;

import org.upyog.adapter.service.IngestionPersistenceService;

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
	private IngestionPersistenceService persistenceService;

	/**
	 * Retrieves the last successfully ingested date for the specified tenant and
	 * module.
	 *
	 * @param tenantId   DIGIT tenant ID (exception.g. "pg.citya" or "pg")
	 * @param moduleName module short code (exception.g. "PT")
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

		} catch (Exception exception) {
			log.error("IngestionSummaryRepository | Failed to query last successful date for tenant {} module {}",
					tenantId, moduleName, exception);
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
		} catch (Exception exception) {
			log.error(
					"IngestionSummaryRepository | Failed to query successfully ingested dates for tenant {} module {} range [{} to {}]",
					tenantId, moduleName, startDate, endDate, exception);
		}
		return result;
	}

	public void saveOrUpdateLastSuccessfulDate(String tenantId, String moduleName, LocalDate successfulDate) {
		persistenceService.saveOrUpdateLastSuccessfulDate(tenantId, moduleName, successfulDate);
	}

	public void saveOrUpdateLastAttemptedDate(String tenantId, String moduleName, LocalDate attemptedDate) {
		persistenceService.saveOrUpdateLastAttemptedDate(tenantId, moduleName, attemptedDate);
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
		} catch (Exception exception) {
			log.error("IngestionSummaryRepository | Failed to query legacy job dates for tenant {} module {}", tenantId,
					moduleName, exception);
		}
		return dates;
	}

	public void createLegacyJob(String tenantId, String moduleName, LocalDate date) {
		persistenceService.createLegacyJob(tenantId, moduleName, date);
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
		} catch (Exception exception) {
			log.error("IngestionSummaryRepository | Failed to fetch pending/failed legacy jobs for tenant {} module {}",
					tenantId, moduleName, exception);
			return List.of();
		}
	}

	public void updateLegacyJobStatus(String jobId, String status, String requestData, String responseData) {
		persistenceService.updateLegacyJobStatus(jobId, status, requestData, responseData);
	}
}
