package org.egov.garbageservice.enums;

/**
 * Gender values for user profiles integrated with eGov user service.
 *
 * Behavior:
 * - Values: FEMALE, MALE, OTHERS, TRANSGENDER (declaration order must not change — see in-file comment).
 * - Used on {@link org.egov.garbageservice.model.UserV2} and user search models.
 *
 * Notes:
 * - Order is significant for legacy DB or API ordinal mapping — do not reorder constants.
 * - Standard enum JSON serialization uses constant names unless customized.
 */
public enum Gender {
	// This order should not be interrupted
	FEMALE, MALE, OTHERS, TRANSGENDER;
}
