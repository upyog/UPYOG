package org.egov.tl.service;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.repository.TLRepository;
import org.egov.tl.service.notification.EditNotificationService;
import org.egov.tl.util.TLConstants;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.validator.TLValidator;
import org.egov.tl.web.models.*;
import org.egov.tl.web.models.user.UserDetailResponse;
import org.egov.tl.web.models.workflow.BusinessService;
import org.egov.tl.workflow.ActionValidator;
import org.egov.tl.workflow.TLWorkflowService;
import org.egov.tl.workflow.WorkflowIntegrator;
import org.egov.tl.workflow.WorkflowService;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static org.egov.tl.util.TLConstants.*;
import static org.egov.tracer.http.HttpUtils.isInterServiceCall;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.JsonPath;

@Service
@Slf4j
public class TradeLicenseService {
	
	private WorkflowIntegrator wfIntegrator;

    private EnrichmentService enrichmentService;

    private UserService userService;

    private TLRepository repository;

    private ActionValidator actionValidator;

    private TLValidator tlValidator;

    private TLWorkflowService TLWorkflowService;

    private CalculationService calculationService;

    private TradeUtil util;

    private DiffService diffService;

    private TLConfiguration config;

    private WorkflowService workflowService;

    private EditNotificationService  editNotificationService;

    private TradeUtil tradeUtil;

    private TLBatchService tlBatchService;
        
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${workflow.bpa.businessServiceCode.fallback_enabled}")
    private Boolean pickWFServiceNameFromTradeTypeOnly;

    @Autowired
    public TradeLicenseService(WorkflowIntegrator wfIntegrator, EnrichmentService enrichmentService,
                               UserService userService, TLRepository repository, ActionValidator actionValidator,
                               TLValidator tlValidator, TLWorkflowService TLWorkflowService,
                               CalculationService calculationService, TradeUtil util, DiffService diffService,
                               TLConfiguration config, EditNotificationService editNotificationService, WorkflowService workflowService,
                               TradeUtil tradeUtil, TLBatchService tlBatchService, KafkaTemplate<String, Object> kafkaTemplate) {
        this.wfIntegrator = wfIntegrator;
        this.enrichmentService = enrichmentService;
        this.userService = userService;
        this.repository = repository;
        this.actionValidator = actionValidator;
        this.tlValidator = tlValidator;
        this.TLWorkflowService = TLWorkflowService;
        this.calculationService = calculationService;
        this.util = util;
        this.diffService = diffService;
        this.config = config;
        this.editNotificationService = editNotificationService;
        this.workflowService = workflowService;
        this.tradeUtil = tradeUtil;
        this.tlBatchService = tlBatchService;
        this.kafkaTemplate = kafkaTemplate;
    }





    /**
     * creates the tradeLicense for the given request
     * @param tradeLicenseRequest The TradeLicense Create Request
     * @return The list of created traddeLicense
     */
    public List<TradeLicense> create(TradeLicenseRequest tradeLicenseRequest,String businessServicefromPath){
       if(businessServicefromPath==null)
            businessServicefromPath = businessService_TL;
       tlValidator.validateBusinessService(tradeLicenseRequest,businessServicefromPath);
       Map<String, Object> mdmsDataMap = new HashMap<String, Object>();
       List<Integer> reminderPeriodsList = new ArrayList<>();
       tradeLicenseRequest.getLicenses().forEach(license -> {
    	   Object mdmsDataForTenantId = util.mDMSCall(tradeLicenseRequest.getRequestInfo(), license.getTenantId());
		   mdmsDataMap.put(license.getTenantId(), mdmsDataForTenantId);
		   reminderPeriodsList.addAll(JsonPath.read(mdmsDataForTenantId, "$.MdmsRes.TradeLicense.ReminderPeriods.*.reminderPeriods"));
	   });
       Long reminderPeriods = Long.valueOf(reminderPeriodsList.isEmpty() ? "0" : reminderPeriodsList.get(0).toString());
       Object billingSlabs = null;//util.getBillingSlabs(tradeLicenseRequest.getRequestInfo(), tradeLicenseRequest.getLicenses().get(0).getTenantId());
       actionValidator.validateCreateRequest(tradeLicenseRequest);
        switch(businessServicefromPath)
        {
            case businessService_BPA:
                validateMobileNumberUniqueness(tradeLicenseRequest, reminderPeriods);
                break;
        }
       enrichmentService.enrichTLCreateRequest(tradeLicenseRequest, mdmsDataMap);
       tlValidator.validateCreate(tradeLicenseRequest, mdmsDataMap, billingSlabs);
       userService.createUser(tradeLicenseRequest, false);
       calculationService.addCalculation(tradeLicenseRequest);

        /*
         * call workflow service if it's enable else uses internal workflow process
         */
       switch(businessServicefromPath)
       {
           case businessService_TL:
               if (config.getIsExternalWorkFlowEnabled())
                   wfIntegrator.callWorkFlow(tradeLicenseRequest);
               break;
       }
        repository.save(tradeLicenseRequest);
       

        return tradeLicenseRequest.getLicenses();
	}

