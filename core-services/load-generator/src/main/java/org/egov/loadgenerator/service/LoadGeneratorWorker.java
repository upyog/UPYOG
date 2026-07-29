package org.egov.loadgenerator.service;

import lombok.RequiredArgsConstructor;
import org.egov.loadgenerator.executor.LoadExecutor;
import org.egov.loadgenerator.generator.ModuleGenerator;
import org.egov.loadgenerator.model.JobStatus;
import org.egov.loadgenerator.model.LoadRequest;
import org.egov.loadgenerator.repository.JobStatusRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Asynchronous worker responsible for executing load generation jobs.
 *
 * <p>This service delegates load generation to the {@link LoadExecutor}
 * using a dedicated asynchronous executor. After execution completes,
 * the latest job status is persisted regardless of whether the execution
 * succeeds or fails.
 *
 * <h3>Responsibilities</h3>
 * <ul>
 *   <li>Execute load generation asynchronously.</li>
 *   <li>Delegate record processing to the {@link LoadExecutor}.</li>
 *   <li>Persist the final job status after execution.</li>
 * </ul>
 *
 * @see LoadExecutor
 * @see JobStatusRepository
 */
@Service
@RequiredArgsConstructor
public class LoadGeneratorWorker {

    private final LoadExecutor loadExecutor;
    private final JobStatusRepository jobStatusRepository;

    /**
     * Executes a load generation job asynchronously.
     *
     * <p>The execution is delegated to the configured load generator
     * thread pool. Regardless of the execution outcome, the latest
     * job status is persisted to the database.
     *
     * @param generator the module-specific payload generator
     * @param request the load generation request
     * @param jobStatus the job status being tracked
     */
    @Async("loadGeneratorExecutor")
    public void runAsync(ModuleGenerator generator,
                         LoadRequest request,
                         JobStatus jobStatus) {
        try {
            loadExecutor.execute(
                    generator,
                    request.getTenantId(),
                    request.getCount(),
                    jobStatus);
        } finally {
            jobStatusRepository.update(jobStatus);
        }
    }
}
