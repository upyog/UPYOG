package org.upyog.adapter.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.upyog.adapter.common.constants.Module;
import org.upyog.adapter.model.LegacyIngestionResponse;
import org.upyog.adapter.service.LegacyIngestionService;

import lombok.extern.slf4j.Slf4j;

/**
 * Controller exposing endpoints to trigger bulk historical (legacy) metrics ingestion.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/legacy")
public class LegacyIngestionController {

    @Autowired
    private LegacyIngestionService legacyIngestionService;
    
    @Autowired
    private org.upyog.adapter.repository.IngestionSummaryRepository summaryRepository;

    /**
     * Retrieves the status of legacy jobs.
     *
     * @param tenantId DIGIT tenant ID
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
        log.info("LegacyIngestionController | Triggering historical ingestion from {} to {} for module {}",
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
        log.info("LegacyIngestionController | Triggering historical ingestion for last {} months for module {}",
                months, targetModule != null ? targetModule : "ALL");

        LegacyIngestionResponse response = legacyIngestionService.ingestHistoricalDataForLastMonths(months, targetModule);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private Module parseModule(String moduleStr) {
        if (moduleStr == null || moduleStr.isBlank()) {
            return null;
        }
        try {
            return Module.valueOf(moduleStr.trim().toUpperCase());
        } catch (Exception exception) {
            log.warn("LegacyIngestionController | Unknown module name '{}'. Will process all enabled modules.", moduleStr);
            return null;
        }
    }
}
