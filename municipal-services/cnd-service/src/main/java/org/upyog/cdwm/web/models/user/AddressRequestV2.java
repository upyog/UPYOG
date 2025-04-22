package org.upyog.cdwm.web.models.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class AddressRequestV2 {

    @JsonProperty("requestInfo")
    RequestInfo requestInfo;

    @JsonProperty("address")
    AddressV2 address;

    @JsonProperty("userUuid")
    String userUuid;

    @JsonProperty("addressId")
    String addressId;

}
