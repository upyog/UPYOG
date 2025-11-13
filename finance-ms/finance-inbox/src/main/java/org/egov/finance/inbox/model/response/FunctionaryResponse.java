package org.egov.finance.inbox.model.response;

import java.util.List;

import org.egov.finance.inbox.model.FunctionaryModel;
import org.egov.finance.inbox.model.ResponseInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FunctionaryResponse {
	private ResponseInfo responseInfo;
    private List<FunctionaryModel> functionaries;
}
