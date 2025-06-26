package org.egov.finance.master.model.request;

import org.egov.finance.master.model.RequestInfo;
import org.egov.finance.master.model.SubledgerDetailModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class SubledgerDetailRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@Valid
	@JsonProperty("SubledgerDetail")
	private SubledgerDetailModel subledgerDetail;
}
