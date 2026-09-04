package org.upyog.dashboard.chb.dto;

import lombok.Getter;
import lombok.Setter;

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
