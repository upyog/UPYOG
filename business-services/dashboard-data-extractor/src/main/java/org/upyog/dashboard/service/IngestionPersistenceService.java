package org.upyog.dashboard.service;

import java.time.LocalDate;

/**
 * Service defining persistence operations for ingestion summaries and legacy jobs.
 * Implementations will route these writes to Kafka or directly to JDBC.
 */
public interface IngestionPersistenceService {

    /**
     * Records or updates the date up to which the module's data was successfully ingested.
     *
     * @param tenantId       the tenant identifier (e.g., {@code pg.citya})
     * @param moduleName     the module short code (e.g., {@code PT})
     * @param successfulDate the last date for which ingestion completed successfully
     */
    void saveOrUpdateLastSuccessfulDate(String tenantId, String moduleName, LocalDate successfulDate);

    /**
     * Records or updates the date for which an ingestion attempt was made.
     *
     * @param tenantId      the tenant identifier
     * @param moduleName    the module short code
     * @param attemptedDate the date for which ingestion was attempted
     */
    void saveOrUpdateLastAttemptedDate(String tenantId, String moduleName, LocalDate attemptedDate);

    /**
     * Submits a request to process legacy data ingestion for the given tenant, module, and date.
     *
     * @param tenantId   the tenant identifier
     * @param moduleName the module short code
     * @param date       the date for which the legacy job should be created
     */
    void createLegacyJob(String tenantId, String moduleName, LocalDate date);

    /**
     * Updates the status of an existing legacy data ingestion job.
     *
     * @param jobId        the unique identifier of the legacy job
     * @param status       the new ingestion status (e.g., {@code SUCCESS} or {@code FAILURE})
     * @param requestData  the JSON request payload sent to the external system
     * @param responseData the JSON response payload received from the external system
     */
    void updateLegacyJobStatus(String jobId, String status, String requestData, String responseData);
}
