package com.example.hpgarbageservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
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
