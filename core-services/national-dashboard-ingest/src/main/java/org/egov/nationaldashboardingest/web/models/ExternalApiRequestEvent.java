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
public class ExternalApiRequestEvent {

    @JsonProperty("id")
    private String id;

    @JsonProperty("rawDetailId")
    private String rawDetailId;

    @JsonProperty("correlationId")
    private String correlationId;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("externalApiName")
    private String externalApiName;

    @JsonProperty("direction")
    private String direction;

    @JsonProperty("requestTime")
    private Long requestTime;

    @JsonProperty("status")
    private String status;

    @JsonProperty("retryCount")
    private Integer retryCount;

    @JsonProperty("createdTime")
    private Long createdTime;

    @JsonProperty("lastModifiedTime")
    private Long lastModifiedTime;

    @JsonProperty("requestPayload")
    private Object requestPayload;

    @JsonProperty("payloadSizeBytes")
    private Long payloadSizeBytes;
}
