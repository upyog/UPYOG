package org.egov.echallan.service;

import java.math.BigDecimal;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.echallan.config.ChallanConfiguration;
import org.egov.echallan.enums.ChallanStatusEnum;
import org.egov.echallan.model.Amount;
import org.egov.echallan.model.AuditDetails;
import org.egov.echallan.model.Challan;
import org.egov.echallan.model.ChallanRequest;
import org.egov.echallan.model.SearchCriteria;
import org.egov.echallan.repository.ChallanRepository;
import org.egov.echallan.util.ChallanConstants;
import org.egov.echallan.util.CommonUtils;
import org.egov.echallan.util.ResponseInfoFactory;
import org.egov.echallan.validator.ChallanValidator;
import org.egov.echallan.web.models.user.UserDetailResponse;
import org.egov.echallan.workflow.WorkflowIntegrator;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


@Service
@Slf4j
public class ChallanService {

    private static final String CHALLAN_FINE_TAX_HEAD = "CH.CHALLAN_FINE";
    private static final String FEE_WAIVER_KEY = "feeWaiver";

    private final EnrichmentService enrichmentService;
    private final ResponseInfoFactory responseInfoFactory;
    private final UserService userService;
    private final ChallanRepository repository;
    private final CalculationService calculationService;
    private final ChallanValidator validator;
    private final CommonUtils utils;
    private final ChallanConfiguration config;
    private final org.egov.echallan.repository.ServiceRequestRepository serviceRequestRepository;
    private final WorkflowIntegrator workflowIntegrator;

    @Autowired
    public ChallanService(EnrichmentService enrichmentService, ResponseInfoFactory responseInfoFactory,
            UserService userService, ChallanRepository repository, CalculationService calculationService,
            ChallanValidator validator, CommonUtils utils, ChallanConfiguration config,
            org.egov.echallan.repository.ServiceRequestRepository serviceRequestRepository,
            @Autowired(required = false) WorkflowIntegrator workflowIntegrator) {
        this.enrichmentService = enrichmentService;
        this.responseInfoFactory = responseInfoFactory;
        this.userService = userService;
        this.repository = repository;
        this.calculationService = calculationService;
        this.validator = validator;
        this.utils = utils;
        this.config = config;
        this.serviceRequestRepository = serviceRequestRepository;
        this.workflowIntegrator = workflowIntegrator;
    }


	/**
	 * Enriches the Request and pushes to the Queue
	 *
	 * @param request ChallanRequest containing list of challans to be created
	 * @return Challan successfully created
	 */
	public Challan create(ChallanRequest request) {
		Object mdmsData = utils.mDMSCall(request);
		Challan challan = request.getChallan();

		setDefaultBusinessService(challan);
		populateTaxPeriod(challan, mdmsData);
		populateAmountFromMDMS(challan, mdmsData);

		validator.validateFields(request, mdmsData);
		enrichmentService.enrichCreateRequest(request);
		userService.createUser(request);

		if (challan.getCitizen() != null && challan.getCitizen().getUuid() != null) {
			challan.setAccountId(challan.getCitizen().getUuid());
		}

		ensureAdditionalDetailNotNull(challan);
		copyAmountToAdditionalDetail(challan);
		extractAndStoreLocationFromDocuments(challan);

		challan.setChallanStatus(ChallanStatusEnum.CHALLAN_CREATED.toString());
		applyWorkflowOnCreate(request, challan);
		applyCalculationOnSubmit(request, challan);
		moveFieldsToAdditionalDetail(challan);

		repository.save(request);
		return request.getChallan();
	}

	private void setDefaultBusinessService(Challan challan) {
		if (StringUtils.isBlank(challan.getBusinessService())) {
			challan.setBusinessService("Challan_Generation");
		}
	}

