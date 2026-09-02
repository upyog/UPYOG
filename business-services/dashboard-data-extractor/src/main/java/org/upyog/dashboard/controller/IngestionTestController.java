package org.upyog.dashboard.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.upyog.dashboard.model.IngestionResult;
import org.upyog.dashboard.service.DailyIngestionService;

import lombok.RequiredArgsConstructor;

/**
 * Controller for manually triggering and testing daily multi-module ingestion via REST API.
 */
@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class IngestionTestController {

    private final DailyIngestionService service;

    /**
     * Manually triggers multi-module metrics extraction and ingestion.
     * Option to pass a date parameter to execute extraction, transformation, and ingestion for a specific date.
     * 
     * @param date optional target date (YYYY-MM-DD) for single-date ingestion
     * @return ResponseEntity containing dataList of IngestionResult payloads and HTTP 200 OK
     */
    @GetMapping
    public ResponseEntity<List<IngestionResult>> pushData(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<IngestionResult> results = (date != null)
                ? service.ingestDailyData(date)
                : service.ingestDailyData();
        return new ResponseEntity<>(results, HttpStatus.OK);
    }
}
