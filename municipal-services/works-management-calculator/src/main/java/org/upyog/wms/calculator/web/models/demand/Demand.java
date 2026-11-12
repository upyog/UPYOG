package org.upyog.wms.calculator.web.models.demand;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.User;
import org.upyog.wms.calculator.web.models.AuditDetails;

/** billing-service Demand contract. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Demand {

    private String id;

    private String tenantId;

    private String consumerCode;

    private String consumerType;

    private String businessService;

    private User payer;

    private Long taxPeriodFrom;

    private Long taxPeriodTo;

    private List<DemandDetail> demandDetails;

    private BigDecimal minimumAmountPayable;

    private String status;

    private Object additionalDetails;

    private AuditDetails auditDetails;
}
