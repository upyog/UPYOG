package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BathroomRequirement extends MdmsFeatureRule {
	
	    @JsonProperty("bathroomtotalArea")
	    private BigDecimal bathroomtotalArea;
	    @JsonProperty("bathroomMinWidth")
	    private BigDecimal bathroomMinWidth;
	    
	    public BigDecimal getBathroomtotalArea() { return bathroomtotalArea; }
	    public void setBathroomtotalArea(BigDecimal bathroomtotalArea) { this.bathroomtotalArea = bathroomtotalArea; }
	    public BigDecimal getBathroomMinWidth() { return bathroomMinWidth; }
	    public void setBathroomMinWidth(BigDecimal bathroomMinWidth) { this.bathroomMinWidth = bathroomMinWidth; }
	    
		@Override
		public String toString() {
			return "BathroomRequirement [bathroomtotalArea=" + bathroomtotalArea + ", bathroomMinWidth="
					+ bathroomMinWidth + "]";
		}
	    
	    

}