	private void populateTaxPeriod(Challan challan, Object mdmsData) {
		if (challan.getTaxPeriodFrom() != null && challan.getTaxPeriodTo() != null) {
			return;
		}
		Map<String, Object> taxPeriodDetails = utils.fetchTaxPeriodFromMDMS(mdmsData, challan.getBusinessService());
		if (taxPeriodDetails.isEmpty()) {
			return;
		}
		if (challan.getTaxPeriodFrom() == null) {
			challan.setTaxPeriodFrom((Long) taxPeriodDetails.get("fromDate"));
		}
		if (challan.getTaxPeriodTo() == null) {
			challan.setTaxPeriodTo((Long) taxPeriodDetails.get("toDate"));
		}
	}

	private void populateAmountFromMDMS(Challan challan, Object mdmsData) {
		if (StringUtils.isBlank(challan.getOffenceTypeName())) {
			return;
		}
		BigDecimal amountFromMDMS = utils.fetchAmountFromOffenceTypeName(mdmsData, challan.getOffenceTypeName());
		if (amountFromMDMS == null) {
			return;
		}
		String taxHeadCode = CHALLAN_FINE_TAX_HEAD;
		String taxHeadCodeFromMDMS = utils.fetchTaxHeadCodeFromOffenceTypeName(mdmsData, challan.getOffenceTypeName());
		if (StringUtils.isNotBlank(taxHeadCodeFromMDMS)) {
			taxHeadCode = taxHeadCodeFromMDMS;
		}
		Amount amountObject = Amount.builder()
			.taxHeadCode(taxHeadCode)
			.amount(amountFromMDMS)
			.build();
		challan.setAmount(Arrays.asList(amountObject));
		if (challan.getChallanAmount() == null) {
			challan.setChallanAmount(amountFromMDMS);
		}
	}

	private void ensureAdditionalDetailNotNull(Challan challan) {
		if (challan.getAdditionalDetail() == null) {
			challan.setAdditionalDetail(new HashMap<>());
		}
	}

	private void copyAmountToAdditionalDetail(Challan challan) {
		if (challan.getAmount() == null || challan.getAmount().isEmpty()) {
			return;
		}
		try {
			for (Amount amount : challan.getAmount()) {
				if (StringUtils.isBlank(amount.getTaxHeadCode())) {
					amount.setTaxHeadCode(CHALLAN_FINE_TAX_HEAD);
				}
			}
			Map<String, Object> additionalDetailMap = toAdditionalDetailMap(challan.getAdditionalDetail());
			ObjectMapper mapper = new ObjectMapper();
			List<Map<String, Object>> amountList = new ArrayList<>();
			for (Amount amount : challan.getAmount()) {
				@SuppressWarnings("unchecked")
				Map<String, Object> amountMap = mapper.convertValue(amount, Map.class);
				amountList.add(amountMap);
			}
			additionalDetailMap.put("amount", amountList);
			challan.setAdditionalDetail(additionalDetailMap);
		} catch (Exception e) {
			log.warn("Failed to copy amount to additionalDetail for challan {}: {}",
				challan.getChallanNo(), e.getMessage());
		}
	}

