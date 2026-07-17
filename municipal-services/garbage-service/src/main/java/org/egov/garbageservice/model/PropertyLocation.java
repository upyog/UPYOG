package org.egov.garbageservice.model;

import org.egov.tracer.annotations.CustomSafeHtml;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
/**
 * Holds property and address details from the new payload structure.
 * propertyId is mapped to GarbageAccount.propertyId during enrichment.
 */
public class PropertyLocation {

    @CustomSafeHtml
    private String propertyId;

    @CustomSafeHtml
    private String houseNo;

    @CustomSafeHtml
    private String houseName;

    @CustomSafeHtml
    private String streetName;

    @CustomSafeHtml
    private String addressline1;

    @CustomSafeHtml
    private String addressline2;

    @CustomSafeHtml
    private String landmark;

    @CustomSafeHtml
    private String city;

    @CustomSafeHtml
    private String locality;

    @CustomSafeHtml
    private String pincode;
}
