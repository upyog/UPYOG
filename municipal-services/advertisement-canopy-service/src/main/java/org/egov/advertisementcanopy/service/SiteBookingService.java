package org.egov.advertisementcanopy.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.egov.advertisementcanopy.contract.bill.BillResponse;
import org.egov.advertisementcanopy.contract.bill.Demand;
import org.egov.advertisementcanopy.contract.bill.GenerateBillCriteria;
import org.egov.advertisementcanopy.model.AuditDetails;
import org.egov.advertisementcanopy.model.SiteBooking;
import org.egov.advertisementcanopy.model.SiteBookingRequest;
import org.egov.advertisementcanopy.model.SiteBookingResponse;
import org.egov.advertisementcanopy.model.SiteBookingSearchCriteria;
import org.egov.advertisementcanopy.model.SiteBookingSearchRequest;
import org.egov.advertisementcanopy.model.SiteCreationData;
import org.egov.advertisementcanopy.model.SiteSearchData;
import org.egov.advertisementcanopy.model.SiteSearchRequest;
import org.egov.advertisementcanopy.producer.Producer;
import org.egov.advertisementcanopy.repository.SiteBookingRepository;
import org.egov.advertisementcanopy.repository.SiteRepository;
import org.egov.advertisementcanopy.util.AdvtConstants;
import org.egov.advertisementcanopy.util.ResponseInfoFactory;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SiteBookingService {

	@Autowired
	private BillService billService;

	@Autowired
	private DemandService demandService;

	@Autowired
	private SiteBookingRepository repository;

	@Autowired
	private Producer producer;

	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@Autowired
	private SiteRepository siteRepository;

	@Autowired
	private WorkflowService workflowService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private AdvtConstants advtConstants;

	public SiteBookingResponse createBooking(SiteBookingRequest siteBookingRequest) {
		
		// validate request
		validateCreateBooking(siteBookingRequest);

		// validate site occupancy
		searchValidateSiteAndEnrichSiteBooking(siteBookingRequest);
		
		// enrich create request
		enrichCreateBooking(siteBookingRequest);
		
		// save create request
		producer.push(AdvtConstants.SITE_BOOKING_CREATE_KAFKA_TOPIC, siteBookingRequest);
//		siteBookingRequest.getSiteBookings().stream().forEach(booking -> {
//			repository.save(booking);
//		});
		
		// call workflow
		workflowService.updateWorkflowStatus(siteBookingRequest);
		
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
			booking.setStatus("INITIATED");
			
			booking.setWorkflowAction("INITIATE");
//			booking.setTenantId("hp.Shimla");
			
			if(null != booking.getFromDate() && null != booking.getToDate()) {
				booking.setPeriodInDays(TimeUnit.MILLISECONDS.toDays(booking.getToDate()-booking.getFromDate()));
//				booking.setPeriodInDays(Math.toIntExact( booking.getToDate()-booking.getFromDate() / (1000 * 60 * 60 * 24) ));
			}
			
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
					|| StringUtils.isEmpty(booking.getMobileNumber())
					|| null == booking.getToDate()
					|| null == booking.getFromDate()) {
				throw new CustomException("ATTRIBUTES_VALIDATION_FAILED","Provide mandatory inputs.");
			}
		});
