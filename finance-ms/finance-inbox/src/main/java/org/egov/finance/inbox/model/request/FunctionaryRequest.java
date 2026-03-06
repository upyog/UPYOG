package org.egov.finance.inbox.model.request;

import java.util.List;

import org.egov.finance.inbox.model.FunctionaryModel;
import org.egov.finance.inbox.model.RequestInfo;

import lombok.Data;

@Data
public class FunctionaryRequest {
	private RequestInfo requestInfo;
	private List<FunctionaryModel> functionaries;
}
