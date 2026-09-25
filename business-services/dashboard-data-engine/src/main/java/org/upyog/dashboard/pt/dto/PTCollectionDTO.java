package org.upyog.dashboard.pt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * DTO representing a payment collection row fetched from DB.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PTCollectionDTO {
    private String usageCategory;
    private String paymentMode;
    private String paymentId;
    private String taxHeadCode;
    private Double taxHeadAmount;
}
