package org.egov.asset.web.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

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
public class LandInfo   {
        @JsonProperty("assetParentCategory")
        private String assetParentCategory = null;

        @JsonProperty("assetAssetCategory")
        private String assetAssetCategory = null;

        @JsonProperty("assetAssetSubCategory")
        private String assetAssetSubCategory = null;
        
        @JsonProperty("OSRLand")
        private String osRLand = null;
        
        @JsonProperty("isitfenced")
        private String isitfenced = null;
        
        @JsonProperty("isItfenced")
        private String isItfencedDesc = null;

        @JsonProperty("isThereAnyBuiltupOnSite")
        private String isThereAnyBuiltupOnSite = null;

        @JsonProperty("isThereAnyBuiltupOnSiteDesc")
        private String isThereAnyBuiltupOnSiteDesc = null;
        
        @JsonProperty("typeofTreesOnSite")
        private String typeofTreesOnSite = null;
        
        @JsonProperty("typeofTreesOnSiteOther")
        private String typeofTreesOnSiteOther = null;
        
        @JsonProperty("typeofTreesOnSiteDesc")
        private String typeofTreesOnSiteDesc = null;

        @JsonProperty("AwardNumber")
        private String awardNumber = null;

        @JsonProperty("BookValue")
        private String bookValue = null;

        @JsonProperty("CollectororderNumber")
        private String collectororderNumber = null;

        @JsonProperty("Costafterdepriciation")
        private String costafterdepriciation = null;

        @JsonProperty("CouncilResolutionNumber")
        private String councilResolutionNumber = null;

        @JsonProperty("Currentassetvalue")
        private String currentassetvalue = null;

        @JsonProperty("DateofDeedExecution")
        private String dateofDeedExecution = null;

        @JsonProperty("DateofPossesion")
        private String dateofPossesion = null;

        @JsonProperty("DepriciationRate")
        private String depriciationRate = null;

        @JsonProperty("FromWhomDeedTaken")
        private String fromWhomDeedTaken = null;

        @JsonProperty("GovernmentorderNumber")
        private String governmentorderNumber = null;

        @JsonProperty("OandMCOI")
        private String oandMCOI = null;

        @JsonProperty("OandMTaskDetail")
        private String oandMTaskDetail = null;

        @JsonProperty("Revenuegeneratedbyasset")
        private String revenuegeneratedbyasset = null;

        @JsonProperty("Totalcost")
        private String totalcost = null;

        @JsonProperty("howassetbeingused")
        private String howassetbeingused = null;

        @JsonProperty("acquisitionProsessionDetails")
        private String LandAcquisitionProssessionInfo = null;

}

