package org.egov.garbageservice.web.models;

import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.egov.garbageservice.enums.AddressType;
import org.egov.tracer.annotations.CustomSafeHtml;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
/**
 * Postal address on UserV2 with type (permanent/correspondence) and geo hierarchy fields.
 * Uses AddressType enum for classification in user-service integration.
 */
@EqualsAndHashCode(of = {"id"})
public class Address {
    @CustomSafeHtml
    private String pinCode;
    @CustomSafeHtml
    private String city;
    @CustomSafeHtml
    private String address;
    private AddressType type;
    private Long id;
    @CustomSafeHtml
    private String tenantId;
    private Long userId;
    @CustomSafeHtml
    private String addressType;
    private Long LastModifiedBy;
    private Date LastModifiedDate;

    /**
     * Gets the invalid.
     *
     * @return the current invalid
     */

    boolean isInvalid() {
        return isPinCodeInvalid() || isCityInvalid() || isAddressInvalid();
    }

    /**
     * Gets the notEmpty.
     *
     * @return the current notEmpty
     */

    boolean isNotEmpty() {
        return StringUtils.isNotEmpty(pinCode) || StringUtils.isNotEmpty(city) || StringUtils.isNotEmpty(address);
    }

    /**
     * Gets the pinCodeInvalid.
     *
     * @return the current pinCodeInvalid
     */

    boolean isPinCodeInvalid() {
        return pinCode != null && pinCode.length() > 10;
    }

    /**
     * Gets the cityInvalid.
     *
     * @return the current cityInvalid
     */

    boolean isCityInvalid() {
        return city != null && city.length() > 300;
    }

    /**
     * Gets the addressInvalid.
     *
     * @return the current addressInvalid
     */

    boolean isAddressInvalid() {
        return address != null && address.length() > 300;
    }
}
