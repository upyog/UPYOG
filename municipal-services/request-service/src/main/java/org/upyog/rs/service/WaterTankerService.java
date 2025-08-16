package org.upyog.rs.service;

import org.egov.common.contract.request.RequestInfo;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingDetail;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingRequest;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingSearchCriteria;

import digit.models.coremodels.PaymentRequest;

import java.util.List;

public interface WaterTankerService {
	
	public WaterTankerBookingDetail createNewWaterTankerBookingRequest(WaterTankerBookingRequest waterTankerRequest);

	public List<WaterTankerBookingDetail> getWaterTankerBookingDetails(RequestInfo requestInfo, WaterTankerBookingSearchCriteria waterTankerBookingSearchCriteria);

	public Integer getApplicationsCount(WaterTankerBookingSearchCriteria waterTankerBookingSearchCriteria, RequestInfo requestInfo);

	public void updateWaterTankerBooking(PaymentRequest paymentRequest, String applicationStatus);

	public WaterTankerBookingDetail updateWaterTankerBooking(WaterTankerBookingRequest waterTankerRequest, String applicationStatus);


}
