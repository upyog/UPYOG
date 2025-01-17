package org.upyog.request.service;

import org.upyog.request.service.web.models.WaterTankerBookingDetail;
import org.upyog.request.service.web.models.WaterTankerBookingRequest;

public interface WaterTankerService {
	
	public WaterTankerBookingDetail createWaterTankerBooking(WaterTankerBookingRequest waterTankerRequest);

}
