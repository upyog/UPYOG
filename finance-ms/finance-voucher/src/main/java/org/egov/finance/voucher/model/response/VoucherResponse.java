package org.egov.finance.voucher.model.response;

import java.util.ArrayList;
import java.util.List;

import org.egov.finance.voucher.model.ResponseInfo;
import org.egov.finance.voucher.model.VoucherModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherResponse {

	@JsonProperty("Vouchers")
	private List<VoucherModel> vouchers;

	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo;

	// private PageContract page;

}
