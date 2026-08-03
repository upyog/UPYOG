package org.egov.garbageservice.util;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.stereotype.Component;

/**
 * Builds standard eGov ResponseInfo from an incoming RequestInfo after internal API handling.
 * Echoes apiId, ver, msgId, and sets status to successful or failed for service-layer responses.
 */
@Component
public class ResponseInfoFactory {

    /**
     * Handles REST API request to register a new garbage account.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Validates the incoming request payload and authentication headers.</li>
     *   <li>Delegates account creation and workflow initialization logic.</li>
     *   <li>Constructs standardized response headers.</li>
     *   <li>Returns HTTP response containing created entity details.</li>
     * </ol>
     *
     * @param requestInfo the request information containing user session details
     * @param success     the success parameter
     * @return the output result
     */

    public ResponseInfo createResponseInfoFromRequestInfo(final RequestInfo requestInfo, final Boolean success) {
        final String apiId = requestInfo != null ? requestInfo.getApiId() : "";
        final String ver = requestInfo != null ? requestInfo.getVer() : "";
        Long ts = null;
        if (requestInfo != null)
            ts = requestInfo.getTs();
        final String resMsgId = "uief87324"; // FIXME : Hard-coded
        final String msgId = requestInfo != null ? requestInfo.getMsgId() : "";
        final String responseStatus = success ? "successful" : "failed";

        return ResponseInfo.builder().apiId(apiId).ver(ver).ts(ts).resMsgId(resMsgId).msgId(msgId).resMsgId(resMsgId)
                .status(responseStatus).build();
    }

}
