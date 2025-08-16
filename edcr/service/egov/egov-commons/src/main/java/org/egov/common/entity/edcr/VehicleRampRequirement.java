package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VehicleRampRequirement extends MdmsFeatureRule {
	
	 @JsonProperty("vehicleRampValue")
	    private BigDecimal vehicleRampValue;
	    @JsonProperty("vehicleRampSlopeValueOne")
	    private BigDecimal vehicleRampSlopeValueOne;
	    @JsonProperty("vehicleRampSlopeValueTwo")
	    private BigDecimal vehicleRampSlopeValueTwo;
	    @JsonProperty("vehicleRampSlopeMinWidthValueOne")
	    private BigDecimal vehicleRampSlopeMinWidthValueOne;
	    @JsonProperty("vehicleRampSlopeMinWidthValueTwo")
	    private BigDecimal vehicleRampSlopeMinWidthValueTwo;
	    @JsonProperty("vehicleRampSlopeMinWidthValueThree")
	    private BigDecimal vehicleRampSlopeMinWidthValueThree;
		public BigDecimal getVehicleRampValue() {
			return vehicleRampValue;
		}
		public void setVehicleRampValue(BigDecimal vehicleRampValue) {
			this.vehicleRampValue = vehicleRampValue;
		}
		public BigDecimal getVehicleRampSlopeValueOne() {
			return vehicleRampSlopeValueOne;
		}
		public void setVehicleRampSlopeValueOne(BigDecimal vehicleRampSlopeValueOne) {
			this.vehicleRampSlopeValueOne = vehicleRampSlopeValueOne;
		}
		public BigDecimal getVehicleRampSlopeValueTwo() {
			return vehicleRampSlopeValueTwo;
		}
		public void setVehicleRampSlopeValueTwo(BigDecimal vehicleRampSlopeValueTwo) {
			this.vehicleRampSlopeValueTwo = vehicleRampSlopeValueTwo;
		}
		public BigDecimal getVehicleRampSlopeMinWidthValueOne() {
			return vehicleRampSlopeMinWidthValueOne;
		}
		public void setVehicleRampSlopeMinWidthValueOne(BigDecimal vehicleRampSlopeMinWidthValueOne) {
			this.vehicleRampSlopeMinWidthValueOne = vehicleRampSlopeMinWidthValueOne;
		}
		public BigDecimal getVehicleRampSlopeMinWidthValueTwo() {
			return vehicleRampSlopeMinWidthValueTwo;
		}
		public void setVehicleRampSlopeMinWidthValueTwo(BigDecimal vehicleRampSlopeMinWidthValueTwo) {
			this.vehicleRampSlopeMinWidthValueTwo = vehicleRampSlopeMinWidthValueTwo;
		}
		public BigDecimal getVehicleRampSlopeMinWidthValueThree() {
			return vehicleRampSlopeMinWidthValueThree;
		}
		public void setVehicleRampSlopeMinWidthValueThree(BigDecimal vehicleRampSlopeMinWidthValueThree) {
			this.vehicleRampSlopeMinWidthValueThree = vehicleRampSlopeMinWidthValueThree;
		}
		@Override
		public String toString() {
			return "VehicleRampRequirement [vehicleRampValue=" + vehicleRampValue + ", vehicleRampSlopeValueOne="
					+ vehicleRampSlopeValueOne + ", vehicleRampSlopeValueTwo=" + vehicleRampSlopeValueTwo
					+ ", vehicleRampSlopeMinWidthValueOne=" + vehicleRampSlopeMinWidthValueOne
					+ ", vehicleRampSlopeMinWidthValueTwo=" + vehicleRampSlopeMinWidthValueTwo
					+ ", vehicleRampSlopeMinWidthValueThree=" + vehicleRampSlopeMinWidthValueThree + "]";
		}

}
