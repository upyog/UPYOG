package org.upyog.dashboard.finance.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO holding collection and account detail breakdown for the Finance module.
 */
@Getter
@Setter
public class FinanceCollectionDTO {
    // Basic fields for collection mapping if needed in the future
    private String paymentChannelType;
    private Double amount;
}
