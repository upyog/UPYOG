package org.upyog.sv.service;

import org.upyog.sv.web.models.StreetVendingRequest;

public interface StreetyVendingNotificationService {

	public void process(StreetVendingRequest streetVendingRequest, String status);
	
	

}
