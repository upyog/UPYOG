package org.ksmart.birth.birthcommon.model.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.Size;

import org.ksmart.birth.common.model.AuditDetails;

import java.math.BigDecimal;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CommonPay {
    @Size(max = 64)
    @JsonProperty("applicationNumber")
    private String applicationNumber;

    private String action;

    private String applicationStatus;

    private Boolean hasPayment;

    @JsonProperty("isPaymentSuccess")
    private Boolean isPaymentSuccess;

    private BigDecimal amount = BigDecimal.ZERO;
    
    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;
}
