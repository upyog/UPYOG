package org.egov.garbageservice.enums;

/**
 * Relationship of a user’s guardian in user-service (HRMS/user v2) models.
 * <p>
 * Behavior:
 * - Values: FATHER, MOTHER, HUSBAND, OTHER.
 * - Stored on {@link org.egov.garbageservice.web.models.UserV2} and user search response content.
 * <p>
 * Notes:
 * - Simpler enum than {@link Relationship} (property owner contract); used for citizen/employee user profiles.
 * - Enum name is serialized as standard Java enum unless custom Jackson config is applied.
 */
public enum GuardianRelation {
    FATHER, MOTHER, HUSBAND, OTHER;
}
