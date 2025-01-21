package org.egov.ptr.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.ptr.config.PetConfiguration;
import org.egov.ptr.models.ApplicationDetail;
import org.egov.ptr.models.Demand;
import org.egov.ptr.models.PetApplicationSearchCriteria;
import org.egov.ptr.models.PetRegistrationActionRequest;
import org.egov.ptr.models.PetRegistrationActionResponse;
import org.egov.ptr.models.PetCountResponse;
import org.egov.ptr.models.PetRegistrationApplication;
import org.egov.ptr.models.PetRegistrationRequest;
import org.egov.ptr.models.collection.Bill;
import org.egov.ptr.models.collection.BillResponse;
import org.egov.ptr.models.collection.BillSearchCriteria;
import org.egov.ptr.models.collection.GenerateBillCriteria;
import org.egov.ptr.models.workflow.BusinessServiceResponse;
import org.egov.ptr.models.workflow.State;
import org.egov.ptr.producer.Producer;
import org.egov.ptr.repository.PetRegistrationRepository;
import org.egov.ptr.repository.ServiceRequestRepository;
import org.egov.ptr.util.CommonUtils;
import org.egov.ptr.util.ErrorConstants;
import org.egov.ptr.util.PTRConstants;
import org.egov.ptr.validator.PetApplicationValidator;
import org.egov.ptr.web.contracts.PDFRequest;
import org.egov.ptr.web.contracts.PetPhotoRequest;
import org.egov.ptr.web.contracts.RequestInfoWrapper;
import org.egov.ptr.web.contracts.alfresco.DMSResponse;
import org.egov.ptr.web.contracts.alfresco.DmsRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

@Service
public class PetRegistrationService {

	@Autowired
	private Producer producer;

	@Autowired
	private PetConfiguration config;

	@Autowired
	private EnrichmentService enrichmentService;

	@Autowired
	private PetApplicationValidator validator;

	@Autowired
	private UserService userService;

	@Autowired
	private WorkflowService wfService;

	@Autowired
	private DemandService demandService;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private PetRegistrationRepository petRegistrationRepository;

	@Autowired
	private BillingService billingService;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private ReportService reportService;

	@Autowired
	private AlfrescoService alfrescoService;

	@Autowired
	private CommonUtils commonUtils;

	/**
	 * Enriches the Request and pushes to the Queue
	 *
	 */
	public List<PetRegistrationApplication> registerPtrRequest(PetRegistrationRequest petRegistrationRequest) {

		validator.validatePetApplication(petRegistrationRequest);
		enrichmentService.enrichPetApplication(petRegistrationRequest);

		// Enrich/Upsert user in upon pet registration
		// userService.callUserService(petRegistrationRequest); need to build the method
		wfService.updateWorkflowStatus(petRegistrationRequest);
		producer.push(config.getCreatePtrTopic(), petRegistrationRequest);

		return petRegistrationRequest.getPetRegistrationApplications();
	}

	public List<PetRegistrationApplication> searchPtrApplications(RequestInfo requestInfo,
			PetApplicationSearchCriteria petApplicationSearchCriteria) {

		validateAndEnrichSearchCriteria(requestInfo, petApplicationSearchCriteria);
		
		List<PetRegistrationApplication> applications = petRegistrationRepository
				.getApplications(petApplicationSearchCriteria);

		if (CollectionUtils.isEmpty(applications))
			return new ArrayList<>();

		return applications;
	}

	private void validateAndEnrichSearchCriteria(RequestInfo requestInfo,
			PetApplicationSearchCriteria petApplicationSearchCriteria) {
		
		// search criteria for CITIZEN
		if(null != requestInfo && null != requestInfo.getUserInfo()
				&& StringUtils.equalsAnyIgnoreCase(requestInfo.getUserInfo().getType(), PTRConstants.USER_TYPE_CITIZEN)) {
			petApplicationSearchCriteria.setCreatedBy(requestInfo.getUserInfo().getUuid());
		}
		
		// search criteria for EMPLOYEE
		if(null != requestInfo && null != requestInfo.getUserInfo()
				&& StringUtils.equalsAnyIgnoreCase(requestInfo.getUserInfo().getType(), PTRConstants.USER_TYPE_EMPLOYEE)) {
			if(petApplicationSearchCriteria == null || StringUtils.isEmpty(petApplicationSearchCriteria.getTenantId())) {
				throw new CustomException("TENANTID_MANDATORY", "TenantId is mandatory for employee to search registrations.");
			}
			
			List<String> listOfStatus = getAccountStatusListByRoles(petApplicationSearchCriteria, requestInfo.getUserInfo().getRoles());
			if(CollectionUtils.isEmpty(listOfStatus)) {
				throw new CustomException("SEARCH_ACCOUNT_BY_ROLES","Search can't be performed by this Employee due to lack of roles.");
			}
			petApplicationSearchCriteria.setStatus(listOfStatus);
		}
		
	}
	
