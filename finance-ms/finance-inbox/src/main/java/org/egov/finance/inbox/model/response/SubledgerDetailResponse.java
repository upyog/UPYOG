package org.egov.finance.inbox.model.response;

import java.util.List;

import org.egov.finance.inbox.model.ResponseInfo;
import org.egov.finance.inbox.model.SubledgerDetailModel;

import lombok.Data;

@Data
public class SubledgerDetailResponse {
	private ResponseInfo responseInfo;
	private List<SubledgerDetailModel> subledgerDetails;
}
