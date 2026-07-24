package org.egov.loadgenerator.service;

import lombok.RequiredArgsConstructor;
import org.egov.loadgenerator.executor.LoadExecutor;
import org.egov.loadgenerator.generator.ModuleGenerator;
import org.egov.loadgenerator.model.JobStatus;
import org.egov.loadgenerator.model.LoadRequest;
import org.egov.loadgenerator.repository.JobStatusRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoadGeneratorWorker {

    private final LoadExecutor loadExecutor;
    private final JobStatusRepository jobStatusRepository;

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
