package org.egov.finance.voucher.model.request;

import org.egov.finance.voucher.model.RequestInfo;
import org.egov.finance.voucher.model.SchemeModel;

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
