package org.egov.user.persistence.dto;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;


@Data
public class UserLoginAttemptAudit {
	
	private String uuid;
	private Long attempt_date; 
	private String ip ;
	private String user_name,user_uuid ;
	private String attempt_status; 
	private String user_agent; 
	private String referrer; 
	private String url; 
	private JsonNode session_details;
	
	public UserLoginAttemptAudit(String uuid, Long attempt_date, String ip, String user_name, String user_uuid,
			String attempt_status, String user_agent, String referrer, String url, JsonNode session_details) {
		super();
		this.uuid = uuid;
		this.attempt_date = attempt_date;
		this.ip = ip;
		this.user_name = user_name;
		this.user_uuid = user_uuid;
		this.attempt_status = attempt_status;
		this.user_agent = user_agent;
		this.referrer = referrer;
		this.url = url;
		this.session_details = session_details;
	}
}
