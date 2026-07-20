package org.egov.garbageservice.model;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.util.List;

import org.egov.garbageservice.enums.UserType;
import org.egov.tracer.annotations.CustomSafeHtml;
import org.egov.tracer.model.CustomException;
import org.springframework.util.CollectionUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
/**
 * Criteria object for user search APIs (tenant, username, mobile, user type, active flag).
 * Embedded in UserSearchRequest or used when building search payloads programmatically.
 */
@NoArgsConstructor
public class UserSearchCriteria {

	private List<Long> id;
	private List<String> uuid;
	@CustomSafeHtml
	private String userName;
	@CustomSafeHtml
	private String name;
	@CustomSafeHtml
	private String mobileNumber;
	@CustomSafeHtml
	private String emailId;
	private boolean fuzzyLogic;
	private Boolean active;
	private Integer offset;
	private Integer limit;
	private List<String> sort;
	private UserType type;
	@CustomSafeHtml
	private String tenantId;
	private List<String> roleCodes;
	@CustomSafeHtml
	private String alternatemobilenumber;

	public void validate(boolean isInterServiceCall) {
		if (validateIfEmptySearch(isInterServiceCall) || validateIfTenantIdExists(isInterServiceCall)) {
			throw new CustomException("INVALID_USER_SEARCH_CRITERIA", "Insufficient or invalid search criteria: ");
		}
	}

	private boolean validateIfEmptySearch(boolean isInterServiceCall) {
		/*
		 * for "InterServiceCall" -> at least one is compulsory --> 'userName' or 'name'
		 * or 'mobileNumber' or 'emailId' or 'uuid' or 'id' or 'roleCodes'
		 * 
		 * and for calls from outside-> at least one is compulsory --> 'userName' or
		 * 'name' or 'mobileNumber' or 'emailId' or 'uuid'
		 */
		if (isInterServiceCall)
			return isEmpty(userName) && isEmpty(name) && isEmpty(mobileNumber) && isEmpty(emailId)
					&& CollectionUtils.isEmpty(uuid) && CollectionUtils.isEmpty(id)
					&& CollectionUtils.isEmpty(roleCodes);
		else
			return isEmpty(userName) && isEmpty(name) && isEmpty(mobileNumber) && isEmpty(emailId)
					&& CollectionUtils.isEmpty(uuid);
	}

	private boolean validateIfTenantIdExists(boolean isInterServiceCall) {
		/*
		 * for calls from outside-> tenantId is compulsory if one of these is non
		 * empty--> 'userName' or 'name', 'mobileNumber' or 'roleCodes' and for
		 * "InterServiceCall" -> tenantId is compulsory if one of these is non empty -->
		 * 'userName' or 'name' or 'mobileNumber'
		 */
		if (isInterServiceCall)
			return (!isEmpty(userName) || !isEmpty(name) || !isEmpty(mobileNumber)
					|| !CollectionUtils.isEmpty(roleCodes)) && isEmpty(tenantId);
		else
			return (!isEmpty(userName) || !isEmpty(name) || !isEmpty(mobileNumber)) && isEmpty(tenantId);

	}
}
