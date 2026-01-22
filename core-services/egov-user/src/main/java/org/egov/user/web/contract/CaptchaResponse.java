package org.egov.user.web.contract;

import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CaptchaResponse {

	 @JsonProperty("responseInfo")
	    ResponseInfo responseInfo;
	 
	 @JsonProperty("captcha")
	 Captcha captcha;
}
