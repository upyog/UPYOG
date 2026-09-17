package org.upyog.dashboard.repository;

import org.upyog.dashboard.constants.DashboardExtractorConstants;
import org.upyog.dashboard.service.IngestionPersistenceService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.upyog.dashboard.repository.querybuilder.IngestionSummaryQueryBuilder;
import org.upyog.dashboard.util.CommonUtils;

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

	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
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
			MapSqlParameterSource params = new MapSqlParameterSource()
					.addValue(DashboardExtractorConstants.PARAM_TENANT_ID, tenantId)
					.addValue(DashboardExtractorConstants.PARAM_MODULE_NAME, moduleName);
			List<Date> dates = namedParameterJdbcTemplate.query(IngestionSummaryQueryBuilder.SELECT_LAST_SUCCESSFUL_DATE_QUERY,
					params, (resultSet, rowNumber) -> resultSet.getDate("last_successful_date"));

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
			Date sqlStartDate = Date.valueOf(startDate);
			Date sqlEndDate = Date.valueOf(endDate);
			MapSqlParameterSource params = new MapSqlParameterSource()
					.addValue(DashboardExtractorConstants.PARAM_TENANT_ID, tenantId)
					.addValue(DashboardExtractorConstants.PARAM_MODULE_NAME, moduleName)
					.addValue(DashboardExtractorConstants.PARAM_START_DATE, sqlStartDate)
					.addValue(DashboardExtractorConstants.PARAM_END_DATE, sqlEndDate);
			List<Date> dates = namedParameterJdbcTemplate.query(IngestionSummaryQueryBuilder.SELECT_SUCCESSFUL_DATES_IN_RANGE_QUERY,
					params, (resultSet, rowNumber) -> resultSet.getDate("push_date"));
			for (Date sqlDate : dates) {
				if (sqlDate != null) {
					result.add(sqlDate.toLocalDate());
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
	 * Checks whether the {@code ingestion_module_detail} table has already marked legacy
	 * ingestion as completed for the given tenant and module.
	 *
	 * @param tenantId   the tenant identifier
	 * @param moduleName the module short code
	 * @return {@code true} if legacy ingestion has completed, {@code false} otherwise
	 */
	public boolean isLegacyIngestionComplete(String tenantId, String moduleName) {
		try {
			MapSqlParameterSource params = new MapSqlParameterSource()
					.addValue(DashboardExtractorConstants.PARAM_TENANT_ID, tenantId)
					.addValue(DashboardExtractorConstants.PARAM_MODULE_NAME, moduleName);
			String sql = "SELECT is_legacy_data_ingested FROM ingestion_module_detail WHERE tenant_id = :tenantId AND module_name = :moduleName";
			List<Boolean> flags = namedParameterJdbcTemplate.query(sql, params, (resultSet, rowNumber) -> resultSet.getBoolean("is_legacy_data_ingested"));
			return !flags.isEmpty() && Boolean.TRUE.equals(flags.get(0));
		} catch (Exception exception) {
			log.error("IngestionSummaryRepository | Failed to check isLegacyIngestionComplete for tenant {} module {}", tenantId, moduleName, exception);
			return false;
		}
	}

	/**
	 * Updates the {@code ingestion_module_detail} table, marking legacy ingestion as
	 * completed ({@code is_legacy_data_ingested = TRUE}) and setting the last ingested date.
	 *
	 * @param tenantId   the tenant identifier
	 * @param moduleName the module short code
	 * @param lastDate   the latest date ingested
	 */
	public void markLegacyIngestionComplete(String tenantId, String moduleName, LocalDate lastDate) {
		try {
			long now = CommonUtils.getCurrentEpochMillis();
			MapSqlParameterSource params = new MapSqlParameterSource()
					.addValue("lastIngestedDate", Date.valueOf(lastDate))
					.addValue(DashboardExtractorConstants.PARAM_LAST_MODIFIED_TIME, now)
					.addValue(DashboardExtractorConstants.PARAM_TENANT_ID, tenantId)
					.addValue(DashboardExtractorConstants.PARAM_MODULE_NAME, moduleName);
			namedParameterJdbcTemplate.update(IngestionSummaryQueryBuilder.UPDATE_MODULE_DETAIL_TABLE_QUERY, params);
			log.info("IngestionSummaryRepository | Marked legacy ingestion complete for tenant {} module {} at date {}", tenantId, moduleName, lastDate);
		} catch (Exception exception) {
			log.error("IngestionSummaryRepository | Failed to mark legacy ingestion complete for tenant {} module {}", tenantId, moduleName, exception);
		}
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
	 * Persists a batch of daily ingestion detail records.
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
			MapSqlParameterSource params = new MapSqlParameterSource()
					.addValue(DashboardExtractorConstants.PARAM_TENANT_ID, tenantId)
					.addValue(DashboardExtractorConstants.PARAM_MODULE_NAME, moduleName);
			List<Date> results = namedParameterJdbcTemplate.query(IngestionSummaryQueryBuilder.SELECT_LEGACY_JOB_DATES_QUERY,
					params, (resultSet, rowNumber) -> resultSet.getDate("push_date"));
			for (Date resultDate : results) {
				if (resultDate != null) {
					dates.add(resultDate.toLocalDate());
				}
			}
		} catch (Exception exception) {
			log.error("IngestionSummaryRepository | Failed to query legacy job dates for tenant {} module {}", tenantId,
					moduleName, exception);
		}
		return dates;
	}

	/**
	 * Checks if any successful legacy ingestion records overlap with the specified date range.
	 *
	 * @param tenantId   the tenant identifier
	 * @param moduleName the module short code
	 * @param startDate  start of the date range
	 * @param endDate    end of the date range
	 * @return a list of overlapping LegacyJob records
	 */
	public List<LegacyJob> findOverlappingSuccessfulLegacyJobs(String tenantId, String moduleName, LocalDate startDate, LocalDate endDate) {
		try {
			MapSqlParameterSource params = new MapSqlParameterSource()
					.addValue(DashboardExtractorConstants.PARAM_TENANT_ID, tenantId)
					.addValue(DashboardExtractorConstants.PARAM_MODULE_NAME, moduleName)
					.addValue(DashboardExtractorConstants.PARAM_START_DATE, Date.valueOf(startDate))
					.addValue(DashboardExtractorConstants.PARAM_END_DATE, Date.valueOf(endDate));
			return namedParameterJdbcTemplate.query(IngestionSummaryQueryBuilder.SELECT_OVERLAPPING_SUCCESSFUL_LEGACY_JOBS_QUERY,
					params, (resultSet, rowNumber) -> {
						Date sqlStartDate = resultSet.getDate("start_date");
						Date sqlEndDate = resultSet.getDate("end_date");
						Date sqlPushDate = resultSet.getDate("push_date");
						LocalDate start = (sqlStartDate != null) ? sqlStartDate.toLocalDate() : (sqlPushDate != null ? sqlPushDate.toLocalDate() : null);
						return new LegacyJob(resultSet.getString("module_ingestion_id"), start);
					});
		} catch (Exception exception) {
			log.error("IngestionSummaryRepository | Failed to query overlapping legacy jobs for tenant {} module {} range [{} to {}]",
					tenantId, moduleName, startDate, endDate, exception);
			return List.of();
		}
	}

	/**
	 * Creates a new legacy ingestion job entry.
	 *
	 * @param jobId      the unique identifier of the legacy job
	 * @param tenantId   the tenant identifier
	 * @param moduleName the module short code
	 * @param pushDate   the push date for the legacy job
	 * @param startDate  the start date of the legacy range
	 * @param endDate    the end date of the legacy range
	 */
	public void createLegacyJob(String jobId, String tenantId, String moduleName, LocalDate pushDate, LocalDate startDate, LocalDate endDate) {
		persistenceService.createLegacyJob(jobId, tenantId, moduleName, pushDate, startDate, endDate);
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
			MapSqlParameterSource params = new MapSqlParameterSource()
					.addValue(DashboardExtractorConstants.PARAM_TENANT_ID, tenantId)
					.addValue(DashboardExtractorConstants.PARAM_MODULE_NAME, moduleName)
					.addValue(DashboardExtractorConstants.PARAM_LIMIT, limit);
			return namedParameterJdbcTemplate.query(IngestionSummaryQueryBuilder.SELECT_PENDING_OR_FAILED_LEGACY_JOBS_QUERY,
					params, (resultSet, rowNumber) -> new LegacyJob(resultSet.getString("module_ingestion_id"),
							resultSet.getDate("push_date").toLocalDate()));
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
			MapSqlParameterSource params = new MapSqlParameterSource()
					.addValue(DashboardExtractorConstants.PARAM_TENANT_ID, tenantId)
					.addValue(DashboardExtractorConstants.PARAM_MODULE_NAME, moduleName);
			namedParameterJdbcTemplate.queryForList(IngestionSummaryQueryBuilder.SELECT_FOR_UPDATE_SUMMARY_QUERY, params);
			return true;
		} catch (Exception exception) {
			log.warn("IngestionSummaryRepository | Failed to acquire lock for tenant {} module {}", tenantId, moduleName, exception);
			return false;
		}
	}
}
