package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FireStairRequirement extends MdmsFeatureRule {
	
	 @JsonProperty("fireStairExpectedNoofRise")
	    private BigDecimal fireStairExpectedNoofRise;
	    public BigDecimal getFireStairExpectedNoofRise() {
		return fireStairExpectedNoofRise;
	}
	public void setFireStairExpectedNoofRise(BigDecimal fireStairExpectedNoofRise) {
		this.fireStairExpectedNoofRise = fireStairExpectedNoofRise;
	}
	@Override
	public String toString() {
		return "FireStairRequirement [fireStairExpectedNoofRise=" + fireStairExpectedNoofRise
				+ ", fireStairMinimumWidth=" + fireStairMinimumWidth + ", fireStairRequiredTread="
				+ fireStairRequiredTread + "]";
	}
	public BigDecimal getFireStairMinimumWidth() {
		return fireStairMinimumWidth;
	}
	public void setFireStairMinimumWidth(BigDecimal fireStairMinimumWidth) {
		this.fireStairMinimumWidth = fireStairMinimumWidth;
	}
	public BigDecimal getFireStairRequiredTread() {
		return fireStairRequiredTread;
	}
	public void setFireStairRequiredTread(BigDecimal fireStairRequiredTread) {
		this.fireStairRequiredTread = fireStairRequiredTread;
	}
		@JsonProperty("fireStairMinimumWidth")
	    private BigDecimal fireStairMinimumWidth;
	    @JsonProperty("fireStairRequiredTread")
	    private BigDecimal fireStairRequiredTread;

}
