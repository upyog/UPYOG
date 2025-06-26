package org.egov.finance.master.model.request;

/**
 * SchemeRequest.java
 * 
 * @author mmavuluri
 * @date 9 Jun 2025
 * @version 1.0
 */
import org.egov.finance.master.model.RequestInfo;
import org.egov.finance.master.model.SchemeModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class SchemeRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@Valid
	@JsonProperty("Scheme")
	private SchemeModel scheme;

}
