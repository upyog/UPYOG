package org.upyog.externalaudit.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.upyog.externalaudit.service.ExternalApiAuditLogger;

/**
 * Existing production Kafka / persister payload for one external API audit event
 * ({@code ug_external_api_message_detail} + raw/error tables).
 * <p>
 * Call sites pass this model to {@link ExternalApiAuditLogger}.
 * Two events (INITIATED then SUCCESS/FAILED) share {@link #correlationId} so persister
 * upserts a single row. {@code external_api_name} identifies the integration.
 * {@code endpoint} and {@code method} are stored in the raw JSON envelope.
 * </p>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ExternalApiAuditDetail {

    @JsonProperty("id")
    private String id;

    /**
     * UUID of the raw-payload row. Set on the INITIATED event; SUCCESS/FAILED may omit it
     * because persister upserts raw detail on {@code correlation_id}.
     */
    @JsonProperty("rawDetailId")
    private String rawDetailId;

    /**
     * Unique per logical request. Persister {@code ON CONFLICT} key.
     * Leave blank to allocate a new UUID; reuse and increment {@link #retryCount} for in-cycle retry.
     */
    @JsonProperty("correlationId")
    private String correlationId;

    @JsonProperty("tenantId")
    private String tenantId;

    /**
     * Stable integration name, for example {@code pfms-data-push}.
     * Persisted in column {@code external_api_name}.
     */
    @JsonProperty("externalApiName")
    private String externalApiName;

    /**
     * {@code INBOUND} or {@code OUTBOUND}. Set by the logger; callers normally omit it.
     */
    @JsonProperty("direction")
    private String direction;

    /**
     * Target URL or inbound path. Stored in the raw JSON envelope as {@code endpoint}.
     */
    @JsonProperty("endpoint")
    private String endpoint;

    /**
     * HTTP method of the integration call, for example {@code POST}.
     * Stored in the raw JSON envelope as {@code method} / {@code httpMethod}.
     */
    @JsonProperty("method")
    private String method;

    /**
     * Tracer / RequestInfo / business correlation id stored in the raw JSON envelope,
     * not used as the table unique key.
     */
    @JsonProperty("originatingCorrelationId")
    private String originatingCorrelationId;

    /**
     * Business entity id (for example PFMS transaction id) stored in the raw JSON envelope.
     */
    @JsonProperty("businessReferenceId")
    private String businessReferenceId;

    @JsonProperty("requestTime")
    private Long requestTime;

    @JsonProperty("status")
    private String status;

    @JsonProperty("httpStatusCode")
    private Integer httpStatusCode;

    @JsonProperty("responseTime")
    private Long responseTime;

    @JsonProperty("durationMs")
    private Long durationMs;

    /**
     * {@code 0} on first attempt. Increment on an in-cycle retry of the same {@link #correlationId}.
     */
    @JsonProperty("retryCount")
    private Integer retryCount;

    @JsonProperty("createdTime")
    private Long createdTime;

    @JsonProperty("lastModifiedTime")
    private Long lastModifiedTime;

    @JsonProperty("requestPayload")
    private Object requestPayload;

    @JsonProperty("responsePayload")
    private Object responsePayload;

    @JsonProperty("payloadSizeBytes")
    private Long payloadSizeBytes;

    @JsonProperty("errorDetails")
    private ExternalApiErrorDetails errorDetails;
}
