package org.egov.nationaldashboardingest.web.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExternalApiResponseEvent {

    @JsonProperty("correlationId")
    private String correlationId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("httpStatusCode")
    private Integer httpStatusCode;

    @JsonProperty("responseTime")
    private Long responseTime;

    @JsonProperty("durationMs")
    private Long durationMs;

    @JsonProperty("lastModifiedTime")
    private Long lastModifiedTime;

    @JsonProperty("responsePayload")
    private Object responsePayload;

    @JsonProperty("payloadSizeBytes")
    private Long payloadSizeBytes;

    @JsonProperty("errorDetails")
    private ExternalApiErrorDetails errorDetails;
}
