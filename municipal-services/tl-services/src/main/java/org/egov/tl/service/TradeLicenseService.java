package org.egov.tl.service;

import static org.egov.tl.util.TLConstants.ACTION_STATUS_APPROVED;
import static org.egov.tl.util.TLConstants.STATUS_APPLIED;
import static org.egov.tl.util.TLConstants.STATUS_APPROVED;
import static org.egov.tl.util.TLConstants.STATUS_INITIATED;
import static org.egov.tl.util.TLConstants.STATUS_PENDINGFORMODIFICATION;
import static org.egov.tl.util.TLConstants.STATUS_REJECTED;
import static org.egov.tl.util.TLConstants.TRADE_LICENSE_MODULE_CODE;
import static org.egov.tl.util.TLConstants.businessService_BPA;
import static org.egov.tl.util.TLConstants.businessService_NewTL;
import static org.egov.tl.util.TLConstants.businessService_TL;
import static org.egov.tracer.http.HttpUtils.isInterServiceCall;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import org.egov.common.contract.request.Role;
import org.egov.mdms.model.MdmsResponse;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.producer.Producer;
import org.egov.tl.repository.RestCallRepository;
import org.egov.tl.repository.TLRepository;
import org.egov.tl.service.notification.EditNotificationService;
import org.egov.tl.service.notification.TLNotificationService;
import org.egov.tl.util.TLConstants;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.validator.TLValidator;
import org.egov.tl.web.models.ApplicationDetail;
import org.egov.tl.web.models.ApplicationStatusChangeRequest;
import org.egov.tl.web.models.Difference;
import org.egov.tl.web.models.OwnerInfo;
import org.egov.tl.web.models.RequestInfoWrapper;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicense.ApplicationTypeEnum;
import org.egov.tl.web.models.TradeLicenseActionRequest;
import org.egov.tl.web.models.TradeLicenseActionResponse;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.egov.tl.web.models.TradeLicenseResponse;
import org.egov.tl.web.models.TradeLicenseSearchCriteria;
import org.egov.tl.web.models.TradeUnit;
import org.egov.tl.web.models.UpdateTLStatusCriteriaRequest;
import org.egov.tl.web.models.contract.BillResponse;
import org.egov.tl.web.models.contract.BillSearchCriteria;
import org.egov.tl.web.models.contract.BusinessService;
import org.egov.tl.web.models.contract.BusinessServiceResponse;
import org.egov.tl.web.models.contract.Demand;
import org.egov.tl.web.models.contract.GenerateBillCriteria;
import org.egov.tl.web.models.contract.PDFRequest;
import org.egov.tl.web.models.contract.ProcessInstance;
import org.egov.tl.web.models.contract.ProcessInstanceRequest;
import org.egov.tl.web.models.contract.ProcessInstanceResponse;
import org.egov.tl.web.models.contract.State;
import org.egov.tl.web.models.contract.Alfresco.DMSResponse;
import org.egov.tl.web.models.contract.Alfresco.DmsRequest;
import org.egov.tl.web.models.user.UserDetailResponse;
import org.egov.tl.workflow.ActionValidator;
import org.egov.tl.workflow.TLWorkflowService;
import org.egov.tl.workflow.WorkflowIntegrator;
import org.egov.tl.workflow.WorkflowService;
import org.egov.tracer.model.CustomException;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Autowired
    private RestCallRepository restCallRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private DemandService demandService;
    
    @Autowired
    private BillService billService;
    
    @Autowired
    private TLNotificationService tlNotificationService;
    
    @Autowired
    private TLConstants constants;
    
    @Autowired
    private Producer producer; 

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
//       calculationService.addCalculation(tradeLicenseRequest);

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
       try {
		log.info("add det : "+tradeLicenseRequest.getLicenses().get(0).getTradeLicenseDetail().getAddress().getAdditionalDetail());
		   log.info("add det : "+tradeLicenseRequest.getLicenses().get(0).getTradeLicenseDetail().getAddress().getAddressLine1());
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
       repository.save(tradeLicenseRequest);

       //trigger wf 
       workflowIntegrator.callWorkFlow(tradeLicenseRequest);
       
        return tradeLicenseRequest.getLicenses();
	}

    private void enrichPreCreateNewTLValues(TradeLicenseRequest tradeLicenseRequest) {
    	tradeLicenseRequest.getLicenses().forEach(license -> {
    		if(StringUtils.equals(license.getBusinessService(), businessService_NewTL)) {
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
        List<String> rolesWithinTenant = getRolesWithinTenant(criteria.getTenantId(), requestInfo.getUserInfo().getRoles());
        enrichmentService.enrichSearchCriteriaWithAccountId(requestInfo,criteria,rolesWithinTenant);
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
         
         // post search activity
         licenses = enrichPostSearchLicense(licenses,requestInfo,criteria);
         
         return licenses;       
    }
    
	private List<TradeLicense> enrichPostSearchLicense(List<TradeLicense> licenses, RequestInfo requestInfo,
			TradeLicenseSearchCriteria criteria) {

		// calculate passed dates from creation date
        enrichPassedDates(licenses);
        
        // filter role based search TL
        List<TradeLicense> tempLicenses = licenses;
		if (StringUtils.isEmpty(criteria.getApplicationNumber())) {
			tempLicenses = filterLicensesBasedOnRolesWithinTenantId(licenses, requestInfo, criteria);
		}
		
		return tempLicenses;

	}


	private List<TradeLicense> filterLicensesBasedOnRolesWithinTenantId(List<TradeLicense> licenses,
			RequestInfo requestInfo, TradeLicenseSearchCriteria criteria) {
		
		List<TradeLicense> tempLicenses = licenses;
		
		if(StringUtils.equalsIgnoreCase(requestInfo.getUserInfo().getType(), TLConstants.ROLE_CODE_EMPLOYEE)) {
			
			tempLicenses = tempLicenses.stream().filter(license -> 
			!StringUtils.equalsIgnoreCase(license.getStatus(), TLConstants.STATUS_INITIATED))
		.collect(Collectors.toList());
			
			// fetch all ROLES within tenant
			List<String> rolesWithinTenant = getRolesWithinTenant(criteria.getTenantId(), requestInfo.getUserInfo().getRoles());
			
			if (!(rolesWithinTenant.contains(TLConstants.ROLE_CODE_SUPERVISOR)
					|| rolesWithinTenant.contains(TLConstants.ROLE_CODE_SECRETARY))) {
				if (rolesWithinTenant.contains(TLConstants.ROLE_CODE_TL_VERIFIER)) {
					tempLicenses = tempLicenses.stream().filter(license -> !StringUtils
							.equalsIgnoreCase(license.getStatus(), TLConstants.STATUS_PENDINGFORAPPROVAL))
							.collect(Collectors.toList());
				}
				if (rolesWithinTenant.contains(TLConstants.ROLE_CODE_TL_APPROVER)) {
					tempLicenses = tempLicenses.stream()
							.filter(license -> !StringUtils.equalsAnyIgnoreCase(license.getStatus(),
									TLConstants.STATUS_PENDINGFORVERIFICATION, TLConstants.STATUS_PENDINGFORPAYMENT))
							.collect(Collectors.toList());
				} 
			}
			
//			// remove INITIATED applications for EMPLOYEE
//			tempLicenses = licenses.stream().filter(license -> 
//						!StringUtils.equalsIgnoreCase(license.getStatus(), TLConstants.STATUS_INITIATED))
//					.collect(Collectors.toList());
//			
//			// fetch all ROLES within tenant
//			List<String> rolesWithinTenant = getRolesWithinTenant(criteria.getTenantId(), requestInfo.getUserInfo().getRoles());
//			
//			// if !TL_VERIFIER remove PENDINGFORVERIFICATION
//			if(!rolesWithinTenant.contains(TLConstants.ROLE_CODE_TL_VERIFIER)) {
//				tempLicenses = tempLicenses.stream().filter(license -> 
//				!StringUtils.equalsIgnoreCase(license.getStatus(), TLConstants.STATUS_PENDINGFORVERIFICATION))
//			.collect(Collectors.toList());
//			}
//			
//			// if !TL_APPROVER remove PENDINGFORAPPROVAL
//			if(!rolesWithinTenant.contains(TLConstants.ROLE_CODE_TL_APPROVER)) {
//				tempLicenses = tempLicenses.stream().filter(license -> 
//				!StringUtils.equalsIgnoreCase(license.getStatus(), TLConstants.STATUS_PENDINGFORAPPROVAL))
//			.collect(Collectors.toList());
//			}
//			
//			if(CollectionUtils.isEmpty(criteria.getStatus())) {
//				List<TradeLicense> tempLicenses1 = new ArrayList<>();
//				// if no status filter provided in search criteria, show only PENDINGFORVERIFICATION to TL_VERIFIER
//				if(rolesWithinTenant.contains(TLConstants.ROLE_CODE_TL_VERIFIER)) {
//					tempLicenses1.addAll(tempLicenses.stream().filter(license -> 
//					StringUtils.equalsIgnoreCase(license.getStatus(), TLConstants.STATUS_PENDINGFORVERIFICATION))
//							.collect(Collectors.toList()));
//				}
//				// if no status filter provided in search criteria, show only PENDINGFORAPPROVAL to TL_APPROVER
//				if(rolesWithinTenant.contains(TLConstants.ROLE_CODE_TL_APPROVER)) {
//					tempLicenses1.addAll(tempLicenses.stream().filter(license -> 
//					StringUtils.equalsIgnoreCase(license.getStatus(), TLConstants.STATUS_PENDINGFORAPPROVAL))
//							.collect(Collectors.toList()));
//				}
//				tempLicenses = tempLicenses1;
//			}
			
			
		}
		return tempLicenses;
	}





	List<String> getRolesWithinTenant(String tenantId, List<Role> roles) {

		List<String> roleCodes = roles.stream()
				.filter(role -> StringUtils.equalsIgnoreCase(role.getTenantId(), tenantId)).map(role -> role.getCode())
				.collect(Collectors.toList());
		return roleCodes;
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
    	enrichmentService.enrichSearchCriteriaWithAccountId(requestInfo,criteria, null);


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
//        if(applicationType != null && (applicationType).toString().equals(TLConstants.APPLICATION_TYPE_RENEWAL ) 
//        		&& licence.getAction().equalsIgnoreCase(TLConstants.TL_ACTION_INITIATE) 
//                && (licence.getStatus().equals(TLConstants.STATUS_APPROVED) 
//                		|| licence.getStatus().equals(TLConstants.STATUS_MANUALLYEXPIRED) 
//                		|| licence.getStatus().equals(TLConstants.STATUS_EXPIRED) )){
//            List<TradeLicense> createResponse = create(tradeLicenseRequest, businessServicefromPath);
//            licenceResponse =  createResponse;
//        }else{
            if (businessServicefromPath == null)
                businessServicefromPath = businessService_TL;
            tlValidator.validateBusinessService(tradeLicenseRequest, businessServicefromPath);
            Object mdmsData = util.mDMSCall(tradeLicenseRequest.getRequestInfo(), tradeLicenseRequest.getLicenses().get(0).getTenantId());
            Object billingSlabs = util.getBillingSlabs(tradeLicenseRequest.getRequestInfo(), tradeLicenseRequest.getLicenses().get(0).getTenantId());
            String businessServiceName = null;
            switch (businessServicefromPath) {
                case businessService_TL:
                case TLConstants.businessService_NewTL:
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
                case TLConstants.businessService_NewTL:
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

            // create and upload pdf
            createAndUploadPDF(tradeLicenseRequest);
            
            // generate demand and bill
            generateDemandAndBill(tradeLicenseRequest);
            
//            calculationService.addCalculation(tradeLicenseRequest);
            repository.update(tradeLicenseRequest, idToIsStateUpdatableMap);
            licenceResponse=  tradeLicenseRequest.getLicenses();
//        }
        
//        // send notifications
    //  sendTLNotifications(tradeLicenseRequest);
        return licenceResponse;
        
    }



	private void generateDemandAndBill(TradeLicenseRequest tradeLicenseRequest) {
		tradeLicenseRequest.getLicenses().stream().forEach(license -> {
			
			if(StringUtils.equalsIgnoreCase(TLConstants.businessService_NewTL, license.getBusinessService())
					&& StringUtils.equalsIgnoreCase(TLConstants.ACTION_RETURN_TO_INITIATOR_FOR_PAYMENT, license.getAction())) {
				
				// search demands which are taxPeriodTo > current date
//				List<Demand> exisitingDemands = demandService.searchDemand(TLConstants.STATE_LEVEL_TENANT_ID
//															, Collections.singleton(license.getApplicationNumber())
//															,tradeLicenseRequest.getRequestInfo(),license.getBusinessService());
//				
//				List<Demand> exisitingDemandsAfterValidTo = ;
				List<Demand> savedDemands = new ArrayList<>();
//				if(CollectionUtils.isEmpty(exisitingDemands)) {
	            	// generate demand
				log.info("#### create deand license: "+license);
					savedDemands = demandService.generateDemand(tradeLicenseRequest.getRequestInfo(), license, businessService_TL);
//	            }
	            

		        if(CollectionUtils.isEmpty(savedDemands)
//		        		&& CollectionUtils.isEmpty(exisitingDemands)
		        		) {
		            throw new CustomException("INVALID CONSUMERCODE","Bill not generated due to no Demand found for the given consumerCode");
		        }

				// fetch/create bill
	            GenerateBillCriteria billCriteria = GenerateBillCriteria.builder()
	            									.tenantId(TLConstants.STATE_LEVEL_TENANT_ID)
	            									.businessService(license.getBusinessService())
	            									.consumerCode(license.getApplicationNumber()).build();
	            BillResponse billResponse = billService.generateBill(tradeLicenseRequest.getRequestInfo(),billCriteria);
	            
			}
		});
	}


	private TradeLicenseRequest enrichPreUpdateNewTLValues(TradeLicenseRequest tradeLicenseRequest, String businessServicefromPath) {
		
		TradeLicenseRequest tempTradeLicenseRequest = TradeLicenseRequest.builder()
				.requestInfo(tradeLicenseRequest.getRequestInfo())
				.licenses(new ArrayList())
				.build();
		
		for(int i=0; i<tradeLicenseRequest.getLicenses().size(); i++) {
			TradeLicense license = tradeLicenseRequest.getLicenses().get(i);
			String action = license.getAction();
			String comment = license.getComment();
			ApplicationTypeEnum applicationType = license.getApplicationType();
			
			if(null == license.getTradeLicenseDetail()
					&& (StringUtils.equalsIgnoreCase(TLConstants.ACTION_FORWARD_TO_VERIFIER, license.getAction())
							|| StringUtils.equalsIgnoreCase(TLConstants.ACTION_RETURN_TO_INITIATOR_FOR_PAYMENT, license.getAction())
							|| StringUtils.equalsIgnoreCase(TLConstants.ACTION_VERIFY, license.getAction())
							|| StringUtils.equalsIgnoreCase(TLConstants.ACTION_RETURN_TO_INITIATOR, license.getAction())
							|| StringUtils.equalsIgnoreCase(TLConstants.ACTION_FORWARD_TO_APPROVER, license.getAction())
							|| StringUtils.equalsIgnoreCase(TLConstants.ACTION_RETURN_TO_VERIFIER, license.getAction())
							|| StringUtils.equalsIgnoreCase(TLConstants.ACTION_FORWARD_TO_APPROVER, license.getAction())
							|| StringUtils.equalsIgnoreCase(TLConstants.ACTION_APPROVE, license.getAction())
							|| StringUtils.equalsIgnoreCase(TLConstants.ACTION_CLOSE, license.getAction())
							|| StringUtils.equalsIgnoreCase(TLConstants.ACTION_REVOKE, license.getAction()))
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
				licenses.get(0).setComment(comment);
				licenses.get(0).setApplicationType(applicationType);
				
				if(StringUtils.equalsIgnoreCase(TLConstants.STATUS_APPROVED, licenses.get(0).getStatus())
						&& ( StringUtils.equalsIgnoreCase(TLConstants.ACTION_RETURN_TO_INITIATOR, license.getAction())
								|| StringUtils.equalsIgnoreCase(TLConstants.ACTION_RETURN_TO_INITIATOR_FOR_PAYMENT, license.getAction())
								|| StringUtils.equalsIgnoreCase(TLConstants.ACTION_FORWARD_TO_VERIFIER, license.getAction())
								|| StringUtils.equalsIgnoreCase(TLConstants.ACTION_CLOSE, license.getAction()))) {
					// this scenario means application is initiated for renewal
					if(null == license.getApplicationType()) {
						throw new CustomException("APPLICATION_TYPE_MISSING","Provide application type.");
					}
					licenses.get(0).setApplicationType(license.getApplicationType());
					if(!StringUtils.equalsIgnoreCase(TLConstants.ACTION_CLOSE, license.getAction())) {
						licenses.get(0).getAuditDetails().setCreatedTime(new Date().getTime());
					}
				}
				
				// calculate passed dates from creation date
		        enrichPassedDates(licenses);
		         
				tempTradeLicenseRequest.getLicenses().add(licenses.get(0));
			}
			else {
				validateInputObjectAndConstraints(license);
				tempTradeLicenseRequest.getLicenses().add(license);
			}
		}
		
		return tempTradeLicenseRequest;
		
	}


//	private ApplicationTypeEnum getApplicationTypeBasedOnOldStatusAndCurrentAction(ApplicationTypeEnum applicationType,
//			TradeLicense tradeLicense, String action) {
//		
//		if(StringUtils.equalsIgnoreCase(tradeLicense.getStatus(), TLConstants.STATUS_APPROVED)
//				&& StringUtils.equalsIgnoreCase(action, TLConstants.ACTION_REVOKE)) {
//			applicationType = ApplicationTypeEnum.REVOKE;
//		}else if(StringUtils.equalsIgnoreCase(tradeLicense.getStatus(), TLConstants.STATUS_APPROVED)
//				&& StringUtils.equalsAnyIgnoreCase(action, TLConstants.ACTION_RETURN_TO_INITIATOR
////														 , TLConstants.ACTION_RETURN_TO_INITIATOR_FOR_PAYMENT
//														 , TLConstants.ACTION_FORWARD_TO_VERIFIER)) {
//			applicationType = ApplicationTypeEnum.MODIFICATION;
//		}else if(StringUtils.equalsIgnoreCase(tradeLicense.getStatus(), TLConstants.STATUS_APPROVED)
//				&& StringUtils.equalsAnyIgnoreCase(action, TLConstants.ACTION_CLOSE)) {
//			applicationType = ApplicationTypeEnum.CLOSURE;
//		}
//		
//		return applicationType;
//	}





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
	        throw new CustomException("INPUT_VALIDATION_FAILED", "Input Data Validation Failed." + errorMessages);
	    }
	}





	private void sendTLNotifications(TradeLicenseRequest tradeLicenseRequest) {
		if(!CollectionUtils.isEmpty(tradeLicenseRequest.getLicenses())) {
			for(TradeLicense license:tradeLicenseRequest.getLicenses()) {
				//licenses.forEach(license -> {
		    		if(StringUtils.equals(license.getBusinessService(), businessService_NewTL)
		    				&& StringUtils.equals(license.getStatus(), constants.STATUS_PENDINGFORVERIFICATION)) {
		    			tlNotificationService.process(tradeLicenseRequest);
		    		//}
			}
			
	    		
				/*
				 * if(StringUtils.equals(license.getBusinessService(), businessService_TL) &&
				 * StringUtils.equals(license.getStatus(), STATUS_APPROVED)) { // send
				 * notification to license owner List<String> mobileNumbers =
				 * license.getTradeLicenseDetail().getOwners().stream().map(owner ->
				 * owner.getMobileNumber()).collect(Collectors.toList());
				 * sendSmsNotification(mobileNumbers, STATUS_APPROVED,
				 * license.getApplicationNumber()); }
				 * if(StringUtils.equals(license.getBusinessService(), businessService_TL) &&
				 * StringUtils.equals(license.getStatus(), STATUS_PENDINGFORMODIFICATION)) { //
				 * send notification to license owner List<String> mobileNumbers =
				 * license.getTradeLicenseDetail().getOwners().stream().map(owner ->
				 * owner.getMobileNumber()).collect(Collectors.toList());
				 * sendSmsNotification(mobileNumbers, STATUS_PENDINGFORMODIFICATION,
				 * license.getApplicationNumber()); }
				 */
	    	}//);
		}
    	
    	
	}


	private void sendSmsNotification(List<String> mobileNumbers, String statusApproved, String applicationNumber) {
//		sendMail;
	}


	private void createAndUploadPDF(TradeLicenseRequest tradeLicenseRequest) {
		
		
		if (!CollectionUtils.isEmpty(tradeLicenseRequest.getLicenses())) {
			tradeLicenseRequest.getLicenses().stream().forEach(license -> {

				// for NEW TL
				if ((StringUtils.equalsIgnoreCase(license.getBusinessService(), TLConstants.businessService_NewTL)
						&& StringUtils.equalsIgnoreCase(license.getApplicationType().name(),
								TLConstants.APPLICATION_TYPE_NEW)
						&& StringUtils.equalsIgnoreCase(license.getAction(), TLConstants.ACTION_APPROVE)) || // for
																												// RENEWAL
																												// TL
						(StringUtils.equalsIgnoreCase(license.getBusinessService(), TLConstants.businessService_NewTL)
								&& StringUtils.equalsIgnoreCase(license.getApplicationType().name(),
										TLConstants.APPLICATION_TYPE_RENEWAL)
								&& StringUtils.equalsIgnoreCase(license.getAction(), TLConstants.ACTION_APPROVE))) {
					
					
					TradeLicenseRequest tradeLicenseRequest1 = TradeLicenseRequest.builder()
							.requestInfo(tradeLicenseRequest.getRequestInfo())
							.licenses(Collections.singletonList(license))
							.build();
					
					producer.push("save-tl-certificate", tradeLicenseRequest1);
					
				}

			});
		}
		
		
		
		
		
//		if (!CollectionUtils.isEmpty(tradeLicenseRequest.getLicenses())) {
//			tradeLicenseRequest.getLicenses().stream().forEach(license -> {
//
//				Thread pdfGenerationThread = new Thread(() -> {
//
//					// for NEW TL
//					if ((StringUtils.equalsIgnoreCase(license.getBusinessService(), TLConstants.businessService_NewTL)
//							&& StringUtils.equalsIgnoreCase(license.getApplicationType().name(),
//									TLConstants.APPLICATION_TYPE_NEW)
//							&& StringUtils.equalsIgnoreCase(license.getAction(), TLConstants.ACTION_APPROVE)) || // for RENEWAL TL
//							(StringUtils.equalsIgnoreCase(license.getBusinessService(),
//									TLConstants.businessService_NewTL)
//									&& StringUtils.equalsIgnoreCase(license.getApplicationType().name(),
//											TLConstants.APPLICATION_TYPE_RENEWAL)
//									&& StringUtils.equalsIgnoreCase(license.getAction(), TLConstants.ACTION_APPROVE))) {
//
//						// validate trade license
//						validateTradeLicenseCertificateGeneration(license, tradeLicenseRequest.getRequestInfo());
//
//						// create pdf
//						Resource resource = createNoSavePDF(license, tradeLicenseRequest.getRequestInfo());
//
//						//upload pdf
//						DmsRequest dmsRequest = generateDmsRequestByTradeLicense(resource, license,
//								tradeLicenseRequest.getRequestInfo());
//						try {
//							DMSResponse dmsResponse = alfrescoService.uploadAttachment(dmsRequest,
//									tradeLicenseRequest.getRequestInfo());
//						} catch (IOException e) {
//							throw new CustomException("UPLOAD_ATTACHMENT_FAILED",
//									"Upload Attachment failed." + e.getMessage());
//						}
//					}
//
//				});
//
//				pdfGenerationThread.start();
//
//			});
//		}
		
		
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
			throw new CustomException("STATUS_CHANGE_FAILED", "Provided application failed to change status.");
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
			throw new CustomException("NO_TRADE_LICENSE_FOUND", "No Trade license found for given input.");
		}
		TradeLicense tl = licenses.get(0);
		
		// validate current status
		if(StringUtils.equals(applicationStatusChangeRequest.getApplicationStatus(), STATUS_APPLIED)) {
			if(!(StringUtils.equals(tl.getStatus(), STATUS_INITIATED)
				|| StringUtils.equals(tl.getStatus(), ACTION_STATUS_APPROVED)
				|| StringUtils.equals(tl.getStatus(), STATUS_PENDINGFORMODIFICATION))) {
				throw new CustomException("STATUS_CHANGE_NOT_ALLOWED", "Currently Status can't be changed to " + STATUS_APPLIED);
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
					.filter(license -> StringUtils.equalsAnyIgnoreCase(license.getStatus()
							, TLConstants.STATUS_PENDINGFORVERIFICATION
							, TLConstants.STATUS_PENDINGFORAPPROVAL
							, TLConstants.STATUS_PENDINGFORMODIFICATION)).count());
			response.setApplicationPendingForPayment((int) response.getLicenses().stream()
					.filter(license -> StringUtils.equalsIgnoreCase(TLConstants.STATUS_PENDINGFORPAYMENT, license.getStatus())).count());
			response.setApplicationRejected((int) response.getLicenses().stream()
					.filter(license -> StringUtils.equalsIgnoreCase(STATUS_REJECTED, license.getStatus())).count());
			response.setApplicationApproved((int) response.getLicenses().stream()
					.filter(license -> StringUtils.equalsIgnoreCase(STATUS_APPROVED, license.getStatus()))
					.count());
			response.setApplicationRevoked((int) response.getLicenses().stream()
					.filter(license -> StringUtils.equalsIgnoreCase(TLConstants.STATUS_REVOKED, license.getStatus()))
					.count());
			response.setApplicationClosed((int) response.getLicenses().stream()
					.filter(license -> StringUtils.equalsIgnoreCase(TLConstants.STATUS_CLOSED, license.getStatus()))
					.count());
		}
		
		
	}
	
	public Resource createNoSavePDF(TradeLicense tradeLicense, RequestInfo requestInfo) {
		
		// generate pdf
		PDFRequest pdfRequest = generatePdfRequestByTradeLicense(tradeLicense, requestInfo);
		Resource resource = reportService.createNoSavePDF(pdfRequest);
		
		return resource;
	}


	public void validateTradeLicenseCertificateGeneration(TradeLicense tradeLicense, RequestInfo requestInfo) {
		
		if (StringUtils.isEmpty(tradeLicense.getLicenseNumber())
			    && StringUtils.isEmpty(tradeLicense.getApplicationNumber())
			    && StringUtils.isEmpty(tradeLicense.getTradeName())
			    && StringUtils.isEmpty(tradeLicense.getTradeLicenseDetail().getAddress().getAddressLine1())
			    && (tradeLicense.getTradeLicenseDetail().getAddress().getAdditionalDetail().get("district") == null 
			        || StringUtils.isEmpty(tradeLicense.getTradeLicenseDetail().getAddress().getAdditionalDetail().get("district").asText()))
			    && (tradeLicense.getTradeLicenseDetail().getAddress().getAdditionalDetail().get("wardName") == null 
			        || StringUtils.isEmpty(tradeLicense.getTradeLicenseDetail().getAddress().getAdditionalDetail().get("wardName").asText()))
			    && StringUtils.isEmpty(tradeLicense.getTradeLicenseDetail().getAddress().getPincode())
			    && tradeLicense.getIssuedDate() == null
			    && tradeLicense.getValidTo() == null
			    && (tradeLicense.getTradeLicenseDetail().getAdditionalDetail().get("tradeCategory") == null 
			        || StringUtils.isEmpty(tradeLicense.getTradeLicenseDetail().getAdditionalDetail().get("tradeCategory").asText()))
			    && (tradeLicense.getTradeLicenseDetail().getAdditionalDetail().get("applicantName") == null 
			        || StringUtils.isEmpty(tradeLicense.getTradeLicenseDetail().getAdditionalDetail().get("applicantName").asText()))
			    && (tradeLicense.getTradeLicenseDetail().getAdditionalDetail().get("applicantMobileNumber") == null 
			        || StringUtils.isEmpty(tradeLicense.getTradeLicenseDetail().getAdditionalDetail().get("applicantMobileNumber").asText()))
			    && StringUtils.isEmpty(tradeLicense.getBusinessService())
			    && null == requestInfo && null == requestInfo.getUserInfo() && null == requestInfo.getUserInfo().getUserName()) {
			    
			    throw new CustomException("NULL_APPLICATION_NUMBER","PDF can't be generated with null values for application number: "+tradeLicense.getApplicationNumber());
			}
	}





	public DmsRequest generateDmsRequestByTradeLicense(Resource resource, TradeLicense tradeLicense,
			RequestInfo requestInfo) {

//		MultipartFile multipartFile = convertResourceToMultipartFile(resource, "filename");

		DmsRequest dmsRequest = DmsRequest.builder().userId(requestInfo.getUserInfo().getId().toString())
				.objectId(tradeLicense.getId()).description(TLConstants.ALFRESCO_COMMON_CERTIFICATE_DESCRIPTION).id(TLConstants.ALFRESCO_COMMON_CERTIFICATE_ID).type(TLConstants.ALFRESCO_COMMON_CERTIFICATE_TYPE).objectName(businessService_NewTL)
				.comments(TLConstants.ALFRESCO_TL_CERTIFICATE_COMMENT).status(STATUS_APPROVED).file(resource).servicetype(tradeLicense.getBusinessService())
				.documentType(TLConstants.ALFRESCO_DOCUMENT_TYPE).documentId(TLConstants.ALFRESCO_COMMON_DOCUMENT_ID).build();

		return dmsRequest;
	}


	private PDFRequest generatePdfRequestByTradeLicense(TradeLicense tradeLicense, RequestInfo requestInfo) {
		
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> map2 = generateDataForTradeLicensePdfCreate(tradeLicense, requestInfo);
		
		map.put("tl", map2);
		
		PDFRequest pdfRequest = PDFRequest.builder()
				.RequestInfo(requestInfo)
				.key("TradeLicense2")
				.tenantId("hp")
				.data(map)
				.build();
		
		return pdfRequest;
	}


	private Map<String, Object> generateDataForTradeLicensePdfCreate(TradeLicense tradeLicense, RequestInfo requestInfo) {

		Map<String, Object> tlObject = new HashMap<>();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		String approvalTime = dateFormat.format(new Date(tradeLicense.getIssuedDate()));
		String licenseValidity = dateFormat.format(new Date(tradeLicense.getValidTo()));
		String licenseIssueDate = dateFormat.format(new Date(tradeLicense.getIssuedDate()));
		
		// map variables and values
		tlObject.put("tradeLicenseNo", tradeLicense.getLicenseNumber());//Trade License No
		tlObject.put("tradeRegistrationNo", tradeLicense.getApplicationNumber()); //Trade Registration No
		tlObject.put("tradeName", tradeLicense.getTradeName());//Trade Name
		tlObject.put("tradePremisesAddress", tradeLicense.getTradeLicenseDetail().getAddress().getAddressLine1().concat(", ")
			.concat(tradeLicense.getTradeLicenseDetail().getAddress().getAdditionalDetail().get("district").asText()).concat(", ")
			.concat(tradeLicense.getTradeLicenseDetail().getAddress().getAdditionalDetail().get("wardName").asText()).concat(", ")
			.concat(tradeLicense.getTradeLicenseDetail().getAddress().getPincode()));// Trade Premises Address
		tlObject.put("licenseIssueDate", licenseIssueDate);// License Issue Date
		tlObject.put("licenseValidity", licenseValidity);//License Validity
		tlObject.put("licenseCategory", tradeLicense.getTradeLicenseDetail().getAdditionalDetail().get("tradeCategory").asText());// License Category
		tlObject.put("licenseSubCategory", tradeLicense.getTradeLicenseDetail().getAdditionalDetail().get("tradeSubType").asText());// License Sub Category
		tlObject.put("licenseApplicantName", tradeLicense.getTradeLicenseDetail().getAdditionalDetail().get("applicantName").asText());// License Applicant Name
		tlObject.put("applicantContactNo", tradeLicense.getTradeLicenseDetail().getAdditionalDetail().get("applicantMobileNumber").asText());// Applicant Contact No
		tlObject.put("ulbType", tradeLicense.getTradeLicenseDetail().getAddress().getAdditionalDetail().get("ulbType").asText());// ulbType
		tlObject.put("ulbName", tradeLicense.getTradeLicenseDetail().getAddress().getAdditionalDetail().get("ulbName").asText());// ulbName
		tlObject.put("licenseeMobileNumber", !CollectionUtils.isEmpty(tradeLicense.getTradeLicenseDetail().getOwners()) ? tradeLicense.getTradeLicenseDetail().getOwners().get(0).getMobileNumber() : null);// Applicant Address
		tlObject.put("approverName", null!=requestInfo.getUserInfo() ? requestInfo.getUserInfo().getUserName() : null);// Approver Name
		tlObject.put("approvalTime", approvalTime);// Approval Time
		tlObject.put("ownerName", !CollectionUtils.isEmpty(tradeLicense.getTradeLicenseDetail().getOwners()) ? tradeLicense.getTradeLicenseDetail().getOwners().get(0).getName().toUpperCase() : null);// Owner Name
		// generate QR code from attributes
		StringBuilder qr = new StringBuilder();
		getQRCodeForPdfCreate(tlObject, qr);
		
		tlObject.put("qrCodeText", qr.toString());
		
		return tlObject;
	}





	private void getQRCodeForPdfCreate(Map<String, Object> tlObject, StringBuilder qr) {
		tlObject.entrySet().stream()
		.filter(entry1 -> Arrays.asList("tradeLicenseNo","tradeRegistrationNo","tradeName","tradePremisesAddress","licenseIssueDate","licenseValidity","licenseCategory","licenseApplicantName","applicantContactNo","applicantAddress")
		.contains(entry1.getKey())).forEach(entry -> {
			qr.append(entry.getKey());
			qr.append(": ");
			qr.append(entry.getValue());
			qr.append("\r\n");
		});
		
	    replaceInStringBuilder(qr, "tradeLicenseNo", "Trade License No");
	    replaceInStringBuilder(qr, "tradeRegistrationNo", "Trade Registration No");
	    replaceInStringBuilder(qr, "tradeName", "Trade Name");
	    replaceInStringBuilder(qr, "tradePremisesAddress", "Trade Premises Address");
	    replaceInStringBuilder(qr, "licenseIssueDate", "License Issue Date");
	    replaceInStringBuilder(qr, "licenseValidity", "License Validity");
	    replaceInStringBuilder(qr, "licenseCategory", "License Category");
	    replaceInStringBuilder(qr, "licenseApplicantName", "License Applicant Name");
	    replaceInStringBuilder(qr, "applicantContactNo", "Applicant Contact No");
	    replaceInStringBuilder(qr, "applicantAddress", "Applicant Address");
		
	}
	
	private void replaceInStringBuilder(StringBuilder sb, String target, String replacement) {
	    int start;
	    while ((start = sb.indexOf(target)) != -1) {
	        sb.replace(start, start + target.length(), replacement);
	    }
	}





	public TradeLicenseActionResponse getActionsOnApplication(TradeLicenseActionRequest tradeLicenseActionRequest) {
		
		if(CollectionUtils.isEmpty(tradeLicenseActionRequest.getApplicationNumbers())) {
			throw new CustomException("INVALID REQUEST","Provide Application Number.");
		}
		
		Map<String, List<String>> applicationActionMaps = new HashMap<>();
		
		tradeLicenseActionRequest.getApplicationNumbers().stream().forEach(applicationNumber -> {
			
			// search application number
			TradeLicenseSearchCriteria criteria = TradeLicenseSearchCriteria.builder()
					.applicationNumber(applicationNumber)
					.build();
			List<TradeLicense> licenses = repository.getLicenses(criteria);
			if(CollectionUtils.isEmpty(licenses)) {
				throw new CustomException("LICENSE_NOT_FOUND","No License found with provided input.");
			}
			
			TradeLicense license = null != licenses ? licenses.get(0): null;
			
			String applicationStatus = license.getStatus();
			String applicationTenantId = license.getTenantId();
			String applicationBusinessId = license.getBusinessService();
			List<String> rolesWithinTenant = getRolesWithinTenant(applicationTenantId, tradeLicenseActionRequest.getRequestInfo().getUserInfo().getRoles());
			
			
			// fetch business service search
			StringBuilder uri = new StringBuilder(config.getWfHost());
			uri.append(config.getWfBusinessServiceSearchPath());
			uri.append("?tenantId=").append(applicationTenantId);
			uri.append("&businessServices=").append(applicationBusinessId);
			RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder()
					.requestInfo(tradeLicenseActionRequest.getRequestInfo()).build();
			LinkedHashMap<String, Object> responseObject = (LinkedHashMap<String, Object>) restCallRepository.fetchResult(uri, requestInfoWrapper);
			BusinessServiceResponse businessServiceResponse = objectMapper.convertValue(responseObject
																					, BusinessServiceResponse.class);
			
			if(null == businessServiceResponse || CollectionUtils.isEmpty(businessServiceResponse.getBusinessServices())) {
				throw new CustomException("NO_BUSINESS_SERVICE_FOUND","Business service not found for application number: "+applicationNumber);
			}
			List<State> stateList = businessServiceResponse.getBusinessServices().get(0).getStates().stream()
					.filter(state -> StringUtils.equalsIgnoreCase(state.getApplicationStatus(), applicationStatus))
					.collect(Collectors.toList());
			
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
		
		TradeLicenseActionResponse tradeLicenseActionResponse = TradeLicenseActionResponse.builder().applicationDetails(nextActionList).build();
		return tradeLicenseActionResponse;
	
	}





	public TradeLicenseActionResponse getApplicationDetails(TradeLicenseActionRequest tradeLicenseActionRequest) {
		
		if(CollectionUtils.isEmpty(tradeLicenseActionRequest.getApplicationNumbers())) {
			throw new CustomException("INVALID REQUEST","Provide Application Number.");
		}
		
		TradeLicenseActionResponse tradeLicenseActionResponse = TradeLicenseActionResponse.builder()
																.applicationDetails(new ArrayList<>())
																.build();
//		Map<String, Object> totalTaxMap = new HashMap<>();
		
		tradeLicenseActionRequest.getApplicationNumbers().stream().forEach(applicationNumber -> {
			
			// search application number
			TradeLicenseSearchCriteria criteria = TradeLicenseSearchCriteria.builder()
					.applicationNumber(applicationNumber)
					.build();
			List<TradeLicense> licenses = repository.getLicenses(criteria);
			licenses = enrichmentService.enrichTradeLicenseSearch(licenses,criteria,tradeLicenseActionRequest.getRequestInfo());
			TradeLicense license = null != licenses ? licenses.get(0): null;
			

			ApplicationDetail applicationDetail = getApplicationBillUserDetail(license, tradeLicenseActionRequest.getRequestInfo());
			
			
			tradeLicenseActionResponse.getApplicationDetails().add(applicationDetail);
		});
		
		return tradeLicenseActionResponse;
	}


	public ApplicationDetail getApplicationBillUserDetail(TradeLicense license, RequestInfo requestInfo) {
		ApplicationDetail applicationDetail = ApplicationDetail.builder()
												.applicationNumber(license.getApplicationNumber())
												.build();
		String businessService = null;
	    String scaleOfBusiness = null;
	    String tradeCategory = null;
	    Integer periodOfLicense = 0;
	    String zone = null;
	    
		if(StringUtils.equalsIgnoreCase(license.getBusinessService(), TLConstants.businessService_NewTL)) {
			getApplicationBillUserDetailForNewTL(applicationDetail, license, requestInfo, businessService, scaleOfBusiness, tradeCategory, periodOfLicense, zone);
		}
		else if(StringUtils.equalsIgnoreCase(license.getBusinessService(), TLConstants.businessService_TL)) {

		}
		
		return applicationDetail;
	}





	private ApplicationDetail getApplicationBillUserDetailForNewTL(ApplicationDetail applicationDetail, TradeLicense license, RequestInfo requestInfo, String businessService, String scaleOfBusiness
							, String tradeCategory, Integer periodOfLicense, String zone) {
		
		BigDecimal totalFee;
		// reading values from TL in DB
		try {
		    businessService = license.getBusinessService();
		    scaleOfBusiness = license.getTradeLicenseDetail().getAdditionalDetail().get(TLConstants.SCALE_OF_BUSINESS).asText();
		    tradeCategory = license.getTradeLicenseDetail().getAdditionalDetail().get(TLConstants.TRADE_CATEGORY).asText();
		    periodOfLicense = license.getTradeLicenseDetail().getAdditionalDetail().get(TLConstants.PERIOD_OF_LICENSE).asInt();
		    zone = license.getTradeLicenseDetail().getAddress().getAdditionalDetail().get(TLConstants.ZONE).asText();
		} catch (Exception e) {
			throw new CustomException("FETCH_LICENSE_FAILED","Failed to fetch values from trade license while calculating tax.");
		}
		
		// validating values from TL in DB
		if(StringUtils.isEmpty(scaleOfBusiness)
				|| StringUtils.isEmpty(zone)
				|| StringUtils.isEmpty(tradeCategory)) {
			throw new CustomException("EMPTY_VALUES","Tax can't be calculated for empty values.");
		}
		
		// mdms call
		MdmsResponse mdmsDataResponse = tradeUtil.mDMSCallCalculateFee(requestInfo, license, scaleOfBusiness, periodOfLicense, zone, tradeCategory);
		
		
		List<Object> feeStructure = mdmsDataResponse.getMdmsRes().get(TLConstants.TRADE_LICENSE).get(TLConstants.FEE_STRUCTURE);
		Double scaleOfBusinessToLicensePeriodPrice = 0.00;
		Double tradeCategoryPrice = 0.00;
		Double zonePrice = 0.00;
		
		// reading values from mdms
		for (Object object : feeStructure) {
		    Map<String, Object> map = (Map<String, Object>) object;
		    
		    if (StringUtils.equalsIgnoreCase(map.get(TLConstants.STRUCTURE_OF).toString(), TLConstants.ZONE) &&
		        StringUtils.equalsIgnoreCase(map.get(TLConstants.TYPE).toString(), zone)) {
		        zonePrice = ((Number) map.get(TLConstants.PRICE)).doubleValue();
		    }
		    else if (StringUtils.equalsIgnoreCase(map.get(TLConstants.STRUCTURE_OF).toString(), TLConstants.TRADE_CATEGORY) &&
		            StringUtils.equalsIgnoreCase(map.get(TLConstants.TYPE).toString(), tradeCategory)) {
		    	tradeCategoryPrice = ((Number) map.get(TLConstants.PRICE)).doubleValue();
		    }
		    else if (StringUtils.equalsIgnoreCase(map.get(TLConstants.STRUCTURE_OF).toString(), TLConstants.SCALE_OF_BUSINESS) &&
		            StringUtils.equalsIgnoreCase(map.get(TLConstants.TYPE).toString(), scaleOfBusiness) &&
		            map.get(TLConstants.PERIOD_OF_LICENSE) == periodOfLicense) {
		    	scaleOfBusinessToLicensePeriodPrice = ((Number) map.get(TLConstants.PRICE)).doubleValue();
		    }
		}
		
		// total fee for NewTL
		totalFee = BigDecimal.valueOf(scaleOfBusinessToLicensePeriodPrice + (tradeCategoryPrice * zonePrice));
		applicationDetail.setTotalPayableAmount(totalFee);
		
//		Scale Of Business( <b>Small</b>) and License Period ( <b>1 Year </b> ) : <b>15.0</b> <br /> + [Zone (<b>zone1</b>): <b>50.0<b><br /> * Category (<b>Agriculture</b>): <b>0.0</b> ]
		
		// formula for NewTL
		StringBuilder feeCalculationFormula = new StringBuilder("Scale Of Business( <b>").append(scaleOfBusiness);
		feeCalculationFormula.append("</b> ) and License Period ( <b>").append(periodOfLicense.toString());
		feeCalculationFormula.append("</b> Year ) : <b>").append(scaleOfBusinessToLicensePeriodPrice);
		feeCalculationFormula.append("</b> <br />  +  [Zone ( <b>").append(zone);
		feeCalculationFormula.append("</b>): <b>").append(zonePrice);
		feeCalculationFormula.append("</b> <br />* Category ( <b>").append(tradeCategory);
		feeCalculationFormula.append("</b>): <b>").append(tradeCategoryPrice).append("</b> ]");
		
		applicationDetail.setFeeCalculationFormula(feeCalculationFormula.toString());
		
		// search bill Details
		BillSearchCriteria billSearchCriteria = BillSearchCriteria.builder()
				.tenantId(TLConstants.STATE_LEVEL_TENANT_ID)
				.consumerCode(Collections.singleton(applicationDetail.getApplicationNumber()))
				.service(license.getBusinessService())
				.build();
		BillResponse billResponse = billService.searchBill(billSearchCriteria,requestInfo);
		Map<Object, Object> billDetailsMap = new HashMap<>();
		if (!CollectionUtils.isEmpty(billResponse.getBill())) {
			billDetailsMap.put("billId", billResponse.getBill().get(0).getId());
			applicationDetail.setTotalPayableAmount(billResponse.getBill().get(0).getTotalAmount());
		}
		applicationDetail.setBillDetails(billDetailsMap);
		
		// enrich userDetails
		Map<Object, Object> userDetails = new HashMap<>();
		try {
			userDetails.put("UserName", license.getTradeName());
			userDetails.put("MobileNo", license.getTradeLicenseDetail().getOwners().get(0).getMobileNumber());
			userDetails.put("Email", license.getTradeLicenseDetail().getOwners().get(0).getEmailId());
			userDetails.put("Address", new String(license.getTradeLicenseDetail().getAddress().getAddressLine1().concat(", "))
										.concat(license.getTradeLicenseDetail().getAddress().getAdditionalDetail().get("wardName").asText().concat(", "))
										.concat(license.getTradeLicenseDetail().getAddress().getAdditionalDetail().get("ulbName").asText().concat(", "))
										.concat(license.getTradeLicenseDetail().getAddress().getAdditionalDetail().get("district").asText().concat(", "))
										.concat(license.getTradeLicenseDetail().getAddress().getPincode()));
		} catch (Exception e) {
			userDetails.put("Address", null);
			log.info("Some mendatory fields are missing for user details: "+ e.getMessage());
		}

		applicationDetail.setUserDetails(userDetails);
		
		return applicationDetail;
	}





	public TradeLicenseActionResponse getCountOfAllApplicationTypes(TradeLicenseActionRequest tradeLicenseActionRequest) {
		TradeLicenseActionResponse tradeLicenseActionResponse = TradeLicenseActionResponse.builder().build();
		List<String> statusList = null;
		
		// validate request if required
		
		// fetch data
		try {
			if(null != tradeLicenseActionRequest) {
				statusList = tlRepository.getTypesOfAllApplications(tradeLicenseActionRequest.getIsHistoryCall(),tradeLicenseActionRequest.getTenantId());
			}
			if (!CollectionUtils.isEmpty(statusList)) {
				tradeLicenseActionResponse
						.setApplicationTypesCount(statusList.stream().filter(status -> StringUtils.isNotEmpty(status))
								.collect(Collectors.groupingBy(String::toString, Collectors.counting())));
			}
		} catch (Exception e) {
			throw new CustomException("FAILED_TO_FETCH","Failed to fetch Application types.");
		}
		
		return tradeLicenseActionResponse;
	}

}
