package org.egov.asset.web.models.calcontract;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.response.ResponseInfo;

import javax.validation.Valid;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepreciationRes {

    @JsonProperty("ResponseInfo")
    @Valid
    private ResponseInfo responseInfo = null;

    @JsonProperty("DepreciationDetails")
    @Valid
    private List<DepreciationDetail> depreciation;

}
