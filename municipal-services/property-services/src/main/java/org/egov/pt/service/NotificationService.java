package org.egov.pt.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.models.collection.Bill;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.models.Property;
import org.egov.pt.models.PtTaxCalculatorTracker;
import org.egov.pt.models.SMSSentRequest;
import org.egov.pt.models.enums.CreationReason;
import org.egov.pt.models.enums.SMSCategory;
import org.egov.pt.models.enums.Status;
import org.egov.pt.models.event.Event;
import org.egov.pt.models.event.EventRequest;
import org.egov.pt.models.workflow.Action;
import org.egov.pt.models.workflow.ProcessInstance;
import org.egov.pt.util.NotificationUtil;
import org.egov.pt.util.PTConstants;
import org.egov.pt.web.contracts.EmailRequest;
import org.egov.pt.web.contracts.Email;
import org.egov.pt.web.contracts.PropertyRequest;
import org.egov.pt.web.contracts.SMSRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.egov.pt.util.PTConstants.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;
import com.jayway.jsonpath.JsonPath;

@Slf4j
@Service
public class NotificationService {
	
	private static final String SMS_TEMPLATE_BILL_NOTIFICATION = "BILL-NOTIFICATION";
	private static final String PROPERTY_BILL_EMAIL_TEMPLATE_LOCATION = "templates/PropertyBillEmailTemplate.html";
	private static final String PROPERTY_PLACEHOLDER = "property";
	private static final String MONTH_PLACEHOLDER = "{month}";
	private static final String YEAR_PLACEHOLDER = "{year}";
	private static final String PROPERTY_ID_PLACEHOLDER = "{property_id}";
	private static final String LINK_PLACEHOLDER = "{link}";
	private static final String AMOUNT_PLACEHOLDER = "{amount}";
	private static final String BILL_NO_PLACEHOLDER = "{bill_no}";
	private static final String RECIPINTS_NAME_PLACEHOLDER = "{recipients_name}";
	private static final String PROPERTY_PAY_NOW_BILL_URL_PLACEHOLDER = "{property_pay_now_bill_url}";

	private static final String SMS_BODY_GENERATE_BILL = "Dear "+RECIPINTS_NAME_PLACEHOLDER+", your "+PROPERTY_PLACEHOLDER+" bill vide  "+PROPERTY_PLACEHOLDER+" id "
			+ PROPERTY_ID_PLACEHOLDER+" for the period "+MONTH_PLACEHOLDER+" amounting to Rs "+AMOUNT_PLACEHOLDER+" has been generated on CitizenSeva portal. "
			+ "Please pay on CitizenSeva Portal or using link "+LINK_PLACEHOLDER+". CitizenSeva H.P.";
	
	private static final String EMAIL_SUBJECT_GENERATE_BILL = "Your Property Collection Bill for " + MONTH_PLACEHOLDER
			+ "/" + " with " + PROPERTY_ID_PLACEHOLDER;
	
	@Autowired
	private NotificationUtil notifUtil;

	@Autowired
	private PropertyConfiguration configs;

	@Autowired
	private RestTemplate restTemplate;

	@Value("${notification.url}")
	private String notificationURL;
	
	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

	@Value("${kafka.topics.email.service.topic.name}")
	private String emailTopic;

	@Value("${kafka.topics.notification.sms}")
	private String smsTopic;

	@Value("${frontend.base.uri}")
	private String frontEndUri;
	
