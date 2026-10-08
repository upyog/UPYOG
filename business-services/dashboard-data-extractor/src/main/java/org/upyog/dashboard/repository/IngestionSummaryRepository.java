package org.upyog.dashboard.repository;

import org.upyog.dashboard.constants.DashboardExtractorConstants;
import org.upyog.dashboard.service.IngestionPersistenceService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.upyog.dashboard.model.IngestionModuleDetail;
import org.upyog.dashboard.model.IngestionSchedulerDetail;
import org.upyog.dashboard.repository.querybuilder.IngestionSummaryQueryBuilder;
import org.upyog.dashboard.util.CommonUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Spring Repository managing persistence operations for
 * {@code ug_ingestion_module_summary}.
 *
 * <p>
 * Tracks and updates the last successfully ingested date per tenant and module.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
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
	 * Retrieves a map of tenant ID to last successful date for all tenants under a module in a single bulk query.
	 *
	 * @param moduleName the module short code (e.g. "PT")
	 * @return map of tenantId to last successful LocalDate
	 */
	public Map<String, LocalDate> findAllLastSuccessfulDatesByModule(String moduleName) {
		Map<String, LocalDate> resultMap = new HashMap<>();
		try {
			MapSqlParameterSource params = new MapSqlParameterSource()
					.addValue(DashboardExtractorConstants.PARAM_MODULE_NAME, moduleName);
			namedParameterJdbcTemplate.query(
					IngestionSummaryQueryBuilder.SELECT_ALL_LAST_SUCCESSFUL_DATES_FOR_MODULE_QUERY,
					params,
					(rs, rowNum) -> {
						String tId = rs.getString("tenant_id");
						Date dt = rs.getDate("last_successful_date");
						if (tId != null && dt != null && !dt.toLocalDate().equals(LocalDate.EPOCH)) {
							resultMap.put(tId, dt.toLocalDate());
						}
						return null;
					});
		} catch (Exception exception) {
			log.error("IngestionSummaryRepository | Failed to query all last successful dates for module {}", moduleName, exception);
		}
		return resultMap;
	}

	/**
	 * Retrieves all tenant IDs that have already successfully ingested metrics for the specified target date.
	 *
	 * @param moduleName the module short code (e.g. "PT")
	 * @param targetDate the date to check
	 * @return set of tenant IDs that already succeeded on or up to targetDate
	 */
	public Set<String> findTenantsSuccessfullyIngestedForDate(String moduleName, LocalDate targetDate) {
		Set<String> resultSet = new HashSet<>();
		try {
			MapSqlParameterSource params = new MapSqlParameterSource()
					.addValue(DashboardExtractorConstants.PARAM_MODULE_NAME, moduleName)
					.addValue("targetDate", Date.valueOf(targetDate));
			List<String> tenants = namedParameterJdbcTemplate.queryForList(
					IngestionSummaryQueryBuilder.SELECT_TENANTS_SUCCESSFULLY_INGESTED_FOR_DATE_QUERY,
					params,
					String.class);
			if (tenants != null) {
				resultSet.addAll(tenants);
			}
		} catch (Exception exception) {
			log.error("IngestionSummaryRepository | Failed to query completed tenants for module {} on date {}",
					moduleName, targetDate, exception);
		}
		return resultSet;
	}

	/**
	 * Queries all dates within the specified date range that have already been
	 * successfully ingested (via daily or legacy pipelines) for the given tenant
	 * and module.
	 *
	 * @param tenantId   the tenant identifier
	 * @param moduleName the module short code
	 * @param startDate  the start of the date range (inclusive)
	 * @param endDate    the end of the date range (inclusive)
	 * @return a {@link Set} of {@link LocalDate} instances representing successful ingest dates
	 */
	public Set<LocalDate> findSuccessfullyIngestedDates(String tenantId, String moduleName,
			LocalDate startDate, LocalDate endDate) {
		Set<LocalDate> result = new HashSet<>();
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
	 * Checks whether legacy ingestion has completed successfully for the given tenant and module.
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
			Integer count = namedParameterJdbcTemplate.queryForObject(
					IngestionSummaryQueryBuilder.CHECK_LEGACY_INGESTION_COMPLETE_QUERY, params, Integer.class);
			return count != null && count > 0;
		} catch (Exception exception) {
			log.error("IngestionSummaryRepository | Failed to check isLegacyIngestionComplete for tenant {} module {}", tenantId, moduleName, exception);
			return false;
		}
	}

	/**
	 * Updates the {@code ug_ingestion_module_detail} table, marking legacy ingestion as
	 * completed and setting the last ingested date.
	 *
	 * @param tenantId   the tenant identifier
	 * @param moduleName the module short code
	 * @param lastDate   the latest date ingested
	 */
	public void markLegacyIngestionComplete(String tenantId, String moduleName, LocalDate lastDate) {
		try {
			long now = CommonUtils.getCurrentEpochMillis();
			MapSqlParameterSource params = new MapSqlParameterSource()
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
	 * Persists or updates the last attempted ingestion date for a batch of tenants and module.
	 *
	 * @param tenantIds      the list of tenant identifiers
	 * @param moduleName     the module short code
	 * @param attemptedDate  the date of the attempted ingestion
	 */
	public void saveOrUpdateLastAttemptedDatesBatch(List<String> tenantIds, String moduleName, LocalDate attemptedDate) {
		persistenceService.saveOrUpdateLastAttemptedDatesBatch(tenantIds, moduleName, attemptedDate);
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
	 * {@code ug_legacy_data_ingestion_detail}. Carries the unique job identifier and the
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

	/**
	 * Upserts a list of {@link IngestionModuleDetail} records into the database.
	 *
	 * @param moduleDetails list of module details to save or update
	 */
	public void upsertModuleDetails(List<IngestionModuleDetail> moduleDetails) {
		if (moduleDetails == null || moduleDetails.isEmpty()) {
			return;
		}
		try {
			long now = CommonUtils.getCurrentEpochMillis();
			MapSqlParameterSource[] batchParams = new MapSqlParameterSource[moduleDetails.size()];
			for (int index = 0; index < moduleDetails.size(); index++) {
				IngestionModuleDetail detail = moduleDetails.get(index);
				String detailId = detail.getDetailId();
				if (detailId == null || detailId.isBlank()) {
					detailId = UUID.nameUUIDFromBytes((detail.getTenantId() + ":" + detail.getModuleName()).getBytes()).toString();
				}
				batchParams[index] = new MapSqlParameterSource()
						.addValue("detailId", detailId)
						.addValue("tenantId", detail.getTenantId())
						.addValue("moduleName", detail.getModuleName())
						.addValue("isActive", detail.isActive())
						.addValue("createdBy", detail.getCreatedBy() != null ? detail.getCreatedBy() : DashboardExtractorConstants.SYSTEM_USER)
						.addValue("createdTime", detail.getCreatedTime() != null ? detail.getCreatedTime() : now)
						.addValue("lastModifiedBy", detail.getLastModifiedBy() != null ? detail.getLastModifiedBy() : DashboardExtractorConstants.SYSTEM_USER)
						.addValue("lastModifiedTime", now);
			}
			namedParameterJdbcTemplate.batchUpdate(IngestionSummaryQueryBuilder.UPSERT_MODULE_DETAIL_QUERY, batchParams);
			log.info("Successfully upserted {} records into ug_ingestion_module_detail", moduleDetails.size());
		} catch (Exception exception) {
			log.error("IngestionSummaryRepository | Failed to batch upsert ug_ingestion_module_detail records", exception);
			throw new RuntimeException("Failed to upsert module details: " + exception.getMessage(), exception);
		}
	}

	/**
	 * Deletes all records from {@code ug_ingestion_module_detail}.
	 */
	public void deleteAllModuleDetails() {
		try {
			namedParameterJdbcTemplate.update(
					IngestionSummaryQueryBuilder.DELETE_ALL_MODULE_DETAILS_QUERY, new MapSqlParameterSource());
			log.info("IngestionSummaryRepository | Cleared all existing records from ug_ingestion_module_detail");
		} catch (Exception exception) {
			log.error("IngestionSummaryRepository | Failed to clear ug_ingestion_module_detail records", exception);
			throw new RuntimeException("Failed to delete existing module details: " + exception.getMessage(), exception);
		}
	}

	/**
	 * Atomically replaces all {@code ug_ingestion_module_detail} records by clearing the table and inserting the new list.
	 *
	 * @param moduleDetails the new list of module details to persist
	 */
	@Transactional
	public void replaceAllModuleDetails(List<IngestionModuleDetail> moduleDetails) {
		deleteAllModuleDetails();
		if (moduleDetails != null && !moduleDetails.isEmpty()) {
			upsertModuleDetails(moduleDetails);
		}
	}

	/**
	 * Checks whether any records currently exist in the {@code ug_ingestion_module_detail} table.
	 *
	 * @return {@code true} if table has at least one record, {@code false} otherwise
	 */
	public boolean hasAnyModuleDetails() {
		try {
			Integer count = namedParameterJdbcTemplate.queryForObject(
					IngestionSummaryQueryBuilder.COUNT_ALL_MODULE_DETAILS_QUERY,
					new MapSqlParameterSource(),
					Integer.class);
			return count != null && count > 0;
		} catch (Exception exception) {
			log.error("IngestionSummaryRepository | Failed to count ug_ingestion_module_detail records", exception);
			return false;
		}
	}

	/**
	 * Retrieves distinct active tenant IDs configured for a specific module.
	 *
	 * @param moduleName module short code (e.g. "PGR", "PT")
	 * @return list of active tenant IDs
	 */
	public List<String> findActiveTenantsByModule(String moduleName) {
		try {
			MapSqlParameterSource params = new MapSqlParameterSource()
					.addValue(DashboardExtractorConstants.PARAM_MODULE_NAME, moduleName);
			return namedParameterJdbcTemplate.queryForList(
					IngestionSummaryQueryBuilder.SELECT_ACTIVE_TENANTS_BY_MODULE_QUERY, params, String.class);
		} catch (Exception exception) {
			log.error("IngestionSummaryRepository | Failed to find active tenants for module {}", moduleName, exception);
			return List.of();
		}
	}

	/**
	 * Retrieves all distinct active tenant IDs across all modules.
	 *
	 * @return list of active tenant IDs
	 */
	public List<String> findAllActiveTenants() {
		try {
			return namedParameterJdbcTemplate.queryForList(
					IngestionSummaryQueryBuilder.SELECT_ALL_ACTIVE_TENANTS_QUERY, new MapSqlParameterSource(), String.class);
		} catch (Exception exception) {
			log.error("IngestionSummaryRepository | Failed to find all active tenants", exception);
			return List.of();
		}
	}

	/**
	 * Retrieves all active {@link IngestionModuleDetail} records.
	 *
	 * @return list of active module details
	 */
	public List<IngestionModuleDetail> findAllActiveModuleDetails() {
		try {
			return namedParameterJdbcTemplate.query(
					IngestionSummaryQueryBuilder.SELECT_ALL_ACTIVE_MODULE_DETAILS_QUERY,
					new MapSqlParameterSource(),
					(rs, rowNum) -> IngestionModuleDetail.builder()
							.detailId(rs.getString("detail_id"))
							.tenantId(rs.getString("tenant_id"))
							.moduleName(rs.getString("module_name"))
							.active(rs.getBoolean("is_active"))
							.build());
		} catch (Exception exception) {
			log.error("IngestionSummaryRepository | Failed to find all active module details", exception);
			return List.of();
		}
	}

	/**
	 * Creates a new scheduler execution record in {@code ug_ingestion_scheduler_detail}.
	 *
	 * @param schedulerDetail the scheduler detail entity to persist
	 */
	public void createSchedulerRun(IngestionSchedulerDetail schedulerDetail) {
		if (schedulerDetail == null) {
			return;
		}
		try {
			long now = CommonUtils.getCurrentEpochMillis();
			MapSqlParameterSource params = new MapSqlParameterSource()
					.addValue("schedulerId", schedulerDetail.getSchedulerId())
					.addValue("schedulerName", schedulerDetail.getSchedulerName())
					.addValue("cronExpression", schedulerDetail.getCronExpression())
					.addValue("startTime", schedulerDetail.getStartTime() != null ? schedulerDetail.getStartTime() : now)
					.addValue("status", schedulerDetail.getStatus() != null ? schedulerDetail.getStatus() : "RUNNING")
					.addValue("totalRecordsProcessed", schedulerDetail.getTotalRecordsProcessed() != null ? schedulerDetail.getTotalRecordsProcessed() : 0)
					.addValue("successRecordsCount", schedulerDetail.getSuccessRecordsCount() != null ? schedulerDetail.getSuccessRecordsCount() : 0)
					.addValue("failureRecordsCount", schedulerDetail.getFailureRecordsCount() != null ? schedulerDetail.getFailureRecordsCount() : 0)
					.addValue("createdBy", schedulerDetail.getCreatedBy() != null ? schedulerDetail.getCreatedBy() : DashboardExtractorConstants.SYSTEM_USER)
					.addValue("createdTime", schedulerDetail.getCreatedTime() != null ? schedulerDetail.getCreatedTime() : now)
					.addValue("lastModifiedBy", schedulerDetail.getLastModifiedBy() != null ? schedulerDetail.getLastModifiedBy() : DashboardExtractorConstants.SYSTEM_USER)
					.addValue("lastModifiedTime", now);

			namedParameterJdbcTemplate.update(IngestionSummaryQueryBuilder.INSERT_SCHEDULER_DETAIL_QUERY, params);
			log.info("IngestionSummaryRepository | Created scheduler run record: {} ({})",
					schedulerDetail.getSchedulerId(), schedulerDetail.getSchedulerName());
		} catch (Exception exception) {
			log.error("IngestionSummaryRepository | Failed to create scheduler run record {}", schedulerDetail.getSchedulerId(), exception);
		}
	}

	/**
	 * Updates an existing scheduler execution record upon completion or failure.
	 *
	 * @param schedulerId    the unique scheduler execution ID
	 * @param startTime      the epoch milliseconds when the scheduler started
	 * @param totalRecords   total count of ingestion records processed
	 * @param successRecords count of successful ingestion records
	 * @param failureRecords count of failed ingestion records
	 * @param status         final execution status (e.g. COMPLETED or FAILED)
	 * @param errorMessage   error message if failed, or null
	 */
	public void completeSchedulerRun(String schedulerId, long startTime, int totalRecords, int successRecords, int failureRecords,
			String status, String errorMessage) {
		if (schedulerId == null) {
			return;
		}
		try {
			long now = CommonUtils.getCurrentEpochMillis();
			long duration = (startTime > 0) ? (now - startTime) : 0;
			MapSqlParameterSource params = new MapSqlParameterSource()
					.addValue("schedulerId", schedulerId)
					.addValue("endTime", now)
					.addValue("durationMs", duration)
					.addValue("status", status)
					.addValue("totalRecordsProcessed", totalRecords)
					.addValue("successRecordsCount", successRecords)
					.addValue("failureRecordsCount", failureRecords)
					.addValue("errorMessage", errorMessage)
					.addValue("lastModifiedBy", DashboardExtractorConstants.SYSTEM_USER)
					.addValue("lastModifiedTime", now);

			namedParameterJdbcTemplate.update(IngestionSummaryQueryBuilder.UPDATE_SCHEDULER_DETAIL_QUERY, params);
			log.info("IngestionSummaryRepository | Completed scheduler run record: {} with status: {} in {}ms",
					schedulerId, status, duration);
		} catch (Exception exception) {
			log.error("IngestionSummaryRepository | Failed to complete scheduler run record {}", schedulerId, exception);
		}
	}
}

