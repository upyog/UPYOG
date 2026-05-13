package org.egov.ndc.service.notification;

import java.util.*;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.ndc.config.NDCConfiguration;
import org.egov.ndc.repository.ServiceRequestRepository;
import org.egov.ndc.service.UserService;
import org.egov.ndc.util.NotificationUtil;
import org.egov.ndc.web.model.OwnerInfo;
import org.egov.ndc.web.model.SMSRequest;
import org.egov.ndc.web.model.UserResponse;
import org.egov.ndc.web.model.ndc.Application;
import org.egov.ndc.web.model.ndc.NdcApplicationRequest;
import org.egov.ndc.web.model.ndc.NdcApplicationSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NDCNotificationService {

	private NDCConfiguration config;

	private NotificationUtil util;

	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private UserService userService;

	@Autowired
	public NDCNotificationService(NDCConfiguration config, NotificationUtil util,
                                  ServiceRequestRepository serviceRequestRepository) {
		this.config = config;
		this.util = util;
		this.serviceRequestRepository = serviceRequestRepository;
	}

	/**
	 * Creates and send the sms based on the NDCRequest
	 * 
	 * @param request
	 *            The NDCRequest listenend on the kafka topic
	 */
	public void process(NdcApplicationRequest ndcRequest) {
		List<SMSRequest> smsRequests = new LinkedList<>();
		List<Map<String, Object>> emailRequests = new ArrayList<>();
		if (null != config.getIsSMSEnabled()) {
			if (config.getIsSMSEnabled()) {
				enrichSMSRequest(ndcRequest, smsRequests);
				if (!CollectionUtils.isEmpty(smsRequests))
					util.sendSMS(smsRequests, config.getIsSMSEnabled());
			}
			if (config.getIsEmailEnabled()) {
				enrichEmailRequest(ndcRequest, emailRequests);
				 if (!CollectionUtils.isEmpty(emailRequests))
					 util.sendEmail(emailRequests, config.getIsEmailEnabled());
			}
		}
	}
	
	
	

	/**
	 * Enriches the smsRequest with the customized messages
	 * 
	 * @param request
	 *            The bpaRequest from kafka topic
	 * @param smsRequests
	 *            List of SMSRequets
	 */
	private void enrichSMSRequest(NdcApplicationRequest ndcRequest, List<SMSRequest> smsRequests) {
		List<Application> applications = ndcRequest.getApplications();
		for(Application application : applications) {
			String tenantId = application.getTenantId();
			String localizationMessages = util.getLocalizationMessages(tenantId, ndcRequest.getRequestInfo());
			String message = util.getCustomizedMsg(ndcRequest.getRequestInfo(), application, localizationMessages);
			if (message != null) {
				Map<String, String> mobileNumberToOwner = getUserList(application,ndcRequest.getRequestInfo());
				smsRequests.addAll(util.createSMSRequest(message, mobileNumberToOwner));
			}
		}
		
	}
	
	
	/**
	 * Enriches the emailRequest with the customized messages
	 * 
	 * @param request       The bpaRequest from kafka topic
	 * @param emailRequests List of EmailRequets
	 */
	// Change the second parameter from HashMap to List<Map<String, Object>>
	private void enrichEmailRequest(NdcApplicationRequest ndcRequest, List<Map<String, Object>> emailRequests) {
	    
	    for (Application application : ndcRequest.getApplications()) {
	        String tenantId = application.getTenantId();
	        
	        // 1. Fetch the Punjab Govt NDC HTML Template from Localization
	        String localizationMessages = util.getLocalizationMessages(tenantId, ndcRequest.getRequestInfo());
	        
	        // 2. Replace placeholders ({ownerName}, {consumerCode}, etc.)
	        String message = util.getCustomizedMsgForMail(ndcRequest.getRequestInfo(), application, localizationMessages);
	        
	        if (message != null) {
	            // 3. Get the map of Mobile -> Email
	            Map<String, String> emailToOwner = getEmailUserList(application, ndcRequest.getRequestInfo());
	            log.info("emailToOwner: " + emailToOwner);

	            emailRequests.addAll(util.createEmailRequest(message, emailToOwner));
	        }
	    }
	}

	/**
	 * To get the Users to whom we need to send the sms notifications or event
	 * notifications.
	 *
	 * @param ndcRequest
	 * @param requestInfo
	 * @return
	 */
	private Map<String, String> getUserList(Application ndcRequest, RequestInfo requestInfo) {
		Map<String, String> mobileNumberToOwner = new HashMap<>();
		String tenantId = ndcRequest.getTenantId();
//		List<String> mobileNumbers = ndcRequest.getOwners().stream().map(OwnerInfo::getMobileNumber).collect(Collectors.toList());
		List<String> uuid = ndcRequest.getOwners().stream().map(OwnerInfo::getUuid).collect(Collectors.toList());
		Set<String> ownerId = new HashSet<>();
		ownerId.addAll(uuid);
		NdcApplicationSearchCriteria ndcSearchCriteria = new NdcApplicationSearchCriteria();
		ndcSearchCriteria.setOwnerIds(ownerId);
		ndcSearchCriteria.setTenantId(tenantId);
		UserResponse userDetailResponse = userService.getUser(ndcSearchCriteria, requestInfo);
		mobileNumberToOwner.put(userDetailResponse.getUser().get(0).getMobileNumber(),
				userDetailResponse.getUser().get(0).getName());
		return mobileNumberToOwner;
	}
	
	private Map<String, String> getEmailUserList(Application ndcRequest, RequestInfo requestInfo) {
		Map<String, String> emailToOwner = new HashMap<>();
		String tenantId = ndcRequest.getTenantId();
//		List<String> mobileNumbers = ndcRequest.getOwners().stream().map(OwnerInfo::getMobileNumber).collect(Collectors.toList());
		List<String> uuid = ndcRequest.getOwners().stream().map(OwnerInfo::getUuid).collect(Collectors.toList());
		Set<String> ownerId = new HashSet<>();
		ownerId.addAll(uuid);
		NdcApplicationSearchCriteria ndcSearchCriteria = new NdcApplicationSearchCriteria();
		ndcSearchCriteria.setOwnerIds(ownerId);
		ndcSearchCriteria.setTenantId(tenantId);
		UserResponse userDetailResponse = userService.getUser(ndcSearchCriteria, requestInfo);
		emailToOwner.put(userDetailResponse.getUser().get(0).getMobileNumber(),
				userDetailResponse.getUser().get(0).getEmailId()
				);
		return emailToOwner;
	}

}
