package org.egov.asset.web.models;

import java.math.BigInteger;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Specific details related to the asset get stored as JSON in single column in
 * Database.
 */
@ApiModel(description = "Specific details related to the asset get stored as JSON in single column in Database.")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-12T12:56:34.514+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LandInfo implements AdditionalDetails{
	
	@JsonProperty("id")
	private String id;

    @JsonProperty("assetId")
    private String assetId;

    @JsonProperty("area") // Assuming area is a double
    private double area;

    @JsonProperty("bookValue")
    private double bookValue;

    @JsonProperty("dateOfDeedExecution")
    private String dateOfDeedExecution;

    @JsonProperty("dateOfPossession")
    private String dateOfPossession;

    @JsonProperty("anyBuiltUp")
    private String anyBuiltUp;

    @JsonProperty("isThereAnyBuiltUpOnSiteDesc")
    private String isThereAnyBuiltUpOnSiteDesc;

    @JsonProperty("typeOfTreesOnSite")
    private String typeOfTreesOnSite;

    @JsonProperty("typeOfTreesOnSiteOther")
    private String typeOfTreesOnSiteOther;

    @JsonProperty("typeOfTreesOnSiteDesc")
    private String typeOfTreesOnSiteDesc;

    @JsonProperty("fromWhomDeedTaken")
    private String fromWhomDeedTaken;

    @JsonProperty("governmentOrderNumber")
    private String governmentOrderNumber;

    @JsonProperty("collectorOrderNumber")
    private String collectorOrderNumber;

    @JsonProperty("councilResolutionNumber")
    private String councilResolutionNumber;

    @JsonProperty("awardNumber")
    private String awardNumber;

    @JsonProperty("oAndMCOI")
    private String oAndMCOI;

    @JsonProperty("oAndMTaskDetail")
    private String oAndMTaskDetail;

    @JsonProperty("totalCost") // Assuming totalCost is a double with scientific notation (E7)
    private double totalCost;

    @JsonProperty("depreciationRate") // Assuming depreciationRate is a double
    private double depreciationRate;

    @JsonProperty("costAfterDepreciation") // Assuming costAfterDepreciation is a double with scientific notation (E7)
    private double costAfterDepreciation;

    @JsonProperty("currentAssetValue") // Assuming currentAssetValue is a double with scientific notation (E7)
    private double currentAssetValue;

    @JsonProperty("revenueGeneratedByAsset") // Assuming revenueGeneratedByAsset is a double
    private double revenueGeneratedByAsset;

    @JsonProperty("osrLand")
    private String osrLand;

    @JsonProperty("isItFenced")
    private String isItFenced;

    @JsonProperty("howAssetBeingUsed")
    private String howAssetBeingUsed;

    @JsonProperty("acquisitionProcessionDetails")
    private LandAcquisitionProcessionDetails acquisitionProcessionDetails = null;

}
