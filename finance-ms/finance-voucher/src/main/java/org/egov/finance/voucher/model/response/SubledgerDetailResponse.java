package org.egov.finance.voucher.model.response;

import java.util.List;

import org.egov.finance.voucher.model.ResponseInfo;
import org.egov.finance.voucher.model.SubledgerDetailModel;

import lombok.Data;

@Data
public class SubledgerDetailResponse {
	private ResponseInfo responseInfo;
	private List<SubledgerDetailModel> subledgerDetails;
}
