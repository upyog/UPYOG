package org.upyog.adapter.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.upyog.adapter.model.IngestionResult;
import org.upyog.adapter.service.DailyIngestionService;

/**
 * Controller for manually triggering and testing daily multi-module ingestion via REST API.
 */
@RestController
@RequestMapping("/api/v1/test")
public class IngestionTestController {

    @Autowired
    private DailyIngestionService service;

    /**
     * Manually triggers multi-module metrics extraction and ingestion.
     * 
     * @return ResponseEntity containing list of IngestionResult payloads and HTTP 200 OK
     */
    @GetMapping
    public ResponseEntity<List<IngestionResult>> pushData() {
        List<IngestionResult> results = service.ingestDailyData();
        return new ResponseEntity<>(results, HttpStatus.OK);
    }
}
