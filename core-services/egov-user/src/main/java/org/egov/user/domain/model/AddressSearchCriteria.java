package org.egov.user.domain.model;

import lombok.*;
import org.egov.user.domain.model.enums.AddressType;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class AddressSearchCriteria {
    private Long id;
    private Long userId;
    private String tenantId;
    private AddressType addressType;
    private String city;
    private String pinCode;
    private String status;

    private String userUuid; // This field is added to search address with particular user using userUuid

}
