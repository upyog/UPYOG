package org.egov.garbageservice.model;

import java.util.Collections;
import java.util.List;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import org.egov.common.contract.request.RequestInfo;
import org.egov.garbageservice.enums.UserType;
import org.egov.garbageservice.util.UserServiceConstants;
import org.egov.tracer.annotations.CustomSafeHtml;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
/**
 * Request to search users in eGov user service with rich filters (mobile, uuid, tenant, type).
 * Built by UserService when garbage-service resolves owners or creates citizen logins.
 */
@Builder
public class UserSearchRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@JsonProperty("id")
	private List<Long> id;

	@JsonProperty("uuid")
	private List<String> uuid;

	@Size(max = 64)
	@CustomSafeHtml
	@JsonProperty("userName")
	private String userName;

	@Size(max = 100)
	@CustomSafeHtml
	@JsonProperty("name")
	private String name;

	@Pattern(regexp = UserServiceConstants.PATTERN_MOBILE)
	@CustomSafeHtml
	@JsonProperty("mobileNumber")
	private String mobileNumber;

	@Size(max = 20)
	@CustomSafeHtml
	@JsonProperty("aadhaarNumber")
	private String aadhaarNumber;

	@Size(max = 10)
	@CustomSafeHtml
	@JsonProperty("pan")
	private String pan;

	@Size(max = 128)
	@CustomSafeHtml
	@JsonProperty("emailId")
	private String emailId;

	@JsonProperty("fuzzyLogic")
	private boolean fuzzyLogic;

	@JsonProperty("active")
	@Setter
	private Boolean active;

	@Pattern(regexp = UserServiceConstants.PATTERN_TENANT)
	@Size(max = 256)
	@CustomSafeHtml
	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("pageSize")
	private int pageSize;

	@JsonProperty("pageNumber")
	private int pageNumber = 0;

	@JsonProperty("sort")
	private List<String> sort = Collections.singletonList("name");

	@Size(max = 50)
	@CustomSafeHtml
	@JsonProperty("userType")
	private String userType;

	@JsonProperty("roleCodes")
	private List<String> roleCodes;

	public UserSearchCriteria toDomain() {
		return UserSearchCriteria.builder().id(id).userName(userName).name(name).mobileNumber(mobileNumber)
				.emailId(emailId).fuzzyLogic(fuzzyLogic).active(active).limit(pageSize).offset(pageNumber).sort(sort)
				.type(UserType.fromValue(userType)).tenantId(tenantId).roleCodes(roleCodes).uuid(uuid).build();
	}
}
