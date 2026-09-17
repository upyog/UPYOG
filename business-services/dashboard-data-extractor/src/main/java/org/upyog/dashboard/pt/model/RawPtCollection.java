package org.upyog.dashboard.pt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Raw data class for PT collection metrics fetched directly via
 * BeanPropertyRowMapper.
 * <p>
 * This class facilitates grouping and aggregation of individual collection
 * receipts by tenant, before being mapped to the target Dashboard payload DTO.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawPtCollection {

    private String tenantid;
    private String usageCategory;
    private String paymentMode;
    private String paymentId;
    private String taxHeadCode;
    private Double taxHeadAmount;
}
