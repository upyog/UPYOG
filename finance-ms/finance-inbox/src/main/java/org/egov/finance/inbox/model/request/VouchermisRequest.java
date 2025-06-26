package org.egov.finance.inbox.model.request;

import java.util.List;

import org.egov.finance.inbox.model.RequestInfo;
import org.egov.finance.inbox.model.VouchermisModel;

import lombok.Data;

@Data
public class VouchermisRequest {
	
	private RequestInfo requestInfo;
    private List<VouchermisModel> vouchermisList;

}
