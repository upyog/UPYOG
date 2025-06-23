package org.egov.finance.inbox.model.response;

import java.util.List;

import org.egov.finance.inbox.model.AccountDetailModel;
import org.egov.finance.inbox.model.ResponseInfo;

import lombok.Data;

@Data
public class AccountDetailResponse {
	private ResponseInfo responseInfo;
    private List<AccountDetailModel> accountDetails;
}
