package org.egov.finance.master.model.response;

import java.util.List;

import org.egov.finance.master.model.BankaccountModel;
import org.egov.finance.master.model.ResponseInfo;

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
