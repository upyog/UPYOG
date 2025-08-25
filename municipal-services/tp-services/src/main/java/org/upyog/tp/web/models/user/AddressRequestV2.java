package org.upyog.tp.web.models.user;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;
import org.upyog.tp.web.models.ApplicantDetail;
/**
 * This class represents the AddressRequestV2 model used for handling
 * address-related requests in the system.
 * 
 * Key Components:
 * - requestInfo: Contains metadata about the request, such as user and request details.
 * - address: Represents the address details being requested or updated.
 * - applicantDetails: Holds information about the applicant making the request.
 * - userUuid: A unique identifier for the user associated with the request.
 * - addressId: A unique identifier for the address being referenced.
 * 
 * Annotations:
 * - @AllArgsConstructor: Automatically generates a constructor with all fields.
 * - @NoArgsConstructor: Automatically generates a no-argument constructor.
 * - @Getter: Automatically generates getter methods for all fields.
 * - @Builder: Enables the builder pattern for creating instances of this class.
 * - @JsonProperty: Maps JSON properties to Java fields for serialization/deserialization.
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class AddressRequestV2 {

    @JsonProperty("requestInfo")
    RequestInfo requestInfo;

    @JsonProperty("address")
    AddressV2 address;

    @JsonProperty("applicantDetails")
    ApplicantDetail applicantDetails;

    @JsonProperty("userUuid")
    String userUuid;

    @JsonProperty("addressId")
    String addressId;

}