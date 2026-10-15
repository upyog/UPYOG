package org.egov.pt.models;
import java.math.BigDecimal;
import org.egov.common.contract.response.ResponseInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomAmountUpdateResponse {

    private ResponseInfo responseInfo;

    private String tenantId;

    private String billId;

    private String consumerCode;

    private String status;

    private BigDecimal oldAmount;
    
    private BigDecimal customAmount;
    
    private BigDecimal updatedTotalAmount;

    private String message;

    private Long updatedTime;

    private String updatedBy;

    private String reason;

    private Boolean isCustomAmountApplied;

    private String demandId;
}
