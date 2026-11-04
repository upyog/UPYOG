package org.egov.nationaldashboardingest.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.response.ResponseInfo;

/**
 * Standard UPYOG response envelope returned by the bulk data ingestion initialization API ({@code POST /bulk/v1/_init}).
 * <p>
 * Returns execution outcome, diagnostic message, numeric status code, echoing back the submitted
 * {@link BulkIngestInitDetail} alongside UPYOG {@link ResponseInfo}.
 * </p>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class BulkIngestInitResponse {

    /**
     * Standard UPYOG platform response header conveying correlation ID, API status, and timestamp.
     */
    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    /**
     * Textual representation of the initialization status (e.g. {@code "SUCCESS"}, {@code "FAILED"}, {@code "ALREADY_PRESENT"}).
     */
    @JsonProperty("status")
    private String status;

    /**
     * Numeric status code representing the outcome (1 = SUCCESS, 2 = ALREADY_PRESENT, 3 = FAILED).
     */
    @JsonProperty("statusCode")
    private Integer statusCode;

    /**
     * Human-readable diagnostic description or status message.
     */
    @JsonProperty("message")
    private String message;

    /**
     * Echo of the target S3 file path and state code submitted with the request.
     */
    @JsonProperty("details")
    private BulkIngestInitDetail details;
}
