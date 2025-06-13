package org.egov.finance.master.model.response;

import java.util.List;

import org.egov.finance.master.model.CVoucherHeaderModel;
import org.egov.finance.master.model.ResponseInfo;

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
