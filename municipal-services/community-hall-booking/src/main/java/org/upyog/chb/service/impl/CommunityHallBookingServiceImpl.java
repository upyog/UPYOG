package org.upyog.chb.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.upyog.chb.web.models.PDFRequest;
import org.upyog.chb.web.models.DmsRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.enums.BookingStatusEnum;
import org.upyog.chb.repository.CommunityHallBookingRepository;
import org.upyog.chb.repository.ServiceRequestRepository;
import org.upyog.chb.service.BillingService;
import org.upyog.chb.service.AlfrescoService;
import org.upyog.chb.service.CHBEncryptionService;
import org.upyog.chb.service.CommunityHallBookingService;
import org.upyog.chb.service.DemandService;
import org.upyog.chb.service.EnrichmentService;
import org.upyog.chb.service.WorkflowService;
import org.upyog.chb.util.CommunityHallBookingUtil;
import org.upyog.chb.util.MdmsUtil;
import org.upyog.chb.validator.CommunityHallBookingValidator;
import org.upyog.chb.web.models.ApplicationDetail;
import org.upyog.chb.web.models.Asset;
import org.upyog.chb.web.models.AssetResponse;
import org.upyog.chb.web.models.AssetSearchCriteria;
import org.upyog.chb.web.models.AssetUpdate;
import org.upyog.chb.web.models.AssetUpdateRequest;
import org.upyog.chb.web.models.AssetUpdationResponse;
import org.upyog.chb.web.models.CommmunityHallSlotDetailsRequest;
import org.upyog.chb.web.models.CommunityHallBookingActionRequest;
import org.upyog.chb.web.models.CommunityHallBookingActionResponse;
import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.CommunityHallBookingRequest;
import org.upyog.chb.web.models.CommunityHallBookingSearchCriteria;
import org.upyog.chb.web.models.CommunityHallBookingUpdateStatusRequest;
import org.upyog.chb.web.models.CommunityHallSlotAvailabilityDetail;
import org.upyog.chb.web.models.CommunityHallSlotSearchCriteria;
import org.upyog.chb.web.models.RequestInfoWrapper;
import org.upyog.chb.web.models.User;
import org.upyog.chb.web.models.collection.Bill;
import org.upyog.chb.web.models.collection.BillSearchCriteria;
import org.upyog.chb.web.models.collection.GenerateBillCriteria;
import org.upyog.chb.service.ReportService;
import java.util.stream.Collectors;


import com.fasterxml.jackson.databind.ObjectMapper;

