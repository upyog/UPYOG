package org.egov.custom.mapper.billing.impl;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeterReadingSearchResponse {

    private String id;
    private String connectionNo;
    private String billingPeriod;
    private String meterStatus;

    private BigDecimal lastReading;
    private BigDecimal currentReading;

    private String propertyId;
    private String applicationNo;
    private String connectionStatus;
    private String applicationStatus;

    private String usageCategory;

    private String zoneCode;
    private String blockCode;
    private String localityCode;
    private String groups;
}