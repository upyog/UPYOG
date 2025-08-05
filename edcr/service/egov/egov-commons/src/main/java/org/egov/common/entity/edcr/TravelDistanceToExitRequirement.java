package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TravelDistanceToExitRequirement extends MdmsFeatureRule {
	
	 @JsonProperty("travelDistanceToExitValueOne")
	    private BigDecimal travelDistanceToExitValueOne;
	    @JsonProperty("travelDistanceToExitValueTwo")
	    private BigDecimal travelDistanceToExitValueTwo;
	    @JsonProperty("travelDistanceToExitValueThree")
	    private BigDecimal travelDistanceToExitValueThree;
		public BigDecimal getTravelDistanceToExitValueOne() {
			return travelDistanceToExitValueOne;
		}
		public void setTravelDistanceToExitValueOne(BigDecimal travelDistanceToExitValueOne) {
			this.travelDistanceToExitValueOne = travelDistanceToExitValueOne;
		}
		public BigDecimal getTravelDistanceToExitValueTwo() {
			return travelDistanceToExitValueTwo;
		}
		public void setTravelDistanceToExitValueTwo(BigDecimal travelDistanceToExitValueTwo) {
			this.travelDistanceToExitValueTwo = travelDistanceToExitValueTwo;
		}
		public BigDecimal getTravelDistanceToExitValueThree() {
			return travelDistanceToExitValueThree;
		}
		public void setTravelDistanceToExitValueThree(BigDecimal travelDistanceToExitValueThree) {
			this.travelDistanceToExitValueThree = travelDistanceToExitValueThree;
		}
		@Override
		public String toString() {
			return "TravelDistanceToExitRequirement [travelDistanceToExitValueOne=" + travelDistanceToExitValueOne
					+ ", travelDistanceToExitValueTwo=" + travelDistanceToExitValueTwo
					+ ", travelDistanceToExitValueThree=" + travelDistanceToExitValueThree + "]";
		}

}