	public void sendNotificationForMutation(PropertyRequest propertyRequest) {

		String msg = null;
		String state = null;
		Property property = propertyRequest.getProperty();
		ProcessInstance wf = property.getWorkflow();
		String completeMsgs = notifUtil.getLocalizationMessages(property.getTenantId(), propertyRequest.getRequestInfo());
		state = getStateFromWf(wf, configs.getIsMutationWorkflowEnabled());
		String localisedState = getLocalisedState(wf, completeMsgs);

		switch (state) {

		case WF_NO_WORKFLOW:
			msg = getMsgForMutation(property, completeMsgs, MT_NO_WORKFLOW, NOTIFICATION_MUTATION_LINK);
			break;
			
		case WF_STATUS_OPEN:
			msg = getMsgForMutation(property, completeMsgs, WF_MT_STATUS_OPEN_CODE, NOTIFICATION_MUTATION_LINK);
			break;

		case WF_STATUS_APPROVED:
			msg = getMsgForMutation(property, completeMsgs, WF_MT_STATUS_APPROVED_CODE, NOTIFICATION_MUTATION_LINK);
			sendNotificationForCitizenFeedback(property,completeMsgs,MUTATED_STRING);
			break;

		case WF_STATUS_PAYMENT_PENDING:
			msg = getMsgForMutation(property, completeMsgs, WF_MT_STATUS_PAYMENT_PENDING_CODE, NOTIFICATION_PAY_LINK);
			break;

		default:
			msg = getMsgForMutation(property, completeMsgs, WF_MT_STATUS_CHANGE_CODE, NOTIFICATION_MUTATION_LINK);
			
			break;
			
		case WF_STATUS_PAID:
			break;
		}

		// Ignoring paid status, since it's wired from payment consumer directly
		if (!StringUtils.isEmpty(msg)) {
			msg = replaceCommonValues(property, msg, localisedState);
			prepareMsgAndSend(propertyRequest, msg, state);
		}
	}

	public void sendNotificationForMtPayment(PropertyRequest propertyRequest, BigDecimal Amount) {

		Property property = propertyRequest.getProperty();
		String CompleteMsgs = notifUtil.getLocalizationMessages(property.getTenantId(), propertyRequest.getRequestInfo());
		
			String msg = getMsgForMutation(property, CompleteMsgs, WF_MT_STATUS_PAID_CODE, NOTIFICATION_MUTATION_LINK)
						.replace(NOTIFICATION_AMOUNT, Amount.toPlainString());
			msg = replaceCommonValues(property, msg, "");		
			prepareMsgAndSend(propertyRequest, msg,"");
	}
	
	public void sendNotificationForUpdate(PropertyRequest propertyRequest) {

		Property property = propertyRequest.getProperty();
		ProcessInstance wf = property.getWorkflow();
		String createOrUpdate = null;
		String msg = null;
		
		Boolean isCreate =  CreationReason.CREATE.equals(property.getCreationReason());
		String state = getStateFromWf(wf, configs.getIsWorkflowEnabled());
		String completeMsgs = notifUtil.getLocalizationMessages(property.getTenantId(), propertyRequest.getRequestInfo());
		String localisedState = getLocalisedState(wf, completeMsgs);
		switch (state) {

		case WF_NO_WORKFLOW:
			createOrUpdate = isCreate ? CREATED_STRING : UPDATED_STRING;
			msg = getMsgForUpdate(property, UPDATE_NO_WORKFLOW, completeMsgs, createOrUpdate);
			break;

		case WF_STATUS_OPEN:
			createOrUpdate = isCreate ? CREATE_STRING : UPDATE_STRING;
			msg = getMsgForUpdate(property, WF_UPDATE_STATUS_OPEN_CODE, completeMsgs, createOrUpdate);
			break;

		case WF_STATUS_APPROVED:
			createOrUpdate = isCreate ? CREATED_STRING : UPDATED_STRING;
			msg = getMsgForUpdate(property, WF_UPDATE_STATUS_APPROVED_CODE, completeMsgs, createOrUpdate);
			sendNotificationForCitizenFeedback(property,completeMsgs,createOrUpdate);
			break;

		default:
			createOrUpdate = isCreate ? CREATE_STRING : UPDATE_STRING;
			msg = getMsgForUpdate(property, WF_UPDATE_STATUS_CHANGE_CODE, completeMsgs, createOrUpdate);
			break;
		}

		msg = replaceCommonValues(property, msg, localisedState);
		prepareMsgAndSend(propertyRequest, msg,state);
	}


