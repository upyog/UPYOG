package org.egov.loadgenerator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egov.loadgenerator.generator.ModuleGenerator;
import org.egov.loadgenerator.model.JobStatus;
import org.egov.loadgenerator.model.LoadRequest;
import org.egov.loadgenerator.registry.ModuleGeneratorRegistry;
import org.egov.loadgenerator.repository.JobStatusRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service responsible for managing the lifecycle of load generation jobs.
 *
 * <p>This service validates incoming requests, creates and persists job
 * metadata, delegates asynchronous execution to the worker, and provides
 * APIs for retrieving and deleting job information.
 *
 * <h3>Responsibilities</h3>
 * <ul>
 *   <li>Validate module requests.</li>
 *   <li>Create and persist load generation jobs.</li>
 *   <li>Trigger asynchronous load generation.</li>
 *   <li>Retrieve job execution status and history.</li>
 *   <li>Delete load-test job records.</li>
 * </ul>
 *
 * @see LoadGeneratorWorker
 * @see ModuleGeneratorRegistry
 * @see JobStatusRepository
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LoadGeneratorService {

    private final ModuleGeneratorRegistry registry;
    private final LoadGeneratorWorker loadGeneratorWorker;
    private final JobStatusRepository jobStatusRepository;

    /**
     * Creates a new load generation job and initiates asynchronous execution.
     *
     * <p>The request is validated, a unique job identifier is generated,
     * the initial job status is persisted, and the actual load generation
     * is delegated to the worker for asynchronous processing.
     *
     * @param request the load generation request
     * @return the initial job status containing the generated job identifier
     */
    public JobStatus create(LoadRequest request) {
        ModuleGenerator generator = registry.getGenerator(request.getModule());

        String jobId = UUID.randomUUID().toString();

        JobStatus jobStatus = JobStatus.builder()
                .jobId(jobId)
                .module(request.getModule().toUpperCase())
                .tenantId(request.getTenantId())
                .totalRecords(request.getCount())
                .successCount(0)
                .failureCount(0)
                .status("ACCEPTED")
                .startTimeMs(System.currentTimeMillis())
                .build();

        jobStatusRepository.save(jobStatus);

        // Fire and forget — runs in loadGeneratorExecutor thread pool
        loadGeneratorWorker.runAsync(generator, request, jobStatus);

        return jobStatus;
    }

    /**
     * Retrieves the execution status of a load generation job.
     *
     * @param jobId the unique job identifier
     * @return an {@link Optional} containing the job status if found;
     *         otherwise an empty Optional
     */
    public Optional<JobStatus> getStatus(String jobId) {
        return jobStatusRepository.findById(jobId);
    }

    /**
     * Retrieves recently executed load generation jobs.
     *
     * @return a list of job status records
     */
    public List<JobStatus> getAllJobs() {
        return jobStatusRepository.findAll();
    }

    /**
     * Deletes load-test job records for the specified module and tenant.
     *
     * <p>The module is validated before deletion. Only load generator
     * job records are removed; production application data remains
     * unaffected.
     *
     * @param module the target module name
     * @param tenantId the tenant identifier
     * @return the number of job records deleted
     */
    public int delete(String module, String tenantId) {
        registry.getGenerator(module); // validates module exists
        int deleted = jobStatusRepository.deleteByModuleAndTenant(module, tenantId);
        log.info("Deleted {} job records for module={}, tenantId={}", deleted, module, tenantId);
        return deleted;
    }
}
