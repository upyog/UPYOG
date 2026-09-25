package org.egov.ewst.util;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.stereotype.Component;

/**
 * Factory class to create ResponseInfo objects.
 * This class is responsible for creating ResponseInfo objects based on the provided RequestInfo and success status.
 */
@Component
public class ResponseInfoFactory {

	private static final String DEFAULT_RES_MSG_ID = "uief87324";

	/**
	 * Creates a ResponseInfo object based on the provided RequestInfo and success status.
	 *
	 * @param requestInfo The RequestInfo object containing request details.
	 * @param success     The success status of the operation.
	 * @return A ResponseInfo object with the specified details.
	 */
	public ResponseInfo createResponseInfoFromRequestInfo(final RequestInfo requestInfo, final boolean success) {

		final String apiId = requestInfo != null ? requestInfo.getApiId() : "";
		final String ver = requestInfo != null ? requestInfo.getVer() : "";
		Long ts = null;
		if (requestInfo != null)
			ts = requestInfo.getTs();
		final String resMsgId = DEFAULT_RES_MSG_ID;
		final String msgId = requestInfo != null ? requestInfo.getMsgId() : "";
		final String responseStatus = success ? "successful" : "failed";

		return ResponseInfo.builder().apiId(apiId).ver(ver).ts(ts).resMsgId(resMsgId).msgId(msgId).resMsgId(resMsgId)
				.status(responseStatus).build();
	}

}
