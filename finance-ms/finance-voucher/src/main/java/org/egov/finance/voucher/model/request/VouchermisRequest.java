package org.egov.finance.voucher.model.request;

import java.util.List;

import org.egov.finance.voucher.model.RequestInfo;
import org.egov.finance.voucher.model.VouchermisModel;

import lombok.Data;

@Data
public class VouchermisRequest {
	
	private RequestInfo requestInfo;
    private List<VouchermisModel> vouchermisList;

}
