package org.egov.finance.report.model.request;

import java.util.List;

import org.egov.finance.report.customannotation.SafeHtml;
import org.egov.finance.report.model.RequestInfo;
import org.egov.finance.report.model.VoucherModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VoucherRequest {

	@NotNull
	@JsonProperty("tenantId")
	@SafeHtml
	private String tenantId;

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@JsonProperty("vouchers")
	private List<VoucherModel> vouchers;

}
