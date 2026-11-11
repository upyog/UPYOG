package org.upyog.wms.calculator.util;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.stereotype.Component;

@Component
public class ResponseInfoFactory {

    public ResponseInfo createResponseInfoFromRequestInfo(RequestInfo requestInfo, boolean success) {
        String apiId = requestInfo != null ? requestInfo.getApiId() : "";
        String ver = requestInfo != null ? requestInfo.getVer() : "";
        Long ts = requestInfo != null ? requestInfo.getTs() : null;
        String msgId = requestInfo != null ? requestInfo.getMsgId() : "";
        return ResponseInfo.builder()
                .apiId(apiId)
                .ver(ver)
                .ts(ts)
                .resMsgId("uief87324")
                .msgId(msgId)
                .status(success ? "successful" : "failed")
                .build();
    }
}
