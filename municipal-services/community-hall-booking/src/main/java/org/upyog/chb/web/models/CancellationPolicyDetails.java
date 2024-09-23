package org.upyog.chb.web.models;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Details of booking cancellation and policy
 */
@ApiModel(description = "Details of booking cancellation and policy")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CancellationPolicyDetails   {
        @JsonProperty("id")
        private Integer id = null;

        @JsonProperty("cancelFrom")
        private Integer cancelFrom = null;

        @JsonProperty("cancelTo")
        private Integer cancelTo = null;

        @JsonProperty("percentageDeduction")
        private Integer percentageDeduction = null;


}

