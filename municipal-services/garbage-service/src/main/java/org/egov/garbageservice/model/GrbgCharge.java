package org.egov.garbageservice.model;

import org.egov.tracer.annotations.CustomSafeHtml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
/**
 * Master charge definition (rate, tax head, effective period) for garbage fee computation.
 * Referenced when building demands and bills during account approval or scheduler runs.
 */
@NoArgsConstructor
public class GrbgCharge {

    @CustomSafeHtml
    private String uuid;
    @CustomSafeHtml
    private String category;
    @CustomSafeHtml
    private String type;
    private Double amountPerDay;
    private Double amountPm;
    private Boolean isActive;
}