//		
//		// validate site occupancy
//		validateSiteWhileBooking(siteBookingRequest);
		
		
	}

	private Map<String, SiteCreationData> searchValidateSiteAndEnrichSiteBooking(SiteBookingRequest siteBookingRequest) {
		
		List<String> siteUuids = siteBookingRequest.getSiteBookings().stream()
//				.filter(siteBooking -> BooleanUtils.isFalse(siteBooking.getIsOnlyWorkflowCall()))
				.map(booking -> booking.getSiteUuid()).collect(Collectors.toList());
		SiteSearchRequest searchSiteRequest = SiteSearchRequest.builder()
				.requestInfo(siteBookingRequest.getRequestInfo())
				.siteSearchData(SiteSearchData.builder()
								.uuids(siteUuids)
								.build())
				.build();
		List<SiteCreationData> siteSearchDatas = siteRepository.searchSites(searchSiteRequest);
		Map<String, SiteCreationData> siteMap = siteSearchDatas.stream().collect(Collectors.toMap(SiteCreationData::getUuid, site->site));
		
		siteBookingRequest.getSiteBookings().stream()
//		.filter(siteBooking -> BooleanUtils.isFalse(siteBooking.getIsOnlyWorkflowCall()))
		.forEach(booking -> {
			SiteCreationData tempSite = siteMap.get(booking.getSiteUuid());
			// validate site
			if(null == tempSite) {
				throw new CustomException("SITE_NOT_FOUND","Site not found for given site uuid: "+booking.getSiteUuid());
			}
			if(!StringUtils.equalsIgnoreCase(tempSite.getStatus(), "AVAILABLE")) {
				throw new CustomException("SITE_CANT_BE_BOOKED","Site "+ tempSite.getSiteName() +" is not Available.");
			}
			// enrich tenant from site to booking
			booking.setTenantId(tempSite.getTenantId());
		});
		return siteMap;
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
		
		// process search response
		processSiteBookingResoponse(siteBookingResponse);
		
		return siteBookingResponse;
	}
	

	private void processSiteBookingResoponse(SiteBookingResponse siteBookingResponse) {
		
		// categorize each bookings
		if (!CollectionUtils.isEmpty(siteBookingResponse.getSiteBookings())
				) {
			siteBookingResponse.setCount(siteBookingResponse.getSiteBookings().size());
			siteBookingResponse.setApplicationInitiated((int) siteBookingResponse.getSiteBookings().stream()
					.filter(license -> StringUtils.equalsIgnoreCase(AdvtConstants.STATUS_INITIATED, license.getStatus())).count());
			siteBookingResponse.setApplicationApplied((int) siteBookingResponse.getSiteBookings().stream()
					.filter(license -> StringUtils.equalsAnyIgnoreCase(license.getStatus()
							, AdvtConstants.STATUS_PENDINGFORVERIFICATION
							, AdvtConstants.STATUS_PENDINGFORAPPROVAL
							, AdvtConstants.STATUS_PENDINGFORMODIFICATION)).count());
			siteBookingResponse.setApplicationPendingForPayment((int) siteBookingResponse.getSiteBookings().stream()
					.filter(license -> StringUtils.equalsIgnoreCase(AdvtConstants.STATUS_PENDINGFORPAYMENT, license.getStatus())).count());
			siteBookingResponse.setApplicationApproved((int) siteBookingResponse.getSiteBookings().stream()
					.filter(license -> StringUtils.equalsIgnoreCase(AdvtConstants.STATUS_APPROVED, license.getStatus()))
					.count());
		}
		
		
	}

	private void validateSearchCriteria(SiteBookingSearchRequest siteBookingSearchRequest) {
		
		if(null == siteBookingSearchRequest.getSiteBookingSearchCriteria()) {
			throw new CustomException("NULL_SEARCH_CTITERIA","Provide Search Criteria.");
		}
		if((null != siteBookingSearchRequest.getRequestInfo()
				&& null != siteBookingSearchRequest.getRequestInfo().getUserInfo()
				&& !StringUtils.equalsIgnoreCase(siteBookingSearchRequest.getRequestInfo().getUserInfo().getType(), "CITIZEN"))
			&& (null != siteBookingSearchRequest.getSiteBookingSearchCriteria()
					&& CollectionUtils.isEmpty(siteBookingSearchRequest.getSiteBookingSearchCriteria().getApplicationNumbers())
					&& CollectionUtils.isEmpty(siteBookingSearchRequest.getSiteBookingSearchCriteria().getUuids())
					&& CollectionUtils.isEmpty(siteBookingSearchRequest.getSiteBookingSearchCriteria().getCreatedBy())
					&& StringUtils.isEmpty(siteBookingSearchRequest.getSiteBookingSearchCriteria().getTenantId()))) {
			
			throw new CustomException("INVALID_SEARCH_PARAMETER", "Provide search criteria.");
			
			
		}
		
	}

	private void enrichSearchCriteria(SiteBookingSearchRequest siteBookingSearchRequest) {
		
		if(null == siteBookingSearchRequest.getSiteBookingSearchCriteria()
				&& StringUtils.equalsIgnoreCase(siteBookingSearchRequest.getRequestInfo().getUserInfo().getType(), "CITIZEN")) {
			siteBookingSearchRequest
					.setSiteBookingSearchCriteria(SiteBookingSearchCriteria.builder()
							.createdBy(Collections
									.singletonList(siteBookingSearchRequest.getRequestInfo().getUserInfo().getUuid()))
							.build());
		}
		else if(null != siteBookingSearchRequest.getSiteBookingSearchCriteria()
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
		Map<String, SiteBooking> appNoToSiteBookingMap = searchSiteBookingFromRequest(siteBookingRequest);
		
		// enrich update request
		siteBookingRequest = validateAndEnrichUpdateSiteBooking(siteBookingRequest, appNoToSiteBookingMap);
		
		// update request
		producer.push(AdvtConstants.SITE_BOOKING_UPDATE_KAFKA_TOPIC, siteBookingRequest);
//		siteBookingRequest.getSiteBookings().stream().forEach(booking -> {
//			repository.update(booking);
//		});

		// call workflow
		workflowService.updateWorkflowStatus(siteBookingRequest);

		// generate demand and fetch bill
		generateDemandAndBill(siteBookingRequest);
		
		
		SiteBookingResponse siteBookingResponse = SiteBookingResponse.builder()
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(siteBookingRequest.getRequestInfo(), true))
				.siteBookings(siteBookingRequest.getSiteBookings()).build();
		return siteBookingResponse;
	}

	private void generateDemandAndBill(SiteBookingRequest siteBookingRequest) {
		siteBookingRequest.getSiteBookings().stream().forEach(booking -> {
			
			if(StringUtils.equalsIgnoreCase(AdvtConstants.ACTION_RETURN_TO_INITIATOR_FOR_PAYMENT, booking.getWorkflowAction())) {
				
				List<Demand> savedDemands = new ArrayList<>();
            	// generate demand
				savedDemands = demandService.generateDemand(siteBookingRequest.getRequestInfo(), booking, AdvtConstants.BUSINESS_SERVICE);
	            

		        if(CollectionUtils.isEmpty(savedDemands)) {
		            throw new CustomException("INVALID_CONSUMERCODE","Bill not generated due to no Demand found for the given consumerCode");
		        }

				// fetch/create bill
	            GenerateBillCriteria billCriteria = GenerateBillCriteria.builder()
	            									.tenantId(booking.getTenantId())
	            									.businessService(AdvtConstants.BUSINESS_SERVICE)
	            									.consumerCode(booking.getApplicationNo()).build();
	            BillResponse billResponse = billService.generateBill(siteBookingRequest.getRequestInfo(),billCriteria);
	            
			}
		});
	}

	private SiteBookingRequest validateAndEnrichUpdateSiteBooking(SiteBookingRequest siteBookingRequest, Map<String, SiteBooking> appNoToSiteBookingMap) {

		SiteBookingRequest siteBookingRequestTemp = SiteBookingRequest.builder()
				.requestInfo(siteBookingRequest.getRequestInfo()).siteBookings(new ArrayList<>()).build();

		siteBookingRequest.getSiteBookings().stream().forEach(booking -> {
			SiteBooking existingSiteBooking = appNoToSiteBookingMap.get(booking.getApplicationNo());
			
			// validate existence
			if(null == existingSiteBooking) {
				throw new CustomException("BOOKING_NOT_FOUND","Booking id not found: "+booking.getApplicationNo());
			}
			
			if(!booking.getIsOnlyWorkflowCall()) {
				// enrich some existing data in request
				booking.setApplicationNo(existingSiteBooking.getApplicationNo());
				booking.setStatus(existingSiteBooking.getStatus());
				booking.setAuditDetails(existingSiteBooking.getAuditDetails());
				booking.getAuditDetails().setLastModifiedBy(siteBookingRequest.getRequestInfo().getUserInfo().getUuid());
				booking.getAuditDetails().setLastModifiedDate(new Date().getTime());

				siteBookingRequestTemp.getSiteBookings().add(booking);
			
			}else {
				// enrich all existing data in request except WF attributes
				Boolean isWfCall = booking.getIsOnlyWorkflowCall();
				String tempApplicationNo = booking.getApplicationNo();
				String action = booking.getWorkflowAction();
				String status = advtConstants.getStatusOrAction(action, true);
				String comments = booking.getComments();
				
				SiteBooking bookingTemp = objectMapper.convertValue(appNoToSiteBookingMap.get(booking.getApplicationNo()), SiteBooking.class);
				
				if(null == bookingTemp) {
					throw new CustomException("FAILED_SEARCH_SITE_BOOKING","Site Booking not found for workflow call.");
				}
				
				bookingTemp.setIsOnlyWorkflowCall(isWfCall);
				bookingTemp.setApplicationNo(tempApplicationNo);
				bookingTemp.setWorkflowAction(action);
				bookingTemp.setComments(comments);
				bookingTemp.setStatus(status);
				
				siteBookingRequestTemp.getSiteBookings().add(bookingTemp);
			
			}
			
		});
		return siteBookingRequestTemp;
	}

	private void validateSiteUpdateRequest(SiteBookingRequest siteBookingRequest) {

		SiteBookingRequest activeSiteBookingRequest = SiteBookingRequest.builder().requestInfo(siteBookingRequest.getRequestInfo()).build();
		
		// validate bookings present or not
		if(CollectionUtils.isEmpty(siteBookingRequest.getSiteBookings())) {
			throw new CustomException("EMPTY_REQUEST","Provide bookings to update.");
		}
		
		// validate booking uuid
		siteBookingRequest.getSiteBookings().stream()
	    .filter(booking -> BooleanUtils.isFalse(booking.getIsOnlyWorkflowCall()))
	    .forEach(booking -> {
	        if (StringUtils.isEmpty(booking.getUuid())) {
	            throw new CustomException("EMPTY_REQUEST", "Provide bookings uuid to update.");
	        }
	    });
		

		// validate SITE availability if booking is active
		activeSiteBookingRequest.setSiteBookings(siteBookingRequest
				.getSiteBookings().stream().filter(site -> BooleanUtils.isTrue(site.getIsActive()) && BooleanUtils.isFalse(site.getIsOnlyWorkflowCall())).collect(Collectors.toList()));
		
		if (!CollectionUtils.isEmpty(activeSiteBookingRequest.getSiteBookings())) {
			searchValidateSiteAndEnrichSiteBooking(activeSiteBookingRequest);
		}

	}

	private Map<String, SiteBooking> searchSiteBookingFromRequest(SiteBookingRequest siteBookingRequest) {
		
		SiteBookingSearchCriteria criteria = SiteBookingSearchCriteria.builder()
				.applicationNumbers(siteBookingRequest.getSiteBookings().stream().map(booking -> booking.getApplicationNo()).collect(Collectors.toList()))
				.build();
		
		// search bookings
		List<SiteBooking> siteBookings = repository.search(criteria);
				
		return siteBookings.stream().collect(Collectors.toMap(SiteBooking::getApplicationNo, booking->booking));
	}

}