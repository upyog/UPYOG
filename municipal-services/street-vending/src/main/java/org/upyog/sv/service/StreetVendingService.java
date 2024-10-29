package org.upyog.sv.service;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.upyog.sv.web.models.StreetVendingDetail;
import org.upyog.sv.web.models.StreetVendingRequest;
import org.upyog.sv.web.models.StreetVendingSearchCriteria;

public interface StreetVendingService {

	public StreetVendingDetail createStreetVendingApplication(StreetVendingRequest vendingRequest);
	
	List<StreetVendingDetail> getStreetVendingDetails(RequestInfo info, StreetVendingSearchCriteria streetVendingSearchCriteria);
	
}
