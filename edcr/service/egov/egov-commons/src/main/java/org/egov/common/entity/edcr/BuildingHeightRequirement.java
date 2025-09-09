package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BuildingHeightRequirement extends MdmsFeatureRule {
	
	 @JsonProperty("buildingHeightMaximumDistancetoRoad")
	    private BigDecimal buildingHeightMaximumDistancetoRoad;
	    @JsonProperty("buildingHeightMaxBuildingHeight")
	    private BigDecimal buildingHeightMaxBuildingHeight;
	    
	    public BigDecimal getBuildingHeightMaximumDistancetoRoad() { return buildingHeightMaximumDistancetoRoad; }
	    public void setBuildingHeightMaximumDistancetoRoad(BigDecimal buildingHeightMaximumDistancetoRoad) { this.buildingHeightMaximumDistancetoRoad = buildingHeightMaximumDistancetoRoad; }
	    public BigDecimal getBuildingHeightMaxBuildingHeight() { return buildingHeightMaxBuildingHeight; }
	    public void setBuildingHeightMaxBuildingHeight(BigDecimal buildingHeightMaxBuildingHeight) { this.buildingHeightMaxBuildingHeight = buildingHeightMaxBuildingHeight; }
	    
		@Override
		public String toString() {
			return "BuildingHeightRequirement [buildingHeightMaximumDistancetoRoad="
					+ buildingHeightMaximumDistancetoRoad + ", buildingHeightMaxBuildingHeight="
					+ buildingHeightMaxBuildingHeight + "]";
		}

}
