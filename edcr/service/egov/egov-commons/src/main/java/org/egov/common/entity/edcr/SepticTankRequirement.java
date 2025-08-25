package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SepticTankRequirement extends MdmsFeatureRule {

	@JsonProperty("septicTankMinDisWatersrc")
    private BigDecimal septicTankMinDisWatersrc;
    @JsonProperty("septicTankMinDisBuilding")
    private BigDecimal septicTankMinDisBuilding;
	public BigDecimal getSepticTankMinDisWatersrc() {
		return septicTankMinDisWatersrc;
	}
	public void setSepticTankMinDisWatersrc(BigDecimal septicTankMinDisWatersrc) {
		this.septicTankMinDisWatersrc = septicTankMinDisWatersrc;
	}
	public BigDecimal getSepticTankMinDisBuilding() {
		return septicTankMinDisBuilding;
	}
	public void setSepticTankMinDisBuilding(BigDecimal septicTankMinDisBuilding) {
		this.septicTankMinDisBuilding = septicTankMinDisBuilding;
	}
	@Override
	public String toString() {
		return "SepticTankRequirement [septicTankMinDisWatersrc=" + septicTankMinDisWatersrc
				+ ", septicTankMinDisBuilding=" + septicTankMinDisBuilding + "]";
	}
}
