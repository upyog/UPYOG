package org.upyog.adapter.service;

import java.time.LocalDate;

/**
 * Service defining persistence operations for ingestion summaries and legacy jobs.
 * Implementations will route these writes to Kafka or directly to JDBC.
 */
public interface IngestionPersistenceService {

    /**
     * Records or updates the date up to which the module's data was successfully ingested.
     */
    void saveOrUpdateLastSuccessfulDate(String tenantId, String moduleName, LocalDate successfulDate);

    /**
     * Records or updates the date for which an ingestion attempt was made.
     */
    void saveOrUpdateLastAttemptedDate(String tenantId, String moduleName, LocalDate attemptedDate);

    /**
     * Submits a request to process legacy data ingestion.
     */
    void createLegacyJob(String tenantId, String moduleName, LocalDate date);

    /**
     * Updates the status of an existing legacy data ingestion job.
     */
    void updateLegacyJobStatus(String jobId, String status, String requestData, String responseData);
}
