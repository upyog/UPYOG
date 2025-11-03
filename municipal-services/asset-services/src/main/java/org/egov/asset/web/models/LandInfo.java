package org.egov.asset.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.validation.annotation.Validated;

/**
 * Specific details related to the asset get stored as JSON in single column in Database.
 */
@ApiModel(description = "Specific details related to the asset get stored as JSON in single column in Database.")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-12T12:56:34.514+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LandInfo {
    @JsonProperty("assetParentCategory")
    private String assetParentCategory = null;

    @JsonProperty("assetAssetCategory")
    private String assetAssetCategory = null;

    @JsonProperty("assetAssetSubCategory")
    private String assetAssetSubCategory = null;

    @JsonProperty("typeofTreesOnSite")
    private String typeofTreesOnSite = null;

    @JsonProperty("typeofTreesOnSiteOther")
    private String typeofTreesOnSiteOther = null;

    @JsonProperty("typeofTreesOnSiteDesc")
    private String typeofTreesOnSiteDesc = null;

    @JsonProperty("Currentassetvalue")
    private String currentassetvalue = null;

    @JsonProperty("Revenuegeneratedbyasset")
    private String revenuegeneratedbyasset = null;

    @JsonProperty("acquisitionProsessionDetails")
    private String LandAcquisitionProssessionInfo = null;

}

