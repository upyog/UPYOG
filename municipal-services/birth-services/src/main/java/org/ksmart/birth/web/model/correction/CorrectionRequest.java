package org.ksmart.birth.web.model.correction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CorrectionRequest {
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("CorrectionDetails")
    @Valid
    private List<CorrectionApplication> correctionDetails;

    public CorrectionRequest addCorrectionBirthDetails(CorrectionApplication correctionDetail) {
        if (correctionDetails == null) {
            correctionDetails = null;
        }
        return this;
    }
}
