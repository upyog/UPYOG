package org.egov.finance.voucher.model.response;

import java.util.List;

import org.egov.finance.voucher.model.AccountDetailModel;
import org.egov.finance.voucher.model.ResponseInfo;

import lombok.Data;

@Data
public class AccountDetailResponse {
	private ResponseInfo responseInfo;
    private List<AccountDetailModel> accountDetails;
}
