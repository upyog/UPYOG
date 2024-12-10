package org.upyog.employee.dasboard.util;

import org.egov.common.contract.request.RequestInfo;

import org.springframework.stereotype.Component;
import org.upyog.employee.dasboard.web.models.ResponseInfo;
import org.upyog.employee.dasboard.web.models.ResponseInfo.StatusEnum;

import lombok.NonNull;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;

@Component
public class ResponseInfoUtil {

    
    public static ResponseInfo createResponseInfo(final RequestInfo requestInfo, String resMsg, StatusEnum status) {
        final String apiId = requestInfo != null ? requestInfo.getApiId() : StringUtils.EMPTY;
        final String ver = requestInfo != null ? requestInfo.getVer() : StringUtils.EMPTY;
        Long ts = null;
        if (requestInfo != null)
            ts = requestInfo.getTs();
        final String msgId = requestInfo != null ? requestInfo.getMsgId() : StringUtils.EMPTY;

        ResponseInfo responseInfo = ResponseInfo.builder()
                .apiId(apiId)
                .ver(ver)
                .ts(ts)
                .msgId(msgId)
                .resMsgId(resMsg)
                .status(status)
                .build();

        return responseInfo;
    }



}