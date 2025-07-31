package org.upyog.cdwm.web.models;

import lombok.*;
import org.upyog.cdwm.web.models.user.enums.AddressType;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CNDAddressDetail {
    private String applicationId;
    private String houseNumber;
    private String addressLine1;
    private String addressLine2;
    private String landmark;
    private String floorNumber;
    private String locality;
    private String city;
    private String pinCode;
    private AddressType addressType;
}
