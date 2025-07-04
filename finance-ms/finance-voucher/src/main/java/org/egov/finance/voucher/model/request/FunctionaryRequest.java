package org.egov.finance.voucher.model.request;

import java.util.List;

import org.egov.finance.voucher.model.FunctionaryModel;
import org.egov.finance.voucher.model.RequestInfo;

import lombok.Data;

@Data
public class FunctionaryRequest {
	private RequestInfo requestInfo;
	private List<FunctionaryModel> functionaries;
}
