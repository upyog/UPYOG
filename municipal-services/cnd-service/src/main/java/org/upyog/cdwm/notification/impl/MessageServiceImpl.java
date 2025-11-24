package org.upyog.cdwm.notification.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.cdwm.config.CNDConfiguration;
import org.upyog.cdwm.notification.MessageService;
import org.upyog.cdwm.notification.constants.NotificationConstants;
import org.upyog.cdwm.notification.util.NotificationUtil;
import org.upyog.cdwm.repository.ServiceRequestRepository;
import org.upyog.cdwm.web.models.CNDApplicationDetail;

import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MessageServiceImpl implements MessageService{

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;
	
	@Autowired
	private NotificationUtil util;

	/**
	 * Generates a customized notification message based on the action status of a 
	 * CND application detail and localization message.
	 *
	 * @param requestInfo         The request information containing metadata about the request.
	 * @param cndApplicationDetail The CND application details, including workflow actions.
	 * @param localizationMessage The localized message template used for generating notifications.
	 * @return A map containing the notification message and an action link.
	 *
	 * The method determines the action status from the application's workflow and 
	 * retrieves the corresponding message template. It then formats the message 
	 * with relevant details and returns it in a map along with an action link.
	 *
	 * Supported action statuses:
	 * - APPLY: Sends a message when an application is submitted.
	 * - APPROVE: Sends a message when an application is approved.
	 * - ASSIGN_VENDOR: Sends a message when a vendor is assigned.
	 * - ASSIGN_VEHICLE_DRIVER: Sends a message when a vehicle driver is assigned.
	 * - COMPLETE_REQUEST: Sends a message when a request is completed.
	 * - REJECT: Sends a message when an application is rejected.
	 * - PAY: Sends a message when payment is made.
	 *
	 * Logs the generated message template for debugging purposes.
	 */
	
	@Override
	public Map<String, String> getCustomizedMsg(RequestInfo requestInfo, CNDApplicationDetail cndApplicationDetail,
			String localizationMessage) {
		String message = null, messageTemplate;
		String link = null;
		String ACTION_STATUS = cndApplicationDetail.getWorkflow().getAction();
		switch (ACTION_STATUS) {

		case NotificationConstants.ACTION_STATUS_APPLY:
			messageTemplate = getMessageTemplate(NotificationConstants.NOTIFICATION_APPLY, localizationMessage);
			message = getMessageWithNumberAndFinalDetails(cndApplicationDetail, messageTemplate);
			break;
			
		case NotificationConstants.ACTION_STATUS_APPROVE:
			messageTemplate = getMessageTemplate(NotificationConstants.NOTIFICATION_APPROVED, localizationMessage);
			message = getMessageWithNumberAndFinalDetails(cndApplicationDetail, messageTemplate);
			break;	
			
		case NotificationConstants.ACTION_STATUS_ASSIGN_VENDOR:
			messageTemplate = getMessageTemplate(NotificationConstants.NOTIFICATION_ASSIGN_VENDOR, localizationMessage);
			message = getMessageWithNumberAndFinalDetails(cndApplicationDetail, messageTemplate);
			break;
		
		case NotificationConstants.ACTION_STATUS_ASSIGN_VEHICLE_DRIVER:
			messageTemplate = getMessageTemplate(NotificationConstants.NOTIFICATION_ASSIGN_VEHICLE_DRIVER, localizationMessage);
			message = getMessageWithNumberAndFinalDetails(cndApplicationDetail, messageTemplate);
			break;
			
		case NotificationConstants.ACTION_STATUS_COMPLETE_REQUEST:
			messageTemplate = getMessageTemplate(NotificationConstants.NOTIFICATION_COMPLETE_REQUEST, localizationMessage);
			message = getMessageWithNumberAndFinalDetails(cndApplicationDetail, messageTemplate);
			break;		
	
		case NotificationConstants.ACTION_STATUS_REJECT:
			messageTemplate = getMessageTemplate(NotificationConstants.NOTIFICATION_REJECT, localizationMessage);
			message = getMessageWithNumberAndFinalDetails(cndApplicationDetail, messageTemplate);
			break;

		case NotificationConstants.ACTION_STATUS_PAY:
			messageTemplate = getMessageTemplate(NotificationConstants.NOTIFICATION_APPLICATIONSUBMITTED,
					localizationMessage);
			message = getMessageWithNumberAndFinalDetails(cndApplicationDetail, messageTemplate);
			break;
		}
		

		if (message.contains(NotificationConstants.NOTIFICATION_PAY_NOW)) {
		   
		    link = util.getPayUrl(cndApplicationDetail, message);
		}		
		
		if (message.contains(NotificationConstants.NOTIFICATION_DOWNLOAD_RECEIPT)) {
			   
		    link = util.getReceiptDownloadLink(cndApplicationDetail);
		}
		
		Map<String, String> messageMap = new HashMap<>();
		messageMap.put(NotificationConstants.ACTION_LINK, link); 
		messageMap.put(NotificationConstants.MESSAGE_TEXT, message);

		log.info("getCustomizedMsg messageTemplate : " + message);
		return messageMap;


	}


	@Override
	public String getMessageWithNumberAndFinalDetails(CNDApplicationDetail cndApplicationDetail, String message) {
	    return String.format(message, cndApplicationDetail.getApplicantDetail().getNameOfApplicant(), cndApplicationDetail.getApplicationNumber());
	}
	
	
	/**
	 * Extracts message for the specific code
	 *
	 * @param notificationCode    The code for which message is required
	 * @param localizationMessage The localization messages
	 * @return message for the specific code
	 */
	public String getMessageTemplate(String notificationCode, String localizationMessage) {

		String path = "$..messages[?(@.code==\"{}\")].message";
		path = path.replace("{}", notificationCode);
		String message = "";
		try {
			Object messageObj = JsonPath.parse(localizationMessage).read(path);
			message = ((ArrayList<String>) messageObj).get(0);
		} catch (Exception e) {
			log.warn("Fetching from localization failed", e);
		}
		return message;
	}
	
	/**
	 * Fetches messages from localization service
	 *
	 * @param tenantId    tenantId of the PTR
	 * @param requestInfo The requestInfo of the request
	 * @return Localization messages for the module
	 */
	public String getLocalizationMessages(String tenantId, RequestInfo requestInfo) {

		String locale = NotificationConstants.NOTIFICATION_LOCALE;
		Boolean isRetryNeeded = false;
		String jsonString = null;
		LinkedHashMap responseMap = null;

		if (!StringUtils.isEmpty(requestInfo.getMsgId()) && requestInfo.getMsgId().split("\\|").length >= 2) {
			locale = requestInfo.getMsgId().split("\\|")[1];
			isRetryNeeded = true;
		}

		responseMap = (LinkedHashMap) serviceRequestRepository.fetchResult(util.getUri(tenantId, requestInfo, locale),
				requestInfo);
		jsonString = new JSONObject(responseMap).toString();

		if (StringUtils.isEmpty(jsonString) && isRetryNeeded) {

			responseMap = (LinkedHashMap) serviceRequestRepository.fetchResult(
					util.getUri(tenantId, requestInfo, NotificationConstants.NOTIFICATION_LOCALE), requestInfo);
			jsonString = new JSONObject(responseMap).toString();
			if (StringUtils.isEmpty(jsonString))
				throw new CustomException("UG_RS_LOCALE_ERROR",
						"Localisation values not found for Request Service notifications");
		}
		return jsonString;
	}

}