import digit.models.coremodels.PaymentDetail;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommunityHallBookingServiceImpl implements CommunityHallBookingService {

	@Autowired
	private ReportService reportService;
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
	
	@Autowired
	private AlfrescoService alfrescoService;

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

		setRelatedAssetData(communityHallsBookingRequest);

		if (communityHallsBookingRequest.getHallsBookingApplication().getRelatedAsset() == null) {
			throw new CustomException("INVALID_BOOKING_CODE",
					"Community Hall/Ground not availabel for booking. Failed to create booking  for : "
							+ communityHallsBookingRequest.getHallsBookingApplication().getCommunityHallCode());
		}
//		 
		Object mdmsData = mdmsUtil.mDMSCall(communityHallsBookingRequest.getRequestInfo(), tenantId);

		// 1. Validate request master data to confirm it has only valid data in records
		hallBookingValidator.validateCreate(communityHallsBookingRequest, mdmsData);

		// 2. Add fields that has custom logic like booking no, ids using UUID
		enrichmentService.enrichCreateBookingRequest(communityHallsBookingRequest);

		// ENcrypt PII data of applicant
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
		billingService.generateBill(communityHallsBookingRequest.getRequestInfo(), billCriteria);

		// 4.Persist the request using persister service
		bookingRepository.saveCommunityHallBooking(communityHallsBookingRequest);

		// Rest Update Asset

//		blockAssetBookingStatus(communityHallsBookingRequest);
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
		setRelatedAssetData(communityHallsBookingRequest);
		if (status != BookingStatusEnum.BOOKED) {
			CommunityHallBookingSearchCriteria bookingSearchCriteria = CommunityHallBookingSearchCriteria.builder()
					.bookingNo(bookingNo).build();
			List<CommunityHallBookingDetail> bookingDetails = bookingRepository
					.getBookingDetails(bookingSearchCriteria);
			if (bookingDetails.size() == 0) {
				throw new CustomException("INVALID_BOOKING_CODE",
						"Booking no not valid. Failed to update booking status for : " + bookingNo);
			}

			convertBookingRequest(communityHallsBookingRequest, bookingDetails.get(0));

		}
		else {
			createCertificate(communityHallsBookingRequest);
		}

		enrichmentService.enrichUpdateBookingRequest(communityHallsBookingRequest, status);

		workflowService.updateWorkflow(communityHallsBookingRequest);
		// Update payment date and receipt no on successful payment when payment detail
		// object is received
		if (paymentDetail != null) {
			communityHallsBookingRequest.getHallsBookingApplication().setReceiptNo(paymentDetail.getReceiptNumber());
			communityHallsBookingRequest.getHallsBookingApplication().setPaymentDate(paymentDetail.getReceiptDate());
		}
		bookingRepository.updateBooking(communityHallsBookingRequest);
		log.info("fetched booking detail and updated status "
				+ communityHallsBookingRequest.getHallsBookingApplication().getBookingStatus());
		return communityHallsBookingRequest.getHallsBookingApplication();
	}
	
	private void createCertificate(CommunityHallBookingRequest communityHallsBookingRequest) {
//		validateGrbCertificateGeneration(GarbageAccount);

		// create pdf
		Resource resource = createResource(communityHallsBookingRequest);

		// upload pdf
		DmsRequest dmsRequest = generateDmsRequest(resource, communityHallsBookingRequest);
		try {
			String documentReferenceId = alfrescoService.uploadAttachment(dmsRequest, communityHallsBookingRequest.getRequestInfo());
		} catch (IOException e) {
			throw new CustomException("UPLOAD_ATTACHMENT_FAILED", "Upload Attachment failed." + e.getMessage());
		}

	}
	
	private Resource createResource(CommunityHallBookingRequest communityHallsBookingRequest) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> chbObject = new HashMap<>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		CommunityHallBookingDetail bookingDetail = communityHallsBookingRequest.getHallsBookingApplication();
		chbObject.put("approverName", bookingRepository.getApproverName(bookingDetail.getTenantId()));
		log.info("approverName " + bookingRepository.getApproverName(bookingDetail.getTenantId()));

//		chbObject.put("userName", null != requestInfo.getUserInfo() ? requestInfo.getUserInfo().getName() : null);
		
		chbObject.put("fromDate", bookingDetail.getBookingSlotDetails().get(0).getBookingDate());
		chbObject.put("toDate", bookingDetail.getBookingSlotDetails().get(0).getBookingToDate());
		chbObject.put("siteName", bookingDetail.getRelatedAsset().getAssetName());
		chbObject.put("siteAddress", String.join(", ",
			    bookingDetail.getRelatedAsset().getAddressDetails().getAddressLine1(),
			    bookingDetail.getRelatedAsset().getAddressDetails().getCity(),
			    bookingDetail.getRelatedAsset().getAddressDetails().getStreet(),
			    bookingDetail.getRelatedAsset().getAddressDetails().getPincode()
			));
		
		chbObject.put("bookingReference", bookingDetail.getBookingNo());
		chbObject.put("bookedBy", bookingDetail.getApplicantDetail().getApplicantName());
		chbObject.put("mobileNumber", bookingDetail.getApplicantDetail().getApplicantMobileNo());
		chbObject.put("bookingDate", dateFormat.format(new Date(bookingDetail.getPaymentDate() * 1000)));
		chbObject.put("address",  String.join(", ",
			    bookingDetail.getAddress().getAddressLine1(),
			    bookingDetail.getAddress().getCity(),
			    bookingDetail.getAddress().getPincode()));
		chbObject.put("contactNumber", bookingDetail.getApplicantDetail().getApplicantAlternateMobileNo());
		chbObject.put("pinLocation", null);
		chbObject.put("contactLocation", null);
		chbObject.put("bookingStatus","BOOKED");
		chbObject.put("perDayCost", new BigDecimal(communityHallsBookingRequest
	            .getHallsBookingApplication()
	            .getRelatedAsset()
	            .getAssetDetails()
	            .get("gstAssetCost")
	            .asText()));
		chbObject.put("ulbName", bookingDetail.getRelatedAsset().getAddressDetails().getStreet());
		chbObject.put("ulbType", bookingDetail.getRelatedAsset().getAddressDetails().getType());
		long days = calculateDaysBetween(bookingDetail.getBookingSlotDetails().get(0).getBookingDate(),
				bookingDetail.getBookingSlotDetails().get(0).getBookingToDate());
		chbObject.put("numberOfDays", days);

		// Total Payable amount
		BigDecimal totalPayableAmount = BigDecimal.valueOf(days)
			    .multiply(new BigDecimal(communityHallsBookingRequest
			            .getHallsBookingApplication()
			            .getRelatedAsset()
			            .getAssetDetails()
			            .get("gstAssetCost")
			            .asText())) // Converts assetCost string to BigDecimal
			    .add(new BigDecimal(communityHallsBookingRequest
			            .getHallsBookingApplication()
			            .getRelatedAsset()
			            .getAssetDetails()
			            .get("securityAmount")
			            .asText()));
		chbObject.put("totalAmount", totalPayableAmount);
		chbObject.put("securityAmount", new BigDecimal(communityHallsBookingRequest.getHallsBookingApplication()
				.getRelatedAsset()
	            .getAssetDetails()
	            .get("securityAmount")
	            .asText()));

