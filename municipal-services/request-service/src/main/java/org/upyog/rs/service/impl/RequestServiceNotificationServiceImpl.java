package org.upyog.rs.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.rs.config.RequestServiceConfiguration;
import org.upyog.rs.constant.RequestServiceConstants;
import org.upyog.rs.service.RequestServiceNotificationService;
import org.upyog.rs.util.NotificationUtil;
import org.upyog.rs.web.models.events.Action;
import org.upyog.rs.web.models.events.ActionItem;
import org.upyog.rs.web.models.events.Event;
import org.upyog.rs.web.models.events.EventRequest;
import org.upyog.rs.web.models.events.Recepient;
import org.upyog.rs.web.models.events.Source;
import org.upyog.rs.web.models.mobileToilet.MobileToiletBookingRequest;
import org.upyog.rs.web.models.notification.EmailRequest;
import org.upyog.rs.web.models.notification.SMSRequest;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingRequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RequestServiceNotificationServiceImpl implements RequestServiceNotificationService {

	@Autowired
	private RequestServiceConfiguration config;

	@Autowired
	private NotificationUtil util;
	
	/**
	 * Processes a booking request and sends notifications based on the application status.
	 * 
	 * @param request The booking request object, which can be either a {@link WaterTankerBookingRequest} 
	 *                or a {@link MobileToiletBookingRequest}.
	 * @param status  The application status associated with the booking request.
	 * @throws IllegalArgumentException If the request type is not supported.
	 */
	
	public void process(Object request, String status) {
	    String tenantId;
	    RequestInfo requestInfo;
	    Map<String, String> messageMap;
	    String localizationMessages;

	    if (request instanceof WaterTankerBookingRequest) {
	        WaterTankerBookingRequest waterRequest = (WaterTankerBookingRequest) request;
	        tenantId = waterRequest.getWaterTankerBookingDetail().getTenantId();
	        requestInfo = waterRequest.getRequestInfo();
	        localizationMessages = util.getLocalizationMessages(tenantId, requestInfo);
	        messageMap = util.getCustomizedMsg(requestInfo, waterRequest.getWaterTankerBookingDetail(), localizationMessages);
	    } else if (request instanceof MobileToiletBookingRequest) {
	        MobileToiletBookingRequest toiletRequest = (MobileToiletBookingRequest) request;
	        tenantId = toiletRequest.getMobileToiletBookingDetail().getTenantId();
	        requestInfo = toiletRequest.getRequestInfo();
	        localizationMessages = util.getLocalizationMessages(tenantId, requestInfo);
	        messageMap = util.getCustomizedMsg(requestInfo, toiletRequest.getMobileToiletBookingDetail(), localizationMessages);
	    } else {
	        throw new IllegalArgumentException("Unsupported request type: " + request.getClass().getSimpleName());
	    }

	    EventRequest eventRequest = getEventsForRS(request, messageMap.get(NotificationUtil.ACTION_LINK), messageMap);
	    log.info("Event Request in RequestService process method: " + eventRequest);

	    Set<String> mobileNumbers = new HashSet<>(util.fetchUserUUIDs(new HashSet<>(), requestInfo, tenantId).keySet());
	    List<String> configuredChannelNames = util.fetchChannelList(new RequestInfo(), tenantId.split("\\.")[0], config.getModuleName(), status);

	    // Send app notification
	    if (eventRequest != null) {
	        util.sendEventNotification(eventRequest);
	    }

	    // Send SMS notification
	    if (isNotificationEnabled(config.getIsSMSNotificationEnabled(), configuredChannelNames, RequestServiceConstants.CHANNEL_NAME_SMS)) {
	        List<SMSRequest> smsRequests = new LinkedList<>();
	        util.enrichSMSRequest(request, smsRequests);
	        if (!CollectionUtils.isEmpty(smsRequests)) {
	            util.sendSMS(smsRequests);
	        }
	    }

	    // Send Email notification
	    if (isNotificationEnabled(config.getIsEmailNotificationEnabled(), configuredChannelNames, RequestServiceConstants.CHANNEL_NAME_EMAIL)) {
	        Map<String, String> mapOfPhnoAndEmail = util.fetchUserEmailIds(mobileNumbers, requestInfo, tenantId);
	        List<EmailRequest> emailRequests = util.createEmailRequest(requestInfo, messageMap.get(RequestServiceConstants.MESSAGE_TEXT), mapOfPhnoAndEmail);
	        util.sendEmail(emailRequests);
	    }
	}

	

	/**
	 * Generates an event request for notification purposes.
	 * 
	 * @param request    The booking request object (either
	 *                   {@link MobileToiletBookingRequest} or
	 *                   {@link WaterTankerBookingRequest}).
	 * @param actionLink The action link for the notification.
	 * @param messageMap The message content map for the notification.
	 * @return An {@link EventRequest} object containing event details, or null if
	 *         an error occurs.
	 */

	private EventRequest getEventsForRS(Object request, String actionLink, Map<String, String> messageMap) {
		List<Event> events = new ArrayList<>();
		String tenantId;
		String mobileNumber;
		RequestInfo requestInfo;

		if (request instanceof MobileToiletBookingRequest) {
			MobileToiletBookingRequest toiletRequest = (MobileToiletBookingRequest) request;
			tenantId = toiletRequest.getMobileToiletBookingDetail().getTenantId();
			mobileNumber = toiletRequest.getMobileToiletBookingDetail().getApplicantDetail().getMobileNumber();
			requestInfo = toiletRequest.getRequestInfo();
		} else if (request instanceof WaterTankerBookingRequest) {
			WaterTankerBookingRequest tankerRequest = (WaterTankerBookingRequest) request;
			tenantId = tankerRequest.getWaterTankerBookingDetail().getTenantId();
			mobileNumber = tankerRequest.getWaterTankerBookingDetail().getApplicantDetail().getMobileNumber();
			requestInfo = tankerRequest.getRequestInfo();
		} else {
			log.error("Unsupported request type: " + request.getClass().getName());
			return null;
		}

		String localizationMessages = util.getLocalizationMessages(tenantId, requestInfo);
		List<String> toUsers = new ArrayList<>();
		Map<String, String> mapOfPhoneNoAndUUIDs = util.fetchUserUUIDs(mobileNumber, requestInfo, tenantId);

		if (CollectionUtils.isEmpty(mapOfPhoneNoAndUUIDs.keySet())) {
			log.info("UUID search failed!");
		}

		toUsers.add(mapOfPhoneNoAndUUIDs.get(mobileNumber));
		String message = messageMap.get(NotificationUtil.MESSAGE_TEXT);
		log.info("Message for event in RequestService: " + message);

		Recepient recepient = Recepient.builder().toUsers(toUsers).toRoles(null).build();
		log.info("Recipient object in RequestService: " + recepient);

		ActionItem actionItem = ActionItem.builder().actionUrl(actionLink).code("LINK").build();
		List<ActionItem> actionItems = new ArrayList<>();
		actionItems.add(actionItem);

		Action action = Action.builder().tenantId(tenantId).id(mobileNumber).actionUrls(actionItems)
				.eventId(RequestServiceConstants.CHANNEL_NAME_EVENT).build();

		events.add(Event.builder().tenantId(tenantId).description(message)
				.eventType(RequestServiceConstants.USREVENTS_EVENT_TYPE)
				.name(RequestServiceConstants.USREVENTS_EVENT_NAME)
				.postedBy(RequestServiceConstants.USREVENTS_EVENT_POSTEDBY).source(Source.WEBAPP).recepient(recepient)
				.actions(null).eventDetails(null).build());

		return CollectionUtils.isEmpty(events) ? null
				: EventRequest.builder().requestInfo(requestInfo).events(events).build();
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

}