    public void validateMobileNumberUniqueness(TradeLicenseRequest request, Long reminderPeriods) {
    	Long currentTime = System.currentTimeMillis();
        for (TradeLicense license : request.getLicenses()) {
        	String applicationType = license.getApplicationType() != null
    				? license.getApplicationType().toString()
    				: "";
            for (TradeUnit tradeUnit : license.getTradeLicenseDetail().getTradeUnits()) {
                String tradetypeOfNewLicense = tradeUnit.getTradeType().split("\\.")[0];
                List<String> mobileNumbers = license.getTradeLicenseDetail().getOwners().stream().map(OwnerInfo::getMobileNumber).collect(Collectors.toList());
                for (String mobno : mobileNumbers) {
                    TradeLicenseSearchCriteria tradeLicenseSearchCriteria = TradeLicenseSearchCriteria.builder().tenantId(license.getTenantId()).businessService(license.getBusinessService()).mobileNumber(mobno).build();
                    List<TradeLicense> licensesFromSearch = getLicensesFromMobileNumber(tradeLicenseSearchCriteria, request.getRequestInfo());
                    List<String> tradeTypeResultforSameMobNo = new ArrayList<>();
                    for (TradeLicense result : licensesFromSearch) {
                    	Long validToDate = result.getValidTo();
                        if (!StringUtils.equals(result.getApplicationNumber(), license.getApplicationNumber()) && 
                        		!(StringUtils.equals(result.getStatus(),STATUS_REJECTED) || 
                        				StringUtils.equals(result.getStatus(),STATUS_EXPIRED) || 
                        				STATUS_INACTIVE.equalsIgnoreCase(result.getStatus()) ||
                        				(validToDate != null && (validToDate - currentTime) <= reminderPeriods && APPLICATION_TYPE_RENEWAL.equalsIgnoreCase(applicationType)))) {
                            tradeTypeResultforSameMobNo.add(result.getTradeLicenseDetail().getTradeUnits().get(0).getTradeType().split("\\.")[0]);
                        }
                    }
                    if (tradeTypeResultforSameMobNo.contains(tradetypeOfNewLicense)) {
                        throw new CustomException("DUPLICATE_TRADETYPEONMOBNO", " A user account with this mobile number already exists. Please use a different number or log in with the existing account.");
                    }
                }
            }
        }
    }
    /**
     *  Searches the tradeLicense for the given criteria if search is on owner paramter then first user service
     *  is called followed by query to db
     * @param criteria The object containing the paramters on which to search
     * @param requestInfo The search request's requestInfo
     * @return List of tradeLicense for the given criteria
     */
    public List<TradeLicense> search(TradeLicenseSearchCriteria criteria, RequestInfo requestInfo, String serviceFromPath, HttpHeaders headers){
        List<TradeLicense> licenses;
        // allow mobileNumber based search by citizen if interserviceCall
        boolean isInterServiceCall = isInterServiceCall(headers);
        tlValidator.validateSearch(requestInfo,criteria,serviceFromPath, isInterServiceCall);
        criteria.setBusinessService(serviceFromPath);
        enrichmentService.enrichSearchCriteriaWithAccountId(requestInfo,criteria);
        if(criteria.getRenewalPending()!=null && criteria.getRenewalPending()== true ) {
        	
        	String currentFinancialYear = "";
       	    
            
            Object mdmsData = util.mDMSCall(requestInfo, criteria.getTenantId() );
            String jsonPath = TLConstants.MDMS_CURRENT_FINANCIAL_YEAR.replace("{}",businessService_TL);
            List<Map<String,Object>> jsonOutput =  JsonPath.read(mdmsData, jsonPath);
            
            for (int i=0; i<jsonOutput.size();i++) {
           	 Object startingDate = jsonOutput.get(i).get(TLConstants.MDMS_STARTDATE);
           	 Object endingDate = jsonOutput.get(i).get(TLConstants.MDMS_ENDDATE);
           	 Long startTime = (Long)startingDate;
           	 Long endTime = (Long)endingDate;
           	 
           	 if(System.currentTimeMillis()>=startTime && System.currentTimeMillis()<=endTime) {
           		 currentFinancialYear = jsonOutput.get(i).get(TLConstants.MDMS_FIN_YEAR_RANGE).toString();
           		 break;
           	 }
           	 
            }
            
            
            criteria.setFinancialYear(currentFinancialYear);
        	
        }
        
         if(criteria.getMobileNumber()!=null || criteria.getOwnerName() != null){
             licenses = getLicensesFromMobileNumber(criteria,requestInfo);
         }
         else {
             licenses = getLicensesWithOwnerInfo(criteria,requestInfo);
         }

         if(criteria.getOnlyLatestApplication() && !CollectionUtils.isEmpty(licenses))
        	 licenses = Arrays.asList(licenses.get(0));
         
         if(businessService_BPA.equalsIgnoreCase(criteria.getBusinessService())) {
        	 licenses.forEach(license -> {
            	 if(license.getTradeLicenseDetail().getApplicationDocuments() != null) {
            		 String signatureId = license.getTradeLicenseDetail().getApplicationDocuments().stream()
                          	.filter(documnet -> documnet.getDocumentType().equalsIgnoreCase(SIGNATURE_DOC_TYPE))
                          	.map(Document::getFileStoreId).findAny().orElse(null);
                 	 if(StringUtils.isEmpty(signatureId) && !StringUtils.isEmpty(license.getTradeLicenseDetail().getOwners().get(0).getSignature())) {
                 		 license.getTradeLicenseDetail().getApplicationDocuments()
                 		 .add(Document.builder()
                 				 .active(true)
                 				 .tenantId(license.getTenantId())
                 				 .fileStoreId(license.getTradeLicenseDetail().getOwners().get(0).getSignature())
                 				 .documentType(SIGNATURE_DOC_TYPE).build());
                 	 }
            	 }
             }); 
         }
         
         return licenses;       
    }
    
