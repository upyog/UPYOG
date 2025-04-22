package org.upyog.cdwm.web.models.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.egov.common.contract.response.ResponseInfo;
import org.upyog.cdwm.web.models.user.Address;

import java.util.List;

@AllArgsConstructor
@Getter
public class AddressResponse {

    @JsonProperty("responseInfo")
    ResponseInfo responseInfo;

    @JsonProperty("address")
    List<Address> address;

}
