package org.egov.filemgmnt.web.controllers;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;

interface ResponseInfoBuilderInterface {

    String RES_MSG_ID = "ui3246";
    String RES_STATUS_SUCCESS = "successful";
    String RES_STATUS_FAILED = "failed";

    default ResponseInfo buildResponseInfo(RequestInfo requestInfo, Boolean success) {
        String apiId = "";
        String ver = "";
        Long ts = null;
        String msgId = "";

        if (requestInfo != null) {
            apiId = requestInfo.getApiId();
            ver = requestInfo.getVer();
            ts = requestInfo.getTs();
            msgId = requestInfo.getMsgId();
        }

        String status = success ? RES_STATUS_SUCCESS : RES_STATUS_FAILED;

        return ResponseInfo.builder()
                           .apiId(apiId)
                           .ver(ver)
                           .ts(ts)
                           .resMsgId(RES_MSG_ID)
                           .msgId(msgId)
                           .status(status)
                           .build();
    }
}
