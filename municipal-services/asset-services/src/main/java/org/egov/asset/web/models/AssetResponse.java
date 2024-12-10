package org.egov.asset.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.egov.asset.dto.AssetDTO;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Contract class to send response. Array of items are used in case of search
 * results or response for create, whereas single item is used for update
 */
@ApiModel(description = "Contract class to send response. Array of items are used in case of search results or response for create, whereas single item is used for update")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-12T12:56:34.514+05:30")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AssetResponse {
    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo = null;

    @JsonProperty("Assets")
    @Valid
    private List<AssetDTO> assets = null;

    public AssetResponse addAssetsItem(Asset assetsItem) {
        if (this.assets == null) {
            this.assets = new ArrayList<>();
        }
        this.assets.add(assetsItem);
        return this;
    }

    public AssetResponse Asset(List<AssetDTO> assets) {
        this.assets = assets;
        return this;
    }


}
