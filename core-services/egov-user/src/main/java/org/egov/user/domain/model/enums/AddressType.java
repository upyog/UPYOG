package org.egov.user.domain.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum AddressType {
    // This order should not be interrupted
    PERMANENT("PERMANENT"), CORRESPONDENCE("CORRESPONDENCE"), USUALADDRESS("CORRESPONDENCE"), EVENTADDRESS("CORRESPONDENCE"), PRESENTADDRESS("CORRESPONDENCE"), PROPERTYADDRESS("CORRESPONDENCE"),
    // This category is added to handle the address type of other apart from the above-mentioned types and also this is part of V2 user api endpoints
    OTHER("OTHER");

    @JsonCreator
    public static AddressType fromValue(String text) {
        for (AddressType b : AddressType.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }

    private String value;

    AddressType(String value) {
        this.value = value;
    }

}
