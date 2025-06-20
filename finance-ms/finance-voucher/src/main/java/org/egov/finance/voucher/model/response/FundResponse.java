/**
 * Created on Jun 2, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.voucher.model.response;


import java.util.List;

import org.egov.finance.voucher.model.FundModel;
import org.egov.finance.voucher.model.ResponseInfo;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FundResponse {
	ResponseInfo responseInfo;
	List<FundModel> funds;

}

