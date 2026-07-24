package org.egov.loadgenerator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egov.loadgenerator.executor.LoadExecutor;
import org.egov.loadgenerator.generator.ModuleGenerator;
import org.egov.loadgenerator.model.JobStatus;
import org.egov.loadgenerator.model.LoadRequest;
import org.egov.loadgenerator.registry.ModuleGeneratorRegistry;
import org.egov.loadgenerator.repository.JobStatusRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoadGeneratorService {

    private final ModuleGeneratorRegistry registry;
    private final LoadGeneratorWorker loadGeneratorWorker;
    private final JobStatusRepository jobStatusRepository;

    /**
     * Validates request, creates a job, fires async execution, returns jobId immediately.
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

    public Optional<JobStatus> getStatus(String jobId) {
        return jobStatusRepository.findById(jobId);
    }

    public List<JobStatus> getAllJobs() {
        return jobStatusRepository.findAll();
    }

    /**
     * Deletes only load-test generated data for a given module + tenant.
     * Does NOT touch production data.
     */
    public int delete(String module, String tenantId) {
        registry.getGenerator(module); // validates module exists
        int deleted = jobStatusRepository.deleteByModuleAndTenant(module, tenantId);
        log.info("Deleted {} job records for module={}, tenantId={}", deleted, module, tenantId);
        return deleted;
    }
}