	/**
	 * Method to prepare msg for create/update process
	 * 
	 * @param property
	 * @param msgCode
	 * @param completeMsgs
	 * @param createUpdateReplaceString
	 * @return
	 */
	private String getMsgForUpdate(Property property, String msgCode, String completeMsgs, String createUpdateReplaceString) {

		String url = notifUtil.getShortenedUrl(
					   configs.getUiAppHost().concat(configs.getViewPropertyLink()
					  .replace(NOTIFICATION_PROPERTYID, property.getPropertyId())
					  .replace(NOTIFICATION_TENANTID, property.getTenantId())));
		
		return notifUtil.getMessageTemplate(msgCode, completeMsgs)
				.replace(NOTIFICATION_PROPERTY_LINK, url)
				.replace(NOTIFICATION_UPDATED_CREATED_REPLACE, createUpdateReplaceString);
	}
	
	

	/**
	 * private method to prepare mutation msg for localization
	 * 
	 * @param property
	 * @param CompleteMsgs
	 * @param statusCode
	 * @param urlCode
	 * @return
	 */
	private String getMsgForMutation (Property property, String CompleteMsgs, String statusCode, String urlCode) {

		String url = statusCode.equalsIgnoreCase(WF_STATUS_PAYMENT_PENDING) ? notifUtil.getPayUrl(property) : notifUtil.getMutationUrl(property);
		return notifUtil.getMessageTemplate(statusCode, CompleteMsgs).replace(urlCode, url);
	}

	/**
	 * replaces common variable for all messages
	 * 
	 * @param property
	 * @param msg
	 * @return
	 */
	private String replaceCommonValues(Property property, String msg, String localisedState) {

		msg = msg.replace(NOTIFICATION_PROPERTYID, property.getPropertyId()).replace(NOTIFICATION_APPID,
				property.getAcknowldgementNumber());

		if (configs.getIsWorkflowEnabled())
			msg = msg.replace(NOTIFICATION_STATUS, localisedState);
		return msg;
	}
	
	private String getLocalisedState(ProcessInstance workflow, String completeMsgs) {
		
		String state ="";
		if(configs.getIsWorkflowEnabled()) {
			state = workflow.getState().getState();
		}
		
		switch (state) {
			
		case WF_STATUS_REJECTED :
			return notifUtil.getMessageTemplate(WF_STATUS_REJECTED_LOCALE, completeMsgs);
			
		case WF_STATUS_DOCVERIFIED :
			return notifUtil.getMessageTemplate(WF_STATUS_DOCVERIFIED_LOCALE, completeMsgs);
			
		case WF_STATUS_FIELDVERIFIED:
			return notifUtil.getMessageTemplate(WF_STATUS_FIELDVERIFIED_LOCALE, completeMsgs);
			
		case WF_STATUS_OPEN:
			return notifUtil.getMessageTemplate(WF_STATUS_OPEN_LOCALE, completeMsgs);
			
		case PT_UPDATE_OWNER_NUMBER:
			return notifUtil.getMessageTemplate(PT_UPDATE_OWNER_NUMBER, completeMsgs);
			
		}
		return state;
	}


	/**
	 * Method to extract state from the workflow object
	 * 
	 * @param wf
	 * @return
	 */
	private String getStateFromWf(ProcessInstance wf, Boolean isWorkflowEnabled) {
		
		String state;
		if (isWorkflowEnabled) {

			Boolean isPropertyActive = wf.getState().getApplicationStatus().equalsIgnoreCase(Status.ACTIVE.toString());
			Boolean isTerminateState = wf.getState().getIsTerminateState();
			Set<String> actions = null != wf.getState().getActions()
					? actions = wf.getState().getActions().stream().map(Action::getAction).collect(Collectors.toSet())
					: Collections.emptySet();

			if (isTerminateState && CollectionUtils.isEmpty(actions)) {

				state = isPropertyActive ? WF_STATUS_APPROVED : WF_STATUS_REJECTED;
			} else if (actions.contains(ACTION_PAY)) {

				state = WF_STATUS_PAYMENT_PENDING;
			} else {

				state = wf.getState().getState();
			}

		} else {
			state = WF_NO_WORKFLOW;
		}
		return state;
	}

