package org.upyog.as.model.payload;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestInfo {
	private String apiId;
	private String ver;
	private String ts;
	private String action;
	private String did;
	private String key;
	private String msgId;
	private String authToken;
	private UserInfo userInfo;
}
