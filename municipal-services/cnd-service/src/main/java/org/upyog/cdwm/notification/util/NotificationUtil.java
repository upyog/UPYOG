package org.upyog.cdwm.notification.util;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.upyog.cdwm.config.CNDConfiguration;
import org.upyog.cdwm.kafka.Producer;
import org.upyog.cdwm.notification.constants.NotificationConstants;
import org.upyog.cdwm.notification.impl.MessageServiceImpl;
import org.upyog.cdwm.repository.ServiceRequestRepository;
import org.upyog.cdwm.web.models.CNDApplicationDetail;
import org.upyog.cdwm.web.models.CNDApplicationRequest;
import org.upyog.cdwm.web.models.events.Action;
import org.upyog.cdwm.web.models.events.ActionItem;
import org.upyog.cdwm.web.models.events.Event;
import org.upyog.cdwm.web.models.events.EventRequest;
import org.upyog.cdwm.web.models.events.Recepient;
import org.upyog.cdwm.web.models.events.Source;
import org.upyog.cdwm.web.models.notification.Email;
import org.upyog.cdwm.web.models.notification.EmailRequest;
import org.upyog.cdwm.web.models.notification.SMSRequest;

import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NotificationUtil {

	private ServiceRequestRepository serviceRequestRepository;

	private CNDConfiguration config;
	
	@Autowired
	private MessageServiceImpl messageService;

	private Producer producer;

	private RestTemplate restTemplate;

	@Autowired
	public NotificationUtil(ServiceRequestRepository serviceRequestRepository, CNDConfiguration config,
			Producer producer, RestTemplate restTemplate) {
		this.serviceRequestRepository = serviceRequestRepository;
		this.config = config;
		this.producer = producer;
		this.restTemplate = restTemplate;
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
				.append(NotificationConstants.NOTIFICATION_MODULE_NAME);

		return uri;
	}
	
	/**
	 * Creates an event request for the CND application notification system.
	 *
	 * @param request    The CND application request containing details such as
	 *                   tenant ID and applicant information.
	 * @param actionLink The URL link to be included in the notification for user
	 *                   action.
	 * @param messageMap A map containing the notification message text.
	 * @return An {@link EventRequest} containing the event details, or {@code null}
	 *         if no events are created.
	 *
	 *         The method performs the following actions: - Retrieves the tenant ID
	 *         and applicant's mobile number from the request. - Fetches user UUIDs
	 *         using the mobile number. - Constructs a recipient list based on the
	 *         retrieved UUID. - Builds an {@link Event} object with the provided
	 *         message and action link. - Wraps the event in an {@link EventRequest}
	 *         and returns it.
	 *
	 *         Logs relevant information such as UUID search results, message
	 *         content, and recipient details.
	 */

	public EventRequest getEventsForCND(CNDApplicationRequest request, String actionLink,
			Map<String, String> messageMap) {

		List<Event> events = new ArrayList<>();
		String tenantId = request.getCndApplication().getTenantId();
		List<String> toUsers = new ArrayList<>();
		String mobileNumber = request.getCndApplication().getApplicantDetail().getMobileNumber();

		Map<String, String> mapOfPhoneNoAndUUIDs = fetchUserUUIDs(mobileNumber, request.getRequestInfo(), tenantId);

		if (CollectionUtils.isEmpty(mapOfPhoneNoAndUUIDs.keySet())) {
			log.info("UUID search failed!");
		}
		toUsers.add(mapOfPhoneNoAndUUIDs.get(mobileNumber));

		String message = messageMap.get(NotificationConstants.MESSAGE_TEXT);
		log.info("Message for event in CND: " + message);

		Recepient recepient = Recepient.builder().toUsers(toUsers).toRoles(null).build();
		log.info("Recipient object in CND: " + recepient);

		Action action = null;

		if (message.contains(NotificationConstants.NOTIFICATION_ACTION)) {
			
			action = getActionLinkAndCode(message, actionLink, tenantId);
			
			String code = StringUtils.substringBetween(message, NotificationConstants.NOTIFICATION_ACTION, NotificationConstants.NOTIFICATION_ACTION_BUTTON);
			message = message.replace(NotificationConstants.NOTIFICATION_ACTION, "").replace(NotificationConstants.NOTIFICATION_ACTION_BUTTON, "").replace(code, "");
			
		}
	
		Event event = Event.builder().tenantId(tenantId).description(message)
				.eventType(NotificationConstants.USREVENTS_EVENT_TYPE).name(NotificationConstants.USREVENTS_EVENT_NAME)
				.postedBy(NotificationConstants.USREVENTS_EVENT_POSTEDBY).source(Source.WEBAPP).recepient(recepient)
				.actions(action)
				.eventDetails(null).build();

		events.add(event);

		return EventRequest.builder().requestInfo(request.getRequestInfo()).events(events).build();
	}

	
	/**
	 * Enriches the smsRequest with the customized messages
	 *
	 * @param request
	 *            The request from kafka topic
	 * @param smsRequests
	 *            List of SMSRequets
	 */
	public void enrichSMSRequest(CNDApplicationRequest request, List<SMSRequest> smsRequests) {
		String tenantId = request.getCndApplication().getTenantId();
		String localizationMessages = messageService.getLocalizationMessages(tenantId, request.getRequestInfo());
	    Map<String, String> messageMap = messageService.getCustomizedMsg(request.getRequestInfo(), request.getCndApplication(),
				localizationMessages);	
		String message = messageMap.get(NotificationConstants.MESSAGE_TEXT);
		String mobileNumber = request.getCndApplication().getApplicantDetail().getMobileNumber();
		Map<String, String> mobileNumberToOwner = fetchUserUUIDs(mobileNumber, request.getRequestInfo(), tenantId);
		smsRequests.addAll(createSMSRequest(message, mobileNumberToOwner));

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
			String customizedMsg = message.replace(NotificationConstants.NOTIFICATION_OWNERNAME, entryset.getValue());
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
		uri.append(config.getUserHost()).append(config.getUserV2SearchEndpoint());
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
		uri.append(config.getUserHost()).append(config.getUserV2SearchEndpoint());
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
			if (message.contains(NotificationConstants.NOTIFICATION_EMAIL))
				customizedMsg = message.replace(NotificationConstants.NOTIFICATION_EMAIL, entryset.getValue());

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
		uri.append(config.getUserHost()).append(config.getUserV2SearchEndpoint());
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
		Filter masterDataFilter = filter(where(NotificationConstants.MODULE).is(moduleName)
				.and(NotificationConstants.ACTION).is(action));

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
		masterDetail.setName(NotificationConstants.CHANNEL_LIST);
	//	masterDetail.setFilter("[?(@['module'] == 'CND' && @['action'] == '"+ action +"')]");
		List<MasterDetail> masterDetailList = new ArrayList<>();
		masterDetailList.add(masterDetail);

		ModuleDetail moduleDetail = new ModuleDetail();
		moduleDetail.setMasterDetails(masterDetailList);
		moduleDetail.setModuleName(NotificationConstants.CHANNEL);
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
	 * Shortens a given URL using the configured URL shortening service.
	 * <p>
	 * This method sends a POST request with the original URL to the shortening service
	 * and returns the shortened URL. If the shortening service fails or returns an empty response,
	 * the original URL is returned as a fallback.
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
		} else {
			return res;
		}
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
	 * @return A shortened payment URL pointing to the citizen's "Pay Now" page.
	 */
	public String getPayUrl(CNDApplicationDetail cndApplicationDetail, String message) {
	    String payLinkTemplate = config.getPayNowLink();
	    String actionLink = String.format(payLinkTemplate,
	            config.getModuleName(),
	            cndApplicationDetail.getApplicationNumber()
//	            cndApplicationDetail.getTenantId()
	            );
	    String finalUrl = config.getUiAppHost() + actionLink;
	    
	    log.info("Final url for Payment link : " + finalUrl);

	    return getShortenedUrl(finalUrl);
	}
	
	
	/**
	 * Generates a downloadable receipt link for the given {@link CNDApplicationDetail}.
	 * <p>
	 * This method constructs the download URL using the configured request link template
	 * and the application number and tenant ID from the provided {@code CNDApplicationDetail} object.
	 * The full URL is then shortened before returning.
	 *
	 * @param cndApplicationDetail the application detail containing the application number and tenant ID
	 * @return a shortened URL string for downloading the receipt
	 */
	
		public String getReceiptDownloadLink(CNDApplicationDetail cndApplicationDetail) {
				
				String downloadReceiptLinkTemplate = config.getDownloadReceiptLink();
			    String actionLink = String.format(downloadReceiptLinkTemplate,
			    		cndApplicationDetail.getApplicationNumber(),
			    		cndApplicationDetail.getTenantId()
			            );
			    
			    String finalUrl = config.getUiAppHost() + actionLink;
			    
			    log.info("Final url to download receipt : " + finalUrl);
		
			    return getShortenedUrl(finalUrl);
		
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
		 * ex-  Dear Shivank, You have successfully completed your cnd registration under application number: CND-1013-000163. Your certificate id: 234567 and can be downloaded from your account. Thank you. {Action Button}Download Receipt{/Action Button}

		 */
		
		public Action getActionLinkAndCode(String message, String actionLink, String tenantId) {
		   
		        String code = StringUtils.substringBetween(
		                message, 
		                NotificationConstants.NOTIFICATION_ACTION, 
		                NotificationConstants.NOTIFICATION_ACTION_BUTTON
		        );
		
		        if (NotificationConstants.NOTIFICATION_PAY_NOW.equalsIgnoreCase(code) || 
		        		NotificationConstants.NOTIFICATION_DOWNLOAD_RECEIPT.equalsIgnoreCase(code)) {
		
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
