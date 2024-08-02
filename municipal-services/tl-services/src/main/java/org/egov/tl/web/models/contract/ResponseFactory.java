package org.egov.tl.web.models.contract;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.Error;
import org.egov.common.contract.response.ErrorResponse;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class ResponseFactory {

	public ResponseInfo getResponseInfo(RequestInfo requestInfo, HttpStatus status) {

		ResponseInfo responseInfo = new ResponseInfo();
		if (requestInfo != null) {
			responseInfo.setApiId(requestInfo.getApiId());
			responseInfo.setMsgId(requestInfo.getMsgId());
			// responseInfo.setResMsgId(requestInfo.get);
			responseInfo.setStatus(status.toString());
			responseInfo.setVer(requestInfo.getVer());
			responseInfo.setTs(requestInfo.getTs());
		}
		return responseInfo;
	}

	public ErrorResponse getErrorResponse(RequestInfo requestInfo) {

		Error error = new Error();
		error.setCode(400);
		error.setMessage("EG_BS_API_ERROR");
		error.setDescription("The API you are trying to access has been depricated, Access the V2 API's");
		return new ErrorResponse(getResponseInfo(requestInfo, HttpStatus.BAD_REQUEST), error);
	}
}
