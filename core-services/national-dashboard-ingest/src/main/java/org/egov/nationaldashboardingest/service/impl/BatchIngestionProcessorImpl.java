package org.egov.nationaldashboardingest.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.nationaldashboardingest.service.BatchIngestionProcessor;
import org.egov.nationaldashboardingest.service.ExternalApiAuditLogger;
import org.egov.nationaldashboardingest.service.IngestService;
import org.egov.nationaldashboardingest.utils.ExternalApiAuditConstants;
import org.egov.nationaldashboardingest.utils.ResponseInfoFactory;
import org.egov.nationaldashboardingest.web.models.Data;
import org.egov.nationaldashboardingest.web.models.IngestRequest;
import org.egov.nationaldashboardingest.web.models.IngestResponse;
import org.egov.nationaldashboardingest.web.models.IngestRowData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Production implementation of {@link BatchIngestionProcessor}.
 * <p>
 * Processes streaming Excel rows sequentially:
 * <ul>
 * <li>Wraps individual {@link Data} records into single-item
 * {@link IngestRequest} payloads.</li>
 * <li>Resolves authentication credentials, preferring row-level
 * {@link RequestInfo} extracted from the first row of the Excel file, falling
 * back to method defaults or a system-generated header.</li>
 * <li>Generates unique UUID correlation identifiers for each row to record
 * granular inbound API audits in {@code ug_external_api_audit_request} and
 * {@code ug_external_api_audit_response} tables.</li>
 * <li>Calls {@link IngestService#ingestData} within
 * {@link ExternalApiAuditLogger#logInboundApi} wrapper lambda.</li>
 * <li>Catches row-level failures to prevent any single corrupted row from
 * aborting the remaining batch, logging the error and registering failure
 * diagnostics in {@code failedDatesList}.</li>
 * </ul>
 * </p>
 */
@Slf4j
@Service
public class BatchIngestionProcessorImpl implements BatchIngestionProcessor {

    @Autowired
    private IngestService ingestService;

    @Autowired
    private ExternalApiAuditLogger integrationAuditLogger;

    @Autowired
    private org.egov.nationaldashboardingest.config.ApplicationProperties applicationProperties;

    /**
     * Iterates through a streaming batch of parsed Excel row objects, executing
     * per-row ingestion and audit logging.
     * <p>
     * For each valid {@link IngestRowData}:
     * <ol>
     * <li>Derives tenant identifier from ULB or state field.</li>
     * <li>Constructs effective {@link RequestInfo}.</li>
     * <li>Executes {@link ExternalApiAuditLogger#logInboundApi} to log the API
     * call, ingest the metric into Elasticsearch, and capture the generated
     * hash response.</li>
     * <li>Captures any thrown {@link Exception} into the provided
     * {@code failedDatesList} with row metadata.</li>
     * </ol>
     * </p>
     *
     * @param batchRowList batch list of row wrapper objects containing row data
     * and optional request info
     * @param defaultRequestInfo default request info fallback if row does not
     * supply custom credentials
     * @param failedDatesList mutable list collecting structured error maps for
     * any row execution failures
     */
    @Override
    public void processBatchWithFallback(List<IngestRowData> batchRowList, RequestInfo defaultRequestInfo, List<Map<String, Object>> failedDatesList) {
        if (batchRowList == null || batchRowList.isEmpty()) {
            return;
        }

        for (IngestRowData rowData : batchRowList) {
            if (rowData == null || rowData.getData() == null) {
                continue;
            }

            Data singleData = rowData.getData();
            String tenantId = singleData.getUlb() != null ? singleData.getUlb() : singleData.getState();

            RequestInfo effectiveRequestInfo = rowData.getRequestInfo() != null
                    ? rowData.getRequestInfo()
                    : (defaultRequestInfo != null ? defaultRequestInfo : createFallbackSystemRequestInfo(tenantId));

            IngestRequest singleRequest = IngestRequest.builder()
                    .requestInfo(effectiveRequestInfo)
                    .ingestData(List.of(singleData))
                    .build();

            String rowCorrelationId = UUID.randomUUID().toString();

            try {
                integrationAuditLogger.logInboundApi(
                        rowCorrelationId,
                        tenantId,
                        ExternalApiAuditConstants.API_NATIONAL_DASHBOARD_METRIC_INGEST,
                        singleRequest,
                        () -> {
                            List<Integer> responseHash = ingestService.ingestData(singleRequest);
                            ResponseInfo responseInfo = ResponseInfoFactory.createResponseInfoFromRequestInfo(
                                    singleRequest.getRequestInfo(), true);
                            IngestResponse response = IngestResponse.builder()
                                    .responseInfo(responseInfo)
                                    .responseHash(responseHash)
                                    .build();
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        }
                );
                log.info("Successfully ingested and audited row for date: {}, ulb: {}", singleData.getDate(), singleData.getUlb());
            } catch (Exception singleException) {
                log.error("Failed to ingest row for date: {}, ulb: {}, error: {}",
                        singleData.getDate(), singleData.getUlb(), singleException.getMessage());

                Map<String, Object> failedRecord = new HashMap<>();
                failedRecord.put("date", singleData.getDate());
                failedRecord.put("ulb", singleData.getUlb());
                failedRecord.put("module", singleData.getModule());
                failedRecord.put("region", singleData.getRegion());
                failedRecord.put("state", singleData.getState());
                failedRecord.put("error", singleException.getMessage());

                failedDatesList.add(failedRecord);
            }
        }
    }

    /**
     * Constructs a fallback UPYOG {@link RequestInfo} carrying automated SYSTEM
     * credentials and tenant context.
     * <p>
     * Used when neither the Excel row nor the caller supplied an explicit
     * {@link RequestInfo}.
     * </p>
     *
     * @param tenantId tenant identifier (ULB or state code) to assign to the
     * fallback system user
     * @return constructed {@link RequestInfo} with SYSTEM user credentials and
     * generated message UUID
     */
    private RequestInfo createFallbackSystemRequestInfo(String tenantId) {
        String systemUuid = "b3177c56-a9f2-4b59-a00c-9e9c38b5f28f";
        if (applicationProperties != null && applicationProperties.getNationalDashboardUser() != null
                && applicationProperties.getNationalDashboardUser().containsKey("SUPERUUID")) {
            systemUuid = applicationProperties.getNationalDashboardUser().get("SUPERUUID");
        }

        User userInfo = User.builder()
                .uuid(systemUuid)
                .tenantId(tenantId)
                .roles(Collections.emptyList())
                .build();

        return RequestInfo.builder()
                .apiId("national-dashboard-ingest-bulk")
                .ver("1.0")
                .ts(System.currentTimeMillis())
                .msgId(UUID.randomUUID().toString())
                .userInfo(userInfo)
                .build();
    }
}
