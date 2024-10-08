package org.egov.inbox.web.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserInfo {
	
	private String uuid;
	private String userName;
	private String name;
	private String mobileNumber;
	private String emailId;
	private String type;
	private String tenantId;
	private List<Role> roles;
	private String id;
	private String locale;
	private String permanentCity;
	private boolean active;
}
