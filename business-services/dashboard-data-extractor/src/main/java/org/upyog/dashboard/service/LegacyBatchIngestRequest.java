package org.upyog.dashboard.service;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request payload model for triggering legacy historical batch ingestion.
 * <p>
 * Encapsulates the target module name and the date range (start and end dates)
 * over which historical metrics should be extracted, aggregated, and ingested.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegacyBatchIngestRequest {

    @JsonProperty("moduleName")
    @NotNull(message = "Module name is required")
    private String moduleName;

    @JsonProperty("startDate")
    @NotNull(message = "Start date is required")
    private String startDate;

    @JsonProperty("endDate")
    @NotNull(message = "End date is required")
    private String endDate;

    @JsonProperty("async")
    private Boolean async;
}
