package org.egov.finance.voucher.model.request;

import java.util.List;

import org.egov.finance.voucher.customannotation.SafeHtml;
import org.egov.finance.voucher.model.RequestInfo;
import org.egov.finance.voucher.model.VoucherModel;

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
