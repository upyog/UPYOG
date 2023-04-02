package org.ksmart.birth.web.model.birthnac;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.response.ResponseInfo;
import org.ksmart.birth.birthnacregistry.model.NacCertificate;
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


public class NacResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("nacDetails")
    @Valid
    private List<NacApplication> nacDetails;

    @JsonProperty("NacCertificate")
    @Valid
    private NacCertificate nacCertificate;


    @JsonProperty("Count")
    private int count;

    public NacResponse addNac(NacApplication newNac) {
        if (nacDetails == null) {
        	nacDetails = new ArrayList<>();
        }
        nacDetails.add(newNac);
        return this;
    }
}
