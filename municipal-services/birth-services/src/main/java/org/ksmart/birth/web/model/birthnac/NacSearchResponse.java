package org.ksmart.birth.web.model.birthnac;

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
public class NacSearchResponse {
    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("nacDetails")
    @Valid
    private List<NacApplication> nacDetails;

    @JsonProperty("Count")
    private int count;

    public NacSearchResponse addKsmartBirthSearchApplication(NacApplication nacDetail) {
        if (nacDetails == null) {
        	nacDetails = new ArrayList<>();
        }
        nacDetails.add(nacDetail);
        return this;
    }
}