    private void getLatestRejectedApplication(RequestInfo requestInfo, List<TradeLicense> licenses) {
    	List <TradeLicense> licensesToBeRemoved = new ArrayList<TradeLicense>();
    	List <TradeLicense> licensesToBeAdded = new ArrayList<TradeLicense>();
        
        for (TradeLicense rejectedLicense : licenses) {
       	 
       	 if(rejectedLicense.getStatus().toString().equalsIgnoreCase(TLConstants.STATUS_REJECTED)) {
       		 TradeLicenseSearchCriteria rejectedCriteria = new TradeLicenseSearchCriteria();
       		 
       		 rejectedCriteria.setTenantId(rejectedLicense.getTenantId());
       		 
       		 List <String> rejectedLicenseNumbers = new ArrayList<String>();
       		 rejectedLicenseNumbers.add(rejectedLicense.getLicenseNumber());
       		 
       		 rejectedCriteria.setLicenseNumbers(rejectedLicenseNumbers);
       		 licensesToBeRemoved.add(rejectedLicense);
       		 
       		 List <TradeLicense> rejectedLicenses = getLicensesWithOwnerInfo(rejectedCriteria,requestInfo);
       		 
       		 TradeLicense latestApplication = rejectedLicense;
       		 
       		 for(TradeLicense newLicense: rejectedLicenses) {
       			 if(latestApplication.getStatus().equalsIgnoreCase(TLConstants.STATUS_REJECTED)) {
       				 latestApplication = newLicense;
       			 }
       			 else {
       				 if(newLicense.getFinancialYear().toString().compareTo(latestApplication.getFinancialYear().toString())>0 && !newLicense.getStatus().equalsIgnoreCase(TLConstants.STATUS_REJECTED)) {
       					 latestApplication=newLicense;
       				 }
       			 }
       		 }
       		 
       		 if(latestApplication.getFinancialYear().toString().compareTo(rejectedLicense.getFinancialYear().toString()) <0) {
       			 licensesToBeAdded.add(latestApplication);
       		 }

       	 }
       	 
        }
        licenses.addAll(licensesToBeAdded);
        licenses.removeAll(licensesToBeRemoved);
	}


