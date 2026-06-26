package org.egov.custom.mapper.billing.impl;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeterConnection {

    private String connectionNo;
    private String propertyId;
    private String applicationNo;

    private String usageCategory;

    private String ownerId;
    private String ownerName;
    private String guardianName;
    private String mobileNumber;

    private String zoneCode;
    private String blockCode;
    private String localityCode;

    private String groups;

    private List<MeterReading> meterReadings;
}
