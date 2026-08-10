package org.upyog.dashboard.chb.dto;

import lombok.Data;

@Data
public class CHBAggregatedData {

    private Integer totalActiveVenueAvailable;
    private Integer totalApplicationReceived;
    private Integer totalCollections;
    private Integer noShowBookings;
    
    private String bookingsJson;
    private String bookingTypeJson;

}
