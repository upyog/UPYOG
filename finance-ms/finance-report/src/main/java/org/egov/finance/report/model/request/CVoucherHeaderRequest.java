package org.egov.finance.report.model.request;

import org.egov.finance.report.model.CVoucherHeaderModel;
import org.egov.finance.report.model.RequestInfo;

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