	/**
	 * Prepares msg for each owner and send 
	 *
	 * @param request
	 * @param msg
	 */
	private void prepareMsgAndSend(PropertyRequest request, String msg, String state) {

		Property property = request.getProperty();
		RequestInfo requestInfo = request.getRequestInfo();
		Map<String, String> mobileNumberToOwner = new HashMap<>();
		String tenantId = request.getProperty().getTenantId();
		String moduleName = request.getProperty().getWorkflow().getModuleName();

		String action;
		if(request.getProperty().getWorkflow()!=null)
			action = request.getProperty().getWorkflow().getAction();
		else
			action = WF_NO_WORKFLOW;

		List<String> configuredChannelNames =  notifUtil.fetchChannelList(new RequestInfo(), tenantId, moduleName, action);
		Set<String> mobileNumbers = new HashSet<>();

		property.getOwners().forEach(owner -> {
			if (owner.getMobileNumber() != null)
				mobileNumberToOwner.put(owner.getMobileNumber(), owner.getName());
			    mobileNumbers.add(owner.getMobileNumber());
		});


		List<SMSRequest> smsRequests = notifUtil.createSMSRequest(msg, mobileNumberToOwner);

		if(configuredChannelNames.contains(CHANNEL_NAME_SMS)){
			notifUtil.sendSMS(smsRequests);

			Boolean isActionReq = false;
			if(state.equalsIgnoreCase(PT_CORRECTION_PENDING))
				isActionReq = true;

			List<Event> events = notifUtil.enrichEvent(smsRequests, requestInfo, property.getTenantId(), property, isActionReq);
			notifUtil.sendEventNotification(new EventRequest(requestInfo, events));
		}
		if(configuredChannelNames.contains(CHANNEL_NAME_EMAIL)){
			List<EmailRequest> emailRequests = notifUtil.createEmailRequestFromSMSRequests(requestInfo,smsRequests, tenantId);
			notifUtil.sendEmail(emailRequests);
		}
	}

	private String fetchContentFromLocalization(RequestInfo requestInfo, String tenantId, String module, String code){
		String message = null;
		List<String> codes = new ArrayList<>();
		List<String> messages = new ArrayList<>();
		Object result = null;
		String locale = "";
		if (!StringUtils.isEmpty(requestInfo.getMsgId()) && requestInfo.getMsgId().split("|").length >= 2)
			locale = requestInfo.getMsgId().split("\\|")[1];

		if(StringUtils.isEmpty(locale))
			locale = configs.getFallBackLocale();
		StringBuilder uri = new StringBuilder();
		uri.append(configs.getLocalizationHost()).append(configs.getLocalizationContextPath()).append(configs.getLocalizationSearchEndpoint());
		uri.append("?tenantId=").append(tenantId.split("\\.")[0]).append("&locale=").append(locale).append("&module=").append(module);
		Map<String, Object> request = new HashMap<>();
		request.put("RequestInfo", requestInfo);
		try {
			result = restTemplate.postForObject(uri.toString(), request, Map.class);
			codes = JsonPath.read(result, LOCALIZATION_CODES_JSONPATH);
			messages = JsonPath.read(result, LOCALIZATION_MSGS_JSONPATH);
		} catch (Exception e) {
			log.error("Exception while fetching from localization: " + e);
		}
		if(CollectionUtils.isEmpty(messages)){
			throw new CustomException("LOCALIZATION_NOT_FOUND", "Localization not found for the code: " + code);
		}
		for(int index = 0; index < codes.size(); index++){
			if(codes.get(index).equals(code)){
				message = messages.get(index);
			}
		}
		return message;
	}
	
	/*
	 Method to send notification while updating owner mobile number	 
	*/

	public void sendNotificationForMobileNumberUpdate(PropertyRequest propertyRequest, Property propertyFromSearch, Map<String, String> uuidToMobileNumber) {
		
		Property property = propertyRequest.getProperty();
		String msg = null;
		
		String completeMsgs = notifUtil.getLocalizationMessages(property.getTenantId(), propertyRequest.getRequestInfo());
		msg = getMsgForMobileNumberUpdate(PT_UPDATE_OWNER_NUMBER, completeMsgs);
		prepareMsgAndSendToBothNumbers(propertyRequest, propertyFromSearch, msg,uuidToMobileNumber);
		
	}
	
