package org.egov.advertisementcanopy.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.egov.advertisementcanopy.model.AuditDetails;
import org.egov.advertisementcanopy.model.SiteBooking;
import org.egov.advertisementcanopy.model.SiteBookingRequest;
import org.egov.advertisementcanopy.model.SiteBookingResponse;
import org.egov.advertisementcanopy.model.SiteBookingSearchCriteria;
import org.egov.advertisementcanopy.model.SiteBookingSearchRequest;
import org.egov.advertisementcanopy.producer.Producer;
import org.egov.advertisementcanopy.repository.SiteBookingRepository;
import org.egov.advertisementcanopy.util.ResponseInfoFactory;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class SiteBookingService {

	@Autowired
	private SiteBookingRepository repository;

	@Autowired
	private Producer producer;

	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	public SiteBookingResponse createBooking(SiteBookingRequest siteBookingRequest) {
		
		// validate request
		validateCreateBooking(siteBookingRequest);
		
		// enrich create request
		enrichCreateBooking(siteBookingRequest);
		
		// save create request
		producer.push("save-site-booking", siteBookingRequest);
//		siteBookingRequest.getSiteBookings().stream().forEach(booking -> {
//			repository.save(booking);
//		});
		
		// call workflow
		
		
		SiteBookingResponse siteBookingResponse = SiteBookingResponse.builder()
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(siteBookingRequest.getRequestInfo(), true))
				.siteBookings(siteBookingRequest.getSiteBookings()).build();
		return siteBookingResponse;
	}

	private void enrichCreateBooking(SiteBookingRequest siteBookingRequest) {

		siteBookingRequest.getSiteBookings().stream().forEach(booking -> {
			
			booking.setUuid(UUID.randomUUID().toString());
			booking.setApplicationNo(String.valueOf(System.currentTimeMillis()));
			booking.setIsActive(true);
			
			if(null != siteBookingRequest.getRequestInfo()
					&& null != siteBookingRequest.getRequestInfo().getUserInfo()) {
				AuditDetails auditDetails = AuditDetails.builder()
						.createdBy(siteBookingRequest.getRequestInfo().getUserInfo().getUuid())
						.createdDate(new Date().getTime())
						.build();
				booking.setAuditDetails(auditDetails);
			}
		});
		
	}

	private void validateCreateBooking(SiteBookingRequest siteBookingRequest) {
		
		// validate object
		if(CollectionUtils.isEmpty(siteBookingRequest.getSiteBookings())) {
			throw new CustomException("SITE_BOOKING_IS_EMPTY","Provide site booking payload.");
		}
		
		// validate attributes
		siteBookingRequest.getSiteBookings().stream().forEach(booking -> {
			if(StringUtils.isEmpty(booking.getApplicantName())
					|| StringUtils.isEmpty(booking.getMobileNumber())) {
				throw new CustomException("ATTRIBUTES_VALIDATION_FAILED","Provide mandatory inputs.");
			}
		});
		
		// validate site occupancy
		// need to be done:::::::::::::::::::::::::
		
	}
	
	

	public SiteBookingResponse searchBooking(SiteBookingSearchRequest siteBookingSearchRequest) {

		// enrich search criteria
		enrichSearchCriteria(siteBookingSearchRequest);
		
		// validate search criteria
		validateSearchCriteria(siteBookingSearchRequest);
		
		// search bookings
		List<SiteBooking> siteBookings = repository.search(siteBookingSearchRequest.getSiteBookingSearchCriteria());
		
		
		SiteBookingResponse siteBookingResponse = SiteBookingResponse.builder()
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(siteBookingSearchRequest.getRequestInfo(), true))
				.siteBookings(siteBookings).build();
		return siteBookingResponse;
	}
	

	private void validateSearchCriteria(SiteBookingSearchRequest siteBookingSearchRequest) {
		
		if((null != siteBookingSearchRequest.getRequestInfo()
				&& null != siteBookingSearchRequest.getRequestInfo().getUserInfo()
				&& !StringUtils.equalsIgnoreCase(siteBookingSearchRequest.getRequestInfo().getUserInfo().getType(), "CITIZEN"))
			&& (null != siteBookingSearchRequest.getSiteBookingSearchCriteria()
					&& CollectionUtils.isEmpty(siteBookingSearchRequest.getSiteBookingSearchCriteria().getApplicationNumbers())
					&& CollectionUtils.isEmpty(siteBookingSearchRequest.getSiteBookingSearchCriteria().getUuids())
					&& CollectionUtils.isEmpty(siteBookingSearchRequest.getSiteBookingSearchCriteria().getCreatedBy()))) {
			
			throw new CustomException("INVALID_SEARCH_PARAMETER", "Provide search criteria.");
			
			
		}
		
	}

	private void enrichSearchCriteria(SiteBookingSearchRequest siteBookingSearchRequest) {
		
		if(null != siteBookingSearchRequest.getSiteBookingSearchCriteria()
				&& null != siteBookingSearchRequest.getRequestInfo()
				&& null != siteBookingSearchRequest.getRequestInfo().getUserInfo()
				&& StringUtils.equalsIgnoreCase(siteBookingSearchRequest.getRequestInfo().getUserInfo().getType(), "CITIZEN")) {
			siteBookingSearchRequest.getSiteBookingSearchCriteria()
									.setCreatedBy(Arrays.asList(siteBookingSearchRequest.getRequestInfo()
																				.getUserInfo().getUuid()));
		}
		
	}


	
	public SiteBookingResponse updateBooking(SiteBookingRequest siteBookingRequest) {

		// validate update request
		validateSiteUpdateRequest(siteBookingRequest);
		
		// fetch existing
		Map<String, SiteBooking> siteBookingMap = searchSiteBookingFromRequest(siteBookingRequest);
		
		// enrich update request
		enrichUpdateSiteBooking(siteBookingRequest);
		
		// update request
		producer.push("update-site-booking", siteBookingRequest);
//		siteBookingRequest.getSiteBookings().stream().forEach(booking -> {
//			repository.update(booking);
//		});
		
		SiteBookingResponse siteBookingResponse = SiteBookingResponse.builder()
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(siteBookingRequest.getRequestInfo(), true))
				.siteBookings(siteBookingRequest.getSiteBookings()).build();
		return siteBookingResponse;
	}

	private void enrichUpdateSiteBooking(SiteBookingRequest siteBookingRequest) {
		
		siteBookingRequest.getSiteBookings().stream().forEach(booking -> {
			booking.getAuditDetails().setLastModifiedBy(siteBookingRequest.getRequestInfo().getUserInfo().getUuid());
			booking.getAuditDetails().setLastModifiedDate(new Date().getTime());
		});
		
	}

	private void validateSiteUpdateRequest(SiteBookingRequest siteBookingRequest) {

		if(CollectionUtils.isEmpty(siteBookingRequest.getSiteBookings())) {
			throw new CustomException("EMPTY_REQUEST","Provide bookings to update.");
		}
		
		siteBookingRequest.getSiteBookings().stream().forEach(booking -> {
			if(StringUtils.isEmpty(booking.getUuid())) {
				throw new CustomException("EMPTY_REQUEST","Provide bookings to update.");
			}
		});
		
	}

	private Map<String, SiteBooking> searchSiteBookingFromRequest(SiteBookingRequest siteBookingRequest) {
		
		SiteBookingSearchCriteria criteria = SiteBookingSearchCriteria.builder()
				.uuids(siteBookingRequest.getSiteBookings().stream().map(booking -> booking.getUuid()).collect(Collectors.toList()))
				.build();
		
		// search bookings
		List<SiteBooking> siteBookings = repository.search(criteria);
				
		return siteBookings.stream().collect(Collectors.toMap(SiteBooking::getUuid, booking->booking));
	}

}