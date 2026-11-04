package org.egov.nationaldashboardingest.service;

import org.egov.nationaldashboardingest.web.models.BulkIngestInitDetail;

/**
 * Service interface contract for managing the lifecycle of bulk dataset file processing.
 * <p>
 * Orchestrates:
 * <ul>
 *   <li>Downloading remote binary dataset objects from AWS S3 storage to local temporary disk.</li>
 *   <li>Iterating through spreadsheet rows in streaming batches to preserve system memory.</li>
 *   <li>Forwarding row batches to {@link BatchIngestionProcessor} for per-row audit logging and ingestion.</li>
 *   <li>Ensuring mandatory deletion and cleanup of local disk files upon completion or failure.</li>
 * </ul>
 * </p>
 */
public interface BulkFileProcessorService {

    /**
     * Downloads an S3 file object specified in the initialization request, streams its row contents,
     * ingests metrics per row, and cleans up local temporary files.
     *
     * @param bulkIngestInitDetail payload detailing the target S3 file path and state code
     */
    void processBulkFile(BulkIngestInitDetail bulkIngestInitDetail);
}
