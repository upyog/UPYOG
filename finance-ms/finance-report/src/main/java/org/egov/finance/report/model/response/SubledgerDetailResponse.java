package org.egov.finance.report.model.response;

import java.util.List;

import org.egov.finance.report.model.ResponseInfo;
import org.egov.finance.report.model.SubledgerDetailModel;

import lombok.Data;

@Data
public class SubledgerDetailResponse {
	private ResponseInfo responseInfo;
	private List<SubledgerDetailModel> subledgerDetails;
}
