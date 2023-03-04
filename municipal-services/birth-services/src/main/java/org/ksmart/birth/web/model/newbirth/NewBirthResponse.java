package org.ksmart.birth.web.model.newbirth;

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


public class NewBirthResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("ChildDetails")
    @Valid
    private List<NewBirthApplication> ksmartBirthDetails;

    @JsonProperty("BirthCertificate")
    @Valid
    private BirthCertificate birthCertificate;


    @JsonProperty("Count")
    private int count;

    public NewBirthResponse addBirthApplication(NewBirthApplication birthDetail) {
        if (ksmartBirthDetails == null) {
            ksmartBirthDetails = new ArrayList<>();
        }
        ksmartBirthDetails.add(birthDetail);
        return this;
    }
}
