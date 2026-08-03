package org.egov.garbageservice.util;

/**
 * Static constants for user-service integration (validation patterns, error codes, client id).
 * Referenced by UserService and UserSearchRequest when creating or validating citizen users.
 */
public class UserServiceConstants {

    public static final String PATTERN_NAME = "^[^\\\\$\\\"<>?\\\\\\\\~`!@#$%^()+={}\\\\[\\\\]*,:;“”‘’]*$";

    public static final String PATTERN_MOBILE = "(^$|[0-9]{10})";
    public static final String PATTERN_TENANT = "^[a-zA-Z. ]*$";
}
