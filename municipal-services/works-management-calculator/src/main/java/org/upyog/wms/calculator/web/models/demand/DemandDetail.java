package org.upyog.wms.calculator.web.models.demand;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.upyog.wms.calculator.web.models.AuditDetails;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DemandDetail {

    private String id;

    private String demandId;

    private String taxHeadMasterCode;

    private BigDecimal taxAmount;

    private BigDecimal collectionAmount;

    private Object additionalDetails;

    private AuditDetails auditDetails;

    private String tenantId;
}
