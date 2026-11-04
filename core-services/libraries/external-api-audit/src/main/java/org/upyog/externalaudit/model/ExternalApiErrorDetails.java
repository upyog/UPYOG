package org.upyog.externalaudit.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Error captured on FAILED events and inserted into {@code ug_external_api_error_detail}.
 * There is no ON CONFLICT on this table; each FAILED publish inserts a new row.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExternalApiErrorDetails {

    @JsonProperty("id")
    private String id;

    @JsonProperty("correlationId")
    private String correlationId;

    @JsonProperty("errorCode")
    private String errorCode;

    @JsonProperty("errorType")
    private String errorType;

    @JsonProperty("errorMessage")
    private String errorMessage;

    @JsonProperty("createdTime")
    private Long createdTime;
}
