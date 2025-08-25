package org.upyog.rs.repository;

import java.util.List;

import org.upyog.rs.web.models.mobileToilet.MobileToiletBookingDetail;
import org.upyog.rs.web.models.mobileToilet.MobileToiletBookingRequest;
import org.upyog.rs.web.models.mobileToilet.MobileToiletBookingSearchCriteria;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingDetail;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingRequest;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingSearchCriteria;

public interface RequestServiceRepository {

	void saveWaterTankerBooking(WaterTankerBookingRequest waterTankerRequest);

	List<WaterTankerBookingDetail> getWaterTankerBookingDetails(WaterTankerBookingSearchCriteria waterTankerBookingSearchCriteria);

	Integer getApplicationsCount(WaterTankerBookingSearchCriteria criteria);

	void updateWaterTankerBooking(WaterTankerBookingRequest waterTankerRequest);

	void saveMobileToiletBooking(MobileToiletBookingRequest mobileToiletRequest);

	List<MobileToiletBookingDetail> getMobileToiletBookingDetails(MobileToiletBookingSearchCriteria mobileToiletBookingSearchCriteria);

	Integer getApplicationsCount(MobileToiletBookingSearchCriteria criteria);

	void updateMobileToiletBooking(MobileToiletBookingRequest mobileToiletRequest);

}
