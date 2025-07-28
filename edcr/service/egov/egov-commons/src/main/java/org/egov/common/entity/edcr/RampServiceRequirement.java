package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RampServiceRequirement extends MdmsFeatureRule {
	
	@JsonProperty("rampServiceValueOne")
    private BigDecimal rampServiceValueOne;
    @JsonProperty("rampServiceExpectedSlopeOne")
    private BigDecimal rampServiceExpectedSlopeOne;
    @JsonProperty("rampServiceDivideExpectedSlope")
    private BigDecimal rampServiceDivideExpectedSlope;
    @JsonProperty("rampServiceSlopValue")
    private BigDecimal rampServiceSlopValue;
    @JsonProperty("rampServiceBuildingHeight")
    private BigDecimal rampServiceBuildingHeight;
    @JsonProperty("rampServiceTotalLength")
    private BigDecimal rampServiceTotalLength;
    @JsonProperty("rampServiceExpectedSlopeTwo")
    private BigDecimal rampServiceExpectedSlopeTwo;
    @JsonProperty("rampServiceExpectedSlopeCompare")
    private BigDecimal rampServiceExpectedSlopeCompare;
    @JsonProperty("rampServiceExpectedSlopeCompareTrue")
    private BigDecimal rampServiceExpectedSlopeCompareTrue;
    @JsonProperty("rampServiceExpectedSlopeCompareFalse")
    private BigDecimal rampServiceExpectedSlopeCompareFalse;
    @JsonProperty("rampServiceMinHeightEntrance")
    private BigDecimal rampServiceMinHeightEntrance;
	public BigDecimal getRampServiceMinHeightEntrance() {
		return rampServiceMinHeightEntrance;
	}
	public void setRampServiceMinHeightEntrance(BigDecimal rampServiceMinHeightEntrance) {
		this.rampServiceMinHeightEntrance = rampServiceMinHeightEntrance;
	}
	public BigDecimal getRampServiceValueOne() {
		return rampServiceValueOne;
	}
	public void setRampServiceValueOne(BigDecimal rampServiceValueOne) {
		this.rampServiceValueOne = rampServiceValueOne;
	}
	public BigDecimal getRampServiceExpectedSlopeOne() {
		return rampServiceExpectedSlopeOne;
	}
	public void setRampServiceExpectedSlopeOne(BigDecimal rampServiceExpectedSlopeOne) {
		this.rampServiceExpectedSlopeOne = rampServiceExpectedSlopeOne;
	}
	public BigDecimal getRampServiceDivideExpectedSlope() {
		return rampServiceDivideExpectedSlope;
	}
	public void setRampServiceDivideExpectedSlope(BigDecimal rampServiceDivideExpectedSlope) {
		this.rampServiceDivideExpectedSlope = rampServiceDivideExpectedSlope;
	}
	public BigDecimal getRampServiceSlopValue() {
		return rampServiceSlopValue;
	}
	public void setRampServiceSlopValue(BigDecimal rampServiceSlopValue) {
		this.rampServiceSlopValue = rampServiceSlopValue;
	}
	public BigDecimal getRampServiceBuildingHeight() {
		return rampServiceBuildingHeight;
	}
	public void setRampServiceBuildingHeight(BigDecimal rampServiceBuildingHeight) {
		this.rampServiceBuildingHeight = rampServiceBuildingHeight;
	}
	public BigDecimal getRampServiceTotalLength() {
		return rampServiceTotalLength;
	}
	public void setRampServiceTotalLength(BigDecimal rampServiceTotalLength) {
		this.rampServiceTotalLength = rampServiceTotalLength;
	}
	public BigDecimal getRampServiceExpectedSlopeTwo() {
		return rampServiceExpectedSlopeTwo;
	}
	public void setRampServiceExpectedSlopeTwo(BigDecimal rampServiceExpectedSlopeTwo) {
		this.rampServiceExpectedSlopeTwo = rampServiceExpectedSlopeTwo;
	}
	public BigDecimal getRampServiceExpectedSlopeCompare() {
		return rampServiceExpectedSlopeCompare;
	}
	public void setRampServiceExpectedSlopeCompare(BigDecimal rampServiceExpectedSlopeCompare) {
		this.rampServiceExpectedSlopeCompare = rampServiceExpectedSlopeCompare;
	}
	public BigDecimal getRampServiceExpectedSlopeCompareTrue() {
		return rampServiceExpectedSlopeCompareTrue;
	}
	public void setRampServiceExpectedSlopeCompareTrue(BigDecimal rampServiceExpectedSlopeCompareTrue) {
		this.rampServiceExpectedSlopeCompareTrue = rampServiceExpectedSlopeCompareTrue;
	}
	public BigDecimal getRampServiceExpectedSlopeCompareFalse() {
		return rampServiceExpectedSlopeCompareFalse;
	}
	public void setRampServiceExpectedSlopeCompareFalse(BigDecimal rampServiceExpectedSlopeCompareFalse) {
		this.rampServiceExpectedSlopeCompareFalse = rampServiceExpectedSlopeCompareFalse;
	}
	@Override
	public String toString() {
		return "RampServiceRequirement [rampServiceValueOne=" + rampServiceValueOne + ", rampServiceExpectedSlopeOne="
				+ rampServiceExpectedSlopeOne + ", rampServiceDivideExpectedSlope=" + rampServiceDivideExpectedSlope
				+ ", rampServiceSlopValue=" + rampServiceSlopValue + ", rampServiceBuildingHeight="
				+ rampServiceBuildingHeight + ", rampServiceTotalLength=" + rampServiceTotalLength
				+ ", rampServiceExpectedSlopeTwo=" + rampServiceExpectedSlopeTwo + ", rampServiceExpectedSlopeCompare="
				+ rampServiceExpectedSlopeCompare + ", rampServiceExpectedSlopeCompareTrue="
				+ rampServiceExpectedSlopeCompareTrue + ", rampServiceExpectedSlopeCompareFalse="
				+ rampServiceMinHeightEntrance + ", rampServiceMinHeightEntrance="
				+ rampServiceExpectedSlopeCompareFalse + "]";
	}

}
