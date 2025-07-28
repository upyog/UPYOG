package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BathroomRequirement extends MdmsFeatureRule {
	
	    @JsonProperty("bathroomtotalArea")
	    private BigDecimal bathroomtotalArea;
	    @JsonProperty("bathroomMinWidth")
	    private BigDecimal bathroomMinWidth;
	    
	    @JsonProperty("bathAndStoreVentilationArea")
	    private BigDecimal bathAndStoreVentilationArea;
	    
	    @JsonProperty("bathAndStoreVentilationWidth")
	    private BigDecimal bathAndStoreVentilationWidth;
	    
	    public BigDecimal getBathAndStoreVentilationArea() {
			return bathAndStoreVentilationArea;
		}
		public void setBathAndStoreVentilationArea(BigDecimal bathAndStoreVentilationArea) {
			this.bathAndStoreVentilationArea = bathAndStoreVentilationArea;
		}
		public BigDecimal getBathAndStoreVentilationWidth() {
			return bathAndStoreVentilationWidth;
		}
		public void setBathAndStoreVentilationWidth(BigDecimal bathAndStoreVentilationWidth) {
			this.bathAndStoreVentilationWidth = bathAndStoreVentilationWidth;
		}
		public BigDecimal getBathroomtotalArea() { return bathroomtotalArea; }
	    public void setBathroomtotalArea(BigDecimal bathroomtotalArea) { this.bathroomtotalArea = bathroomtotalArea; }
	    public BigDecimal getBathroomMinWidth() { return bathroomMinWidth; }
	    public void setBathroomMinWidth(BigDecimal bathroomMinWidth) { this.bathroomMinWidth = bathroomMinWidth; }
	    
		@Override
		public String toString() {
			return "BathroomRequirement [bathroomtotalArea=" + bathroomtotalArea + ", bathroomMinWidth="
					+ bathroomMinWidth + ", bathAndStoreVentilationArea=" + bathAndStoreVentilationArea
					+ ", bathAndStoreVentilationWidth=" + bathAndStoreVentilationWidth + "]";
		}
	    
	    

}
