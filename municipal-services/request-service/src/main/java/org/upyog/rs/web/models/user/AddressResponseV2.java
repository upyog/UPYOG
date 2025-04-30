package org.upyog.rs.web.models.user;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.egov.common.contract.response.ResponseInfo;

import java.util.List;

/**
 * AddressResponseV2 is a model class that represents the response
 * structure for address-related requests in the system.
 * 
 * Key Components:
 * - responseInfo: Contains metadata about the response, such as status and messages.
 * - address: A list of AddressV2 objects representing the address details in the response.
 * 
 * Annotations:
 * - @AllArgsConstructor: Automatically generates a constructor with all fields.
 * - @Getter: Automatically generates getter methods for all fields.
 * - @JsonProperty: Maps JSON properties to Java fields for serialization/deserialization.
 */
@AllArgsConstructor
@Getter
public class AddressResponseV2 {

    @JsonProperty("responseInfo")
    ResponseInfo responseInfo;

    @JsonProperty("address")
    List<AddressV2> address;

}
