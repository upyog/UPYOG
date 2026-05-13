package org.egov.ndc.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.ndc.config.NDCConfiguration;
import org.egov.ndc.producer.Producer;
import org.egov.ndc.repository.ServiceRequestRepository;
import org.egov.ndc.web.model.SMSRequest;
import org.egov.ndc.web.model.ndc.Application;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

import static org.egov.ndc.util.NDCConstants.*;

@Component
@Slf4j
public class NotificationUtil {

	@Autowired
	private NDCConfiguration config;

	@Autowired
	private Producer producer;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	

	/**
	 * Send the SMSRequest on the SMSNotification kafka topic
	 * 
	 * @param smsRequestList
	 *            The list of SMSRequest to be sent
	 */
	public void sendSMS(List<SMSRequest> smsRequestList, boolean isSMSEnabled) {
		if (isSMSEnabled) {
			if (CollectionUtils.isEmpty(smsRequestList))
				log.info("Messages from localization couldn't be fetched!");
			for (SMSRequest smsRequest : smsRequestList) {
				producer.push(config.getSmsNotifTopic(), smsRequest);
				log.info("MobileNumber: " + smsRequest.getMobileNumber() + " Messages: " + smsRequest.getMessage());
			}
		}
	}
	
	public void sendEmail(List<Map<String, Object>> emailRequests, boolean isEmailEnabled) {
	    if (isEmailEnabled) {
	        if (CollectionUtils.isEmpty(emailRequests)) {
	            log.info("No email requests found to send.");
	            return;
	        }

	        for (Map<String, Object> emailRequest : emailRequests) {
	            try {
	                // 1. Push to the EMAIL topic, not the SMS topic
	                producer.push(config.getEmailNotifTopic(), emailRequest);
	                
	                // 2. Log correctly using keys from your HashMap
	                log.info("Email sent to: " + emailRequest.get("email") + 
	                         " | Subject: " + emailRequest.get("subject"));
	                         
	            } catch (Exception e) {
	                log.error("Failed to push email to Kafka for: " + emailRequest.get("email"), e);
	            }
	        }
	    }
	}

	/**
	 * Creates sms request for the each owners
	 * 
	 * @param message
	 *            The message for the specific ndc
	 * @param mobileNumberToOwnerName
	 *            Map of mobileNumber to OwnerName
	 * @return List of SMSRequest
	 */
	public List<SMSRequest> createSMSRequest(String message, Map<String, String> mobileNumberToOwner) {
		List<SMSRequest> smsRequest = new LinkedList<>();
		for (Map.Entry<String, String> entryset : mobileNumberToOwner.entrySet()) {
			smsRequest.add(new SMSRequest(entryset.getKey(), message));
		}
		return smsRequest;
	}
	
	public List<Map<String, Object>> createEmailRequest(String message, Map<String, String> emailToOwner) {
	    List<Map<String, Object>> emailRequests = new LinkedList<>();
	    
	    for (Map.Entry<String, String> entryset : emailToOwner.entrySet()) {
	        HashMap<String, Object> emailRequest = new HashMap<>();
	        
	        // standard keys expected by egov-notification-mail service
	        emailRequest.put("emailTo", entryset.getValue()); // Receiver Email ID
	        emailRequest.put("subject", EMAIL_SUBJECT);           // e.g., "NDC Approval - Govt of Punjab"
	        emailRequest.put("body", message);             // Your full HTML String
	        emailRequest.put("isHTML", true);               // Tells the service to render the HTML
	        
	        emailRequests.add(emailRequest);
	        
	        log.info("HashMap Email Request created for: {}", entryset.getValue());
	    }
	    return emailRequests;
	}
	/**
	 * Returns the uri for the localization call
	 * 
	 * @param tenantId
	 *            TenantId of the propertyRequest
	 * @return The uri for localization search call
	 */
	public StringBuilder getUri(String tenantId, RequestInfo requestInfo) {
		if (config.getIsLocalizationStateLevel())
			tenantId = tenantId.split("\\.")[0];
		String locale = "en_IN";
		if (!StringUtils.isEmpty(requestInfo.getMsgId()) && requestInfo.getMsgId().split("|").length >= 2)
			locale = requestInfo.getMsgId().split("\\|")[1];

		StringBuilder uri = new StringBuilder();
		uri.append(config.getLocalizationHost()).append(config.getLocalizationContextPath())
				.append(config.getLocalizationSearchEndpoint()).append("?").append("locale=").append(locale)
				.append("&tenantId=").append(tenantId).append("&module=").append(NDCConstants.SEARCH_MODULE);
		return uri;
	}

