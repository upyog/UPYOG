package org.egov.finance.master.model.response;

import java.util.List;

import org.egov.finance.master.model.AccountDetailModel;
import org.egov.finance.master.model.ResponseInfo;

import lombok.Data;

@Data
public class AccountDetailResponse {
	private ResponseInfo responseInfo;
    private List<AccountDetailModel> accountDetails;
}
