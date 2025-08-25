package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FireTenderMovementRequirement extends MdmsFeatureRule {
	 public BigDecimal getFireTenderMovementValueOne() {
		return fireTenderMovementValueOne;
	}
	public void setFireTenderMovementValueOne(BigDecimal fireTenderMovementValueOne) {
		this.fireTenderMovementValueOne = fireTenderMovementValueOne;
	}
	public BigDecimal getFireTenderMovementValueTwo() {
		return fireTenderMovementValueTwo;
	}
	public void setFireTenderMovementValueTwo(BigDecimal fireTenderMovementValueTwo) {
		this.fireTenderMovementValueTwo = fireTenderMovementValueTwo;
	}
	@JsonProperty("fireTenderMovementValueOne")
	    private BigDecimal fireTenderMovementValueOne;
	    @JsonProperty("fireTenderMovementValueTwo")
	    private BigDecimal fireTenderMovementValueTwo;

		@Override
		public String toString() {
			return "FireTenderMovementRequirement [fireTenderMovementValueOne=" + fireTenderMovementValueOne
					+ ", fireTenderMovementValueTwo=" + fireTenderMovementValueTwo + "]";
		}

}
