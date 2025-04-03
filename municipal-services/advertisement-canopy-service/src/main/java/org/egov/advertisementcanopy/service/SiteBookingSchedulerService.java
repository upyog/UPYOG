package org.egov.advertisementcanopy.service;

import java.time.Instant;
import java.util.Collections;

import org.egov.advertisementcanopy.model.SiteBooking;
import org.egov.advertisementcanopy.model.SiteBookingResponse;
import org.egov.advertisementcanopy.model.SiteBookingSearchCriteria;
import org.egov.advertisementcanopy.model.SiteBookingSearchRequest;
import org.egov.advertisementcanopy.model.SiteUpdateRequest;
import org.egov.advertisementcanopy.model.SiteUpdationResponse;
import org.egov.advertisementcanopy.util.AdvtConstants;
import org.egov.advertisementcanopy.util.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SiteBookingSchedulerService {

	@Autowired
	private SiteBookingService siteBookingService;

	@Autowired
	private SiteService siteService;

	public void changeSiteStatus(RequestInfoWrapper requestInfoWrapper) {

		SiteBookingSearchRequest siteBookingSearchRequest = SiteBookingSearchRequest.builder()
				.requestInfo(requestInfoWrapper.getRequestInfo()).skipValidation(true)
				.siteBookingSearchCriteria(SiteBookingSearchCriteria.builder()
						.siteStatus(Collections.singletonList(AdvtConstants.SITE_STATUS_BOOKED)).build())
				.build();

		SiteBookingResponse siteBookingResponse = siteBookingService.searchBooking(siteBookingSearchRequest);

		if (null != siteBookingResponse && !CollectionUtils.isEmpty(siteBookingResponse.getSiteBookings())) {
			siteBookingResponse.getSiteBookings().stream().filter(this::isExpired)
					.forEach(siteBooking -> updateSiteBooking(siteBooking, requestInfoWrapper));
		}

//		return siteBookingResponse;
	}

	private void updateSiteBooking(SiteBooking siteBooking, RequestInfoWrapper requestInfoWrapper) {
		siteBooking.getSiteCreationData().setStatus(AdvtConstants.SITE_STATUS_AVAILABLE);

		SiteUpdateRequest siteUpdateRequest = SiteUpdateRequest.builder()
				.requestInfo(requestInfoWrapper.getRequestInfo()).siteUpdationData(siteBooking.getSiteCreationData())
				.build();
		SiteUpdationResponse siteUpdationResponse = siteService.update(siteUpdateRequest);

		siteBooking.setSiteCreationData(siteUpdationResponse.getSiteCreationData());
	}

	private boolean isExpired(SiteBooking siteBooking) {
		return Instant.ofEpochSecond(siteBooking.getToDate()).isBefore(Instant.now());
	}

}
