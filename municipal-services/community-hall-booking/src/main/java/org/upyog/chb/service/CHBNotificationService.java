package org.upyog.chb.service;

import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.repository.ServiceRequestRepository;
import org.upyog.chb.util.NotificationUtil;
import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.CommunityHallBookingRequest;
import org.upyog.chb.web.models.events.Action;
import org.upyog.chb.web.models.events.Event;
import org.upyog.chb.web.models.events.EventRequest;
import org.upyog.chb.web.models.events.Recepient;
import org.upyog.chb.web.models.events.Source;
import org.upyog.chb.web.models.notification.EmailRequest;
import org.upyog.chb.web.models.notification.SMSRequest;

import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

/**
 * This service class handles notification-related operations for the Community Hall Booking module.
 * 
 * Purpose:
 * - To send notifications to users based on booking events and status updates.
 * - To ensure that users are informed about the status of their bookings through various channels.
 * 
 * Dependencies:
 * - CommunityHallBookingConfiguration: Provides configuration properties for notifications.
 * - NotificationUtil: Utility class for sending notifications via different channels.
 * - ServiceRequestRepository: Handles communication with external services for notification delivery.
 * - CHBEncryptionService: Decrypts sensitive booking details for use in notifications.
 * - UserService: Fetches user details required for sending notifications.
 * 
 * Features:
 * - Processes booking requests and sends notifications based on the booking status.
 * - Decrypts sensitive applicant details before including them in notifications.
 * - Fetches configured notification channels (e.g., SMS, email) for the tenant.
 * - Logs notification processing details for debugging and monitoring purposes.
 * 
 * Methods:
 * 1. process:
 *    - Processes a booking request and sends notifications based on the booking status.
 *    - Decrypts booking details and determines the appropriate notification channels.
 * 
 * Usage:
 * - This class is automatically managed by Spring and injected wherever notification
 *   operations are required.
 * - It ensures consistent and reusable logic for sending notifications across the module.
 */
@Service
@Slf4j
public class CHBNotificationService {
	@Autowired
	private CommunityHallBookingConfiguration config;
	@Autowired
	private NotificationUtil util;
	@Autowired
	private ServiceRequestRepository serviceRequestRepository;
	@Autowired
	private CHBEncryptionService chbEncryptionService;

	@Autowired
	private UserService userService;
	
	public void process(CommunityHallBookingRequest bookingRequest, String status) {
		CommunityHallBookingDetail bookingDetail = bookingRequest.getHallsBookingApplication();
		// Decrypt applicant detail it will be used in notification
		bookingDetail = chbEncryptionService.decryptObject(bookingDetail, bookingRequest.getRequestInfo());
		RequestInfo requestInfo = bookingRequest.getRequestInfo();

		log.info("Processing notification for booking no : " + bookingDetail.getBookingNo() + " with status : "
				+ status);
		String tenantId = bookingRequest.getHallsBookingApplication().getTenantId();
		String action = status;
		Set<String> mobileNumbers = new HashSet<>(util.fetchUserUUIDs(new HashSet<>(), requestInfo, tenantId).keySet());
		List<String> configuredChannelNames = fetchChannelList(new RequestInfo(), tenantId.split("\\.")[0],
				config.getModuleName(), action);

		log.info("Fetching localization message for notification");
		// All notification messages are part of this messages object
		String localizationMessages = util.getLocalizationMessages(tenantId, bookingRequest.getRequestInfo());
		Map<String, String> messageMap = util.getCustomizedMsg(bookingRequest.getHallsBookingApplication(), localizationMessages, status,
				CommunityHallBookingConstants.CHANNEL_NAME_EVENT);


		if (configuredChannelNames.contains(CommunityHallBookingConstants.CHANNEL_NAME_EVENT)) {
			sendEventNotification(localizationMessages, bookingRequest, status);
		}

		if (configuredChannelNames.contains(CommunityHallBookingConstants.CHANNEL_NAME_SMS)) {
			sendMessageNotification(localizationMessages, bookingRequest, status);
		}
		
		// Send Email notification
        if (configuredChannelNames.contains(CommunityHallBookingConstants.CHANNEL_NAME_EMAIL)) {
            Map<String, String> mapOfPhnoAndEmail = util.fetchUserEmailIds(mobileNumbers, requestInfo, tenantId);
            List<EmailRequest> emailRequests = util.createEmailRequest(requestInfo, messageMap.get(CommunityHallBookingConstants.MESSAGE_TEXT), mapOfPhnoAndEmail);
            util.sendEmail(emailRequests);
        }
	}
	