	private void applyWorkflowOnCreate(ChallanRequest request, Challan challan) {
		boolean workflowEnabled = !Boolean.FALSE.equals(config.getIsExternalWorkFlowEnabled());
		if (!workflowEnabled) {
			log.info("Workflow is disabled, setting status to CHALLAN_GENERATED");
			challan.setChallanStatus(ChallanStatusEnum.CHALLAN_GENERATED.toString());
			return;
		}
		try {
			if (workflowIntegrator != null
					&& challan.getWorkflow() != null
					&& StringUtils.isNotBlank(challan.getWorkflow().getAction())) {
				log.info("Calling workflow for challan {} with action: {} (user role: {})",
					challan.getChallanNo(),
					challan.getWorkflow().getAction(),
					request.getRequestInfo().getUserInfo().getRoles().get(0).getCode());
				String nextStatus = workflowIntegrator.transition(
					request.getRequestInfo(),
					challan,
					challan.getWorkflow().getAction()
				);
				if (StringUtils.isNotBlank(nextStatus)) {
					challan.setChallanStatus(nextStatus);
					log.info("Workflow set status to: {}", nextStatus);
				} else {
					log.warn("Workflow didn't return status. Setting to CHALLAN_GENERATED");
					challan.setChallanStatus(ChallanStatusEnum.CHALLAN_GENERATED.toString());
				}
			} else {
				log.info("No workflow action provided, setting status to CHALLAN_GENERATED");
				challan.setChallanStatus(ChallanStatusEnum.CHALLAN_GENERATED.toString());
			}
		} catch (Exception ex) {
			log.error("Workflow transition on create failed for challan {} with error: {}. Setting status to CHALLAN_GENERATED and continuing.",
				challan.getChallanNo(), ex.getMessage());
			challan.setChallanStatus(ChallanStatusEnum.CHALLAN_GENERATED.toString());
		}
	}

	private void applyCalculationOnSubmit(ChallanRequest request, Challan challan) {
		if (challan.getWorkflow() == null
				|| StringUtils.isBlank(challan.getWorkflow().getAction())
				|| !challan.getWorkflow().getAction().equalsIgnoreCase(ChallanConstants.SUBMIT)) {
			return;
		}
		log.info("SUBMIT action detected during create, calling calculation service for challan: {}",
			challan.getChallanNo());
		try {
			calculationService.addCalculation(request);
			storeDemandIdFromBill(request);
			log.info("Calculation completed successfully for challan: {}", challan.getChallanNo());
		} catch (Exception ex) {
			log.error("Calculation service failed for challan {} with error: {}. Continuing with challan creation.",
				challan.getChallanNo(), ex.getMessage());
		}
	}


	 public List<Challan> search(SearchCriteria criteria, RequestInfo requestInfo){
	        enrichmentService.enrichSearchCriteriaWithAccountId(requestInfo,criteria);
	         if(criteria.getMobileNumber()!=null){
	        	 return getChallansFromMobileNumber(criteria,requestInfo);
	         }
	         return getChallansWithOwnerInfo(criteria,requestInfo);
	    }
	 public List<Challan> getChallansFromMobileNumber(SearchCriteria criteria, RequestInfo requestInfo){
	        UserDetailResponse userDetailResponse = userService.getUser(criteria,requestInfo);
	        if(CollectionUtils.isEmpty(userDetailResponse.getUser())){
	            return Collections.emptyList();
	        }
	        enrichmentService.enrichSearchCriteriaWithOwnerids(criteria,userDetailResponse);
	        List<Challan> challans = repository.getChallans(criteria);

	        if(CollectionUtils.isEmpty(challans)){
	            return Collections.emptyList();
	        }

	        criteria=enrichmentService.getChallanCriteriaFromIds(challans);
	        return getChallansWithOwnerInfo(criteria,requestInfo);
	    }

	 public List<Challan> getChallansWithOwnerInfo(SearchCriteria criteria,RequestInfo requestInfo){
	        List<Challan> challans = repository.getChallans(criteria);
	        if(challans.isEmpty())
	            return Collections.emptyList();
	        return enrichmentService.enrichChallanSearch(challans,criteria,requestInfo);
	    }

	/**
	 * gets the total count for a search request
	 *
	 * @param criteria The echallan search criteria
	 * @param requestInfo requestInfo
	 */
	public int countForSearch(SearchCriteria criteria, RequestInfo requestInfo){
		enrichmentService.enrichSearchCriteriaWithAccountId(requestInfo,criteria);
		if(criteria.getMobileNumber()!=null){
			return getCountOfChallansFromMobileNumber(criteria,requestInfo);
		}
		return getCountOfChallansWithOwnerInfo(criteria);
	}

