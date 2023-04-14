package org.ksmart.birth.birthcommon.model.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CommonPayRequest {
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("PaymentDetails")
    @Valid
    private List<CommonPay> commonPays;

    public CommonPayRequest addPayment(CommonPay commonPay) {
        if (commonPays == null) {
            commonPays = null;
        }
        return this;
    }

}
