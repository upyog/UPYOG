package org.upyog.dashboard.chb.model;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CHBMetric {
    private Integer totalActiveVenueAvailable;
    private Integer totalApplicationReceived;
    private Integer totalCollections;
    private Integer noShowBookings;
    private List<Map<String, Object>> bookings;
    private List<Map<String, Object>> bookingType;

    public Map<String, Object> toMap() {
        Map<String, Object> dataMap = new java.util.LinkedHashMap<>();
        dataMap.put("totalActiveVenueAvailable", totalActiveVenueAvailable != null ? totalActiveVenueAvailable : 0);
        dataMap.put("totalApplicationReceived", totalApplicationReceived != null ? totalApplicationReceived : 0);
        dataMap.put("totalCollections", totalCollections != null ? totalCollections : 0);
        dataMap.put("noShowBookings", noShowBookings != null ? noShowBookings : 0);
        dataMap.put("bookings", bookings != null ? bookings : List.of());
        dataMap.put("bookingType", bookingType != null ? bookingType : List.of());
        return dataMap;
    }
}
