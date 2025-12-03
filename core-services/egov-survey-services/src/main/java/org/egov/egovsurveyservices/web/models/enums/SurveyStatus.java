package org.egov.egovsurveyservices.web.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SurveyStatus {

    DRAFT("Draft"),

    SUBMIT("Submit");



    private String value;

    SurveyStatus(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static SurveyStatus fromValue(String text) {
        for (SurveyStatus s : SurveyStatus.values()) {
            if (String.valueOf(s.value).equalsIgnoreCase(text)) {
                return s;
            }
        }
        return null;
    }
}