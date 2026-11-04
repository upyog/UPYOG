package org.egov.nationaldashboardingest.service;

import org.egov.nationaldashboardingest.web.models.BulkIngestInitRequest;

/**
 * Service interface contract for accepting, validating, and initializing bulk data ingestion requests.
 * <p>
 * Evaluates inbound initialization requests from client services, enforces structural validation,
 * and publishes valid tasks to the Kafka bulk ingestion topic for decoupled asynchronous processing.
 * </p>
 */
public interface BulkIngestService {

    /**
     * Validates a bulk ingestion initialization request and pushes the file event to Kafka.
     *
     * @param bulkIngestInitRequest request envelope containing target S3 file path, state code, and RequestInfo
     * @return integer status code:
     *         <ul>
     *           <li>{@link org.egov.nationaldashboardingest.utils.BulkIngestConstants#STATUS_CODE_SUCCESS} (1) on successful enqueue</li>
     *           <li>{@link org.egov.nationaldashboardingest.utils.BulkIngestConstants#STATUS_CODE_ALREADY_PRESENT} (2) if the file is already enqueued</li>
     *           <li>{@link org.egov.nationaldashboardingest.utils.BulkIngestConstants#STATUS_CODE_FAILED} (3) on internal or messaging failure</li>
     *         </ul>
     * @throws org.egov.tracer.model.CustomException if request validation fails
     */
    Integer init(BulkIngestInitRequest bulkIngestInitRequest);
}
