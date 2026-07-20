package org.egov.garbageservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Type of user in eGov user-service integration for garbage-service.
 *
 * Behavior:
 * - Values: CITIZEN, EMPLOYEE, SYSTEM, BUSINESS — used on user search/create models.
 * - Deserialized from API text via {@link #fromValue(String)} (case-insensitive enum name match).
 *
 * Notes:
 * - Used on {@link org.egov.garbageservice.model.UserV2}, {@link org.egov.garbageservice.model.UserSearchCriteria},
 *   and related user contract models.
 * - Aligns with eGov common user types; not garbage-account workflow status (see {@link Status}).
 */
public enum UserType {
	CITIZEN, EMPLOYEE, SYSTEM, BUSINESS;

	@JsonCreator
	public static UserType fromValue(String text) {
		for (UserType userType : UserType.values()) {
			if (String.valueOf(userType).equalsIgnoreCase(text)) {
				return userType;
			}
		}
		return null;
	}
}
