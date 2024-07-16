package com.example.hpgarbageservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
public class GrbgCollection {

    private String uuid;
    private Long garbageId;
    private String staffUuid;
    private String collecType;
    private Long startDate;
    private Long endDate;
    private Boolean isActive;
    private String createdBy;
    private Long createdDate;
    private String lastModifiedBy;
    private Long lastModifiedDate;
}
