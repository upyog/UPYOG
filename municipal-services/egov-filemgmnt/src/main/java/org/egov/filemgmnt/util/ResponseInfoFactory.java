package org.egov.filemgmnt.util;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.stereotype.Component;

@Component
public class ResponseInfoFactory {

    public ResponseInfo createResponseInfoFromRequestInfo(final RequestInfo requestInfo, final Boolean success) {

        final String apiId = requestInfo != null ? requestInfo.getApiId() : "";
        final String ver = requestInfo != null ? requestInfo.getVer() : "";
        Long ts = null;
        if (requestInfo != null) {
            ts = requestInfo.getTs();
        }
        String resMsgId = "uief87324"; // FIXME : Hard-coded
        String msgId = requestInfo != null ? requestInfo.getMsgId() : "";
        String responseStatus = success ? "successful" : "failed";

        return ResponseInfo.builder()
                           .apiId(apiId)
                           .ver(ver)
                           .ts(ts)
                           .resMsgId(resMsgId)
                           .msgId(msgId)
                           .resMsgId(resMsgId)
                           .status(responseStatus)
                           .build();
    }

}