	/*
	 Method to get the message template for owner mobile number update notification
	*/
	
	private String getMsgForMobileNumberUpdate(String msgCode, String completeMsgs) {
		
		return notifUtil.getMessageTemplate(msgCode, completeMsgs);
	}
	
	/*
	 Method to send notifications to both (old and new) owner mobile number while updation.
	*/

	private void prepareMsgAndSendToBothNumbers(PropertyRequest request, Property propertyFromSearch,
			String msg, Map<String, String> uuidToMobileNumber) {
		
		Property property = request.getProperty();
		RequestInfo requestInfo = request.getRequestInfo();
		List<String> configuredChannelNames =  notifUtil.fetchChannelList(requestInfo, request.getProperty().getTenantId(), PTConstants.PT_BUSINESSSERVICE, ACTION_UPDATE_MOBILE);
		Set<String> mobileNumbers = new HashSet<>();

		property.getOwners().forEach(owner -> {

			if(uuidToMobileNumber.containsKey(owner.getUuid()) && uuidToMobileNumber.get(owner.getUuid())!=owner.getMobileNumber()) {
				
				String customizedMsg = msg.replace(PT_OWNER_NAME,owner.getName()).replace(PT_OLD_MOBILENUMBER, uuidToMobileNumber.get(owner.getUuid())).replace(PT_NEW_MOBILENUMBER, owner.getMobileNumber());
				Map<String, String> mobileNumberToOwner = new HashMap<>();
				
				mobileNumberToOwner.put(uuidToMobileNumber.get(owner.getUuid()), owner.getName());
				mobileNumberToOwner.put(owner.getMobileNumber(),owner.getName());
				mobileNumbers.add(uuidToMobileNumber.get(owner.getUuid()));
				mobileNumbers.add(owner.getMobileNumber());

				if(configuredChannelNames.contains(CHANNEL_NAME_SMS)) {
					List<SMSRequest> smsRequests = notifUtil.createSMSRequest(customizedMsg, mobileNumberToOwner);
					notifUtil.sendSMS(smsRequests);
				}

				if(configuredChannelNames.contains(CHANNEL_NAME_EVENT)) {
					Boolean isActionReq = true;
					List<SMSRequest> smsRequests = notifUtil.createSMSRequest(customizedMsg, mobileNumberToOwner);
					List<Event> events = notifUtil.enrichEvent(smsRequests, requestInfo, property.getTenantId(), property, isActionReq);
					notifUtil.sendEventNotification(new EventRequest(requestInfo, events));
				}

				if(configuredChannelNames.contains(CHANNEL_NAME_EMAIL)) {
					Map<String, String> mapOfPhnoAndEmail = notifUtil.fetchUserEmailIds(mobileNumbers, requestInfo, request.getProperty().getTenantId());
					List<EmailRequest> emailRequests = notifUtil.createEmailRequest(requestInfo, customizedMsg, mapOfPhnoAndEmail);
					notifUtil.sendEmail(emailRequests);
				}
				}
		});
		
	}

	public void sendNotificationForAlternateNumberUpdate(PropertyRequest request, Property propertyFromSearch,
			Map<String, String> uuidToAlternateMobileNumber) {


		Property property = request.getProperty();
		String msg = null;
		
		String completeMsgs = notifUtil.getLocalizationMessages(property.getTenantId(), request.getRequestInfo());
		msg = getMsgForMobileNumberUpdate(PT_UPDATE_ALTERNATE_NUMBER, completeMsgs);
		prepareMsgAndSendToAlternateNumber(request, propertyFromSearch, msg,uuidToAlternateMobileNumber);
		
	}