	private List<String> getAccountStatusListByRoles(PetApplicationSearchCriteria petApplicationSearchCriteria, List<Role> roles) {
		
		List<String> rolesWithinTenant = getRolesByTenantId(petApplicationSearchCriteria.getTenantId(), roles);	
		Set<String> statusWithRoles = new HashSet();
		AtomicReference<String> tempTenantId = new AtomicReference<>(petApplicationSearchCriteria.getTenantId());
		
		rolesWithinTenant.stream().forEach(role -> {
			
			if(StringUtils.equalsIgnoreCase(role, PTRConstants.USER_ROLE_PTR_VERIFIER)) {
				statusWithRoles.add(PTRConstants.APPLICATION_STATUS_PENDINGFORVERIFICATION);
			}else if(StringUtils.equalsIgnoreCase(role, PTRConstants.USER_ROLE_PTR_APPROVER)) {
				statusWithRoles.add(PTRConstants.APPLICATION_STATUS_PENDINGFORAPPROVAL);
				statusWithRoles.add(PTRConstants.APPLICATION_STATUS_APPROVED);
				statusWithRoles.add(PTRConstants.APPLICATION_STATUS_REJECTED);
				statusWithRoles.add(PTRConstants.APPLICATION_STATUS_PENDINGFORMODIFICATION);
			}else if(StringUtils.equalsAnyIgnoreCase(role, PTRConstants.USER_ROLE_SUPERVISOR, PTRConstants.USER_ROLE_SECRETARY)) {
				statusWithRoles.addAll(Arrays.asList(
						PTRConstants.APPLICATION_STATUS_INITIATED,
					    PTRConstants.APPLICATION_STATUS_PENDINGFORVERIFICATION,
					    PTRConstants.APPLICATION_STATUS_PENDINGFORAPPROVAL,
					    PTRConstants.APPLICATION_STATUS_PENDINGFORMODIFICATION,
					    PTRConstants.APPLICATION_STATUS_PENDINGFORPAYMENT,
					    PTRConstants.APPLICATION_STATUS_APPROVED,
					    PTRConstants.APPLICATION_STATUS_REJECTED
		            ));
				if(StringUtils.equalsIgnoreCase(petApplicationSearchCriteria.getTenantId()
						, PTRConstants.STATE_LEVEL_TENANT_ID)) {
					tempTenantId.set(null);
				}
			}
			
		});
		petApplicationSearchCriteria.setTenantId(tempTenantId.get());
		
		return new ArrayList<>(statusWithRoles);
	}
	

	private List<String> getRolesByTenantId(String tenantId, List<Role> roles) {

		List<String> roleCodes = roles.stream()
				.filter(role -> StringUtils.equalsIgnoreCase(role.getTenantId(), tenantId)).map(role -> role.getCode())
				.collect(Collectors.toList());
		return roleCodes;
	}


	public PetRegistrationApplication updatePtrApplication(PetRegistrationRequest petRegistrationRequest) {
		PetRegistrationApplication existingApplication = validator
				.validateApplicationExistence(petRegistrationRequest.getPetRegistrationApplications().get(0));
		
		// enrich
		enrichmentService.enrichPetApplicationUponUpdate(petRegistrationRequest, existingApplication);

        // create and upload pdf
        createAndUploadPDF(petRegistrationRequest);
        
		// generate demand and bill
		generateDemandAndBill(petRegistrationRequest);
		
		// wf call
		wfService.updateWorkflowStatus(petRegistrationRequest);
		
		// update
		producer.push(config.getUpdatePtrTopic(), petRegistrationRequest);

		return petRegistrationRequest.getPetRegistrationApplications().get(0);
	}

