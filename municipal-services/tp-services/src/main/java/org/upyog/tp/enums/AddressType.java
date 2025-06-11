package org.upyog.tp.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * AddressType is an enumeration that defines the types of addresses
 * supported in the system.
 * Enum Values:
 * - PERMANENT: Represents a permanent address.
 * - CORRESPONDENCE: Represents a correspondence or mailing address.
 * - OTHER: Represents any other type of address not explicitly defined.
 * Key Methods:
 * - fromValue(String text): Converts a string value to its corresponding
 *   AddressType enum value. Returns null if no match is found.
 * Fields:
 * - value: A string representation of the address type.
 * Annotations:
 * - @JsonCreator: Indicates that the fromValue method is used for
 *   deserialization of JSON into an AddressType enum.
 */
public enum AddressType {

    PERMANENT("PERMANENT"), CORRESPONDENCE("CORRESPONDENCE"), OTHER("OTHER");

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