	public int getCountOfChallansFromMobileNumber(SearchCriteria criteria, RequestInfo requestInfo){
		UserDetailResponse userDetailResponse = userService.getUser(criteria,requestInfo);
		if(CollectionUtils.isEmpty(userDetailResponse.getUser())){
			return 0;
		}
		enrichmentService.enrichSearchCriteriaWithOwnerids(criteria,userDetailResponse);
		return repository.getChallanSearchCount(criteria);
	}

	public int getCountOfChallansWithOwnerInfo(SearchCriteria criteria){
		return repository.getChallanSearchCount(criteria);
	}
	 public List<Challan> searchChallans(ChallanRequest request){
	        SearchCriteria criteria = new SearchCriteria();
	        List<String> ids = new LinkedList<>();
	        ids.add(request.getChallan().getId());

	        criteria.setTenantId(request.getChallan().getTenantId());
	        criteria.setIds(ids);
	        criteria.setBusinessService(request.getChallan().getBusinessService());

	        List<Challan> challans = repository.getChallans(criteria);

	        if(challans.isEmpty())
	            return Collections.emptyList();
	        return enrichmentService.enrichChallanSearch(challans,criteria,request.getRequestInfo());
	    }

	 public Challan update(ChallanRequest request) {
		 Object mdmsData = utils.mDMSCall(request);
		 validator.validateFields(request, mdmsData);
		List<Challan> searchResult = searchChallans(request);
		validator.validateUpdateRequest(request,searchResult);

		 Challan challan = request.getChallan();
		 Challan existingChallan = preserveCitizenFromExisting(request, searchResult);

		 AuditDetails existingAuditDetails = existingChallan != null ? existingChallan.getAuditDetails() : null;
		 enrichmentService.enrichUpdateRequest(request, existingAuditDetails);

		 copyAmountToAdditionalDetail(challan);
		 extractAndStoreLocationFromDocuments(challan);
		 applyWorkflowOnUpdate(request, searchResult);
		 moveFieldsToAdditionalDetail(challan);

		 repository.update(request);
		 return request.getChallan();
		}

	private Challan preserveCitizenFromExisting(ChallanRequest request, List<Challan> searchResult) {
		Challan challan = request.getChallan();
		if (searchResult == null || searchResult.isEmpty()) {
			return null;
		}
		Challan existingChallan = searchResult.get(0);
		if (existingChallan.getCitizen() != null) {
			challan.setCitizen(existingChallan.getCitizen());
			log.info("Preserved original citizen data for challan: {}", challan.getChallanNo());
		}
		return existingChallan;
	}

	private void applyWorkflowOnUpdate(ChallanRequest request, List<Challan> searchResult) {
		try {
			if (workflowIntegrator == null
					|| request.getChallan().getWorkflow() == null
					|| StringUtils.isBlank(request.getChallan().getWorkflow().getAction())) {
				return;
			}
			String action = request.getChallan().getWorkflow().getAction();
			String nextStatus = workflowIntegrator.transition(request.getRequestInfo(),
					request.getChallan(),
					action);

			if (action.equalsIgnoreCase(ChallanConstants.SUBMIT)) {
				calculationService.addCalculation(request);
				storeDemandIdFromBill(request);
			}
			if (action.equalsIgnoreCase(ChallanConstants.ACTION_SETTLED)) {
				handleSettledAction(request, searchResult);
			}
			if (StringUtils.isNotBlank(nextStatus)) {
				request.getChallan().setChallanStatus(nextStatus);
			}
		} catch (Exception ex) {
			log.error("Workflow transition on update failed for challan {}", request.getChallan().getChallanNo(), ex);
		}
	}

	public Map<String,Object>  getChallanCountResponse(RequestInfo requestInfo, String tenantId){
		 validator.validateChallanCountRequest(tenantId);

		 Map<String,Object> response = new HashMap<>();
		 ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfo, true);

		 response.put("ResponseInfo",responseInfo);
		 Map<String,String> results = repository.fetchChallanCount(tenantId);

