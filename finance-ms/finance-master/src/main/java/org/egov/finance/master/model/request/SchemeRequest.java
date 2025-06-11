package org.egov.finance.master.model.request;

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
