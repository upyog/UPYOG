package org.upyog.rs.service;

import org.upyog.rs.web.models.waterTanker.WaterTankerBookingRequest;

public interface RequestServiceNotificationService {

	public void process(WaterTankerBookingRequest waterTankerRequest);
	
	

}
