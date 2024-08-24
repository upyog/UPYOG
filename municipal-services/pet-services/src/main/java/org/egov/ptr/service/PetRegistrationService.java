package org.egov.ptr.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.ptr.config.PetConfiguration;
import org.egov.ptr.models.Demand;
import org.egov.ptr.models.PetApplicationSearchCriteria;
import org.egov.ptr.models.PetRegistrationApplication;
import org.egov.ptr.models.PetRegistrationRequest;
import org.egov.ptr.models.collection.BillResponse;
import org.egov.ptr.models.collection.GenerateBillCriteria;
import org.egov.ptr.producer.Producer;
import org.egov.ptr.repository.PetRegistrationRepository;
import org.egov.ptr.util.PTRConstants;
import org.egov.ptr.validator.PetApplicationValidator;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

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
			
			List<String> listOfStatus = getAccountStatusListByRoles(petApplicationSearchCriteria.getTenantId(), requestInfo.getUserInfo().getRoles());
			if(CollectionUtils.isEmpty(listOfStatus)) {
				throw new CustomException("SEARCH_ACCOUNT_BY_ROLES","Search can't be performed by this Employee due to lack of roles.");
			}
			petApplicationSearchCriteria.setStatus(listOfStatus);
		}
		
		
		
	}
	
	private List<String> getAccountStatusListByRoles(String tenantId, List<Role> roles) {
		
		List<String> rolesWithinTenant = getRolesByTenantId(tenantId, roles);	
		Set<String> statusWithRoles = new HashSet();
		
		rolesWithinTenant.stream().forEach(role -> {
			
			if(StringUtils.equalsIgnoreCase(role, PTRConstants.USER_ROLE_PTR_VERIFIER)) {
				statusWithRoles.add(PTRConstants.APPLICATION_STATUS_PENDINGFORVERIFICATION);
			}else if(StringUtils.equalsIgnoreCase(role, PTRConstants.USER_ROLE_PTR_APPROVER)) {
				statusWithRoles.add(PTRConstants.APPLICATION_STATUS_PENDINGFORAPPROVAL);
			}
			
		});
		
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
		existingApplication.setWorkflow(petRegistrationRequest.getPetRegistrationApplications().get(0).getWorkflow());
		existingApplication.setIsOnlyWorkflowCall(petRegistrationRequest.getPetRegistrationApplications().get(0).getIsOnlyWorkflowCall());
		
		if(BooleanUtils.isTrue(petRegistrationRequest.getPetRegistrationApplications().get(0).getIsOnlyWorkflowCall())
				&& null != petRegistrationRequest.getPetRegistrationApplications().get(0).getWorkflow()) {
			petRegistrationRequest.setPetRegistrationApplications(Collections.singletonList(existingApplication));	
		}
		
//		petRegistrationRequest.setPetRegistrationApplications(Collections.singletonList(existingApplication));

		enrichmentService.enrichPetApplicationUponUpdate(petRegistrationRequest);

		if (petRegistrationRequest.getPetRegistrationApplications().get(0).getWorkflow().getAction()
				.equals("APPROVE")) {
			
			// create demands
			List<Demand> savedDemands = demandService.createDemand(petRegistrationRequest);

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
		wfService.updateWorkflowStatus(petRegistrationRequest);
		producer.push(config.getUpdatePtrTopic(), petRegistrationRequest);

		return petRegistrationRequest.getPetRegistrationApplications().get(0);
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

}
