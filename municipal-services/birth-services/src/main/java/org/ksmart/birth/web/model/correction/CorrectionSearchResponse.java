package org.ksmart.birth.web.model.correction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.response.ResponseInfo;
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
public class CorrectionSearchResponse {
    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("CorrectionDetails")
    @Valid
    private List<CorrectionApplication> newCorrectionDetails;
    public CorrectionSearchResponse addKCorrectionSearchApplication(CorrectionApplication newCorrectionDetail) {
        if (newCorrectionDetails == null) {
            newCorrectionDetails = new ArrayList<>();
        }
        newCorrectionDetails.add(newCorrectionDetail);
        return this;
    }
}
