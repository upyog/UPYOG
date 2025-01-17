package org.upyog.request.service.repository;

import org.upyog.request.service.web.models.WaterTankerBookingRequest;

public interface RequestServiceRepository {

	void saveWaterTankerBooking(WaterTankerBookingRequest waterTankerRequest);
	
}
