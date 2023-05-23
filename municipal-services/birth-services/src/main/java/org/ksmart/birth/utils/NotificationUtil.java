package org.ksmart.birth.utils;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.json.JSONObject;
import org.ksmart.birth.common.producer.BndProducer;
import org.ksmart.birth.common.repository.ServiceRequestRepository;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.web.model.SMSRequest;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;


import static org.ksmart.birth.utils.BirthConstants.*;
@Component
@Slf4j
public class NotificationUtil {
	
	 @Autowired
	    BirthConfiguration config;
	    private BndProducer producer;
	    private RestTemplate restTemplate;
	    private ServiceRequestRepository serviceRequestRepository;
	    
	@Autowired
	public NotificationUtil(BirthConfiguration config, ServiceRequestRepository serviceRequestRepository,
			BndProducer producer, RestTemplate restTemplate ) {
		this.config = config;
		this.serviceRequestRepository = serviceRequestRepository;
		this.producer = producer;
		this.restTemplate = restTemplate;
		 
	}
	

	/**
	 * Creates customized message based on tradelicense
	 * 
	 * @param license
	 *                            The tradeLicense for which message is to be sent
	 * @param localizationMessage
	 *                            The messages from localization
	 * @return customized message based on tradelicense
	 */
	public String getCustomizedMsg(RequestInfo requestInfo, NewBirthApplication birth, String localizationMessage) {
		String message = null, messageTemplate;
		String ACTION_STATUS = birth.getAction() + "_" + birth.getApplicationStatus();
		switch (ACTION_STATUS) {

			case ACTION_STATUS_INITIATED:
				messageTemplate = getMessageTemplate(NOTIFICATION_INITIATED_TEST, localizationMessage);
				message = getInitiatedMsg(birth, messageTemplate);
				break;

			case ACTION_STATUS_PAYMENT:
				messageTemplate = getMessageTemplate(NOTIFICATION_INITIATED_TEST, localizationMessage);
				message = getInitiatedMsg(birth, messageTemplate);
				break;

			/*
			 * case ACTION_STATUS_PAID : messageTemplate =
			 * getMessageTemplate(TLConstants.NOTIFICATION_PAID,localizationMessage);
			 * message = getApprovalPendingMsg(license,messageTemplate); break;
			 */

			case ACTION_STATUS_APPROVED:
//				BigDecimal amountToBePaid = getAmountToBePaid(requestInfo, birth);
				messageTemplate = getMessageTemplate(NOTIFICATION_APPROVED, localizationMessage);
				message = getApprovedMsg(birth,  messageTemplate);
				break;

			case ACTION_STATUS_REJECTED:
				messageTemplate = getMessageTemplate(NOTIFICATION_REJECTED, localizationMessage);
				message = getRejectedMsg(birth, messageTemplate);
				break;
  

		}

		return message;
	}
	
	/**
	 * Extracts message for the specific code
	 * 
	 * @param notificationCode
	 *                            The code for which message is required
	 * @param localizationMessage
	 *                            The localization messages
	 * @return message for the specific code
	 */
	private String getMessageTemplate(String notificationCode, String localizationMessage) {
		String path = "$..messages[?(@.code==\"{}\")].message";
		path = path.replace("{}", notificationCode);
		String message = null;
		try {
			Object messageObj = JsonPath.parse(localizationMessage).read(path);
			message = ((ArrayList<String>) messageObj).get(0);
		} catch (Exception e) {
			log.warn("Fetching from localization failed", e);
		}
		return message;
	}
	
	/**
	 * Creates customized message for initiate
	 * 
	 * @param license
	 *                tenantId of the tradeLicense
	 * @param message
	 *                Message from localization for initiate
	 * @return customized message for initiate
	 */
	private String getInitiatedMsg(NewBirthApplication birth, String message) {
		message = message.replace("{1}",birth.getInitiatorDetails().getInitiatorEn());
		message = message.replace("{2}", "Birth Registration");
		message = message.replace("{3}", birth.getApplicationNo());

		return message;
	}
	

	/**
	 * Creates customized message for apply
	 * 
	 * @param license
	 *                tenantId of the tradeLicense
	 * @param message
	 *                Message from localization for apply
	 * @return customized message for apply
	 */
	private String getAppliedMsg(NewBirthApplication birth, String message) {
		// message = message.replace("{1}",);
		message = message.replace("{2}", birth.getFirstNameEn());
		message = message.replace("{3}", birth.getApplicationNo());

		return message;
	}
	
	/**
	 * Creates customized message for approved
	 * 
	 * @param license
	 *                tenantId of the tradeLicense
	 * @param message
	 *                Message from localization for approved
	 * @return customized message for approved
	 */
	private String getApprovedMsg(NewBirthApplication birth,  String message) {
		message = message.replace("{2}", birth.getFirstNameEn());
//		message = message.replace("{3}", amountToBePaid.toString());

//		String UIHost = config.getUiAppHost();
//
//		String paymentPath = config.getPayLinkSMS();
//		paymentPath = paymentPath.replace("$consumercode", birth.getApplicationNo());
//		paymentPath = paymentPath.replace("$tenantId", birth.getTenantId());
//		paymentPath = paymentPath.replace("$businessservice", BUSINESS_SERVICE_BND);
//
//		String finalPath = UIHost + paymentPath;
//
//		message = message.replace(PAYMENT_LINK_PLACEHOLDER, getShortenedUrl(finalPath));
		return message;
	}
	
