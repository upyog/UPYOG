package org.ksmart.birth.birthapplication.model.birth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.response.ResponseInfo;
import org.ksmart.birth.birthapplication.model.BirthApplicationDetail;
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


public class BirthApplicationResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("BirthDetails")
    @Valid
    private List<BirthApplicationDetail> birthDetails;

    @JsonProperty("BirthCertificate")
    @Valid
    private BirthCertificate birthCertificate;


    @JsonProperty("Count")
    private int count;

    public BirthApplicationResponse addBirthApplication(BirthApplicationDetail birthDetail) {
        if (birthDetails == null) {
            birthDetails = new ArrayList<>();
        }
        birthDetails.add(birthDetail);
        return this;
    }
}
