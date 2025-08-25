package org.upyog.cdwm.web.models.user.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

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
