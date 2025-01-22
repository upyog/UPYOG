package org.upyog.chb.service.impl;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.constants.WorkflowStatus;
import org.upyog.chb.enums.BookingStatusEnum;
import org.upyog.chb.repository.CommunityHallBookingRepository;
import org.upyog.chb.repository.ServiceRequestRepository;
import org.upyog.chb.service.BillingService;
import org.upyog.chb.service.CHBEncryptionService;
import org.upyog.chb.service.CommunityHallBookingService;
import org.upyog.chb.service.DemandService;
import org.upyog.chb.service.EnrichmentService;
import org.upyog.chb.service.WorkflowService;
import org.upyog.chb.util.CommunityHallBookingUtil;
import org.upyog.chb.util.MdmsUtil;
import org.upyog.chb.validator.CommunityHallBookingValidator;
import org.upyog.chb.web.models.ApplicationDetail;
import org.upyog.chb.web.models.AssetDTO;
import org.upyog.chb.web.models.AssetResponse;
import org.upyog.chb.web.models.AssetSearchCriteria;
import org.upyog.chb.web.models.CommunityHallBookingActionRequest;
import org.upyog.chb.web.models.CommunityHallBookingActionResponse;
import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.CommunityHallBookingRequest;
import org.upyog.chb.web.models.CommunityHallBookingSearchCriteria;
import org.upyog.chb.web.models.CommunityHallBookingUpdateStatusRequest;
import org.upyog.chb.web.models.CommunityHallSlotAvailabilityDetail;
import org.upyog.chb.web.models.CommunityHallSlotSearchCriteria;
import org.upyog.chb.web.models.RequestInfoWrapper;
import org.upyog.chb.web.models.collection.Bill;
import org.upyog.chb.web.models.collection.BillResponse;
import org.upyog.chb.web.models.collection.BillSearchCriteria;
import org.upyog.chb.web.models.collection.GenerateBillCriteria;

import com.fasterxml.jackson.databind.ObjectMapper;

