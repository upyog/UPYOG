package org.egov.finance.inbox.model.request;

import java.util.List;

import org.egov.finance.inbox.entity.ChequeFormat;
import org.egov.finance.inbox.model.RequestInfo;

import lombok.Data;

@Data
public class ChequeFormatRequest {
	private RequestInfo requestInfo;
	private List<ChequeFormat> chequeFormats;
}
