package org.upyog.adv.util;

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
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.upyog.adv.config.BookingConfiguration;
import org.upyog.adv.constants.BookingConstants;
import org.upyog.adv.enums.BookingStatusEnum;
import org.upyog.adv.kafka.Producer;
import org.upyog.adv.repository.ServiceRequestRepository;
import org.upyog.adv.web.models.BookingDetail;
import org.upyog.adv.web.models.BookingRequest;
import org.upyog.adv.web.models.events.Action;
import org.upyog.adv.web.models.events.ActionItem;
import org.upyog.adv.web.models.events.EventRequest;
import org.upyog.adv.web.models.notification.Email;
import org.upyog.adv.web.models.notification.EmailRequest;

import com.jayway.jsonpath.JsonPath;

import digit.models.coremodels.SMSRequest;
import lombok.extern.slf4j.Slf4j;
/**
 * Utility class for interacting with the MDMS (Master Data Management System) in the Advertisement Booking Service.
 * 
 * Key Responsibilities:
 * - Fetches master data from MDMS for various configurations such as tax heads, calculation types, and other metadata.
 * - Constructs and sends requests to the MDMS service.
 * - Processes responses from MDMS to extract required data.
 * 
 * Methods:
 * - Builds MDMS request criteria for fetching specific master data.
 * - Parses and maps MDMS responses to Java objects for further processing.
 * 
 * Dependencies:
 * - BookingConfiguration: Provides configuration properties for MDMS interactions.
 * - ServiceRequestRepository: Handles HTTP requests to the MDMS service.
 * - ObjectMapper: Used for JSON serialization and deserialization.
 * 
 * Annotations:
 * - @Component: Marks this class as a Spring-managed component.
 * - @Slf4j: Enables logging for debugging and monitoring MDMS interactions.
 */
@Slf4j
@Component
public class NotificationUtil {

	private ServiceRequestRepository serviceRequestRepository;

	private BookingConfiguration config;

	private Producer producer;

	private final String URL = "url";
	private static final String PAYMENT_LINK = "PAYMENT_LINK";

	private static final String PERMISSION_LETTER_LINK = "PERMISSION_LETTER_LINK";
	private static final String VIEW_APPLICATION_LINK = "VIEW_APPLICATION_LINK";
	public static final String MESSAGE_TEXT = "MESSAGE_TEXT";
	public static final String ACTION_LINK = "ACTION_LINK";

	@Autowired
	public NotificationUtil(ServiceRequestRepository serviceRequestRepository, BookingConfiguration config,
			Producer producer) {
		this.serviceRequestRepository = serviceRequestRepository;
		this.config = config;
		this.producer = producer;
	}

