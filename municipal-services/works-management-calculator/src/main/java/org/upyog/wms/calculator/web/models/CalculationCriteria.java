package org.upyog.wms.calculator.web.models;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Either actionPlan (unsaved or just-submitted data) or applicationNumber (fetched from the service). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculationCriteria {

    @NotBlank
    private String tenantId;

    private String applicationNumber;

    private ActionPlan actionPlan;
}
