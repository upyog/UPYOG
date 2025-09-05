package org.egov.finance.voucher.model.response;

import java.util.List;

import org.egov.finance.voucher.model.CVoucherHeaderModel;
import org.egov.finance.voucher.model.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CVoucherHeaderResponse {

	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo;

	@JsonProperty("voucherHeaders")
	private List<CVoucherHeaderModel> voucherHeaders;
}
