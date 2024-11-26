package org.upyog.sv.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.sv.config.StreetVendingConfiguration;
import org.upyog.sv.constants.StreetVendingConstants;
import org.upyog.sv.repository.ServiceRequestRepository;
import org.upyog.sv.service.StreetyVendingNotificationService;
import org.upyog.sv.util.NotificationUtil;
import org.upyog.sv.web.models.StreetVendingRequest;
import org.upyog.sv.web.models.events.Event;
import org.upyog.sv.web.models.events.EventRequest;
import org.upyog.sv.web.models.events.Recepient;
import org.upyog.sv.web.models.events.Source;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StreetyVendingNotificationServiceImpl implements StreetyVendingNotificationService {

	@Autowired
	private StreetVendingConfiguration config;

	@Autowired
	private NotificationUtil util;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	public void process(StreetVendingRequest request) {
		EventRequest eventRequest = getEventsForSV(request);
		log.info("Event Request in StreetVending process method" + eventRequest.toString());
		if (null != eventRequest)
			util.sendEventNotification(eventRequest);

	}

	private EventRequest getEventsForSV(StreetVendingRequest request) {

		List<Event> events = new ArrayList<>();
		String tenantId = request.getStreetVendingDetail().getTenantId();
		String localizationMessages = util.getLocalizationMessages(tenantId, request.getRequestInfo());
		List<String> toUsers = new ArrayList<>();
		String mobileNumber = request.getStreetVendingDetail().getVendorDetail().get(0).getMobileNo();

		Map<String, String> mapOfPhoneNoAndUUIDs = fetchUserUUIDs(mobileNumber, request.getRequestInfo(), tenantId);

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
			log.info("User fetched in fetUserUUID method of StreetVending notfication consumer" + user.toString());
//			if (null != user) {
//				String uuid = JsonPath.read(user, "$.user[0].uuid");
			if (user instanceof Optional) {
				Optional<Object> optionalUser = (Optional<Object>) user;
				if (optionalUser.isPresent()) {
					String uuid = JsonPath.read(optionalUser.get(), "$.user[0].uuid");
					mapOfPhoneNoAndUUIDs.put(mobileNumber, uuid);
				}
			} else {
				log.error("Service returned null while fetching user for username - " + mobileNumber);
			}
		} catch (Exception e) {
			log.error("Exception while fetching user for username - " + mobileNumber);
			log.error("Exception trace: ", e);
		}

		return mapOfPhoneNoAndUUIDs;
	}

}
