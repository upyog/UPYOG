package org.upyog.rs.util;

import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.upyog.rs.config.RequestServiceConfiguration;
import org.upyog.rs.constant.RequestServiceConstants;
import org.upyog.rs.kafka.Producer;
import org.upyog.rs.repository.ServiceRequestRepository;
import org.upyog.rs.web.models.events.EventRequest;
import org.upyog.rs.web.models.mobileToilet.MobileToiletBookingDetail;
import org.upyog.rs.web.models.mobileToilet.MobileToiletBookingRequest;
import org.upyog.rs.web.models.notification.Email;
import org.upyog.rs.web.models.notification.EmailRequest;
import org.upyog.rs.web.models.notification.SMSRequest;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingDetail;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingRequest;

import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NotificationUtil {

	private ServiceRequestRepository serviceRequestRepository;

	private RequestServiceConfiguration config;

	private Producer producer;

	private RestTemplate restTemplate;

	@Autowired
	public NotificationUtil(ServiceRequestRepository serviceRequestRepository, RequestServiceConfiguration config,
			Producer producer, RestTemplate restTemplate) {
		this.serviceRequestRepository = serviceRequestRepository;
		this.config = config;
		this.producer = producer;
		this.restTemplate = restTemplate;
	}

	public static final String ACTION_STATUS_APPLY = "APPLY";
	
	public static final String ACTION_STATUS_APPROVE = "APPROVE";

	public static final String ACTION_STATUS_ASSIGN_VENDOR = "ASSIGN_VENDOR";

	public static final String ACTION_STATUS_ASSIGN_VEHICLE_DRIVER = "ASSIGN_VEHICLE_DRIVER";

	public static final String ACTION_STATUS_REJECT = "REJECT";
	
	public static final String ACTION_STATUS_COMPLETE_REQUEST = "COMPLETE_REQUEST";

	public static final String ACTION_STATUS_PAY = "PAY";
	
	public static final String ACTION_LINK = "ACTION_LINK";
	
	public static final String MESSAGE_TEXT = "MESSAGE_TEXT";
	
	private final String URL = "url";

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

		String locale = RequestServiceConstants.NOTIFICATION_LOCALE;
		Boolean isRetryNeeded = false;
		String jsonString = null;
		LinkedHashMap responseMap = null;

		if (!StringUtils.isEmpty(requestInfo.getMsgId()) && requestInfo.getMsgId().split("\\|").length >= 2) {
			locale = requestInfo.getMsgId().split("\\|")[1];
			isRetryNeeded = true;
		}

		responseMap = (LinkedHashMap) serviceRequestRepository.fetchResult(getUri(tenantId, requestInfo, locale),
				requestInfo);
		jsonString = new JSONObject(responseMap).toString();

		if (StringUtils.isEmpty(jsonString) && isRetryNeeded) {

			responseMap = (LinkedHashMap) serviceRequestRepository.fetchResult(
					getUri(tenantId, requestInfo, RequestServiceConstants.NOTIFICATION_LOCALE), requestInfo);
			jsonString = new JSONObject(responseMap).toString();
			if (StringUtils.isEmpty(jsonString))
				throw new CustomException("UG_RS_LOCALE_ERROR",
						"Localisation values not found for Request Service notifications");
		}
		return jsonString;
	}

	/**
	 * Returns the uri for the localization call
	 *
	 * @return The uri for localization search call
	 */
	public StringBuilder getUri(String tenantId, RequestInfo requestInfo, String locale) {

		if (config.getIsLocalizationStateLevel())
			tenantId = tenantId.split("\\.")[0];

		StringBuilder uri = new StringBuilder();
		uri.append(config.getLocalizationHost()).append(config.getLocalizationContextPath())
				.append(config.getLocalizationSearchEndpoint()).append("?").append("locale=").append(locale)
				.append("&tenantId=").append(tenantId).append("&module=")
				.append(RequestServiceConstants.NOTIFICATION_MODULE_NAME);

		return uri;
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
			String customizedMsg = message.replace(RequestServiceConstants.NOTIFICATION_OWNERNAME, entryset.getValue());
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

		if (config.getIsSMSNotificationEnabled()) {
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


	/**
	 * Fetches UUIDs of CITIZENs based on the phone number.
	 *
	 * @param mobileNumbers
	 * @param requestInfo
	 * @param tenantId
	 * @return
	 */
	public Map<String, String> fetchUserUUIDs(Set<String> mobileNumbers, RequestInfo requestInfo, String tenantId) {

		Map<String, String> mapOfPhnoAndUUIDs = new HashMap<>();
		StringBuilder uri = new StringBuilder();
		uri.append(config.getUserHost()).append(config.getUserSearchEndpoint());
		Map<String, Object> userSearchRequest = new HashMap<>();
		userSearchRequest.put("RequestInfo", requestInfo);
		userSearchRequest.put("tenantId", tenantId);
		userSearchRequest.put("userType", "CITIZEN");
		for (String mobileNo : mobileNumbers) {
			userSearchRequest.put("userName", mobileNo);
			try {
				Object user = serviceRequestRepository.fetchResult(uri, userSearchRequest);
				if (null != user) {
					String uuid = JsonPath.read(user, "$.user[0].uuid");
					mapOfPhnoAndUUIDs.put(mobileNo, uuid);
				} else {
					log.error("Service returned null while fetching user for username - " + mobileNo);
				}
			} catch (Exception e) {
				log.error("Exception while fetching user for username - " + mobileNo);
				log.error("Exception trace: ", e);
				continue;
			}
		}
		return mapOfPhnoAndUUIDs;
	}

	/**
	 * Pushes the event request to Kafka Queue.
	 *
	 * @param request
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
			if (message.contains(RequestServiceConstants.NOTIFICATION_EMAIL))
				customizedMsg = message.replace(RequestServiceConstants.NOTIFICATION_EMAIL, entryset.getValue());

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

		if (config.getIsEmailNotificationEnabled()) {
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
	 * Enriches the smsRequest with the customized messages
	 *
	 * @param request
	 *            The request from kafka topic
	 * @param smsRequests
	 *            List of SMSRequets
	 */

	public void enrichSMSRequest(Object request, List<SMSRequest> smsRequests) {
	    String tenantId;
	    String localizationMessages;
	    Map<String, String> messageMap;
	    String message;
	    String mobileNumber;
	    RequestInfo requestInfo;

	    if (request instanceof WaterTankerBookingRequest) {
	        WaterTankerBookingRequest waterRequest = (WaterTankerBookingRequest) request;
	        tenantId = waterRequest.getWaterTankerBookingDetail().getTenantId();
	        requestInfo = waterRequest.getRequestInfo();
	        localizationMessages = getLocalizationMessages(tenantId, requestInfo);
	        messageMap = getCustomizedMsg(requestInfo, waterRequest.getWaterTankerBookingDetail(), localizationMessages);
	        message = messageMap.get(RequestServiceConstants.MESSAGE_TEXT);
	        mobileNumber = waterRequest.getWaterTankerBookingDetail().getApplicantDetail().getMobileNumber();
	    } else if (request instanceof MobileToiletBookingRequest) {
	        MobileToiletBookingRequest toiletRequest = (MobileToiletBookingRequest) request;
	        tenantId = toiletRequest.getMobileToiletBookingDetail().getTenantId();
	        requestInfo = toiletRequest.getRequestInfo();
	        localizationMessages = getLocalizationMessages(tenantId, requestInfo);
	        messageMap = getCustomizedMsg(requestInfo, toiletRequest.getMobileToiletBookingDetail(), localizationMessages);
	        message = messageMap.get(RequestServiceConstants.MESSAGE_TEXT);
	        mobileNumber = toiletRequest.getMobileToiletBookingDetail().getApplicantDetail().getMobileNumber();
	    } else {
	        throw new IllegalArgumentException("Unsupported request type: " + request.getClass().getSimpleName());
	    }

	    Map<String, String> mobileNumberToOwner = fetchUserUUIDs(mobileNumber, requestInfo, tenantId);
	    smsRequests.addAll(createSMSRequest(message, mobileNumberToOwner));
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
	public List<String> fetchChannelList(RequestInfo requestInfo, String tenantId, String moduleName, String action) {
		List<String> masterData = new ArrayList<>();
		StringBuilder uri = new StringBuilder();
		uri.append(config.getMdmsHost()).append(config.getMdmsPath());
		if (StringUtils.isEmpty(tenantId))
			return masterData;
		MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestForChannelList(requestInfo, tenantId, moduleName, action);
		//Can create filter as string using this
		Filter masterDataFilter = filter(where(RequestServiceConstants.MODULE).is(moduleName)
				.and(RequestServiceConstants.ACTION).is(action));

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
	 * Constructs an MDMS (Master Data Management System) criteria request for retrieving 
	 * the channel list based on the specified module and action.
	 *
	 * @param requestInfo The request information containing metadata about the request.
	 * @param tenantId    The tenant ID for which the channel list is to be retrieved.
	 * @param moduleName  The module name for filtering the data.
	 * @param action      The specific action used to filter the channel list.
	 * @return An {@link MdmsCriteriaReq} object containing the criteria for fetching 
	 *         the channel list from MDMS.
	 *
	 * This method:
	 * - Creates a {@link MasterDetail} object specifying the required master data.
	 * - Builds a {@link ModuleDetail} object containing the master details.
	 * - Constructs an {@link MdmsCriteria} with the provided tenant ID and module details.
	 * - Wraps the criteria in an {@link MdmsCriteriaReq} and returns it.
	 */
	
	private MdmsCriteriaReq getMdmsRequestForChannelList(RequestInfo requestInfo, String tenantId, String moduleName, String action) {

		MasterDetail masterDetail = new MasterDetail();
		masterDetail.setName(RequestServiceConstants.CHANNEL_LIST);
	//	masterDetail.setFilter("[?(@['module'] == 'CND' && @['action'] == '"+ action +"')]");
		List<MasterDetail> masterDetailList = new ArrayList<>();
		masterDetailList.add(masterDetail);

		ModuleDetail moduleDetail = new ModuleDetail();
		moduleDetail.setMasterDetails(masterDetailList);
		moduleDetail.setModuleName(RequestServiceConstants.CHANNEL);
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
	 * Fetches email ids of CITIZENs based on the phone number.
	 *
	 * @param mobileNumbers
	 * @param requestInfo
	 * @param tenantId
	 * @return
	 */

	public Map<String, String> fetchUserEmailIds(Set<String> mobileNumbers, RequestInfo requestInfo, String tenantId) {
		Map<String, String> mapOfPhnoAndEmailIds = new HashMap<>();
		StringBuilder uri = new StringBuilder();
		uri.append(config.getUserHost()).append(config.getUserSearchEndpoint());
		Map<String, Object> userSearchRequest = new HashMap<>();
		userSearchRequest.put("RequestInfo", requestInfo);
		userSearchRequest.put("tenantId", tenantId);
		userSearchRequest.put("userType", "CITIZEN");
		for (String mobileNo : mobileNumbers) {
			userSearchRequest.put("userName", mobileNo);
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
				log.error("Exception while fetching user for username - " + mobileNo);
				log.error("Exception trace: ", e);
				continue;
			}
		}
		return mapOfPhnoAndEmailIds;
	}

	/**
	 * Generates a customized message based on the action status of the mobile toilet booking request.
	 * This method retrieves localized message templates, formats them with booking details,
	 * and optionally includes a payment link.
	 *
	 * @param requestInfo       The request information containing user details.
	 * @param mobileToiletDetail The details of the mobile toilet booking.
	 * @param localizationMessage The localization message string for fetching message templates.
	 * @return A map containing the customized message and an optional action link.
	 */
	public Map<String, String> getCustomizedMsg(RequestInfo requestInfo, WaterTankerBookingDetail waterTankerDetail,
			String localizationMessage) {
		String message = null, messageTemplate;
		String link = null;
		String ACTION_STATUS = waterTankerDetail.getWorkflow().getAction();
		switch (ACTION_STATUS) {

		case ACTION_STATUS_APPLY:
			messageTemplate = getMessageTemplate(RequestServiceConstants.NOTIFICATION_APPLY, localizationMessage);
			message = getMessageWithNumberAndFinalDetails(waterTankerDetail, messageTemplate);
			break;
			
		case ACTION_STATUS_APPROVE:
			messageTemplate = getMessageTemplate(RequestServiceConstants.NOTIFICATION_APPROVED, localizationMessage);
			message = getMessageWithNumberAndFinalDetails(waterTankerDetail, messageTemplate);
		//	link = getPayUrl(waterTankerDetail, message);
			break;	
			
		case ACTION_STATUS_ASSIGN_VENDOR:
			messageTemplate = getMessageTemplate(RequestServiceConstants.NOTIFICATION_ASSIGN_VENDOR, localizationMessage);
			message = getMessageWithNumberAndFinalDetails(waterTankerDetail, messageTemplate);
			break;
		
		case ACTION_STATUS_ASSIGN_VEHICLE_DRIVER:
			messageTemplate = getMessageTemplate(RequestServiceConstants.NOTIFICATION_ASSIGN_VEHICLE_DRIVER, localizationMessage);
			message = getMessageWithNumberAndFinalDetails(waterTankerDetail, messageTemplate);
			break;
			
		case ACTION_STATUS_COMPLETE_REQUEST:
			messageTemplate = getMessageTemplate(RequestServiceConstants.NOTIFICATION_COMPLETE_REQUEST, localizationMessage);
			message = getMessageWithNumberAndFinalDetails(waterTankerDetail, messageTemplate);
			break;		
	
		case ACTION_STATUS_REJECT:
			messageTemplate = getMessageTemplate(RequestServiceConstants.NOTIFICATION_REJECT, localizationMessage);
			message = getMessageWithNumberAndFinalDetails(waterTankerDetail, messageTemplate);
			break;

		case ACTION_STATUS_PAY:
			messageTemplate = getMessageTemplate(RequestServiceConstants.NOTIFICATION_TANKERBOOKED,
					localizationMessage);
			message = getMessageWithNumberAndFinalDetails(waterTankerDetail, messageTemplate);
			break;
		}
		

		if (message.contains("{PAY_LINK}")) {
			 link = null;
			 message = getPayUrl(waterTankerDetail, message);
			 
		}
		
		Map<String, String> messageMap = new HashMap<String, String>();
		messageMap.put(ACTION_LINK, link);
		messageMap.put(MESSAGE_TEXT, message);
		
		log.info("getCustomizedMsg messageTemplate : " + message);
		return messageMap;

		//return message;
	}

	/**
	 * Generates a customized message based on the action status of the mobile toilet booking request.
	 * This method retrieves localized message templates, formats them with booking details,
	 * and optionally includes a payment link.
	 *
	 * @param requestInfo       The request information containing user details.
	 * @param mobileToiletDetail The details of the mobile toilet booking.
	 * @param localizationMessage The localization message string for fetching message templates.
	 * @return A map containing the customized message and an optional action link.
	 */
	public Map<String, String> getCustomizedMsg(RequestInfo requestInfo, MobileToiletBookingDetail mobileToiletDetail,
												String localizationMessage) {
		String message = null, messageTemplate;
		String link = null;
		String ACTION_STATUS = mobileToiletDetail.getWorkflow().getAction();
		switch (ACTION_STATUS) {

			case ACTION_STATUS_APPLY:
				messageTemplate = getMessageTemplate(RequestServiceConstants.MT_NOTIFICATION_APPLY, localizationMessage);
				message = getMessageWithNumberAndFinalDetails(mobileToiletDetail, messageTemplate);
				break;

			case ACTION_STATUS_APPROVE:
				messageTemplate = getMessageTemplate(RequestServiceConstants.MT_NOTIFICATION_APPROVED, localizationMessage);
				message = getMessageWithNumberAndFinalDetails(mobileToiletDetail, messageTemplate);
				//	link = getPayUrl(waterTankerDetail, message);
				break;

			case ACTION_STATUS_ASSIGN_VENDOR:
				messageTemplate = getMessageTemplate(RequestServiceConstants.MT_NOTIFICATION_ASSIGN_VENDOR, localizationMessage);
				message = getMessageWithNumberAndFinalDetails(mobileToiletDetail, messageTemplate);
				break;

			case ACTION_STATUS_ASSIGN_VEHICLE_DRIVER:
				messageTemplate = getMessageTemplate(RequestServiceConstants.MT_NOTIFICATION_ASSIGN_VEHICLE_DRIVER, localizationMessage);
				message = getMessageWithNumberAndFinalDetails(mobileToiletDetail, messageTemplate);
				break;

			case ACTION_STATUS_COMPLETE_REQUEST:
				messageTemplate = getMessageTemplate(RequestServiceConstants.MT_NOTIFICATION_COMPLETE_REQUEST, localizationMessage);
				message = getMessageWithNumberAndFinalDetails(mobileToiletDetail, messageTemplate);
				break;

			case ACTION_STATUS_REJECT:
				messageTemplate = getMessageTemplate(RequestServiceConstants.MT_NOTIFICATION_REJECT, localizationMessage);
				message = getMessageWithNumberAndFinalDetails(mobileToiletDetail, messageTemplate);
				break;

			case ACTION_STATUS_PAY:
				messageTemplate = getMessageTemplate(RequestServiceConstants.MT_NOTIFICATION_TOILETBOOKED,
						localizationMessage);
				message = getMessageWithNumberAndFinalDetails(mobileToiletDetail, messageTemplate);
				break;
		}


		if (message.contains("{PAY_LINK}")) {
			link = null;
			message = getPayUrl(mobileToiletDetail, message);
		}

		Map<String, String> messageMap = new HashMap<String, String>();
		messageMap.put(ACTION_LINK, link);
		messageMap.put(MESSAGE_TEXT, message);
		log.info("getCustomizedMsg messageTemplate : " + message);
		return messageMap;

		//return message;
	}
	
	private String getMessageWithNumberAndFinalDetails(WaterTankerBookingDetail waterTankerDetail, String message) {
	    return String.format(message, waterTankerDetail.getApplicantDetail().getName(), waterTankerDetail.getBookingNo());
	}

	private String getMessageWithNumberAndFinalDetails(MobileToiletBookingDetail mobileToiletDetail, String message) {
		return String.format(message, mobileToiletDetail.getApplicantDetail().getName(), mobileToiletDetail.getBookingNo());
	}

	/**
	 * Prepares and return url for view screen
	 *
	 * @param mobileToiletDetail
	 * @return
	 */
	public String getPayUrl(MobileToiletBookingDetail mobileToiletDetail, String message) {
		String actionLink = String.format("%s?mobile=%s&applicationNo=%s&tenantId=%s&businessService=%s",
				config.getPayLink(),
				mobileToiletDetail.getApplicantDetail().getMobileNumber(),
				mobileToiletDetail.getBookingNo(),
				mobileToiletDetail.getTenantId(),
				config.getBusinessServiceName());

		message = message.replace("{PAY_LINK}", getShortenedUrl(config.getUiAppHost() + actionLink));

		return message;
	}

	  /**
     * Prepares and return url for view screen
     *
     * @param waterTankerDetail
     * @return
     */
	public String getPayUrl(WaterTankerBookingDetail waterTankerDetail, String message) {
	    String actionLink = String.format("%s?mobile=%s&applicationNo=%s&tenantId=%s&businessService=%s",
	            config.getPayLink(),
	            waterTankerDetail.getApplicantDetail().getMobileNumber(),
	            waterTankerDetail.getBookingNo(),
	            waterTankerDetail.getTenantId(),
	            config.getBusinessServiceName());

	    message = message.replace("{PAY_LINK}", getShortenedUrl(config.getUiAppHost() + actionLink));

	    return message;
	}

	/**
	 * Method to shortent the url returns the same url if shortening fails
	 * 
	 * @param url
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
		} else {
			return res;
		}
	}

}
