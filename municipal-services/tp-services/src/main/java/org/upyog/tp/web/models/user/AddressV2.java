package org.upyog.tp.web.models.user;

import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.upyog.tp.enums.AddressType;

import java.util.Date;

/**
 * AddressV2 is a model class that represents the structure of an address
 * in the system. It includes various fields to capture detailed address
 * information and methods to validate the address data.
 * 
 * Key Fields:
 * - pinCode: The postal code of the address.
 * - city: The city where the address is located.
 * - address: The primary address line.
 * - type: The type of address (e.g., residential, commercial).
 * - id: A unique identifier for the address.
 * - tenantId: The tenant identifier associated with the address.
 * - userId: The user identifier associated with the address.
 * - addressType: A string representation of the address type.
 * - LastModifiedBy: The identifier of the user who last modified the address.
 * - LastModifiedDate: The date when the address was last modified.
 * - address2: An additional address line for more details.
 * - houseNumber: The house number of the address.
 * - houseName: The name of the house or building.
 * - streetName: The name of the street.
 * - landmark: A landmark near the address.
 * - locality: The locality or neighborhood of the address.
 * 
 * Key Methods:
 * - isInvalid(): Validates the address fields and checks if any are invalid.
 * - isNotEmpty(): Checks if any of the key address fields are not empty.
 * 
 * Annotations:
 * - @Getter: Automatically generates getter methods for all fields.
 * - @Setter: Automatically generates setter methods for all fields.
 * - @Builder: Enables the builder pattern for creating instances of this class.
 * - @AllArgsConstructor: Automatically generates a constructor with all fields.
 * - @EqualsAndHashCode: Generates equals and hashCode methods based on the "id" field.
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class AddressV2 {
    private String pinCode;
    private String city;
    private String address;
    private AddressType type;
    private Long id;
    private String tenantId;
    private Long userId;
    private String addressType;
    private Long LastModifiedBy;
    private Date LastModifiedDate;
    //    Adding new fields in Address model for profile update as part of V2 api
    private String address2;
    private String houseNumber;
    private String houseName;
    private String streetName;
    private String landmark;
    private String locality;


    boolean isInvalid() {
        return isPinCodeInvalid()
                || isCityInvalid()
                || isAddressInvalid();
    }

    boolean isNotEmpty() {
        return StringUtils.isNotEmpty(pinCode)
                || StringUtils.isNotEmpty(city)
                || StringUtils.isNotEmpty(address);
    }

    boolean isPinCodeInvalid() {
        return pinCode != null && pinCode.length() > 10;
    }

    boolean isCityInvalid() {
        return city != null && city.length() > 300;
    }

    boolean isAddressInvalid() {
        return address != null && address.length() > 300;
    }
}
