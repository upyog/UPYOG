package org.egov.finance.master.model.request;

import java.util.List;

import org.egov.finance.master.model.FunctionaryModel;
import org.egov.finance.master.model.RequestInfo;

import lombok.Data;

@Data
public class FunctionaryRequest {
	private RequestInfo requestInfo;
	private List<FunctionaryModel> functionaries;
}
