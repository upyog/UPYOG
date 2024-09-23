package org.egov.user.domain.model;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.egov.user.domain.model.enums.AddressType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Address {
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
