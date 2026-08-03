package org.egov.ndc.calculator.utils;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;

public class ResponseInfoFactory {

    private static final String RES_MSG_ID = "uief87324";

    private ResponseInfoFactory() {
    }

    public static ResponseInfo createResponseInfoFromRequestInfo(final RequestInfo requestInfo) {

        final String apiId = requestInfo != null ? requestInfo.getApiId() : "";
        final String ver = requestInfo != null ? requestInfo.getVer() : "";
        Long ts = null;
        if (requestInfo != null)
            ts = requestInfo.getTs();
        final String msgId = requestInfo != null ? requestInfo.getMsgId() : "";

        return ResponseInfo.builder().apiId(apiId).ver(ver).ts(ts).resMsgId(RES_MSG_ID).msgId(msgId).resMsgId(RES_MSG_ID)
                .build();
    }
}