	/**
	 * Extracts message for the specific code
	 *
	 * @param notificationCode    The code for which message is required
	 * @param localizationMessage The localization messages
	 * @return message for the specific code
	 */
	@SuppressWarnings({ "unchecked" })
	private String getMessageTemplate(String notificationCode, String localizationMessage) {

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
	 * @param tenantId    tenantId of the BPA
	 * @param requestInfo The requestInfo of the request
	 * @return Localization messages for the module
	 */
	@SuppressWarnings("rawtypes")
	public String getLocalizationMessages(String tenantId, RequestInfo requestInfo) {

		LinkedHashMap responseMap = (LinkedHashMap) serviceRequestRepository.fetchResult(getUri(tenantId, requestInfo),
				requestInfo);
		String jsonString = new JSONObject(responseMap).toString();
		return jsonString;
	}

	/**
	 * Returns the uri for the localization call
	 * 
	 * @param tenantId TenantId of the propertyRequest
	 * @return The uri for localization search call
	 */
	private StringBuilder getUri(String tenantId, RequestInfo requestInfo) {

		if (config.getIsLocalizationStateLevel())
			tenantId = tenantId.split("\\.")[0];

		String locale = "en_IN";
		if (!StringUtils.isEmpty(requestInfo.getMsgId()) && requestInfo.getMsgId().split("|").length >= 2)
			locale = requestInfo.getMsgId().split("\\|")[1];

		StringBuilder uri = new StringBuilder();
		uri.append(config.getLocalizationHost()).append(config.getLocalizationContextPath())
				.append(config.getLocalizationSearchEndpoint()).append("?").append("locale=").append(locale)
				.append("&tenantId=").append(tenantId).append("&module=")
				.append(BookingConstants.NOTIFICATION_MODULE_NAME);
		return uri;
	}

	/**
	 * Creates sms request for the each owners
	 * 
	 * @param mobileNumberToOwnerName Map of mobileNumber to OwnerName
	 * @return List of SMSRequest
	 */
	public List<SMSRequest> createSMSRequest(BookingRequest bookingRequest, String message,
			Map<String, String> mobileNumberToOwnerName) {

		List<SMSRequest> smsRequest = new LinkedList<>();
		// message = "Dear Citizen, Your OTP to complete your UPYOG Registration is
		// 12345.\\n\\nUPYOG" + "#1207167462318135756#1207167462307097438" ;
		for (Map.Entry<String, String> entryset : mobileNumberToOwnerName.entrySet()) {
			smsRequest.add(new SMSRequest(entryset.getKey(), message));
		}
		return smsRequest;
	}

	/**
	 * Send the SMSRequest on the SMSNotification kafka topic
	 *
	 * @param smsRequestList The list of SMSRequest to be sent
	 */
	public void sendSMS(List<SMSRequest> smsRequestList) {
		if (CollectionUtils.isEmpty(smsRequestList))
			log.info("Messages fobject is empty in send sms!");
		for (SMSRequest smsRequest : smsRequestList) {
			producer.push(config.getSmsNotifTopic(), smsRequest);
			log.debug("SMS request object : " + smsRequest);
			log.info("Sending SMS notification to MobileNumber: " + smsRequest.getMobileNumber() + " Messages: "
					+ smsRequest.getMessage());
		}
	}

	/**
	 * Pushes the event request to Kafka Queue.
	 *
	 * @param request
	 */
	public void sendEventNotification(EventRequest request) {
		log.info("EVENT notification sent!");
		producer.push(config.getSaveUserEventsTopic(), request);
	}

	/**
	 * 
	 * Get message from {@code localizationMessage} for particular event Replace
	 * dynamic values of messages
	 * 
	 * @param bookingDetail
	 * @param localizationMessage
	 * @param actionStatus
	 * @return
	 */
	public Map<String, String> getCustomizedMsg(BookingDetail bookingDetail, String localizationMessage,
			String actionStatus, String eventType) {
		String messageTemplate = null, link = null;
		String notificationEventType = actionStatus + "_" + eventType;
		log.info(" booking status : " + bookingDetail.getBookingStatus());
		log.info(" booking status ACTION_STATUS : " + actionStatus);
		log.info("notificationEventType  : " + notificationEventType);

		BookingStatusEnum notificationType = BookingStatusEnum.valueOf(actionStatus);

		switch (notificationType) {

		case BOOKING_CREATED:
			messageTemplate = getMessageTemplate(notificationEventType, localizationMessage);
		//	link = getActionLink(bookingDetail, actionStatus);
			break;

		case BOOKED:
			messageTemplate = getMessageTemplate(notificationEventType, localizationMessage);
		//	link = getActionLink(bookingDetail, actionStatus);
			break;

		case CANCELLED:
			messageTemplate = getMessageTemplate(notificationEventType, localizationMessage);
			break;

//		case PAYMENT_FAILED:
//			messageTemplate = getMessageTemplate(notificationEventType, localizationMessage);
//			link = getActionLink(bookingDetail, actionStatus);
//			break;

		default:
			messageTemplate = "Localization message not available for  status : " + actionStatus;
			break;

		}
		
		if (messageTemplate.contains(BookingConstants.NOTIFICATION_PAY_NOW)) {
			   
		    link = getPayUrl(bookingDetail, messageTemplate);
		}		
		
		if (messageTemplate.contains(BookingConstants.NOTIFICATION_DOWNLOAD_RECEIPT)) {
			   
		    link = getReceiptDownloadLink(bookingDetail);
		}

		Map<String, String> messageMap = new HashMap<String, String>();
		messageMap.put(ACTION_LINK, link);
		messageMap.put(MESSAGE_TEXT, messageTemplate);

		log.info("getCustomizedMsg messageTemplate : " + messageTemplate);
		return messageMap;
	}

	public String getActionLink(BookingDetail bookingDetail, String action) {
		String link = null;
		if (BookingConstants.BOOKING_CREATED_STATUS.equals(action)) {
			// Payment Link
			link = config.getUiAppHost() + config.getPayLinkSMS().replace("$consumerCode", bookingDetail.getBookingNo())
					.replace("$mobile", bookingDetail.getApplicantDetail().getApplicantMobileNo())
					.replace("$tenantId", bookingDetail.getTenantId())
					.replace("$businessService", config.getBusinessServiceName());
		} else if (BookingConstants.BOOKED_STATUS.equals(action)) {
			// Permission letter link
			link = config.getUiAppHost()
					+ config.getPermissionLetterLink().replace("$consumerCode", bookingDetail.getBookingNo())
							.replace("$mobile", bookingDetail.getApplicantDetail().getApplicantMobileNo())
							.replace("$tenantId", bookingDetail.getTenantId())
							.replace("$businessService", config.getBusinessServiceName());
		}
		if (null != link) {
			link = getShortnerURL(link);
		}
		return link;
	}

	private String getShortnerURL(String actualURL) {
		net.minidev.json.JSONObject obj = new net.minidev.json.JSONObject();
		obj.put(URL, actualURL);
		String url = config.getUrlShortnerHost() + config.getShortenerEndpoint();

		Object response = serviceRequestRepository.getShorteningURL(new StringBuilder(url), obj);
		return response.toString();
	}
	
	
	/**
	 * Generates a shortened payment URL for the citizen to make payment based on the application details.
	 * <p>
	 * The final URL is built using a URL template from configuration (`payLinkTemplate`) and fills in dynamic values 
	 * like business service name, application number, tenant ID, and mobile number. The constructed URL is then 
	 * prefixed with the UI host and passed through a shortening service.
	 *
	 * @param cndApplicationDetail The application detail object containing applicant and application metadata.
	 * @param message              The notification message (not used in this method, but kept for signature consistency).
     * We are redirecting the pay now link to myBookings page in ADV because in the normal flow we need timer value, 
	 * and we get that timer value by hitting the make payment button
	 * if timer value is not given then the proceed to pay button will be disabled
	 * @return A shortened payment URL pointing to the citizen's "Pay Now" page.
	 */
	public String getPayUrl(BookingDetail bookingDetail, String message) {
	    String payLinkTemplate = config.getPayNowLink();
	   /* String actionLink = String.format(payLinkTemplate,
	            config.getBusinessServiceName(),
	            bookingDetail.getBookingNo()
                cndApplicationDetail.getTenantId()
	            ); */
	    String finalUrl = config.getUiAppHost() + payLinkTemplate;
	    
	    log.info("Final url For pay link : " + finalUrl);

	    return getShortnerURL(finalUrl);
	}
	
	
	/**
	 * Generates a downloadable receipt link for the given {@link BookingDetail}.
	 * <p>
	 * This method constructs the download URL using the configured request link template
	 * and the application number and tenant ID from the provided {@code CNDApplicationDetail} object.
	 * The full URL is then shortened before returning.
	 *
	 * @param cndApplicationDetail the application detail containing the application number and tenant ID
	 * @return a shortened URL string for downloading the receipt
	 */
	
     public String getReceiptDownloadLink(BookingDetail bookingDetail) {
		
		String downloadReceiptLinkTemplate = config.getDownloadReceiptLink();
	    String actionLink = String.format(downloadReceiptLinkTemplate,
	    		bookingDetail.getBookingNo(),
	    		bookingDetail.getTenantId()
	            );
	    
	    String finalUrl = config.getUiAppHost() + actionLink;
	    
	    log.info("Final url to download receipt : " + finalUrl);

	    return getShortnerURL(finalUrl);

	}
     
     /**
      * Fetches UUIDs (unique user identifiers) for a set of mobile numbers.
      * <p>
      * This method sends a search request for each mobile number to the user service endpoint
      * and retrieves the corresponding UUIDs from the JSON response using JsonPath. The results are
      * stored in a map where the key is the mobile number and the value is the UUID.
      * </p>
      *
      * <p>
      * If a user is not found or an error occurs during the fetch, appropriate error logs are generated.
      * </p>
      *
      * @param mobileNumbers the set of mobile numbers to search for
      * @param requestInfo   the {@link RequestInfo} object containing metadata for the request
      * @param tenantId      the tenant ID under which the search is performed
      * @return a map of mobile numbers to corresponding user UUIDs
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
 	 * Fetches the email IDs of users based on their mobile numbers.
 	 * <p>
 	 * For each mobile number in the input set, this method constructs a user search request and
 	 * queries the user service to retrieve the corresponding email ID. The email IDs are extracted
 	 * from the JSON response using JsonPath and mapped to their respective mobile numbers.
 	 * </p>
 	 *
 	 * <p>
 	 * If an email ID is not found or an exception occurs during the fetch, appropriate logs are generated.
 	 * The method returns a map where keys are mobile numbers and values are the associated email IDs.
 	 * </p>
 	 *
 	 * @param mobileNumbers the set of mobile numbers for which email IDs need to be fetched
 	 * @param requestInfo   the {@link RequestInfo} object containing metadata for the service request
 	 * @param tenantId      the tenant ID used for scoping the user search
 	 * @return a map of mobile numbers to their corresponding email IDs
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
	 * Creates a list of {@link EmailRequest} objects using the provided message template and a map
	 * of mobile numbers to email IDs.
	 * <p>
	 * For each entry in the map, the placeholder {@code BookingConstants.NOTIFICATION_EMAIL} in the message
	 * is replaced with the corresponding email ID. Each customized message is then used to create an
	 * {@link Email} object, which is wrapped in an {@link EmailRequest} along with the given {@link RequestInfo}.
	 * </p>
	 *
	 * @param requestInfo          the request information containing metadata about the request
	 * @param message              the email message template that may contain placeholders
	 * @param mobileNumberToEmailId a map where keys are mobile numbers and values are corresponding email IDs
	 * @return a list of {@link EmailRequest} objects ready to be sent
	 */
	public List<EmailRequest> createEmailRequest(RequestInfo requestInfo, String message,
			Map<String, String> mobileNumberToEmailId) {

		List<EmailRequest> emailRequest = new LinkedList<>();
		for (Map.Entry<String, String> entryset : mobileNumberToEmailId.entrySet()) {
			String customizedMsg = "";
			if (message.contains(BookingConstants.NOTIFICATION_EMAIL))
				customizedMsg = message.replace(BookingConstants.NOTIFICATION_EMAIL, entryset.getValue());

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
	 * Sends email notifications based on the provided list of {@link EmailRequest}.
	 * <p>
	 * This method checks whether email notifications are enabled through the configuration.
	 * If enabled and the list of email requests is not empty, it iterates through each request,
	 * and pushes it to the configured Kafka topic for further processing, provided the email body is not empty.
	 * </p>
	 *
	 * <p>
	 * If the list is empty or the email body is empty, appropriate logs are generated and the email is not sent.
	 * </p>
	 *
	 * @param emailRequestList the list of {@link EmailRequest} objects to be processed for email notifications
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
	 * Generates an {@link Action} object based on specific placeholders found in the input message.
	 * <p>
	 * This method checks if the provided message contains the {@code NOTIFICATION_ACTION} and 
	 * {@code NOTIFICATION_ACTION_BUTTON} placeholders. If present, it extracts the action code 
	 * between these placeholders and validates it against known constants like 
	 * {@code NOTIFICATION_PAY_NOW} or {@code NOTIFICATION_DOWNLOAD_RECEIPT}.
	 * If the code is valid, it constructs an {@link ActionItem} with the provided action URL and tenant ID,
	 * wraps it in an {@link Action} object, and returns it.
	 *
	 * @param message    The notification message which may contain action placeholders.
	 * @param actionLink The URL to be used for the action (e.g., pay now or download receipt).
	 * @param tenantId   The tenant ID to associate with the generated action.
	 * @return An {@link Action} object if the message contains a recognized action code, otherwise {@code null}.
	 * EX- Hi Nehatest your advertisement booking under booking number: ADV-1508-000470 has been completed successfully. Please download receipt using link {Action Button}Download Receipt{/Action Button}
	 */
	
	public Action getActionLinkAndCode(String message, String actionLink, String tenantId) {
	   
	        String code = StringUtils.substringBetween(
	                message, 
	                BookingConstants.NOTIFICATION_ACTION, 
	                BookingConstants.NOTIFICATION_ACTION_BUTTON
	        );

	        if (BookingConstants.NOTIFICATION_PAY_NOW.equalsIgnoreCase(code) || 
	            BookingConstants.NOTIFICATION_DOWNLOAD_RECEIPT.equalsIgnoreCase(code)) {

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
