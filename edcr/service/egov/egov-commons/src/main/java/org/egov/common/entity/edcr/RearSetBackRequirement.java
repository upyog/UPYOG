package org.egov.common.entity.edcr;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RearSetBackRequirement extends MdmsFeatureRule {
	
	    public BigDecimal getPermissibleLight() {
		return permissibleLight;
	}

	public void setPermissibleLight(BigDecimal permissibleLight) {
		this.permissibleLight = permissibleLight;
	}

	public BigDecimal getPermissibleMedium() {
		return permissibleMedium;
	}

	public void setPermissibleMedium(BigDecimal permissibleMedium) {
		this.permissibleMedium = permissibleMedium;
	}

	public BigDecimal getPermissibleFlattered() {
		return permissibleFlattered;
	}

	public void setPermissibleFlattered(BigDecimal permissibleFlattered) {
		this.permissibleFlattered = permissibleFlattered;
	}

		@JsonProperty("permissibleLight")
	    private BigDecimal permissibleLight;
	    
	    @JsonProperty("permissibleMedium")
	    private BigDecimal permissibleMedium;
	    
	    @JsonProperty("permissibleFlattered")
	    private BigDecimal permissibleFlattered;

		@Override
		public String toString() {
			return "RearSetBackRequirement [permissibleLight=" + permissibleLight + ", permissibleMedium="
					+ permissibleMedium + ", permissibleFlattered=" + permissibleFlattered + "]";
		}
	    


}
