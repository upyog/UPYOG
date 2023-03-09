package org.ksmart.birth.web.model.outsidecountry;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.response.ResponseInfo;
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
public class BirthOutsideearchResponse {
    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("ChildDetails")
    @Valid
    private List<BirthOutsideApplication> newBirthDetails;
    public BirthOutsideearchResponse addKsmartBirthSearchApplication(BirthOutsideApplication birthDetail) {
        if (newBirthDetails == null) {
            newBirthDetails = new ArrayList<>();
        }
        newBirthDetails.add(birthDetail);
        return this;
    }
}