	private void prepareMsgAndSendToAlternateNumber(PropertyRequest request, Property propertyFromSearch, String msg,
			Map<String, String> uuidToAlternateMobileNumber) {
		
		Property property = request.getProperty();
		RequestInfo requestInfo = request.getRequestInfo();
		List<String> configuredChannelNames =  notifUtil.fetchChannelList(request.getRequestInfo(), request.getProperty().getTenantId(), PTConstants.PT_BUSINESSSERVICE, PTConstants.ACTION_ALTERNATE_MOBILE);
		Set<String> mobileNumbers = new HashSet<>();

		property.getOwners().forEach(owner -> {

			if(owner.getAlternatemobilenumber()!=null && !uuidToAlternateMobileNumber.get(owner.getUuid()).equalsIgnoreCase(owner.getAlternatemobilenumber()) ) {	
				String customizedMsgForApp = msg.replace(PT_OWNER_NAME,owner.getName()).replace(PT_ALTERNATE_NUMBER, owner.getAlternatemobilenumber());
				String customizedMsg =  customizedMsgForApp.replace(VIEW_PROPERTY_CODE,"");
				Map<String, String> mobileNumberToOwner = new HashMap<>();
				mobileNumberToOwner.put(owner.getMobileNumber(), owner.getName());
				mobileNumbers.add(owner.getMobileNumber());

				if(configuredChannelNames.contains(CHANNEL_NAME_SMS)) {
					List<SMSRequest> smsRequests = notifUtil.createSMSRequest(customizedMsg, mobileNumberToOwner);
					notifUtil.sendSMS(smsRequests);
				}

				if(configuredChannelNames.contains(CHANNEL_NAME_EVENT)) {
					Boolean isActionReq = true;
					List<SMSRequest> smsRequests = notifUtil.createSMSRequest(customizedMsgForApp, mobileNumberToOwner);
					List<Event> events = notifUtil.enrichEvent(smsRequests, requestInfo, property.getTenantId(), property, isActionReq);
					notifUtil.sendEventNotification(new EventRequest(requestInfo, events));
				}

				if(configuredChannelNames.contains(CHANNEL_NAME_EMAIL)) {
					Map<String, String> mapOfPhnoAndEmail = notifUtil.fetchUserEmailIds(mobileNumbers, requestInfo, request.getProperty().getTenantId());
					List<EmailRequest> emailRequests = notifUtil.createEmailRequest(requestInfo, customizedMsg, mapOfPhnoAndEmail);
				 	notifUtil.sendEmail(emailRequests);
				}
			}
		});
		
		
	}



	/**
	 * Method to send notifications for citizen feedback
	 *
	 * @param property
	 * @param localizationMsgs
	 * @param serviceType
	 * @return
	 */
	private void sendNotificationForCitizenFeedback(Property property, String localizationMsgs, String serviceType) {

		String citizenFeedackMessage = notifUtil.getMsgForCitizenFeedbackNotification(property, localizationMsgs, serviceType);
		Map<String, String> mobileNumberToOwner = new HashMap<>();

		property.getOwners().forEach(owner -> {
			if (owner.getMobileNumber() != null)
				mobileNumberToOwner.put(owner.getMobileNumber(), owner.getName());
		});

		List<SMSRequest> smsRequests = notifUtil.createSMSRequest(citizenFeedackMessage, mobileNumberToOwner);
		notifUtil.sendSMS(smsRequests);

	}
	
	public void triggerNotificationsGenerateBill(PtTaxCalculatorTracker propertyTracker, Bill bill, RequestInfo requestInfo) {
		ClassPathResource resource = new ClassPathResource(PROPERTY_BILL_EMAIL_TEMPLATE_LOCATION);
		String emailBody = getContentAsString(resource);
		String smsBody = SMS_BODY_GENERATE_BILL;
		String emailSubject = EMAIL_SUBJECT_GENERATE_BILL;
	
		emailBody = populateNotificationPlaceholders(emailBody, propertyTracker, bill);
		smsBody = populateNotificationPlaceholders(smsBody, propertyTracker, bill);
		emailSubject = populateNotificationPlaceholders(emailSubject, propertyTracker, bill);
	
		if (!StringUtils.isEmpty(bill.getPayerEmail())) {
			sendEmailforGenerateBill(emailBody, Collections.singletonList(bill.getPayerEmail()), requestInfo, null,
					emailSubject);
		}
		if (!StringUtils.isEmpty(bill.getMobileNumber())) {
			sendSms(smsBody, bill.getMobileNumber());
		}

	}
	