	private void filterRejectedApplications(RequestInfo requestInfo, List<TradeLicense> licenses) {
    	String currentFinancialYear = "";
   	    TradeLicenseRequest tradeLicenseRequest = new TradeLicenseRequest();
        tradeLicenseRequest.setRequestInfo(requestInfo);
        tradeLicenseRequest.setLicenses(licenses);
        
        Object mdmsData = util.mDMSCall(tradeLicenseRequest.getRequestInfo(), tradeLicenseRequest.getLicenses().get(0).getTenantId());
        String jsonPath = TLConstants.MDMS_CURRENT_FINANCIAL_YEAR.replace("{}",businessService_TL);
        List<Map<String,Object>> jsonOutput =  JsonPath.read(mdmsData, jsonPath);
        
        for (int i=0; i<jsonOutput.size();i++) {
       	 Object startingDate = jsonOutput.get(i).get(TLConstants.MDMS_STARTDATE);
       	 Object endingDate = jsonOutput.get(i).get(TLConstants.MDMS_ENDDATE);
       	 Long startTime = (Long)startingDate;
       	 Long endTime = (Long)endingDate;
       	 
       	 if(System.currentTimeMillis()>=startTime && System.currentTimeMillis()<=endTime) {
       		 currentFinancialYear = jsonOutput.get(i).get(TLConstants.MDMS_FIN_YEAR_RANGE).toString();
       		 break;
       	 }
       	 
        }
        
        String checker = currentFinancialYear;
        licenses.removeIf(t->t.getStatus().toString().equalsIgnoreCase(TLConstants.STATUS_REJECTED) && !t.getFinancialYear().toString().equalsIgnoreCase(checker));

	}

	
	public int countLicenses(TradeLicenseSearchCriteria criteria, RequestInfo requestInfo, String serviceFromPath, HttpHeaders headers){
		
		criteria.setBusinessService(serviceFromPath);
    	enrichmentService.enrichSearchCriteriaWithAccountId(requestInfo,criteria);


    	int licenseCount = repository.getLicenseCount(criteria);
    	
    	if(criteria.getOnlyLatestApplication() && licenseCount > 0)
    		licenseCount = 1;
    	
    	return licenseCount;
    }

	public Map<String,Integer> countApplications(TradeLicenseSearchCriteria criteria, RequestInfo requestInfo, String serviceFromPath, HttpHeaders headers){
	
		criteria.setBusinessService(serviceFromPath);
		
		Map<String,Integer> licenseCount = repository.getApplicationsCount(criteria);
	
		return licenseCount;
	}
    

    public void checkEndStateAndAddBPARoles(TradeLicenseRequest tradeLicenseRequest) {
        List<String> endstates = tradeUtil.getBPAEndState(tradeLicenseRequest);
        List<TradeLicense> licensesToAddRoles = new ArrayList<>();
        for (int i = 0; i < tradeLicenseRequest.getLicenses().size(); i++) {
            TradeLicense license = tradeLicenseRequest.getLicenses().get(0);
            if ((license.getStatus() != null) && (license.getStatus().equalsIgnoreCase(endstates.get(i))
            		|| license.getStatus().equalsIgnoreCase(TLConstants.STATUS_BLACKLISTED)
            		|| license.getStatus().equalsIgnoreCase(TLConstants.STATUS_EXPIRED)
            		|| license.getStatus().equalsIgnoreCase(TLConstants.STATUS_INACTIVE) )) {
                licensesToAddRoles.add(license);
            }
        }
        if (!licensesToAddRoles.isEmpty()) {
            TradeLicenseRequest tradeLicenseRequestForUserUpdate = TradeLicenseRequest.builder().licenses(licensesToAddRoles).requestInfo(tradeLicenseRequest.getRequestInfo()).build();
            userService.createUser(tradeLicenseRequestForUserUpdate, true);
        }
    }

