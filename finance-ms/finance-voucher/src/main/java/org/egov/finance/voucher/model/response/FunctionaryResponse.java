package org.egov.finance.voucher.model.response;

import java.util.List;

import org.egov.finance.voucher.model.FunctionaryModel;
import org.egov.finance.voucher.model.ResponseInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FunctionaryResponse {
	private ResponseInfo responseInfo;
    private List<FunctionaryModel> functionaries;
}
