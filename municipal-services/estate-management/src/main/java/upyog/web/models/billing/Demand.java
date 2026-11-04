package upyog.web.models.billing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.models.AuditDetails;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Demand {

    @JsonProperty("id")
    private String id;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("consumerCode")
    private String consumerCode;

    @JsonProperty("consumerType")
    private String consumerType;

    @JsonProperty("businessService")
    private String businessService;

    @JsonProperty("payer")
    private BillingUser payer;

    @JsonProperty("taxPeriodFrom")
    private Long taxPeriodFrom;

    @JsonProperty("taxPeriodTo")
    private Long taxPeriodTo;

    @Builder.Default
    @JsonProperty("demandDetails")
    private List<DemandDetail> demandDetails = new ArrayList<>();

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

    @JsonProperty("additionalDetails")
    private Object additionalDetails;

    @Builder.Default
    @JsonProperty("minimumAmountPayable")
    private BigDecimal minimumAmountPayable = BigDecimal.ZERO;

    @Builder.Default
    @JsonProperty("isPaymentCompleted")
    private Boolean isPaymentCompleted = false;

    @JsonProperty("status")
    private String status;
}
