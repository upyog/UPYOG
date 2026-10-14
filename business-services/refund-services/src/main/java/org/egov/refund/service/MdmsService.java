package org.egov.refund.service;

import org.egov.common.contract.request.RequestInfo;

public interface MdmsService {

    String getReceiptKey(String tenantId, String businessService, RequestInfo requestInfo);
}