	/**
	 * 
	 * @param localizationMessages
	 * @param bookingRequest
	 * @param status
	 */
	private void  sendMessageNotification(String localizationMessages, CommunityHallBookingRequest bookingRequest, String status) {
		CommunityHallBookingDetail bookingDetail = bookingRequest.getHallsBookingApplication();
		Map<String, String> messageMap = new HashMap<String, String>();
    	String message = null;
		try {
			messageMap = util.getCustomizedMsg(bookingRequest.getHallsBookingApplication(), localizationMessages, status
					 , CommunityHallBookingConstants.CHANNEL_NAME_SMS);
			
			message = messageMap.get(NotificationUtil.MESSAGE_TEXT);
			 /**
			  * Dynamic values Place holders
			  * {APPLICANT_NAME} 
			  * {BOOKING_NO}
			  * {COMMUNITY_HALL_NAME}
			  * {LINK} - Optional			 
			  */
			 message = String.format(message, bookingDetail.getApplicantDetail().getApplicantName(), 
					 bookingDetail.getBookingNo(), bookingDetail.getCommunityHallName(), messageMap.get(NotificationUtil.ACTION_LINK));
		}catch (Exception e) {
			log.error("Exception occcured while fetching message", e);
			e.printStackTrace();
		}
		log.info("Message for sending sms notification : " + message);
		if (message != null) {
			List<SMSRequest> smsRequests = new LinkedList<>();
			if (config.getIsSMSNotificationEnabled()) {
				Map<String, String> mobileNumberToOwner = new HashMap<String, String>();
				mobileNumberToOwner.put(bookingDetail.getApplicantDetail().getApplicantMobileNo(),
						bookingDetail.getApplicantDetail().getApplicantName());
				enrichSMSRequest(bookingRequest, smsRequests, mobileNumberToOwner, message);
				if (!CollectionUtils.isEmpty(smsRequests))
					util.sendSMS(smsRequests);
			}
		}
		
	}
	
    private void sendEventNotification(String localizationMessages, CommunityHallBookingRequest bookingRequest, String status) {
    	CommunityHallBookingDetail bookingDetail = bookingRequest.getHallsBookingApplication();
    	Map<String, String> messageMap = new HashMap<String, String>();
    	String message = null;
		try {
			messageMap = util.getCustomizedMsg(bookingRequest.getHallsBookingApplication(), localizationMessages, status
					 , CommunityHallBookingConstants.CHANNEL_NAME_EVENT);
			
			message = messageMap.get(NotificationUtil.MESSAGE_TEXT);
			 /**
			  * Dynamic values Place holders
			  * {APPLICANT_NAME} 
			  * {BOOKING_NO}
			  * {COMMUNITY_HALL_NAME}
			  * {LINK} - Optional			 
			  */
			 message = String.format(message, bookingDetail.getApplicantDetail().getApplicantName(), 
					 bookingDetail.getBookingNo(), bookingDetail.getCommunityHallName());
			 
		}catch (Exception e) {
			log.error("Exception occcured while fetching message", e);
			e.printStackTrace();
		}
		log.info("Message for sending event notification : " + message);
		if (message != null) {
			if (null != config.getIsUserEventsNotificationEnabled()) {
				if (config.getIsUserEventsNotificationEnabled()) {
					EventRequest eventRequest = getEventsForCommunityHallBooking(bookingRequest, message, messageMap.get( NotificationUtil.ACTION_LINK));
					if (null != eventRequest)
						util.sendEventNotification(eventRequest);
				}
			}
		}
	}

	/**
	 * Enriches the smsRequest with the customized messages
	 * 
	 * @param bpaRequest  The bpaRequest from kafka topic
	 * @param smsRequests List of SMSRequets
	 */
	private void enrichSMSRequest(CommunityHallBookingRequest bookingRequest, List<SMSRequest> smsRequests,
			Map<String, String> mobileNumberToOwner, String message) {
		smsRequests.addAll(util.createSMSRequest(bookingRequest, message, mobileNumberToOwner));
	}

