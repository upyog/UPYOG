package org.egov.ndc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.ndc.config.NDCConfiguration;
import org.egov.ndc.producer.Producer;
import org.egov.ndc.repository.NDCRepository;
import org.egov.ndc.repository.ServiceRequestRepository;
import org.egov.ndc.util.NDCConstants;
import org.egov.ndc.util.NDCUtil;
import org.egov.ndc.web.model.AuditDetails;
import org.egov.ndc.web.model.OwnerInfo;
import org.egov.ndc.web.model.UserResponse;
import org.egov.ndc.web.model.calculator.CalculationCriteria;
import org.egov.ndc.web.model.calculator.CalculationReq;
import org.egov.ndc.web.model.calculator.CalculationRes;
import org.egov.ndc.web.model.ndc.*;
import org.egov.ndc.web.model.workflow.SearchCriteria;
import org.egov.ndc.workflow.WorkflowIntegrator;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NDCService {

	private static final String LOG_REQUEST = "Request: {}";

	private final NDCUtil ndcUtil;
	private final UserService userService;
	private final WorkflowIntegrator workflowIntegrator;
	private final NDCConfiguration ndcConfiguration;
	private final NDCRepository ndcRepository;
	private final ObjectMapper mapper;
	private final ServiceRequestRepository serviceRequestRepository;
	private final NDCConfiguration config;
	private final Producer producer;
	private final EnrichmentService enrichmentService;

	public NDCService(NDCUtil ndcUtil, UserService userService,
			WorkflowIntegrator workflowIntegrator, NDCConfiguration ndcConfiguration, NDCRepository ndcRepository,
			ObjectMapper mapper, ServiceRequestRepository serviceRequestRepository, Producer producer,
			EnrichmentService enrichmentService) {
		this.ndcUtil = ndcUtil;
		this.userService = userService;
		this.workflowIntegrator = workflowIntegrator;
		this.ndcConfiguration = ndcConfiguration;
		this.ndcRepository = ndcRepository;
		this.mapper = mapper;
		this.serviceRequestRepository = serviceRequestRepository;
		this.config = ndcConfiguration;
		this.producer = producer;
		this.enrichmentService = enrichmentService;
	}

	public NdcApplicationRequest createNdcApplication(boolean skipWorkFlow, NdcApplicationRequest ndcApplicationRequest) {
		String userUuidFromRequestInfo = ndcApplicationRequest.getRequestInfo().getUserInfo().getUuid();
		for (Application application : ndcApplicationRequest.getApplications()) {
			initializeNewApplication(ndcApplicationRequest.getRequestInfo(), application, userUuidFromRequestInfo);
			enrichNewApplicationDetails(application);
			enrichNewApplicationDocuments(application, userUuidFromRequestInfo);
			userService.createUser(ndcApplicationRequest.getRequestInfo(), application);
			triggerWorkflowIfRequired(skipWorkFlow, ndcApplicationRequest, application);
		}

		log.info(LOG_REQUEST, ndcApplicationRequest);
		producer.push(config.getSaveTopic(), ndcApplicationRequest);
		return ndcApplicationRequest;
	}

	private void initializeNewApplication(RequestInfo requestInfo, Application application, String userUuidFromRequestInfo) {
		List<String> idList = ndcUtil.getIdList(requestInfo, application.getTenantId(), "ndc.applicationid",
				"NDC-[cy:yyyy-MM-dd]-[SEQ_EGOV_COMMON]", 1);
		log.info(idList.toString());
		application.setUuid(UUID.randomUUID().toString());
		application.setApplicationNo(idList.get(0));
		application.setAuditDetails(AuditDetails.builder().createdBy(userUuidFromRequestInfo)
				.createdTime(System.currentTimeMillis())
				.lastModifiedBy(userUuidFromRequestInfo)
				.lastModifiedTime(System.currentTimeMillis()).build());
		if (application.getActive() == null) {
			application.setActive(true);
		}
	}

	private void enrichNewApplicationDetails(Application application) {
		List<NdcDetailsRequest> ndcDetails = application.getNdcDetails();
		if (ndcDetails == null) {
			return;
		}
		String applicationId = application.getUuid();
		for (NdcDetailsRequest details : ndcDetails) {
			details.setUuid(UUID.randomUUID().toString());
			details.setApplicationId(applicationId);
		}
	}

	private void enrichNewApplicationDocuments(Application application, String userUuidFromRequestInfo) {
		List<DocumentRequest> documents = application.getDocuments();
		if (documents == null) {
			return;
		}
		String applicationId = application.getUuid();
		for (DocumentRequest document : documents) {
			if (document.getDocumentAttachment() == null) {
				throw new CustomException("DOCUMENT_ATTACHMENT_NULL", "Document attachment is null");
			}
			if (document.getUuid() == null) {
				throw new CustomException("DOCUMENT_UUID_NULL", "Document uuid is null");
			}
			document.setApplicationId(applicationId);
			document.setCreatedby(userUuidFromRequestInfo);
			document.setLastmodifiedby(userUuidFromRequestInfo);
			document.setCreatedtime(System.currentTimeMillis());
			document.setLastmodifiedtime(System.currentTimeMillis());
		}
	}

	private void triggerWorkflowIfRequired(boolean skipWorkFlow, NdcApplicationRequest ndcApplicationRequest,
			Application application) {
		if (skipWorkFlow) {
			return;
		}
		NdcApplicationRequest workflowRequest = NdcApplicationRequest.builder()
				.requestInfo(ndcApplicationRequest.getRequestInfo())
				.applications(Collections.singletonList(application))
				.build();
		workflowIntegrator.callWorkFlow(workflowRequest, NDCConstants.NDC_BUSINESS_SERVICE);
	}

	public NdcApplicationRequest updateNdcApplication(boolean skipWorkFlow, NdcApplicationRequest ndcApplicationRequest) {
		RequestInfo requestInfo = ndcApplicationRequest.getRequestInfo();
		log.info(LOG_REQUEST, ndcApplicationRequest);
		log.info(LOG_REQUEST, ndcApplicationRequest.getRequestInfo());
		String userUuidFromRequestInfo = requestInfo.getUserInfo().getUuid();
		List<Application> applications = ndcApplicationRequest.getApplications();
		if (applications == null || applications.isEmpty()) {
			throw new CustomException("APPLICATIONS_EMPTY", "No applications found in request.");
		}
		for (Application application : applications) {
			validateAndUpdateApplication(application, userUuidFromRequestInfo, requestInfo, skipWorkFlow);
		}
		producer.push(config.getUpdateTopic(), ndcApplicationRequest);
		return ndcApplicationRequest;
	}

	private void validateAndUpdateApplication(Application application, String userUuidFromRequestInfo,
			RequestInfo requestInfo, boolean skipWorkFlow) {
		if (ObjectUtils.isEmpty(application.getUuid())) {
			throw new CustomException("APPLICANT_UUID_NULL", "Applicant details or uuid is null");
		}
		if (!ndcRepository.checkApplicationExists(application.getUuid())) {
			throw new CustomException("APPLICANT_NOT_FOUND", "Applicant details or uuid is not found.");
		}

		AuditDetails auditDetails = application.getAuditDetails();
		auditDetails.setLastModifiedBy(userUuidFromRequestInfo);
		auditDetails.setLastModifiedTime(System.currentTimeMillis());
		application.setAuditDetails(auditDetails);

		updateApplicationDetails(application);
		updateApplicationDocuments(application, userUuidFromRequestInfo);

		if (application.getOwners() != null) {
			userService.createUser(requestInfo, application);
		}

		NdcApplicationRequest requestTobeUpdated = NdcApplicationRequest.builder()
				.requestInfo(requestInfo)
				.applications(Collections.singletonList(application))
				.build();
		log.info("ndc request with current applications :", requestTobeUpdated);
		if (!skipWorkFlow) {
			workflowIntegrator.callWorkFlow(requestTobeUpdated, NDCConstants.NDC_BUSINESS_SERVICE);
		}
		if (application.getWorkflow().getAction().equalsIgnoreCase("APPLY")) {
			getCalculation(requestTobeUpdated);
		}
	}

	private void updateApplicationDetails(Application application) {
		List<NdcDetailsRequest> ndcDetails = application.getNdcDetails();
		if (ndcDetails == null) {
			return;
		}
		Set<String> existingDetailUuids = getExistingUuids("eg_ndc_details",
				ndcDetails.stream().map(NdcDetailsRequest::getUuid).toList());
		for (NdcDetailsRequest details : ndcDetails) {
			if (details.getUuid() == null || !existingDetailUuids.contains(details.getUuid())) {
				details.setUuid(UUID.randomUUID().toString());
				details.setApplicationId(application.getUuid());
			}
		}
	}

	private void updateApplicationDocuments(Application application, String userUuidFromRequestInfo) {
		List<DocumentRequest> documents = application.getDocuments();
		if (documents == null) {
			return;
		}
		Set<String> existingDocumentUuids = getExistingUuids("eg_ndc_documents",
				documents.stream().map(DocumentRequest::getUuid).toList());
		for (DocumentRequest document : documents) {
			if (document.getUuid() == null) {
				throw new CustomException("DOCUMENT_ID_ERR", "Please provide a valid document id.");
			}
			if (!existingDocumentUuids.contains(document.getUuid())) {
				document.setUuid(document.getUuid());
				document.setApplicationId(application.getUuid());
				document.setCreatedby(userUuidFromRequestInfo);
				document.setCreatedtime(System.currentTimeMillis());
			}
			document.setLastmodifiedby(userUuidFromRequestInfo);
			document.setLastmodifiedtime(System.currentTimeMillis());
		}
	}

	public NdcApplicationRequest deleteNdcApplication(NdcDeleteRequest ndcDeleteRequest) {

		if(ObjectUtils.isEmpty(ndcDeleteRequest.getUuid())){
			throw new CustomException("APPLICANT_UUID_NULL", "Applicant uuid is null");
		}
		if(ObjectUtils.isEmpty(ndcDeleteRequest.getTenantId())){
			throw new CustomException("APPLICANT_TENANT_NULL", "Applicant tenantId is null");
		}

		if(!ndcRepository.checkApplicationExists(ndcDeleteRequest.getUuid())) {
			throw new CustomException("APPLICANT_NOT_FOUND", "Applicant uuid not found.");
		}
		Application application = searchNdcApplications(NdcApplicationSearchCriteria.builder().tenantId(ndcDeleteRequest.getTenantId()).uuid(Collections.singletonList(ndcDeleteRequest.getUuid())).build(), ndcDeleteRequest.getRequestInfo()).get(0);
		NdcApplicationRequest ndcApplicationRequest = NdcApplicationRequest.builder().requestInfo(ndcDeleteRequest.getRequestInfo()).build();
		AuditDetails auditDetails = application.getAuditDetails();
		auditDetails.setLastModifiedBy(ndcDeleteRequest.getRequestInfo().getUserInfo().getUuid());
		auditDetails.setLastModifiedTime(System.currentTimeMillis());
		application.setAuditDetails(auditDetails);
		application.setActive(ndcDeleteRequest.getActive());
		ndcApplicationRequest.setApplications(Collections.singletonList(application));

		producer.push(config.getDeleteTopic(), application);
		return ndcApplicationRequest;
	}


	private Set<String> getExistingUuids(String tableName, List<String> uuids) {
		if (uuids == null || uuids.isEmpty()) {
			return new HashSet<>();
		}
		return ndcRepository.getExistingUuids(tableName, uuids);
	}

	public List<Application> searchNdcApplications(NdcApplicationSearchCriteria criteria, RequestInfo requestInfo) {
		if (StringUtils.isBlank(criteria.getTenantId())) {
			throw new CustomException("EG_NDC_TENANT_ID_NULL", "Tenant ID must not be null");
		}

		boolean isSpecificLookup = !CollectionUtils.isEmpty(criteria.getApplicationNo())
				|| !CollectionUtils.isEmpty(criteria.getUuid()) || criteria.getMobileNumber()!=null || criteria.getName()!=null;
		if (!isSpecificLookup) {
			criteria.setCreatedBy(requestInfo.getUserInfo().getUuid());
			criteria.setMobileNumber(requestInfo.getUserInfo().getMobileNumber());
		}
		if (criteria.getMobileNumber() != null || criteria.getName() != null) {
			getApplicationsWhenMobileNumberGiven(criteria, requestInfo);
		}
		List<Application> applications = getApplicationsWithOwnerInfo(criteria, requestInfo);

		SearchCriteria searchCriteria = new SearchCriteria();
		searchCriteria.setTenantId(criteria.getTenantId());
		enrichmentService.enrichProcessInstance(applications, searchCriteria, requestInfo);
		return applications;
	}

	private void getApplicationsWhenMobileNumberGiven(NdcApplicationSearchCriteria criteria, RequestInfo requestInfo) {
		UserResponse userDetailResponse = userService.getUser(criteria, requestInfo);
		if (userDetailResponse.getUser().isEmpty()) {
			return;
		}
		criteria.setOwnerIds(userDetailResponse.getUser().stream().map(OwnerInfo::getUuid).collect(Collectors.toSet()));
	}

	public List<Application> getApplicationsWithOwnerInfo(NdcApplicationSearchCriteria criteria, RequestInfo requestInfo) {
		List<Application> applications = ndcRepository.fetchNdcApplications(criteria);
		if (CollectionUtils.isEmpty(applications))
			return Collections.emptyList();
		enrichmentService.enrichApplicationCriteriaWithOwnerids(criteria, applications);
		UserResponse userDetailResponse = null;
		if (!CollectionUtils.isEmpty(criteria.getOwnerIds())) {
			userDetailResponse = userService.getUser(criteria, requestInfo);
			enrichmentService.enrichOwner(userDetailResponse, applications);
		}

		return applications;
	}

	public void getCalculation(NdcApplicationRequest request){

		List<CalculationCriteria> calculationCriteriaList = new ArrayList<>();


		List<NdcDetailsRequest> ndcDetails = request
				.getApplications().get(0).getNdcDetails();


		String propertyType = null;
		for (NdcDetailsRequest detail : ndcDetails) {
			if (NDCConstants.PROPERTY_BUSINESSSERVICE.equalsIgnoreCase(detail.getBusinessService())) {
				JsonNode additionalDetails = detail.getAdditionalDetails();
				if (additionalDetails != null && additionalDetails.has(NDCConstants.ADDITIONAL_DETAILS_FEE_TYPE_PARAM)) {
					propertyType = additionalDetails.get(NDCConstants.ADDITIONAL_DETAILS_FEE_TYPE_PARAM).asText();
					break;
				}
			}
		}

		if (propertyType == null) {
			throw new CustomException("FEE_TYPE_MISSING", "Property type missing in additionalDetails");
		}

		String mappedFeeType = NDCConstants.RESIDENTIAL.equalsIgnoreCase(propertyType)
				? NDCConstants.RESIDENTIAL
				: NDCConstants.COMMERCIAL;

		CalculationCriteria calculationCriteria = CalculationCriteria.builder()
				.ndcApplicationRequest(request)
				.propertyType(mappedFeeType)
				.tenantId(request.getApplications().get(0).getTenantId())
				.applicationNumber(request.getApplications().get(0).getApplicationNo())
				.build();
		calculationCriteriaList.add(calculationCriteria);

		CalculationReq calculationReq = CalculationReq.builder()
				.requestInfo(request.getRequestInfo())
				.calculationCriteria(calculationCriteriaList)
				.build();

		StringBuilder url = new StringBuilder().append(ndcConfiguration.getNdcCalculatorHost())
				.append(ndcConfiguration.getNdcCalculatorEndpoint());
		Object response = serviceRequestRepository.fetchResult(url, calculationReq);
		CalculationRes calculationRes = mapper.convertValue(response, CalculationRes.class);
		log.info("Calculation Response: " + calculationRes);
	}

}
