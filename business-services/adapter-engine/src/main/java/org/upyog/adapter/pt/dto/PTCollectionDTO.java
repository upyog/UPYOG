package org.upyog.adapter.pt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a payment collection row fetched from DB.
 */
@Data
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
