package org.egov.finance.inbox.model.response;

import java.util.List;

import org.egov.finance.inbox.model.FundsourceModel;
import org.egov.finance.inbox.model.ResponseInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundsourceResponse {
	
	private ResponseInfo responseInfo;
    private List<FundsourceModel> fundsources;

}
