package org.upyog.dashboard.chb.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Model encapsulating aggregated venue availability, application counts, and booking JSON for CHB module.
 */
@Getter
@Setter
public class CHBAggregatedData {

    private Integer totalActiveVenueAvailable;
    private Integer totalApplicationReceived;
    private Integer totalCollections;
    private Integer noShowBookings;
    private String bookingsJson;
    private String bookingTypeJson;
    private String createdByListJson;
}
