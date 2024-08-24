package org.egov.garbageservice.model;

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
public class GrbgCollectionUnit {

    private String uuid;
    private String unitName;
    private String unitWard;
    private String ulbName;
    private String typeOfUlb;
    private Long garbageId;
    private String unitType;
    private String category;
    private String subCategory;
    private String subCategoryType;
    private Boolean isActive;
}
