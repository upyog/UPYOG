package org.egov.nationaldashboardingest.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;

import java.util.List;

/**
 * Standard UPYOG request model wrapper for the bulk data ingestion initialization endpoint ({@code POST /bulk/v1/_init}).
 * <p>
 * Pairs the functional target file details ({@link BulkIngestInitDetail}) with standard platform request
 * headers ({@link RequestInfo}) carrying authentication, API credentials, and tracing context.
 * </p>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class BulkIngestInitRequest {

    /**
     * Functional details specifying the S3 file location and state tenant jurisdiction.
     */
    @Valid
    @NotNull
    @JsonProperty("details")
    private BulkIngestInitDetail details;

    /**
     * Standard UPYOG platform request metadata carrying authentication credentials and user context.
     */
    @JsonProperty("requestInfo")
    private RequestInfo requestInfo;

}
