package org.egov.ptr.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.egov.common.contract.request.RequestInfo;
import org.egov.ptr.config.PetConfiguration;
import org.egov.ptr.util.NotificationUtil;
import org.egov.ptr.util.PTRConstants;
import org.egov.ptr.models.PetRegistrationApplication;
import org.egov.ptr.models.PetRegistrationRequest;
import org.egov.ptr.models.event.*;
import org.egov.ptr.models.event.EventRequest;
import org.egov.ptr.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;



/**
 * Service responsible for handling notifications related to Pet Registration applications.
 */
@Service
@Slf4j
public class PTRNotificationService {
	@Autowired
	private PetConfiguration config;
	@Autowired
	private NotificationUtil util;
	@Autowired
	private ServiceRequestRepository serviceRequestRepository;


	/**
	 * Processes the notification for pet registration applications.
	 * Constructs event requests and sends notifications to users.
	 *
	 * @param request The pet registration request containing application details.
	 */
	public void process(PetRegistrationRequest request) {
		EventRequest eventRequest = getEventsForPTR(request);
		log.info("Event Request in Pet process method" + eventRequest.toString());
		if (null != eventRequest)
			util.sendEventNotification(eventRequest);

	}

	/**
	 * Constructs an EventRequest object containing notifications for pet registration applications.
	 *
	 * @param request The pet registration request containing application details.
	 * @return An EventRequest containing the notification details, or null if no events are created.
	 */
	private EventRequest getEventsForPTR(PetRegistrationRequest request) {
		List<Event> events = new ArrayList<>();
		String localizationMessages = util.getLocalizationMessages(
				request.getPetRegistrationApplications().get(0).getTenantId(), request.getRequestInfo());

		// Iterate over all PetRegistrationApplications
		for (PetRegistrationApplication application : request.getPetRegistrationApplications()) {
			String tenantId = application.getTenantId();
			String mobileNumber = application.getMobileNumber();

			// Fetch user UUIDs based on mobile number
			Map<String, String> mapOfPhoneNoAndUUIDs = fetchUserUUIDs(mobileNumber, request.getRequestInfo(), tenantId);
			if (CollectionUtils.isEmpty(mapOfPhoneNoAndUUIDs.keySet())) {
				log.info("UUID search failed for mobile number: {}", mobileNumber);
				continue; // Skip this application if UUID lookup fails
			}

			// Prepare recipient and message
			List<String> toUsers = new ArrayList<>();
			toUsers.add(mapOfPhoneNoAndUUIDs.get(mobileNumber));
			String message = util.getCustomizedMsg(request.getRequestInfo(), application, localizationMessages);

			log.info("Message for event in Pet: {}", message);
			Recepient recepient = Recepient.builder().toUsers(toUsers).toRoles(null).build();
			log.info("Recipient object in pet: {}", recepient);

			// Add event to the list
			Event event = Event.builder().tenantId(tenantId).description(message)
					.eventType(PTRConstants.USREVENTS_EVENT_TYPE).name(PTRConstants.USREVENTS_EVENT_NAME)
					.postedBy(PTRConstants.USREVENTS_EVENT_POSTEDBY).source(Source.WEBAPP).recepient(recepient)
					.eventDetails(null).actions(null).build();
			events.add(event);
		}

		// Check if events are created and return EventRequest
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
			log.info("User fetched in fetUserUUID method of pet notfication consumer" + user.toString());
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
