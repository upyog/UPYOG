package org.egov.asset.web.models.disposal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;

import javax.validation.Valid;
import java.util.List;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetDisposalRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo = null;

    @JsonProperty("AssetDisposal")
    @Valid
    private AssetDisposal assetDisposal = null;

}
