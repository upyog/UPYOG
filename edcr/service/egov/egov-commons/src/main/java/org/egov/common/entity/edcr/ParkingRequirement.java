package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ParkingRequirement extends MdmsFeatureRule {
	
	 @Override
	public String toString() {
		return "ParkingRequirement [noOfParking=" + noOfParking + "]";
	}

	@JsonProperty("noOfParking")
	    private BigDecimal noOfParking;

	public BigDecimal getNoOfParking() {
		return noOfParking;
	}

	public void setNoOfParking(BigDecimal noOfParking) {
		this.noOfParking = noOfParking;
	}

}
