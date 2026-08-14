package org.egov.echallan.util;

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
        final String msgId = requestInfo != null ? requestInfo.getMsgId() : "";
        final String responseStatus = Boolean.TRUE.equals(success) ? "successful" : "failed";

        return ResponseInfo.builder().apiId(apiId).ver(ver).ts(ts).resMsgId(msgId).msgId(msgId)
                .status(responseStatus).build();
    }

}