import digit.models.coremodels.PaymentDetail;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommunityHallBookingServiceImpl implements CommunityHallBookingService {

	@Autowired
	private CommunityHallBookingRepository bookingRepository;
	@Autowired
	private CommunityHallBookingValidator hallBookingValidator;

	/*
	 * @Autowired private WorkflowService workflowService;
	 */

	@Autowired
	private EnrichmentService enrichmentService;

	@Autowired
	private DemandService demandService;
	
	@Autowired
	 private WorkflowService workflowService;

	@Autowired
	private BillingService billingService;
	
	@Autowired
	private MdmsUtil mdmsUtil;
	
	@Autowired
	private CHBEncryptionService encryptionService;

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private ObjectMapper mapper;

	
	@Autowired
	private CommunityHallBookingConfiguration config;

	
	@Autowired
	private ServiceRequestRepository serviceRequestRepository;
	
	
	@Override
	public CommunityHallBookingDetail createBooking(@Valid CommunityHallBookingRequest communityHallsBookingRequest) {
		log.info("Create community hall booking for user : "
				+ communityHallsBookingRequest.getRequestInfo().getUserInfo().getUuid());
		// TODO move to util calss 
		String tenantId = communityHallsBookingRequest.getHallsBookingApplication().getTenantId().split("\\.")[0];
		if (communityHallsBookingRequest.getHallsBookingApplication().getTenantId().split("\\.").length == 1) {
			throw new CustomException(CommunityHallBookingConstants.INVALID_TENANT,
					"Please provide valid tenant id for booking creation");
		}

		Object mdmsData = mdmsUtil.mDMSCall(communityHallsBookingRequest.getRequestInfo(), tenantId);

		// 1. Validate request master data to confirm it has only valid data in records
		hallBookingValidator.validateCreate(communityHallsBookingRequest, mdmsData);
		// 2. Add fields that has custom logic like booking no, ids using UUID
		enrichmentService.enrichCreateBookingRequest(communityHallsBookingRequest);
		
		//ENcrypt PII data of applicant
		encryptionService.encryptObject(communityHallsBookingRequest);

		/**
		 * Workflow will come into picture once hall location changes or booking is
		 * cancelled otherwise after payment booking will be auto approved
		 * 
		 */

		// 3.Update workflow of the application
		 workflowService.updateWorkflow(communityHallsBookingRequest);

		demandService.createDemand(communityHallsBookingRequest, mdmsData, true);

		
		// fetch/create bill
        GenerateBillCriteria billCriteria = GenerateBillCriteria.builder()
        									.tenantId(communityHallsBookingRequest.getHallsBookingApplication().getTenantId())
        									.businessService("chb-services")
        									.consumerCode(communityHallsBookingRequest.getHallsBookingApplication().getBookingNo()).build();
       billingService.generateBill(communityHallsBookingRequest.getRequestInfo(),billCriteria);
        
		// 4.Persist the request using persister service
		bookingRepository.saveCommunityHallBooking(communityHallsBookingRequest);

		return communityHallsBookingRequest.getHallsBookingApplication();
	}
	
	@Override
	public CommunityHallBookingDetail createInitBooking(
			@Valid CommunityHallBookingRequest communityHallsBookingRequest) {
		log.info("Create community hall temp booking for user : "
				+ communityHallsBookingRequest.getRequestInfo().getUserInfo().getUuid());
		bookingRepository.saveCommunityHallBookingInit(communityHallsBookingRequest);
		return null;
	}

	@Override
	public List<CommunityHallBookingDetail> getBookingDetails(CommunityHallBookingSearchCriteria bookingSearchCriteria,
			RequestInfo info) {
		hallBookingValidator.validateSearch(info, bookingSearchCriteria);
		List<CommunityHallBookingDetail> bookingDetails = new ArrayList<CommunityHallBookingDetail>();
		List<String> roles = new ArrayList<>();
		for (Role role : info.getUserInfo().getRoles()) {
			roles.add(role.getCode());
		}
		log.info("user roles for searching : " + roles);
		if ((bookingSearchCriteria.tenantIdOnly() || bookingSearchCriteria.isEmpty())
				&& roles.contains(CommunityHallBookingConstants.CITIZEN)) {
			log.debug("loading data of created and by me");
			bookingDetails = this.getBookingCreatedByMe(bookingSearchCriteria, info);
		} else {
			bookingDetails = bookingRepository.getBookingDetails(bookingSearchCriteria);
		}
		
		bookingDetails = encryptionService.decryptObject(bookingDetails, info);

		return bookingDetails;
	}

	private List<CommunityHallBookingDetail> getBookingCreatedByMe(CommunityHallBookingSearchCriteria criteria,
			RequestInfo requestInfo) {
		List<CommunityHallBookingDetail> bookingDetails = new ArrayList<CommunityHallBookingDetail>();

		List<String> uuids = new ArrayList<>();
		if (requestInfo.getUserInfo() != null && !StringUtils.isEmpty(requestInfo.getUserInfo().getUuid())) {
			uuids.add(requestInfo.getUserInfo().getUuid());
			criteria.setCreatedBy(uuids);
		}
		log.debug("loading data of created and by me" + uuids.toString());
		bookingDetails = bookingRepository.getBookingDetails(criteria);
		return bookingDetails;
	}

	@Override
	public CommunityHallBookingDetail updateBooking(CommunityHallBookingRequest communityHallsBookingRequest,
			PaymentDetail paymentDetail, BookingStatusEnum status) {
		String bookingNo = communityHallsBookingRequest.getHallsBookingApplication().getBookingNo();
		log.info("Updating booking for booking no : " + bookingNo);
		if (bookingNo == null) {
			return null;
		}
		if (status != BookingStatusEnum.BOOKED) {
		CommunityHallBookingSearchCriteria bookingSearchCriteria = CommunityHallBookingSearchCriteria.builder()
				.bookingNo(bookingNo).build();
		List<CommunityHallBookingDetail> bookingDetails = bookingRepository.getBookingDetails(bookingSearchCriteria);
		if (bookingDetails.size() == 0) {
			throw new CustomException("INVALID_BOOKING_CODE",
					"Booking no not valid. Failed to update booking status for : " + bookingNo);
		}

		convertBookingRequest(communityHallsBookingRequest, bookingDetails.get(0));
		
		}

		enrichmentService.enrichUpdateBookingRequest(communityHallsBookingRequest, status);
		
		workflowService.updateWorkflow(communityHallsBookingRequest);
		//Update payment date and receipt no on successful payment when payment detail object is received
		if (paymentDetail != null) {
			communityHallsBookingRequest.getHallsBookingApplication().setReceiptNo(paymentDetail.getReceiptNumber());
			communityHallsBookingRequest.getHallsBookingApplication().setPaymentDate(paymentDetail.getReceiptDate());
		}
		bookingRepository.updateBooking(communityHallsBookingRequest);
		log.info("fetched booking detail and updated status "
				+ communityHallsBookingRequest.getHallsBookingApplication().getBookingStatus());
		return communityHallsBookingRequest.getHallsBookingApplication();
	}

	private void convertBookingRequest(CommunityHallBookingRequest communityHallsBookingRequest,
			CommunityHallBookingDetail bookingDetailDB) {
		CommunityHallBookingDetail bookingDetailRequest = communityHallsBookingRequest.getHallsBookingApplication();
		if (bookingDetailDB.getPermissionLetterFilestoreId() == null
				&& bookingDetailRequest.getPermissionLetterFilestoreId() != null) {
			bookingDetailDB.setPermissionLetterFilestoreId(bookingDetailRequest.getPermissionLetterFilestoreId());
		}

		if (bookingDetailDB.getPaymentReceiptFilestoreId() == null
				&& bookingDetailRequest.getPaymentReceiptFilestoreId() != null) {
			bookingDetailDB.setPaymentReceiptFilestoreId(bookingDetailRequest.getPaymentReceiptFilestoreId());
		}
		communityHallsBookingRequest.setHallsBookingApplication(bookingDetailDB);
	}

	@Override
	public List<CommunityHallSlotAvailabilityDetail> getCommunityHallSlotAvailability(
			CommunityHallSlotSearchCriteria criteria) {
		if (criteria.getCommunityHallCode() == null || CollectionUtils.isEmpty(criteria.getHallCodes())) {

		}

		List<CommunityHallSlotAvailabilityDetail> availabiltityDetails = bookingRepository
				.getCommunityHallSlotAvailability(criteria);
		log.info("Availabiltity details fetched from DB :" + availabiltityDetails);
		
		List<CommunityHallSlotAvailabilityDetail> availabiltityDetailsResponse = convertToCommunityHallAvailabilityResponse(
				criteria, availabiltityDetails);

		log.info("Availabiltity details response after updating status :" + availabiltityDetailsResponse);
		return availabiltityDetailsResponse;
	}

	/**
	 * 
	 * @param criteria
	 * @param availabiltityDetails
	 * @return
	 */
	private List<CommunityHallSlotAvailabilityDetail> convertToCommunityHallAvailabilityResponse(
			CommunityHallSlotSearchCriteria criteria, List<CommunityHallSlotAvailabilityDetail> availabiltityDetails) {

		List<CommunityHallSlotAvailabilityDetail> availabiltityDetailsResponse = new ArrayList<CommunityHallSlotAvailabilityDetail>();
		LocalDate startDate = CommunityHallBookingUtil.parseStringToLocalDate(criteria.getBookingStartDate());

		LocalDate endDate = CommunityHallBookingUtil.parseStringToLocalDate(criteria.getBookingEndDate());

		List<LocalDate> totalDates = new ArrayList<>();
		//Calculating list of dates for booking
		while (!startDate.isAfter(endDate)) {
			totalDates.add(startDate);
			startDate = startDate.plusDays(1);
		}

		totalDates.stream().forEach(date -> {
			List<String> hallCodes = new ArrayList<>();
			if (StringUtils.isNotBlank(criteria.getHallCode())) {
				hallCodes.add(criteria.getHallCode());
			} else {
				hallCodes.addAll(criteria.getHallCodes());
			}
			hallCodes.stream().forEach(data -> {
				availabiltityDetailsResponse.add(createCommunityHallSlotAvailabiltityDetail(criteria, date, data));
			});
		});

		//Setting hall status to booked if it is already booked by checking in the database entry
		availabiltityDetailsResponse.stream().forEach(detail -> {
			if (availabiltityDetails.contains(detail)) {
				detail.setSlotStaus(BookingStatusEnum.BOOKED.toString());
			}
		});

		return availabiltityDetailsResponse;
	}

	private CommunityHallSlotAvailabilityDetail createCommunityHallSlotAvailabiltityDetail(
			CommunityHallSlotSearchCriteria criteria, LocalDate date, String hallCode) {
		CommunityHallSlotAvailabilityDetail availabiltityDetail = CommunityHallSlotAvailabilityDetail.builder()
				.communityHallCode(criteria.getCommunityHallCode()).hallCode(hallCode)
			//Setting slot status available for every hall and hall code
				.slotStaus(BookingStatusEnum.AVAILABLE.toString()).tenantId(criteria.getTenantId())
				.bookingDate(CommunityHallBookingUtil.parseLocalDateToString(date)).build();
		return availabiltityDetail;
	}


	public CommunityHallBookingActionResponse getApplicationDetails(
			CommunityHallBookingActionRequest communityHallActionRequest) {
		
		if(CollectionUtils.isEmpty(communityHallActionRequest.getApplicationNumbers())) {
			throw new CustomException("INVALID REQUEST","Provide Application Number.");
		}
		
		CommunityHallBookingActionResponse communityHallActionResponse = CommunityHallBookingActionResponse.builder()
																.applicationDetails(new ArrayList<>())
																.build();
		communityHallActionRequest.getApplicationNumbers().stream().forEach(applicationNumber -> {
			
			// search application number
			CommunityHallBookingSearchCriteria criteria = CommunityHallBookingSearchCriteria.builder()
					.bookingNo(applicationNumber)
					.build();
			List<CommunityHallBookingDetail> petApplications = bookingRepository.getBookingDetails(criteria);
			
			CommunityHallBookingDetail communityHallApplication = null != petApplications ? petApplications.get(0): null;	
			communityHallApplication = encryptionService.decryptObject(communityHallApplication, communityHallActionRequest.getRequestInfo());
			 ApplicationDetail applicationDetail = getApplicationBillUserDetail(communityHallApplication, communityHallActionRequest.getRequestInfo());
		 	communityHallActionResponse.getApplicationDetails().add(applicationDetail);
		});
		
		return communityHallActionResponse;
	}
	
	private ApplicationDetail getApplicationBillUserDetail(CommunityHallBookingDetail communityHallApplication,
			RequestInfo requestInfo) {
	
		ApplicationDetail applicationDetail = ApplicationDetail.builder()
				.applicationNumber(communityHallApplication.getBookingNo())
				.build();

		// formula
		StringBuilder feeCalculationFormula = new StringBuilder("Formula: ");
		
		applicationDetail.setFeeCalculationFormula(feeCalculationFormula.toString());
		

		// search bill Details
		BillSearchCriteria billSearchCriteria = BillSearchCriteria.builder()
				.tenantId(communityHallApplication.getTenantId())
				.consumerCode(Collections.singleton(applicationDetail.getApplicationNumber()))
				.service("chb-services")
				.build();
		List<Bill> bills = billingService.searchBill(billSearchCriteria,requestInfo);
		Map<Object, Object> billDetailsMap = new HashMap<>();
		if (!CollectionUtils.isEmpty(bills)) {
			billDetailsMap.put("billId", bills.get(0).getId());
		// total fee
			applicationDetail.setTotalPayableAmount(bills.get(0).getTotalAmount());
		}
		applicationDetail.setBillDetails(billDetailsMap);
		
		// enrich userDetails
		Map<Object, Object> userDetails = new HashMap<>();
		userDetails.put("UserName", communityHallApplication.getApplicantDetail().getApplicantName());
		userDetails.put("MobileNo", communityHallApplication.getApplicantDetail().getApplicantMobileNo());
		userDetails.put("Email", communityHallApplication.getApplicantDetail().getApplicantEmailId());
		userDetails.put("Address", new String(communityHallApplication.getAddress().getAddressLine1().concat(", "))
									.concat(communityHallApplication.getAddress().getPincode()));
		applicationDetail.setUserDetails(userDetails);
		return applicationDetail;
	}
	

	@Override
	public CommunityHallBookingDetail updateStatus(
			@Valid CommunityHallBookingUpdateStatusRequest communityHallBookingUpdateStatusRequest) {
		// TODO Auto-generated method stub
		String bookingNo = communityHallBookingUpdateStatusRequest.getBookingNo();
		log.info("Updating booking for booking no : " + bookingNo);
		if (bookingNo == null) {
			return null;
		}
		CommunityHallBookingSearchCriteria bookingSearchCriteria = CommunityHallBookingSearchCriteria.builder()
				.bookingNo(bookingNo).build();
		List<CommunityHallBookingDetail> bookingDetails = bookingRepository.getBookingDetails(bookingSearchCriteria);
		CommunityHallBookingRequest communityHallBookingRequest = CommunityHallBookingRequest .builder()
				.hallsBookingApplication(bookingDetails.get(0))
				.requestInfo(communityHallBookingUpdateStatusRequest.getRequestInfo())
				 .build();
		
		communityHallBookingRequest.getHallsBookingApplication()
        .setWorkflow(communityHallBookingUpdateStatusRequest.getWorkflow());
			
		if (bookingDetails.size() == 0) {
			throw new CustomException("INVALID_BOOKING_CODE",
					"Booking no not valid. Failed to update booking status for : " + bookingNo);
		}
		return updateBooking(communityHallBookingRequest,null,BookingStatusEnum.BOOKED);
	}

	@Override
	public List<AssetDTO> fetchAssets(AssetSearchCriteria assetSearchCriteria, RequestInfo requestInfo) {
		 Object assetResponseEntity=null;
		// TODO Auto-generated method stub

		RequestInfoWrapper requestBody = RequestInfoWrapper.builder()
		        .requestInfo(requestInfo)
		        .build();
		
		   	String uri = config.getAssetSearchEndpoint();
	         uri = uri.concat("?tenantId=").concat(assetSearchCriteria.getTenantId());

			  Object result = serviceRequestRepository.fetchResult(new StringBuilder(uri),RequestInfoWrapper.builder()
                .requestInfo(requestInfo).build());                          
       
				AssetResponse assetSearchResponse = mapper.convertValue(result, AssetResponse.class);

		// Build the URL for the Asset Search API
		 
		// AssetResponse assetSearchResponse = null;
			try {
				// assetResponseEntity = restTemplate.postForObject(assetSearchUrl, requestBody,
				// 		 Object.class);

				// System.out.println(assetResponseEntity);
				
				// assetSearchResponse = mapper.convertValue(assetResponseEntity,AssetResponse.class);	
				
				System.out.println(assetSearchResponse);

			} catch (Exception e) {
				log.error("Error occured while user search.", e);
				throw new CustomException("USER SEARCH ERROR",
						"Error occured while user search. Message: " + e.getMessage());
			}
		  return new ArrayList<>();

		//throw new UnsupportedOperationException("Unimplemented method 'fetchAssets'");
	}

}
