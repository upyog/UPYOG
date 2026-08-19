package org.upyog.dashboard.service;

import org.upyog.dashboard.util.CommonUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.upyog.dashboard.api.DashboardClient;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.config.SchemaMappingConfig;
import org.upyog.dashboard.extractor.ModuleExtractor;
import org.upyog.dashboard.model.DashboardRequest;
import org.upyog.dashboard.model.DashboardData;
import org.upyog.dashboard.model.IngestionResult;
import org.upyog.dashboard.registry.ExtractorRegistry;
import org.upyog.dashboard.repository.IngestionSummaryRepository;
import org.upyog.dashboard.config.DashboardProperties;
import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class that manages daily metrics extraction and ingestion for all
 * state-enabled modules.
 *
 * <p>This service iterates over enabled modules, determines the appropriate
 * start date for catch‑up ingestion, and delegates data extraction to the
 * registered {@link ModuleExtractor}s. It persists ingestion progress using
 * {@link IngestionSummaryRepository} and builds {@link IngestionResult} objects
 * that are returned to callers.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DailyIngestionService {

	private static final String STATUS_SUCCESS = "SUCCESS";
	private static final String STATUS_FAILURE = "FAILURE";

	private final DashboardClient dashboardClient;
	private final ExtractorRegistry extractorRegistry;
	private final SchemaMappingConfig schemaMappingConfig;
	private final IngestionSummaryRepository summaryRepository;
	private final DashboardProperties dashboardProperties;

	private String tenantId;
	private String defaultStartDateStr;

	/**
	 * Initialises service-level configuration values from {@link DashboardProperties}.
	 * This method is called after bean construction.
	 */
	@PostConstruct
	public void init() {
		this.tenantId = dashboardProperties.getTenantId();
		this.defaultStartDateStr = dashboardProperties.getDefaultStartDateStr();
	}

	/**
 * Executes daily ingestion for all enabled modules using the default
 * date range (yesterday). The method determines the appropriate start date
 * for each module based on previous successful runs.
 *
 * @return a list of {@link IngestionResult} objects representing the outcome
 *         of each module's ingestion attempt.
 */
public List<IngestionResult> ingestDailyData() {
		List<IngestionResult> allResults = new ArrayList<>();
		List<Module> enabledModules = schemaMappingConfig.getEnabledModules();

		if (enabledModules.isEmpty()) {
			log.warn("No modules enabled under extractor.enabled-modules in schema-mapping.yml");
			return allResults;
		}

		LocalDate yesterday = LocalDate.now().minusDays(1);
		LocalDate defaultStartDate = parseDefaultStartDate();

		for (Module module : enabledModules) {
			ModuleExtractor<?> extractor = extractorRegistry.get(module);
			if (extractor == null) {
				log.error("Enabled module {} has no registered ModuleExtractor bean", module);
				continue;
			}

			Optional<LocalDate> lastSuccessOpt = summaryRepository.findLastSuccessfulDate(tenantId, module.name());
			LocalDate startDate = lastSuccessOpt.map(date -> date.plusDays(1)).orElse(defaultStartDate);

			if (startDate.isAfter(yesterday)) {
				log.info("Module {} is already up-to-date up to yesterday ({}). Skipping.", module, yesterday);
				allResults.add(buildResult("SKIPPED", module, yesterday, "Module " + module.name() + " is already up-to-date up to yesterday (" + yesterday + ")", null));
				continue;
			}

			long daysToIngest = java.time.temporal.ChronoUnit.DAYS.between(startDate, yesterday) + 1;
			int catchUpLimit = dashboardProperties.getDailyCatchUpLimitDays();
			if (daysToIngest > catchUpLimit) {
				log.error("Catch-up gap of {} days exceeds max limit of {} days for module {}. Please use legacy migration.", daysToIngest, catchUpLimit, module);
				allResults.add(buildResult(STATUS_FAILURE, module, yesterday, "Catch-up gap of " + daysToIngest + " days exceeds max limit of " + catchUpLimit + " days. Use legacy migration endpoint.", null));
				continue;
			}

			log.info("Catching up module {} for date range: {} to {}", module, startDate, yesterday);
			processDateRange(module, extractor, startDate, yesterday, allResults);
		}

		return allResults;
	}

	/**
 * Iterates over a date range for a single module, invoking ingestion for each
 * date until either all dates are processed or a failure occurs.
 *
 * @param module      the module being processed
 * @param extractor   the {@link ModuleExtractor} responsible for data extraction
 * @param startDate   the inclusive start date for ingestion
 * @param yesterday   the exclusive upper bound (typically yesterday's date)
 * @param allResults  collection to which each {@link IngestionResult} is added
 */
private void processDateRange(Module module, ModuleExtractor<?> extractor, LocalDate startDate, LocalDate yesterday, List<IngestionResult> allResults) {
		LocalDate currentDate = startDate;
		while (!currentDate.isAfter(yesterday)) {
			summaryRepository.saveOrUpdateLastAttemptedDate(tenantId, module.name(), currentDate);
			IngestionResult result = ingestModuleForDate(module, extractor, currentDate);
			allResults.add(result);

			if (STATUS_SUCCESS.equalsIgnoreCase(result.getIngestionStatus())) {
				summaryRepository.saveOrUpdateLastSuccessfulDate(tenantId, module.name(), currentDate);
				currentDate = currentDate.plusDays(1);
			} else {
				log.warn("Ingestion failed for module {} on date {}. Halting catch-up for subsequent dates.", module, currentDate);
				break;
			}
		}
	}

	/**
 * Executes ingestion for all enabled modules for a specific target date.
 * This is primarily used for back‑fill or on‑demand ingestion scenarios.
 *
 * @param targetDate the date for which data should be ingested
 * @return a list of {@link IngestionResult} objects representing the outcome
 *         of each module's ingestion attempt.
 */
public List<IngestionResult> ingestDailyData(LocalDate targetDate) {
		List<IngestionResult> results = new ArrayList<>();
		List<Module> enabledModules = schemaMappingConfig.getEnabledModules();

		if (enabledModules.isEmpty()) {
			log.warn("No modules enabled under extractor.enabled-modules in schema-mapping.yml");
			return results;
		}

		for (Module module : enabledModules) {
			ModuleExtractor<?> extractor = extractorRegistry.get(module);
			if (extractor == null) {
				log.error("Enabled module {} has no registered ModuleExtractor bean", module);
				continue;
			}

			summaryRepository.saveOrUpdateLastAttemptedDate(tenantId, module.name(), targetDate);
			IngestionResult result = ingestModuleForDate(module, extractor, targetDate);
			results.add(result);

			if (STATUS_SUCCESS.equalsIgnoreCase(result.getIngestionStatus())) {
				summaryRepository.saveOrUpdateLastSuccessfulDate(tenantId, module.name(), targetDate);
			}
		}

		return results;
	}

	/**
 * Performs ingestion for a single module on a specific date.
 * It extracts raw data via the {@link ModuleExtractor}, normalises the payload,
 * and delegates to the appropriate processing method.
 *
 * @param module   the module to ingest
 * @param extractor the extractor implementation for the module
 * @param date     the date for which data should be ingested
 * @return an {@link IngestionResult} describing success or failure
 */
private IngestionResult ingestModuleForDate(Module module, ModuleExtractor<?> extractor, LocalDate date) {
		try {
			Object rawData = extractor.extractData(date);
			if (rawData instanceof DashboardData) {
				rawData = List.of((DashboardData) rawData);
			}
			
			if (rawData instanceof List) {
				return processDataList(module, (List<?>) rawData, date);
			}

			return processSingleData(module, rawData, date);
		} catch (Exception exception) {
			log.error("Ingestion failed for module {} on date {}", module, date, exception);
			return buildResult(STATUS_FAILURE, module, date, exception.getMessage(), null);
		}
	}

	/**
 * Processes a list of extracted items for a module.
 * Aggregates success status, captures the first successful response payload
 * and concatenates any failure reasons.
 *
 * @param module   the module being processed
 * @param dataList the list of extracted data items
 * @param date     the ingestion date
 * @return an {@link IngestionResult} reflecting overall success or failure
 */
private IngestionResult processDataList(Module module, List<?> dataList, LocalDate date) {
		if (dataList.isEmpty()) {
			log.info("No data found for module {} on date {}", module, date);
			return buildResult(STATUS_SUCCESS, module, date, null, null);
		}

		boolean allSuccess = true;
		StringBuilder errors = new StringBuilder();
		String responseData = null;

		for (Object item : dataList) {
			IngestionResult result = executeIngestion(module, item, date);
			if (result != null && STATUS_SUCCESS.equalsIgnoreCase(result.getIngestionStatus())) {
				if (responseData == null && result.getResponseData() != null) {
					responseData = result.getResponseData();
				}
			} else {
				allSuccess = false;
				if (result != null && result.getFailureReason() != null) {
					errors.append(result.getFailureReason()).append("; ");
				}
			}
		}

		if (allSuccess) {
			log.info("Ingestion status for module {} on date {}: SUCCESS", module, date);
			return buildResult(STATUS_SUCCESS, module, date, null, responseData);
		} else {
			log.error("Partial/Full Ingestion failed for module {} on date {}: {}", module, date, errors.toString());
			return buildResult(STATUS_FAILURE, module, date, errors.toString(), null);
		}
	}

	/**
 * Executes the dashboard client for a single extracted item.
 * Utilises Java 16 pattern matching to wrap {@link DashboardData} items in a
 * singleton list.
 *
 * @param module the module associated with the item
 * @param item   the extracted payload (either a {@link DashboardData} or any other object)
 * @param date   the ingestion date
 * @return the {@link IngestionResult} returned by the client
 */
private IngestionResult executeIngestion(Module module, Object item, LocalDate date) {
		Object payloadItem = item instanceof DashboardData dashboardData ? List.of(dashboardData) : item;
		DashboardRequest request = DashboardRequest.builder().module(module).rawData(payloadItem).build();
		log.info("Executing dashboardClient for item: {}", item);
		IngestionResult result = dashboardClient.execute(request);
		if (result != null && result.getDate() == null) {
			result.setDate(date.toString());
		}
		return result;
	}

	/**
 * Processes a single data item (non‑list) for ingestion.
 *
 * @param module   the module being processed
 * @param rawData  the raw payload extracted by the module extractor
 * @param date     the ingestion date
 * @return the {@link IngestionResult} from the dashboard client
 */
private IngestionResult processSingleData(Module module, Object rawData, LocalDate date) {
		DashboardRequest request = DashboardRequest.builder().module(module).rawData(rawData).build();
		IngestionResult result = dashboardClient.execute(request);
		if (result != null && result.getDate() == null) {
			result.setDate(date.toString());
		}
		log.info("Ingestion status for module {} on date {}: {}", module, date, result != null ? result.getIngestionStatus() : null);
		return result;
	}

	/**
 * Helper to construct a standardized {@link IngestionResult} instance.
 *
 * @param status        ingestion status (e.g., SUCCESS or FAILURE)
 * @param module        the module for which the result is built
 * @param date          the ingestion date
 * @param failureReason optional failure reason message; may be {@code null}
 * @param responseData  optional response payload from the client; may be {@code null}
 * @return a fully populated {@link IngestionResult}
 */
private IngestionResult buildResult(String status, Module module, LocalDate date, String failureReason, String responseData) {
		return IngestionResult.builder()
				.ingestionStatus(status)
				.date(date.toString())
				.moduleName(module.name())
				.failureReason(failureReason)
				.responseData(responseData)
				.ingestedAt(CommonUtils.getCurrentEpochMillis())
				.build();
	}

	/**
 * Parses the default start date configuration value. If the value is
 * missing, blank, or unparsable, the method falls back to yesterday's date.
 *
 * @return the configured start date or yesterday as a fallback
 */
private LocalDate parseDefaultStartDate() {
		try {
			if (defaultStartDateStr != null && !defaultStartDateStr.isBlank()) {
				return LocalDate.parse(defaultStartDateStr.trim());
			}
		} catch (Exception exception) {
			log.warn("Failed to parse defaultStartDateStr '{}'. Falling back to yesterday.", defaultStartDateStr, exception);
		}
		return LocalDate.now().minusDays(1);
	}
}