	private String populateNotificationPlaceholders(String body, PtTaxCalculatorTracker propertyTracker, Bill bill) {

        SimpleDateFormat monthFormat = new SimpleDateFormat("dd MMMM - yyyy");

//		Instant instant = Instant.ofEpochMilli(bill.getBillDate());
//		LocalDateTime dateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();

		body = body.replace(MONTH_PLACEHOLDER,  monthFormat.format(propertyTracker.getFromDate())+" / "+monthFormat.format(propertyTracker.getToDate()));
		body = body.replace(RECIPINTS_NAME_PLACEHOLDER, StringUtils.isEmpty(bill.getPayerName())?"N/A":bill.getPayerName());
		//		body = body.replace(YEAR_PLACEHOLDER, propertyTracker.getFinancialYear());
		body = body.replace(BILL_NO_PLACEHOLDER, bill.getBillNumber());
//		if (!CollectionUtils.isEmpty(garbageAccount.getAddresses())) {
//			body = body.replace(ADDRESS_PLACEHOLDER, prepareAddress(garbageAccount.getAddresses().get(0)));
//		}
//		if (!CollectionUtils.isEmpty(garbageAccount.getGrbgCollectionUnits())) {
//			body = body.replace(COLLECTION_UNIT_TYPE_PLACEHOLDER,
//					!StringUtils.isEmpty(garbageAccount.getGrbgCollectionUnits().get(0).getUnitType())
//							? garbageAccount.getGrbgCollectionUnits().get(0).getUnitType()
//							: "");
//		}
		body = body.replace(AMOUNT_PLACEHOLDER, String.valueOf(bill.getTotalAmount()));
//		body = body.replace(DUE_DATE_PLACEHOLDER, "");

		body = body.replace(LINK_PLACEHOLDER, "https://citizenseva.hp.gov.in/hp-udd/ ");
		body = body.replace(PROPERTY_ID_PLACEHOLDER, propertyTracker.getPropertyId());
		body = body.replace(PROPERTY_PAY_NOW_BILL_URL_PLACEHOLDER,frontEndUri);
//		body = body.replace(GARBAGE_PAY_NOW_BILL_URL_PLACEHOLDER,
//				grbgConfig.getGrbgServiceHostUrl() + "" + grbgConfig.getGrbgPayNowBillEndpoint() + ""
//						+ encryptionService.encryptString(garbageAccount.getCreated_by()));

		return body;
	}
	
	public void sendSms(String message, String mobileNumber) {

		SMSSentRequest smsRequest = SMSSentRequest.builder().message(message).mobileNumber(mobileNumber)
				.category(SMSCategory.NOTIFICATION).templateName(SMS_TEMPLATE_BILL_NOTIFICATION).build();

		kafkaTemplate.send(smsTopic, smsRequest);
	}
	
	private void sendEmailforGenerateBill(String emailBody, List<String> emailIds, RequestInfo requestInfo,
			List<String> attachmentDocRefIds, String emailSubject) {
		Email email = new Email();
		email.setEmailTo(new HashSet<>(emailIds));
		email.setBody(emailBody);
		email.setSubject(emailSubject);

		if (!CollectionUtils.isEmpty(attachmentDocRefIds)) {
			email.setHTML(true);
//			email.setFileStoreIds(attachmentDocRefIds);
		} else {
			email.setHTML(true);
		}

		EmailRequest emailRequest = EmailRequest.builder().requestInfo(requestInfo).email(email).build();
		kafkaTemplate.send(emailTopic, emailRequest);
	}
	
	public String getContentAsString(ClassPathResource resource) {
	    try (InputStream is = resource.getInputStream();
	         BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
	        return reader.lines().collect(Collectors.joining("\n"));
	    } catch (IOException e) {
	        e.printStackTrace();
	        return "";
	    }
	}


}