    public List<TradeLicense> getLicensesFromMobileNumber(TradeLicenseSearchCriteria criteria, RequestInfo requestInfo){
    	
        List<TradeLicense> licenses = new LinkedList<>();
        
        boolean isEmpty = enrichWithUserDetails(criteria,requestInfo);
        
        if(isEmpty) {
        	return Collections.emptyList();
        }
        
        //Get all tradeLicenses with ownerInfo enriched from user service
        licenses = getLicensesWithOwnerInfo(criteria,requestInfo);
        return licenses;
    }


    /**
     * Returns the tradeLicense with enrivhed owners from user servise
     * @param criteria The object containing the paramters on which to search
     * @param requestInfo The search request's requestInfo
     * @return List of tradeLicense for the given criteria
     */
    public List<TradeLicense> getLicensesWithOwnerInfo(TradeLicenseSearchCriteria criteria,RequestInfo requestInfo){
        List<TradeLicense> licenses = repository.getLicenses(criteria);
        if(licenses.isEmpty())
            return Collections.emptyList();
        licenses = enrichmentService.enrichTradeLicenseSearch(licenses,criteria,requestInfo);
        return licenses;
    }


    private void removeDuplicates(List<TradeLicense> licenses) {
    	List <TradeLicense> duplicateLicenses = new ArrayList<TradeLicense>();
    	
    	for(TradeLicense license : licenses) {
    		for(TradeLicense duplicateLicense : licenses) {
    			if (!license.getApplicationNumber().equalsIgnoreCase(duplicateLicense.getApplicationNumber()) && license.getLicenseNumber().equalsIgnoreCase(duplicateLicense.getLicenseNumber()) &&  duplicateLicense.getFinancialYear().compareTo(license.getFinancialYear())<0 ) {
    				duplicateLicenses.add(duplicateLicense);
    			}
    		}
    	}
    	
    	for (TradeLicense duplicateLicense : duplicateLicenses) {
    		licenses.removeIf(t->t.getApplicationNumber().equalsIgnoreCase(duplicateLicense.getApplicationNumber()));
    	}
		
	}

	/**
     * Returns tradeLicense from db for the update request
     * @param request The update request
     * @return List of tradeLicenses
     */
    public List<TradeLicense> getLicensesWithOwnerInfo(TradeLicenseRequest request){
    	
        List<TradeLicense> licenses = new LinkedList<>();
        
        request.getLicenses().forEach(license -> {
        	TradeLicenseSearchCriteria criteria = new TradeLicenseSearchCriteria();
        	List<String> ids = Arrays.asList(license.getId());
        	criteria.setIds(ids);
        	criteria.setTenantId(license.getTenantId());
        	criteria.setBusinessService(license.getBusinessService());
        	List<TradeLicense> licensesFromDb = repository.getLicenses(criteria);
        	
        	if(!licensesFromDb.isEmpty()) {
        		licenses.addAll(enrichmentService.enrichTradeLicenseSearch(licensesFromDb,criteria,request.getRequestInfo()));
        	}
        	
		});

        if(licenses.isEmpty())
            return Collections.emptyList();
        
        return licenses;
    }


