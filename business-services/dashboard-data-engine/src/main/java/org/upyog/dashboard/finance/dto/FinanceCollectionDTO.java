package org.upyog.dashboard.finance.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinanceCollectionDTO {
    // Basic fields for collection mapping if needed in the future
    private String paymentChannelType;
    private Double amount;
}