	private EventRequest getEventsForCommunityHallBooking(CommunityHallBookingRequest request, String message, String actionLink) {

		List<Event> events = new ArrayList<>();
		String tenantId = request.getHallsBookingApplication().getTenantId();
		List<String> toUsers = new ArrayList<>();

		// Mobile no will be used to filter out user to send notification
		String mobileNumber = request.getRequestInfo().getUserInfo().getMobileNumber();

		Map<String, String> mapOfPhoneNoAndUUIDs = userService.fetchUserUUIDs(mobileNumber, request.getRequestInfo(), tenantId);

		if (CollectionUtils.isEmpty(mapOfPhoneNoAndUUIDs.keySet())) {
			log.error("UUID search failed in event  processing for CHB!");
		}

		toUsers.add(mapOfPhoneNoAndUUIDs.get(mobileNumber));
		
		log.info("Message for user event : " + message);
		Recepient recepient = Recepient.builder().toUsers(toUsers).toRoles(null).build();
		log.info("Recipient object in CHB event :" + recepient.toString());
		
		Action action = null;

		if (message.contains(CommunityHallBookingConstants.NOTIFICATION_ACTION)) {
			
			action = util.getActionLinkAndCode(message, actionLink, tenantId);
			String code = StringUtils.substringBetween(message, CommunityHallBookingConstants.NOTIFICATION_ACTION, CommunityHallBookingConstants.NOTIFICATION_ACTION_BUTTON);
			message = message.replace(CommunityHallBookingConstants.NOTIFICATION_ACTION, "").replace(CommunityHallBookingConstants.NOTIFICATION_ACTION_BUTTON, "").replace(code, "");

		}
	
		
		events.add(Event.builder().tenantId(tenantId).description(message)
				.eventType(CommunityHallBookingConstants.USREVENTS_EVENT_TYPE)
				.name(CommunityHallBookingConstants.USREVENTS_EVENT_NAME)
				.postedBy(CommunityHallBookingConstants.USREVENTS_EVENT_POSTEDBY).source(Source.WEBAPP)
				.actions(action)
				.recepient(recepient).eventDetails(null).build());
		
		log.info("EVENT in CHB : " + events.toString());

		if (!CollectionUtils.isEmpty(events)) {
			return EventRequest.builder().requestInfo(request.getRequestInfo()).events(events).build();
		} else {
			return null;
		}

	}
	
	/**
	 * Method to fetch the list of channels for a particular action from mdms config
	 * from mdms configs returns the message minus some lines to match In App
	 * Templates
	 * 
	 * @param requestInfo
	 * @param tenantId
	 * @param moduleName
	 * @param action
	 */
	private List<String> fetchChannelList(RequestInfo requestInfo, String tenantId, String moduleName, String action) {
		List<String> masterData = new ArrayList<>();
		StringBuilder uri = new StringBuilder();
		uri.append(config.getMdmsHost()).append(config.getMdmsPath());
		if (StringUtils.isEmpty(tenantId))
			return masterData;
		MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestForChannelList(requestInfo, tenantId, moduleName, action);
		//Can create filter as string using this
		Filter masterDataFilter = filter(where(CommunityHallBookingConstants.MODULE).is(moduleName)
				.and(CommunityHallBookingConstants.ACTION).is(action));

		try {
			Object response = serviceRequestRepository.fetchResult(uri, mdmsCriteriaReq);
			masterData = JsonPath.parse(response).read("$.MdmsRes.Channel.channelList[?].channelNames[*]",
					masterDataFilter);
		} catch (Exception e) {
			log.error("Exception while fetching workflow states to ignore: ", e);
		}

		return masterData;
	}

	private MdmsCriteriaReq getMdmsRequestForChannelList(RequestInfo requestInfo, String tenantId, String moduleName, String action) {

		MasterDetail masterDetail = new MasterDetail();
		masterDetail.setName(CommunityHallBookingConstants.CHANNEL_LIST);
		masterDetail.setFilter("[?(@['module'] == 'CHB' && @['action'] == '"+ action +"')]");
		List<MasterDetail> masterDetailList = new ArrayList<>();
		masterDetailList.add(masterDetail);

		ModuleDetail moduleDetail = new ModuleDetail();
		moduleDetail.setMasterDetails(masterDetailList);
		moduleDetail.setModuleName(CommunityHallBookingConstants.CHANNEL);
		List<ModuleDetail> moduleDetailList = new ArrayList<>();
		moduleDetailList.add(moduleDetail);

		MdmsCriteria mdmsCriteria = new MdmsCriteria();
		mdmsCriteria.setTenantId(tenantId);
		mdmsCriteria.setModuleDetails(moduleDetailList);

		MdmsCriteriaReq mdmsCriteriaReq = new MdmsCriteriaReq();
		mdmsCriteriaReq.setMdmsCriteria(mdmsCriteria);
		mdmsCriteriaReq.setRequestInfo(requestInfo);

		return mdmsCriteriaReq;
	}
	
	

}
