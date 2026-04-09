package org.egov.noc.service.notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.egov.noc.config.NOCConfiguration;
import org.egov.noc.repository.ServiceRequestRepository;
import org.egov.noc.service.UserService;
import org.egov.noc.util.NOCConstants;
import org.egov.noc.util.NotificationUtil;
import org.egov.noc.web.model.NocRequest;
import org.egov.noc.web.model.NocSearchCriteria;
import org.egov.noc.web.model.SMSRequest;
import org.egov.noc.web.model.User;
import org.egov.noc.web.model.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NOCNotificationService {

	private NOCConfiguration config;

	private NotificationUtil util;

	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private UserService userService;

	@Autowired
	public NOCNotificationService(NOCConfiguration config, NotificationUtil util,
			ServiceRequestRepository serviceRequestRepository) {
		this.config = config;
		this.util = util;
		this.serviceRequestRepository = serviceRequestRepository;
	}

	/**
	 * Creates and send the sms based on the NOCRequest
	 * 
	 * @param request
	 *            The NOCRequest listenend on the kafka topic
	 */
	public void process(NocRequest nocRequest, String rawRecord) {
		
		List<SMSRequest> smsRequests = new LinkedList<>();
		if (null != config.getIsSMSEnabled()) {
			if (config.getIsSMSEnabled()) {
				enrichSMSRequest(nocRequest, smsRequests, rawRecord);
				if (!CollectionUtils.isEmpty(smsRequests))
					util.sendSMS(smsRequests, config.getIsSMSEnabled());
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
	private void enrichSMSRequest(NocRequest nocRequest, List<SMSRequest> smsRequests, String rawRecord) {
		String tenantId = nocRequest.getNoc().getTenantId();
		String localizationMessages = util.getLocalizationMessages(tenantId, nocRequest.getRequestInfo());
		String message = util.getCustomizedMsg(nocRequest.getRequestInfo(), nocRequest.getNoc(), localizationMessages, rawRecord);
		if(message != null){
			Map<String, String> mobileNumberToOwner = getUserList(nocRequest, message);
			smsRequests.addAll(util.createSMSRequest(message, mobileNumberToOwner));
		}
		
	}

	/**
	 * To get the Users to whom we need to send the sms notifications or event
	 * notifications.
	 * 
	 * @param nocRequest
	 * @return
	 */
	private Map<String, String> getUserList(NocRequest nocRequest, String message) {
		Map<String, String> mobileNumberToOwner = new HashMap<>();
		String tenantId = nocRequest.getNoc().getTenantId();
		String stakeUUID = nocRequest.getNoc().getAccountId();
		List<String> ownerId = new ArrayList<String>();
		ownerId.add(stakeUUID);
		ownerId.addAll(nocRequest.getNoc().getOwners().stream().map(User::getUuid).collect(Collectors.toList()));
		
		if(message.split("\n")[0].contains("/"))
			ownerId.add(nocRequest.getNoc().getAuditDetails().getCreatedBy());
		
		NocSearchCriteria nocSearchCriteria = new NocSearchCriteria();
		nocSearchCriteria.setOwnerIds(ownerId);
		nocSearchCriteria.setTenantId(tenantId);
		UserResponse userDetailResponse = userService.getUser(nocSearchCriteria, nocRequest.getRequestInfo());
		userDetailResponse.getUser().stream().forEach(owner -> {
			mobileNumberToOwner.put(owner.getMobileNumber(), owner.getName());
		});
		return mobileNumberToOwner;
	}

}
