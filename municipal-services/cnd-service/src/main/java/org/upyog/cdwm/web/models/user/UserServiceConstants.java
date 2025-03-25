package org.upyog.cdwm.web.models.user;

import org.springframework.context.annotation.Configuration;

@Configuration
public class UserServiceConstants {

    public static final String PATTERN_NAME = "^[^\\\\$\\\"<>?\\\\\\\\~`!@#$%^()+={}\\\\[\\\\]*,:;“”‘’]*$";

    public static final String PATTERN_MOBILE = "(^$|[0-9]{10})";

    public static final String PATTERN_TENANT = "^[a-zA-Z. ]*$";

}

