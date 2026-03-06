package org.egov.inbox.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class RequestInfo {
	@JsonProperty("userInfo")
	private UserInfo userInfo;
	private String apiId;
	private String authToken;
	private String msgId;
	private Long ts;
	private String ver;
	private Object plainAccessRequest;


}
