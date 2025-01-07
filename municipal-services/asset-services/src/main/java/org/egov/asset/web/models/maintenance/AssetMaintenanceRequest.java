package org.egov.asset.web.models.maintenance;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.asset.web.models.disposal.AssetDisposal;
import org.egov.common.contract.request.RequestInfo;

import javax.validation.Valid;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetMaintenanceRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo = null;

    @JsonProperty("AssetMaintenance")
    @Valid
    private AssetMaintenance assetMaintenance = null;

}
