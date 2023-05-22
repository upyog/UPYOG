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
	 * Creates and send the sms based on the tradeLicenseRequest
	 * 
	 * @param request The tradeLicenseRequest listenend on the kafka topic
	 */
	public void process(NewBirthDetailRequest request) {
		RequestInfo requestInfo = request.getRequestInfo();
		Map<String, String> mobileNumberToOwner = new HashMap<>();
		String tenantId = request.getNewBirthDetails().get(0).getTenantId();
		String action = request.getNewBirthDetails().get(0).getAction();
		Map<Object, Object> configuredChannelList = new HashMap<>();
//		List<String> configuredChannelNames = Arrays.asList(new String[]{"SMS","EVENT","EMAIL"});
		Set<String> mobileNumbers = new HashSet<>();

		for (NewBirthApplication birth : request.getNewBirthDetails()) {

			if (birth.getInitiatorDetails().getInitiatorMobileNo() != null)
//				mobileNumbers.add(birth.getInitiatorDetails().getInitiatorMobileNo());
				mobileNumbers.add("9747303943");

		}

		List<SMSRequest> smsRequestsBR = new LinkedList<>();
		if (null != config.getIsBRSMSEnabled()) {
			if (config.getIsBRSMSEnabled()) {
				enrichSMSRequest(request, smsRequestsBR, configuredChannelList);
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
	private void enrichSMSRequest(NewBirthDetailRequest request, List<SMSRequest> smsRequests,
			Map<Object, Object> configuredChannelList) {
		String tenantId = request.getNewBirthDetails().get(0).getTenantId();
		for (NewBirthApplication birth : request.getNewBirthDetails()) {

			String message = null;

			String localizationMessages = util.getLocalizationMessages(tenantId, request.getRequestInfo());
			message = util.getCustomizedMsg(request.getRequestInfo(), birth, localizationMessages);

			if (message == null)
				continue;

			Map<String, String> mobileNumberToOwner = new HashMap<>();

			if (birth.getInitiatorDetails().getInitiatorMobileNo() != null)
//				mobileNumberToOwner.put(birth.getInitiatorDetails().getInitiatorMobileNo(),
				mobileNumberToOwner.put("9747303943",
						"Varsha");

			smsRequests.addAll(util.createSMSRequest(message, mobileNumberToOwner));
		}
	}

}
