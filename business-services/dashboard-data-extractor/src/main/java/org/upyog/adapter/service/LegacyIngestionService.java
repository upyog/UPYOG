package org.upyog.adapter.service;

import org.upyog.adapter.producer.AdapterProducer;

import org.upyog.adapter.util.CommonUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.adapter.api.AdapterClient;
import org.upyog.adapter.common.constants.Module;
import org.upyog.adapter.config.AdapterProperties;
import org.upyog.adapter.config.SchemaMappingConfig;
import org.upyog.adapter.extractor.ModuleExtractor;
import org.upyog.adapter.model.AdapterRequest;
import org.upyog.adapter.model.DashboardData;
import org.upyog.adapter.model.IngestionResult;
import org.upyog.adapter.model.LegacyIngestionResponse;
import org.upyog.adapter.registry.ExtractorRegistry;
import org.upyog.adapter.repository.IngestionSummaryRepository;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * Service managing bulk historical (legacy) metrics ingestion for past date
 * ranges.
 *
 * <p>
 * Includes deduplication logic to ensure dates that have already been
 * successfully ingested for a tenant/module are skipped without re-pushing
 * duplicate metrics.
 */
@Slf4j
@Service
public class LegacyIngestionService {

	@Autowired
	private AdapterClient adapterClient;

	@Autowired
	private ExtractorRegistry extractorRegistry;

	@Autowired
	private SchemaMappingConfig schemaMappingConfig;

	@Autowired
	private IngestionSummaryRepository summaryRepository;

	@Autowired
	private AdapterProducer producer;

	@Autowired
	private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

	@Autowired
	private AdapterProperties adapterProperties;

	private String tenantId;

	@PostConstruct
	public void init() {
		this.tenantId = adapterProperties.getTenantId();
	}

	/**
	 * Scheduler 1 helper: Checks for missing dates/jobs and creates them in the
	 * table.
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
	 * Populates legacy jobs for a custom range.
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
					summaryRepository.createLegacyJob(tenantId, module.name(), currentDate);
					createdCount++;
				}
				currentDate = currentDate.plusDays(1);
			}
		}
		return createdCount;
	}

	/**
	 * Scheduler 2 helper: Fetches pending or failed legacy jobs and executes them.
	 */
	public int executeLegacyJobs(int limit, Module targetModule) {
		List<Module> modulesToProcess = getModulesToProcess(targetModule);
		int executedCount = 0;

		for (Module module : modulesToProcess) {
			ModuleExtractor<?> extractor = extractorRegistry.get(module);
			if (extractor == null) {
				log.error("LegacyIngestionService | Module {} has no registered ModuleExtractor", module);
				continue;
			}

			List<IngestionSummaryRepository.LegacyJob> pendingJobs = summaryRepository
					.findPendingOrFailedLegacyJobs(tenantId, module.name(), limit);

			Optional<LocalDate> currentLastSuccessOpt = summaryRepository.findLastSuccessfulDate(tenantId,
					module.name());

			for (IngestionSummaryRepository.LegacyJob job : pendingJobs) {
				LocalDate date = job.getPushDate();

				// Mark attempted date in summary
				summaryRepository.saveOrUpdateLastAttemptedDate(tenantId, module.name(), date);

				// Run ingestion
				long now = CommonUtils.getCurrentEpochMillis();
				Object rawData = null;
				IngestionResult result = null;
				String requestJson = "{}";
				String responseJson = "{}";

				try {
					rawData = extractor.extractData(date);
					Object requestRawData = rawData;
					if (rawData instanceof DashboardData) {
						requestRawData = List.of((DashboardData) rawData);
					}
					AdapterRequest request = AdapterRequest.builder().module(module).rawData(requestRawData).build();
					try {
						requestJson = objectMapper.writeValueAsString(request);
					} catch (Exception ignored) {
					}

					result = adapterClient.execute(request);
					if (result != null && result.getDate() == null) {
						result.setDate(date.toString());
					}
					log.info("LegacyIngestionService | Ingested legacy module {} date {}: status {}", module, date,
							result.getIngestionStatus());
					responseJson = result.getResponseData() != null ? sanitizeJson(result.getResponseData())
							: sanitizeJson(result.getFailureReason());

				} catch (Exception exception) {
					log.error("LegacyIngestionService | Ingestion error for legacy module {} date {}", module, date, exception);
					responseJson = sanitizeJson(exception.getMessage());
					result = IngestionResult.builder().ingestionStatus("FAILURE").failureReason(exception.getMessage())
							.ingestedAt(now).moduleName(module.name()).date(date.toString()).build();
				}

				// Update legacy job table directly
				summaryRepository.updateLegacyJobStatus(job.getJobId(), result.getIngestionStatus(), requestJson,
						responseJson);

				// Update summary tracker on success if date is newer than recorded success date
				if ("SUCCESS".equalsIgnoreCase(result.getIngestionStatus())) {
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
	 * Backward-compatible endpoint trigger: last N months lookback backfill.
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
	 * Backward-compatible endpoint trigger: custom date range backfill.
	 */
	public LegacyIngestionResponse ingestHistoricalDataForRange(LocalDate startDate, LocalDate endDate,
			Module targetModule) {
		if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
			throw new IllegalArgumentException("Invalid date range: startDate must be on or before endDate");
		}

		// Step 1: Populate pending jobs
		populateLegacyJobsForRange(startDate, endDate, targetModule);

		// Step 2: Execute pending jobs immediately
		int totalExpectedCount = 10000; // Batch limit representing custom trigger size
		executeLegacyJobs(totalExpectedCount, targetModule);

		// Step 3: Read result summaries from repository
		Set<LocalDate> successfullyIngested = summaryRepository.findSuccessfullyIngestedDates(tenantId,
				(targetModule != null ? targetModule.name() : "PT"), startDate, endDate);

		return LegacyIngestionResponse.builder()
				.totalDatesRequested((int) (java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1))
				.datesSkipped(0).datesProcessedSuccessfully(successfullyIngested.size()).datesFailed(0)
				.skippedDates(List.of()).processedResults(List.of()).build();
	}

	private List<Module> getModulesToProcess(Module targetModule) {
		if (targetModule != null) {
			return List.of(targetModule);
		}
		List<Module> enabled = schemaMappingConfig.getEnabledModules();
		return (enabled != null && !enabled.isEmpty()) ? enabled : List.of();
	}

	private String sanitizeJson(String input) {
		if (input == null || input.isBlank()) {
			return "{}";
		}
		try {
			com.fasterxml.jackson.databind.JsonNode node = objectMapper.readTree(input);
			if (node != null && (node.isObject() || node.isArray())) {
				return input;
			}
		} catch (Exception ignored) {
		}
		try {
			return objectMapper.writeValueAsString(java.util.Map.of("error", input));
		} catch (Exception exception) {
			return "{\"error\":\"" + input.replace("\"", "\\\"").replace("\n", " ") + "\"}";
		}
	}
}
