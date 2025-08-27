package org.egov.user.domain.model;

import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.egov.user.domain.model.enums.AddressType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id","pinCode","city","address","type","address2","houseNumber","houseName","streetName","landmark","locality"})
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
//    Adding new fields in Address model for profile update as part of V2 api
    private String address2;
    private String houseNumber;
    private String houseName;
    private String streetName;
    private String landmark;
    private String locality;

    @JsonIgnore
    boolean isInvalid() {
        return isPinCodeInvalid()
                || isCityInvalid()
                || isAddressInvalid();
    }

    @JsonIgnore
    public boolean isNotEmpty() {
        return StringUtils.isNotEmpty(pinCode)
                || StringUtils.isNotEmpty(city)
                || StringUtils.isNotEmpty(address);
    }

    /**
     * Validates if mandatory address fields are present when mandatory field validation is enabled
     * @param mandatoryFieldsEnabled whether mandatory field validation is enabled
     * @return true if validation fails, false otherwise
     */
    @JsonIgnore
    public boolean isMandatoryFieldsMissing(boolean mandatoryFieldsEnabled) {
        if (!mandatoryFieldsEnabled) {
            return false; // Skip validation if not enabled
        }
        
        // Check if any of the mandatory fields are null or empty
        return StringUtils.isEmpty(city) 
                || StringUtils.isEmpty(pinCode) 
                || StringUtils.isEmpty(address);
    }
    
    @JsonIgnore
    boolean isPinCodeInvalid() {
        return pinCode != null && pinCode.length() > 10;
    }
    
    @JsonIgnore
    boolean isCityInvalid() {
        return city != null && city.length() > 300;
    }

    @JsonIgnore
    boolean isAddressInvalid() {
        return address != null && address.length() > 300;
    }
}
