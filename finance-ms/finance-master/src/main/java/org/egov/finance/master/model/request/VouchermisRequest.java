package org.egov.finance.master.model.request;

import java.util.List;

import org.egov.finance.master.model.RequestInfo;
import org.egov.finance.master.model.VouchermisModel;

import lombok.Data;

@Data
public class VouchermisRequest {
	
	private RequestInfo requestInfo;
    private List<VouchermisModel> vouchermisList;

}
