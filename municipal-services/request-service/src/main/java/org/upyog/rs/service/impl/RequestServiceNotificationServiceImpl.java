package org.upyog.rs.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.rs.config.RequestServiceConfiguration;
import org.upyog.rs.constant.RequestServiceConstants;
import org.upyog.rs.repository.ServiceRequestRepository;
import org.upyog.rs.service.RequestServiceNotificationService;
import org.upyog.rs.util.NotificationUtil;
import org.upyog.rs.web.models.WaterTankerBookingRequest;
import org.upyog.rs.web.models.events.Event;
import org.upyog.rs.web.models.events.EventRequest;
import org.upyog.rs.web.models.events.Recepient;
import org.upyog.rs.web.models.events.Source;

import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RequestServiceNotificationServiceImpl implements RequestServiceNotificationService {

	@Autowired
	private RequestServiceConfiguration config;

	@Autowired
	private NotificationUtil util;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	public void process(WaterTankerBookingRequest request) {
		EventRequest eventRequest = getEventsForRS(request);
		log.info("Event Request in RequestService process method" + eventRequest.toString());
		if (null != eventRequest)
			util.sendEventNotification(eventRequest);

	}

	private EventRequest getEventsForRS(WaterTankerBookingRequest request) {

		List<Event> events = new ArrayList<>();
		String tenantId = request.getWaterTankerBookingDetail().getTenantId();
		String localizationMessages = util.getLocalizationMessages(tenantId, request.getRequestInfo());
		List<String> toUsers = new ArrayList<>();
		String mobileNumber = request.getWaterTankerBookingDetail().getApplicantDetail().getMobileNumber();

		Map<String, String> mapOfPhoneNoAndUUIDs = fetchUserUUIDs(mobileNumber, request.getRequestInfo(), tenantId);

		if (CollectionUtils.isEmpty(mapOfPhoneNoAndUUIDs.keySet())) {
			log.info("UUID search failed!");
		}

		toUsers.add(mapOfPhoneNoAndUUIDs.get(mobileNumber));
		String message = null;
		message = util.getCustomizedMsg(request.getRequestInfo(), request.getWaterTankerBookingDetail(),
				localizationMessages);
		log.info("Message for event in RequestService:" + message);
		Recepient recepient = Recepient.builder().toUsers(toUsers).toRoles(null).build();
		log.info("Recipient object in RequestService:" + recepient.toString());
		events.add(Event.builder().tenantId(tenantId).description(message)
				.eventType(RequestServiceConstants.USREVENTS_EVENT_TYPE)
				.name(RequestServiceConstants.USREVENTS_EVENT_NAME)
				.postedBy(RequestServiceConstants.USREVENTS_EVENT_POSTEDBY).source(Source.WEBAPP).recepient(recepient)
				.eventDetails(null).actions(null).build());

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
	public Map<String, String> fetchUserUUIDs(String mobileNumber, RequestInfo requestInfo, String tenantId) {
		Map<String, String> mapOfPhoneNoAndUUIDs = new HashMap<>();
		StringBuilder uri = new StringBuilder();
		uri.append(config.getUserHost()).append(config.getUserSearchEndpoint());
		Map<String, Object> userSearchRequest = new HashMap<>();
		userSearchRequest.put("RequestInfo", requestInfo);
		userSearchRequest.put("tenantId", tenantId);
		userSearchRequest.put("userType", "CITIZEN");
		userSearchRequest.put("userName", mobileNumber);
		try {

			Object user = serviceRequestRepository.fetchResult(uri, userSearchRequest);
			log.info("User fetched in fetUserUUID method of RequestService notfication consumer" + user.toString());
			if (user != null) {
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

}
