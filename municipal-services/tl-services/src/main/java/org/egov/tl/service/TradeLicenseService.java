package org.egov.tl.service;

import static org.egov.tl.util.TLConstants.ACTION_STATUS_APPROVED;
import static org.egov.tl.util.TLConstants.STATUS_APPLIED;
import static org.egov.tl.util.TLConstants.STATUS_APPROVED;
import static org.egov.tl.util.TLConstants.STATUS_INITIATED;
import static org.egov.tl.util.TLConstants.STATUS_PENDINGFORMODIFICATION;
import static org.egov.tl.util.TLConstants.STATUS_REJECTED;
import static org.egov.tl.util.TLConstants.STATUS_VERIFIED;
import static org.egov.tl.util.TLConstants.TRADE_LICENSE_MODULE_CODE;
import static org.egov.tl.util.TLConstants.businessService_BPA;
import static org.egov.tl.util.TLConstants.businessService_TL;
import static org.egov.tracer.http.HttpUtils.isInterServiceCall;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ErrorResponse;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.repository.TLRepository;
import org.egov.tl.service.notification.EditNotificationService;
import org.egov.tl.util.TLConstants;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.validator.TLValidator;
import org.egov.tl.web.models.ApplicationStatusChangeRequest;
import org.egov.tl.web.models.Difference;
import org.egov.tl.web.models.OwnerInfo;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.egov.tl.web.models.TradeLicenseResponse;
import org.egov.tl.web.models.TradeLicenseSearchCriteria;
import org.egov.tl.web.models.TradeUnit;
import org.egov.tl.web.models.UpdateTLStatusCriteriaRequest;
import org.egov.tl.web.models.contract.PDFRequest;
import org.egov.tl.web.models.contract.ProcessInstance;
import org.egov.tl.web.models.contract.ProcessInstanceRequest;
import org.egov.tl.web.models.contract.ProcessInstanceResponse;
import org.egov.tl.web.models.contract.Alfresco.DMSResponse;
import org.egov.tl.web.models.contract.Alfresco.DmsRequest;
import org.egov.tl.web.models.user.UserDetailResponse;
import org.egov.tl.web.models.workflow.BusinessService;
import org.egov.tl.workflow.ActionValidator;
import org.egov.tl.workflow.TLWorkflowService;
import org.egov.tl.workflow.WorkflowIntegrator;
import org.egov.tl.workflow.WorkflowService;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

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
    
    @Autowired
    private WorkflowIntegrator workflowIntegrator;

    @Autowired
    private TLRepository tlRepository;
    
    @Autowired
    private ReportService reportService;

    @Autowired
    private AlfrescoService alfrescoService;

    @Value("${workflow.bpa.businessServiceCode.fallback_enabled}")
    private Boolean pickWFServiceNameFromTradeTypeOnly;

    @Autowired
    public TradeLicenseService(WorkflowIntegrator wfIntegrator, EnrichmentService enrichmentService,
                               UserService userService, TLRepository repository, ActionValidator actionValidator,
                               TLValidator tlValidator, TLWorkflowService TLWorkflowService,
                               CalculationService calculationService, TradeUtil util, DiffService diffService,
                               TLConfiguration config, EditNotificationService editNotificationService, WorkflowService workflowService,
                               TradeUtil tradeUtil, TLBatchService tlBatchService) {
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
    }





    /**
     * creates the tradeLicense for the given request
     * @param tradeLicenseRequest The TradeLicense Create Request
     * @return The list of created traddeLicense
     */
    public List<TradeLicense> create(TradeLicenseRequest tradeLicenseRequest,String businessServicefromPath){
       if(businessServicefromPath==null)
            businessServicefromPath = businessService_TL;
       enrichPreCreateNewTLValues(tradeLicenseRequest);
       tlValidator.validateBusinessService(tradeLicenseRequest,businessServicefromPath);
       Object mdmsData = util.mDMSCall(tradeLicenseRequest.getRequestInfo(), tradeLicenseRequest.getLicenses().get(0).getTenantId());
       Object billingSlabs = util.getBillingSlabs(tradeLicenseRequest.getRequestInfo(), tradeLicenseRequest.getLicenses().get(0).getTenantId());
       actionValidator.validateCreateRequest(tradeLicenseRequest);
        switch(businessServicefromPath)
        {
            case businessService_BPA:
                validateMobileNumberUniqueness(tradeLicenseRequest);
                break;
        }
       enrichmentService.enrichTLCreateRequest(tradeLicenseRequest, mdmsData);
//       tlValidator.validateCreate(tradeLicenseRequest, mdmsData, billingSlabs);
       log.info("request is " + tradeLicenseRequest);
       userService.createUser(tradeLicenseRequest, false);
       calculationService.addCalculation(tradeLicenseRequest);

        /*
         * call workflow service if it's enable else uses internal workflow process
         */
//       switch(businessServicefromPath)
//       {
//           case businessService_TL:
//               if (config.getIsExternalWorkFlowEnabled())
//                   wfIntegrator.callWorkFlow(tradeLicenseRequest);
//               break;
//       }
       
       repository.save(tradeLicenseRequest);

       //trigger wf 
       workflowIntegrator.callWorkFlow(tradeLicenseRequest);
       
        return tradeLicenseRequest.getLicenses();
	}

    private void enrichPreCreateNewTLValues(TradeLicenseRequest tradeLicenseRequest) {
    	tradeLicenseRequest.getLicenses().forEach(license -> {
    		if(StringUtils.equals(license.getBusinessService(), businessService_TL)) {
        		enrichmentService.enrichCreateNewTLValues(tradeLicenseRequest.getRequestInfo().getUserInfo().getUuid(), license);
    		}
    	});
    	
	}





	public void validateMobileNumberUniqueness(TradeLicenseRequest request) {
        for (TradeLicense license : request.getLicenses()) {
            for (TradeUnit tradeUnit : license.getTradeLicenseDetail().getTradeUnits()) {
                String tradetypeOfNewLicense = tradeUnit.getTradeType().split("\\.")[0];
                List<String> mobileNumbers = license.getTradeLicenseDetail().getOwners().stream().map(OwnerInfo::getMobileNumber).collect(Collectors.toList());
                for (String mobno : mobileNumbers) {
                    TradeLicenseSearchCriteria tradeLicenseSearchCriteria = TradeLicenseSearchCriteria.builder().tenantId(license.getTenantId()).businessService(license.getBusinessService()).mobileNumber(mobno).build();
                    List<TradeLicense> licensesFromSearch = getLicensesFromMobileNumber(tradeLicenseSearchCriteria, request.getRequestInfo());
                    List<String> tradeTypeResultforSameMobNo = new ArrayList<>();
                    for (TradeLicense result : licensesFromSearch) {
                        if (!StringUtils.equals(result.getApplicationNumber(), license.getApplicationNumber()) && !StringUtils.equals(result.getStatus(),STATUS_REJECTED)) {
                            tradeTypeResultforSameMobNo.add(result.getTradeLicenseDetail().getTradeUnits().get(0).getTradeType().split("\\.")[0]);
                        }
                    }
                    if (tradeTypeResultforSameMobNo.contains(tradetypeOfNewLicense)) {
                        throw new CustomException("DUPLICATE_TRADETYPEONMOBNO", " Same mobile number can not be used for more than one applications on same license type: "+tradetypeOfNewLicense);
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
         
         
         // calculate passed dates from creation date
         enrichPassedDates(licenses);

         // enrich ULB from tenantId
         enrichUlbFromTenantId(licenses);

         return licenses;       
    }
    
    private void enrichUlbFromTenantId(List<TradeLicense> licenses) {
		
    	licenses.stream().forEach(license -> {
    		if(StringUtils.equalsIgnoreCase(license.getTenantId(), "hp.shimla")) {
    			license.setUlb("Shimla");
    		}
    	});
		
	}





	private void enrichPassedDates(List<TradeLicense> licenses) {
		licenses.stream().forEach(license -> {
			
			if(null != license.getAuditDetails() && null != license.getAuditDetails().getCreatedTime()) {
				Long passedDays = new Date().getTime() - license.getAuditDetails().getCreatedTime();
				// Convert milliseconds to days (assuming 24 hours = 86400000 milliseconds)
                long passedDaysCount = passedDays / 86400000L;
				license.setPassedDays((int) passedDaysCount);
			}
			
		});
		
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
            if ((license.getStatus() != null) && license.getStatus().equalsIgnoreCase(endstates.get(i))) {
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
        TradeLicenseSearchCriteria criteria = new TradeLicenseSearchCriteria();
        List<String> ids = new LinkedList<>();
        request.getLicenses().forEach(license -> {ids.add(license.getId());});

        criteria.setTenantId(request.getLicenses().get(0).getTenantId());
        criteria.setIds(ids);
        criteria.setBusinessService(request.getLicenses().get(0).getBusinessService());

        List<TradeLicense> licenses = repository.getLicenses(criteria);

        if(licenses.isEmpty())
            return Collections.emptyList();
        licenses = enrichmentService.enrichTradeLicenseSearch(licenses,criteria,request.getRequestInfo());
        return licenses;
    }


    /**
     * Updates the tradeLicenses
     * @param tradeLicenseRequest The update Request
     * @return Updated TradeLcienses
     */
    public List<TradeLicense> update(TradeLicenseRequest tradeLicenseRequest, String businessServicefromPath){
    	tradeLicenseRequest = enrichPreUpdateNewTLValues(tradeLicenseRequest, businessServicefromPath);
        TradeLicense licence = tradeLicenseRequest.getLicenses().get(0);
        TradeLicense.ApplicationTypeEnum applicationType = licence.getApplicationType();
        List<TradeLicense> licenceResponse = null;
        if(applicationType != null && (applicationType).toString().equals(TLConstants.APPLICATION_TYPE_RENEWAL ) &&
                licence.getAction().equalsIgnoreCase(TLConstants.TL_ACTION_INITIATE) && (licence.getStatus().equals(TLConstants.STATUS_APPROVED) || licence.getStatus().equals(TLConstants.STATUS_MANUALLYEXPIRED) || licence.getStatus().equals(TLConstants.STATUS_EXPIRED) )){
            List<TradeLicense> createResponse = create(tradeLicenseRequest, businessServicefromPath);
            licenceResponse =  createResponse;
        }
        else{
            if (businessServicefromPath == null)
                businessServicefromPath = businessService_TL;
            tlValidator.validateBusinessService(tradeLicenseRequest, businessServicefromPath);
            Object mdmsData = util.mDMSCall(tradeLicenseRequest.getRequestInfo(), tradeLicenseRequest.getLicenses().get(0).getTenantId());
            Object billingSlabs = util.getBillingSlabs(tradeLicenseRequest.getRequestInfo(), tradeLicenseRequest.getLicenses().get(0).getTenantId());
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
                    break;
            }
            BusinessService businessService = workflowService.getBusinessService(tradeLicenseRequest.getLicenses().get(0).getTenantId(), tradeLicenseRequest.getRequestInfo(), businessServiceName);
            List<TradeLicense> searchResult = getLicensesWithOwnerInfo(tradeLicenseRequest);
            
            validateLatestApplicationCancellation(tradeLicenseRequest, businessService);

            enrichmentService.enrichTLUpdateRequest(tradeLicenseRequest, businessService);
            tlValidator.validateUpdate(tradeLicenseRequest, searchResult, mdmsData, billingSlabs);
            switch(businessServicefromPath)
            {
                case businessService_BPA:
                    validateMobileNumberUniqueness(tradeLicenseRequest);
                    break;
            }
            Map<String, Difference> diffMap = diffService.getDifference(tradeLicenseRequest, searchResult);
            Map<String, Boolean> idToIsStateUpdatableMap = util.getIdToIsStateUpdatableMap(businessService, searchResult);

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
            enrichmentService.postStatusEnrichment(tradeLicenseRequest,endStates,mdmsData);
            userService.createUser(tradeLicenseRequest, false);
            calculationService.addCalculation(tradeLicenseRequest);
            repository.update(tradeLicenseRequest, idToIsStateUpdatableMap);
            licenceResponse=  tradeLicenseRequest.getLicenses();
        }
        
//        // send notifications
//        sendTLNotifications(licenceResponse);
//        
//        // create and upload pdf
//        createAndUploadPDF(licenceResponse, tradeLicenseRequest.getRequestInfo());
        
        return licenceResponse;
        
    }


	private TradeLicenseRequest enrichPreUpdateNewTLValues(TradeLicenseRequest tradeLicenseRequest, String businessServicefromPath) {
		
		TradeLicenseRequest tempTradeLicenseRequest = TradeLicenseRequest.builder()
				.requestInfo(tradeLicenseRequest.getRequestInfo())
				.licenses(new ArrayList())
				.build();
		
		for(int i=0; i<tradeLicenseRequest.getLicenses().size(); i++) {
			TradeLicense license = tradeLicenseRequest.getLicenses().get(i);
			String action = license.getAction();
			
			if(null == license.getTradeLicenseDetail()
					&& (StringUtils.equalsIgnoreCase(TLConstants.ACTION_FORWARD_TO_VERIFIER, license.getAction())
							|| StringUtils.equalsIgnoreCase(TLConstants.ACTION_VERIFY, license.getAction())
							|| StringUtils.equalsIgnoreCase(TLConstants.ACTION_RETURN_TO_INITIATOR, license.getAction())
							|| StringUtils.equalsIgnoreCase(TLConstants.ACTION_RETURN_TO_VERIFIER, license.getAction())
							|| StringUtils.equalsIgnoreCase(TLConstants.ACTION_APPROVE, license.getAction()))
					&& StringUtils.isNotEmpty(license.getApplicationNumber())) {
				// search TL by application number
				TradeLicenseSearchCriteria tradeLicenseSearchCriteria = TradeLicenseSearchCriteria.builder()
						.businessService(businessServicefromPath)
						.tenantId(license.getTenantId())
						.applicationNumber(license.getApplicationNumber())
						.build();
				List<TradeLicense> licenses = getLicensesWithOwnerInfo(tradeLicenseSearchCriteria,tradeLicenseRequest.getRequestInfo());
				
				//enrich input fields
				licenses.get(0).setAction(action);
				tempTradeLicenseRequest.getLicenses().add(licenses.get(0));
			}
			else {
				validateInputObjectAndConstraints(license);
				tempTradeLicenseRequest.getLicenses().add(license);
			}
		}
		
		return tempTradeLicenseRequest;
		
	}


	private void validateInputObjectAndConstraints(TradeLicense license) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
	    Validator validator = factory.getValidator();
	    Set<ConstraintViolation<TradeLicense>> violations = validator.validate(license);

	    if (!violations.isEmpty()) {
	        // Collect validation errors
	        List<String> errorMessages = violations.stream()
	                .map(ConstraintViolation::getMessage)
	                .collect(Collectors.toList());

	        // Return a Bad Request response with validation errors
	        throw new RuntimeException("Input Data Validation Failed."+errorMessages);
	    }
	}





	private void sendTLNotifications(List<TradeLicense> licenceResponse) {
		
    	licenceResponse.stream().forEach(license -> {
    		
    		if(StringUtils.equals(license.getBusinessService(), businessService_TL)
    				&& StringUtils.equals(license.getStatus(), STATUS_APPROVED)) {
    			// send notification to license owner
    			List<String> mobileNumbers = license.getTradeLicenseDetail().getOwners().stream().map(owner -> owner.getMobileNumber()).collect(Collectors.toList());
    			sendSmsNotification(mobileNumbers, STATUS_APPROVED, license.getApplicationNumber());
    		}
    		if(StringUtils.equals(license.getBusinessService(), businessService_TL)
    				&& StringUtils.equals(license.getStatus(), STATUS_PENDINGFORMODIFICATION)) {
    			// send notification to license owner
    			List<String> mobileNumbers = license.getTradeLicenseDetail().getOwners().stream().map(owner -> owner.getMobileNumber()).collect(Collectors.toList());
    			sendSmsNotification(mobileNumbers, STATUS_PENDINGFORMODIFICATION, license.getApplicationNumber());
    		}
    	});
    	
	}


	private void sendSmsNotification(List<String> mobileNumbers, String statusApproved, String applicationNumber) {
//		sendMail;
	}


	private void createAndUploadPDF(List<TradeLicense> licenceResponse, RequestInfo requestInfo) {
		
		licenceResponse.stream().forEach(license -> {
			Resource object = createNoSavePDF(license
					, requestInfo);
		});
		
		
	}




	private void validateLatestApplicationCancellation(TradeLicenseRequest tradeLicenseRequest, BusinessService businessService) {
    	List <TradeLicense> licenses = tradeLicenseRequest.getLicenses();
        TradeLicenseSearchCriteria criteria = new TradeLicenseSearchCriteria();
    	
    	List <String> licenseNumbers = new ArrayList<String>();
    	
    	for (TradeLicense license : licenses) {
    		licenseNumbers.add(license.getLicenseNumber());
    		
    	}
    	
    	criteria.setTenantId(licenses.get(0).getTenantId());
    	criteria.setLicenseNumbers(licenseNumbers);
    	
    	List<TradeLicense> searchResultForCancellation = getLicensesWithOwnerInfo(criteria,tradeLicenseRequest.getRequestInfo());
        
        actionValidator.validateUpdateRequest(tradeLicenseRequest, businessService,searchResultForCancellation);
		
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





	public ProcessInstanceResponse updateState(UpdateTLStatusCriteriaRequest updateTLStatusCriteriaRequest) {
		
		ProcessInstance processInstance = ProcessInstance.builder()
				.tenantId(updateTLStatusCriteriaRequest.getUpdateTLStatusCriteria().getTenantId())
				.businessService(businessService_TL)
				.moduleName("TL")
				.businessId(updateTLStatusCriteriaRequest.getUpdateTLStatusCriteria().getBusinessId())
				.action(updateTLStatusCriteriaRequest.getUpdateTLStatusCriteria().getAction())
				.comment(updateTLStatusCriteriaRequest.getUpdateTLStatusCriteria().getComment())
				.build();
		
		ProcessInstanceRequest processInstanceRequest = ProcessInstanceRequest.builder()
				.requestInfo(updateTLStatusCriteriaRequest.getRequestInfo())
				.processInstances(Arrays.asList(processInstance))
				.build();
		
		// run workflow
		ProcessInstanceResponse response = workflowService.transition(processInstanceRequest);
		
		if(response == null) {
			throw new RuntimeException("Provided application failed to change status.");
		}
		
		//update status of trade license
		if(StringUtils.equalsIgnoreCase(updateTLStatusCriteriaRequest.getUpdateTLStatusCriteria().getAction(), "VERIFY")) {
			tlRepository.updateTlStatus(updateTLStatusCriteriaRequest.getUpdateTLStatusCriteria().getBusinessId(), "VERIFIED");
		}else if(StringUtils.equalsIgnoreCase(updateTLStatusCriteriaRequest.getUpdateTLStatusCriteria().getAction(), "APPROVE")) {
			tlRepository.updateTlStatus(updateTLStatusCriteriaRequest.getUpdateTLStatusCriteria().getBusinessId(), "APPROVED");
		}
		
		return response;
	}





	public ApplicationStatusChangeRequest updateStateOfApplication(ApplicationStatusChangeRequest applicationStatusChangeRequest) {
		
		// search TL
		TradeLicenseSearchCriteria criteria = TradeLicenseSearchCriteria.builder()
				.tenantId(applicationStatusChangeRequest.getTenantId())
				.businessService(businessService_TL)
				.applicationNumber(applicationStatusChangeRequest.getApplicationNumber())
				.build();
		List<TradeLicense> licenses = repository.getLicenses(criteria);
		
		if(CollectionUtils.isEmpty(licenses)) {
			throw new RuntimeException("No Trade license found for given input.");
		}
		TradeLicense tl = licenses.get(0);
		
		// validate current status
		if(StringUtils.equals(applicationStatusChangeRequest.getApplicationStatus(), STATUS_APPLIED)) {
			if(!(StringUtils.equals(tl.getStatus(), STATUS_INITIATED)
				|| StringUtils.equals(tl.getStatus(), ACTION_STATUS_APPROVED)
				|| StringUtils.equals(tl.getStatus(), STATUS_PENDINGFORMODIFICATION))) {
			throw new RuntimeException("Currently Status can't be changed to "+STATUS_APPLIED);
			}
		}
		
		
		tlRepository.updateStateOfApplicationApplied(applicationStatusChangeRequest);
		return applicationStatusChangeRequest;
	}





	public void processResponse(TradeLicenseResponse response) {
		
		// categorize each license
		if (!CollectionUtils.isEmpty(response.getLicenses())
				) {
			response.setApplicationInitiated((int) response.getLicenses().stream()
					.filter(license -> StringUtils.equalsIgnoreCase(STATUS_INITIATED, license.getStatus())).count());
			response.setApplicationApplied((int) response.getLicenses().stream()
					.filter(license -> StringUtils.equalsIgnoreCase(STATUS_APPLIED, license.getStatus())).count());
			response.setApplicationVerified((int) response.getLicenses().stream()
					.filter(license -> StringUtils.equalsIgnoreCase(STATUS_VERIFIED, license.getStatus())).count());
			response.setApplicationRejected((int) response.getLicenses().stream()
					.filter(license -> StringUtils.equalsIgnoreCase(STATUS_REJECTED, license.getStatus())).count());
			response.setApplicationApproved((int) response.getLicenses().stream()
					.filter(license -> StringUtils.equalsIgnoreCase(STATUS_APPROVED, license.getStatus()))
					.count());
		}
		
		
	}
	
	public Resource createNoSavePDF(TradeLicense tradeLicense, RequestInfo requestInfo) {
		
		// generate pdf
		PDFRequest pdfRequest = generatePdfRequestByTradeLicense(tradeLicense, requestInfo);
		Resource resource = reportService.createNoSavePDF(pdfRequest);
		
		
		//upload pdf
		DmsRequest dmsRequest = generateDmsRequestByTradeLicense(resource, tradeLicense, requestInfo);
		try {
			DMSResponse dmsResponse = alfrescoService.uploadAttachment(dmsRequest, requestInfo);
		} catch (IOException e) {
			throw new RuntimeException("Upload Attachment failed." + e.getMessage());
		}
		
		return resource;
	}


	private DmsRequest generateDmsRequestByTradeLicense(Resource resource, TradeLicense tradeLicense,
			RequestInfo requestInfo) {

//		MultipartFile multipartFile = convertResourceToMultipartFile(resource, "filename");

		DmsRequest dmsRequest = DmsRequest.builder().userId("123").objectId("1222").description("TL certificate")
				.id("").type("PDF").objectName("TL").comments("Signed Certificate").status(STATUS_APPROVED).file(resource)
				.build();

		return dmsRequest;
	}


	private PDFRequest generatePdfRequestByTradeLicense(TradeLicense tradeLicense, RequestInfo requestInfo) {
		
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> map2 = generateDataForTradeLicensePdfCreate();
		
		map.put("tl", map2);
		
		PDFRequest pdfRequest = PDFRequest.builder()
				.RequestInfo(requestInfo)
				.key("TradeLicense")
				.tenantId("hp")
				.data(map)
				.build();
		
		return pdfRequest;
	}


	private Map<String, Object> generateDataForTradeLicensePdfCreate() {

		Map<String, Object> tlObject = new HashMap<>();

		tlObject.put("applicationno", "PB-TL-2024-07-04-000098");
		tlObject.put("qrCodeText", "your_qr_code_text_value");
		tlObject.put("approvalDate", 1730636800000L);
		tlObject.put("serviceType", "your_service_type_value");
		tlObject.put("plotNo", "your_plot_no_value");
		tlObject.put("phaseNo", "your_phase_no_value");
		tlObject.put("permitNo", "your_permit_no_value");
		tlObject.put("lesseeName", "your_lessee_name_value");
		tlObject.put("approverName", "your_approver_name_value");
		tlObject.put("estate", "your_estate_value");
		tlObject.put("addressLine1", "your_address_line1_value");
		tlObject.put("addressLine2", "your_address_line2_value");
		tlObject.put("pincode", "your_pincode");
		return tlObject;
	}

}
