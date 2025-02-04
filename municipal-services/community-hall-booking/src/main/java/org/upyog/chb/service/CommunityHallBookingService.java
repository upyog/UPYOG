package org.upyog.chb.service;

import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.upyog.chb.enums.BookingStatusEnum;
import org.upyog.chb.web.models.Asset;
import org.upyog.chb.web.models.AssetSearchCriteria;
import org.upyog.chb.web.models.AssetUpdate;
import org.upyog.chb.web.models.AssetUpdateRequest;
import org.upyog.chb.web.models.CommunityHallBookingActionRequest;
import org.upyog.chb.web.models.CommunityHallBookingActionResponse;
import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.CommunityHallBookingRequest;
import org.upyog.chb.web.models.CommunityHallBookingSearchCriteria;
import org.upyog.chb.web.models.CommunityHallBookingUpdateStatusRequest;
import org.upyog.chb.web.models.CommunityHallSlotAvailabilityDetail;
import org.upyog.chb.web.models.CommunityHallSlotSearchCriteria;
import org.upyog.chb.web.models.RequestInfoWrapper;

import digit.models.coremodels.PaymentDetail;

public interface CommunityHallBookingService {

	CommunityHallBookingDetail createBooking(@Valid CommunityHallBookingRequest communityHallsBookingRequest);
	
	CommunityHallBookingDetail createInitBooking(@Valid CommunityHallBookingRequest communityHallsBookingRequest);	
	
	List<CommunityHallBookingDetail> getBookingDetails(CommunityHallBookingSearchCriteria bookingSearchCriteria, RequestInfo info);

	CommunityHallBookingDetail updateBooking(@Valid CommunityHallBookingRequest communityHallsBookingRequest, PaymentDetail paymentDetail, BookingStatusEnum bookingStatusEnum);

	List<CommunityHallSlotAvailabilityDetail> getCommunityHallSlotAvailability(CommunityHallSlotSearchCriteria criteria);

	CommunityHallBookingActionResponse getApplicationDetails( @Valid CommunityHallBookingActionRequest communityHallActionRequest);
	
	CommunityHallBookingDetail updateStatus( @Valid CommunityHallBookingUpdateStatusRequest communityHallBookingUpdateStatusRequest);

	List<Asset> fetchAssets(AssetSearchCriteria assetSearchCriteria, RequestInfo requestInfo);

	void setRelatedAsset(List<CommunityHallBookingDetail> applications, @Valid RequestInfoWrapper requestInfoWrapper);

	void setRelatedAssetData(CommunityHallBookingRequest communityHallsBookingRequest);

	List<AssetUpdate> updateAsset(AssetUpdateRequest assetUpdateRequest);


	

}