	private void createAndUploadPDF(PetRegistrationRequest petRegistrationRequest) {
		
		if (!CollectionUtils.isEmpty(petRegistrationRequest.getPetRegistrationApplications())) {
			petRegistrationRequest.getPetRegistrationApplications().stream().forEach(petApplication -> {

//				Thread pdfGenerationThread = new Thread(() -> {

					// for NEW TL
					if (StringUtils.equalsIgnoreCase(petApplication.getWorkflow().getAction(), PTRConstants.WORKFLOW_ACTION_APPROVE)) {

						// validate trade license
						validatePetCertificateGeneration(petApplication);

						// create pdf
						Resource resource = createNoSavePDF(petApplication, petRegistrationRequest.getRequestInfo());

						//upload pdf
						DmsRequest dmsRequest = generateDmsRequestByPetApplication(resource, petApplication,
								petRegistrationRequest.getRequestInfo());
						try {
							DMSResponse dmsResponse = alfrescoService.uploadAttachment(dmsRequest,
									petRegistrationRequest.getRequestInfo());
						} catch (IOException e) {
							throw new CustomException("UPLOAD_ATTACHMENT_FAILED",
									"Upload Attachment failed." + e.getMessage());
						}
					}

//				});

//				pdfGenerationThread.start();

			});
		}
		
		
	}

	private DmsRequest generateDmsRequestByPetApplication(Resource resource, PetRegistrationApplication petRegistrationApplication,
			RequestInfo requestInfo) {

		DmsRequest dmsRequest = DmsRequest.builder().userId(requestInfo.getUserInfo().getId().toString())
				.objectId(petRegistrationApplication.getId()).description(PTRConstants.ALFRESCO_COMMON_CERTIFICATE_DESCRIPTION).id(PTRConstants.ALFRESCO_COMMON_CERTIFICATE_ID).type(PTRConstants.ALFRESCO_COMMON_CERTIFICATE_TYPE).objectName(PTRConstants.BUSINESS_SERVICE)
				.comments(PTRConstants.ALFRESCO_TL_CERTIFICATE_COMMENT).status(PTRConstants.APPLICATION_STATUS_APPROVED).file(resource).servicetype(PTRConstants.BUSINESS_SERVICE)
				.documentType(PTRConstants.ALFRESCO_DOCUMENT_TYPE).documentId(PTRConstants.ALFRESCO_COMMON_DOCUMENT_ID).build();

		return dmsRequest;
	}

	private Resource createNoSavePDF(PetRegistrationApplication petRegistrationApplication, RequestInfo requestInfo) {
		
		// generate pdf
		PDFRequest pdfRequest = generatePdfRequestByTradeLicense(petRegistrationApplication, requestInfo);
		Resource resource = reportService.createNoSavePDF(pdfRequest);
		
		return resource;
	}

	private PDFRequest generatePdfRequestByTradeLicense(
			PetRegistrationApplication petRegistrationApplication, RequestInfo requestInfo) {
		
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> map2 = generateDataForTradeLicensePdfCreate(petRegistrationApplication, requestInfo);
		
		map.put("ptr", map2);
		
		PDFRequest pdfRequest = PDFRequest.builder()
				.RequestInfo(requestInfo)
				.key("PetCertificate")
				.tenantId("hp")
				.data(map)
				.build();
		
		return pdfRequest;
	}

