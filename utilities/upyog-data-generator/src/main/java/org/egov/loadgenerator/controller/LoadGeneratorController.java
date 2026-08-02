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

/**
 * REST controller exposing APIs for managing load generation jobs.
 *
 * <p>This controller serves as the primary entry point for clients interacting
 * with the Load Generator application. It provides endpoints to start new
 * load generation jobs, monitor their execution status, retrieve recently
 * executed jobs, and delete load generator metadata.
 *
 * <p>The controller delegates all business logic to
 * {@link LoadGeneratorService}, limiting its responsibility to request
 * handling, response construction, and HTTP status management.
 *
 * <h3>Supported Operations</h3>
 * <ul>
 *   <li>Create a new asynchronous load generation job.</li>
 *   <li>Retrieve the status of a specific job.</li>
 *   <li>Retrieve recently executed load generation jobs.</li>
 *   <li>Delete load generator job records for a module and tenant.</li>
 * </ul>
 *
 * <h3>Execution Model</h3>
 * <p>Load generation requests are processed asynchronously. The create API
 * immediately returns a unique job identifier while execution continues in
 * the background. Clients can use the status APIs to monitor job progress,
 * throughput, and completion status.
 *
 * @see LoadGeneratorService
 * @see LoadRequest
 * @see JobStatus
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class LoadGeneratorController {

    private final LoadGeneratorService loadGeneratorService;

    /**
     * Creates a new asynchronous load generation job.
     *
     * <p>The request contains the target module, tenant identifier, and
     * the number of records to generate. After successfully scheduling the
     * job, the API immediately returns a unique job identifier without
     * waiting for execution to complete.
     *
     * @param request the load generation request containing the module,
     *                tenant identifier, and record count
     * @return a response containing the generated job identifier,
     *         initial job status, and tracking information
     */
    @PostMapping("/create")
    public ResponseEntity<LoadGeneratorResponse> create(@RequestBody LoadRequest request) {
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
     * Retrieves the current status of a previously submitted load generation
     * job.
     *
     * <p>If a job with the specified identifier exists, the response includes
     * the latest execution status, progress information, throughput, and
     * other runtime details. If no matching job is found, the API returns
     * HTTP 404 (Not Found).
     *
     * @param jobId the unique identifier of the load generation job
     * @return the current job status if found; otherwise HTTP 404
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
     * Retrieves the most recently executed load generation jobs.
     *
     * <p>This endpoint provides a quick overview of recent load generation
     * activity, allowing clients to inspect the latest job executions
     * without querying each job individually.
     *
     * @return a list of recently executed load generation jobs
     */
    @GetMapping("/status")
    public ResponseEntity<List<JobStatus>> getAllJobs() {
        return ResponseEntity.ok(loadGeneratorService.getAllJobs());
    }

    /**
     * Deletes load generator job records for the specified module and tenant.
     *
     * <p>This operation removes only the metadata maintained by the Load
     * Generator application. It does <strong>not</strong> delete any business
     * data created in the target eGov module.
     *
     * @param module the target module whose job records should be removed
     * @param tenantId the tenant identifier associated with the jobs
     * @return a message indicating the number of deleted job records
     */
    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam String module,
                                         @RequestParam String tenantId) {
        int deleted = loadGeneratorService.delete(module, tenantId);

        return ResponseEntity.ok("Deleted " + deleted + " job records for module=" + module
                + ", tenantId=" + tenantId);
    }
}
