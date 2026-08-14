package org.upyog.cdwm.web.models.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.upyog.cdwm.web.models.user.enums.AddressType;

import java.time.Instant;

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
    @JsonFormat(shape = JsonFormat.Shape.NUMBER, timezone = "UTC")
    private Instant lastModifiedDate;
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
