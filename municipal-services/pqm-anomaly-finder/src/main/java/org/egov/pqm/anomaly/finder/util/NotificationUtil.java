package org.egov.pqm.anomaly.finder.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pqm.anomaly.finder.config.PqmAnomalyConfiguration;
import org.egov.pqm.anomaly.finder.producer.PqmAnomalyFinderProducer;
import org.egov.pqm.anomaly.finder.repository.ServiceRequestRepository;
import org.egov.pqm.anomaly.finder.web.model.Test;
import org.egov.pqm.anomaly.finder.web.model.notification.EventRequest;
import org.egov.pqm.anomaly.finder.web.model.notification.SMSRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NotificationUtil {

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private PqmAnomalyFinderProducer pqmAnomalyFinderProducer;

	@Autowired
	private PqmAnomalyConfiguration pqmAnomalyConfiguration;

	static final String RECEIPT_NUMBER_KEY = "receiptNumber";

	/**
	 * Creates customized message based on fsm
	 * 
	 * @param test                 The test for which message is to be sent
	 * @param localizationMessage The messages from localization
	 * @return customized message based on fsm
	 */
	@SuppressWarnings("unchecked")
	public String getCustomizedMsg(Test test, String localizationMessage, String messageCode, RequestInfo requestInfo) {
		String message = null;

//		Test test = testRequest.getTests().get(0);

		String messageTemplate = getMessageTemplate(messageCode, localizationMessage);

		if (null != messageTemplate && !StringUtils.isEmpty(messageTemplate)) {

			message = callBenchmarkDetails(messageTemplate, test,localizationMessage);
		}
		return message;
	}

	private String callBenchmarkDetails(String message, Test test,String localizationMessage) {
			
		if (message.contains("{Plant Name}")) {
			String messageCode = "PQM_PLANT_" + test.getPlantCode();
			String plantNmae = getLocalizationMessage(messageCode, localizationMessage);
			message = message.replace("{Plant Name}", plantNmae);
		}
		if (message.contains("{Output}")) {
			String messageCode = "PQM_MATERIAL_" + test.getMaterialCode();
			String materialName = getLocalizationMessage(messageCode, localizationMessage);
			message = message.replace("{Output}", materialName);
		}

		if (message.contains("{Stage}")) {
			String messageCode = "PQM_STAGE_" + test.getStageCode();
			String stageNmae = getLocalizationMessage(messageCode, localizationMessage);
			message = message.replace("{Stage}", stageNmae);
		}

		if (message.contains("{Test Submitted Date}")) {
			Calendar possibleSrvdt = Calendar.getInstance();
			possibleSrvdt.setTimeInMillis(test.getAuditDetails().getLastModifiedTime());
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			dateFormat.setTimeZone(possibleSrvdt.getTimeZone());
			message = message.replace("{Test Submitted Date}", dateFormat.format(possibleSrvdt.getTime()));
		}

		if (message.contains("{Test Scheduled Date}")) {
			Calendar possibleSrvdt = Calendar.getInstance();
			possibleSrvdt.setTimeInMillis(test.getScheduledDate());
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			dateFormat.setTimeZone(possibleSrvdt.getTimeZone());
			message = message.replace("{Test Scheduled Date}", dateFormat.format(possibleSrvdt.getTime()));
		}

		return message;
	}

	/**
	 * Extracts message for the specific code
	 * 
	 * @param notificationCode    The code for which message is required
	 * @param localizationMessage The localization messages
	 * @return message for the specific code
	 */
	@SuppressWarnings("rawtypes")
	public String getMessageTemplate(String notificationCode, String localizationMessage) {
		String path = "$..messages[?(@.code==\"{}\")].message";
		String message = null;
		log.info("notificationCode :::  {} " + notificationCode);
		if (null != notificationCode) {
			try {
				path = path.replace("{}", notificationCode.trim());
				List data = JsonPath.parse(localizationMessage).read(path);
				if (!CollectionUtils.isEmpty(data))
					message = data.get(0).toString();
				else
					log.error("Fetching from localization failed with code " + notificationCode);
			} catch (Exception e) {
				log.warn("Fetching from localization failed", e);
			}
		}
		return message;
	}
	
	@SuppressWarnings("rawtypes")
	public String getLocalizationMessage(String notificationCode, String localizationMessage) {
		String path = "$..messages[?(@.code==\"{}\")].message";
		String message = null;
		log.info("notificationCode :::  {} " + notificationCode);
		if (null != notificationCode) {
			try {
				path = path.replace("{}", notificationCode.trim());
				List data = JsonPath.parse(localizationMessage).read(path);
				if (!CollectionUtils.isEmpty(data))
					message = data.get(0).toString();
				else
					message = notificationCode;
//					log.error("Fetching from localization failed with code " + notificationCode);
			} catch (Exception e) {
				log.warn("Fetching from localization failed", e);
			}
		}
		return message;
	}

	/**
	 * Returns the uri for the localization call
	 * 
	 * @param tenantId TenantId of the propertyRequest
	 * @return The uri for localization search call
	 */
	public StringBuilder getUri(String tenantId, RequestInfo requestInfo) {

		if (pqmAnomalyConfiguration.getIsLocalizationStateLevel())
			tenantId = tenantId.split("\\.")[0];

		String locale = "en_IN";
		if (!StringUtils.isEmpty(requestInfo.getMsgId()) && requestInfo.getMsgId().split("\\|").length >= 2)
			locale = requestInfo.getMsgId().split("\\|")[1];

		StringBuilder uri = new StringBuilder();
		uri.append(pqmAnomalyConfiguration.getLocalizationHost())
				.append(pqmAnomalyConfiguration.getLocalizationContextPath())
				.append(pqmAnomalyConfiguration.getLocalizationSearchEndpoint()).append("?").append("locale=")
				.append(locale).append("&tenantId=").append(tenantId).append("&module=")
				.append(AnomalyFinderConstants.SEARCH_MODULE_MDMS).append(",")
				.append(AnomalyFinderConstants.TQM_LOC_SEARCH_MODULE);
		return uri;
	}

	/**
	 * Fetches messages from localization service
	 * 
	 * @param tenantId    tenantId of the fsm
	 * @param requestInfo The requestInfo of the request
	 * @return Localization messages for the module
	 */
	@SuppressWarnings("rawtypes")
	public String getLocalizationMessages(String tenantId, RequestInfo requestInfo) {

		LinkedHashMap responseMap = (LinkedHashMap) serviceRequestRepository.fetchResult(getUri(tenantId, requestInfo),
				requestInfo);
		return new JSONObject(responseMap).toString();
	}

	/**
	 * Creates sms request for the each owners
	 * 
	 * @param message                 The message for the specific fsm
	 * @param mobileNumberToOwner Map of mobileNumber to OwnerName
	 * @return List of SMSRequest
	 */
	public List<SMSRequest> createSMSRequest(String message, Map<String, String> mobileNumberToOwner) {
		List<SMSRequest> smsRequest = new LinkedList<>();

		for (Map.Entry<String, String> entryset : mobileNumberToOwner.entrySet()) {
			String customizedMsg = message.replace("{1}", entryset.getValue());
			smsRequest.add(new SMSRequest(entryset.getKey(), customizedMsg));
		}
		return smsRequest;
	}

	/**
	 * Pushes the event request to Kafka Queue.
	 * 
	 * @param request
	 */
	public void sendEventNotification(EventRequest request) {
		pqmAnomalyFinderProducer.push(pqmAnomalyConfiguration.getSaveUserEventsTopic(), request);

		log.debug("STAKEHOLDER:: " + request.getEvents().get(0).getDescription());
	}

}