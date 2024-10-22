package org.upyog.sv.service;

import org.upyog.sv.web.models.StreetVendingDetail;
import org.upyog.sv.web.models.StreetVendingRequest;

public interface StreetVendingService {

	public StreetVendingDetail createStreetVendingApplication(StreetVendingRequest vendingRequest);
	
}
