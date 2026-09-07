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
