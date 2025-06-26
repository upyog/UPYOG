/**
 * Created on Jun 2, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.report.model.response;


import java.util.List;

import org.egov.finance.report.model.FundModel;
import org.egov.finance.report.model.ResponseInfo;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FundResponse {
	ResponseInfo responseInfo;
	List<FundModel> funds;

}

