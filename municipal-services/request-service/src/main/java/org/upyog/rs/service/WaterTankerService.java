package org.upyog.rs.service;

import org.egov.common.contract.request.RequestInfo;
import org.upyog.rs.web.models.WaterTankerBookingDetail;
import org.upyog.rs.web.models.WaterTankerBookingRequest;
import org.upyog.rs.web.models.WaterTankerBookingSearchCriteria;

import digit.models.coremodels.PaymentRequest;

import java.util.List;

public interface WaterTankerService {
	
	public WaterTankerBookingDetail createNewWaterTankerBookingRequest(WaterTankerBookingRequest waterTankerRequest);

	public List<WaterTankerBookingDetail> getWaterTankerBookingDetails(RequestInfo requestInfo, WaterTankerBookingSearchCriteria waterTankerBookingSearchCriteria);

	public Integer getApplicationsCount(WaterTankerBookingSearchCriteria waterTankerBookingSearchCriteria, RequestInfo requestInfo);

	public WaterTankerBookingDetail updateWaterTankerBooking(WaterTankerBookingRequest waterTankerRequest, PaymentRequest paymentRequest, String applicationStatus);


}
