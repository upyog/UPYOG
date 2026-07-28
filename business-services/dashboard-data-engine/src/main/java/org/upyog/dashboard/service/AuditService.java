package org.upyog.dashboard.service;

import org.upyog.dashboard.model.DashboardPayload;

/**
 * Service responsible for auditing ingestion details and error logs.
 */
public interface AuditService {

    /**
     * Pushes an ingestion audit record to the underlying storage mechanism.
     *
     * @param data            the original {@link DashboardPayload} passed to the loader
     * @param requestJson     the JSON string that was sent to the national dashboard endpoint
     * @param responseOrError the response body on success, or the exception message on failure
     * @param status          {@code "SUCCESS"} or {@code "FAILURE"}
     */
    void pushIngestionRecord(DashboardPayload data, String requestJson, String responseOrError, String status);
}
