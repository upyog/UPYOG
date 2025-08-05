package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RiverDistanceRequirement extends MdmsFeatureRule {
	
	 @JsonProperty("rDminDistanceFromProtectionWall")
	    private BigDecimal rDminDistanceFromProtectionWall;

	    @JsonProperty("rDminDistanceFromEmbankment")
	    private BigDecimal rDminDistanceFromEmbankment;

	    @JsonProperty("rDminDistanceFromMainRiverEdge")
	    private BigDecimal rDminDistanceFromMainRiverEdge;

	    @JsonProperty("rDminDistanceFromSubRiver")
	    private BigDecimal rDminDistanceFromSubRiver;

	    // Getters and Setters

	    public BigDecimal getrDminDistanceFromProtectionWall() {
	        return rDminDistanceFromProtectionWall;
	    }

	    public void setrDminDistanceFromProtectionWall(BigDecimal rDminDistanceFromProtectionWall) {
	        this.rDminDistanceFromProtectionWall = rDminDistanceFromProtectionWall;
	    }

	    public BigDecimal getrDminDistanceFromEmbankment() {
	        return rDminDistanceFromEmbankment;
	    }

	    public void setrDminDistanceFromEmbankment(BigDecimal rDminDistanceFromEmbankment) {
	        this.rDminDistanceFromEmbankment = rDminDistanceFromEmbankment;
	    }

	    public BigDecimal getrDminDistanceFromMainRiverEdge() {
	        return rDminDistanceFromMainRiverEdge;
	    }

	    public void setrDminDistanceFromMainRiverEdge(BigDecimal rDminDistanceFromMainRiverEdge) {
	        this.rDminDistanceFromMainRiverEdge = rDminDistanceFromMainRiverEdge;
	    }

	    public BigDecimal getrDminDistanceFromSubRiver() {
	        return rDminDistanceFromSubRiver;
	    }

	    public void setrDminDistanceFromSubRiver(BigDecimal rDminDistanceFromSubRiver) {
	        this.rDminDistanceFromSubRiver = rDminDistanceFromSubRiver;
	    }

		@Override
		public String toString() {
			return "RiverDistanceRequirement [rDminDistanceFromProtectionWall=" + rDminDistanceFromProtectionWall
					+ ", rDminDistanceFromEmbankment=" + rDminDistanceFromEmbankment
					+ ", rDminDistanceFromMainRiverEdge=" + rDminDistanceFromMainRiverEdge
					+ ", rDminDistanceFromSubRiver=" + rDminDistanceFromSubRiver + "]";
		}

}
