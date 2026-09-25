package org.upyog.cdwm.web.models.user;

public class UserServiceConstants {

    private UserServiceConstants() {
    }

    public static final String PATTERN_NAME = "^[^\\\\$\\\"<>?\\\\\\\\~`!@#$%^()+={}\\\\[\\\\]*,:;“”‘’]*$";

    public static final String PATTERN_MOBILE = "(^$|[0-9]{10})";

    public static final String PATTERN_TENANT = "^[a-zA-Z. ]*$";

}

