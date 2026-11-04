package org.upyog.dashboard.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload envelope model for calling the downstream bulk ingestion initialization endpoint ({@code /bulk/v1/_init}).
 * <p>
 * Complies with UPYOG platform standards by wrapping the functional body ({@link BulkIngestDetails})
 * alongside authentication and audit headers ({@link RequestInfo}).
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkIngestRequest {

    /**
     * Standard UPYOG {@link RequestInfo} header carrying API credentials, authentication token,
     * action metadata, and caller user context.
     */
    @JsonProperty("requestInfo")
    private RequestInfo requestInfo;

    /**
     * Functional details specifying the uploaded S3 file location and target tenant jurisdiction.
     */
    @JsonProperty("details")
    private BulkIngestDetails details;
}
