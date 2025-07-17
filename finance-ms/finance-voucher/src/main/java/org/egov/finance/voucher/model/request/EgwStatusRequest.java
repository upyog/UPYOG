package org.egov.finance.voucher.model.request;

import org.egov.finance.voucher.model.EgwStatusModel;
import org.egov.finance.voucher.model.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class EgwStatusRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@Valid
	@JsonProperty("EgwStatus")
	private EgwStatusModel egwStatus;

}
