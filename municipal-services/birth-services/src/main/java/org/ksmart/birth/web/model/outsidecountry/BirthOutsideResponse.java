package org.ksmart.birth.web.model.outsidecountry;

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


public class BirthOutsideResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("ChildDetails")
    @Valid
    private List<BirthOutsideApplication> ksmartBirthDetails;

    @JsonProperty("BirthCertificate")
    @Valid
    private BirthCertificate birthCertificate;


    @JsonProperty("Count")
    private int count;

    public BirthOutsideResponse addBirthApplication(BirthOutsideApplication birthDetail) {
        if (ksmartBirthDetails == null) {
            ksmartBirthDetails = new ArrayList<>();
        }
        ksmartBirthDetails.add(birthDetail);
        return this;
    }
}
