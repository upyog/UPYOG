package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GovtBuildingDistanceRequirement extends MdmsFeatureRule {
	
	 public BigDecimal getGovtBuildingDistanceValue() {
		return GovtBuildingDistanceValue;
	}
	public void setGovtBuildingDistanceValue(BigDecimal govtBuildingDistanceValue) {
		GovtBuildingDistanceValue = govtBuildingDistanceValue;
	}
	public BigDecimal getGovtBuildingDistanceMaxHeight() {
		return GovtBuildingDistanceMaxHeight;
	}
	public void setGovtBuildingDistanceMaxHeight(BigDecimal govtBuildingDistanceMaxHeight) {
		GovtBuildingDistanceMaxHeight = govtBuildingDistanceMaxHeight;
	}
	@JsonProperty("GovtBuildingDistanceValue")
	    private BigDecimal GovtBuildingDistanceValue;
	    @JsonProperty("GovtBuildingDistanceMaxHeight")
	    private BigDecimal GovtBuildingDistanceMaxHeight;

}
