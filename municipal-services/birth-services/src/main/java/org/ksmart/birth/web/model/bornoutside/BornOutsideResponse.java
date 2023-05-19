package org.ksmart.birth.web.model.bornoutside;

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


public class BornOutsideResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("BornOutsideChildDetails")
    @Valid
    private List<BornOutsideApplication> birthDetails;


    @JsonProperty("Count")
    private int count;

    public BornOutsideResponse addBirthApplication(BornOutsideApplication birthDetail) {
        if (birthDetails == null) {
            birthDetails = new ArrayList<>();
        }
        birthDetails.add(birthDetail);
        return this;
    }
}
