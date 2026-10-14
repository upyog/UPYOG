package org.egov.refund.service;

import org.egov.common.contract.request.RequestInfo;

public interface PdfService {

    String generateReceipt(String tenantId,
                           String receiptKey,
                           Object pdfRequest,
                           RequestInfo requestInfo);
}