	private Map<String, Object> generateDataForTradeLicensePdfCreate(
			PetRegistrationApplication petRegistrationApplication, RequestInfo requestInfo) {

		Map<String, Object> tlObject = new HashMap<>();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		String createdTime = dateFormat.format(new Date(petRegistrationApplication.getAuditDetails().getCreatedTime()));
		
		String lastVaccineDateStr = petRegistrationApplication.getPetDetails().getLastVaccineDate().toString();
		String lastVaccineDate = null; // Declare outside the try block

		try {
		    // Define the input format of the String (example: yyyy-MM-dd)
		    SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");

		    // Parse the String into a Date object
		    Date lastVaccineDateObj = inputDateFormat.parse(lastVaccineDateStr);

		    // Format the Date object into the desired format (example: dd-MM-yyyy)
		    SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy");
		    lastVaccineDate = outputDateFormat.format(lastVaccineDateObj);

		} catch (Exception e) {
		    System.err.println("Error parsing last vaccine date: " + e.getMessage());
		    e.printStackTrace();
		}

//		String lastVaccineDate = dateFormat.format(petRegistrationApplication.getPetDetails().getLastVaccineDate().toString());
		
		// map variables and values
		tlObject.put("applicationNumber", petRegistrationApplication.getApplicationNumber());//Trade License No
		tlObject.put("petName", petRegistrationApplication.getPetDetails().getPetName()); //Trade Registration No
		tlObject.put("breedType", petRegistrationApplication.getPetDetails().getBreedType());//Trade Name
		tlObject.put("address", petRegistrationApplication.getAddress().getAddressLine1().concat(", ")
			.concat(petRegistrationApplication.getAdditionalDetail().get("ulbName").toString().concat(", ")
			.concat(petRegistrationApplication.getAddress().getPincode())));// Trade Premises Address
		tlObject.put("createdTime", createdTime);// License Issue Date
		tlObject.put("lastVaccineDate", lastVaccineDate);//License Validity
		tlObject.put("applicantName", petRegistrationApplication.getApplicantName());
		tlObject.put("mobileNumber", petRegistrationApplication.getMobileNumber());
		tlObject.put("userName", requestInfo.getUserInfo().getName());
		tlObject.put("approverName", requestInfo.getUserInfo().getUserName());
		tlObject.put("OwnerName", petRegistrationApplication.getAdditionalDetail().get("applicantName"));
		// generate QR code from attributes
		StringBuilder qr = new StringBuilder();
		getQRCodeForPdfCreate(tlObject, qr);
		
		tlObject.put("qrCodeText", qr.toString());
		
		String base64Photo = getPetPhoto(petRegistrationApplication,requestInfo);
		tlObject.put("petPhotoURL", "data:image/png;base64," + base64Photo);
		
		return tlObject;
	}

	private void getQRCodeForPdfCreate(Map<String, Object> tlObject, StringBuilder qr) {
		
		List<String> orderedKeys = Arrays.asList("applicationNumber","OwnerName", "petName", "breedType", "address");

		orderedKeys.forEach(key -> {
		    if (tlObject.containsKey(key)) {
		        qr.append(key);
		        qr.append(": ");
		        qr.append(tlObject.get(key));
		        qr.append("\r\n");
		    }
		});
		
	    replaceInStringBuilder(qr, "applicationNumber", "Pet Application No");
	    replaceInStringBuilder(qr, "OwnerName", "Pet Owner Name");
	    replaceInStringBuilder(qr, "petName", "Pet Name");
	    replaceInStringBuilder(qr, "breedType", "Breed Type");
	    replaceInStringBuilder(qr, "address", "Address");

	}
	
	private String getPetPhoto(PetRegistrationApplication petRegistrationApplication, RequestInfo requestInfo) {
		
		PetPhotoRequest PhotoRequest = generatePhotoRequestPet(petRegistrationApplication, requestInfo);
		
		String resource = reportService.getPetPhoto(PhotoRequest);

		return resource;
	}
	
	private  PetPhotoRequest generatePhotoRequestPet(PetRegistrationApplication petRegistrationApplication, RequestInfo requestInfo){
//	        Map<String, Object> requestBody = new HashMap<>();
//	        requestBody.put("RequestInfo", Collections.singletonMap("authToken", "717791b3-8e75-46ed-9194-5630ecc786f6"));
		PetPhotoRequest PhotoObject = PetPhotoRequest.builder()
				.RequestInfo(requestInfo)
				.objectId(petRegistrationApplication.getId())
				.build();;
		return PhotoObject;
	}

	private void replaceInStringBuilder(StringBuilder qr, String target, String replacement) {
	    int start;
	    while ((start = qr.indexOf(target)) != -1) {
	        qr.replace(start, start + target.length(), replacement);
	    }
	}

	private void validatePetCertificateGeneration(PetRegistrationApplication petRegistrationApplication) {
		
		if (StringUtils.isEmpty(petRegistrationApplication.getApplicationNumber())
			    || StringUtils.isEmpty(petRegistrationApplication.getPetDetails().getPetName())
			    || StringUtils.isEmpty(petRegistrationApplication.getPetDetails().getBreedType())
			    || StringUtils.isEmpty(petRegistrationApplication.getAddress().getAddressLine1())
			    || null == petRegistrationApplication.getAuditDetails().getCreatedTime()
			    || StringUtils.isEmpty(petRegistrationApplication.getPetDetails().getLastVaccineDate())
			    || StringUtils.isEmpty(petRegistrationApplication.getApplicantName())
			    || StringUtils.isEmpty(petRegistrationApplication.getMobileNumber())) {
			    
			    throw new CustomException("NULL_APPLICATION_NUMBER","PDF can't be generated with null values for application number: "+petRegistrationApplication.getApplicationNumber());
			}
	}

