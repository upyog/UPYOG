package org.egov.rl.services.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.egov.common.contract.request.RequestInfo;
import org.egov.rl.services.config.RentLeaseConfiguration;
import org.egov.rl.services.models.*;
import org.egov.rl.services.models.demand.CalculationCriteria;
import org.egov.rl.services.models.demand.CalculationReq;
import org.egov.rl.services.models.demand.DemandResponse;
import org.egov.rl.services.producer.AllotmentProducer;
import org.egov.rl.services.repository.AllotmentRepository;
import org.egov.rl.services.repository.ServiceRequestRepository;
import org.egov.rl.services.util.EncryptionDecryptionUtil;
import org.egov.rl.services.util.RLConstants;
import org.egov.rl.services.validator.AllotmentValidator;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class AllotmentService {

	@Autowired
	ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private AllotmentProducer allotmentProducer;

	@Autowired
	private RentLeaseConfiguration config;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private AllotmentEnrichmentService allotmentEnrichmentService;

	@Autowired
	private AllotmentValidator allotmentValidator;

	@Autowired
	private workflowService wfService;

	@Autowired
	EncryptionDecryptionUtil encryptionDecryptionUtil;

	@Autowired
	BoundaryService boundaryService;

	@Autowired
	UserService userService;

	@Autowired
	AllotmentRepository allotmentRepository;

	/**
	 * Enriches the Request and pushes to the Queue
	 *
	 * @param request PropertyRequest containing list of properties to be created
	 * @return List of properties successfully created
	 */

	public AllotmentDetails allotmentCreate(AllotmentRequest allotmentRequest) {

		allotmentValidator.validateAllotementRequest(allotmentRequest);
		allotmentEnrichmentService.enrichCreateRequest(allotmentRequest);
		userService.createUser(allotmentRequest);

		if (config.getIsWorkflowEnabled()) {
			wfService.updateWorkflowStatus(allotmentRequest);
		} else {
			allotmentRequest.getAllotment().get(0).setStatus(RLConstants.APPROVED);
		}
		String previousApplicationNumber = allotmentRequest.getAllotment().get(0).getPreviousApplicationNumber();
		AllotmentDetails allotment = allotmentRequest.getAllotment().get(0);
		String resolvedIncomingType = resolveApplicationType(allotment);
		if (RLConstants.APPLICATION_TYPE_LEGACY.equalsIgnoreCase(resolvedIncomingType)) {
			allotment.setApplicationType(RLConstants.APPLICATION_TYPE_LEGACY);
			allotmentRequest.setAllotment(Arrays.asList(allotment));
		} else if (previousApplicationNumber != null && previousApplicationNumber.trim().length() > 0) {
			allotment.setApplicationType(RLConstants.RENEWAL_RL_APPLICATION);
			allotmentRequest.setAllotment(Arrays.asList(allotment));
		} else {
			allotment.setApplicationType(RLConstants.NEW_RL_APPLICATION);
			allotmentRequest.setAllotment(Arrays.asList(allotment));
		}
		allotmentProducer.push(config.getSaveRLAllotmentTopic(), allotmentRequest);
	    AllotmentDetails updatedAllotment = allotmentRequest.getAllotment().get(0);
	    updatedAllotment.setWorkflow(null);
		allotmentRequest.setAllotment(Arrays.asList(updatedAllotment));
		return allotmentRequest.getAllotment().get(0);
	}

	public AllotmentDetails allotmentUpdate(AllotmentRequest allotmentRequest) {
        String action=allotmentRequest.getAllotment().get(0).getWorkflow().getAction();
		allotmentValidator.validateUpdateAllotementRequest(allotmentRequest);
		allotmentEnrichmentService.enrichUpdateRequest(allotmentRequest);
		userService.createUser(allotmentRequest);
		AllotmentDetails allotmentDetails = allotmentRequest.getAllotment().get(0);
		allotmentRequest.setAllotment(Arrays.asList(allotmentDetails));
		if (config.getIsWorkflowEnabled()) {
			wfService.updateWorkflowStatus(allotmentRequest);
		} else {
			allotmentRequest.getAllotment().get(0).setStatus(RLConstants.APPROVED);
		}
		boolean isApprove = action.contains(RLConstants.APPROVED_RL_APPLICATION);
		boolean isLegacyApplication = isLegacyApplication(allotmentDetails);
		String applicationType = resolveApplicationType(allotmentDetails);
		if (isLegacyApplication) {
			allotmentDetails.setApplicationType(RLConstants.APPLICATION_TYPE_LEGACY);
		}

		log.info("Processing update for application: {}, action: {}, isApprove: {}, isLegacy: {}, applicationType: {}", 
				allotmentDetails.getApplicationNumber(), action, isApprove, isLegacyApplication, applicationType);

		if (isApprove && isLegacyApplication) {
			// For legacy applications use the same flow as new applications but exclude security deposit and include arrear details if present
			log.info("Processing legacy application as NEW flow (security deposit excluded) for application: {}", allotmentDetails.getApplicationNumber());
			try {
				// isSatelment=false, isSecurityDeposite=false (exclude security deposit)
				callCalculatorServiceForLegacy(allotmentRequest);
			} catch (Exception e) {
				log.error("Error creating demand for legacy application: {}", allotmentDetails.getApplicationNumber(), e);
				throw new CustomException("CREATE_DEMAND_ERROR",
						"Error occurred while generating demand for legacy application.");
			}
		} else if (isApprove && applicationType != null
				&& (applicationType.contains(RLConstants.NEW_RL_APPLICATION)
						|| applicationType.contains(RLConstants.RENEWAL_RL_APPLICATION))) {
			try {
				callCalculatorService(false,true,allotmentRequest);
			} catch (Exception e) {
				e.printStackTrace();
				throw new CustomException("CREATE_DEMAND_ERROR",
						"Error occured while demand generation.");
			}
		}
		
		if(action.equalsIgnoreCase(RLConstants.FORWARD_FOR_SATELMENT_RL_APPLICATION)) {
			satelmentAllotment(allotmentRequest);
		}
		
		allotmentProducer.push(config.getUpdateRLAllotmentTopic(), allotmentRequest);
		allotmentRequest.getAllotment().get(0).setWorkflow(null);
		return allotmentRequest.getAllotment().get(0);
	}
	
	private void satelmentAllotment(AllotmentRequest allotmentRequest) {
		List<RLProperty> calculateAmount = boundaryService.allPropertyList(allotmentRequest);
		
		AllotmentDetails allotmentDetails = allotmentRequest.getAllotment().get(0);
		
		BigDecimal amountDeducted = allotmentDetails.getAmountToBeDeducted(); // BigDecimal
		
		BigDecimal securityAmount = calculateAmount.stream()
				.filter(d -> d.getPropertyId().equals(allotmentDetails.getPropertyId())).findFirst()
				.map(d -> new BigDecimal(d.getSecurityDeposit())) // BigDecimal
				.orElse(BigDecimal.ZERO);
		BigDecimal amountToBeRefunded = securityAmount.subtract(amountDeducted);
	
		if(amountToBeRefunded.compareTo(BigDecimal.ZERO) > 0) {
		    allotmentRequest.getAllotment().get(0).setAmountToBeRefund(amountToBeRefunded);
		} else {
			callCalculatorService(true,false,allotmentRequest);
		}
	}
	
	private String callCalculatorService(boolean isSatelment,boolean isSecurityDeposite,AllotmentRequest allotmentRequest) {
		CalculationReq calculationReq = getCalculationReq(isSatelment,isSecurityDeposite,allotmentRequest);

		StringBuilder url = new StringBuilder().append(config.getRlCalculatorHost())
				.append(config.getRlCalculatorEndpoint());
		Object response = serviceRequestRepository.fetchResult(url, calculationReq).get();
		DemandResponse demandResponse = mapper.convertValue(response, DemandResponse.class);
		String demandId =demandResponse.getDemands().get(0).getId();
		return demandId;
	}

    /**
     * Check if the application is a legacy application based on additionalDetails
     * @param allotmentDetails The allotment details to check
     * @return true if applicationType in additionalDetails is "Legacy"
     */
    private boolean isLegacyApplication(AllotmentDetails allotmentDetails) {
        if (allotmentDetails == null) {
            log.warn("AllotmentDetails is null, cannot determine if legacy application");
            return false;
        }
		String applicationType = resolveApplicationType(allotmentDetails);
		boolean isLegacy = RLConstants.APPLICATION_TYPE_LEGACY.equalsIgnoreCase(applicationType);
		log.info("Resolved applicationType for application {} as {}, isLegacy: {}",
				allotmentDetails.getApplicationNumber(), applicationType, isLegacy);
		return isLegacy;
    }

	private String resolveApplicationType(AllotmentDetails allotmentDetails) {
		if (allotmentDetails == null) {
			return null;
		}
		String rootType = allotmentDetails.getApplicationType();
		if (rootType != null && !rootType.trim().isEmpty()) {
			if (RLConstants.APPLICATION_TYPE_LEGACY.equalsIgnoreCase(rootType)) {
				return RLConstants.APPLICATION_TYPE_LEGACY;
			}
			return rootType;
		}
		return null;
	}

    /**
     * Call calculator service for legacy applications
     * Extracts arrear details from additionalDetails and generates demand
     * @param allotmentRequest The allotment request containing legacy application details
     * @return The demand ID generated
     */
    private String callCalculatorServiceForLegacy(AllotmentRequest allotmentRequest) {
        AllotmentDetails allotmentDetails = allotmentRequest.getAllotment().get(0);
        com.fasterxml.jackson.databind.JsonNode additionalDetails = allotmentDetails.getAdditionalDetails();

        // Extract arrear details from additionalDetails
        BigDecimal arrearAmount = BigDecimal.ZERO;
        Long arrearStartDate = null;
        Long arrearEndDate = null;

        if (additionalDetails.has(RLConstants.LEGACY_ARREAR_KEY)) {
            arrearAmount = new BigDecimal(additionalDetails.get(RLConstants.LEGACY_ARREAR_KEY).asText());
        }
        if (additionalDetails.has(RLConstants.LEGACY_ARREAR_START_DATE_KEY)) {
            arrearStartDate = additionalDetails.get(RLConstants.LEGACY_ARREAR_START_DATE_KEY).asLong();
        }
        if (additionalDetails.has(RLConstants.LEGACY_LAST_BILLING_PERIOD_KEY)) {
            arrearEndDate = additionalDetails.get(RLConstants.LEGACY_LAST_BILLING_PERIOD_KEY).asLong();
        }

        CalculationReq calculationReq = getCalculationReqForLegacy(allotmentRequest, arrearAmount, arrearStartDate, arrearEndDate);

        StringBuilder url = new StringBuilder().append(config.getRlCalculatorHost())
                .append(config.getRlCalculatorEndpoint());
        Object response = serviceRequestRepository.fetchResult(url, calculationReq).get();
        DemandResponse demandResponse = mapper.convertValue(response, DemandResponse.class);
        String demandId = demandResponse.getDemands().get(0).getId();
        return demandId;
    }

    /**
     * Build CalculationReq for legacy applications with arrear details
     */
    private CalculationReq getCalculationReqForLegacy(AllotmentRequest allotmentRequest, BigDecimal arrearAmount,
                                                      Long arrearStartDate, Long arrearEndDate) {
        CalculationReq calculationReq = new CalculationReq();
        calculationReq.setRequestInfo(allotmentRequest.getRequestInfo());
        List<CalculationCriteria> calculationCriteriaList = new ArrayList<>();
        CalculationCriteria calculationCriteria = CalculationCriteria.builder()
                .isSecurityDeposite(false)
                .isSatelment(false)
                .isLegacyArrear(true)
                .arrearAmount(arrearAmount)
                .arrearStartDate(arrearStartDate)
                .lastBillingPeriod(arrearEndDate)
                .allotmentRequest(allotmentRequest)
                .build();
        calculationCriteriaList.add(calculationCriteria);
        calculationReq.setCalculationCriteria(calculationCriteriaList);
        return calculationReq;
    }

	private CalculationReq getCalculationReq(boolean isSatelment,boolean isSecurityDeposite,AllotmentRequest allotmentRequest) {
		CalculationReq calculationReq =new CalculationReq();
		calculationReq.setRequestInfo(allotmentRequest.getRequestInfo());
		List<CalculationCriteria> calculationCriteriaList = new ArrayList<>();
		CalculationCriteria calculationCriteria =CalculationCriteria.builder()
				.isSecurityDeposite(isSecurityDeposite)
				.isSatelment(isSatelment?isSatelment:false)
				.allotmentRequest(allotmentRequest)
				.build();
		calculationCriteriaList.add(calculationCriteria);
		calculationReq.setCalculationCriteria(calculationCriteriaList);
		return calculationReq;
	}

	public List<AllotmentDetails> searchAllotedApplications(RequestInfo requestInfo,
			AllotmentCriteria allotmentCriteria) {

		if (!ObjectUtils.isEmpty(allotmentCriteria.getMobileNumber())) {
			log.info("DEBUG: Searching by mobile number: " + allotmentCriteria.getMobileNumber());

			List<String> userUuids1 = userService.getUserUuidsByMobileNumber(allotmentCriteria.getMobileNumber(),
					allotmentCriteria.getTenantId(), requestInfo);
			Set<String> userUuidSet = new HashSet<>(userUuids1);
			log.info("DEBUG: Found user UUIDs: " + userUuidSet);

			if (CollectionUtils.isEmpty(userUuidSet)) {
        
				log.info("DEBUG: No users found for mobile number, returning empty list");
				return new ArrayList<>();
			}
			allotmentCriteria.setOwnerIds(userUuidSet);
			allotmentCriteria.setMobileNumber(null);
		}
		
		List<AllotmentDetails> applications = allotmentRepository.getAllotmentSearch(allotmentCriteria);
		if (CollectionUtils.isEmpty(applications))
			return new ArrayList<>();
		allotmentEnrichmentService.enrichOwnerDetailsFromUserService(applications, requestInfo);
		return applications;
	}

}