	/**
	 * Fetches messages from localization service
	 * 
	 * @param tenantId
	 *            tenantId of the NDC
	 * @param requestInfo
	 *            The requestInfo of the request
	 * @return Localization messages for the module
	 */
	@SuppressWarnings("rawtypes")
	public String getLocalizationMessages(String tenantId, RequestInfo requestInfo) {
		Object o = serviceRequestRepository.fetchResult(getUri(tenantId, requestInfo), requestInfo);
		System.out.println(o);
		LinkedHashMap responseMap = (LinkedHashMap) o;
		String jsonString = new JSONObject(responseMap).toString();
		return jsonString;
	}

	/**
	 * Creates customized message based on ndc
	 * 
	 * @param ndc
	 *            The ndc for which message is to be sent
	 * @param localizationMessage
	 *            The messages from localization
	 * @return customized message based on ndc
	 */
	public String getCustomizedMsg(RequestInfo requestInfo, Application ndc, String localizationMessage) {
		String message = null, messageTemplate;
		String messageCode;
		if(ndc.getWorkflow() == null)
			messageCode = ndc.getWorkflow() + "_" + ndc.getApplicationStatus();
		else 
			messageCode = ndc.getWorkflow().getAction() + "_" + ndc.getApplicationStatus();
		switch (messageCode) {
		case ACTION_STATUS_CREATED:
			case ACTION_STATUS_FORWARD_REVIEW:
            case ACTION_STATUS_INITIATED:
				messageTemplate = getMessageTemplate(messageCode, localizationMessage);
				if (!StringUtils.isEmpty(messageTemplate))
					message = getInitiatedMsg(ndc, messageTemplate);
				break;
			case ACTION_STATUS_PAYMENT_CONFIRMATION:
				messageTemplate = getMessageTemplate(messageCode, localizationMessage);
				if (!StringUtils.isEmpty(messageTemplate))
					message = getPaymentConfirmationMsg(ndc, messageTemplate);
				break;
            case ACTION_STATUS_CITIZEN_ACTION_REQ:
				messageTemplate = getMessageTemplate(messageCode, localizationMessage);
				if (!StringUtils.isEmpty(messageTemplate))
					message = getSendBackToCitizenCommonMsg(ndc, messageTemplate);
				break;
            case ACTION_STATUS_APPROVED:
				messageTemplate = getMessageTemplate(messageCode, localizationMessage);
				if (!StringUtils.isEmpty(messageTemplate))
					message = getApproveMsg(ndc, messageTemplate);
				break;
            case ACTION_STATUS_REJECTED:
                messageTemplate = getMessageTemplate(messageCode, localizationMessage);
			if (!StringUtils.isEmpty(messageTemplate))
				message = getRejectedMsg(ndc, messageTemplate);
			break;
        }
		return message;
	}

	
	
	public String getCustomizedMsgForMail(RequestInfo requestInfo, Application ndc, String localizationMessage) {
		String message = null, messageTemplate;
		String messageCode;
		if(ndc.getWorkflow() == null)
			messageCode = ndc.getWorkflow() + "_" + ndc.getApplicationStatus();
		else 
			messageCode = ndc.getWorkflow().getAction() + "_" + ndc.getApplicationStatus();
		switch (messageCode) {
            case ACTION_STATUS_APPROVED:
            	messageCode=EMAIL_APPROVED_CODE;
				messageTemplate = getMessageTemplate(messageCode, localizationMessage);
				if (!StringUtils.isEmpty(messageTemplate))
					message = getApproveMsgForMail(ndc, messageTemplate);
				break;
        }
		return message;
	}
	/**
	 * Extracts message for the specific code
	 * 
	 * @param notificationCode
	 *            The code for which message is required
	 * @param localizationMessage
	 *            The localization messages
	 * @return message for the specific code
	 */
	@SuppressWarnings("rawtypes")
	public String getMessageTemplate(String notificationCode, String localizationMessage) {
		String path = "$..messages[?(@.code==\"{}\")].message";
		path = path.replace("{}", notificationCode);
		String message = null;
		try {
			List data = JsonPath.parse(localizationMessage).read(path);
			if (!CollectionUtils.isEmpty(data))
				message = data.get(0).toString();
			else
				log.error("Fetching from localization failed with code " + notificationCode);
		} catch (Exception e) {
			log.warn("Fetching from localization failed", e);
		}
		return message;
	}

