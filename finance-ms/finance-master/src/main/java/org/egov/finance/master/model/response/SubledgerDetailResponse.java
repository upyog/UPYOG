package org.egov.finance.master.model.response;

import java.util.List;

import org.egov.finance.master.model.ResponseInfo;
import org.egov.finance.master.model.SubledgerDetailModel;

import lombok.Data;

@Data
public class SubledgerDetailResponse {
	private ResponseInfo responseInfo;
	private List<SubledgerDetailModel> subledgerDetails;
}
