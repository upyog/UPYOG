package org.egov.finance.voucher.model.request;

import java.util.List;

import org.egov.finance.voucher.entity.Voucher;
import org.egov.finance.voucher.model.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class VoucherRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@JsonProperty("vouchers")
	private List<Voucher> vouchers;

}
