package org.egov.nationaldashboardingest.web.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.nationaldashboardingest.service.BulkIngestService;
import org.egov.nationaldashboardingest.utils.ResponseInfoFactory;
import org.egov.nationaldashboardingest.web.models.BulkIngestInitRequest;
import org.egov.nationaldashboardingest.web.models.BulkIngestInitResponse;
import org.egov.nationaldashboardingest.web.models.BulkIngestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API controller exposing endpoints for bulk dataset ingestion operations.
 * <p>
 * Provides:
 * <ul>
 *   <li>{@code POST /bulk/v1/_init}: Ingests metadata referencing an uploaded dataset file in S3,
 *       validates request headers and payloads, publishes an event to Kafka for decoupled processing,
 *       and returns standardized UPYOG {@link BulkIngestInitResponse}.</li>
 * </ul>
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/bulk/v1")
public class BulkDataIngestController {

    @Autowired
    private BulkIngestService bulkIngestService;

    /**
     * Endpoint to initialize asynchronous bulk data ingestion for an S3 spreadsheet file.
     * <p>
     * Flow:
     * <ol>
     *   <li>Validates inbound {@link BulkIngestInitRequest} payload via Jakarta Bean Validation ({@code @Valid}).</li>
     *   <li>Delegates validation and Kafka dispatch to {@link BulkIngestService#init}.</li>
     *   <li>Maps returned integer code to {@link BulkIngestStatus} enum.</li>
     *   <li>Constructs standard UPYOG {@link ResponseInfo} via {@link ResponseInfoFactory}.</li>
     *   <li>Returns {@link ResponseEntity} containing {@link BulkIngestInitResponse} and appropriate HTTP status (200 OK or 500 ERROR).</li>
     * </ol>
     * </p>
     *
     * @param ingestInitRequest request payload containing S3 file key, state code, and caller {@code RequestInfo}
     * @return {@link ResponseEntity} containing a structured {@link BulkIngestInitResponse} and HTTP status code
     */
    @PostMapping("/_init")
    public ResponseEntity<BulkIngestInitResponse> init(@Valid @RequestBody BulkIngestInitRequest ingestInitRequest) {
        log.info("Received bulk ingest init request: {}", ingestInitRequest);

        Integer statusCode = bulkIngestService.init(ingestInitRequest);
        BulkIngestStatus bulkIngestStatus = BulkIngestStatus.fromCode(statusCode);

        ResponseInfo responseInfo = ResponseInfoFactory.createResponseInfoFromRequestInfo(
                ingestInitRequest != null ? ingestInitRequest.getRequestInfo() : null,
                bulkIngestStatus == BulkIngestStatus.SUCCESS
        );

        BulkIngestInitResponse response = BulkIngestInitResponse.builder()
                .responseInfo(responseInfo)
                .status(bulkIngestStatus.getStatus())
                .statusCode(bulkIngestStatus.getCode())
                .message(bulkIngestStatus.getMessage())
                .details(ingestInitRequest != null ? ingestInitRequest.getDetails() : null)
                .build();

        return new ResponseEntity<>(response, bulkIngestStatus.getHttpStatus());
    }
}
