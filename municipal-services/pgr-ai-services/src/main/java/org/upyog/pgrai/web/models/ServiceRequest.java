package org.upyog.pgrai.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Request object to fetch the report data
 */
@Schema(description = "Request object to fetch the report data")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2020-07-15T11:35:33.568+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceRequest   {

        @NotNull
        @JsonProperty("RequestInfo")
        private RequestInfo requestInfo = null;

        @Valid
        @NonNull
        @JsonProperty("service")
        private Service service = null;

        @Valid
        @JsonProperty("workflow")
        private Workflow workflow = null;


}

