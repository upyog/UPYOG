package org.egov.finance.report.model.request;

import java.util.List;

import org.egov.finance.report.entity.ChequeFormat;
import org.egov.finance.report.model.RequestInfo;

import lombok.Data;

@Data
public class ChequeFormatRequest {
	private RequestInfo requestInfo;
	private List<ChequeFormat> chequeFormats;
}
