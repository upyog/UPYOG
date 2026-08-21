package org.upyog.dashboard.repository;

import org.upyog.dashboard.service.IngestionPersistenceService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.upyog.dashboard.repository.querybuilder.IngestionSummaryQueryBuilder;

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
@lombok.RequiredArgsConstructor
public class IngestionSummaryRepository {

	private final JdbcTemplate jdbcTemplate;
	private final IngestionSummaryQueryBuilder queryBuilder;
	private final IngestionPersistenceService persistenceService;


	/**
 * Retrieves the last successfully ingested date for the specified tenant and module.
 *
 * @param tenantId   the tenant identifier (e.g., "pg.citya" or "pg")
 * @param moduleName the module short code (e.g., "PT")
 * @return an {@code Optional} containing the last successful {@link LocalDate}, or empty if none exists
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
 * Retrieves all dates within the given range that have already been successfully ingested for the specified tenant and module.
 *
 * @param tenantId   the tenant identifier
 * @param moduleName the module short code
 * @param startDate  the start of the date range (inclusive)
 * @param endDate    the end of the date range (inclusive)
 * @return a {@link Set} of {@link LocalDate} instances representing successful ingest dates
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

	/**
 * Persists or updates the last successful ingestion date for a tenant and module.
 *
 * @param tenantId       the tenant identifier
 * @param moduleName     the module short code
 * @param successfulDate the date of the successful ingestion
 */
public void saveOrUpdateLastSuccessfulDate(String tenantId, String moduleName, LocalDate successfulDate) {
		persistenceService.saveOrUpdateLastSuccessfulDate(tenantId, moduleName, successfulDate);
	}

	/**
 * Persists or updates the last attempted ingestion date for a tenant and module.
 *
 * @param tenantId       the tenant identifier
 * @param moduleName     the module short code
 * @param attemptedDate  the date of the attempted ingestion
 */
public void saveOrUpdateLastAttemptedDate(String tenantId, String moduleName, LocalDate attemptedDate) {
		persistenceService.saveOrUpdateLastAttemptedDate(tenantId, moduleName, attemptedDate);
	}

	/**
	 * Persists a batch of daily ingestion detail audit records.
	 *
	 * @param details list of daily ingestion data objects or rows
	 */
	public void saveIngestionDetailsBatch(List<?> details) {
		persistenceService.saveIngestionDetailsBatch(details);
	}

	/**
 * Retrieves all legacy job dates that have been registered for a given tenant and module.
 *
 * @param tenantId   the tenant identifier
 * @param moduleName the module short code
 * @return a {@link Set} of {@link LocalDate} representing registered legacy job dates
 */
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

	/**
 * Creates a new legacy ingestion job entry.
 *
 * @param tenantId   the tenant identifier
 * @param moduleName the module short code
 * @param date       the date for which the legacy job is created
 */
public void createLegacyJob(String tenantId, String moduleName, LocalDate date) {
		persistenceService.createLegacyJob(tenantId, moduleName, date);
	}

	/**
	 * Immutable value object representing a legacy ingestion job entry retrieved from
	 * {@code legacy_data_ingestion_detail}. Carries the unique job identifier and the
	 * date for which data should be ingested.
	 */
	public static class LegacyJob {
		private final String jobId;
		private final LocalDate pushDate;

		/**
		 * Constructs a {@code LegacyJob} with the given job ID and push date.
		 *
		 * @param jobId    the unique identifier of the legacy job
		 * @param pushDate the date for which the legacy job should ingest data
		 */
		public LegacyJob(String jobId, LocalDate pushDate) {
			this.jobId = jobId;
			this.pushDate = pushDate;
		}

		/**
		 * Returns the unique identifier of this legacy job.
		 *
		 * @return the job ID string
		 */
		public String getJobId() {
			return jobId;
		}

		/**
		 * Returns the date for which this legacy job should ingest data.
		 *
		 * @return the push date
		 */
		public LocalDate getPushDate() {
			return pushDate;
		}
	}

	/**
 * Retrieves pending or failed legacy jobs up to a specified limit.
 *
 * @param tenantId   the tenant identifier
 * @param moduleName the module short code
 * @param limit      maximum number of jobs to return
 * @return a {@link List} of {@link LegacyJob} objects representing pending/failed jobs
 */
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

	/**
 * Updates the status and payload data of a legacy job.
 *
 * @param jobId        the unique identifier of the legacy job
 * @param status       the new status (e.g., SUCCESS, FAILURE)
 * @param requestData  the request payload sent to the external system
 * @param responseData the response payload received from the external system
 */
public void updateLegacyJobStatus(String jobId, String status, String requestData, String responseData) {
		persistenceService.updateLegacyJobStatus(jobId, status, requestData, responseData);
	}

	/**
	 * Acquires a DB row-level pessimistic lock (FOR UPDATE) on the summary record for tenant and module.
	 *
	 * @param tenantId   the tenant identifier
	 * @param moduleName the module short code
	 * @return true if lock was acquired, false otherwise
	 */
	public boolean tryAcquireLock(String tenantId, String moduleName) {
		try {
			jdbcTemplate.queryForList(queryBuilder.getSelectForUpdateSummaryQuery(), tenantId, moduleName);
			return true;
		} catch (Exception exception) {
			log.warn("IngestionSummaryRepository | Failed to acquire lock for tenant {} module {}", tenantId, moduleName, exception);
			return false;
		}
	}
}
