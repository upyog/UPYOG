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
 * Core parallel execution engine.
 * Uses Project Reactor (WebFlux) for non-blocking concurrent HTTP calls.
 * Handles retry, metrics tracking, and progress reporting.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LoadExecutor {

    private final WebClient webClient;
    private final LoadGeneratorConfig config;
    private final ObjectMapper objectMapper;

    /**
     * Executes all requests concurrently and updates the JobStatus in-place.
     */
    /**
     * Starts parallel load generation and updates job metrics.
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
     * Fetches the latest property state required for the next workflow action.
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
     * Performs a workflow transition such as VERIFY, FORWARD or APPROVE.
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
     * Returns true only for retryable server or connection errors.
     */
    private boolean isRetryable(Throwable throwable) {
        String msg = throwable.getMessage();
        // Retry on 5xx or connection errors, not on 4xx
        return msg != null && (msg.contains("500") || msg.contains("502")
                || msg.contains("503") || msg.contains("Connection"));
    }

    /**
     * Calculates final job metrics and updates the job status.
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
