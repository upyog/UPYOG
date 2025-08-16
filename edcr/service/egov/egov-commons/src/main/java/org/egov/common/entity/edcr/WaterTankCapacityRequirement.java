package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WaterTankCapacityRequirement extends MdmsFeatureRule {
	
	@JsonProperty("waterTankCapacityArea")
    private BigDecimal waterTankCapacityArea;
    @JsonProperty("waterTankCapacityExpected")
    private BigDecimal waterTankCapacityExpected;
	public BigDecimal getWaterTankCapacityArea() {
		return waterTankCapacityArea;
	}
	public void setWaterTankCapacityArea(BigDecimal waterTankCapacityArea) {
		this.waterTankCapacityArea = waterTankCapacityArea;
	}
	public BigDecimal getWaterTankCapacityExpected() {
		return waterTankCapacityExpected;
	}
	public void setWaterTankCapacityExpected(BigDecimal waterTankCapacityExpected) {
		this.waterTankCapacityExpected = waterTankCapacityExpected;
	}
	@Override
	public String toString() {
		return "WaterTankCapacityRequirement [waterTankCapacityArea=" + waterTankCapacityArea
				+ ", waterTankCapacityExpected=" + waterTankCapacityExpected + "]";
	}

}
