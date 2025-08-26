package org.egov.vendor.web.models.vendorcontract.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Setter
@ToString
@Builder
public class UserSearchRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@JsonProperty("uuid")
	private List<String> uuid;	

	@JsonProperty("id")
	private List<String> id;

	@JsonProperty("userName")
	private String userName;

	@JsonProperty("name")
	private String name;

	@JsonProperty("mobileNumber")
	private String mobileNumber;

	@JsonProperty("aadhaarNumber")
	private String aadhaarNumber;

	@JsonProperty("pan")
	private String pan;

	@JsonProperty("emailId")
	private String emailId;

	@JsonProperty("fuzzyLogic")
	private boolean fuzzyLogic;

	@JsonProperty("active")
	@Setter
	private Boolean active;

	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("pageSize")
	private int pageSize;

	@JsonProperty("pageNumber")
	private int pageNumber = 0;

	@JsonProperty("sort")
	private List<String> sort = Collections.singletonList("name");

	@JsonProperty("userType")
	private String userType;

	@JsonProperty("roleCodes")
	private List<String> roleCodes;

}
