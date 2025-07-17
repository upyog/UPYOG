package org.egov.finance.voucher.util;

import org.egov.finance.voucher.model.RequestInfo;
import org.egov.finance.voucher.model.ResponseInfo;
import org.springframework.stereotype.Component;

@Component
public class ResponseInfoFactory {

	public ResponseInfo createResponseInfoFromRequestInfo(final RequestInfo requestInfo, final Boolean success) {

		final String apiId = requestInfo != null ? requestInfo.getApiId() : "";
		final String ver = requestInfo != null ? requestInfo.getVer() : "";
		Long ts = null;
		if (requestInfo != null)
			ts = requestInfo.getTs();
		final String resMsgId = "uief87324"; // FIXME : Hard-coded
		final String msgId = requestInfo != null ? requestInfo.getMsgId() : "";
		final String responseStatus = success ? "successful" : "failed";
		final String tenantId = requestInfo.getTenantId();

		return ResponseInfo.builder().apiId(apiId).ver(ver).ts(ts).resMsgId(resMsgId).msgId(msgId).resMsgId(resMsgId)
				.status(responseStatus).tenantId(tenantId).build();
	}

}