package org.egov.finance.voucher.model.response;

import java.util.List;

import org.egov.finance.voucher.model.BankModel;
import org.egov.finance.voucher.model.ResponseInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankResponse {
	private ResponseInfo responseInfo;
	private List<BankModel> banks;

}