    /**
     * Updates the tradeLicenses
     * @param tradeLicenseRequest The update Request
     * @return Updated TradeLcienses
     */
    public List<TradeLicense> update(TradeLicenseRequest tradeLicenseRequest, String businessServicefromPath){
        TradeLicense licence = tradeLicenseRequest.getLicenses().get(0);
        TradeLicense.ApplicationTypeEnum applicationType = licence.getApplicationType();
        List<TradeLicense> licenceResponse = null;
        if(applicationType != null && (applicationType).toString().equals(TLConstants.APPLICATION_TYPE_RENEWAL ) &&
        		(licence.getAction().equalsIgnoreCase(TLConstants.TL_ACTION_APPLY) || licence.getAction().equalsIgnoreCase(TLConstants.TL_ACTION_INITIATE)) && (licence.getStatus().equals(TLConstants.STATUS_APPROVED) || licence.getStatus().equals(TLConstants.STATUS_MANUALLYEXPIRED) || licence.getStatus().equals(TLConstants.STATUS_EXPIRED) )){
            List<TradeLicense> createResponse = create(tradeLicenseRequest, businessServicefromPath);
            licenceResponse =  createResponse;
        }
        else{
            if (businessServicefromPath == null)
                businessServicefromPath = businessService_TL;
            tlValidator.validateBusinessService(tradeLicenseRequest, businessServicefromPath);
            Map<String, Object> mdmsDataMap = new HashMap<String, Object>();
            List<Integer> reminderPeriodsList = new ArrayList<>();
            tradeLicenseRequest.getLicenses().forEach(license -> {
         	   Object mdmsDataForTenantId = util.mDMSCall(tradeLicenseRequest.getRequestInfo(), license.getTenantId());
     		   mdmsDataMap.put(license.getTenantId(), mdmsDataForTenantId);
     		   reminderPeriodsList.addAll(JsonPath.read(mdmsDataForTenantId, "$.MdmsRes.TradeLicense.ReminderPeriods.*.reminderPeriods"));
     	   });
            Long reminderPeriods = Long.valueOf(reminderPeriodsList.isEmpty() ? "0" : reminderPeriodsList.get(0).toString());
            Object billingSlabs = null;//util.getBillingSlabs(tradeLicenseRequest.getRequestInfo(), tradeLicenseRequest.getLicenses().get(0).getTenantId());
            String businessServiceName = null;
            switch (businessServicefromPath) {
                case businessService_TL:
                    businessServiceName = config.getTlBusinessServiceValue();
                    break;

                case businessService_BPA:
                    String tradeType = tradeLicenseRequest.getLicenses().get(0).getTradeLicenseDetail().getTradeUnits().get(0).getTradeType();
                    if (pickWFServiceNameFromTradeTypeOnly)
                        tradeType = tradeType.split("\\.")[0];
                    businessServiceName = tradeType;
                    if(TLConstants.APPLICATION_TYPE_UPGRADE.equals(applicationType != null ? applicationType.toString() : "")) {
                    	if(businessServiceName.equalsIgnoreCase("ARCHITECT"))
                    		businessServiceName = businessServiceName + "_" + TLConstants.APPLICATION_TYPE_UPGRADE;
                    	else
                    		businessServiceName = businessService_BPA + "_" + TLConstants.APPLICATION_TYPE_UPGRADE;
                    }
                    break;
            }
            
            Map<String, BusinessService> businessServiceMap = new HashMap<>();
            for(TradeLicense license : tradeLicenseRequest.getLicenses() ) {
				if (businessServiceMap.get(license.getTenantId()) == null) {
					BusinessService businessService = workflowService.getBusinessService(license.getTenantId(), tradeLicenseRequest.getRequestInfo(), businessServiceName);
					businessServiceMap.put(license.getTenantId(), businessService);
				}
			};
            List<TradeLicense> searchResult = getLicensesWithOwnerInfo(tradeLicenseRequest);
            
            validateLatestApplicationCancellation(tradeLicenseRequest, businessServiceMap);

            enrichmentService.enrichTLUpdateRequest(tradeLicenseRequest, businessServiceMap);
            tlValidator.validateUpdate(tradeLicenseRequest, searchResult, mdmsDataMap, billingSlabs);
            switch(businessServicefromPath)
            {
                case businessService_BPA:
                    validateMobileNumberUniqueness(tradeLicenseRequest, reminderPeriods);
                    break;
            }
            Map<String, Difference> diffMap = diffService.getDifference(tradeLicenseRequest, searchResult);
            Map<String, Boolean> idToIsStateUpdatableMap = util.getIdToIsStateUpdatableMap(businessServiceMap, searchResult);

            /*
             * call workflow service if it's enable else uses internal workflow process
             */
            List<String> endStates = Collections.nCopies(tradeLicenseRequest.getLicenses().size(),STATUS_APPROVED);
            switch (businessServicefromPath) {
                case businessService_TL:
                    if (config.getIsExternalWorkFlowEnabled()) {
                        wfIntegrator.callWorkFlow(tradeLicenseRequest);
                    } else {
                        TLWorkflowService.updateStatus(tradeLicenseRequest);
                    }
                    break;

                case businessService_BPA:
                    endStates = tradeUtil.getBPAEndState(tradeLicenseRequest);
                    wfIntegrator.callWorkFlow(tradeLicenseRequest);
                    break;
            }
            enrichmentService.postStatusEnrichment(tradeLicenseRequest,endStates,mdmsDataMap);
            userService.createUser(tradeLicenseRequest, false);
            if(!TLConstants.APPLICATION_TYPE_UPGRADE.equals(applicationType != null ? applicationType.toString() : ""))
            	calculationService.addCalculation(tradeLicenseRequest);
            repository.update(tradeLicenseRequest, idToIsStateUpdatableMap);
            licenceResponse=  tradeLicenseRequest.getLicenses();
        }
        return licenceResponse;
        
    }

