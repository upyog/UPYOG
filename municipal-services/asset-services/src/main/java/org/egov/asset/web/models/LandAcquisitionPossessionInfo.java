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
public class LandAcquisitionPossessionInfo {

    @JsonProperty("compensationPaidOrAcquisition")
    private String compensationPaidOrAcquisition = null;

    @JsonProperty("specialConditionsOnHolding ")
    private String specialConditionsOnHolding = null;

    @JsonProperty("assetImprovmentDate")
    private String assetImprovmentDate = null;

    @JsonProperty("tasksDoneForAssetImprovmentDesc")
    private String tasksDoneForAssetImprovmentDesc = null;

    @JsonProperty("depreciationRate")
    private String depreciationRate = null;

    @JsonProperty("revenueGeneratedByAsset")
    private String revenueGeneratedByAsset = null;


}
