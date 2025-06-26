package org.egov.finance.voucher.model.response;

import java.util.List;

import org.egov.finance.voucher.model.BankaccountModel;
import org.egov.finance.voucher.model.ResponseInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankaccountResponse {
	private ResponseInfo responseInfo;
	private List<BankaccountModel> bankaccounts;

}
