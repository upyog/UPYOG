package upyog.util;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.stereotype.Component;

import static upyog.config.ServiceConstants.*;

/**
 * Factory class for creating {@link ResponseInfo} objects.
 * Provides utility methods to generate response information based on request details.
 */
@Component
public class ResponseInfoFactory {

    /**
     * Creates a {@link ResponseInfo} object from the given {@link RequestInfo}.
     *
     * @param requestInfo The request information object containing API details.
     * @param success     A flag indicating whether the operation was successful.
     * @return A {@link ResponseInfo} object populated with the relevant details.
     */
    public ResponseInfo createResponseInfoFromRequestInfo(final RequestInfo requestInfo, final Boolean success) {

        final String apiId = requestInfo != null ? requestInfo.getApiId() : "";
        final String ver = requestInfo != null ? requestInfo.getVer() : "";
        Long ts = null;
        if (requestInfo != null)
            ts = requestInfo.getTs();
        final String resMsgId = RES_MSG_ID;
        final String msgId = requestInfo != null ? requestInfo.getMsgId() : "";
        final String responseStatus = success ? SUCCESSFUL : FAILED;

        return ResponseInfo.builder()
                .apiId(apiId)
                .ver(ver)
                .ts(ts)
                .resMsgId(resMsgId)
                .msgId(msgId)
                .status(responseStatus)
                .build();
    }
}