	private void generateDemandAndBill(PetRegistrationRequest petRegistrationRequest ) {
		if (petRegistrationRequest.getPetRegistrationApplications().get(0).getWorkflow().getAction()
				.equals(PTRConstants.WORKFLOW_ACTION_RETURN_TO_INITIATOR_FOR_PAYMENT)) {
			
			// fetch fees from MDMS
			
			
			BigDecimal taxAmount = getFeesFromMdms(petRegistrationRequest);
//			taxAmount = ;
			// create demands
			List<Demand> savedDemands = demandService.createDemand(petRegistrationRequest, taxAmount);

	        if(CollectionUtils.isEmpty(savedDemands)) {
	            throw new CustomException("INVALID_CONSUMERCODE","Bill not generated due to no Demand found for the given consumerCode");
	        }

			// fetch/create bill
            GenerateBillCriteria billCriteria = GenerateBillCriteria.builder()
            									.tenantId(petRegistrationRequest.getPetRegistrationApplications().get(0).getTenantId())
            									.businessService("pet-services")
            									.consumerCode(petRegistrationRequest.getPetRegistrationApplications().get(0).getApplicationNumber()).build();
            BillResponse billResponse = billingService.generateBill(petRegistrationRequest.getRequestInfo(),billCriteria);
            
		}
	}
	

	private BigDecimal getFeesFromMdms(PetRegistrationRequest petRegistrationRequest) {

		AtomicReference<BigDecimal> taxAmount = new AtomicReference<BigDecimal>(BigDecimal.ZERO);
		String tenantId = petRegistrationRequest.getPetRegistrationApplications().get(0).getTenantId();

		try {
			Optional<Object> response = commonUtils.getAttributeValues(PTRConstants.STATE_LEVEL_TENANT_ID,
					PTRConstants.MODULE_NAME_PET_SERVICE, Arrays.asList(PTRConstants.MASTER_NAME_FEE_STRUCTURE),
					petRegistrationRequest);

				List<LinkedHashMap<Object, Object>> feeStructureList = JsonPath.read(response.get(),
						"$.MdmsRes.PetService.FeeStructure");
				feeStructureList.stream()
						.filter(fee1 -> StringUtils.equalsIgnoreCase((String) fee1.get("tenantId"), tenantId))
						.forEach(obj -> {
							taxAmount.set(BigDecimal.valueOf(Double.valueOf(obj.get("price").toString())));
						});
				return taxAmount.get();

		} catch (Exception e) {
			throw new CustomException("FETCH_FEES_FAILED","Failed to fetch fees from mdms for tenant id: "+tenantId);
		}

	}

	public Object enrichResponseDetail(List<PetRegistrationApplication> applications) {
		
		Map<String, Object> responseDetail = new HashMap<>();

		responseDetail.put("applicationInitiated", applications.stream().filter(application -> StringUtils
				.equalsIgnoreCase(application.getStatus(), PTRConstants.APPLICATION_STATUS_INITIATED)).count());
		responseDetail.put("applicationApplied", applications.stream().filter(application -> StringUtils
				.equalsAnyIgnoreCase(application.getStatus(), PTRConstants.APPLICATION_STATUS_PENDINGFORVERIFICATION
															, PTRConstants.APPLICATION_STATUS_PENDINGFORAPPROVAL
															, PTRConstants.APPLICATION_STATUS_PENDINGFORMODIFICATION)).count());
		responseDetail.put("applicationPendingForPayment", applications.stream().filter(application -> StringUtils
				.equalsIgnoreCase(application.getStatus(), PTRConstants.APPLICATION_STATUS_PENDINGFORPAYMENT)).count());
		responseDetail.put("applicationRejected", applications.stream().filter(application -> StringUtils
				.equalsIgnoreCase(application.getStatus(), PTRConstants.APPLICATION_STATUS_REJECTED)).count());
		responseDetail.put("applicationApproved", applications.stream().filter(application -> StringUtils
				.equalsIgnoreCase(application.getStatus(), PTRConstants.APPLICATION_STATUS_APPROVED)).count());

		return responseDetail;
	}

