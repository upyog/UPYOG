package org.upyog.chb.web.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

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

