package org.upyog.tp.web.models.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.upyog.tp.enums.AddressType;

import java.util.Date;

/**
 * AddressV2 is a model class that represents the structure of an address
 * in the system. It includes various fields to capture detailed address
 * information and methods to validate the address data.
 */

@SuppressWarnings({"java:S3437", "java:S2143", "java:S6212", "java:S6213"})
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
    @JsonProperty("LastModifiedBy")
    private Long lastModifiedBy;
    @JsonProperty("LastModifiedDate")
    @SuppressWarnings({"java:S3437", "java:S2143", "java:S6212", "java:S6213"})
    private Date lastModifiedDate;
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
