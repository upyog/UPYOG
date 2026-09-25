package org.upyog.adv.util;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.stereotype.Component;
/**
 * Factory class for creating ResponseInfo objects in the Advertisement Booking Service.
 */
@Component
public class ResponseInfoFactory {

	/**
	 * Creates a ResponseInfo object from the incoming request metadata.
	 *
	 * @param requestInfo source request metadata
	 * @param success whether the operation succeeded
	 * @return populated response metadata
	 */
	public ResponseInfo createResponseInfoFromRequestInfo(final RequestInfo requestInfo, final Boolean success) {

		final String apiId = requestInfo != null ? requestInfo.getApiId() : "";
		final String ver = requestInfo != null ? requestInfo.getVer() : "";
		Long ts = null;
		if (requestInfo != null)
			ts = requestInfo.getTs();
		final String msgId = requestInfo != null ? requestInfo.getMsgId() : "";
		final String responseStatus = Boolean.TRUE.equals(success) ? "successful" : "failed";

		return ResponseInfo.builder().apiId(apiId).ver(ver).ts(ts).resMsgId(msgId).msgId(msgId)
				.status(responseStatus).build();
	}

}
