package org.ksmart.birth.common.services;

import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.ksmart.birth.common.repository.ServiceRequestRepository;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.utils.NotificationUtil;
import org.ksmart.birth.web.model.EventRequest;
import org.ksmart.birth.web.model.SMSRequest;
import org.ksmart.birth.web.model.birthnac.NacDetailRequest;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import static org.ksmart.birth.utils.BirthConstants.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.awt.image.BufferStrategy;
import java.util.*;
import java.util.stream.Collectors;

import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;

@Slf4j
@Service
public class BirthNotificationService {

	private NotificationUtil util;
	private BirthConfiguration config;
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	public BirthNotificationService(BirthConfiguration config, ServiceRequestRepository serviceRequestRepository,
			NotificationUtil util) {
		this.config = config;
		this.serviceRequestRepository = serviceRequestRepository;
		this.util = util;

	}

	/**
	 * Creates and send the sms based on the BirthRequest
	 * 
	 * @param request The BirthRequest listenend on the kafka topic
	 */
	public void process(NewBirthDetailRequest request,NacDetailRequest nac) {
		
		String mobNo =null;
		String name =null;
		String tenantId = null;
		String action =null;
		String status = null;
		String appNo= null;
		String appType =null;
		 
		RequestInfo requestInfo = new RequestInfo();
		if(request.getNewBirthDetails()  != null) {
			mobNo= request.getNewBirthDetails().get(0).getInitiatorDetails().getInitiatorMobileNo();
			name = request.getNewBirthDetails().get(0).getInitiatorDetails().getInitiatorNameEn();
			action=request.getNewBirthDetails().get(0).getAction();
			status = request.getNewBirthDetails().get(0).getApplicationStatus();
			
			 
			appNo= request.getNewBirthDetails().get(0).getApplicationNo();
			appType="NewBirth Registration";
			
			tenantId = request.getNewBirthDetails().get(0).getTenantId();
			 requestInfo = request.getRequestInfo();
		}
		else if(nac.getNacDetails() != null) {
			mobNo=nac.getNacDetails().get(0).getApplicantDetails().getMobileNo();
			name= nac.getNacDetails().get(0).getApplicantDetails().getApplicantNameEn();
			action=nac.getNacDetails().get(0).getAction();
			status = nac.getNacDetails().get(0).getApplicationStatus();
			tenantId = nac.getNacDetails().get(0).getTenantId();
			appType="Nac Registration";
			appNo= nac.getNacDetails().get(0).getApplicationNo();
			requestInfo = nac.getRequestInfo();
		}
	
 
		Map<Object, Object> configuredChannelList = new HashMap<>(); 
		Set<String> mobileNumbers = new HashSet<>();

		
		mobileNumbers.add(mobNo);
		
		
//		for (NewBirthApplication birth : request.getNewBirthDetails()) {
//
//			if (birth.getInitiatorDetails().getInitiatorMobileNo() != null)
//				mobileNumbers.add(birth.getInitiatorDetails().getInitiatorMobileNo());
// 
//
//		}
 
		List<SMSRequest> smsRequestsBR = new LinkedList<>();
		if (null != config.getIsBRSMSEnabled()) {
			if (config.getIsBRSMSEnabled()) {
				enrichSMSRequest(tenantId, smsRequestsBR,requestInfo, mobNo,name,appNo,appType,action,status,configuredChannelList);
				if (!CollectionUtils.isEmpty(smsRequestsBR))
					util.sendSMS(smsRequestsBR, true);
			}
		}

//					if (null != config.getIsUserEventsNotificationEnabledForBirth()) {
//						if (config.getIsUserEventsNotificationEnabledForBirth()) {
//							EventRequest eventRequest = getEventsForTL(request);
//							if (null != eventRequest)
//								util.sendEventNotification(eventRequest);
//						}
//					}

	}

	/**
	 * Enriches the smsRequest with the customized messages
	 * 
	 * @param request               The tradeLicenseRequest from kafka topic
	 * @param smsRequests           List of SMSRequests
	 * @param configuredChannelList Map of actions mapped to configured channels for
	 *                              this business service for BPAREG flow
	 */
	private void enrichSMSRequest(String tenantId, List<SMSRequest> smsRequests, RequestInfo requestInfo,String mobNo,String name,
			String appNo,String appType,String action,String status,Map<Object, Object> configuredChannelList) {
//		String tenantId = request.getNewBirthDetails().get(0).getTenantId();
	 
			String message = null;
			String localizationMessages = util.getLocalizationMessages(tenantId, requestInfo);			 
			message = util.getCustomizedMsg(requestInfo, action, status,appNo,name,appType,localizationMessages);		 
			if (message == null)
				return;
			Map<String, String> mobileNumberToOwner = new HashMap<>();
		 
				mobileNumberToOwner.put(mobNo,name);
 
			smsRequests.addAll(util.createSMSRequest(message, mobileNumberToOwner));
		 
	}

}
