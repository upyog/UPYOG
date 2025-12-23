package org.egov.finance.report.model.request;

import java.util.List;

import org.egov.finance.report.model.RequestInfo;
import org.egov.finance.report.model.VouchermisModel;

import lombok.Data;

@Data
public class VouchermisRequest {
	
	private RequestInfo requestInfo;
    private List<VouchermisModel> vouchermisList;

}
