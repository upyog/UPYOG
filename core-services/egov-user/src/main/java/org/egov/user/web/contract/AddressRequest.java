package org.egov.user.web.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.egov.common.contract.request.RequestInfo;
import org.egov.user.domain.model.Address;

import java.util.List;

@AllArgsConstructor
@Getter
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