	/**
	 * Creates customized message for rejected
	 * 
	 * @param license
	 *                tenantId of the tradeLicense
	 * @param message
	 *                Message from localization for rejected
	 * @return customized message for rejected
	 */
	private String getRejectedMsg(NewBirthApplication birth, String message) {
		// message = message.replace("{1}",);
		message = message.replace("{2}", birth.getFirstNameEn());

		return message;
	}
	
	/**
	 * Creates customized message for rejected
	 * 
	 * @param license
	 *                tenantId of the tradeLicense
	 * @param message
	 *                Message from localization for rejected
	 * @return customized message for rejected
	 */
	private String getFieldInspectionMsg(NewBirthApplication birth, String message) {
		message = message.replace("{2}", birth.getFirstNameEn());
		return message;
	}
	
	/**
	 * Creates customized message for rejected
	 *
	 * @param license
	 *                tenantId of the tradeLicense
	 * @param message
	 *                Message from localization for rejected
	 * @return customized message for rejected
	 */
	private String getPendingApprovalMsg(NewBirthApplication birth, String message) {
		message = message.replace("{2}", birth.getApplicationNo());
		message = message.replace("{3}", birth.getFirstNameEn());
		return message;
	}
	
	/**
	 * Creates customized message for citizen sendback
	 * 
	 * @param license
	 *                tenantId of the tradeLicense
	 * @param message
	 *                Message from localization for cancelled
	 * @return customized message for cancelled
	 */
	private String getCitizenSendBack(NewBirthApplication birth, String message) {
		message = message.replace("{2}", birth.getApplicationNo());
		message = message.replace("{3}", birth.getFirstNameEn());

		return message;
	}
	
	/**
	 * Creates customized message for citizen forward
	 *
	 * @param license
	 *                tenantId of the tradeLicense
	 * @param message
	 *                Message from localization for cancelled
	 * @return customized message for cancelled
	 */
	private String getCitizenForward(NewBirthApplication birth, String message) {
		message = message.replace("{2}", birth.getApplicationNo());
		message = message.replace("{3}", birth.getFirstNameEn());

		return message;
	}
	
	/**
	 * Creates customized message for cancelled
	 *
	 * @param license
	 *                tenantId of the tradeLicense
	 * @param message
	 *                Message from localization for cancelled
	 * @return customized message for cancelled
	 */
	private String getCancelledMsg(NewBirthApplication birth, String message) {
		message = message.replace("{2}", birth.getFirstNameEn());
		message = message.replace("{3}", birth.getApplicationNo());
		return message;
	}
	/**
	 * Creates sms request for the each owners
	 * 
	 * @param message
	 *                                The message for the specific tradeLicense
	 * @param mobileNumberToOwnerName
	 *                                Map of mobileNumber to OwnerName
	 * @return List of SMSRequest
	 */
	public List<SMSRequest> createSMSRequest(String message, Map<String, String> mobileNumberToOwnerName) {
		List<SMSRequest> smsRequest = new LinkedList<>();
		for (Map.Entry<String, String> entryset : mobileNumberToOwnerName.entrySet()) {
			String customizedMsg = message.replace("%s", "Varsha");
//			customizedMsg = customizedMsg.replace(NOTIF_OWNER_NAME_KEY, entryset.getValue());
			smsRequest.add(new SMSRequest(entryset.getKey(), customizedMsg));
		}
		return smsRequest;
	}

	 

	/**
	 * Fetches messages from localization service
	 * 
	 * @param tenantId
	 *                    tenantId of the tradeLicense
	 * @param requestInfo
	 *                    The requestInfo of the request
	 * @return Localization messages for the module
	 */
	public String getLocalizationMessages(String tenantId, RequestInfo requestInfo) {
		LinkedHashMap responseMap = (LinkedHashMap) serviceRequestRepository.fetchResult(getUri(tenantId, requestInfo),
				requestInfo);
		String jsonString = new JSONObject(responseMap).toString();
		return jsonString;
	}

	/**
	 * Returns the uri for the localization call
	 * 
	 * @param tenantId
	 *                 TenantId of the propertyRequest
	 * @return The uri for localization search call
	 */
	public StringBuilder getUri(String tenantId, RequestInfo requestInfo) {

		if (config.getIsLocalizationStateLevel())
			tenantId = tenantId.split("\\.")[0];

		String locale = NOTIFICATION_LOCALE;
		if (!StringUtils.isEmpty(requestInfo.getMsgId()) && requestInfo.getMsgId().split("|").length >= 2)
			locale = requestInfo.getMsgId().split("\\|")[1];

		StringBuilder uri = new StringBuilder();
		uri.append(config.getLocalizationHost()).append(config.getLocalizationContextPath())
				.append(config.getLocalizationSearchEndpoint()).append("?").append("locale=").append(locale)
				.append("&tenantId=").append(tenantId).append("&module=").append(MODULE)
				.append("&codes=").append(StringUtils.join(NOTIFICATION_CODES, ','));

		return uri;
	}
	
	/**
	 * Send the SMSRequest on the SMSNotification kafka topic
	 * 
	 * @param smsRequestList
	 *                       The list of SMSRequest to be sent
	 */
	public void sendSMS(List<SMSRequest> smsRequestList, boolean isSMSEnabled) {
		if (isSMSEnabled) {
			if (CollectionUtils.isEmpty(smsRequestList))
				log.info("Messages from localization couldn't be fetched!");
			for (SMSRequest smsRequest : smsRequestList) {
				producer.push(config.getSmsNotifTopic(), smsRequest);
				log.info("SMS SENT!");
			}
		}
	}

}
