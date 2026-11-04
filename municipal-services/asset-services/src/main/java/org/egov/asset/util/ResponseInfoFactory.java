package org.egov.asset.util;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.stereotype.Component;

@Component
public class ResponseInfoFactory {

    private static final String DEFAULT_RES_MSG_ID = "uief87324";

    public ResponseInfo createResponseInfoFromRequestInfo(final RequestInfo requestInfo, final boolean success) {
        final String apiId = requestInfo != null ? requestInfo.getApiId() : "";
        final String ver = requestInfo != null ? requestInfo.getVer() : "";
        Long ts = null;
        if (requestInfo != null) {
            ts = requestInfo.getTs();
        }
        final String msgId = requestInfo != null ? requestInfo.getMsgId() : "";
        final String responseStatus = success ? "successful" : "failed";

        return ResponseInfo.builder().apiId(apiId).ver(ver).ts(ts).resMsgId(DEFAULT_RES_MSG_ID).msgId(msgId)
                .status(responseStatus).build();
    }
}
