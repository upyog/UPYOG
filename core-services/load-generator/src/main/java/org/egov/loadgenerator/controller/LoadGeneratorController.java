package org.egov.loadgenerator.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egov.loadgenerator.model.JobStatus;
import org.egov.loadgenerator.model.LoadRequest;
import org.egov.loadgenerator.model.response.LoadGeneratorResponse;
import org.egov.loadgenerator.service.LoadGeneratorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LoadGeneratorController {

    private final LoadGeneratorService loadGeneratorService;

    /**
     * POST /load-generator/create
     * Accepts { "module": "PGR", "tenantId": "pb.amritsar", "count": 100000 }
     * Returns jobId immediately. Execution runs in background.
     */
    @PostMapping("/create")
    public ResponseEntity<LoadGeneratorResponse> create(@Valid @RequestBody LoadRequest request) {
        log.info("Load generation request received: module={}, tenantId={}, count={}",
                request.getModule(), request.getTenantId(), request.getCount());

        JobStatus jobStatus = loadGeneratorService.create(request);

        LoadGeneratorResponse response = LoadGeneratorResponse.builder()
                .jobId(jobStatus.getJobId())
                .message("Load generation started. Use /status/" + jobStatus.getJobId() + " to track progress.")
                .status(jobStatus)
                .build();

        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    /**
     * GET /load-generator/status/{jobId}
     * Returns current status, progress, throughput, ETA.
     */
    @GetMapping("/status/{jobId}")
    public ResponseEntity<LoadGeneratorResponse> getStatus(@PathVariable String jobId) {
        return loadGeneratorService.getStatus(jobId)
        .map(status -> ResponseEntity.ok(
                LoadGeneratorResponse.builder()
                        .jobId(jobId)
                        .status(status)
                        .build()))
        .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * GET /load-generator/status
     * Returns last 50 jobs.
     */
    @GetMapping("/status")
    public ResponseEntity<List<JobStatus>> getAllJobs() {
        return ResponseEntity.ok(loadGeneratorService.getAllJobs());
    }

    /**
     * DELETE /load-generator/delete?module=PGR&tenantId=pb.amritsar
     * Removes only load-test job records. Does NOT delete actual module data.
     */
    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam String module,
                                         @RequestParam String tenantId) {
        int deleted = loadGeneratorService.delete(module, tenantId);
        return ResponseEntity.ok("Deleted " + deleted + " job records for module=" + module
                + ", tenantId=" + tenantId);
    }
}
