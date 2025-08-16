package org.egov.common.entity.edcr;

import java.math.BigDecimal;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BathroomWCRequirement extends MdmsFeatureRule {

	 @JsonProperty("bathroomWCRequiredArea")
	    private BigDecimal bathroomWCRequiredArea;

	    @JsonProperty("bathroomWCRequiredWidth")
	    private BigDecimal bathroomWCRequiredWidth;

	    @JsonProperty("bathroomWCRequiredHeight")
	    private BigDecimal bathroomWCRequiredHeight;


	    public BigDecimal getBathroomWCRequiredArea() {
	        return bathroomWCRequiredArea;
	    }

	    public void setBathroomWCRequiredArea(BigDecimal bathroomWCRequiredArea) {
	        this.bathroomWCRequiredArea = bathroomWCRequiredArea;
	    }

	    public BigDecimal getBathroomWCRequiredWidth() {
	        return bathroomWCRequiredWidth;
	    }

	    public void setBathroomWCRequiredWidth(BigDecimal bathroomWCRequiredWidth) {
	        this.bathroomWCRequiredWidth = bathroomWCRequiredWidth;
	    }

	    public BigDecimal getBathroomWCRequiredHeight() {
	        return bathroomWCRequiredHeight;
	    }

	    public void setBathroomWCRequiredHeight(BigDecimal bathroomWCRequiredHeight) {
	        this.bathroomWCRequiredHeight = bathroomWCRequiredHeight;
	    }
	

	    @Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (!(o instanceof BathroomWCRequirement)) return false;
	        if (!super.equals(o)) return false;
	        BathroomWCRequirement that = (BathroomWCRequirement) o;
	        return Objects.equals(bathroomWCRequiredArea, that.bathroomWCRequiredArea) &&
	               Objects.equals(bathroomWCRequiredWidth, that.bathroomWCRequiredWidth) &&
	               Objects.equals(bathroomWCRequiredHeight, that.bathroomWCRequiredHeight);
	    }

	    @Override
	    public int hashCode() {
	        return Objects.hash(super.hashCode(), bathroomWCRequiredArea, bathroomWCRequiredWidth, bathroomWCRequiredHeight);
	    }
	    
@Override
public String toString() {
    return "BathroomWCRule{" +
        "bathroomWCRequiredArea=" + bathroomWCRequiredArea +
        ", bathroomWCRequiredWidth=" + bathroomWCRequiredWidth +
        ", bathroomWCRequiredHeight=" + bathroomWCRequiredHeight +
        ", active=" + getActive() +
        ", city=" + getCity() +
        '}';
}
}