	public PetRegistrationActionResponse getApplicationDetails(
			PetRegistrationActionRequest ptradeLicenseActionRequest) {
		
		if(CollectionUtils.isEmpty(ptradeLicenseActionRequest.getApplicationNumbers())) {
			throw new CustomException("INVALID REQUEST","Provide Application Number.");
		}
		
		PetRegistrationActionResponse tradeLicenseActionResponse = PetRegistrationActionResponse.builder()
																.applicationDetails(new ArrayList<>())
																.build();
//		Map<String, Object> totalTaxMap = new HashMap<>();
		
		ptradeLicenseActionRequest.getApplicationNumbers().stream().forEach(applicationNumber -> {
			
			// search application number
			PetApplicationSearchCriteria criteria = PetApplicationSearchCriteria.builder()
					.applicationNumber(applicationNumber)
					.build();
			List<PetRegistrationApplication> petApplications = petRegistrationRepository.getApplications(criteria);
			
			PetRegistrationApplication petRegistrationApplication = null != petApplications ? petApplications.get(0): null;
			
			PetRegistrationRequest mdmsrequest = PetRegistrationRequest.builder()
	                .requestInfo(ptradeLicenseActionRequest.getRequestInfo())
	                .build();
			
			mdmsrequest.setPetRegistrationApplications(petApplications);			

			BigDecimal taxAmount = getFeesFromMdms(mdmsrequest);
	        
			ApplicationDetail applicationDetail = getApplicationBillUserDetail(petRegistrationApplication, ptradeLicenseActionRequest.getRequestInfo());
			
			applicationDetail.setTotalPayableAmount(taxAmount);
	 
			tradeLicenseActionResponse.getApplicationDetails().add(applicationDetail);
		});
		
		return tradeLicenseActionResponse;
	}

	private ApplicationDetail getApplicationBillUserDetail(PetRegistrationApplication petRegistrationApplication,
			RequestInfo requestInfo) {
		
		ApplicationDetail applicationDetail = ApplicationDetail.builder()
				.applicationNumber(petRegistrationApplication.getApplicationNumber())
				.build();

		// formula
		StringBuilder feeCalculationFormula = new StringBuilder("Breed: "+petRegistrationApplication.getPetDetails().getBreedType());
		
		applicationDetail.setFeeCalculationFormula(feeCalculationFormula.toString());
		

		// search bill Details
		BillSearchCriteria billSearchCriteria = BillSearchCriteria.builder()
				.tenantId(petRegistrationApplication.getTenantId())
				.consumerCode(Collections.singleton(applicationDetail.getApplicationNumber()))
				.service("pet-services")
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
		userDetails.put("UserName", petRegistrationApplication.getApplicantName());
		userDetails.put("MobileNo", petRegistrationApplication.getMobileNumber());
		userDetails.put("Email", petRegistrationApplication.getEmailId());
		userDetails.put("Address", new String(petRegistrationApplication.getAddress().getAddressLine1().concat(", "))
//									.concat(petRegistrationApplication.getAddress().getAddressLine2().concat(", "))
//									.concat(petRegistrationApplication.getAddress().getDoorNo().concat(", "))
//									.concat(petRegistrationApplication.getAddress().getBuildingName().concat(", "))
//									.concat(petRegistrationApplication.getAddress().getStreet().concat(", "))
//									.concat(petRegistrationApplication.getAddress().getLandmark().concat(", "))
//									.concat(petRegistrationApplication.getAddress().getCity().concat(", "))
									.concat(petRegistrationApplication.getAddress().getPincode()));

		applicationDetail.setUserDetails(userDetails);
		
		
		
		return applicationDetail;
	}

