package org.upyog.request.service.repository;

import org.upyog.request.service.web.models.WaterTankerBookingDetail;
import org.upyog.request.service.web.models.WaterTankerBookingRequest;
import org.upyog.request.service.web.models.WaterTankerBookingSearchCriteria;

import java.util.List;

public interface RequestServiceRepository {

	void saveWaterTankerBooking(WaterTankerBookingRequest waterTankerRequest);

	List<WaterTankerBookingDetail> getWaterTankerBookingDetails(WaterTankerBookingSearchCriteria waterTankerBookingSearchCriteria);
}
