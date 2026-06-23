package org.egov.rl.calculator.web.models.demand;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DueDate {

    @JsonProperty("billingCycle")
    private String billingCycle;

    @JsonProperty("dueDay")
    private Integer dueDay;

}
