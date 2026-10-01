package org.egov.nationaldashboardingest.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.nationaldashboardingest.web.models.IngestRowData;

import java.util.List;
import java.util.Map;

/**
 * Service interface contract for processing streaming batches of parsed spreadsheet row data.
 * <p>
 * Key responsibilities:
 * <ul>
 *   <li>Iterates through parsed {@link IngestRowData} objects sequentially within the batch.</li>
 *   <li>Derives effective UPYOG {@link RequestInfo} per row (preserving row-level user/auth tokens or falling back to defaults).</li>
 *   <li>Generates unique per-row correlation IDs to ensure isolated audit entries in {@code ug_external_api_*} tables.</li>
 *   <li>Dispatches individual row ingestion requests to {@link IngestService} and catches row-level exceptions without halting the batch.</li>
 *   <li>Captures structured error metadata for any failed rows into {@code failedDatesList}.</li>
 * </ul>
 * </p>
 */
public interface BatchIngestionProcessor {

    /**
     * Processes a batch of parsed Excel row objects individually with per-row audit logging and isolated failure handling.
     *
     * @param batchRowList       list of {@link IngestRowData} items containing row metric values and associated RequestInfo
     * @param defaultRequestInfo fallback {@link RequestInfo} to use if an individual row does not carry custom credentials
     * @param failedDatesList    collector list accumulating structured error maps for any rows that fail validation or ingestion
     */
    void processBatchWithFallback(List<IngestRowData> batchRowList, RequestInfo defaultRequestInfo, List<Map<String, Object>> failedDatesList);
}
