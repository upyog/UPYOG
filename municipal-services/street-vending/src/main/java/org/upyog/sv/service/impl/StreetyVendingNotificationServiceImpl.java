package org.upyog.sv.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.sv.config.StreetVendingConfiguration;
import org.upyog.sv.constants.StreetVendingConstants;
import org.upyog.sv.repository.ServiceRequestRepository;
import org.upyog.sv.service.StreetVendingEncryptionService;
import org.upyog.sv.service.StreetyVendingNotificationService;
import org.upyog.sv.util.NotificationUtil;
import org.upyog.sv.web.models.StreetVendingDetail;
import org.upyog.sv.web.models.StreetVendingRequest;
import org.upyog.sv.web.models.events.Event;
import org.upyog.sv.web.models.events.EventRequest;
import org.upyog.sv.web.models.events.Recepient;
import org.upyog.sv.web.models.events.Source;
import org.upyog.sv.web.models.notification.EmailRequest;
import org.upyog.sv.web.models.notification.SMSRequest;

import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StreetyVendingNotificationServiceImpl implements StreetyVendingNotificationService {

	@Autowired
	private StreetVendingConfiguration config;

	@Autowired
	private NotificationUtil util;
	
	/**
	 * Processes the Street Vending request by generating events, sending notifications, 
	 * and handling SMS and email notifications based on the provided status.
	 *
	 * @param request the StreetVendingRequest containing street vending details
	 * @param status  the status of the street vending request
	 */
	public void process(StreetVendingRequest request, String status) {
		EventRequest eventRequest = getEventsForSV(request);
		log.info("Event Request in StreetVending process method" + eventRequest.toString());
		 String tenantId = request.getStreetVendingDetail().getTenantId();
	        RequestInfo requestInfo = request.getRequestInfo();

	        String localizationMessages = util.getLocalizationMessages(tenantId, requestInfo);
	        String messageMap = util.getCustomizedMsg(requestInfo, request.getStreetVendingDetail(), localizationMessages);
	        log.info("Event Request in CND service process method: {}", eventRequest);

	        Set<String> mobileNumbers = new HashSet<>(util.fetchUserUUIDs(new HashSet<>(), requestInfo, tenantId).keySet());
	        List<String> configuredChannelNames = util.fetchChannelList(new RequestInfo(), tenantId.split("\\.")[0], config.getModuleName(), status);

	        // Send app notification
	        if (eventRequest != null) {
	            util.sendEventNotification(eventRequest);
	        }

	        // Send SMS notification
	        if (isNotificationEnabled(config.getIsSMSNotificationEnabled(), configuredChannelNames, StreetVendingConstants.CHANNEL_NAME_SMS)) {
	            List<SMSRequest> smsRequests = new LinkedList<>();
	            util.enrichSMSRequest(request, smsRequests);
	            if (!CollectionUtils.isEmpty(smsRequests)) {
	                util.sendSMS(smsRequests);
	            }
	        }

	        // Send Email notification
	        if (isNotificationEnabled(config.getIsEmailNotificationEnabled(), configuredChannelNames, StreetVendingConstants.CHANNEL_NAME_EMAIL)) {
	            Map<String, String> mapOfPhnoAndEmail = util.fetchUserEmailIds(mobileNumbers, requestInfo, tenantId);
	            List<EmailRequest> emailRequests = util.createEmailRequest(requestInfo, messageMap, mapOfPhnoAndEmail);
	            util.sendEmail(emailRequests);
	        }

	}
	
	/**
     * Checks if a specific notification type is enabled.
     *
     * @param isEnabled        the flag indicating if the notification type is enabled
     * @param channels         the list of configured channels
     * @param notificationType the notification type to check
     * @return true if the notification is enabled, false otherwise
     */
    private boolean isNotificationEnabled(Boolean isEnabled, List<String> channels, String notificationType) {
        return Boolean.TRUE.equals(isEnabled) && channels.contains(notificationType);
    }


    /**
     * Generates an EventRequest object for a given Street Vending request.
     *
     * @param request the StreetVendingRequest containing details of the street vending application
     * @return an EventRequest containing event details for notifications, or null if no events are generated
     */
	private EventRequest getEventsForSV(StreetVendingRequest request) {

		List<Event> events = new ArrayList<>();
		String tenantId = request.getStreetVendingDetail().getTenantId();
		String localizationMessages = util.getLocalizationMessages(tenantId, request.getRequestInfo());
		List<String> toUsers = new ArrayList<>();
		String mobileNumber = request.getStreetVendingDetail().getVendorDetail().get(0).getMobileNo();

		Map<String, String> mapOfPhoneNoAndUUIDs = util.fetchUserUUIDs(mobileNumber, request.getRequestInfo(), tenantId, request.getStreetVendingDetail());

		if (CollectionUtils.isEmpty(mapOfPhoneNoAndUUIDs.keySet())) {
			log.info("UUID search failed!");
		}

		toUsers.add(mapOfPhoneNoAndUUIDs.get(mobileNumber));
		String message = null;
		message = util.getCustomizedMsg(request.getRequestInfo(), request.getStreetVendingDetail(),
				localizationMessages);
		log.info("Message for event in StreetVending:" + message);
		Recepient recepient = Recepient.builder().toUsers(toUsers).toRoles(null).build();
		log.info("Recipient object in StreetVending:" + recepient.toString());
		events.add(Event.builder().tenantId(tenantId).description(message)
				.eventType(StreetVendingConstants.USREVENTS_EVENT_TYPE)
				.name(StreetVendingConstants.USREVENTS_EVENT_NAME)
				.postedBy(StreetVendingConstants.USREVENTS_EVENT_POSTEDBY).source(Source.WEBAPP).recepient(recepient)
				.eventDetails(null).actions(null).build());

		if (!CollectionUtils.isEmpty(events)) {
			return EventRequest.builder().requestInfo(request.getRequestInfo()).events(events).build();
		} else {
			return null;
		}

	}

	
}
