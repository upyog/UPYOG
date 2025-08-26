package org.upyog.cdwm.web.models.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.egov.common.contract.response.ResponseInfo;

import java.util.List;

@AllArgsConstructor
@Getter
public class AddressResponseV2 {

    @JsonProperty("responseInfo")
    ResponseInfo responseInfo;

    @JsonProperty("address")
    List<AddressV2> address;

}
