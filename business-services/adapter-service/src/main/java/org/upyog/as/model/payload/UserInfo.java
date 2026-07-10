package org.upyog.as.model.payload;

import java.util.List;

import lombok.Data;

@Data
public class UserInfo {
	private String id;
	private String uuid;
	private String userName;
	private String name;
	private String mobileNumber;
	private String emailId;
	private String locale;
	private String type;
	private Boolean active;
	private String tenantId;
	private List<Roles> roles;
}