	/**
	 * Creates customized message for initiate
	 * 
	 * @param ndc
	 *            tenantId of the ndc
	 * @param message
	 *            Message from localization for initiate
	 * @return customized message for initiate
	 */
	private String getInitiatedMsg(Application ndc, String message) {
		message = message.replace("[Citizen Name]", ndc.getOwners().get(0).getName());
		message = message.replace("[Application ID]", ndc.getApplicationNo());
		message = message.replace("[Date]", timestampToDate(ndc.getAuditDetails().getCreatedTime()));
		message = message.replace("[Department Name]", DEPARTMENT_PMIDC);
		return message;
	}

	private String getPaymentConfirmationMsg(Application ndc, String message) {
		message = message.replace("[Citizen Name]", ndc.getOwners().get(0).getName());
		message = message.replace("[Date]", timestampToDate(ndc.getAuditDetails().getLastModifiedTime()));
		message = message.replace("[Application ID]", ndc.getApplicationNo());
		message = message.replace("[Department Name]", DEPARTMENT_PMIDC);
		return message;
	}

	private String getSendBackToCitizenCommonMsg(Application ndc, String message) {
		message = message.replace("[Citizen Name]", ndc.getOwners().get(0).getName());
		message = message.replace("[Application ID]", ndc.getApplicationNo());
		message = message.replace("[Helpline Number]", HELPLINE_NUMBER);
		message = message.replace("[Portal/Office Name]", PORTAL_LINK);
		message = message.replace("[Department Name]", DEPARTMENT_PMIDC);

		return message;
	}
	private String getApproveMsg(Application ndc, String message) {
		message = message.replace("[Citizen Name]", ndc.getOwners().get(0).getName());
		message = message.replace("[Application ID]", ndc.getApplicationNo());
		message = message.replace("[Portal Link]", PORTAL_LINK);
		message = message.replace("[Office Name]", OFFICE_NAME);
		message = message.replace("[Department Name]", DEPARTMENT_PMIDC);
		return message;
	}

	
	private String getApproveMsgForMail(Application ndc, String message) {
	    String issueDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
	    String cityName = ndc.getTenantId().split("\\.")[1]; 
	    String consumerCode = ndc.getNdcDetails().get(0).getConsumerCode();   // check your field

		 String ownerName = ndc.getOwners().get(0).getName();
		    String applicationNo = ndc.getApplicationNo();

		    // Format Reason
		    String reason = formatReason(ndc.getReason());

		    // Extract Address
		    String address = "";
		    if (ndc.getNdcDetails() != null && !ndc.getNdcDetails().isEmpty()) {
		        String additionalDetails = ndc.getNdcDetails().get(0).getAdditionalDetails().toString();

		        try {
		            ObjectMapper mapper = new ObjectMapper();
		            JsonNode node = mapper.readTree(additionalDetails);
		            address = node.get("propertyAddress").asText();
		        } catch (Exception e) {
		            address = "";
		        }
		    }

		    message = message.replace("{ownerName}", ownerName);
		    message = message.replace("{applicationNo}", applicationNo);
		    message = message.replace("{propertyAddress}", address);
		    message = message.replace("{reason}", reason);
		    message = message.replace("{cityName}", cityName);
		    message = message.replace("{issueDate}", issueDate);
		    message = message.replace("{consumerCode}", consumerCode);

// usually works in egov


		    return message;
	}
	
	private String formatReason(String reason) {
	    if (reason == null) return "";

	    switch (reason) {
	        case "FOR_SALE_OF_PROPERTY":
	            return "Sale of Property";
	        case "FOR_LOAN_PURPOSE":
	            return "Loan Purpose";
	        case "FOR_PROPERTY_TRANSFER":
	            return "Property Transfer";
	        default:
	            return reason.replace("_", " ");
	    }
	}
	private String getRejectedMsg(Application ndc, String message) {
		message = message.replace("[Citizen Name]", ndc.getOwners().get(0).getName());
		message = message.replace("[Application ID]", ndc.getApplicationNo());
		message = message.replace("[Helpline Number]", HELPLINE_NUMBER);
		message = message.replace("[Portal/Office name]", PORTAL_LINK);
		message = message.replace("[Department Name]", DEPARTMENT_PMIDC);
		message = message.replace("[Reason]", ndc.getWorkflow().getComment());
		return message;
	}

	private String timestampToDate (long millis){
			Instant instant = Instant.ofEpochMilli(millis);
			ZoneId zone = ZoneId.of("Asia/Kolkata");
			ZonedDateTime dateTime = instant.atZone(zone);
			String formattedDate = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			return formattedDate;
	}
}