    private void validateLatestApplicationCancellation(TradeLicenseRequest tradeLicenseRequest, Map<String, BusinessService> businessServiceMap) {
    	List <TradeLicense> licenses = tradeLicenseRequest.getLicenses();
    	List<TradeLicense> searchResultForCancellation = new ArrayList<TradeLicense>();
    			
    	licenses.forEach(license -> {
    		TradeLicenseSearchCriteria criteria = new TradeLicenseSearchCriteria();
    		List <String> licenseNumbers = Collections.singletonList(license.getLicenseNumber());
    		criteria.setTenantId(license.getTenantId());
    		criteria.setLicenseNumbers(licenseNumbers);
    		List<TradeLicense> searchResult = getLicensesWithOwnerInfo(criteria, tradeLicenseRequest.getRequestInfo());
    		searchResultForCancellation.addAll(searchResult);
    	});
        
        actionValidator.validateUpdateRequest(tradeLicenseRequest, businessServiceMap,searchResultForCancellation);
		
	}





	public List<TradeLicense> plainSearch(TradeLicenseSearchCriteria criteria, RequestInfo requestInfo){
        List<TradeLicense> licenses;
        List<String> ids = repository.fetchTradeLicenseIds(criteria);
        if(ids.isEmpty())
            return Collections.emptyList();

        criteria.setIds(ids);

        TradeLicenseSearchCriteria idsCriteria = TradeLicenseSearchCriteria.builder().ids(ids).build();

        licenses = repository.getPlainLicenseSearch(idsCriteria);

        if(!CollectionUtils.isEmpty(licenses))
            licenses = enrichmentService.enrichTradeLicenseSearch(licenses,criteria,requestInfo);

        log.info("Total Records Returned: "+licenses.size());

        return licenses;
    }


    /**
     *
     * @param serviceName
     */
    public void runJob(String serviceName, String jobname, RequestInfo requestInfo){

        if(serviceName == null)
            serviceName = TRADE_LICENSE_MODULE_CODE;

        tlBatchService.getLicensesAndPerformAction(serviceName, jobname, requestInfo);


    }
    
    public boolean enrichWithUserDetails(TradeLicenseSearchCriteria criteria, RequestInfo requestInfo) {
    	List<TradeLicense> licenses = new LinkedList<>();
        UserDetailResponse userDetailResponse = userService.getUser(criteria,requestInfo);

        if(userDetailResponse.getUser().size()==0){
            return true;
        }
        enrichmentService.enrichTLCriteriaWithOwnerids(criteria,userDetailResponse);
        
        if(criteria.getOnlyMobileNumber()!=null && criteria.getOnlyMobileNumber() ) {
        	criteria.setTenantId(null);
        }
        
        licenses = repository.getLicenses(criteria);

        if(licenses.size()==0){
        	return true;
        }

        Boolean isRenewalPending = (criteria.getRenewalPending()!=null && criteria.getRenewalPending()==true);
        
        criteria=enrichmentService.getTradeLicenseCriteriaFromIds(licenses);
        
        if(isRenewalPending) {
        	criteria.setRenewalPending(true);
        }
        
        return false;
    }





	public int getApplicationValidity() {
		return Integer.valueOf(config.getApplicationValidity());
	}
	
