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
/**
 * Defines a chargeable garbage collection unit (bins, capacity, frequency) on an application.
 * Used in fee calculation and open-search bill preview collection unit lists.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GrbgCollectionUnit {

    @CustomSafeHtml
    private String uuid;
    @CustomSafeHtml
    private String unitName;
    @CustomSafeHtml
    private String unitWard;
    @CustomSafeHtml
    private String ulbName;
    @CustomSafeHtml
    private String ownerType;
    @CustomSafeHtml
    private String typeOfUlb;
    private Long garbageId;
    @CustomSafeHtml
    private String unitType;
    @CustomSafeHtml
    private String category;
    @CustomSafeHtml
    private String subCategory;
    @CustomSafeHtml
    private String subCategoryType;
    private Boolean isActive;
    @Builder.Default
    private Boolean isbplunit = false;
    @Builder.Default
    private Boolean ismonthlybilling = true;
    @Builder.Default
    private Boolean isvariablecalculation = false;
    @Builder.Default
    private Boolean isbulkgeneration = false;
    @Builder.Default
    private Integer no_of_units = 0;
    @CustomSafeHtml
    private Boolean isInheritance;
    @CustomSafeHtml
    private String specialCategory;

}
