package org.egov.asset.web.models.maintenance;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.asset.web.models.disposal.AssetDisposal;
import org.egov.common.contract.response.ResponseInfo;

import javax.validation.Valid;
import java.util.List;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetMaintenanceResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo = null;

    @JsonProperty("AssetMaintenance")
    @Valid
    private List<AssetMaintenance> assetMaintenance = null;
}
