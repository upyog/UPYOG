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
				+ mezzanineHeight + ", mezzanineBuiltUpArea=" + mezzanineBuiltUpArea + ", minMezzanineHeight="
				+ minMezzanineHeight + ", maxMezzanineHeight=" + maxMezzanineHeight + "]";
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
	    
	    public BigDecimal getMinMezzanineHeight() {
			return minMezzanineHeight;
		}

		public void setMinMezzanineHeight(BigDecimal minMezzanineHeight) {
			this.minMezzanineHeight = minMezzanineHeight;
		}

		@JsonProperty("mezzanineBuiltUpArea")
	    private BigDecimal mezzanineBuiltUpArea;
	    
	    @JsonProperty("minMezzanineHeight")
	    private BigDecimal minMezzanineHeight;

	    
	    @JsonProperty("maxMezzanineHeight")
	    private BigDecimal maxMezzanineHeight;

		public BigDecimal getMaxMezzanineHeight() {
			return maxMezzanineHeight;
		}

		public void setMaxMezzanineHeight(BigDecimal maxMezzanineHeight) {
			this.maxMezzanineHeight = maxMezzanineHeight;
		}

}
