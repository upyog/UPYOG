package org.upyog.chb.util;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.stereotype.Component;

/**
 * This utility class is responsible for creating standardized ResponseInfo objects
 * for the Community Hall Booking module.
 */
@Component
public class ResponseInfoFactory {

    public ResponseInfo createResponseInfoFromRequestInfo(final RequestInfo requestInfo, final boolean success) {

        final String apiId = requestInfo != null ? requestInfo.getApiId() : "";
        final String ver = requestInfo != null ? requestInfo.getVer() : "";
        Long ts = null;
        if (requestInfo != null) {
            ts = requestInfo.getTs();
        }
        final String resMsgId = requestInfo != null ? requestInfo.getMsgId() : "";
        final String msgId = requestInfo != null ? requestInfo.getMsgId() : "";
        final String responseStatus = success ? "successful" : "failed";

        return ResponseInfo.builder().apiId(apiId).ver(ver).ts(ts).resMsgId(resMsgId).msgId(msgId)
                .status(responseStatus).build();
    }

}
