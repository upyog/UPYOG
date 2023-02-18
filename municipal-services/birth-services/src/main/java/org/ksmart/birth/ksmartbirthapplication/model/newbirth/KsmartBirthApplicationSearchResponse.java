package org.ksmart.birth.ksmartbirthapplication.model.newbirth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.response.ResponseInfo;
import org.ksmart.birth.birthregistry.model.BirthCertificate;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KsmartBirthApplicationSearchResponse {
    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("ChildDetails")
    @Valid
    private List<KsmartBirthAppliactionDetail> ksmartBirthDetails;
    public KsmartBirthApplicationSearchResponse addKsmartBirthSearchApplication(KsmartBirthAppliactionDetail birthDetail) {
        if (ksmartBirthDetails == null) {
            ksmartBirthDetails = new ArrayList<>();
        }
        ksmartBirthDetails.add(birthDetail);
        return this;
    }
}
