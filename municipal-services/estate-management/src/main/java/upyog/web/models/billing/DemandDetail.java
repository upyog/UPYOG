package upyog.web.models.billing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.models.AuditDetails;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandDetail {

    @JsonProperty("id")
    private String id;

    @JsonProperty("demandId")
    private String demandId;

    @JsonProperty("taxHeadMasterCode")
    private String taxHeadMasterCode;

    @JsonProperty("taxAmount")
    private BigDecimal taxAmount;

    @Builder.Default
    @JsonProperty("collectionAmount")
    private BigDecimal collectionAmount = BigDecimal.ZERO;

    @JsonProperty("additionalDetails")
    private Object additionalDetails;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

    @JsonProperty("tenantId")
    private String tenantId;
}
