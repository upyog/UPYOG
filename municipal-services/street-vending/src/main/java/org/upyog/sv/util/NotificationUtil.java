package org.upyog.sv.util;

import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
import org.egov.tracer.model.CustomException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.upyog.sv.config.StreetVendingConfiguration;
import org.upyog.sv.constants.StreetVendingConstants;
import org.upyog.sv.kafka.producer.Producer;
import org.upyog.sv.repository.ServiceRequestRepository;
import org.upyog.sv.web.models.StreetVendingDetail;
import org.upyog.sv.web.models.StreetVendingRequest;
import org.upyog.sv.web.models.VendorDetail;
import org.upyog.sv.web.models.events.Action;
import org.upyog.sv.web.models.events.ActionItem;
import org.upyog.sv.web.models.events.EventRequest;
import org.upyog.sv.web.models.notification.Email;
import org.upyog.sv.web.models.notification.EmailRequest;
import org.upyog.sv.web.models.notification.SMSRequest;

import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NotificationUtil {

	private static final String REQUEST_INFO_KEY = "RequestInfo";
	private static final String TENANT_ID_KEY = "tenantId";
	private static final String USER_TYPE_KEY = "userType";
	private static final String USER_NAME_KEY = "userName";
	private static final String USER_FETCH_ERROR_MSG = "Exception while fetching user for username - ";
	private static final String EXCEPTION_TRACE_MSG = "Exception trace: ";

	private final ServiceRequestRepository serviceRequestRepository;

	private final StreetVendingConfiguration config;

	private final Producer producer;

	private final RestTemplate restTemplate;

	public NotificationUtil(ServiceRequestRepository serviceRequestRepository, StreetVendingConfiguration config,
			Producer producer, RestTemplate restTemplate) {
		this.serviceRequestRepository = serviceRequestRepository;
		this.config = config;
		this.producer = producer;
		this.restTemplate = restTemplate;
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

		String locale = StreetVendingConstants.NOTIFICATION_LOCALE;
		boolean isRetryNeeded = false;
		String jsonString;
		Map<String, Object> responseMap;

		if (!StringUtils.isEmpty(requestInfo.getMsgId()) && requestInfo.getMsgId().split("\\|").length >= 2) {
			locale = requestInfo.getMsgId().split("\\|")[1];
			isRetryNeeded = true;
		}

		responseMap = serviceRequestRepository.fetchResult(getUri(tenantId, requestInfo, locale),
				requestInfo);
		jsonString = new JSONObject(responseMap).toString();

		if (StringUtils.isEmpty(jsonString) && isRetryNeeded) {

			responseMap = serviceRequestRepository.fetchResult(
					getUri(tenantId, requestInfo, StreetVendingConstants.NOTIFICATION_LOCALE), requestInfo);
			jsonString = new JSONObject(responseMap).toString();
			if (StringUtils.isEmpty(jsonString))
				throw new CustomException("EG_EW_LOCALE_ERROR",
						"Localisation values not found for Ewaste notifications");
		}
		return jsonString;
	}

	/**
	 * Returns the uri for the localization call
	 *
	 * @return The uri for localization search call
	 */
	@SuppressWarnings("java:S1172")
	public StringBuilder getUri(String tenantId, RequestInfo requestInfo, String locale) {

		if (Boolean.TRUE.equals(config.getIsLocalizationStateLevel()))
			tenantId = tenantId.split("\\.")[0];

		StringBuilder uri = new StringBuilder();
		uri.append(config.getLocalizationHost()).append(config.getLocalizationContextPath())
				.append(config.getLocalizationSearchEndpoint()).append("?").append("locale=").append(locale)
				.append("&tenantId=").append(tenantId).append("&module=")
				.append(StreetVendingConstants.NOTIFICATION_MODULE_NAME);

		return uri;
	}

	/**
	 * Enriches the smsRequest with the customized messages
	 *
	 * @param request
	 *            The request from kafka topic
	 * @param smsRequests
	 *            List of SMSRequets
	 */
	public void enrichSMSRequest(StreetVendingRequest request, List<SMSRequest> smsRequests) {
	    String tenantId = request.getStreetVendingDetail().getTenantId();
	    String localizationMessages = getLocalizationMessages(tenantId, request.getRequestInfo());
	    Map<String, String> messageMap = getCustomizedMsg(request.getRequestInfo(), request.getStreetVendingDetail(), localizationMessages);
	    List<VendorDetail> vendorDetails = request.getStreetVendingDetail().getVendorDetail();
	    String mobileNumber = null;
	    
	    if (vendorDetails != null && !vendorDetails.isEmpty()) {
	        mobileNumber = vendorDetails.get(0).getMobileNo();  
	    }
	    
	    if (mobileNumber != null) {
	        Map<String, String> mobileNumberToOwner = fetchUserUUIDs(mobileNumber, request.getRequestInfo(), tenantId, request.getStreetVendingDetail());
	        smsRequests.addAll(createSMSRequest(messageMap.get(StreetVendingConstants.MESSAGE_TEXT), mobileNumberToOwner));
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
	public Map<String, String> fetchUserUUIDs(String mobileNumber, RequestInfo requestInfo, String tenantId, StreetVendingDetail requestDetail) {
		Map<String, String> mapOfPhoneNoAndUUIDs = new HashMap<>();
		StringBuilder uri = new StringBuilder();
		uri.append(config.getUserHost()).append(config.getUserSearchEndpoint());
	    String userMobileNumber = requestDetail.getVendorDetail().get(0).getMobileNo();
		Map<String, Object> userSearchRequest = new HashMap<>();
		userSearchRequest.put(REQUEST_INFO_KEY, requestInfo);
		userSearchRequest.put(TENANT_ID_KEY, tenantId);
		userSearchRequest.put(USER_TYPE_KEY, StreetVendingConstants.CITIZEN);
		userSearchRequest.put(USER_NAME_KEY, userMobileNumber);
		try {

			Object user = serviceRequestRepository.fetchResult(uri, userSearchRequest);
			log.info("User fetched in fetUserUUID method of StreetVending notfication consumer" + user.toString());
			if (user != null) {
				String uuid = JsonPath.read(user, "$.user[0].uuid");
				mapOfPhoneNoAndUUIDs.put(userMobileNumber, uuid);
				log.info("mapOfPhoneNoAndUUIDs : " + mapOfPhoneNoAndUUIDs);
			}
		} catch (Exception e) {
			log.error(USER_FETCH_ERROR_MSG + userMobileNumber);
			log.error(EXCEPTION_TRACE_MSG, e);
		}

		return mapOfPhoneNoAndUUIDs;
	}
	


	/**
	 * Creates sms request for the each owners
	 * 
	 * @param mobileNumberToOwnerName Map of mobileNumber to OwnerName
	 * @return List of SMSRequest
	 */
	public List<SMSRequest> createSMSRequest(String message, Map<String, String> mobileNumberToOwnerName) {

		List<SMSRequest> smsRequest = new LinkedList<>();
		for (Map.Entry<String, String> entryset : mobileNumberToOwnerName.entrySet()) {
			String customizedMsg = message.replace(StreetVendingConstants.NOTIFICATION_OWNERNAME, entryset.getValue());
			smsRequest.add(new SMSRequest(entryset.getKey(), customizedMsg));
		}
		return smsRequest;
	}

	/**
	 * Send the SMSRequest on the SMSNotification kafka topic
	 *
	 * @param smsRequestList The list of SMSRequest to be sent
	 */
	public void sendSMS(List<SMSRequest> smsRequestList) {

		if (Boolean.TRUE.equals(config.getIsSMSNotificationEnabled())) {
			if (CollectionUtils.isEmpty(smsRequestList))
				log.info("Messages from localization couldn't be fetched!");
			for (SMSRequest smsRequest : smsRequestList) {
				producer.push(config.getSmsNotifTopic(), smsRequest);
				log.info("Sending SMS notification: ");
				log.info("MobileNumber: " + smsRequest.getMobileNumber() + " Messages: " + smsRequest.getMessage());
			}
		}
	}

	/**
	 * Fetches UUIDs of CITIZENs based on the phone number.
	 *
	 * @param mobileNumbers mobile numbers to search
	 * @param requestInfo request information
	 * @param tenantId tenant identifier
	 * @return map of phone numbers to UUIDs
	 */
	public Map<String, String> fetchUserUUIDs(Set<String> mobileNumbers, RequestInfo requestInfo, String tenantId) {

		Map<String, String> mapOfPhnoAndUUIDs = new HashMap<>();
		StringBuilder uri = new StringBuilder();
		uri.append(config.getUserHost()).append(config.getUserSearchEndpoint());
		Map<String, Object> userSearchRequest = new HashMap<>();
		userSearchRequest.put(REQUEST_INFO_KEY, requestInfo);
		userSearchRequest.put(TENANT_ID_KEY, tenantId);
		userSearchRequest.put(USER_TYPE_KEY, StreetVendingConstants.CITIZEN);
		for (String mobileNo : mobileNumbers) {
			userSearchRequest.put(USER_NAME_KEY, mobileNo);
			try {
				Object user = serviceRequestRepository.fetchResult(uri, userSearchRequest);
				if (null != user) {
					String uuid = JsonPath.read(user, "$.user[0].uuid");
					mapOfPhnoAndUUIDs.put(mobileNo, uuid);
				} else {
					log.error("Service returned null while fetching user for username - " + mobileNo);
				}
			} catch (Exception e) {
				log.error(USER_FETCH_ERROR_MSG + mobileNo);
				log.error(EXCEPTION_TRACE_MSG, e);
			}
		}
		return mapOfPhnoAndUUIDs;
	}

	/**
	 * Pushes the event request to Kafka Queue.
	 *
	 * @param request event request to publish
	 */
	public void sendEventNotification(EventRequest request) {
		log.info("EVENT notification sent!" + request);
		producer.push(config.getSaveUserEventsTopic(), request);
	}

	/**
	 * Creates email request for the each owners
	 *
	 * @param mobileNumberToEmailId Map of mobileNumber to emailIds
	 * @return List of EmailRequest
	 */

	public List<EmailRequest> createEmailRequest(RequestInfo requestInfo, String message,
			Map<String, String> mobileNumberToEmailId) {

		List<EmailRequest> emailRequest = new LinkedList<>();
		for (Map.Entry<String, String> entryset : mobileNumberToEmailId.entrySet()) {
			String customizedMsg = "";
			if (message.contains(StreetVendingConstants.NOTIFICATION_EMAIL))
				customizedMsg = message.replace(StreetVendingConstants.NOTIFICATION_EMAIL, entryset.getValue());

			String subject = "";
			String body = customizedMsg;
			Email emailobj = Email.builder().emailTo(Collections.singleton(entryset.getValue())).isHTML(false)
					.body(body).subject(subject).build();
			EmailRequest email = new EmailRequest(requestInfo, emailobj);
			emailRequest.add(email);
		}
		return emailRequest;
	}

	/**
	 * Send the EmailRequest on the EmailNotification kafka topic
	 *
	 * @param emailRequestList The list of EmailRequest to be sent
	 */
	public void sendEmail(List<EmailRequest> emailRequestList) {

		if (Boolean.TRUE.equals(config.getIsEmailNotificationEnabled())) {
			if (CollectionUtils.isEmpty(emailRequestList))
				log.info("Messages from localization couldn't be fetched!");
			for (EmailRequest emailRequest : emailRequestList) {
				if (!StringUtils.isEmpty(emailRequest.getEmail().getBody())) {
					producer.push(config.getEmailNotifTopic(), emailRequest);
					log.info("Sending EMAIL notification! ");
					log.info("Email Id: " + emailRequest.getEmail().toString());
				} else {
					log.info("Email body is empty, hence no email notification will be sent.");
				}
			}

		}
	}

	/**
	 * Fetches email ids of CITIZENs based on the phone number.
	 *
	 * @param mobileNumbers mobile numbers to search
	 * @param requestInfo request information
	 * @param tenantId tenant identifier
	 * @return map of phone numbers to email IDs
	 */

	public Map<String, String> fetchUserEmailIds(Set<String> mobileNumbers, RequestInfo requestInfo, String tenantId) {
		Map<String, String> mapOfPhnoAndEmailIds = new HashMap<>();
		StringBuilder uri = new StringBuilder();
		uri.append(config.getUserHost()).append(config.getUserSearchEndpoint());
		Map<String, Object> userSearchRequest = new HashMap<>();
		userSearchRequest.put(REQUEST_INFO_KEY, requestInfo);
		userSearchRequest.put(TENANT_ID_KEY, tenantId);
		userSearchRequest.put(USER_TYPE_KEY, StreetVendingConstants.CITIZEN);
		for (String mobileNo : mobileNumbers) {
			userSearchRequest.put(USER_NAME_KEY, mobileNo);
			try {
				Object user = serviceRequestRepository.fetchResult(uri, userSearchRequest);
				if (null != user) {
					if (JsonPath.read(user, "$.user[0].emailId") != null) {
						String email = JsonPath.read(user, "$.user[0].emailId");
						mapOfPhnoAndEmailIds.put(mobileNo, email);
					}
				} else {
					log.error("Service returned null while fetching user for username - " + mobileNo);
				}
			} catch (Exception e) {
				log.error(USER_FETCH_ERROR_MSG + mobileNo);
				log.error(EXCEPTION_TRACE_MSG, e);
			}
		}
		return mapOfPhnoAndEmailIds;
	}

	/**
	 * Method to fetch the list of channels for a particular action from mdms
	 * configd from mdms configs returns the message minus some lines to match In
	 * App Templates
	 * 
	 * @param requestInfo request information
	 * @param tenantId tenant identifier
	 * @param moduleName module name
	 * @param action workflow action
	 * @return list of channel names
	 */
	public List<String> fetchChannelList(RequestInfo requestInfo, String tenantId, String moduleName, String action) {
		List<String> masterData = new ArrayList<>();
		StringBuilder uri = new StringBuilder();
		uri.append(config.getMdmsHost()).append(config.getMdmsPath());
		if (StringUtils.isEmpty(tenantId))
			return masterData;
		MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestForChannelList(requestInfo, tenantId);

		Filter masterDataFilter = filter(
				where(StreetVendingConstants.MODULE).is(moduleName).and(StreetVendingConstants.ACTION).is(action));

		try {
			Object response = serviceRequestRepository.fetchResult(uri, mdmsCriteriaReq);
			masterData = JsonPath.parse(response).read("$.MdmsRes.Channel.channelList[?].channelNames[*]",
					masterDataFilter);
		} catch (Exception e) {
			log.error("Exception while fetching workflow states to ignore: ", e);
		}

		return masterData;
	}

	
	/**
	 * Builds an MDMS request for retrieving the channel list.
	 *
	 * @param requestInfo the request information containing metadata
	 * @param tenantId    the tenant ID for which the data is requested
	 * @return an {@link MdmsCriteriaReq} object containing the request criteria for fetching the channel list
	 */
	
	private MdmsCriteriaReq getMdmsRequestForChannelList(RequestInfo requestInfo, String tenantId) {
		MasterDetail masterDetail = new MasterDetail();
		masterDetail.setName(StreetVendingConstants.CHANNEL_LIST);
		List<MasterDetail> masterDetailList = new ArrayList<>();
		masterDetailList.add(masterDetail);

		ModuleDetail moduleDetail = new ModuleDetail();
		moduleDetail.setMasterDetails(masterDetailList);
		moduleDetail.setModuleName(StreetVendingConstants.CHANNEL);
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
	
	/**
	 * Retrieves a customized notification message based on the application status and workflow action.
	 *
	 * @param requestInfo         the request information containing metadata
	 * @param streetVendingDetail the details of the street vending application
	 * @param localizationMessage the localized message template
	 * @return a customized notification message for the user
	 */

	public Map<String, String> getCustomizedMsg(RequestInfo requestInfo, StreetVendingDetail streetVendingDetail,
			String localizationMessage) {
		String message = null;
		String messageTemplate;
		String link = null;
		String actionStatus = streetVendingDetail.getWorkflow().getAction();
		switch (actionStatus) {

		case StreetVendingConstants.ACTION_STATUS_APPLY:
			messageTemplate = getMessageTemplate(StreetVendingConstants.NOTIFICATION_SUBMIT, localizationMessage);
			message = getMessageWithNumber(streetVendingDetail, messageTemplate);
			break;

		case StreetVendingConstants.ACTION_STATUS_FORWARD:
			if (streetVendingDetail.getApplicationStatus().equals(StreetVendingConstants.INSPECTION_PENDING_STATUS)) {
				messageTemplate = getMessageTemplate(StreetVendingConstants.NOTIFICATION_INSPECTION,
						localizationMessage);
			} else {
				messageTemplate = getMessageTemplate(StreetVendingConstants.NOTIFICATION_INSPECTION_COMPLETE,
						localizationMessage);
			}
			message = getMessageWithNumber(streetVendingDetail, messageTemplate);
			break;
			
		case StreetVendingConstants.ACTION_STATUS_APPROVE:
			messageTemplate = getMessageTemplate(StreetVendingConstants.NOTIFICATION_APPROVED, localizationMessage);
			message = getMessageWithNumber(streetVendingDetail, messageTemplate);
			break;	

		case StreetVendingConstants.ACTION_STATUS_SENDBACKTOCITIZEN:
			messageTemplate = getMessageTemplate(StreetVendingConstants.NOTIFICATION_SENTBACK, localizationMessage);
			message = getMessageWithNumber(streetVendingDetail, messageTemplate);
			break;

		case StreetVendingConstants.ACTION_STATUS_REJECT:
			messageTemplate = getMessageTemplate(StreetVendingConstants.NOTIFICATION_REJECT, localizationMessage);
			message = getMessageWithNumber(streetVendingDetail, messageTemplate);
			break;

		case StreetVendingConstants.ACTION_STATUS_PAY:
			messageTemplate = getMessageTemplate(StreetVendingConstants.NOTIFICATION_REGISTRATIONCOMPLETED,
					localizationMessage);
			message = getMessageWithNumberAndFinalDetails(streetVendingDetail, messageTemplate);
			break;
		case StreetVendingConstants.ACTION_STATUS_ELIGIBLE_TO_RENEW:
			messageTemplate = getMessageTemplate(StreetVendingConstants.NOTIFICATION_ELIGIBLETORENEW,
					localizationMessage);
			message = getMessageWithNumberAndFinalDetails(streetVendingDetail, messageTemplate);
			break;
		case StreetVendingConstants.ACTION_STATUS_APPLICATION_EXPIRED:
			messageTemplate = getMessageTemplate(StreetVendingConstants.NOTIFICATION_APPLICATIONEXPIRED,
					localizationMessage);
			message = getMessageWithNumberAndFinalDetails(streetVendingDetail, messageTemplate);
			break; 		
		case StreetVendingConstants.ACTION_STATUS_SCHEDULE_PAYMENT:
		    messageTemplate = getMessageTemplate(StreetVendingConstants.NOTIFICATION_SCHEDULEPAYMENT, localizationMessage);
		    message = getMessageWithNumberAndFinalDetails(streetVendingDetail, messageTemplate);
		    break;
		default:
			break;
	
		}
		
		
		if (message.contains(StreetVendingConstants.NOTIFICATION_PAY_NOW)) {
			   
		    link = getPayUrl(streetVendingDetail, message);
		}		
		
		if (message.contains(StreetVendingConstants.NOTIFICATION_DOWNLOAD_RECEIPT)) {
			   
		    link = getReceiptDownloadLink(streetVendingDetail);
		}
		

		Map<String, String> messageMap = new HashMap<>();
		messageMap.put(StreetVendingConstants.ACTION_LINK, link); 
		messageMap.put(StreetVendingConstants.MESSAGE_TEXT, message);

		log.info("getCustomizedMsg messageTemplate : " + message);
		return messageMap;
	}

	/**
	 * Formats a notification message by replacing placeholders with the vendor's name and application number.
	 *
	 * @param streetVendingDetail the details of the street vending application
	 * @param message             the message template with placeholders
	 * @return a formatted message with the vendor's details
	 */
	
	private String getMessageWithNumber(StreetVendingDetail streetVendingDetail, String message) {
		message = message.replace("{1}", streetVendingDetail.getVendorDetail().get(0).getName());
		message = message.replace("{2}", streetVendingDetail.getApplicationNo());
		return message;
	}

	/**
	 * Formats a notification message by replacing placeholders with the vendor's name, application number,
	 * and certificate number.
	 *
	 * @param streetVendingDetail the details of the street vending application
	 * @param message             the message template with placeholders
	 * @return a formatted message with the vendor's details and certificate number
	 */
	
	private String getMessageWithNumberAndFinalDetails(StreetVendingDetail streetVendingDetail, String message) {
		message = message.replace("{1}", streetVendingDetail.getVendorDetail().get(0).getName());
		message = message.replace("{2}", streetVendingDetail.getApplicationNo());
		message = message.replace("{3}", streetVendingDetail.getCertificateNo());
		return message;
	}
	
	/**
	 * Generates a shortened payment URL for the citizen to make payment based on the application details.
	 *
	 * @param streetVendingDetail The application detail object containing applicant and application metadata.
	 * @param message              The notification message (not used in this method, but kept for signature consistency).
	 * @return A shortened payment URL pointing to the citizen's "Pay Now" page.
	 */
	public String getPayUrl(StreetVendingDetail streetVendingDetail, String message) {
	    String payLinkTemplate = config.getPayNowLink();
	    String actionLink = String.format(payLinkTemplate,
	            config.getModuleName(),
	            streetVendingDetail.getApplicationNo()
	            );
	    
	    String finalUrl = config.getUiAppHost() + actionLink;
	    
	    log.info("Final url for Payment link :  " + finalUrl);

	    return getShortenedUrl(finalUrl);
	}
	
	/**
	 * Generates a downloadable receipt link for the given {@link StreetVendingDetail}.
	 *
	 * @param streetVendingDetail the street vending detail object containing the application number and tenant ID
	 * @return a shortened URL string for downloading the receipt
	 */
	
	public String getReceiptDownloadLink(StreetVendingDetail streetVendingDetail) {
		
		String downloadReceiptLinkTemplate = config.getDownloadReceiptLink();
	    String actionLink = String.format(downloadReceiptLinkTemplate,
	            streetVendingDetail.getApplicationNo(),
	            streetVendingDetail.getTenantId()
	            );
	    
	    String finalUrl = config.getUiAppHost() + actionLink;
	    
	    log.info("Final url to download receipt :  " + finalUrl);
	    return getShortenedUrl(finalUrl);

	}
	
	
	/**
	 * Shortens a given URL using the configured URL shortening service.
	 *
	 * @param url The original long URL to be shortened.
	 * @return The shortened URL returned by the shortening service, or the original URL if shortening fails.
	 */
	public String getShortenedUrl(String url) {
		String res = null;
		HashMap<String, String> body = new HashMap<>();
		body.put("url", url);
		StringBuilder builder = new StringBuilder(config.getUrlShortnerHost());
		builder.append(config.getShortenerEndpoint());
		try {
			res = restTemplate.postForObject(builder.toString(), body, String.class);

		} catch (Exception e) {
			log.error("Error while shortening the url: " + url, e);

		}
		if (StringUtils.isEmpty(res)) {
			log.error("URL_SHORTENING_ERROR", "Unable to shorten url: " + url);
			return url;
		}
		return res;
	}
	
	/**
	 * Generates an {@link Action} object based on specific placeholders found in the input message.
	 *
	 * @param message    The notification message which may contain action placeholders.
	 * @param actionLink The URL to be used for the action (e.g., pay now or download receipt).
	 * @param tenantId   The tenant ID to associate with the generated action.
	 * @return An {@link Action} object if the message contains a recognized action code, otherwise {@code null}.
	 */
	
	public Action getActionLinkAndCode(String message, String actionLink, String tenantId) {
	    
	        String code = StringUtils.substringBetween(
	                message, 
	                StreetVendingConstants.NOTIFICATION_ACTION, 
	                StreetVendingConstants.NOTIFICATION_ACTION_BUTTON
	        );
	
	        if (StreetVendingConstants.NOTIFICATION_PAY_NOW.equalsIgnoreCase(code) || 
	        		StreetVendingConstants.NOTIFICATION_DOWNLOAD_RECEIPT.equalsIgnoreCase(code)) {
	
	            ActionItem actionItem = ActionItem.builder()
	                    .actionUrl(actionLink)
	                    .code(code)
	                    .build();
	
	            return Action.builder()
	                    .tenantId(tenantId)
	                    .actionUrls(Collections.singletonList(actionItem))
	                    .build();
	        }
	    
	    return null;
	}
	

}
