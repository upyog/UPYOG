package org.egov.loadgenerator.executor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egov.loadgenerator.config.LoadGeneratorConfig;
import org.egov.loadgenerator.generator.ModuleGenerator;
import org.egov.loadgenerator.model.JobStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;

/**
 * Core execution engine responsible for performing concurrent load generation
 * against eGov services.
 *
 * <p>This component orchestrates the complete request execution lifecycle using
 * Project Reactor and Spring WebFlux. It generates module-specific payloads,
 * invokes target APIs concurrently, applies configurable retry policies,
 * executes post-creation workflows, and continuously updates execution metrics
 * for the associated load generation job.
 *
 * <h3>Responsibilities</h3>
 * <ul>
 *   <li>Execute concurrent HTTP requests using reactive streams.</li>
 *   <li>Apply configurable retry policies for transient failures.</li>
 *   <li>Execute module workflow transitions after successful resource creation.</li>
 *   <li>Track execution metrics such as success count, failure count,
 *       throughput, and average response time.</li>
 *   <li>Update the associated {@link JobStatus} throughout the execution
 *       lifecycle.</li>
 * </ul>
 *
 * <h3>Execution Flow</h3>
 * <ol>
 *   <li>Generate a module-specific request payload.</li>
 *   <li>Invoke the module Create API.</li>
 *   <li>Execute required workflow transitions (Search → Update).</li>
 *   <li>Record execution metrics.</li>
 *   <li>Finalize the job after all requests complete.</li>
 * </ol>
 *
 * <h3>Concurrency Model</h3>
 * <p>Concurrency is controlled by the configured thread pool size exposed by
 * {@link LoadGeneratorConfig}. The executor uses non-blocking reactive
 * processing to maximize throughput while minimizing thread usage.
 *
 * @see ModuleGenerator
 * @see JobStatus
 * @see WebClient
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LoadExecutor {

    private final WebClient webClient;
    private final LoadGeneratorConfig config;
    private final ObjectMapper objectMapper;

 /**
 * Executes the requested load generation job.
 *
 * <p>The method creates the configured number of asynchronous requests,
 * executes them concurrently, tracks runtime metrics, and updates the
 * supplied {@link JobStatus} as execution progresses.
 *
 * <p>Each request performs the complete business workflow including payload
 * generation, resource creation, workflow transitions, retry handling,
 * and metric collection.
 *
 * @param generator module-specific payload generator
 * @param tenantId target tenant identifier
 * @param count total number of requests to execute
 * @param jobStatus job status object updated throughout execution
 */
    public void execute(ModuleGenerator generator, String tenantId, int count, JobStatus jobStatus) {

        // Tracks successful workflow executions
        AtomicInteger successCount = new AtomicInteger(0);
        // Tracks failed workflow executions
        AtomicInteger failureCount = new AtomicInteger(0);
        // Accumulates response time for average calculation
        AtomicLong totalResponseTimeMs = new AtomicLong(0);

        jobStatus.setStatus("RUNNING");
        jobStatus.setStartTimeMs(System.currentTimeMillis());

        log.info("Starting load generation: module={}, tenantId={}, count={}, jobId={}",
                generator.getModuleName(), tenantId, count, jobStatus.getJobId());

        // Build a Flux of indices, map each to an async HTTP call, run concurrently
        Flux.range(0, count)
                .flatMap(index -> callWithRetry(generator, tenantId, index, successCount, failureCount, totalResponseTimeMs, jobStatus),
                        config.getThreadPoolSize()) // concurrency = thread pool size
                .doOnComplete(() -> finalizeJob(jobStatus, successCount, failureCount, totalResponseTimeMs, count))
                .doOnError(e -> {
                    log.error("Fatal error in load executor for job {}: {}", jobStatus.getJobId(), e.getMessage());
                    jobStatus.setStatus("FAILED");
                    jobStatus.setErrorSummary(e.getMessage());
                })
                .blockLast(); // block until all done (called from async @Async thread)
    }

 /**
 * Executes a single load generation request with retry support.
 *
 * <p>The request payload is generated by the supplied
 * {@link ModuleGenerator}. After successfully creating the resource,
 * the complete workflow is executed by performing the required search
 * and update operations. Retry policies are applied to recover from
 * transient server or connectivity failures.
 *
 * @param generator module-specific request generator
 * @param tenantId target tenant identifier
 * @param index request sequence number
 * @param successCount successful request counter
 * @param failureCount failed request counter
 * @param totalResponseTimeMs cumulative response time
 * @param jobStatus current job status
 * @return a reactive completion signal for the request execution
 */
    private Mono<Void> callWithRetry(ModuleGenerator generator, String tenantId, int index,
                                     AtomicInteger successCount, AtomicInteger failureCount,
                                     AtomicLong totalResponseTimeMs, JobStatus jobStatus) {
        Object payload = generator.buildPayload(tenantId, index);
        log.info("Payload for index {}: {}", index, payload);
        long callStart = System.currentTimeMillis();

        // Create a new property
        return webClient.post()
                .uri(generator.getCreateApiUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
        .onStatus(
        HttpStatusCode::isError,
        response -> response.bodyToMono(String.class)
                .flatMap(body -> {
                    log.error("PT Error Response: {}", body);
                    return Mono.error(new RuntimeException(body));
                })
            )
        .bodyToMono(String.class)
                .retryWhen(Retry.backoff(config.getMaxRetryAttempts(), Duration.ofMillis(config.getRetryDelayMs()))
                        .filter(this::isRetryable)
                        .onRetryExhaustedThrow((spec, signal) -> signal.failure()))
                .flatMap(response -> {

    log.info("CREATE RESPONSE = {}", response);

    try {

        Map<String, Object> requestMap =
                (Map<String, Object>) payload;

        Map<String, Object> requestInfo =
                (Map<String, Object>) requestMap.get("RequestInfo");

        if (requestInfo == null) {
            return Mono.error(new RuntimeException("RequestInfo is missing in payload"));
        }

        // Parse CREATE API response
        Map<String, Object> responseMap =
                objectMapper.readValue(response, Map.class);

        List<Map<String, Object>> properties =
        (List<Map<String, Object>>) responseMap.get("Properties");

// Ensure CREATE response contains a property before continuing
        if (properties == null || properties.isEmpty()) {
            log.error("CREATE returned no Properties. Response={}", response);
            return Mono.error(new RuntimeException("CREATE returned no Properties"));
        }

        Map<String, Object> createdProperty = properties.get(0);

String propertyId = (String) createdProperty.get("propertyId");
String tenantId1 = (String) createdProperty.get("tenantId");

// Execute workflow and retry when SEARCH temporarily returns no data
        return Mono.defer(() ->

    searchProperty(propertyId, tenantId1,
            generator.getSearchApiUrl(), requestInfo)

        .flatMap(property ->
            updateProperty(property, "VERIFY",
                    generator.getUpdateApiUrl(), requestInfo))

        .then(searchProperty(propertyId, tenantId1,
                generator.getSearchApiUrl(), requestInfo))

        .flatMap(property ->
            updateProperty(property, "FORWARD",
                    generator.getUpdateApiUrl(), requestInfo))

        .then(searchProperty(propertyId, tenantId1,
                generator.getSearchApiUrl(), requestInfo))

        .flatMap(property ->
            updateProperty(property, "APPROVE",
                    generator.getUpdateApiUrl(), requestInfo))

)

.retryWhen(
    Retry.fixedDelay(2, Duration.ofSeconds(2))
         .filter(e -> e.getMessage() != null &&
                 e.getMessage().contains("SEARCH returned no Properties"))
)

.doOnSuccess(r -> {
    log.info("Workflow Completed");
    successCount.incrementAndGet();
    totalResponseTimeMs.addAndGet(System.currentTimeMillis() - callStart);

    if (index % 1000 == 0) {
        log.info("Job progress: index={}, success={}, failure={}",
                index, successCount.get(), failureCount.get());
    }
})

.thenReturn(response);

    } catch (Exception e) {
        return Mono.error(e);
    }
})
                .doOnError(e -> {
    failureCount.incrementAndGet();
    log.error("Request failed at index {} - {}", index, e.getMessage(), e);
    jobStatus.setErrorSummary(e.getMessage());
})
                .onErrorResume(e -> Mono.empty())
                .then();
    }

 /**
 * Retrieves the latest property state from the Property service.
 *
 * <p>Workflow transitions require the latest persisted version of the
 * property before performing the next action. This method invokes the
 * Property Search API and returns the first matching property.
 *
 * @param propertyId property identifier
 * @param tenantId tenant identifier
 * @param url Property Search API endpoint
 * @param requestInfo request metadata required by the API
 * @return a {@link Mono} containing the latest property details
 */
    private Mono<Map<String, Object>> searchProperty(String propertyId,
                                                 String tenantId,
                                                 String url,
                                                 Map<String, Object> requestInfo) {

    log.info("SEARCH URL = {}", url);
    log.info("REQUEST INFO = {}", requestInfo);

    Map<String, Object> payload = new HashMap<>();
    payload.put("RequestInfo", requestInfo);

    return webClient.post()
            .uri(url + "?tenantId=" + tenantId + "&propertyIds=" + propertyId)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(payload)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .map(response -> {
                List<Map<String, Object>> properties =
                        (List<Map<String, Object>>) response.get("Properties");
                log.info("Response: {}", response);
                log.info("Properties: {}", properties);
                if (properties == null || properties.isEmpty()) {
                    throw new RuntimeException("SEARCH returned no Properties: " + response);
                }
                return properties.get(0);
            });
}

 /**
 * Executes a workflow transition for an existing property.
 *
 * <p>The supplied property is enriched with workflow information and
 * submitted to the Property Update API. Typical workflow actions include
 * VERIFY, FORWARD, and APPROVE.
 *
 * @param property latest property state
 * @param action workflow action to execute
 * @param url Property Update API endpoint
 * @param requestInfo request metadata
 * @return a {@link Mono} containing the update API response
 */
    private Mono<String> updateProperty(Map<String, Object> property,
                                    String action,
                                    String url,
                                    Map<String, Object> requestInfo) {

    Map<String, Object> workflow = new HashMap<>();
    workflow.put("action", action);
    workflow.put("businessService", "PT.CREATE");
    workflow.put("moduleName", "PT");

    if ("APPROVE".equals(action)) {
        workflow.put("comment", "ok");
        workflow.put("assignees", List.of());
    } else {
        workflow.put("comment", "");
        workflow.put("assignees", List.of(
    Map.of(
        "uuid", ((Map<String,Object>)requestInfo.get("userInfo")).get("uuid")
    )
));
    }

    property.put("workflow", workflow);
    property.remove("auditDetails");
//property.remove("status");
//property.remove("additionalDetails");
    Map<String, Object> newRequestInfo = new HashMap<>(requestInfo);
    newRequestInfo.put("action", "_update");
    Map<String,Object> payload = new HashMap<>();
    payload.put("Property", property);
    payload.put("RequestInfo", newRequestInfo);

log.info("{} UPDATE PAYLOAD = {}", action, payload);

   return webClient.post()
        .uri(url)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(payload)
        .retrieve()
        .onStatus(
                HttpStatusCode::isError,
                response -> response.bodyToMono(String.class)
                        .flatMap(body -> {
                            log.error("{} FAILED : {}", action, body);
                            return Mono.error(new RuntimeException(body));
                        })
        )
        .bodyToMono(String.class)
        .doOnSuccess(r -> log.info("{} SUCCESS : {}", action, r));
}

 /**
 * Determines whether a failed request should be retried.
 *
 * <p>Only transient failures such as server-side (5xx) responses and
 * connection-related exceptions are considered retryable. Client-side
 * validation errors are intentionally excluded.
 *
 * @param throwable the exception that caused the failure
 * @return {@code true} if the request should be retried;
 *         {@code false} otherwise
 */
    private boolean isRetryable(Throwable throwable) {
        String msg = throwable.getMessage();
        // Retry on 5xx or connection errors, not on 4xx
        return msg != null && (msg.contains("500") || msg.contains("502")
                || msg.contains("503") || msg.contains("Connection"));
    }

 /**
 * Finalizes the load generation job after all requests have completed.
 *
 * <p>This method calculates aggregate execution statistics including
 * throughput, average response time, success count, failure count,
 * and overall job status before updating the supplied
 * {@link JobStatus} instance.
 *
 * @param jobStatus job being finalized
 * @param successCount total successful requests
 * @param failureCount total failed requests
 * @param totalResponseTimeMs cumulative response time
 * @param total total number of requested executions
 */
    private void finalizeJob(JobStatus jobStatus, AtomicInteger successCount,
                             AtomicInteger failureCount, AtomicLong totalResponseTimeMs, int total) {
        long endTime = System.currentTimeMillis();
        long durationMs = endTime - jobStatus.getStartTimeMs();

        jobStatus.setEndTimeMs(endTime);
        jobStatus.setSuccessCount(successCount.get());
        jobStatus.setFailureCount(failureCount.get());
        jobStatus.setStatus(successCount.get() == 0 ? "FAILED" : (failureCount.get() > 0 ? "PARTIAL" : "COMPLETED"));

        double throughput = durationMs > 0 ? (successCount.get() * 1000.0) / durationMs : 0;
        double avgResponseTime = successCount.get() > 0
                ? (double) totalResponseTimeMs.get() / successCount.get() : 0;

        jobStatus.setThroughputPerSec(Math.round(throughput * 100.0) / 100.0);
        jobStatus.setAvgResponseTimeMs(Math.round(avgResponseTime * 100.0) / 100.0);

        log.info("Job {} completed: total={}, success={}, failure={}, throughput={} req/s, avgResponseTime={}ms",
                jobStatus.getJobId(), total, successCount.get(), failureCount.get(),
                jobStatus.getThroughputPerSec(), jobStatus.getAvgResponseTimeMs());
    }
}
