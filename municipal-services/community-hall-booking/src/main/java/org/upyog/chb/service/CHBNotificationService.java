package org.upyog.chb.service;

import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import org.upyog.chb.web.models.events.Event;
import org.upyog.chb.web.models.events.EventRequest;
import org.upyog.chb.web.models.events.Recepient;
import org.upyog.chb.web.models.events.Source;
import org.upyog.chb.web.models.notification.SMSRequest;

import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CHBNotificationService {
	@Autowired
	private CommunityHallBookingConfiguration config;
	@Autowired
	private NotificationUtil util;
	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	public void process(CommunityHallBookingRequest bookingRequest) {
		CommunityHallBookingDetail bookingDetail = bookingRequest.getHallsBookingApplication();
		log.info("Processing notification for booking no : " + bookingDetail.getBookingNo() + " with status : " + bookingDetail.getBookingStatus());
		String tenantId = bookingRequest.getHallsBookingApplication().getTenantId();
		String action = bookingDetail.getBookingStatus();

		List<String> configuredChannelNames = fetchChannelList(new RequestInfo(), tenantId.split("\\.")[0], config.getModuleName(),
				action);

		Map<String, String> mobileNumberToOwner = new HashMap<String, String>();
		mobileNumberToOwner.put(bookingDetail.getApplicantDetail().getApplicantMobileNo(),
				bookingDetail.getApplicantDetail().getApplicantName());
		log.info("Fetching localization message for notification");
		String localizationMessages = util.getLocalizationMessages(tenantId, bookingRequest.getRequestInfo());
		String message = null;
		try {
			 message = util.getCustomizedMsg(bookingRequest.getHallsBookingApplication(), localizationMessages);
		}catch (Exception e) {
			log.error("Exception occcured while fetching message", e);
			e.printStackTrace();
		}
		
		log.info("Message for sending sms and event : " + message);
        
		if (configuredChannelNames.contains(CommunityHallBookingConstants.CHANNEL_NAME_SMS)) {
			List<SMSRequest> smsRequests = new LinkedList<>();
			if (config.getIsSMSNotificationEnabled()) {
				enrichSMSRequest(bookingRequest, smsRequests, mobileNumberToOwner, message);
				if (!CollectionUtils.isEmpty(smsRequests))
					util.sendSMS(smsRequests);
			}
		}

		if (configuredChannelNames.contains(CommunityHallBookingConstants.CHANNEL_NAME_EVENT)) {
			if (null != config.getIsUserEventsNotificationEnabled()) {
				if (config.getIsUserEventsNotificationEnabled()) {
					EventRequest eventRequest = getEventsForCommunityHallBooking(bookingRequest, message);
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

	private EventRequest getEventsForCommunityHallBooking(CommunityHallBookingRequest request, String message) {

		List<Event> events = new ArrayList<>();
		String tenantId = request.getHallsBookingApplication().getTenantId();
		List<String> toUsers = new ArrayList<>();

		// Mobile no will be used to filter out user to send notification
		String mobileNumber = request.getRequestInfo().getUserInfo().getMobileNumber();

		Map<String, String> mapOfPhoneNoAndUUIDs = fetchUserUUIDs(mobileNumber, request.getRequestInfo(), tenantId);

		if (CollectionUtils.isEmpty(mapOfPhoneNoAndUUIDs.keySet())) {
			log.error("UUID search failed in event  processing for CHB!");
		}

		toUsers.add(mapOfPhoneNoAndUUIDs.get(mobileNumber));
		
		log.info("Message for user event : " + message);
		Recepient recepient = Recepient.builder().toUsers(toUsers).toRoles(null).build();
		log.info("Recipient object in CHB event :" + recepient.toString());
		events.add(Event.builder().tenantId(tenantId).description(message)
				.eventType(CommunityHallBookingConstants.USREVENTS_EVENT_TYPE)
				.name(CommunityHallBookingConstants.USREVENTS_EVENT_NAME)
				.postedBy(CommunityHallBookingConstants.USREVENTS_EVENT_POSTEDBY).source(Source.WEBAPP)
				.recepient(recepient).eventDetails(null).actions(null).build());

		if (!CollectionUtils.isEmpty(events)) {
			return EventRequest.builder().requestInfo(request.getRequestInfo()).events(events).build();
		} else {
			return null;
		}

	}

	/**
	 * Fetches UUIDs of CITIZEN based on the phone number.
	 *
	 * @param mobileNumber - Mobile Numbers
	 * @param requestInfo  - Request Information
	 * @param tenantId     - Tenant Id
	 * @return Returns List of MobileNumbers and UUIDs
	 */
	private Map<String, String> fetchUserUUIDs(String mobileNumber, RequestInfo requestInfo, String tenantId) {
		Map<String, String> mapOfPhoneNoAndUUIDs = new HashMap<>();
		StringBuilder uri = new StringBuilder();
		uri.append(config.getUserHost()).append(config.getUserSearchEndpoint());
		Map<String, Object> userSearchRequest = new HashMap<>();
		userSearchRequest.put("RequestInfo", requestInfo);
		userSearchRequest.put("tenantId", tenantId);
		userSearchRequest.put("userType", CommunityHallBookingConstants.CITIZEN);
		userSearchRequest.put("userName", mobileNumber);
		try {

			Object user = serviceRequestRepository.fetchResult(uri, userSearchRequest);
			log.info("User fetched in fetUserUUID method of CHB notfication consumer" + user.toString());
//			if (null != user) {
//				String uuid = JsonPath.read(user, "$.user[0].uuid");
			if (user  != null) {
					String uuid = JsonPath.read(user, "$.user[0].uuid");
					mapOfPhoneNoAndUUIDs.put(mobileNumber, uuid);
					log.info("mapOfPhoneNoAndUUIDs : " + mapOfPhoneNoAndUUIDs);
			} 
		} catch (Exception e) {
			log.error("Exception while fetching user for username - " + mobileNumber);
			log.error("Exception trace: ", e);
		}

		return mapOfPhoneNoAndUUIDs;
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
