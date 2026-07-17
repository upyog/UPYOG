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
 * Holds applicant and collection unit details from the new payload structure.
 * Fields are mapped to flat fields on GarbageAccount and GrbgCollectionUnit during enrichment.
 */
public class GarbageSpecification {

    @CustomSafeHtml
    private String oldGarbageId;

    @CustomSafeHtml
    private String typeOfCollection;

    @CustomSafeHtml
    private String propertyOwnerType;

    @CustomSafeHtml
    private String name;

    @CustomSafeHtml
    private String phoneNumber;

    @CustomSafeHtml
    private String gender;

    @CustomSafeHtml
    private String email;

    @CustomSafeHtml
    private String category;

    @CustomSafeHtml
    private String subCategory;

    @CustomSafeHtml
    private String subCategoryType;

    private Boolean isvariablecalculation;

    private Boolean isbulkgeneration;

    private Integer no_of_units;

    private Boolean isAdditional;

    private Boolean isInheritance;

    @CustomSafeHtml
    private String specialCategory;
}
