package org.upyog.rs.repository;

import java.util.List;

import org.upyog.rs.web.models.WaterTankerBookingDetail;
import org.upyog.rs.web.models.WaterTankerBookingRequest;
import org.upyog.rs.web.models.WaterTankerBookingSearchCriteria;

public interface RequestServiceRepository {

	void saveWaterTankerBooking(WaterTankerBookingRequest waterTankerRequest);

	List<WaterTankerBookingDetail> getWaterTankerBookingDetails(WaterTankerBookingSearchCriteria waterTankerBookingSearchCriteria);

	Integer getApplicationsCount(WaterTankerBookingSearchCriteria criteria);

	void updateWaterTankerBooking(WaterTankerBookingRequest waterTankerRequest);

}
