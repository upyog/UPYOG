package org.upyog.adapter.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Normalized intermediate payload produced by a
 * {@link org.upyog.adapter.transformer.ModuleTransformer} and consumed by both
 * the validation layer and the {@link org.upyog.adapter.loader.Loader}.
 *
 * <p>This class acts as the normalized, module-agnostic data container that flows
 * through the adapter pipeline after transformation:
 * <pre>
 * rawData → ModuleTransformer → DashboardPayload → CommonValidator → Loader
 * </pre>
 *
 * <h3>Jackson serialization</h3>
 * The {@code data} field is annotated with {@code @JsonProperty("Data")} so that
 * it serializes to the upper-case key {@code "Data"} required by the National
 * Dashboard ingest endpoint when wrapped inside a
 * {@link NationalDashboardIngestRequest}.
 *
 * <h3>Contents</h3>
 * Each element in the {@code data} list represents one ULB-date-module snapshot.
 * For a single ULB the list typically has one element; for batch ingestion it
 * may contain multiple elements.
 *
 * @see DashboardData
 * @see NationalDashboardIngestRequest
 * @see org.upyog.adapter.transformer.ModuleTransformer
 * @see org.upyog.adapter.validator.CommonValidator
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardPayload {

    /**
     * The list of module metric records to be ingested.
     *
     * <p>Serialized as the JSON key {@code "Data"} (upper-case) to satisfy the
     * National Dashboard API contract.
     *
     * <p>Must not be {@code null} or empty — validated by
     * {@link org.upyog.adapter.validator.CommonValidator#validate(DashboardPayload)}.
     * Each element must contain valid contextual fields and a non-{@code null}
     * metrics map.
     */
    @JsonProperty("Data")
    private List<DashboardData> data;
}
