package org.egov.ndc.service.notification;

import java.util.*;

import org.egov.common.contract.request.RequestInfo;
import org.egov.ndc.config.NDCConfiguration;
import org.egov.ndc.service.UserService;
import org.egov.ndc.util.NotificationUtil;
import org.egov.ndc.web.model.OwnerInfo;
import org.egov.ndc.web.model.SMSRequest;
import org.egov.ndc.web.model.UserResponse;
import org.egov.ndc.web.model.ndc.Application;
import org.egov.ndc.web.model.ndc.NdcApplicationRequest;
import org.egov.ndc.web.model.ndc.NdcApplicationSearchCriteria;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NDCNotificationService {

	private final NDCConfiguration config;
	private final NotificationUtil util;
	private final UserService userService;

	public NDCNotificationService(NDCConfiguration config, NotificationUtil util, UserService userService) {
		this.config = config;
		this.util = util;
		this.userService = userService;
	}

	/**
	 * Creates and send the sms based on the NDCRequest
	 * 
	 * @param ndcRequest
	 *            The NDCRequest listenend on the kafka topic
	 */
	public void process(NdcApplicationRequest ndcRequest) {
		if (config.getIsSMSEnabled() == null || !config.getIsSMSEnabled()) {
			return;
		}
		List<SMSRequest> smsRequests = new LinkedList<>();
		enrichSMSRequest(ndcRequest, smsRequests);
		if (!CollectionUtils.isEmpty(smsRequests)) {
			util.sendSMS(smsRequests, config.getIsSMSEnabled());
		}
	}

	/**
	 * Enriches the smsRequest with the customized messages
	 * 
	 * @param ndcRequest
	 *            The bpaRequest from kafka topic
	 * @param smsRequests
	 *            List of SMSRequets
	 */
	private void enrichSMSRequest(NdcApplicationRequest ndcRequest, List<SMSRequest> smsRequests) {
		List<Application> applications = ndcRequest.getApplications();
		for(Application application : applications) {
			String tenantId = application.getTenantId();
			String localizationMessages = util.getLocalizationMessages(tenantId, ndcRequest.getRequestInfo());
			String message = util.getCustomizedMsg(application, localizationMessages);
			if (message != null) {
				Map<String, String> mobileNumberToOwner = getUserList(application,ndcRequest.getRequestInfo());
				smsRequests.addAll(util.createSMSRequest(message, mobileNumberToOwner));
			}
		}
		
	}

	/**
	 * To get the Users to whom we need to send the sms notifications or event
	 * notifications.
	 *
	 * @param application
	 * @param requestInfo
	 * @return map of mobile number to owner name
	 */
	private Map<String, String> getUserList(Application application, RequestInfo requestInfo) {
		Map<String, String> mobileNumberToOwner = new HashMap<>();
		String tenantId = application.getTenantId();
		List<String> uuid = application.getOwners().stream().map(OwnerInfo::getUuid).toList();
		Set<String> ownerId = new HashSet<>(uuid);
		NdcApplicationSearchCriteria ndcSearchCriteria = new NdcApplicationSearchCriteria();
		ndcSearchCriteria.setOwnerIds(ownerId);
		ndcSearchCriteria.setTenantId(tenantId);
		UserResponse userDetailResponse = userService.getUser(ndcSearchCriteria, requestInfo);
		mobileNumberToOwner.put(userDetailResponse.getUser().get(0).getMobileNumber(),
				userDetailResponse.getUser().get(0).getName());
		return mobileNumberToOwner;
	}

}
