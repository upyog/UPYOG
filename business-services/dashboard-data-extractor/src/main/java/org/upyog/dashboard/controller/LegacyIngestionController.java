package org.upyog.dashboard.controller;

import org.apache.commons.lang3.StringUtils;
import org.upyog.dashboard.repository.IngestionSummaryRepository;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.model.LegacyIngestionResponse;
import org.upyog.dashboard.service.LegacyIngestionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.upyog.dashboard.service.LegacyBatchIngestionOrchestrator;
import org.upyog.dashboard.service.LegacyBatchIngestRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controller exposing endpoints to trigger bulk historical (legacy) metrics ingestion.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/legacy")
@RequiredArgsConstructor
public class LegacyIngestionController {

    private final LegacyIngestionService legacyIngestionService;
    private final LegacyBatchIngestionOrchestrator legacyBatchIngestionOrchestrator;
    private final IngestionSummaryRepository summaryRepository;

    /**
     * Retrieves the status of legacy jobs.
     *
     * @param tenantId tenant ID
     * @param moduleName module short code (exception.g. "PT")
     * @param limit maximum number of pending/failed jobs to return
     * @return ResponseEntity with a dataMap containing pending/failed jobs and registered dates
     */
    @org.springframework.web.bind.annotation.GetMapping("/jobs/status")
    public ResponseEntity<java.util.Map<String, Object>> getLegacyJobsStatus(
            @RequestParam String tenantId,
            @RequestParam String moduleName,
            @RequestParam(defaultValue = "100") int limit) {
        
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("pendingOrFailedJobs", summaryRepository.findPendingOrFailedLegacyJobs(tenantId, moduleName, limit));
        response.put("registeredDates", summaryRepository.findRegisteredLegacyJobDates(tenantId, moduleName));
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Triggers bulk historical ingestion for a custom date range.
     *
     * @param startDate start date (YYYY-MM-DD); defaults to 5 months ago if null
     * @param endDate end date (YYYY-MM-DD); defaults to yesterday if null
     * @param module optional module name filter (exception.g. "PT")
     * @return ResponseEntity with LegacyIngestionResponse
     */
    @PostMapping("/ingest")
    public ResponseEntity<LegacyIngestionResponse> ingestRange(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String module) {

        if (endDate == null) {
            endDate = LocalDate.now().minusDays(1);
        }
        if (startDate == null) {
            startDate = endDate.minusMonths(5).withDayOfMonth(1);
        }

        Module targetModule = parseModule(module);
        log.info("Triggering historical ingestion from {} to {} for module {}",
                startDate, endDate, targetModule != null ? targetModule : "ALL");

        LegacyIngestionResponse response = legacyIngestionService.ingestHistoricalDataForRange(startDate, endDate, targetModule);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Triggers bulk historical ingestion for the last N months up to yesterday.
     *
     * @param months number of months to look back (default: 5)
     * @param module optional module name filter (exception.g. "PT")
     * @return ResponseEntity with LegacyIngestionResponse
     */
    @PostMapping("/ingest/last-months")
    public ResponseEntity<LegacyIngestionResponse> ingestLastMonths(
            @RequestParam(defaultValue = "5") int months,
            @RequestParam(required = false) String module) {

        Module targetModule = parseModule(module);
        log.info("Triggering historical ingestion for last {} months for module {}",
                months, targetModule != null ? targetModule : "ALL");

        LegacyIngestionResponse response = legacyIngestionService.ingestHistoricalDataForLastMonths(months, targetModule);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Triggers legacy batch DB extraction, SXSSF streaming Excel generation, and downstream ingestion.
     *
     * @param request payload containing moduleName, startDate (YYYY-MM-DD), and endDate (YYYY-MM-DD)
     * @return ResponseEntity with LegacyIngestionResponse
     */
    @PostMapping("/batch-ingest")
    public ResponseEntity<LegacyIngestionResponse> batchIngest(@Valid @RequestBody LegacyBatchIngestRequest request) {
        log.info("Received request for legacy batch excel extraction & ingestion for module {} (date range: {} to {})",
                request.getModuleName(), request.getStartDate(), request.getEndDate());

        LegacyIngestionResponse response = legacyBatchIngestionOrchestrator.processLegacyBatchIngest(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Attempts to parse a module name string into the corresponding {@link Module} enum constant.
     * Returns {@code null} if the input is blank or does not match any known module name,
     * and logs a warning rather than throwing an exception.
     *
     * @param moduleStr the raw module name string from the request parameter; may be {@code null}
     * @return the matching {@link Module} constant, or {@code null} to signal all-modules processing
     */
    private Module parseModule(String moduleStr) {
        if (StringUtils.isBlank(moduleStr)) {
            return null;
        }
        try {
            return Module.valueOf(moduleStr.trim().toUpperCase());
        } catch (Exception exception) {
            log.warn("Unknown module name '{}'. Will process all enabled modules.", moduleStr);
            return null;
        }
    }
}
