package org.upyog.dashboard.adv.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO holding collection and receipt breakdown details for the ADV module.
 */
@Getter
@Setter
public class ADVCollectionDTO {
    private String paymentChannelType;
    private Double amount;
}
