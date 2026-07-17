package org.upyog.adapter.loader;

import org.upyog.adapter.model.DashboardPayload;
import org.upyog.adapter.model.IngestionResult;

/**
 * Strategy interface for pushing a normalized {@link DashboardPayload} to a
 * downstream target (e.g. the National Dashboard ingest endpoint).
 *
 * <p>The adapter-service supports multiple loader strategies.  The active
 * implementation is selected by which Spring bean is bound to this interface
 * in the application context.  Currently the only implementation is
 * {@link org.upyog.adapter.loader.impl.HttpLoader}, which POSTs the payload
 * over HTTP and publishes an audit record to Kafka.
 *
 * <h3>Default implementation</h3>
 * The interface provides a no-op {@code default} implementation that immediately
 * returns a {@code SUCCESS} result with an empty response body.  This is useful
 * for unit tests and dry-run scenarios where no real HTTP call should be made.
 * Concrete implementations should always {@code @Override} this method.
 *
 * <h3>Extension points</h3>
 * <ul>
 *   <li>Add a new implementing class (e.g. {@code FileLoader}, {@code KafkaLoader})
 *       and annotate it with {@code @Component} / {@code @Primary} to swap the
 *       active strategy without changing any other code.</li>
 * </ul>
 *
 * @see org.upyog.adapter.loader.impl.HttpLoader
 * @see DashboardPayload
 * @see IngestionResult
 */
public interface Loader {

    /**
     * Pushes the normalized {@code data} payload to the configured downstream
     * target and returns a result describing the outcome.
     *
     * <p>The default implementation is a no-op that returns a
     * {@code SUCCESS} result with an empty {@code responseData} string and the
     * current epoch-millis timestamp.  Override this method in concrete loaders
     * to perform real I/O.
     *
     * @param data the fully validated, normalized dashboard payload to push;
     *             must not be {@code null}; the {@code Data} list should contain
     *             at least one {@link org.upyog.adapter.model.DashboardData} entry
     * @return an {@link IngestionResult} describing the outcome:
     *         <ul>
     *           <li>{@code ingestionStatus} — {@code "SUCCESS"} or {@code "FAILURE"}</li>
     *           <li>{@code responseData}    — raw response body (success path)</li>
     *           <li>{@code failureReason}   — exception message (failure path)</li>
     *           <li>{@code ingestedAt}      — epoch millis at time of completion</li>
     *         </ul>
     *         Never {@code null}.
     */
    default IngestionResult load(DashboardPayload data) {
        return IngestionResult.builder()
                .ingestionStatus("SUCCESS")
                .responseData("")
                .ingestedAt(System.currentTimeMillis())
                .build();
    }
}
