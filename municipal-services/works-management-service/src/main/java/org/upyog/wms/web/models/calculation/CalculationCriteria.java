package org.upyog.wms.web.models.calculation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.upyog.wms.web.models.ActionPlan;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculationCriteria {

    private String tenantId;

    private String applicationNumber;

    private ActionPlan actionPlan;
}
