package org.upyog.sv.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum ApplicationCreatedByEnum {
    EMPLOYEE("EMPLOYEE"),
    CITIZEN("CITIZEN");

    private final String value;

    private static final Map<String, ApplicationCreatedByEnum> MAP =
            Stream.of(values()).collect(Collectors.toMap(v -> v.value.toLowerCase(), v -> v));

    ApplicationCreatedByEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String toValue() {
        return value;
    }

    @JsonCreator
    public static ApplicationCreatedByEnum fromValue(String value) {
        if (value == null) return null;
        ApplicationCreatedByEnum result = MAP.get(value.toLowerCase());
        if (result == null) {
            throw new IllegalArgumentException("Invalid ApplicationCreatedBy value: " + value);
        }
        return result;
    }
}
