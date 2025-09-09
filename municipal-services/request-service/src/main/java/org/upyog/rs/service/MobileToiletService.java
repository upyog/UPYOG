package org.upyog.rs.service;

import digit.models.coremodels.PaymentRequest;
import org.egov.common.contract.request.RequestInfo;
import org.upyog.rs.web.models.mobileToilet.MobileToiletBookingDetail;
import org.upyog.rs.web.models.mobileToilet.MobileToiletBookingRequest;
import org.upyog.rs.web.models.mobileToilet.MobileToiletBookingSearchCriteria;

import java.util.List;

public interface MobileToiletService {

      public MobileToiletBookingDetail createNewMobileToiletBookingRequest(MobileToiletBookingRequest mobileToiletRequest);

      public List<MobileToiletBookingDetail> getMobileToiletBookingDetails(RequestInfo requestInfo, MobileToiletBookingSearchCriteria mobileToiletBookingSearchCriteria);

      public Integer getApplicationsCount(MobileToiletBookingSearchCriteria mobileToiletBookingSearchCriteria, RequestInfo requestInfo);

      public void updateMobileToiletBooking(PaymentRequest paymentRequest, String applicationStatus);

      public MobileToiletBookingDetail updateMobileToiletBooking(MobileToiletBookingRequest mobileToiletRequest, String applicationStatus);


}
