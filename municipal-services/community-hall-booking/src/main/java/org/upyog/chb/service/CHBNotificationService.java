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
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.repository.ServiceRequestRepository;
import org.upyog.chb.util.NotificationUtil;
import org.upyog.chb.web.models.VenueBookingDetail;
import org.upyog.chb.web.models.VenueBookingRequest;
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

@Service
@Slf4j
public class CHBNotificationService {

	private final CommunityHallBookingConfiguration config;
	private final NotificationUtil util;
	private final ServiceRequestRepository serviceRequestRepository;
	private final CHBEncryptionService chbEncryptionService;
	private final UserService userService;

	public CHBNotificationService(CommunityHallBookingConfiguration config, NotificationUtil util,
			ServiceRequestRepository serviceRequestRepository, CHBEncryptionService chbEncryptionService,
			UserService userService) {
		this.config = config;
		this.util = util;
		this.serviceRequestRepository = serviceRequestRepository;
		this.chbEncryptionService = chbEncryptionService;
		this.userService = userService;
	}
	
	public void process(VenueBookingRequest bookingRequest, String status) {
		VenueBookingDetail bookingDetail = bookingRequest.getVenueBookingApplication();
		bookingDetail = chbEncryptionService.decryptObject(bookingDetail, bookingRequest.getRequestInfo());
		RequestInfo requestInfo = bookingRequest.getRequestInfo();

		log.info("Processing notification for booking no : " + bookingDetail.getBookingNo() + " with status : "
				+ status);
		String tenantId = bookingRequest.getVenueBookingApplication().getTenantId();
		String action = status;
		Set<String> mobileNumbers = new HashSet<>(util.fetchUserUUIDs(new HashSet<>(), requestInfo, tenantId).keySet());
		List<String> configuredChannelNames = fetchChannelList(new RequestInfo(), tenantId.split("\\.")[0],
				config.getModuleName(), action);

		log.info("Fetching localization message for notification");
		String localizationMessages = util.getLocalizationMessages(tenantId, bookingRequest.getRequestInfo());
		Map<String, String> messageMap = util.getCustomizedMsg(bookingRequest.getVenueBookingApplication(), localizationMessages, status,
				CommunityHallBookingConstants.CHANNEL_NAME_EVENT);


		if (configuredChannelNames.contains(CommunityHallBookingConstants.CHANNEL_NAME_EVENT)) {
			sendEventNotification(localizationMessages, bookingRequest, status);
		}

		if (configuredChannelNames.contains(CommunityHallBookingConstants.CHANNEL_NAME_SMS)) {
			sendMessageNotification(localizationMessages, bookingRequest, status);
		}
		
        if (configuredChannelNames.contains(CommunityHallBookingConstants.CHANNEL_NAME_EMAIL)) {
            Map<String, String> mapOfPhnoAndEmail = util.fetchUserEmailIds(mobileNumbers, requestInfo, tenantId);
            List<EmailRequest> emailRequests = util.createEmailRequest(requestInfo, messageMap.get(CommunityHallBookingConstants.MESSAGE_TEXT), mapOfPhnoAndEmail);
            util.sendEmail(emailRequests);
        }
	}
	
	private void sendMessageNotification(String localizationMessages, VenueBookingRequest bookingRequest, String status) {
		VenueBookingDetail bookingDetail = bookingRequest.getVenueBookingApplication();
		Map<String, String> messageMap = new HashMap<>();
    	String message = null;
		try {
			messageMap = util.getCustomizedMsg(bookingRequest.getVenueBookingApplication(), localizationMessages, status
					 , CommunityHallBookingConstants.CHANNEL_NAME_SMS);
			
			message = messageMap.get(NotificationUtil.MESSAGE_TEXT);
			 message = String.format(message, bookingDetail.getApplicantDetail().getApplicantName(), 
					 bookingDetail.getBookingNo(), bookingDetail.getVenueName(), messageMap.get(NotificationUtil.ACTION_LINK));
		}catch (Exception e) {
			log.error("Exception occcured while fetching message", e);
			e.printStackTrace();
		}
		log.info("Message for sending sms notification : " + message);
		if (message != null) {
			List<SMSRequest> smsRequests = new LinkedList<>();
			if (Boolean.TRUE.equals(config.getIsSMSNotificationEnabled())) {
				Map<String, String> mobileNumberToOwner = new HashMap<>();
				mobileNumberToOwner.put(bookingDetail.getApplicantDetail().getApplicantMobileNo(),
						bookingDetail.getApplicantDetail().getApplicantName());
				enrichSMSRequest(smsRequests, mobileNumberToOwner, message);
				if (!CollectionUtils.isEmpty(smsRequests))
					util.sendSMS(smsRequests);
			}
		}
		
	}
	
    private void sendEventNotification(String localizationMessages, VenueBookingRequest bookingRequest, String status) {
    	VenueBookingDetail bookingDetail = bookingRequest.getVenueBookingApplication();
    	Map<String, String> messageMap = new HashMap<>();
    	String message = null;
		try {
			messageMap = util.getCustomizedMsg(bookingRequest.getVenueBookingApplication(), localizationMessages, status
					 , CommunityHallBookingConstants.CHANNEL_NAME_EVENT);
			
			message = messageMap.get(NotificationUtil.MESSAGE_TEXT);
			 message = String.format(message, bookingDetail.getApplicantDetail().getApplicantName(), 
					 bookingDetail.getBookingNo(), bookingDetail.getVenueName());
			 
		}catch (Exception e) {
			log.error("Exception occcured while fetching message", e);
			e.printStackTrace();
		}
		log.info("Message for sending event notification : " + message);
		if (message != null && Boolean.TRUE.equals(config.getIsUserEventsNotificationEnabled())) {
			EventRequest eventRequest = getEventsForCommunityHallBooking(bookingRequest, message, messageMap.get( NotificationUtil.ACTION_LINK));
			if (null != eventRequest)
				util.sendEventNotification(eventRequest);
		}
	}

	private void enrichSMSRequest(List<SMSRequest> smsRequests,
			Map<String, String> mobileNumberToOwner, String message) {
		smsRequests.addAll(util.createSMSRequest(message, mobileNumberToOwner));
	}

	private EventRequest getEventsForCommunityHallBooking(VenueBookingRequest request, String message, String actionLink) {

		List<Event> events = new ArrayList<>();
		String tenantId = request.getVenueBookingApplication().getTenantId();
		List<String> toUsers = new ArrayList<>();

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
		}
		return null;

	}
	
	private List<String> fetchChannelList(RequestInfo requestInfo, String tenantId, String moduleName, String action) {
		List<String> masterData = new ArrayList<>();
		StringBuilder uri = new StringBuilder();
		uri.append(config.getMdmsHost()).append(config.getMdmsPath());
		if (StringUtils.isEmpty(tenantId))
			return masterData;
		MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestForChannelList(requestInfo, tenantId, moduleName, action);
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
		masterDetail.setFilter("[?(@['module'] == '" + moduleName + "' && @['action'] == '"+ action +"')]");
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
