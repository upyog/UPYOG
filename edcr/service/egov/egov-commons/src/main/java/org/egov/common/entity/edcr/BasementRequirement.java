package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BasementRequirement extends MdmsFeatureRule {
	
	 @JsonProperty("permissibleone")
	    private BigDecimal permissibleOne;

	    @JsonProperty("permissibletwo")
	    private BigDecimal permissibleTwo;

	    @JsonProperty("permissiblethree")
	    private BigDecimal permissibleThree;
	    
	    public BigDecimal getPermissibleOne() { return permissibleOne; }
	    public void setPermissibleOne(BigDecimal permissibleOne) { this.permissibleOne = permissibleOne; }

	    public BigDecimal getPermissibleTwo() { return permissibleTwo; }
	    public void setPermissibleTwo(BigDecimal permissibleTwo) { this.permissibleTwo = permissibleTwo; }

	    public BigDecimal getPermissibleThree() { return permissibleThree; }
	    public void setPermissibleThree(BigDecimal permissibleThree) { this.permissibleThree = permissibleThree; }
	    
		@Override
		public String toString() {
			return "BasementRequirement [permissibleOne=" + permissibleOne + ", permissibleTwo=" + permissibleTwo
					+ ", permissibleThree=" + permissibleThree + "]";
		}

}
