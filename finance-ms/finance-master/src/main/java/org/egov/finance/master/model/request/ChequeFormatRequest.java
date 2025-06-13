package org.egov.finance.master.model.request;

import java.util.List;

import org.egov.finance.master.entity.ChequeFormat;
import org.egov.finance.master.model.RequestInfo;

import lombok.Data;

@Data
public class ChequeFormatRequest {
	private RequestInfo requestInfo;
	private List<ChequeFormat> chequeFormats;
}
