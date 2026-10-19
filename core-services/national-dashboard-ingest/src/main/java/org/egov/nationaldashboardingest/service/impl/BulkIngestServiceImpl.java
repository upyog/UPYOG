package org.egov.nationaldashboardingest.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.nationaldashboardingest.config.ApplicationProperties;
import org.egov.nationaldashboardingest.producer.Producer;
import org.egov.nationaldashboardingest.service.BulkIngestService;
import org.egov.nationaldashboardingest.utils.BulkIngestConstants;
import org.egov.nationaldashboardingest.web.models.BulkIngestInitRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Production implementation of {@link BulkIngestService}.
 * <p>
 * Implements the synchronous intake stage of bulk data ingestion:
 * <ol>
 * <li>Validates that {@link BulkIngestInitRequest} contains mandatory
 * attributes (file name and state code).</li>
 * <li>Publishes the file event payload to the configured Kafka topic via
 * {@link Producer#push}.</li>
 * <li>Maps potential failures or duplicate file errors to standard integer
 * status codes.</li>
 * </ol>
 * </p>
 */
@Slf4j
@Service
public class BulkIngestServiceImpl implements BulkIngestService {

    @Autowired
    private Producer producer;

    @Autowired
    private ApplicationProperties applicationProperties;

    /**
     * Validates and initializes a bulk ingestion job by publishing the request
     * details to Kafka.
     * <p>
     * Enforces non-blank validation on S3 file path and state tenant code.
     * Dispatches the event using the S3 file name as the message partition key.
     * </p>
     *
     * @param bulkIngestInitRequest bulk ingestion initialization request
     * envelope
     * @return integer status code:
     * <ul>
     * <li>{@link BulkIngestConstants#STATUS_CODE_SUCCESS} (1) on successful
     * enqueue</li>
     * <li>{@link BulkIngestConstants#STATUS_CODE_ALREADY_PRESENT} (2) if the
     * file is already being processed</li>
     * <li>{@link BulkIngestConstants#STATUS_CODE_FAILED} (3) on internal or
     * messaging error</li>
     * </ul>
     * @throws CustomException if validation fails or parameters are blank
     */
    @Override
    public Integer init(BulkIngestInitRequest bulkIngestInitRequest) {
        // Validate incoming request payload and details fields
        if (!isInitRequestValid(bulkIngestInitRequest)) {
            throw new CustomException(
                    BulkIngestConstants.ERR_CODE_INVALID_INIT_REQUEST,
                    BulkIngestConstants.MSG_INVALID_INIT_REQUEST
            );
        }

        try {
            // Push initialization event payload to the configured Kafka topic (bulk-ingest-init) using S3 file name as the message key
            producer.push(
                    applicationProperties.getBulkIngestTopic(),
                    bulkIngestInitRequest.getDetails().getFileName(),
                    bulkIngestInitRequest.getDetails()
            );
            return BulkIngestConstants.STATUS_CODE_SUCCESS;
        } catch (CustomException e) {
            log.error("Error in bulk ingest init", e);
            // Check if failure is due to duplicate file or already present event
            if (BulkIngestConstants.ERR_CODE_DUPLICATE_FILE.equalsIgnoreCase(e.getCode())
                    || BulkIngestConstants.ERR_CODE_ALREADY_EXISTS.equalsIgnoreCase(e.getCode())) {
                return BulkIngestConstants.STATUS_CODE_ALREADY_PRESENT;
            }
            return BulkIngestConstants.STATUS_CODE_FAILED;
        } catch (Exception e) {
            log.error("Error in bulk ingest init", e);
            return BulkIngestConstants.STATUS_CODE_FAILED;
        }
    }

    /**
     * Validates that the initialization request payload contains all non-null,
     * non-blank required fields.
     *
     * @param bulkIngestInitRequest the request object to validate
     * @return {@code true} if request and nested details (fileName and
     * stateCode) are valid and non-blank; {@code false} otherwise
     */
    private Boolean isInitRequestValid(BulkIngestInitRequest bulkIngestInitRequest) {
        // Ensure request object and details object are not null
        if (bulkIngestInitRequest == null || bulkIngestInitRequest.getDetails() == null) {
            return false;
        }
        // Ensure fileName (S3 key) is non-blank
        if (StringUtils.isBlank(bulkIngestInitRequest.getDetails().getFileName())) {
            return false;
        }
        // Ensure stateCode is non-blank
        if (StringUtils.isBlank(bulkIngestInitRequest.getDetails().getStateCode())) {
            return false;
        }
        return true;
    }
}
