package org.upyog.adapter.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.upyog.adapter.api.AdapterClient;
import org.upyog.adapter.common.constants.Module;
import org.upyog.adapter.config.SchemaMappingConfig;
import org.upyog.adapter.extractor.ModuleExtractor;
import org.upyog.adapter.model.AdapterRequest;
import org.upyog.adapter.model.DashboardData;
import org.upyog.adapter.model.IngestionResult;
import org.upyog.adapter.registry.ExtractorRegistry;
import org.upyog.adapter.repository.IngestionSummaryRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Service class that manages daily metrics extraction and ingestion for all
 * state-enabled modules.
 * 
 * <p>
 * Reads state-enabled modules from {@link SchemaMappingConfig#getEnabledModules()}
 * and routes data extraction dynamically to registered {@link ModuleExtractor}
 * implementations via {@link ExtractorRegistry}.
 *
 * <p>
 * Uses {@link IngestionSummaryRepository} to check the last successfully ingested date
 * for each module and performs catch-up ingestion for all missing dates up to yesterday
 * (excluding the last successful date, including yesterday).
 */
@Slf4j
@Service
public class DailyIngestionService {

	@Autowired
	private AdapterClient adapterClient;

	@Autowired
	private ExtractorRegistry extractorRegistry;

	@Autowired
	private SchemaMappingConfig schemaMappingConfig;

	@Autowired
	private IngestionSummaryRepository summaryRepository;

	@Value("${adapter.system.user.tenantId:pg}")
	private String tenantId;

	@Value("${adapter.ingestion.default-start-date:2026-06-01s}")
	private String defaultStartDateStr;

	/**
	 * Automatically checks the last successfully ingested date per enabled module
	 * and executes catch-up ingestion for all missing dates from (last_successful_date + 1)
	 * up to yesterday (inclusive).
	 * 
	 * @return List of IngestionResult records for all processed dates
	 */
	public List<IngestionResult> ingestDailyData() {
		List<IngestionResult> allResults = new ArrayList<>();
		List<Module> enabledModules = schemaMappingConfig.getEnabledModules();

		if (enabledModules.isEmpty()) {
			log.warn("DailyIngestionService | No modules enabled under extractor.enabled-modules in schema-mapping.yml");
			return allResults;
		}

		LocalDate yesterday = LocalDate.now().minusDays(1);
		LocalDate defaultStartDate = parseDefaultStartDate();

		for (Module module : enabledModules) {
			ModuleExtractor extractor = extractorRegistry.get(module);
			if (extractor == null) {
				log.error("DailyIngestionService | Enabled module {} has no registered ModuleExtractor bean", module);
				continue;
			}

			Optional<LocalDate> lastSuccessOpt = summaryRepository.findLastSuccessfulDate(tenantId, module.name());
			LocalDate startDate = lastSuccessOpt.map(d -> d.plusDays(1)).orElse(defaultStartDate);

			if (startDate.isAfter(yesterday)) {
				log.info("DailyIngestionService | Module {} is already up-to-date up to yesterday ({}). Skipping.",
						module, yesterday);
				allResults.add(IngestionResult.builder()
						.ingestionStatus("SKIPPED")
						.date(yesterday.toString())
						.failureReason("Module " + module.name() + " is already up-to-date up to yesterday (" + yesterday + ")")
						.ingestedAt(System.currentTimeMillis())
						.build());
				continue;
			}

			log.info("DailyIngestionService | Catching up module {} for date range: {} to {}",
					module, startDate, yesterday);

			LocalDate currentDate = startDate;
			while (!currentDate.isAfter(yesterday)) {
				IngestionResult result = ingestModuleForDate(module, extractor, currentDate);
				allResults.add(result);

				if ("SUCCESS".equalsIgnoreCase(result.getIngestionStatus())) {
					summaryRepository.saveOrUpdateLastSuccessfulDate(tenantId, module.name(), currentDate);
					currentDate = currentDate.plusDays(1);
				} else {
					log.warn("DailyIngestionService | Ingestion failed for module {} on date {}. Halting catch-up for subsequent dates.",
							module, currentDate);
					break;
				}
			}
		}

		return allResults;
	}

	/**
	 * Single-date ingestion trigger (used for manual test endpoints or specific date backfills).
	 * 
	 * @param targetDate the date to extract metrics for
	 * @return List of IngestionResult payloads for each enabled module
	 */
	public List<IngestionResult> ingestDailyData(LocalDate targetDate) {
		List<IngestionResult> results = new ArrayList<>();
		List<Module> enabledModules = schemaMappingConfig.getEnabledModules();

		if (enabledModules.isEmpty()) {
			log.warn("DailyIngestionService | No modules enabled under extractor.enabled-modules in schema-mapping.yml");
			return results;
		}

		for (Module module : enabledModules) {
			ModuleExtractor extractor = extractorRegistry.get(module);
			if (extractor == null) {
				log.error("DailyIngestionService | Enabled module {} has no registered ModuleExtractor bean", module);
				continue;
			}

			IngestionResult result = ingestModuleForDate(module, extractor, targetDate);
			results.add(result);

			if ("SUCCESS".equalsIgnoreCase(result.getIngestionStatus())) {
				summaryRepository.saveOrUpdateLastSuccessfulDate(tenantId, module.name(), targetDate);
			}
		}

		return results;
	}

	private IngestionResult ingestModuleForDate(Module module, ModuleExtractor extractor, LocalDate date) {
		try {
			DashboardData rawData = extractor.extractData(date);
			AdapterRequest request = AdapterRequest.builder().module(module).rawData(List.of(rawData)).build();

			IngestionResult result = adapterClient.execute(request);
			if (result != null && result.getDate() == null) {
				result.setDate(date.toString());
			}
			log.info("DailyIngestionService | Ingestion status for module {} on date {}: {}",
					module, date, result.getIngestionStatus());
			return result;
		} catch (Exception e) {
			log.error("DailyIngestionService | Ingestion failed for module {} on date {}", module, date, e);
			return IngestionResult.builder()
					.ingestionStatus("FAILURE")
					.failureReason(e.getMessage())
					.ingestedAt(System.currentTimeMillis())
					.date(date.toString())
					.build();
		}
	}

	private LocalDate parseDefaultStartDate() {
		try {
			if (defaultStartDateStr != null && !defaultStartDateStr.isBlank()) {
				return LocalDate.parse(defaultStartDateStr.trim());
			}
		} catch (Exception e) {
			log.warn("DailyIngestionService | Failed to parse defaultStartDateStr '{}'. Falling back to yesterday.", defaultStartDateStr, e);
		}
		return LocalDate.now().minusDays(1);
	}
}
