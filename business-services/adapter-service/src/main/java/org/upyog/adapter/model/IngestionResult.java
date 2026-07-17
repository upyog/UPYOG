package org.upyog.adapter.model;

import lombok.Builder;
import lombok.Data;

/**
 * Immutable result object returned by a {@link org.upyog.adapter.loader.Loader}
 * after an ingestion attempt completes (successfully or not).
 *
 * <p>The result is returned all the way up to the original caller of
 * {@link org.upyog.adapter.api.AdapterClient#execute} so that the calling layer
 * (e.g. a scheduled job or REST controller) can log, store, or relay the outcome.
 *
 * <h3>Status values</h3>
 * <ul>
 *   <li>{@code "SUCCESS"} — the downstream endpoint accepted the payload and
 *       returned a 2xx HTTP status.</li>
 *   <li>{@code "FAILURE"} — an exception was thrown, a non-2xx status was
 *       received, or a connection error occurred.</li>
 * </ul>
 *
 * <h3>Failure vs. success fields</h3>
 * <pre>
 * ingestionStatus  responseData   failureReason   Meaning
 * ───────────────  ─────────────  ──────────────  ─────────────────────────────────────
 * SUCCESS          non-null       null            Endpoint accepted the payload
 * FAILURE          null           non-null        Exception or non-2xx response
 * </pre>
 *
 * @see org.upyog.adapter.loader.Loader
 * @see org.upyog.adapter.loader.impl.HttpLoader
 */
/**
 * Class representing the IngestionResult class.
 * 
 * <p>Contributes to the core Property Tax metrics ingestion pipeline.
 */
@Data
@Builder
public class IngestionResult {

    /**
     * Terminal status of the ingestion attempt.
     *
     * <p>Possible values: {@code "SUCCESS"} or {@code "FAILURE"}.
     * Set unconditionally so callers can always read this field without null checks.
     */
    private String ingestionStatus;

    /**
     * Raw HTTP response body returned by the downstream endpoint on a successful
     * push.
     *
     * <p>Contains the JSON string returned by the National Dashboard ingest
     * endpoint when the push succeeded.  {@code null} on the failure path.
     */
    private String responseData;

    /**
     * Human-readable description of why the ingestion failed.
     *
     * <p>Contains the {@link Exception#getMessage()} of the exception that was
     * caught, or a description of the non-2xx HTTP error.  {@code null} on the
     * success path.
     */
    private String failureReason;

    /**
     * Unix epoch timestamp in milliseconds at which the loader returned this result.
     *
     * <p>Set via {@code System.currentTimeMillis()} immediately before the result
     * is built and returned, giving an approximate timestamp of when the HTTP call
     * (or failure) completed.
     */
    private long ingestedAt;
}
