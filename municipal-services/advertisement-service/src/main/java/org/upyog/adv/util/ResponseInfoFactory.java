package org.upyog.adv.util;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.stereotype.Component;
/**
 * Factory class for creating ResponseInfo objects in the Advertisement Booking Service.
 * 
 * Key Responsibilities:
 * - Generates standardized ResponseInfo objects based on RequestInfo and operation success status.
 * 
 * Methods:
 * - `createResponseInfoFromRequestInfo`: Creates a ResponseInfo object using RequestInfo and success flag.
 * 
 * Annotations:
 * - @Component: Marks this class as a Spring-managed component.
 */
@Component
public class ResponseInfoFactory {

    public ResponseInfo createResponseInfoFromRequestInfo(final RequestInfo requestInfo, final Boolean success) {

        final String apiId = requestInfo != null ? requestInfo.getApiId() : "";
        final String ver = requestInfo != null ? requestInfo.getVer() : "";
        Long ts = null;
        if(requestInfo!=null)
            ts = requestInfo.getTs();
        final String resMsgId = "uief87324"; // FIXME : Hard-coded
        final String msgId = requestInfo != null ? requestInfo.getMsgId() : "";
        final String responseStatus = success ? "successful" : "failed";

        return ResponseInfo.builder().apiId(apiId).ver(ver).ts(ts).resMsgId(resMsgId).msgId(msgId).resMsgId(resMsgId)
                .status(responseStatus).build();
    }

}