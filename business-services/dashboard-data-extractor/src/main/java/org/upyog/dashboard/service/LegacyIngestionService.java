package org.upyog.dashboard.service;

import org.upyog.dashboard.constants.DashboardExtractorConstants;
import org.apache.commons.lang3.StringUtils;
import org.upyog.dashboard.util.CommonUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.upyog.dashboard.api.DashboardClient;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.config.DashboardProperties;
import org.upyog.dashboard.config.SchemaMappingConfig;
import org.upyog.dashboard.extractor.ModuleExtractor;
import org.upyog.dashboard.model.DashboardRequest;
import org.upyog.dashboard.model.DashboardData;
import org.upyog.dashboard.model.IngestionResult;
import org.upyog.dashboard.model.LegacyIngestionResponse;
import org.upyog.dashboard.registry.ExtractorRegistry;
import org.upyog.dashboard.repository.IngestionSummaryRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service managing bulk historical (legacy) metrics ingestion for past date ranges.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LegacyIngestionService {

	private static final String STATUS_SUCCESS = DashboardExtractorConstants.STATUS_SUCCESS;
	private static final String STATUS_FAILURE = DashboardExtractorConstants.STATUS_FAILURE;

	private final DashboardClient dashboardClient;
	private final ExtractorRegistry extractorRegistry;
	private final SchemaMappingConfig schemaMappingConfig;
	private final IngestionSummaryRepository summaryRepository;
	private final ObjectMapper objectMapper;
	private final DashboardProperties dashboardProperties;

	private String tenantId;

	/**
	 * Initialises the tenant ID from {@link org.upyog.dashboard.config.DashboardProperties}
	 * after bean construction.
	 */
	@PostConstruct
	public void init() {
		this.tenantId = dashboardProperties.getTenantId();
	}

	/**
	 * Determines the start date for the look-back period and delegates to
	 * {@link #populateLegacyJobsForRange(LocalDate, LocalDate, Module)}.
	 *
	 * @param months       number of months to look back from yesterday (must be &gt; 0)
	 * @param targetModule the module to populate jobs for, or {@code null} for all enabled modules
	 * @return the number of new legacy job rows created
	 * @throws IllegalArgumentException if {@code months} is &le; 0
	 */
	public int populateLegacyJobs(int months, Module targetModule) {
		if (months <= 0) {
			throw new IllegalArgumentException("months are not declared for which legacy needs to run.");
		}
		LocalDate yesterday = LocalDate.now().minusDays(1);
		LocalDate startDate = yesterday.minusMonths(months).withDayOfMonth(1);
		return populateLegacyJobsForRange(startDate, yesterday, targetModule);
	}

	/**
	 * Iterates over every date in {@code [startDate, endDate]} for each module to process
	 * and inserts a pending legacy job row when the date has not yet been successfully
	 * ingested and is not already registered in the legacy queue.
	 *
	 * @param startDate    the inclusive start date of the range
	 * @param endDate      the inclusive end date of the range
	 * @param targetModule the module to populate jobs for, or {@code null} for all enabled modules
	 * @return the total number of new legacy job rows created across all modules
	 */
	public int populateLegacyJobsForRange(LocalDate startDate, LocalDate endDate, Module targetModule) {
		List<Module> modulesToProcess = getModulesToProcess(targetModule);
		int createdCount = 0;

		for (Module module : modulesToProcess) {
			Set<LocalDate> successfullyIngested = summaryRepository.findSuccessfullyIngestedDates(tenantId,
					module.name(), startDate, endDate);
			Set<LocalDate> registeredLegacyJobs = summaryRepository.findRegisteredLegacyJobDates(tenantId,
					module.name());

			LocalDate currentDate = startDate;
			while (!currentDate.isAfter(endDate)) {
				if (!successfullyIngested.contains(currentDate) && !registeredLegacyJobs.contains(currentDate)) {
					summaryRepository.createLegacyJob(CommonUtils.generateUUID(), tenantId, module.name(), currentDate, currentDate, currentDate);
					createdCount++;
				}
				currentDate = currentDate.plusDays(1);
			}
		}
		return createdCount;
	}

	/**
	 * Fetches up to {@code limit} pending or failed legacy jobs for each module to process
	 * and executes them by extracting data and calling the dashboard client.
	 * Updates the job status and the last-successful-date summary after each execution.
	 *
	 * @param limit        maximum number of jobs to process per module
	 * @param targetModule the module to execute jobs for, or {@code null} for all enabled modules
	 * @return the total number of legacy jobs executed across all modules
	 */
	public int executeLegacyJobs(int limit, Module targetModule) {
		List<Module> modulesToProcess = getModulesToProcess(targetModule);
		int executedCount = 0;

		for (Module module : modulesToProcess) {
			ModuleExtractor<?> extractor = extractorRegistry.get(module);
			if (extractor == null) {
				log.error("Module {} has no registered ModuleExtractor", module);
				continue;
			}

			List<IngestionSummaryRepository.LegacyJob> pendingJobs = summaryRepository
					.findPendingOrFailedLegacyJobs(tenantId, module.name(), limit);

			Optional<LocalDate> currentLastSuccessOpt = summaryRepository.findLastSuccessfulDate(tenantId,
					module.name());

			for (IngestionSummaryRepository.LegacyJob job : pendingJobs) {
				LocalDate date = job.getPushDate();
				summaryRepository.saveOrUpdateLastAttemptedDate(tenantId, module.name(), date);

				IngestionResult result = processLegacyJob(extractor, module, date);

				String requestJson = serializeRequest(module, extractor, date);
				String responseJson = sanitizeResponse(result);

				summaryRepository.updateLegacyJobStatus(job.getJobId(), result.getIngestionStatus(), requestJson, responseJson);

				if (STATUS_SUCCESS.equals(result.getIngestionStatus())) {
					if (currentLastSuccessOpt.isEmpty() || date.isAfter(currentLastSuccessOpt.get())) {
						summaryRepository.saveOrUpdateLastSuccessfulDate(tenantId, module.name(), date);
						currentLastSuccessOpt = Optional.of(date);
					}
				}

				executedCount++;
			}
		}
		return executedCount;
	}

	/**
	 * Executes the ingestion pipeline for a single legacy job: extracts raw data via
	 * the module extractor, builds a {@link org.upyog.dashboard.model.DashboardRequest},
	 * and submits it to the dashboard client.
	 *
	 * @param extractor the {@link ModuleExtractor} for the given module
	 * @param module    the module being processed
	 * @param date      the push date of the legacy job
	 * @return an {@link org.upyog.dashboard.model.IngestionResult} describing success or failure
	 */
	private IngestionResult processLegacyJob(ModuleExtractor<?> extractor, Module module, LocalDate date) {
		try {
			Object rawData = extractor.extractData(date);
			Object requestRawData = rawData instanceof DashboardData dashboardData ? List.of(dashboardData) : rawData;
			DashboardRequest request = DashboardRequest.builder().module(module).rawData(requestRawData).build();

			IngestionResult result = dashboardClient.execute(request);
			if (result != null && result.getDate() == null) {
				result.setDate(date.toString());
			}
			log.info("Ingested legacy module {} date {}: status {}", module, date, result != null ? result.getIngestionStatus() : null);
			return result;
		} catch (Exception exception) {
			log.error("Ingestion error for legacy module {} date {}", module, date, exception);
			return IngestionResult.builder()
					.ingestionStatus(STATUS_FAILURE)
					.failureReason(exception.getMessage())
					.ingestedAt(CommonUtils.getCurrentEpochMillis())
					.moduleName(module.name())
					.date(date.toString())
					.build();
		}
	}

	/**
	 * Extracts data for the given module and date and serialises the resulting
	 * {@link org.upyog.dashboard.model.DashboardRequest} to a JSON string for audit storage.
	 * Returns an empty JSON object string ({@code "{}"}) on any error.
	 *
	 * @param module    the module being processed
	 * @param extractor the {@link ModuleExtractor} for the module
	 * @param date      the date for which data should be serialised
	 * @return a JSON string representation of the request payload, or {@code "{}"} on error
	 */
	private String serializeRequest(Module module, ModuleExtractor<?> extractor, LocalDate date) {
		try {
			Object rawData = extractor.extractData(date);
			Object requestRawData = rawData instanceof DashboardData dashboardData ? List.of(dashboardData) : rawData;
			DashboardRequest request = DashboardRequest.builder().module(module).rawData(requestRawData).build();
			return objectMapper.writeValueAsString(request);
		} catch (Exception e) {
			return "{}";
		}
	}

	/**
	 * Extracts a safe JSON string from an {@link org.upyog.dashboard.model.IngestionResult} for
	 * storage as the {@code response_data} audit field. Falls back to the failure reason when
	 * response data is absent. Returns {@code "{}"} for a {@code null} result.
	 *
	 * @param result the ingestion result to sanitise; may be {@code null}
	 * @return a valid JSON string representing the response or failure information
	 */
	private String sanitizeResponse(IngestionResult result) {
		if (result == null) return "{}";
		String data = result.getResponseData() != null ? result.getResponseData() : result.getFailureReason();
		return sanitizeJson(data);
	}

	/**
	 * Convenience method that calculates the start date as the first day of the month
	 * {@code months} months ago and delegates to
	 * {@link #ingestHistoricalDataForRange(LocalDate, LocalDate, Module)}.
	 *
	 * @param months       number of months to look back from yesterday (must be &gt; 0)
	 * @param targetModule the module to ingest, or {@code null} for all enabled modules
	 * @return a {@link LegacyIngestionResponse} summarising the ingestion outcome
	 * @throws IllegalArgumentException if {@code months} is &le; 0
	 */
	public LegacyIngestionResponse ingestHistoricalDataForLastMonths(int months, Module targetModule) {
		if (months <= 0) {
			throw new IllegalArgumentException("months are not declared for which legacy needs to run.");
		}
		LocalDate yesterday = LocalDate.now().minusDays(1);
		LocalDate startDate = yesterday.minusMonths(months).withDayOfMonth(1);
		return ingestHistoricalDataForRange(startDate, yesterday, targetModule);
	}

	/**
	 * Populates pending legacy jobs and immediately executes them for the specified date range
	 * and module. Returns a {@link LegacyIngestionResponse} summarising the total number of
	 * dates requested and how many were successfully ingested.
	 *
	 * @param startDate    the inclusive start date (must not be {@code null} or after {@code endDate})
	 * @param endDate      the inclusive end date (must not be {@code null})
	 * @param targetModule the module to ingest, or {@code null} for all enabled modules
	 * @return a {@link LegacyIngestionResponse} summarising the outcome
	 * @throws IllegalArgumentException if the date range is invalid
	 */
	public LegacyIngestionResponse ingestHistoricalDataForRange(LocalDate startDate, LocalDate endDate,
			Module targetModule) {
		if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
			throw new IllegalArgumentException("Invalid date range: startDate must be on or before endDate");
		}

		populateLegacyJobsForRange(startDate, endDate, targetModule);
		executeLegacyJobs(10000, targetModule);

		Set<LocalDate> successfullyIngested = summaryRepository.findSuccessfullyIngestedDates(tenantId,
				(targetModule != null ? targetModule.name() : "PT"), startDate, endDate);

		return LegacyIngestionResponse.builder()
				.totalDatesRequested((int) (java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1))
				.datesSkipped(0).datesProcessedSuccessfully(successfullyIngested.size()).datesFailed(0)
				.skippedDates(List.of()).processedResults(List.of()).build();
	}

	/**
	 * Returns the list of modules to process. When {@code targetModule} is non-null only that
	 * module is returned; otherwise all modules enabled in
	 * {@link org.upyog.dashboard.config.SchemaMappingConfig} are returned.
	 *
	 * @param targetModule a specific module, or {@code null} to process all enabled modules
	 * @return an immutable list of modules to process; never {@code null}
	 */
	private List<Module> getModulesToProcess(Module targetModule) {
		if (targetModule != null) {
			return List.of(targetModule);
		}
		List<Module> enabled = schemaMappingConfig.getEnabledModules();
		return (enabled != null && !enabled.isEmpty()) ? enabled : List.of();
	}

	/**
	 * Ensures that the given string is a valid JSON object or array before returning it.
	 * Strings that are blank or not valid JSON are wrapped in a JSON error object.
	 *
	 * @param input the raw string to sanitise; may be {@code null}
	 * @return a valid JSON string; never {@code null}
	 */
	private String sanitizeJson(String input) {
		if (StringUtils.isBlank(input)) {
			return "{}";
		}
		try {
			JsonNode node = objectMapper.readTree(input);
			if (node != null && (node.isObject() || node.isArray())) {
				return input;
			}
		} catch (Exception exception) {
			log.debug("Input is not valid JSON, wrapping as error object: {}", exception.getMessage());
		}
		try {
			return objectMapper.writeValueAsString(java.util.Map.of("error", input));
		} catch (Exception exception) {
			return "{\"error\":\"" + input.replace("\"", "\\\"").replace("\\n", " ") + "\"}";
		}
	}
}