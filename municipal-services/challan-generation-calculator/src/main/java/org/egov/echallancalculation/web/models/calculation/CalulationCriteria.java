package org.egov.echallancalculation.web.models.calculation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import org.egov.echallancalculation.model.Challan;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-09-27T14:56:03.454+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalulationCriteria {
        @JsonProperty("challan")
        @Valid
        private Challan challan = null;

        @JsonProperty("challanNo")
        @Size(min=2,max=64) 
        private String challanNo = null;

        @JsonProperty("tenantId")
        @NotNull@Size(min=2,max=256) 
        private String tenantId = null;

        @JsonProperty("demandId")
        @Size(min=1,max=64)
        private String demandId = null;  // Optional: Required for update operations with fee waiver

}

