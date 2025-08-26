package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OverheadElectricalLineServiceRequirement extends MdmsFeatureRule {
	
	@JsonProperty("overheadVerticalDistance_11000")
    private BigDecimal overheadVerticalDistance_11000;
    @JsonProperty("overheadVerticalDistance_33000")
    private BigDecimal overheadVerticalDistance_33000;
    @JsonProperty("overheadHorizontalDistance_11000")
    private BigDecimal overheadHorizontalDistance_11000;
    @JsonProperty("overheadHorizontalDistance_33000")
    private BigDecimal overheadHorizontalDistance_33000;
    @JsonProperty("overheadVoltage_11000")
    private BigDecimal overheadVoltage_11000;
    @JsonProperty("overheadVoltage_33000")
    private BigDecimal overheadVoltage_33000;
	public BigDecimal getOverheadVerticalDistance_11000() {
		return overheadVerticalDistance_11000;
	}
	public void setOverheadVerticalDistance_11000(BigDecimal overheadVerticalDistance_11000) {
		this.overheadVerticalDistance_11000 = overheadVerticalDistance_11000;
	}
	public BigDecimal getOverheadVerticalDistance_33000() {
		return overheadVerticalDistance_33000;
	}
	public void setOverheadVerticalDistance_33000(BigDecimal overheadVerticalDistance_33000) {
		this.overheadVerticalDistance_33000 = overheadVerticalDistance_33000;
	}
	public BigDecimal getOverheadHorizontalDistance_11000() {
		return overheadHorizontalDistance_11000;
	}
	public void setOverheadHorizontalDistance_11000(BigDecimal overheadHorizontalDistance_11000) {
		this.overheadHorizontalDistance_11000 = overheadHorizontalDistance_11000;
	}
	public BigDecimal getOverheadHorizontalDistance_33000() {
		return overheadHorizontalDistance_33000;
	}
	public void setOverheadHorizontalDistance_33000(BigDecimal overheadHorizontalDistance_33000) {
		this.overheadHorizontalDistance_33000 = overheadHorizontalDistance_33000;
	}
	public BigDecimal getOverheadVoltage_11000() {
		return overheadVoltage_11000;
	}
	public void setOverheadVoltage_11000(BigDecimal overheadVoltage_11000) {
		this.overheadVoltage_11000 = overheadVoltage_11000;
	}
	public BigDecimal getOverheadVoltage_33000() {
		return overheadVoltage_33000;
	}
	public void setOverheadVoltage_33000(BigDecimal overheadVoltage_33000) {
		this.overheadVoltage_33000 = overheadVoltage_33000;
	}
	@Override
	public String toString() {
		return "OverheadElectricalLineServiceRequirement [overheadVerticalDistance_11000="
				+ overheadVerticalDistance_11000 + ", overheadVerticalDistance_33000=" + overheadVerticalDistance_33000
				+ ", overheadHorizontalDistance_11000=" + overheadHorizontalDistance_11000
				+ ", overheadHorizontalDistance_33000=" + overheadHorizontalDistance_33000 + ", overheadVoltage_11000="
				+ overheadVoltage_11000 + ", overheadVoltage_33000=" + overheadVoltage_33000 + "]";
	}

}
