package org.upyog.chb.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AssetAddressDTO {
    
    @JsonProperty("street")
	private String street ;
}
