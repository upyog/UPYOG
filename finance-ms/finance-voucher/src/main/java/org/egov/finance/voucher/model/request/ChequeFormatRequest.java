package org.egov.finance.voucher.model.request;

import java.util.List;

import org.egov.finance.voucher.entity.ChequeFormat;
import org.egov.finance.voucher.model.RequestInfo;

import lombok.Data;

@Data
public class ChequeFormatRequest {
	private RequestInfo requestInfo;
	private List<ChequeFormat> chequeFormats;
}
