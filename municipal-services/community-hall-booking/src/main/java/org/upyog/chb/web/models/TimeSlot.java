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
 * Details of time slots for community hall booking
 */
@ApiModel(description = "Details of time slots for community hall booking")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimeSlot   {
        @JsonProperty("id")
        private Integer id = null;

        @JsonProperty("from")
        private String from = null;

        @JsonProperty("to")
        private String to = null;

        @JsonProperty("rent")
        private Integer rent = null;

        @JsonProperty("securityDeposit")
        private Integer securityDeposit = null;

        @JsonProperty("electricityCharges")
        private Integer electricityCharges = null;

        @JsonProperty("waterCharges")
        private Integer waterCharges = null;

        @JsonProperty("conservancyCharges")
        private Integer conservancyCharges = null;


}

