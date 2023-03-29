package org.ksmart.birth.web.model.correction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.response.ResponseInfo;
import org.ksmart.birth.birthregistry.model.BirthCertificate;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
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


public class CorrectionResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("CorrectionApplication")
    @Valid
    private List<CorrectionApplication> correctionDetails;

    @JsonProperty("BirthCertificate")
    @Valid
    private BirthCertificate birthCertificate;


    @JsonProperty("Count")
    private int count;

    public CorrectionResponse addCorrectionApplication(CorrectionApplication correctionDetail) {
        if (correctionDetails == null) {
            correctionDetails = new ArrayList<>();
        }
        correctionDetails.add(correctionDetail);
        return this;
    }
}