//		chbObject.put("ownerName", GarbageAccount.getName());// owner Name
//		chbObject.put("address",
//				GarbageAccount.getAddresses().get(0).getAddress1().concat(", ")
//						.concat(GarbageAccount.getAddresses().get(0).getWardName()).concat(", ")
//						.concat(GarbageAccount.getAddresses().get(0).getUlbName()).concat(" (")
//						.concat(GarbageAccount.getAddresses().get(0).getUlbType()).concat(") ")
//						.concat(GarbageAccount.getAddresses().get(0).getAdditionalDetail().get("district").asText())
//						.concat(", ").concat(GarbageAccount.getAddresses().get(0).getPincode()));
		// Applicant
		// Name
		// Contact // No
//		chbObject.put("propertyId", GarbageAccount.getPropertyId());

//		chbObject.put("createdTime", "sjgjkhd");

//		chbObject.put("approverName",null != requestInfo.getUserInfo() ? requestInfo.getUserInfo().getUserName() : null);

//		chbObject.put("userName", null != requestInfo.getUserInfo() ? requestInfo.getUserInfo().getName() : null);
		// generate QR code from attributes
//		StringBuilder uri = new StringBuilder(applicationPropertiesAndConstant.getFrontEndBaseUri());
//		uri.append("citizen-payment");
//		String qr = GarbageAccount.getCreated_by().concat("/").concat(GarbageAccount.getUuid())
//				.concat("/").concat(GarbageAccount.getPropertyId());
//		uri.append("/").append(qr);
//		chbObject.put("qrCodeText", uri);
		map.put("chb", chbObject);
		PDFRequest pdfRequest = PDFRequest.builder().RequestInfo(communityHallsBookingRequest.getRequestInfo()).key("chbBookingCertificate")
				.tenantId(bookingDetail.getTenantId()).data(map).build();
		Resource resource = reportService.createNoSavePDF(pdfRequest);

		return resource;
	}

	private DmsRequest generateDmsRequest(Resource resource, CommunityHallBookingRequest communityHallsBookingRequest) {
		CommunityHallBookingDetail bookingDetails = communityHallsBookingRequest.getHallsBookingApplication();
		RequestInfo requestInfo = communityHallsBookingRequest.getRequestInfo();		
		DmsRequest dmsRequest = DmsRequest.builder().userId(requestInfo.getUserInfo().getId().toString())
				.objectId(bookingDetails.getBookingId()).description(CommunityHallBookingConfiguration.ALFRESCO_COMMON_CERTIFICATE_DESCRIPTION)
				.id(CommunityHallBookingConfiguration.ALFRESCO_COMMON_CERTIFICATE_ID).type(CommunityHallBookingConfiguration.ALFRESCO_COMMON_CERTIFICATE_TYPE)
				.objectName(CommunityHallBookingConfiguration.ALFRESCO_BUSINESS_SERVICE).comments(CommunityHallBookingConfiguration.ALFRESCO_TL_CERTIFICATE_COMMENT)
				.status(CommunityHallBookingConfiguration.STATUS_APPROVED).file(resource).servicetype(CommunityHallBookingConfiguration.ALFRESCO_BUSINESS_SERVICE)
				.documentType(CommunityHallBookingConfiguration.ALFRESCO_DOCUMENT_TYPE)
				.documentId(CommunityHallBookingConfiguration.ALFRESCO_COMMON_DOCUMENT_ID).build();

		return dmsRequest;
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
		// Calculating list of dates for booking
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

		// Setting hall status to booked if it is already booked by checking in the
		// database entry
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
				// Setting slot status available for every hall and hall code
				.slotStaus(BookingStatusEnum.AVAILABLE.toString()).tenantId(criteria.getTenantId())
				.bookingDate(CommunityHallBookingUtil.parseLocalDateToString(date)).build();
		return availabiltityDetail;
	}

	public CommunityHallBookingActionResponse getApplicationDetails(
			CommunityHallBookingActionRequest communityHallActionRequest) {

		if (CollectionUtils.isEmpty(communityHallActionRequest.getApplicationNumbers())) {
			throw new CustomException("INVALID REQUEST", "Provide Application Number.");
		}

		CommunityHallBookingActionResponse communityHallActionResponse = CommunityHallBookingActionResponse.builder()
				.applicationDetails(new ArrayList<>()).build();
		communityHallActionRequest.getApplicationNumbers().stream().forEach(applicationNumber -> {

			// search application number
			CommunityHallBookingSearchCriteria criteria = CommunityHallBookingSearchCriteria.builder()
					.bookingNo(applicationNumber).build();
			List<CommunityHallBookingDetail> petApplications = bookingRepository.getBookingDetails(criteria);

			CommunityHallBookingDetail communityHallApplication = null != petApplications ? petApplications.get(0)
					: null;
			communityHallApplication = encryptionService.decryptObject(communityHallApplication,
					communityHallActionRequest.getRequestInfo());
			ApplicationDetail applicationDetail = getApplicationBillUserDetail(communityHallApplication,
					communityHallActionRequest.getRequestInfo());
			communityHallActionResponse.getApplicationDetails().add(applicationDetail);
		});

		return communityHallActionResponse;
	}

	private ApplicationDetail getApplicationBillUserDetail(CommunityHallBookingDetail communityHallApplication,
			RequestInfo requestInfo) {

		ApplicationDetail applicationDetail = ApplicationDetail.builder()
				.applicationNumber(communityHallApplication.getBookingNo()).build();

		CommunityHallBookingRequest communityHallsBookingRequest = CommunityHallBookingRequest.builder()
				.hallsBookingApplication(communityHallApplication).requestInfo(requestInfo).build();

		setRelatedAssetData(communityHallsBookingRequest);

		long days = calculateDaysBetween(communityHallApplication.getBookingSlotDetails().get(0).getBookingDate(),
				communityHallApplication.getBookingSlotDetails().get(0).getBookingToDate());
		
		// Total Payable amount
		BigDecimal totalPayableAmount = BigDecimal.valueOf(days)
			    .multiply(new BigDecimal(communityHallsBookingRequest
			            .getHallsBookingApplication()
			            .getRelatedAsset()
			            .getAssetDetails()
			            .get("gstAssetCost")
			            .asText())) // Converts assetCost string to BigDecimal
			    .add(new BigDecimal(communityHallsBookingRequest
			            .getHallsBookingApplication()
			            .getRelatedAsset()
			            .getAssetDetails()
			            .get("securityAmount")
			            .asText())); // Converts securityAmount string to BigDecimal
		


		// Fee calculation formula

		applicationDetail.setFeeCalculationFormula("From Date: (<b>"
				+ communityHallApplication.getBookingSlotDetails().get(0).getBookingDate() + "</b>), To Date: " + "(<b>"
				+ communityHallApplication.getBookingSlotDetails().get(0).getBookingToDate()
				+ "</b>) = Number Of Days (<b>" + days + "</b>) * Cost per day: (<b>"
				+ communityHallsBookingRequest
						.getHallsBookingApplication().getRelatedAsset().getAssetDetails().get("assetCost").asText()
				+ "</b>) + Tax(<b>"
				+ communityHallsBookingRequest
						.getHallsBookingApplication().getRelatedAsset().getAssetDetails().get("gstPercnetage").asText()
				+ "</b>) + Security(<b>"
				+ communityHallsBookingRequest.getHallsBookingApplication().getRelatedAsset().getAssetDetails()
						.get("securityAmount").asText()
				+ ")</b> " + "= Total Payable Amount(<b>" + totalPayableAmount + "</b>)");

		// search bill Details
		BillSearchCriteria billSearchCriteria = BillSearchCriteria.builder()
				.tenantId(communityHallApplication.getTenantId())
				.consumerCode(Collections.singleton(applicationDetail.getApplicationNumber())).service("chb-services")
				.build();
		List<Bill> bills = billingService.searchBill(billSearchCriteria, requestInfo);
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
	
	
	 public long calculateDaysBetween(String fromDate, String toDate) {
	        // Define the date format
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	        // Parse the input strings into LocalDate
	        LocalDate fromDateParsed = LocalDate.parse(fromDate, formatter);
	        LocalDate toDateParsed = LocalDate.parse(toDate, formatter);

	        // Calculate the difference in days
	        return ChronoUnit.DAYS.between(fromDateParsed, toDateParsed) + 1;
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
		bookingDetails = encryptionService.decryptObject(bookingDetails, communityHallBookingUpdateStatusRequest.getRequestInfo());
		CommunityHallBookingRequest communityHallBookingRequest = CommunityHallBookingRequest.builder()
				.hallsBookingApplication(bookingDetails.get(0))
				.requestInfo(communityHallBookingUpdateStatusRequest.getRequestInfo()).build();
		communityHallBookingRequest.getHallsBookingApplication()
				.setWorkflow(communityHallBookingUpdateStatusRequest.getWorkflow());

		if (bookingDetails.size() == 0) {
			throw new CustomException("INVALID_BOOKING_CODE",
					"Booking no not valid. Failed to update booking status for : " + bookingNo);
		}
		return updateBooking(communityHallBookingRequest, null, BookingStatusEnum.BOOKED);
	}

	@Override
	public List<Asset> fetchAssets(AssetSearchCriteria assetSearchCriteria, RequestInfo requestInfo) {

		String uri = config.getAssetHost().concat(config.getAssetSearchEndpoint());
		uri = uri.concat("?tenantId=").concat(assetSearchCriteria.getTenantId()).concat("&applicationNo=")
				.concat(assetSearchCriteria.getApplicationNo());

		AssetResponse assetSearchResponse = null;
		try {
			assetSearchResponse = restTemplate.postForObject(uri,
					RequestInfoWrapper.builder().requestInfo(requestInfo).build(), AssetResponse.class);

			if (assetSearchResponse != null && assetSearchResponse.getAssets() != null) {
				// Return the list of assets
				return assetSearchResponse.getAssets();
			} else {
				// Log that no assets were found or response was empty
				log.warn("No assets found in the response for tenantId: {}", assetSearchCriteria.getTenantId());
			}

		} catch (Exception e) {
			log.error("Error occured while asset search.", e);
			throw new CustomException("ASSET SEARCH ERROR",
					"Error occured while asset search. Message: " + e.getMessage());
		}
		return new ArrayList<>();
	}

	@Override
	public void setRelatedAsset(List<CommunityHallBookingDetail> applications, RequestInfoWrapper requestInfoWrapper) {
		for (CommunityHallBookingDetail communityHallBookingDetail : applications) {
			AssetSearchCriteria assetSearchCriteria = new AssetSearchCriteria();

			assetSearchCriteria.setApplicationNo(communityHallBookingDetail.getCommunityHallCode());
			assetSearchCriteria.setTenantId(communityHallBookingDetail.getTenantId());

			List<Asset> relatedAssets = fetchAssets(assetSearchCriteria, requestInfoWrapper.getRequestInfo());

			if (!CollectionUtils.isEmpty(relatedAssets)) {
				communityHallBookingDetail.setRelatedAsset(relatedAssets.get(0));
			}
		}

	}

	public void setRelatedAssetData(CommunityHallBookingRequest communityHallsBookingRequest) {

		AssetSearchCriteria assetSearchCriteria = new AssetSearchCriteria();

		assetSearchCriteria
				.setApplicationNo(communityHallsBookingRequest.getHallsBookingApplication().getCommunityHallCode());
		assetSearchCriteria.setTenantId(communityHallsBookingRequest.getHallsBookingApplication().getTenantId());
		assetSearchCriteria.setBookingStatus(CommunityHallBookingConstants.BOOKING_AVAILABLE_STATUS);

		List<Asset> relatedAssets = fetchAssets(assetSearchCriteria, communityHallsBookingRequest.getRequestInfo());

		if (!CollectionUtils.isEmpty(relatedAssets)) {
			communityHallsBookingRequest.getHallsBookingApplication().setRelatedAsset(relatedAssets.get(0));
		}

	}

	public void blockAssetBookingStatus(CommunityHallBookingRequest communityHallsBookingRequest) {

		communityHallsBookingRequest.getHallsBookingApplication().getRelatedAsset()
				.setBookingStatus(CommunityHallBookingConstants.BOOKING_BOOKED_STATUS);
		AssetUpdateRequest searchResult = new AssetUpdateRequest();
		ObjectMapper objectMapper = new ObjectMapper();
		AssetUpdate obj = objectMapper.convertValue(
				communityHallsBookingRequest.getHallsBookingApplication().getRelatedAsset(), AssetUpdate.class);
		List<AssetUpdate> assetList = new ArrayList<>();
		assetList.add(obj);
		searchResult.setAssetUpdate(assetList);
		searchResult.setRequestInfo(communityHallsBookingRequest.getRequestInfo());
		updateAsset(searchResult);
	}

	@Override
	public List<AssetUpdate> updateAsset(AssetUpdateRequest assetUpdateRequest) {
		String uri = config.getAssetHost().concat(config.getAssetUpdateEndpoint());

		AssetUpdationResponse assetSearchResponse = null;
		try {
			assetSearchResponse = restTemplate
					.postForObject(uri,
							AssetUpdateRequest.builder().requestInfo(assetUpdateRequest.getRequestInfo())
									.assetUpdate(assetUpdateRequest.getAssetUpdate()).build(),
							AssetUpdationResponse.class);

			if (assetSearchResponse != null && assetSearchResponse.getAssets() != null) {
				// Return the list of assets
				return assetSearchResponse.getAssets();
			} else {
				// Log that no assets were found or response was empty
			}

		} catch (Exception e) {
			log.error("Error occured while asset search.", e);
			throw new CustomException("ASSET SEARCH ERROR",
					"Error occured while asset search. Message: " + e.getMessage());
		}
		return new ArrayList<>();
	}
	
	@Override
	public List<String> fetchSlotDetails(CommmunityHallSlotDetailsRequest commmunityHallSlotDetailsRequest) {
		String bookingCode = commmunityHallSlotDetailsRequest.getCommunityHallCode();
		List<CommunityHallSlotAvailabilityDetail> slots = bookingRepository.getBookingCodeSlots(bookingCode);
		List<LocalDate> bookedDates = new ArrayList<>();
		slots.stream().forEach(slot -> {
			LocalDate startDate = CommunityHallBookingUtil.parseStringToLocalDate1(slot.getBookingDate());

			LocalDate endDate = CommunityHallBookingUtil.parseStringToLocalDate1(slot.getBookingToDate());

			while (!startDate.isAfter(endDate)) {
				bookedDates.add(startDate);
				startDate = startDate.plusDays(1);
			}
		});
		List<String> dateStrings = bookedDates.stream()
	            .map(date -> date.format(DateTimeFormatter.ISO_LOCAL_DATE)) // Format each LocalDate
	            .collect(Collectors.toList());
		return dateStrings;
	}

}
