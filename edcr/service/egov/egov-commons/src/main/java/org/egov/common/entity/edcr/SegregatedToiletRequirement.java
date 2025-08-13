package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SegregatedToiletRequirement extends MdmsFeatureRule {
	
	@JsonProperty("sTValueOne")
    private BigDecimal sTValueOne;
    @JsonProperty("sTValueTwo")
    private BigDecimal sTValueTwo;
    @JsonProperty("sTValueThree")
    private BigDecimal sTValueThree;
    @JsonProperty("sTValueFour")
    private BigDecimal sTValueFour;
    @JsonProperty("sTSegregatedToiletRequired")
    private BigDecimal sTSegregatedToiletRequired;
    @JsonProperty("sTSegregatedToiletProvided")
    private BigDecimal sTSegregatedToiletProvided;
    @JsonProperty("sTminDimensionRequired")
    private BigDecimal sTminDimensionRequired;
	public BigDecimal getsTValueOne() {
		return sTValueOne;
	}
	public void setsTValueOne(BigDecimal sTValueOne) {
		this.sTValueOne = sTValueOne;
	}
	public BigDecimal getsTValueTwo() {
		return sTValueTwo;
	}
	public void setsTValueTwo(BigDecimal sTValueTwo) {
		this.sTValueTwo = sTValueTwo;
	}
	public BigDecimal getsTValueThree() {
		return sTValueThree;
	}
	public void setsTValueThree(BigDecimal sTValueThree) {
		this.sTValueThree = sTValueThree;
	}
	public BigDecimal getsTValueFour() {
		return sTValueFour;
	}
	public void setsTValueFour(BigDecimal sTValueFour) {
		this.sTValueFour = sTValueFour;
	}
	public BigDecimal getsTSegregatedToiletRequired() {
		return sTSegregatedToiletRequired;
	}
	public void setsTSegregatedToiletRequired(BigDecimal sTSegregatedToiletRequired) {
		this.sTSegregatedToiletRequired = sTSegregatedToiletRequired;
	}
	public BigDecimal getsTSegregatedToiletProvided() {
		return sTSegregatedToiletProvided;
	}
	public void setsTSegregatedToiletProvided(BigDecimal sTSegregatedToiletProvided) {
		this.sTSegregatedToiletProvided = sTSegregatedToiletProvided;
	}
	public BigDecimal getsTminDimensionRequired() {
		return sTminDimensionRequired;
	}
	public void setsTminDimensionRequired(BigDecimal sTminDimensionRequired) {
		this.sTminDimensionRequired = sTminDimensionRequired;
	}
	@Override
	public String toString() {
		return "SegregatedToiletRequirement [sTValueOne=" + sTValueOne + ", sTValueTwo=" + sTValueTwo
				+ ", sTValueThree=" + sTValueThree + ", sTValueFour=" + sTValueFour + ", sTSegregatedToiletRequired="
				+ sTSegregatedToiletRequired + ", sTSegregatedToiletProvided=" + sTSegregatedToiletProvided
				+ ", sTminDimensionRequired=" + sTminDimensionRequired + "]";
	}


}
