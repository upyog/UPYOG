package org.ksmart.birth.ksmartbirthapplication.model.newbirth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;
@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KsmartBirthDetailsRequest {
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("ChildDetails")
    @Valid
    private List<KsmartBirthAppliactionDetail> ksmartBirthDetails;

    public KsmartBirthDetailsRequest addKsmartBirthDetails(KsmartBirthAppliactionDetail birthDetail) {
        if (ksmartBirthDetails == null) {
            ksmartBirthDetails = null;
        }
        return this;
    }
}
