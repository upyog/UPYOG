package org.upyog.dashboard.chb.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO holding collection and receipt breakdown details for the CHB module.
 */
@Getter
@Setter
public class CHBCollectionDTO {
    private String bookingStatus;
    private String bookingMode;
    private Double amount;
}