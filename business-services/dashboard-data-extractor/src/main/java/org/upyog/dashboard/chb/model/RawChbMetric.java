package org.upyog.dashboard.chb.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Raw data class for CHB combined metrics fetched directly via
 * BeanPropertyRowMapper.
 * <p>
 * This class is decoupled from the Engine's DTO layer, allowing dynamic
 * reflection mapping of flat SQL result sets directly into a structured Java
 * object.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawChbMetric {

    private String tenantid;
    private Integer totalActiveVenueAvailable;
    private Integer totalApplicationReceived;
    private Integer totalCollections;
    private Integer noShowBookings;
    private String bookingsJson;
    private String createdByListJson;
}