	/**
	 * Inactive all the previous applications
	 * 
	 * @author Roshan chaudhary
	 * 
	 * @param tradeLicense
	 */
	public void inactivepreviousApplications(TradeLicense tradeLicense) {
		String applicationType = tradeLicense.getApplicationType() != null
				? tradeLicense.getApplicationType().toString()
				: "";
		TradeLicenseSearchCriteria criteria = TradeLicenseSearchCriteria.builder()
				.status(Collections.singletonList(TLConstants.STATUS_APPROVED))
				.tenantId(tradeLicense.getTenantId())
				.mobileNumber(tradeLicense.getTradeLicenseDetail().getOwners().get(0).getMobileNumber())
				.businessService(businessService_BPA)
				.build();
		
		//For Architect License type then find all the Approved Applications
		if(tradeLicense.getTradeLicenseDetail().getTradeUnits().get(0).getTradeType().contains("ARCHITECT")) 
			criteria.setOnlyLatestApplication(true);
		
		RequestInfo requestInfo = RequestInfo.builder()
				.userInfo(userService.searchSystemUser())
				.authToken("")
				.build();
		
		List<TradeLicense> licenses = getLicensesFromMobileNumber(criteria,requestInfo);
		
		if(!CollectionUtils.isEmpty(licenses)) {
			// Remove latest Approved application
			licenses = licenses.stream()
					.filter(license -> !(license.getApplicationNumber().equalsIgnoreCase(tradeLicense.getApplicationNumber())))
					.collect(Collectors.toList());
			
			// Set Inactive Action on all the previous application
			licenses.forEach(license -> {
				license.setAction(STATUS_INACTIVE);
				ObjectNode additionalDetails = (ObjectNode)license.getTradeLicenseDetail().getAdditionalDetail();
				additionalDetails.put("inactiveType", applicationType);
				license.getTradeLicenseDetail().setAdditionalDetail(additionalDetails);
			});
			if(!licenses.isEmpty()) {
				try {
					TradeLicenseRequest tradeLicenseRequest = TradeLicenseRequest.builder().requestInfo(requestInfo).licenses(licenses).build();
					update(tradeLicenseRequest, businessService_BPA);
				} catch (Exception e) {
					log.error("Enable to Inactive Previous Applications for user: " + tradeLicense.getTradeLicenseDetail().getOwners().get(0).getMobileNumber());
				}
			}
		}
		
	}
	
	/**
	 * Search all the licenses with an expiry date matching
	 * the current day are marked as expired automatically.
	 * 
	 * @author Roshan chaudhary
	 * 
	 * @param serviceName
	 * @param requestInfo of the System user
	 */
	
	public void getLicensesAndExpire(String serviceName, RequestInfo requestInfo) {

		List<String> tenantIdsFromRepository = repository.fetchTradeLicenseTenantIds(serviceName);

		tenantIdsFromRepository.forEach(tenantIdFromRepository -> {

			try {

				Long validTill = System.currentTimeMillis();

				TradeLicenseSearchCriteria criteria = TradeLicenseSearchCriteria.builder().businessService(serviceName)
						.validTo(validTill).status(Collections.singletonList(STATUS_APPROVED))
						.tenantId(tenantIdFromRepository).limit(config.getPaginationSize()).build();
				

				List<TradeLicense> licenses = getLicensesWithOwnerInfo(criteria,requestInfo);

				if (!CollectionUtils.isEmpty(licenses)) {

					licenses.forEach(license -> {
		                license.setAction(ACTION_EXPIRE);
		                license.setStatus(STATUS_EXPIRED);
		                TradeLicenseRequest tradeLicenseRequest = new TradeLicenseRequest(requestInfo, Collections.singletonList(license));
		                try {
		                	wfIntegrator.callWorkFlow(tradeLicenseRequest);
						} catch (Exception e) {
							log.error("Error While Auto Expiry Application : " + license.getApplicationNumber());
							e.printStackTrace();
						}finally {
							String key = (license.getLicenseNumber() != null)
	                                ? license.getLicenseNumber() + "-" + UUID.randomUUID()
	                                : UUID.randomUUID().toString();
							
		        			kafkaTemplate.send(config.getUpdateTopic(),key, tradeLicenseRequest);
						}
		                
		            });

				}

			

			} catch (Exception ex) {
				log.error("The batch process could not be completed for the tenant id : " + tenantIdFromRepository);
			}

		});

	}

}
