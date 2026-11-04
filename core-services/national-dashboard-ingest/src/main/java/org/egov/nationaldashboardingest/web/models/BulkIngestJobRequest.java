package org.egov.nationaldashboardingest.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;

/**
 * Event payload wrapper model for publishing bulk job persistence events to Kafka persister topics.
 * <p>
 * Pairs {@link BulkIngestJob} execution details with standard UPYOG {@link RequestInfo} headers
 * for egov-persister mapping.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkIngestJobRequest {

    /**
     * Standard UPYOG platform request metadata carrying user context and trace credentials.
     */
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    /**
     * Functional details of the bulk ingestion job execution.
     */
    @JsonProperty("bulkIngestJob")
    private BulkIngestJob bulkIngestJob;
}
