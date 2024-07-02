package org.egov.wf.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.egov.wf.config.SmsConfig;
import org.egov.wf.config.WorkflowConfig;
import org.egov.wf.repository.ServiceRequestRepository;
import org.egov.wf.util.NotificationUtil;
import org.egov.wf.util.WorkflowConstants;
import org.egov.wf.web.models.ProcessInstanceRequest;
import org.egov.wf.web.models.RequestInfoWrapper;
import org.egov.wf.web.models.SMSRequest;
import org.egov.wf.util.WorkflowConstants.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SMSNotificationService {

	private ServiceRequestRepository serviceRequestRepository;

	private NotificationUtil util;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RestTemplate restTemplate;

	private WorkflowConfig config;
	
	private SmsConfig smsConfig;
	
	@Autowired
    private NotificationUtil notificationUtil;

	@Autowired
	public SMSNotificationService(WorkflowConfig config, SmsConfig smsConfig, NotificationUtil notificationUtil,ServiceRequestRepository serviceRequestRepository,
			NotificationUtil util) {
		this.config = config;
		this.notificationUtil=notificationUtil;
		this.smsConfig = smsConfig;
		this.serviceRequestRepository = serviceRequestRepository;
		this.util = util;
	}

	
	private Map<String, List<String>> getFinalMessage(ProcessInstanceRequest processInstanceRequest) {
        String tenantId = processInstanceRequest.getProcessInstances().get(0).getTenantId();
        String localizationMessage = notificationUtil.getLocalizationMessages(tenantId, processInstanceRequest.getRequestInfo(),"rainmaker-bpa");

        Map<String, List<String>> message = new HashMap<>();
        String messageForEmployee = null;
        /**
         * Confirmation SMS to citizens, when they will raise any complaint
         */
        messageForEmployee = notificationUtil.getCustomizedMsg(WorkflowConstants.AUTO_ESC_SMS_EMPLOYEE , processInstanceRequest.getProcessInstances().get(0).getAction(), WorkflowConstants.EMPLOYEE , localizationMessage);
        
        //BPA_AUTONOTIFICATION_PENDINGFORAPPROVAL_EMPLOYEE_SMS_MESSAGE
            message.put("EMPLOYEE", Arrays.asList(messageForEmployee));

            return message;
        }

	/**
	 * Creates and send the sms based on the bpaRequest
	 * 
	 * @param bpaRequest
	 *            The bpaRequest consumed on the kafka topic
	 */
	public void process(ProcessInstanceRequest processInstanceRequest, String topic) {
        try {
           

            Map<String, List<String>> finalMessage = getFinalMessage(processInstanceRequest);
            log.info("final Message is -========" + finalMessage);
            Map<String, String> reassigneeDetails  = getHRMSEmployee(processInstanceRequest,"BPA_APPROVER");
            
            String employeeMobileNumber =reassigneeDetails.get("employeeMobile");
            log.info("mobile number is -========" + employeeMobileNumber);

            
            if(!finalMessage.isEmpty()){
               

                if (smsConfig.getIsSMSEnabled() != null && smsConfig.getIsSMSEnabled()) {
                    for (Map.Entry<String,List<String>> entry : finalMessage.entrySet()) {
                            for(String msg : entry.getValue()) {
                                List<SMSRequest> smsRequests = new ArrayList<>();
                                smsRequests = enrichSmsRequest(employeeMobileNumber, msg);
                                if (!CollectionUtils.isEmpty(smsRequests)) {
                                    notificationUtil.sendSMS(smsRequests,true);
                                }
                            }
                        }
                    }
                

            }
        }catch (Exception ex) {
            log.error("Error occured while processing the record from topic : " + topic, ex);
        }
    }


	private Map<String, String> fetchUserUUIDs(Set<String> mobileNumbers, RequestInfo requestInfo, String tenantId) {

		Map<String, String> mapOfPhnoAndUUIDs = new HashMap<>();
		StringBuilder uri = new StringBuilder();
		uri.append(config.getUserHost()).append(config.getUserSearchEndpoint());
		Map<String, Object> userSearchRequest = new HashMap<>();
		userSearchRequest.put("RequestInfo", requestInfo);
		userSearchRequest.put("tenantId", tenantId);
		userSearchRequest.put("userType", "CITIZEN");
		for (String mobileNo : mobileNumbers) {
			userSearchRequest.put("userName", mobileNo);
			try {
				Object user = serviceRequestRepository.fetchResult(uri, userSearchRequest);
				if (null != user) {
					String uuid = JsonPath.read(user, "$.user[0].uuid");
					mapOfPhnoAndUUIDs.put(mobileNo, uuid);
				} else {
					log.error("Service returned null while fetching user for username - " + mobileNo);
				}
			} catch (Exception e) {
				log.error("Exception while fetching user for username - " + mobileNo);
				log.error("Exception trace: ", e);
				continue;
			}
		}
		return mapOfPhnoAndUUIDs;
	}

	/**
	 * Enriches the smsRequest with the customized messages
	 * 
	 * @param bpaRequest
	 *            The bpaRequest from kafka topic
	 * @param smsRequests
	 *            List of SMSRequets
	 */
	  private List<SMSRequest> enrichSmsRequest(String mobileNumber, String finalMessage) {
	        List<SMSRequest> smsRequest = new ArrayList<>();
	        SMSRequest req = SMSRequest.builder().mobileNumber(mobileNumber).message(finalMessage).build();
	        smsRequest.add(req);
	        return smsRequest;
	    }

	/**
	 * To get the Users to whom we need to send the sms notifications or event
	 * notifications.
	 * 
	 * @param bpaRequest
	 * @return
	 */
//	private Map<String, String> getUserList(ProcessInstanceRequest processInstanceRequest) {
//		Map<String, String> mobileNumberToOwner = new HashMap<>();
//		String tenantId = processInstanceRequest.getBPA().getTenantId();
//		String stakeUUID = bpaRequest.getBPA().getAuditDetails().getCreatedBy();
//		List<String> ownerId = new ArrayList<String>();
//		ownerId.add(stakeUUID);
//		BPASearchCriteria bpaSearchCriteria = new BPASearchCriteria();
//		bpaSearchCriteria.setOwnerIds(ownerId);
//		bpaSearchCriteria.setTenantId(tenantId);
//		UserDetailResponse userDetailResponse = userService.getUser(bpaSearchCriteria, bpaRequest.getRequestInfo());
//
//		LandSearchCriteria landcriteria = new LandSearchCriteria();
//		landcriteria.setTenantId(bpaSearchCriteria.getTenantId());
//		landcriteria.setIds(Arrays.asList(bpaRequest.getBPA().getLandId()));
//		List<LandInfo> landInfo = bpalandService.searchLandInfoToBPA(bpaRequest.getRequestInfo(), landcriteria);
//
//		mobileNumberToOwner.put(userDetailResponse.getUser().get(0).getUserName(),
//				userDetailResponse.getUser().get(0).getName());
//		
//
//		if (bpaRequest.getBPA().getLandInfo() == null) {
//			for (int j = 0; j < landInfo.size(); j++)
//				bpaRequest.getBPA().setLandInfo(landInfo.get(j));
//		}
//		
//		if (!(bpaRequest.getBPA().getWorkflow().getAction().equals(config.getActionsendtocitizen())
//				&& bpaRequest.getBPA().getStatus().equals("INITIATED"))
//				&& !(bpaRequest.getBPA().getWorkflow().getAction().equals(config.getActionapprove())
//						&& bpaRequest.getBPA().getStatus().equals("INPROGRESS"))) {
//			
//			bpaRequest.getBPA().getLandInfo().getOwners().forEach(owner -> {
//					if (owner.getMobileNumber() != null && owner.getIsPrimaryOwner()) {
//						mobileNumberToOwner.put(owner.getMobileNumber(), owner.getName());
//					}
//			});
//			
//		}
//		return mobileNumberToOwner;
//	}

 public Map<String, String> getHRMSEmployee(ProcessInstanceRequest processInstanceRequest,String role){
        Map<String, String> reassigneeDetails = new HashMap<>();
    
        List<String> employeeName = null;
        List<String> employeeMobile = null;
        List<String> employeeUUID=null;

        StringBuilder url =notificationUtil.getHRMSURI(null,processInstanceRequest.getProcessInstances().get(0).getTenantId(),role);
        RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(processInstanceRequest.getRequestInfo()).build();
        Object response = serviceRequestRepository.fetchResult(url, requestInfoWrapper);

        //MDMS CALL
//        Object mdmsData = mdmsUtils.mDMSCall(request);
//        String jsonPath = MDMS_DEPARTMENT_SEARCH.replace("{SERVICEDEF}",request.getIncident().getIncidentType());
//
//        try{
//            mdmsDepartmentList = JsonPath.read(mdmsData,jsonPath);
//            hrmsDepartmentList = JsonPath.read(response, HRMS_DEPARTMENT_JSONPATH);
//        }
//        catch (Exception e){
//            throw new CustomException("JSONPATH_ERROR","Failed to parse mdms response for department");
//        }
//
//        if(CollectionUtils.isEmpty(mdmsDepartmentList))
//            throw new CustomException("PARSING_ERROR","Failed to fetch department from mdms data for serviceCode: "+request.getIncident().getIncidentType());
//        else departmentFromMDMS = mdmsDepartmentList.get(0);
//
//        if(hrmsDepartmentList.contains(departmentFromMDMS)){
//            String localisedDept = notificationUtil.getCustomizedMsgForPlaceholder(localisationMessageForPlaceholder,"COMMON_MASTERS_DEPARTMENT_"+departmentFromMDMS);
//            reassigneeDetails.put("department",localisedDept);
//        }
//
//        String designationJsonPath = HRMS_DESIGNATION_JSONPATH.replace("{department}",departmentFromMDMS);
//
//        try{
//            designation = JsonPath.read(response, designationJsonPath);
          employeeName = JsonPath.read(response, WorkflowConstants.HRMS_EMP_NAME_JSONPATH);
          employeeMobile=JsonPath.read(response,WorkflowConstants.HRMS_EMP_MOBILE_JSONPATH);
          employeeUUID=JsonPath.read(response, WorkflowConstants.HRMS_EMP_UUID_JSONPATH);
        		  //}
//        catch (Exception e){
//            throw new CustomException("JSONPATH_ERROR","Failed to parse mdms response for department");
//        }
//
//        String localisedDesignation = notificationUtil.getCustomizedMsgForPlaceholder(localisationMessageForPlaceholder,"COMMON_MASTERS_DESIGNATION_"+designation.get(0));
//
//        reassigneeDetails.put("designation",localisedDesignation);
       reassigneeDetails.put("employeeName",employeeName.get(0));
       reassigneeDetails.put("employeeMobile",employeeMobile.get(0));

       reassigneeDetails.put("employeeUUID",employeeUUID.get(0));

        return reassigneeDetails;
    }
}
