package org.egov.asset.web.models.disposal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.asset.dto.AssetDTO;
import org.egov.common.contract.response.ResponseInfo;

import javax.validation.Valid;
import java.util.List;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetDisposalResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo = null;

    @JsonProperty("AssetDisposals")
    @Valid
    private List<AssetDisposal> assetDisposals = null;
}
