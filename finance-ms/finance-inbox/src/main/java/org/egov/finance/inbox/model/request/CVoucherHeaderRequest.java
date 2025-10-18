package org.egov.finance.inbox.model.request;

import org.egov.finance.inbox.model.CVoucherHeaderModel;
import org.egov.finance.inbox.model.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class CVoucherHeaderRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@Valid
	@JsonProperty("voucherHeader")
	private CVoucherHeaderModel voucherHeader;
}
