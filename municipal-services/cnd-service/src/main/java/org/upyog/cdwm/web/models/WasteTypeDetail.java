package org.upyog.cdwm.web.models;

import digit.models.coremodels.AuditDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WasteTypeDetail {

    private String applicationId;

    private String wasteTypeId;

    private String enteredByUserType;
    
    private String wasteType;

    private BigDecimal quantity;

    private String metrics;

    private AuditDetails auditDetails;
}