		 if(CollectionUtils.isEmpty(results) || results.get("totalChallan").equalsIgnoreCase("0"))
			 throw new CustomException("NO_RECORDS","No records found for the tenantId: "+tenantId);

		 response.put("ChallanCount",results);
		 return  response;
	 }


	public Map<String, Integer> getDynamicData(String tenantId) {
		return repository.fetchDynamicData(tenantId);
	}

	public int getChallanValidity() {
		return Integer.valueOf(config.getChallanValidity());
	}

	/**
	 * Extracts latitude and longitude from documents and stores them in additionalDetail
	 *
	 * @param challan The challan object containing documents
	 */
	private void extractAndStoreLocationFromDocuments(Challan challan) {
		if (challan.getUploadedDocumentDetails() == null || challan.getUploadedDocumentDetails().isEmpty()) {
			return;
		}
		try {
			Optional<double[]> location = findLocationFromDocuments(challan);
			if (location.isEmpty()) {
				return;
			}
			double[] coords = location.get();
			ensureAdditionalDetailNotNull(challan);
			Map<String, Object> additionalDetailMap = toAdditionalDetailMap(challan.getAdditionalDetail());
			additionalDetailMap.put("latitude", coords[0]);
			additionalDetailMap.put("longitude", coords[1]);
			challan.setAdditionalDetail(additionalDetailMap);
			log.info("Stored latitude: {} and longitude: {} in additionalDetail for challan {}",
				coords[0], coords[1], challan.getChallanNo());
		} catch (Exception e) {
			log.warn("Failed to extract and store location from documents for challan {}: {}",
				challan.getChallanNo(), e.getMessage());
		}
	}

	private Optional<double[]> findLocationFromDocuments(Challan challan) {
		for (org.egov.echallan.model.DocumentDetail document : challan.getUploadedDocumentDetails()) {
			if (document.getLatitude() != null && document.getLongitude() != null) {
				return Optional.of(new double[] { document.getLatitude(), document.getLongitude() });
			}
		}
		return Optional.empty();
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> toAdditionalDetailMap(Object additionalDetail) {
		ObjectMapper mapper = new ObjectMapper();
		if (additionalDetail instanceof Map<?, ?> map) {
			return (Map<String, Object>) map;
		}
		Map<String, Object> convertedMap = mapper.convertValue(additionalDetail, Map.class);
		return convertedMap != null ? convertedMap : new HashMap<>();
	}

	/**
	 * Handles the settled action by updating existing demand with fee waiver
	 * Calculator will find demand using consumerCode and update it
	 *
	 * @param request ChallanRequest with settled action
	 * @param searchResult Existing challan from database
	 */
	private void handleSettledAction(ChallanRequest request, List<Challan> searchResult) {
		if (searchResult == null || searchResult.isEmpty()) {
			log.error("Cannot process settled action: Existing challan not found");
			throw new CustomException("CHALLAN_NOT_FOUND", "Existing challan not found for settlement");
		}

		Challan challan = request.getChallan();
		Challan existingChallan = searchResult.get(0);

		try {
			BigDecimal feeWaiverAmount = extractFeeWaiverFromRequest(challan);
			if (feeWaiverAmount == null || feeWaiverAmount.compareTo(BigDecimal.ZERO) <= 0) {
				throw new CustomException("INVALID_FEE_WAIVER", "Fee waiver amount must be provided and greater than zero");
			}

			String demandId = getDemandIdFromChallan(existingChallan, request.getRequestInfo());
			storeFeeWaiverInAdditionalDetail(challan, feeWaiverAmount);

			log.info("Updating demand for challan {} with fee waiver: {} (demandId: {})",
				challan.getChallanNo(), feeWaiverAmount, demandId != null ? demandId : "will be found by consumerCode");

			calculationService.updateCalculation(request, demandId);

			log.info("Successfully updated demand for challan {} with fee waiver",
				challan.getChallanNo());

		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			log.error("Error processing settled action for challan {}: {}",
				challan.getChallanNo(), e.getMessage(), e);
			throw new CustomException("SETTLED_ACTION_ERROR",
				"Failed to process settled action: " + e.getMessage());
		}
	}

	/**
	 * Extracts fee waiver amount from challan - checks root field first, then additionalDetail
	 *
	 * @param challan Challan object
	 * @return Fee waiver amount or null if not found
	 */
	private BigDecimal extractFeeWaiverFromRequest(Challan challan) {
		if (challan.getFeeWaiver() != null) {
			return challan.getFeeWaiver();
		}
		if (challan.getAdditionalDetail() == null) {
			return null;
		}
		try {
			Map<String, Object> additionalDetailMap = toAdditionalDetailMap(challan.getAdditionalDetail());
			Object feeWaiverObj = additionalDetailMap.get(FEE_WAIVER_KEY);
			if (feeWaiverObj == null) {
				return null;
			}
			if (feeWaiverObj instanceof BigDecimal bigDecimal) {
				return bigDecimal;
			}
			if (feeWaiverObj instanceof Number number) {
				return BigDecimal.valueOf(number.doubleValue());
			}
			if (feeWaiverObj instanceof String str) {
				return new BigDecimal(str);
			}
		} catch (Exception e) {
			log.warn("Failed to extract fee waiver from additionalDetail for challan {}: {}",
				challan.getChallanNo(), e.getMessage());
		}
		return null;
	}

	/**
	 * Retrieves demand ID from existing challan's additionalDetail or bill
	 *
	 * @param challan Existing challan from database
	 * @param requestInfo RequestInfo for bill service call
	 * @return Demand ID or null if not found
	 */
	private String getDemandIdFromChallan(Challan challan, RequestInfo requestInfo) {
		String demandId = extractDemandIdFromAdditionalDetail(challan);
		if (demandId != null) {
			return demandId;
		}
		return fetchAndStoreDemandIdFromBill(challan, requestInfo);
	}

	private String extractDemandIdFromAdditionalDetail(Challan challan) {
		if (challan.getAdditionalDetail() == null) {
			return null;
		}
		try {
			Map<String, Object> additionalDetailMap = toAdditionalDetailMap(challan.getAdditionalDetail());
			Object demandIdObj = additionalDetailMap.get("demandId");
			if (demandIdObj != null && StringUtils.isNotBlank(demandIdObj.toString())) {
				return demandIdObj.toString();
			}
		} catch (Exception e) {
			log.warn("Failed to extract demandId from additionalDetail for challan {}: {}",
				challan.getChallanNo(), e.getMessage());
		}
		return null;
	}

	private String fetchAndStoreDemandIdFromBill(Challan challan, RequestInfo requestInfo) {
		if (requestInfo == null) {
			return null;
		}
		try {
			String demandId = getDemandIdFromBill(challan, requestInfo);
			if (StringUtils.isNotBlank(demandId)) {
				storeDemandIdInAdditionalDetail(challan, demandId);
				return demandId;
			}
		} catch (Exception e) {
			log.warn("Failed to get demandId from bill for challan {}: {}",
				challan.getChallanNo(), e.getMessage());
		}
		return null;
	}

	/**
	 * Retrieves demand ID from bill service
	 *
	 * @param challan Challan object
	 * @param requestInfo RequestInfo object
	 * @return Demand ID or null if not found
	 */
	private String getDemandIdFromBill(Challan challan, RequestInfo requestInfo) {
		try {
			StringBuilder billUri = new StringBuilder();
			billUri.append(config.getBillingHost());
			billUri.append(config.getFetchBillEndpoint());
			billUri.append("?tenantId=").append(challan.getTenantId());
			billUri.append("&consumerCode=").append(challan.getChallanNo());
			billUri.append("&businessService=").append(challan.getBusinessService());

			org.egov.echallan.model.RequestInfoWrapper requestInfoWrapper =
				new org.egov.echallan.model.RequestInfoWrapper(requestInfo);

			Object billResponse = serviceRequestRepository.fetchResult(billUri, requestInfoWrapper);

			if (billResponse != null) {
				com.jayway.jsonpath.JsonPath jsonPath = com.jayway.jsonpath.JsonPath.compile("$.Bill[0].billDetails[0].demandId");
				Object demandIdObj = jsonPath.read(billResponse);
				if (demandIdObj != null) {
					return demandIdObj.toString();
				}
			}
		} catch (Exception e) {
			log.warn("Failed to fetch demandId from bill for challan {}: {}",
				challan.getChallanNo(), e.getMessage());
		}
		return null;
	}

	/**
	 * Stores demandId in challan's additionalDetail
	 *
	 * @param challan Challan object
	 * @param demandId Demand ID to store
	 */
	private void storeDemandIdInAdditionalDetail(Challan challan, String demandId) {
		try {
			Map<String, Object> additionalDetailMap = toAdditionalDetailMap(challan.getAdditionalDetail());
			additionalDetailMap.put("demandId", demandId);
			challan.setAdditionalDetail(additionalDetailMap);
		} catch (Exception e) {
			log.warn("Failed to store demandId in additionalDetail for challan {}: {}",
				challan.getChallanNo(), e.getMessage());
		}
	}

	/**
	 * Stores demandId from bill after calculation is done
	 *
	 * @param request ChallanRequest
	 */
	private void storeDemandIdFromBill(ChallanRequest request) {
		try {
			Challan challan = request.getChallan();
			String demandId = getDemandIdFromBill(challan, request.getRequestInfo());
			if (StringUtils.isNotBlank(demandId)) {
				storeDemandIdInAdditionalDetail(challan, demandId);
				log.info("Stored demandId {} for challan {}", demandId, challan.getChallanNo());
			} else {
				log.warn("Could not retrieve demandId from bill for challan {}", challan.getChallanNo());
			}
		} catch (Exception e) {
			log.warn("Failed to store demandId from bill for challan {}: {}",
				request.getChallan().getChallanNo(), e.getMessage());
		}
	}

	/**
	 * Stores fee waiver in additionalDetail for calculator service
	 *
	 * @param challan Challan object
	 * @param feeWaiverAmount Fee waiver amount
	 */
	private void storeFeeWaiverInAdditionalDetail(Challan challan, BigDecimal feeWaiverAmount) {
		try {
			Map<String, Object> additionalDetailMap = toAdditionalDetailMap(challan.getAdditionalDetail());
			additionalDetailMap.put(FEE_WAIVER_KEY, feeWaiverAmount);
			challan.setAdditionalDetail(additionalDetailMap);
		} catch (Exception e) {
			log.warn("Failed to store fee waiver in additionalDetail for challan {}: {}",
				challan.getChallanNo(), e.getMessage());
		}
	}

	/**
	 * Moves feeWaiver and calculation from root object to additionalDetail before saving to DB
	 * This ensures these fields are stored in additionalDetail JSONB column
	 *
	 * @param challan Challan object
	 */
	private void moveFieldsToAdditionalDetail(Challan challan) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> additionalDetailMap = toAdditionalDetailMap(challan.getAdditionalDetail());

			if (challan.getFeeWaiver() != null) {
				additionalDetailMap.put(FEE_WAIVER_KEY, challan.getFeeWaiver());
			}

			if (challan.getCalculation() != null) {
				@SuppressWarnings("unchecked")
				Map<String, Object> calculationMap = mapper.convertValue(challan.getCalculation(), Map.class);
				additionalDetailMap.put("calculation", calculationMap);
			}

			challan.setAdditionalDetail(additionalDetailMap);

		} catch (Exception e) {
			log.warn("Failed to move fields to additionalDetail for challan {}: {}",
				challan.getChallanNo(), e.getMessage());
		}
	}


}
