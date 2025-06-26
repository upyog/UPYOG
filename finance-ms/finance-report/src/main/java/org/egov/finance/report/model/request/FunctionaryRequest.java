package org.egov.finance.report.model.request;

import java.util.List;

import org.egov.finance.report.model.FunctionaryModel;
import org.egov.finance.report.model.RequestInfo;

import lombok.Data;

@Data
public class FunctionaryRequest {
	private RequestInfo requestInfo;
	private List<FunctionaryModel> functionaries;
}