	public PetRegistrationActionResponse getActionsOnApplication(
			PetRegistrationActionRequest ptradeLicenseActionRequest) {
		
		if(CollectionUtils.isEmpty(ptradeLicenseActionRequest.getApplicationNumbers())) {
			throw new CustomException("INVALID REQUEST","Provide Application Number.");
		}
		
		Map<String, List<String>> applicationActionMaps = new HashMap<>();
		
		ptradeLicenseActionRequest.getApplicationNumbers().stream().forEach(applicationNumber -> {
			
			// search application number
			PetApplicationSearchCriteria criteria = PetApplicationSearchCriteria.builder()
					.applicationNumber(applicationNumber)
					.build();
			List<PetRegistrationApplication> applications = petRegistrationRepository.getApplications(criteria);
			if(CollectionUtils.isEmpty(applications)) {
				throw new CustomException("APPLICATION_NOT_FOUND","No Application found with provided input.");
			}
			
			PetRegistrationApplication application = null != applications ? applications.get(0): null;
			
			String applicationStatus = application.getStatus();
			String applicationTenantId = application.getTenantId();
			String applicationBusinessId = PTRConstants.WORKFLOW_BUSINESS_SERVICE;
			List<String> rolesWithinTenant = getRolesWithinTenant(applicationTenantId, ptradeLicenseActionRequest.getRequestInfo().getUserInfo().getRoles());
			
			
			// fetch business service search
			StringBuilder uri = new StringBuilder(config.getWfHost());
			uri.append(config.getWfBusinessServiceSearchPath());
			uri.append("?tenantId=").append(applicationTenantId);
			uri.append("&businessServices=").append(applicationBusinessId);
			RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder()
					.requestInfo(ptradeLicenseActionRequest.getRequestInfo()).build();
			
			Optional<Object> responseObject = serviceRequestRepository.fetchResult(uri, requestInfoWrapper);
			if(!responseObject.isPresent()) {
				throw new CustomException("WF_SEARCH_FAILED","Failed to fetch WF business service.");
			}
			LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) serviceRequestRepository.fetchResult(uri, requestInfoWrapper).get();
			BusinessServiceResponse businessServiceResponse = mapper.convertValue(responseMap
																					, BusinessServiceResponse.class);
			
			if(null == businessServiceResponse || CollectionUtils.isEmpty(businessServiceResponse.getBusinessServices())) {
				throw new CustomException("NO_BUSINESS_SERVICE_FOUND","Business service not found for application number: "+applicationNumber);
			}
			List<State> stateList = businessServiceResponse.getBusinessServices().get(0).getStates().stream()
					.filter(state -> StringUtils.equalsIgnoreCase(state.getApplicationStatus(), applicationStatus)
										&& !StringUtils.equalsAnyIgnoreCase(state.getApplicationStatus(), PTRConstants.APPLICATION_STATUS_APPROVED)).collect(Collectors.toList());
			
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
		});
		
		List<ApplicationDetail> nextActionList = new ArrayList<>();
		applicationActionMaps.entrySet().stream().forEach(entry -> {
			nextActionList.add(ApplicationDetail.builder().applicationNumber(entry.getKey()).action(entry.getValue()).build());
		});
		
		PetRegistrationActionResponse tradeLicenseActionResponse = PetRegistrationActionResponse.builder().applicationDetails(nextActionList).build();
		return tradeLicenseActionResponse;
	
	}

	private List<String> getRolesWithinTenant(String applicationTenantId, List<Role> roles) {

		List<String> roleCodes = roles.stream()
				.filter(role -> StringUtils.equalsIgnoreCase(role.getTenantId(), applicationTenantId)).map(role -> role.getCode())
				.collect(Collectors.toList());
		return roleCodes;
	}

	public PetCountResponse getAllcounts() {
		PetCountResponse response = new PetCountResponse();
        List<Map<String, Object>> statusList = null;
        statusList = petRegistrationRepository.getAllCounts();
        
        if (!CollectionUtils.isEmpty(statusList)) {
        	response.setCountsData(
		                statusList.stream()
		                        .filter(Objects::nonNull) // Ensure no null entries
		                        .filter(status -> StringUtils.isNotEmpty(status.toString())) // Validate non-empty entries
		                        .collect(Collectors.toList())); // Collect the filtered list
			  
			  if (statusList.get(0).containsKey("total_applications")) {
		            Object totalApplicationsObj = statusList.get(0).get("total_applications");
		            if (totalApplicationsObj instanceof Number) { // Ensure the value is a number
		            	response.setApplicationTotalCount(((Number) totalApplicationsObj).longValue());
		            } else {
		                throw new IllegalArgumentException("total_applications is not a valid number");
		            }
		        }
		}
        return response;
	}	
	
}
