package org.egov.garbageservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
public class GrbgCharge {

    private String uuid;
    private String category;
    private String type;
    private Double amountPerDay;
    private Double amountPm;
    private Boolean isActive;
}
