package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GuardRoomRequirement extends MdmsFeatureRule {

	@JsonProperty("guardRoomMinHeight")
	private BigDecimal guardRoomMinHeight;
	@JsonProperty("guardRoomMinWidth")
	private BigDecimal guardRoomMinWidth;
	@JsonProperty("guardRoomMinArea")
	private BigDecimal guardRoomMinArea;
	@JsonProperty("guardRoomMinCabinHeightOne")
	private BigDecimal guardRoomMinCabinHeightOne;

	@Override
	public String toString() {
		return "GuardRoomRequirement [guardRoomMinHeight=" + guardRoomMinHeight + ", guardRoomMinWidth="
				+ guardRoomMinWidth + ", guardRoomMinArea=" + guardRoomMinArea + ", guardRoomMinCabinHeightOne="
				+ guardRoomMinCabinHeightOne + ", guardRoomMinCabinHeightTwo=" + guardRoomMinCabinHeightTwo + "]";
	}

	@JsonProperty("guardRoomMinCabinHeightTwo")
	private BigDecimal guardRoomMinCabinHeightTwo;

	public BigDecimal getGuardRoomMinHeight() {
		return guardRoomMinHeight;
	}

	public void setGuardRoomMinHeight(BigDecimal guardRoomMinHeight) {
		this.guardRoomMinHeight = guardRoomMinHeight;
	}

	public BigDecimal getGuardRoomMinWidth() {
		return guardRoomMinWidth;
	}

	public void setGuardRoomMinWidth(BigDecimal guardRoomMinWidth) {
		this.guardRoomMinWidth = guardRoomMinWidth;
	}

	public BigDecimal getGuardRoomMinArea() {
		return guardRoomMinArea;
	}

	public void setGuardRoomMinArea(BigDecimal guardRoomMinArea) {
		this.guardRoomMinArea = guardRoomMinArea;
	}

	public BigDecimal getGuardRoomMinCabinHeightOne() {
		return guardRoomMinCabinHeightOne;
	}

	public void setGuardRoomMinCabinHeightOne(BigDecimal guardRoomMinCabinHeightOne) {
		this.guardRoomMinCabinHeightOne = guardRoomMinCabinHeightOne;
	}

	public BigDecimal getGuardRoomMinCabinHeightTwo() {
		return guardRoomMinCabinHeightTwo;
	}

	public void setGuardRoomMinCabinHeightTwo(BigDecimal guardRoomMinCabinHeightTwo) {
		this.guardRoomMinCabinHeightTwo = guardRoomMinCabinHeightTwo;
	}

}
