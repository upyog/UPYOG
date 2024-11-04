package org.egov.advertisementcanopy.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.egov.advertisementcanopy.contract.bill.Bill;
import org.egov.advertisementcanopy.contract.bill.Bill.StatusEnum;
import org.egov.advertisementcanopy.contract.bill.BillResponse;
import org.egov.advertisementcanopy.contract.bill.BillSearchCriteria;
import org.egov.advertisementcanopy.contract.bill.Demand;
import org.egov.advertisementcanopy.contract.bill.GenerateBillCriteria;
import org.egov.advertisementcanopy.contract.workflow.BusinessServiceResponse;
import org.egov.advertisementcanopy.contract.workflow.State;
import org.egov.advertisementcanopy.model.AuditDetails;
import org.egov.advertisementcanopy.model.SiteBooking;
import org.egov.advertisementcanopy.model.SiteBookingActionRequest;
import org.egov.advertisementcanopy.model.SiteBookingActionResponse;
import org.egov.advertisementcanopy.model.SiteBookingDetail;
import org.egov.advertisementcanopy.model.SiteBookingRequest;
import org.egov.advertisementcanopy.model.SiteBookingResponse;
import org.egov.advertisementcanopy.model.SiteBookingSearchCriteria;
import org.egov.advertisementcanopy.model.SiteBookingSearchRequest;
import org.egov.advertisementcanopy.model.SiteCreationData;
import org.egov.advertisementcanopy.model.SiteSearchData;
import org.egov.advertisementcanopy.model.SiteSearchRequest;
import org.egov.advertisementcanopy.model.SiteUpdateRequest;
import org.egov.advertisementcanopy.producer.Producer;
import org.egov.advertisementcanopy.repository.SiteBookingRepository;
import org.egov.advertisementcanopy.repository.SiteRepository;
import org.egov.advertisementcanopy.util.AdvtConstants;
import org.egov.advertisementcanopy.util.ResponseInfoFactory;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
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

	@Autowired
	private SiteBookingRepository siteBookingRepository;

	@Autowired
	private SiteService siteService;

	public SiteBookingResponse createBooking(SiteBookingRequest siteBookingRequest) {
		
		// validate request
		validateCreateBooking(siteBookingRequest);

		// validate site occupancy
		Map<String, SiteCreationData> siteMap = searchValidateSiteAndEnrichSiteBooking(siteBookingRequest);
		
		// enrich create request
		enrichCreateBooking(siteBookingRequest);
		
		// save create request
		producer.push(AdvtConstants.SITE_BOOKING_CREATE_KAFKA_TOPIC, siteBookingRequest);
//		siteBookingRequest.getSiteBookings().stream().forEach(booking -> {
//			repository.save(booking);
//		});
		
		// call workflow
		workflowService.updateWorkflowStatus(siteBookingRequest);
		
		// update site status BOOKED
		updateSiteDataAfterBookingCreate(siteBookingRequest, siteMap);
		
		SiteBookingResponse siteBookingResponse = SiteBookingResponse.builder()
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(siteBookingRequest.getRequestInfo(), true))
				.siteBookings(siteBookingRequest.getSiteBookings()).build();
		return siteBookingResponse;
	}

	private void updateSiteDataAfterBookingCreate(SiteBookingRequest siteBookingRequest,
			Map<String, SiteCreationData> siteMap) {
		
		siteMap.entrySet().stream().forEach(entry -> {
			entry.getValue().setStatus(AdvtConstants.SITE_STATUS_BOOKED);
			if(null != entry.getValue().getAuditDetails()
					&& null != siteBookingRequest.getRequestInfo()
							&& null != siteBookingRequest.getRequestInfo().getUserInfo()) {
				entry.getValue().getAuditDetails().setLastModifiedBy(siteBookingRequest.getRequestInfo().getUserInfo().getUuid());
				entry.getValue().getAuditDetails().setLastModifiedDate(new Date().getTime());
			}
			SiteUpdateRequest siteUpdateRequest = SiteUpdateRequest.builder()
					.siteUpdationData(entry.getValue())
					.requestInfo(siteBookingRequest.getRequestInfo()).build();
			siteService.updateSiteData(siteUpdateRequest);
		});
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
								.isActive(true)
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
		else if(null != siteBookingSearchRequest.getSiteBookingSearchCriteria()
				&& null != siteBookingSearchRequest.getRequestInfo()
				&& null != siteBookingSearchRequest.getRequestInfo().getUserInfo()
				&& StringUtils.equalsIgnoreCase(siteBookingSearchRequest.getRequestInfo().getUserInfo().getType(), "EMPLOYEE")) {
			
			List<String> listOfStatus = getBookingStatusListByRoles(siteBookingSearchRequest.getSiteBookingSearchCriteria().getTenantId(), siteBookingSearchRequest.getRequestInfo().getUserInfo().getRoles());
			if(CollectionUtils.isEmpty(listOfStatus)) {
				throw new CustomException("SEARCH_BOOKING_BY_ROLES","Search can't be performed by this Employee due to lack of roles.");
			}
			siteBookingSearchRequest.getSiteBookingSearchCriteria().setStatus(listOfStatus);
		}
		
	}



	private List<String> getBookingStatusListByRoles(String tenantId, List<Role> roles) {
	
	List<String> rolesWithinTenant = advtConstants.getRolesByTenantId(tenantId, roles);	
	Set<String> statusWithRoles = new HashSet();
	
	rolesWithinTenant.stream().forEach(role -> {
		
		if(StringUtils.equalsIgnoreCase(role, AdvtConstants.ROLE_CODE_SITE_CREATOR)) {
			statusWithRoles.add(AdvtConstants.STATUS_PENDINGFORVERIFICATION);
		}else if(StringUtils.equalsIgnoreCase(role, AdvtConstants.ROLE_CODE_SITE_APPROVER)) {
			statusWithRoles.add(AdvtConstants.STATUS_PENDINGFORAPPROVAL);
		}
		
	});
	
	return new ArrayList<>(statusWithRoles);
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
				savedDemands = demandService.generateDemand(siteBookingRequest.getRequestInfo(), booking, AdvtConstants.BUSINESS_SERVICE_SITE_BOOKING);
	            

		        if(CollectionUtils.isEmpty(savedDemands)) {
		            throw new CustomException("INVALID_CONSUMERCODE","Bill not generated due to no Demand found for the given consumerCode");
		        }

				// fetch/create bill
	            GenerateBillCriteria billCriteria = GenerateBillCriteria.builder()
	            									.tenantId(booking.getTenantId())
	            									.businessService(AdvtConstants.BUSINESS_SERVICE_SITE_BOOKING)
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

	public SiteBookingActionResponse getApplicationDetails(SiteBookingActionRequest siteBookingActionRequest) {
		
		SiteBookingSearchCriteria criteria = SiteBookingSearchCriteria.builder().build();
		SiteBookingActionResponse garbageAccountActionResponse = SiteBookingActionResponse.builder()
				.applicationDetails(new ArrayList<>())
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(siteBookingActionRequest.getRequestInfo(), true))
				.build();

		if(CollectionUtils.isEmpty(siteBookingActionRequest.getApplicationNumbers())) {
			if(null != siteBookingActionRequest.getRequestInfo()
					&& null != siteBookingActionRequest.getRequestInfo().getUserInfo()
					&& !StringUtils.isEmpty(siteBookingActionRequest.getRequestInfo().getUserInfo().getUuid())) {
				criteria.setCreatedBy(Collections.singletonList(siteBookingActionRequest.getRequestInfo().getUserInfo().getUuid()));
			}else {
				throw new CustomException("INVALID REQUEST","Provide Application Number.");
			}
		}else {
			criteria.setApplicationNumbers(siteBookingActionRequest.getApplicationNumbers());
		}
		
		// search application number
		List<SiteBooking> accounts = siteBookingRepository.search(criteria);

		List<SiteBookingDetail> applicationDetails = getApplicationBillUserDetail(accounts, siteBookingActionRequest.getRequestInfo());
		
		garbageAccountActionResponse.setApplicationDetails(applicationDetails);
		
		return garbageAccountActionResponse;
	}

	private List<SiteBookingDetail> getApplicationBillUserDetail(List<SiteBooking> accounts, RequestInfo requestInfo) {
		
		List<SiteBookingDetail> sitegarbageAccountDetails = new ArrayList<>();
		
		accounts.stream().forEach(account -> {
			
			if(null == account.getSiteCreationData()
					|| null == account.getSiteCreationData().getSiteCost()
					|| null == account.getFromDate()
					|| null == account.getToDate()) {
				throw new CustomException("MISSING_VALUES_FOR_CALCULATE_FEE","Mendatory parameters are missing to calculate fees.");
			}
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			String fromDate = dateFormat.format(new Date(account.getFromDate()));
			String toDate = dateFormat.format(new Date(account.getToDate()));
			
			
			SiteBookingDetail siteBookingDetail = SiteBookingDetail.builder().applicationNumber(account.getApplicationNo()).build();
			Long numberOfDays = (account.getToDate() - account.getFromDate()) / (1000 * 60 * 60 * 24);
			
			// enrich formula
				siteBookingDetail.setFeeCalculationFormula("NEED to DECIDE.");
				siteBookingDetail.setFeeCalculationFormula("From Date: ("+fromDate+"), To Date: ("+toDate+"), Cost per day: "+account.getSiteCreationData().getSiteCost());
			
			
			// search bill Details
			BillSearchCriteria billSearchCriteria = BillSearchCriteria.builder()
					.tenantId(account.getTenantId())
					.consumerCode(Collections.singleton(account.getApplicationNo()))
					.service("ADVT")// business service
					.build();
			BillResponse billResponse = billService.searchBill(billSearchCriteria,requestInfo);
			Map<Object, Object> billDetailsMap = new HashMap<>();
			if (!CollectionUtils.isEmpty(billResponse.getBill())) {
				// enrich all bills
				siteBookingDetail.setBills(billResponse.getBill());
				Optional<Bill> activeBill = billResponse.getBill().stream()
						.filter(bill -> StatusEnum.ACTIVE.name().equalsIgnoreCase(bill.getStatus().name()))
			            .findFirst();
				activeBill.ifPresent(bill -> {
					// enrich active bill details
					billDetailsMap.put("billId", bill.getId());
					siteBookingDetail.setTotalPayableAmount(bill.getTotalAmount());
				});
					
			}else if(null != account.getSiteCreationData()) {
				// set payable amount
				BigDecimal totalPayableAmount = BigDecimal.valueOf(numberOfDays).multiply(new BigDecimal(account.getSiteCreationData().getSiteCost()));
				siteBookingDetail.setTotalPayableAmount(totalPayableAmount);
			}
			siteBookingDetail.setBillDetails(billDetailsMap);
			
			
			// enrich userDetails
			Map<Object, Object> userDetails = new HashMap<>();
			userDetails.put("UserName", account.getApplicantName());
			userDetails.put("MobileNo", account.getMobileNumber());
			userDetails.put("Email", account.getEmailId());
			userDetails.put("Address", "NEED to DECIDE");

			siteBookingDetail.setUserDetails(userDetails);
			
			
			
			
			sitegarbageAccountDetails.add(siteBookingDetail);
		});
		
		return sitegarbageAccountDetails;
	}

	public SiteBookingActionResponse getActionsOnApplication(SiteBookingActionRequest siteBookingActionRequest) {
		
		if(CollectionUtils.isEmpty(siteBookingActionRequest.getApplicationNumbers())
				|| StringUtils.isEmpty(siteBookingActionRequest.getBusinessService())) {
			throw new CustomException("INVALID REQUEST","Provide Application Number and Business Service.");
		}
		
		Map<String, List<String>> applicationActionMaps = new HashMap<>();
		String applicationTenantId = StringUtils.EMPTY;
		String applicationBusinessId = StringUtils.EMPTY;
		Map<String, SiteBooking> mapBookings = new HashMap<>();
		Map<String, SiteCreationData> mapSites = new HashMap<>();
		
		if(StringUtils.equalsIgnoreCase(siteBookingActionRequest.getBusinessService(), AdvtConstants.BUSINESS_SERVICE_SITE_CREATION)) {
			
			List<SiteCreationData> siteSearchDatas = new ArrayList<>();
			
			siteBookingActionRequest.getApplicationNumbers().stream().forEach(appNo -> {
				SiteSearchRequest searchSiteRequest = SiteSearchRequest.builder()
						.requestInfo(siteBookingActionRequest.getRequestInfo())
						.siteSearchData(SiteSearchData.builder()
										.siteID(siteBookingActionRequest.getApplicationNumbers().get(0))
										.build())
						.build();
				siteSearchDatas.add(siteRepository.searchSites(searchSiteRequest).get(0));
			});
			
			
			if(CollectionUtils.isEmpty(siteSearchDatas)) {
				throw new CustomException("SITE_APPLICATION_NOT_FOUND","No Site found with given application number.");
			}
			mapSites = siteSearchDatas.stream().collect(Collectors.toMap(site->site.getSiteID(), site->site));
			applicationTenantId = siteSearchDatas.get(0).getTenantId();
			applicationBusinessId = AdvtConstants.BUSINESS_SERVICE_SITE_CREATION;
			
		}else if(StringUtils.equalsIgnoreCase(siteBookingActionRequest.getBusinessService(), AdvtConstants.BUSINESS_SERVICE_SITE_BOOKING)) {

			// search site bookings by application numbers
			SiteBookingSearchCriteria criteria = SiteBookingSearchCriteria.builder()
					.applicationNumbers(siteBookingActionRequest.getApplicationNumbers())
					.build();
			List<SiteBooking> siteBookings = siteBookingRepository.search(criteria);
			if(CollectionUtils.isEmpty(siteBookings)) {	
				throw new CustomException("SITE_BOOKING_NOT_FOUND","No Site Bookings found with given application number.");
			}
			mapBookings = siteBookings.stream().collect(Collectors.toMap(acc->acc.getApplicationNo(), acc->acc));
			applicationTenantId = siteBookings.get(0).getTenantId();
			applicationBusinessId = AdvtConstants.BUSINESS_SERVICE_SITE_BOOKING;
			
		}
		
		
		// fetch business service search
		BusinessServiceResponse businessServiceResponse = workflowService.businessServiceSearch(siteBookingActionRequest, applicationTenantId,
				applicationBusinessId);
		
		if(null == businessServiceResponse || CollectionUtils.isEmpty(businessServiceResponse.getBusinessServices())) {
			throw new CustomException("NO_BUSINESS_SERVICE_FOUND","Business service not found for application numbers: "+siteBookingActionRequest.getApplicationNumbers().toString());
		}
		
		
		List<String> rolesWithinTenant = getRolesWithinTenant(applicationTenantId, siteBookingActionRequest.getRequestInfo().getUserInfo().getRoles(), siteBookingActionRequest.getBusinessService());
		
		
		for(int i=0; i<siteBookingActionRequest.getApplicationNumbers().size(); i++) {
			String applicationNumber = siteBookingActionRequest.getApplicationNumbers().get(i);
//		siteBookingActionRequest.getApplicationNumbers().stream().forEach(applicationNumber -> {
		
			final String status; // = StringUtils.EMPTY;
			if(StringUtils.equalsIgnoreCase(siteBookingActionRequest.getBusinessService(), AdvtConstants.BUSINESS_SERVICE_SITE_CREATION)) {
				status = mapSites.get(applicationNumber).getWorkFlowStatus();
			}else if(StringUtils.equalsIgnoreCase(siteBookingActionRequest.getBusinessService(), AdvtConstants.BUSINESS_SERVICE_SITE_BOOKING)) {
				status = mapBookings.get(applicationNumber).getStatus();
			}else {
				throw new CustomException("UNKNOWN_BUSINESS_SERVICE","Provide the correct business service id.");
			}
			
			List<State> stateList = businessServiceResponse.getBusinessServices().get(0).getStates().stream()
					.filter(state -> StringUtils.equalsIgnoreCase(state.getApplicationStatus(), status)
										&& !StringUtils.equalsAnyIgnoreCase(state.getApplicationStatus(), AdvtConstants.STATUS_APPROVED)
										).collect(Collectors.toList());
			
			// filtering actions based on roles
			List<String> actions = new ArrayList<>();
			stateList.stream().forEach(state -> {
				state.getActions().stream()
				.filter(action -> action.getRoles().stream().anyMatch(role -> rolesWithinTenant.contains(role)))
				.forEach(action -> {
					actions.add(action.getAction());
				});
			}) ;
			
			
			applicationActionMaps.put(applicationNumber, actions);
//		});
		}
		
		List<SiteBookingDetail> siteBookingDetailList = new ArrayList<>();
		applicationActionMaps.entrySet().stream().forEach(entry -> {
			siteBookingDetailList.add(SiteBookingDetail.builder().applicationNumber(entry.getKey()).action(entry.getValue()).build());
		});
		
		// build response
		SiteBookingActionResponse garbageAccountActionResponse = SiteBookingActionResponse.builder()
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(siteBookingActionRequest.getRequestInfo(), true))
				.applicationDetails(siteBookingDetailList).build();
		return garbageAccountActionResponse;
	
	}

	private List<String> getRolesWithinTenant(String tenantId, List<Role> roles, String businessService) {

		List<String> roleCodes = new ArrayList<>();
		
		if (StringUtils.equalsAnyIgnoreCase(businessService, AdvtConstants.BUSINESS_SERVICE_SITE_BOOKING)) {
			
			for (Role role : roles) {
				if (StringUtils.equalsIgnoreCase(role.getCode(), AdvtConstants.ROLE_CODE_CITIZEN)) {
	                roleCodes.add(AdvtConstants.ROLE_CODE_CITIZEN);
	            }
	            if (StringUtils.equalsIgnoreCase(role.getCode(), AdvtConstants.ROLE_CODE_SITE_CREATOR)) {
	                roleCodes.add(AdvtConstants.ROLE_CODE_SITE_CREATOR);
	            }
	            if (StringUtils.equalsIgnoreCase(role.getCode(), AdvtConstants.ROLE_CODE_SITE_APPROVER)) {
	                roleCodes.add(AdvtConstants.ROLE_CODE_SITE_APPROVER);
	            }
	        }
			
		}else if(StringUtils.equalsAnyIgnoreCase(businessService, AdvtConstants.BUSINESS_SERVICE_SITE_CREATION)){
			
			for (Role role : roles) {
	            if (StringUtils.equalsIgnoreCase(role.getCode(), AdvtConstants.ROLE_CODE_CITIZEN)) {
	                roleCodes.add(AdvtConstants.ROLE_CODE_CITIZEN);
	            }
	            if (StringUtils.equalsIgnoreCase(role.getCode(), AdvtConstants.ROLE_CODE_SITE_CREATOR)) {
	                roleCodes.add(AdvtConstants.ROLE_CODE_SITE_WF_CREATOR);
	            }
	            if (StringUtils.equalsIgnoreCase(role.getCode(), AdvtConstants.ROLE_CODE_SITE_APPROVER)) {
	                roleCodes.add(AdvtConstants.ROLE_CODE_SITE_WF_APPROVER);
	            }
	        }
			
//			roleCodes = roles.stream().filter(role -> StringUtils.equalsIgnoreCase(role.getTenantId(), tenantId)).map(role -> role.getCode())
//					.collect(Collectors.toList());
		}
		
		return roleCodes;
	}

}