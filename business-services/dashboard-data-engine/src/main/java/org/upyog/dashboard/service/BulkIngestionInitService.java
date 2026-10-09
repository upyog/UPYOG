package org.upyog.dashboard.service;

/**
 * Service interface contract for notifying downstream systems of completed dataset uploads.
 * <p>
 * Decouples upload mechanisms (e.g. AWS S3 storage client) from downstream API coordination,
 * ensuring the national dashboard ingest microservice is signaled to stream and process
 * uploaded files asynchronously.
 * </p>
 */
public interface BulkIngestionInitService {

    /**
     * Dispatches an initialization request to the downstream national dashboard ingest engine
     * for a dataset file previously uploaded to AWS S3 (defaults delimiter to null for Excel files).
     *
     * @param fileStoreId the complete AWS S3 object key / storage path (e.g. {@code "UPYOG/pg/PGR/test.xlsx"})
     * @return raw response JSON string returned by the downstream init endpoint, or {@code null} if dispatch fails
     */
    String initializeBulkIngestion(String fileStoreId);

    /**
     * Dispatches an initialization request to the downstream national dashboard ingest engine
     * for a dataset file previously uploaded to AWS S3 with an explicit delimiter.
     *
     * @param fileStoreId the complete AWS S3 object key / storage path (e.g. {@code "UPYOG/pg/PT/test_pt_bulk.psv"})
     * @param delimiter dataset delimiter string (e.g. {@code "|"}, {@code ","}) or {@code null} for Excel files
     * @return raw response JSON string returned by the downstream init endpoint, or {@code null} if dispatch fails
     */
    String initializeBulkIngestion(String fileStoreId, String delimiter);
}
