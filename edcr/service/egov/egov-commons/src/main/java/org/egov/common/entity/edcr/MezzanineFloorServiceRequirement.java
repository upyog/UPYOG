package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MezzanineFloorServiceRequirement extends MdmsFeatureRule {
	
	  public BigDecimal getMezzanineArea() {
		return mezzanineArea;
	}

	public void setMezzanineArea(BigDecimal mezzanineArea) {
		this.mezzanineArea = mezzanineArea;
	}

	public BigDecimal getMezzanineHeight() {
		return mezzanineHeight;
	}

	public void setMezzanineHeight(BigDecimal mezzanineHeight) {
		this.mezzanineHeight = mezzanineHeight;
	}

	@Override
	public String toString() {
		return "MezzanineFloorServiceRequirement [mezzanineArea=" + mezzanineArea + ", mezzanineHeight="
				+ mezzanineHeight + ", mezzanineBuiltUpArea=" + mezzanineBuiltUpArea + "]";
	}

	public BigDecimal getMezzanineBuiltUpArea() {
		return mezzanineBuiltUpArea;
	}

	public void setMezzanineBuiltUpArea(BigDecimal mezzanineBuiltUpArea) {
		this.mezzanineBuiltUpArea = mezzanineBuiltUpArea;
	}

	@JsonProperty("mezzanineArea")
	    private BigDecimal mezzanineArea;
	    
	    @JsonProperty("mezzanineHeight")
	    private BigDecimal mezzanineHeight;
	    
	    @JsonProperty("mezzanineBuiltUpArea")
	    private BigDecimal mezzanineBuiltUpArea;

}
