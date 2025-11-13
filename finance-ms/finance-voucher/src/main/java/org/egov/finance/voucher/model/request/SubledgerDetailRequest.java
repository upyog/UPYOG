package org.egov.finance.voucher.model.request;

import org.egov.finance.voucher.model.RequestInfo;
import org.egov.finance.voucher.model.SubledgerDetailModel;

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
