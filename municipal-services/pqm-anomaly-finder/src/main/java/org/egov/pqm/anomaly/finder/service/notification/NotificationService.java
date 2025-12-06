package org.egov.pqm.anomaly.finder.service.notification;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pqm.anomaly.finder.config.PqmAnomalyConfiguration;
import org.egov.pqm.anomaly.finder.repository.ServiceRequestRepository;
import org.egov.pqm.anomaly.finder.util.AnomalyFinderConstants;
import org.egov.pqm.anomaly.finder.util.NotificationUtil;
import org.egov.pqm.anomaly.finder.web.model.Test;
import org.egov.pqm.anomaly.finder.web.model.TestRequest;
import org.egov.pqm.anomaly.finder.web.model.notification.Action;
import org.egov.pqm.anomaly.finder.web.model.notification.ActionItem;
import org.egov.pqm.anomaly.finder.web.model.notification.Event;
import org.egov.pqm.anomaly.finder.web.model.notification.EventRequest;
import org.egov.pqm.anomaly.finder.web.model.notification.Recepient;
import org.egov.pqm.anomaly.finder.web.model.notification.SMSRequest;
import org.egov.pqm.anomaly.finder.web.model.notification.Source;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NotificationService {

	@Autowired
	private PqmAnomalyConfiguration pqmAnomalyConfiguration;

	@Autowired
	private NotificationUtil notificationUtil;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	/**
	 * Creates and send the sms based on the fsmRequest
	 * 
	 * @param testRequest The testRequest listenend on the kafka topic
	 */
	public void process(TestRequest testRequest,String topic) {

		if (null != pqmAnomalyConfiguration.getIsEventsNotificationEnabled()
				&& pqmAnomalyConfiguration.getIsEventsNotificationEnabled()) {

			EventRequest request = getEvents(testRequest, topic);
			if (null != request)
				notificationUtil.sendEventNotification(request);
		}
	}

	/**
	 * Creates and registers an event at the egov-user-event service at defined
	 * trigger points as that of sms notifs.
	 * 
	 * Assumption - The fsmRequest received will always contain only one fsm.
	 * 
	 * @param testRequest
	 * @return
	 */
	
	public EventRequest getEvents(TestRequest testRequest,String topic) {

		List<Event> events = new ArrayList<>();
		RequestInfo requestInfo = testRequest.getRequestInfo();
		List<Test> tests = testRequest.getTests();
		for (Test test : tests) {

			List<SMSRequest> smsRequests = new LinkedList<>();

			enrichSMSRequest(test, smsRequests, requestInfo, topic);

			Set<String> mobileNumbers = smsRequests.stream().map(SMSRequest::getMobileNumber)
					.collect(Collectors.toSet());
			List<String> UUIDs = fetchUserUUIDs( requestInfo,
					test.getTenantId());

			Map<String, String> mobileNumberToMsg = smsRequests.stream()
					.collect(Collectors.toMap(SMSRequest::getMobileNumber, SMSRequest::getMessage));
			for (String mobile : mobileNumbers) {

				List<String> toUsers = new ArrayList<>();

				List<String> toRoles = new ArrayList<>();
				toUsers.addAll(UUIDs);
				Recepient recepient = Recepient.builder().toUsers(toUsers).toRoles(null).build();
				Action action = null;
				List<ActionItem> items = new ArrayList<>();

				String actionLink = pqmAnomalyConfiguration.getTestLink().replace("$testId", test.getTestId());
				actionLink = pqmAnomalyConfiguration.getUiAppHost() + actionLink;
				ActionItem item = ActionItem.builder().actionUrl(actionLink).code(pqmAnomalyConfiguration.getViewCode())
						.build();
				items.add(item);
				action = Action.builder().actionUrls(items).build();
				
				String eventCategory = null;
				
				if(topic.equalsIgnoreCase(pqmAnomalyConfiguration.getNotAsPerBenchMark())) {
					eventCategory = AnomalyFinderConstants.TEST_RESULT_NOT_AS_PER_BENCHMARKS_FOR_LAB;
				}
				if(topic.equalsIgnoreCase(pqmAnomalyConfiguration.getTestNotSubmitted())) {
					eventCategory = AnomalyFinderConstants.TEST_RESULT_NOT_SUBMITTED;
				}

				events.add(Event.builder().tenantId(test.getTenantId()).description(mobileNumberToMsg.get(mobile))
						.eventType(AnomalyFinderConstants.USREVENTS_EVENT_TYPE).eventCategory(eventCategory)
						.name(AnomalyFinderConstants.USREVENTS_EVENT_NAME)
						.postedBy(AnomalyFinderConstants.USREVENTS_EVENT_POSTEDBY).source(Source.WEBAPP)
						.recepient(recepient).eventDetails(null).actions(action).build());
			}
		}

		if (!CollectionUtils.isEmpty(events)) {
			return EventRequest.builder().requestInfo(requestInfo).events(events).build();
		} else {
			return null;
		}

	}


	/**
	 * Enriches the smsRequest with the customized messages
	 * 
	 * @param test     The test from kafka topic
	 * @param smsRequests List of SMSRequets
	 */
	private void enrichSMSRequest(Test test, List<SMSRequest> smsRequests, RequestInfo requestInfo, String topic) {
		String tenantId = test.getTenantId();
		String localizationMessages = notificationUtil.getLocalizationMessages(tenantId, requestInfo);
		String messageCode = null;

		String localizationCode = null;

		if(topic.equalsIgnoreCase(pqmAnomalyConfiguration.getNotAsPerBenchMark())) {
			localizationCode = AnomalyFinderConstants.NOTIF_TEST_RESULT_NOT_AS_PER_BENCHMARK;
		}
		if(topic.equalsIgnoreCase(pqmAnomalyConfiguration.getTestNotSubmitted())) {
			localizationCode = AnomalyFinderConstants.NOTIF_TEST_RESULT_NOT_SUBMITTED;
		}

		if (test != null) {

			String message = notificationUtil.getCustomizedMsg(test, localizationMessages, localizationCode,
					requestInfo);
			Map<String, String> mobileNumberToOwner = new HashMap<>();
			mobileNumberToOwner.put(requestInfo.getUserInfo().getMobileNumber(), requestInfo.getUserInfo().getName());
			smsRequests.addAll(notificationUtil.createSMSRequest(message, mobileNumberToOwner));
		}

	}

	private List<String> fetchUserUUIDs(RequestInfo requestInfo, String tenantId) {

		List<String> returnUuids = new ArrayList<>();
		StringBuilder uri = new StringBuilder();
		uri.append(pqmAnomalyConfiguration.getUserHost()).append(pqmAnomalyConfiguration.getUserSearchEndpoint());
		Map<String, Object> userSearchRequest = new HashMap<>();
		userSearchRequest.put("RequestInfo", requestInfo);
		userSearchRequest.put("tenantId", tenantId);
		List<String> roleCodesList = new ArrayList<>();
		roleCodesList.add("PQM_ADMIN");

		// Put the list into the map
		userSearchRequest.put("roleCodes", roleCodesList);

			try {
				Object user = serviceRequestRepository.fetchResult(uri, userSearchRequest);
				if (null != user) {
					 List<String> uuids = JsonPath.read(user, "$.user[*].uuid");
					 returnUuids.addAll(uuids);
				} else {
					log.error("Service returned null while fetching user for roleCodes - PQM_ADMIN");
				}
			} catch (Exception e) {
				log.error("Exception while fetching user with roleodes - PQM_ADMIN" );
				log.error("Exception trace: ", e);
			}
		
		return returnUuids;
	}

}
