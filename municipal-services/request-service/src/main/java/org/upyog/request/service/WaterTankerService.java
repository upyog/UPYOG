package org.upyog.request.service;

import org.egov.common.contract.request.RequestInfo;
import org.upyog.request.service.web.models.WaterTankerBookingDetail;
import org.upyog.request.service.web.models.WaterTankerBookingRequest;
import org.upyog.request.service.web.models.WaterTankerBookingSearchCriteria;

import java.util.List;

public interface WaterTankerService {
	
	public WaterTankerBookingDetail createNewWaterTankerBookingRequest(WaterTankerBookingRequest waterTankerRequest);

	public List<WaterTankerBookingDetail> getWaterTankerBookingDetails(RequestInfo requestInfo, WaterTankerBookingSearchCriteria waterTankerBookingSearchCriteria);

	public Integer getApplicationsCount(WaterTankerBookingSearchCriteria waterTankerBookingSearchCriteria, RequestInfo requestInfo);

	public WaterTankerBookingDetail updateWaterTankerBooking(WaterTankerBookingRequest waterTankerRequest);

}
