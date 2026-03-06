package org.egov.finance.report.model.response;

import java.util.List;

import org.egov.finance.report.model.AccountDetailModel;
import org.egov.finance.report.model.ResponseInfo;

import lombok.Data;

@Data
public class AccountDetailResponse {
	private ResponseInfo responseInfo;
    private List<AccountDetailModel> accountDetails;
}
