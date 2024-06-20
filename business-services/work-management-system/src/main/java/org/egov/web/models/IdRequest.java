package org.egov.web.models;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdRequest {
	
	 @JsonProperty("idName")
	 @NotNull
	 private String idName;

	 @JsonProperty("tenantId")
	  private String tenantId;
	 	 
	 @JsonProperty("format")
	    private String format;
}
