package org.upyog.cdwm.web.models.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;
import org.upyog.cdwm.web.models.user.Address;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class AddressRequest {

    @JsonProperty("requestInfo")
    RequestInfo requestInfo;

    @JsonProperty("address")
    Address address;

    @JsonProperty("userUuid")
    String userUuid;

    @JsonProperty("addressId")
    String addressId;

}
