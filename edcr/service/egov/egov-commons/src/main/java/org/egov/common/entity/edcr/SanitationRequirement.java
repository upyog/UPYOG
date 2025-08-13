package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SanitationRequirement extends MdmsFeatureRule {
	
	@JsonProperty("sanitationMinAreaofSPWC")
    private BigDecimal sanitationMinAreaofSPWC;
    @JsonProperty("sanitationMinDimensionofSPWC")
    private BigDecimal sanitationMinDimensionofSPWC;
    @JsonProperty("sanitationMinatGroundFloor")
    private BigDecimal sanitationMinatGroundFloor;
    @JsonProperty("sanitationFloorMultiplier")
    private BigDecimal sanitationFloorMultiplier;
	public BigDecimal getSanitationMinAreaofSPWC() {
		return sanitationMinAreaofSPWC;
	}
	public void setSanitationMinAreaofSPWC(BigDecimal sanitationMinAreaofSPWC) {
		this.sanitationMinAreaofSPWC = sanitationMinAreaofSPWC;
	}
	public BigDecimal getSanitationMinDimensionofSPWC() {
		return sanitationMinDimensionofSPWC;
	}
	public void setSanitationMinDimensionofSPWC(BigDecimal sanitationMinDimensionofSPWC) {
		this.sanitationMinDimensionofSPWC = sanitationMinDimensionofSPWC;
	}
	public BigDecimal getSanitationMinatGroundFloor() {
		return sanitationMinatGroundFloor;
	}
	public void setSanitationMinatGroundFloor(BigDecimal sanitationMinatGroundFloor) {
		this.sanitationMinatGroundFloor = sanitationMinatGroundFloor;
	}
	public BigDecimal getSanitationFloorMultiplier() {
		return sanitationFloorMultiplier;
	}
	public void setSanitationFloorMultiplier(BigDecimal sanitationFloorMultiplier) {
		this.sanitationFloorMultiplier = sanitationFloorMultiplier;
	}
	@Override
	public String toString() {
		return "SanitationRequirement [sanitationMinAreaofSPWC=" + sanitationMinAreaofSPWC
				+ ", sanitationMinDimensionofSPWC=" + sanitationMinDimensionofSPWC + ", sanitationMinatGroundFloor="
				+ sanitationMinatGroundFloor + ", sanitationFloorMultiplier=" + sanitationFloorMultiplier + "]";
	}

}
