package org.egov.pt.service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.models.Assessment;
import org.egov.pt.models.Property;
import org.egov.pt.models.PropertyCriteria;
import org.egov.pt.models.enums.CreationReason;
import org.egov.pt.models.enums.Status;
import org.egov.pt.models.event.Event;
import org.egov.pt.models.event.EventRequest;
import org.egov.pt.models.workflow.Action;
import org.egov.pt.models.workflow.ProcessInstance;
import org.egov.pt.util.NotificationUtil;
import org.egov.pt.util.PTConstants;
import org.egov.pt.web.contracts.EmailRequest;
import org.egov.pt.web.contracts.NoticeRequest;
import org.egov.pt.web.contracts.PropertyRequest;
import org.egov.pt.web.contracts.SMSRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties.Template;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import static org.egov.pt.util.PTConstants.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;
import com.jayway.jsonpath.JsonPath;

@Slf4j
@Service
public class NotificationService {

	@Autowired
	private NotificationUtil notifUtil;

	@Autowired
	private PropertyConfiguration configs;

	@Autowired
	private RestTemplate restTemplate;

	@Value("${notification.url}")
	private String notificationURL;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	PropertyService propertyService;


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

	public void sendNotificationForCreate(PropertyRequest propertyRequest) {

		Property property = propertyRequest.getProperty();
		ProcessInstance wf = property.getWorkflow();
		String createOrUpdate = null;
		String msg = null;

		Boolean isCreate =  CreationReason.CREATE.equals(property.getCreationReason());
		String state = getStateFromWf(wf, configs.getIsWorkflowEnabled());
		String completeMsgs = notifUtil.getLocalizationMessages(property.getTenantId(), propertyRequest.getRequestInfo());
		String localisedState = getLocalisedState(wf, completeMsgs);
		String templateId=null;
		boolean replace=false;
		switch (state) {

		case WF_NO_WORKFLOW:
			createOrUpdate = isCreate ? CREATED_STRING : UPDATED_STRING;
			msg = getMsgForUpdate(property, UPDATE_NO_WORKFLOW, completeMsgs, createOrUpdate);

			break;

		case WF_STATUS_OPEN:
			createOrUpdate = isCreate ? CREATE_STRING : UPDATE_STRING;
			msg = getMsgForCreate(property, CREATE_OPEN_STATE_MESSAGE_MNPT, completeMsgs,createOrUpdate,propertyRequest,state);
			templateId=CREATE_OPEN_STATE_MESSAGE_MNPT_TEMPLATE_ID;
			break;

		case WF_STATUS_DOCVERIFIED:
			createOrUpdate = isCreate ? CREATE_STRING : UPDATE_STRING;
			msg = getMsgForCreate(property, CREATE_PT_DOC_VERIFIER_VERIFY_STATE_MESSAGE_MNPT, completeMsgs,createOrUpdate,propertyRequest,state);
			templateId=CREATE_PT_DOC_VERIFIER_VERIFY_STATE_MESSAGE_MNPT_TEMPLATE_ID;
			//sendNotificationUpdateForMutation
			break;

		case WF_STATUS_REJECTED:
			createOrUpdate = isCreate ? CREATE_STRING : UPDATE_STRING;
			msg = getMsgForCreate(property, "REJECT", completeMsgs,createOrUpdate,propertyRequest,state);
			Set<String> role = propertyRequest.getRequestInfo().getUserInfo().getRoles().stream().map(x->x.getCode()).collect(Collectors.toSet());
			if(role.contains("PT_DOC_VERIFIER"))
				templateId=CREATE_PT_DOC_VERIFIER_REJECT_STATE_MESSAGE_MNPT_TEMPLATE_ID;
			else if(role.contains("PT_APPROVER"))
				templateId=CREATE_PT_APPROVER_REJECT_STATE_MESSAGE_MNPT_TEMPLATE_ID;
			
			break;

		case PT_CORRECTION_PENDING:
			createOrUpdate = isCreate ? CREATE_STRING : UPDATE_STRING;
			msg = getMsgForCreate(property, CREATE_PT_DOC_VERIFIER_SENDBACK_STATE_MESSAGE_MNPT, completeMsgs,createOrUpdate,propertyRequest,state);
			templateId=CREATE_PT_DOC_VERIFIER_SENDBACK_STATE_MESSAGE_MNPT_TEMPLATE_ID;
			break;

		case WF_STATUS_FIELDVERIFIED:
			createOrUpdate = isCreate ? CREATE_STRING : UPDATE_STRING;
			msg = getMsgForCreate(property, CREATE_PT_FIELD_INSPECTOR_FORWARD_STATE_MESSAGE_MNPT, completeMsgs,createOrUpdate,propertyRequest,state);
			templateId=CREATE_PT_FIELD_INSPECTOR_FORWARD_STATE_MESSAGE_MNPT_TEMPLATE_ID;
			break;

		case WF_STATUS_APPROVED:
			createOrUpdate = isCreate ? CREATED_STRING : UPDATED_STRING;
			msg = getMsgForCreate(property, CREATE_PT_APPROVER_APPROVE_STATE_MNPT, completeMsgs, createOrUpdate,propertyRequest,state);
			templateId=CREATE_PT_APPROVER_APPROVE_STATE_MNPT_TEMPLATE_ID;
			replace=true;
			//sendNotificationForCitizenFeedback(property,completeMsgs,createOrUpdate);
			break;

		default:
			createOrUpdate = isCreate ? CREATE_STRING : UPDATE_STRING;
			msg = getMsgForUpdate(property, WF_UPDATE_STATUS_CHANGE_CODE, completeMsgs, createOrUpdate);
			break;
		}

		msg = replaceCommonValuesForManipur(property, msg, localisedState,replace);
		System.out.println("message------------------->"+msg);
		prepareMsgAndSendNew(propertyRequest, msg,state,templateId);
	}


	public void sendNotificationForMutationNew(PropertyRequest propertyRequest) {

		Property property = propertyRequest.getProperty();
		ProcessInstance wf = property.getWorkflow();
		String createOrUpdate = null;
		String msg = null;

		Boolean isCreate =  CreationReason.CREATE.equals(property.getCreationReason());
		String state = getStateFromWf(wf, configs.getIsWorkflowEnabled());
		String completeMsgs = notifUtil.getLocalizationMessages(property.getTenantId(), propertyRequest.getRequestInfo());
		String localisedState = getLocalisedState(wf, completeMsgs);
		String templateId=null;
		boolean replace=false;
		switch (state) {

		case WF_NO_WORKFLOW:
			createOrUpdate = isCreate ? CREATED_STRING : UPDATED_STRING;
			msg = getMsgForUpdate(property, UPDATE_NO_WORKFLOW, completeMsgs, createOrUpdate);
			break;

		case WF_STATUS_OPEN:
			createOrUpdate = isCreate ? CREATE_STRING : UPDATE_STRING;
			msg = getMsgForCreateMutationNew(property, MUTATION_OPEN_STATE_MESSAGE_MNPT, completeMsgs,createOrUpdate,propertyRequest,state);
			templateId=MUTATION_OPEN_STATE_MESSAGE_MNPT_TEMPLATE_ID;
			break;

		case WF_STATUS_DOCVERIFIED:
			createOrUpdate = isCreate ? CREATE_STRING : UPDATE_STRING;
			msg = getMsgForCreateMutationNew(property, MUTATION_PT_DOC_VERIFIER_VERIFY_STATE_MESSAGE_MNPT, completeMsgs,createOrUpdate,propertyRequest,state);
			templateId=MUTATION_PT_DOC_VERIFIER_VERIFY_STATE_MESSAGE_MNPT_TEMPLATE_ID;
			sendNotificationUpdateForMutation(property,completeMsgs,createOrUpdate,MUTATION_PT_DOC_VERIFIER_STATUS_UPDATE_STATE_MESSAGE_MNPT,MUTATION_PT_DOC_VERIFIER_STATUS_UPDATE_STATE_MESSAGE_MNPT_TEMPLATE_ID);
			break;

		case WF_STATUS_REJECTED:
			createOrUpdate = isCreate ? CREATE_STRING : UPDATE_STRING;
			msg = getMsgForCreateMutationNew(property, "REJECT", completeMsgs,createOrUpdate,propertyRequest,state);
			templateId=MUTATION_PT_DOC_VERIFIER_REJECT_STATE_MESSAGE_MNPT_TEMPLATE_ID;
			sendNotificationUpdateForMutation(property,completeMsgs,createOrUpdate,MUTATION_PT_DOC_VERIFIER_STATUS_UPDATE_STATE_MESSAGE_MNPT,MUTATION_PT_DOC_VERIFIER_STATUS_UPDATE_STATE_MESSAGE_MNPT_TEMPLATE_ID);
			break;

		case PT_CORRECTION_PENDING:
			createOrUpdate = isCreate ? CREATE_STRING : UPDATE_STRING;
			msg = getMsgForCreateMutationNew(property, MUTATION_PT_DOC_VERIFIER_SENDBACK_STATE_MESSAGE_MNPT, completeMsgs,createOrUpdate,propertyRequest,state);
			templateId=MUTATION_PT_DOC_VERIFIER_SENDBACK_STATE_MESSAGE_MNPT_TEMPLATE_ID;
			sendNotificationUpdateForMutation(property,completeMsgs,createOrUpdate,MUTATION_PT_DOC_VERIFIER_STATUS_UPDATE_STATE_MESSAGE_MNPT,MUTATION_PT_DOC_VERIFIER_STATUS_UPDATE_STATE_MESSAGE_MNPT_TEMPLATE_ID);
			break;

		default:
			createOrUpdate = isCreate ? CREATE_STRING : UPDATE_STRING;
			msg = getMsgForUpdate(property, WF_UPDATE_STATUS_CHANGE_CODE, completeMsgs, createOrUpdate);
			break;
		}

		msg = replaceCommonValuesForManipur(property, msg, localisedState,replace);
		System.out.println("message------------------->"+msg);
		prepareMsgAndSendNew(propertyRequest, msg,state,"templateId");
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
		String templateId=null;
		boolean replace=false;
		switch (state) {

		case WF_NO_WORKFLOW:
			createOrUpdate = isCreate ? CREATED_STRING : UPDATED_STRING;
			msg = getMsgForUpdate(property, UPDATE_NO_WORKFLOW, completeMsgs, createOrUpdate);
			break;

		case WF_STATUS_OPEN:
			createOrUpdate = isCreate ? CREATE_STRING : UPDATE_STRING;
			msg = getMsgForUpdate(property, UPDATE_OPEN_STATE_MESSAGE_MNPT, completeMsgs,createOrUpdate,propertyRequest,state);
			templateId=UPDATE_OPEN_STATE_MESSAGE_MNPT_TEMPLATE_ID;
			break;

		case WF_STATUS_DOCVERIFIED:
			createOrUpdate = isCreate ? CREATE_STRING : UPDATE_STRING;
			msg = getMsgForUpdate(property, UPDATE_PT_DOC_VERIFIER_VERIFY_STATE_MESSAGE_MNPT, completeMsgs,createOrUpdate,propertyRequest,state);
			templateId=UPDATE_PT_DOC_VERIFIER_VERIFY_STATE_MESSAGE_MNPT_TEMPLATE_ID;
			break;

		case WF_STATUS_REJECTED:
			createOrUpdate = isCreate ? CREATE_STRING : UPDATE_STRING;
			msg = getMsgForUpdate(property, "REJECT", completeMsgs,createOrUpdate,propertyRequest,state);
			Set<String> role = propertyRequest.getRequestInfo().getUserInfo().getRoles().stream().map(x->x.getCode()).collect(Collectors.toSet());
			if(role.contains("PT_DOC_VERIFIER"))
				templateId=UPDATE_PT_DOC_VERIFIER_REJECT_STATE_MESSAGE_MNPT_TEMPLATE_ID;
			else if(role.contains("PT_APPROVER"))
				templateId=UPDATE_PT_APPROVER_REJECT_STATE_MESSAGE_MNPT_TEMPLATE_ID;
			break;

		case PT_CORRECTION_PENDING:
			createOrUpdate = isCreate ? CREATE_STRING : UPDATE_STRING;
			msg = getMsgForUpdate(property, UPDATE_PT_DOC_VERIFIER_SENDBACK_STATE_MESSAGE_MNPT, completeMsgs,createOrUpdate,propertyRequest,state);
			templateId=UPDATE_PT_DOC_VERIFIER_SENDBACK_STATE_MESSAGE_MNPT_TEMPLATE_ID;
			break;

		case WF_STATUS_FIELDVERIFIED:
			createOrUpdate = isCreate ? CREATE_STRING : UPDATE_STRING;
			msg = getMsgForUpdate(property, UPDATE_PT_FIELD_INSPECTOR_FORWARD_STATE_MESSAGE_MNPT, completeMsgs,createOrUpdate,propertyRequest,state);
			templateId=UPDATE_PT_FIELD_INSPECTOR_FORWARD_STATE_MESSAGE_MNPT_TEMPLATE_ID;
			break;

		case WF_STATUS_APPROVED:
			createOrUpdate = isCreate ? CREATED_STRING : UPDATED_STRING;
			msg = getMsgForCreate(property, UPDATE_PT_APPROVER_APPROVE_STATE_MNPT, completeMsgs, createOrUpdate,propertyRequest,state);
			templateId=UPDATE_PT_APPROVER_APPROVE_STATE_MNPT_TEMPLATE_ID;
			//replace=true;
			//sendNotificationForCitizenFeedback(property,completeMsgs,createOrUpdate);
			break;

		default:
			createOrUpdate = isCreate ? CREATE_STRING : UPDATE_STRING;
			msg = getMsgForUpdate(property, WF_UPDATE_STATUS_CHANGE_CODE, completeMsgs, createOrUpdate);
			break;
		}

		msg = replaceCommonValuesForManipur(property, msg, localisedState,replace);
		System.out.println("message------------------->"+msg);
		prepareMsgAndSendNew(propertyRequest, msg,state,templateId);
	}




	public void sendNotificationForCreateUpdate(PropertyRequest propertyRequest) {

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

	private String getMsgForCreate(Property property, String msgCode, String completeMsgs, 
			String createUpdateReplaceString,PropertyRequest propertyRequest,String state) {
		String return_message = "";

		Set<String> role = propertyRequest.getRequestInfo().getUserInfo().getRoles().stream().map(x->x.getCode()).collect(Collectors.toSet());
		if(state.equalsIgnoreCase(WF_STATUS_REJECTED) ||state.equalsIgnoreCase(PT_CORRECTION_PENDING)) {

			if(state.equalsIgnoreCase(WF_STATUS_REJECTED)) {	
				if(role.contains("PT_DOC_VERIFIER"))
				{
					return_message = notifUtil.getMessageTemplate(CREATE_PT_DOC_VERIFIER_REJECT_STATE_MESSAGE_MNPT, completeMsgs);

				}
				//MUTATION_PT_DOC_VERIFIER_REJECT_STATE_MESSAGE_MNPT
				else if(role.contains("PT_APPROVER")) {
					return_message = notifUtil.getMessageTemplate(CREATE_PT_APPROVER_REJECT_STATE_MESSAGE_MNPT, completeMsgs);
				}
			
				
			}

			else if(state.equalsIgnoreCase(PT_CORRECTION_PENDING)) {
				return_message = notifUtil.getMessageTemplate(MUTATION_PT_DOC_VERIFIER_SENDBACK_STATE_MESSAGE_MNPT, completeMsgs);	
			}
		}
		else {
			return_message = notifUtil.getMessageTemplate(msgCode, completeMsgs);
		}
		return return_message;
	}
	
	
	
	
	private String getMsgForCreateMutationNew(Property property, String msgCode, String completeMsgs, 
			String createUpdateReplaceString,PropertyRequest propertyRequest,String state) {
		String return_message = "";

		Set<String> role = propertyRequest.getRequestInfo().getUserInfo().getRoles().stream().map(x->x.getCode()).collect(Collectors.toSet());
		if(state.equalsIgnoreCase(WF_STATUS_REJECTED) ||state.equalsIgnoreCase(PT_CORRECTION_PENDING)) {

			if(state.equalsIgnoreCase(WF_STATUS_REJECTED)) {
				if(role.contains("PT_DOC_VERIFIER"))
				{
					return_message = notifUtil.getMessageTemplate(MUTATION_PT_DOC_VERIFIER_REJECT_STATE_MESSAGE_MNPT, completeMsgs);

				}
				
			}

			else if(state.equalsIgnoreCase(PT_CORRECTION_PENDING)) {
				return_message = notifUtil.getMessageTemplate(MUTATION_PT_DOC_VERIFIER_SENDBACK_STATE_MESSAGE_MNPT, completeMsgs);	
			}
		}
		else {
			return_message = notifUtil.getMessageTemplate(msgCode, completeMsgs);
		}
		return return_message;
	}



	private String getMsgForUpdate(Property property, String msgCode, String completeMsgs, 
			String createUpdateReplaceString,PropertyRequest propertyRequest,String state) {
		String return_message = "";

		Set<String> role = propertyRequest.getRequestInfo().getUserInfo().getRoles().stream().map(x->x.getCode()).collect(Collectors.toSet());
		if(state.equalsIgnoreCase(WF_STATUS_REJECTED) ||state.equalsIgnoreCase(PT_CORRECTION_PENDING)) {

			if(state.equalsIgnoreCase(WF_STATUS_REJECTED)) {
				if(role.contains("PT_DOC_VERIFIER"))
				{
					return_message = notifUtil.getMessageTemplate(UPDATE_PT_DOC_VERIFIER_REJECT_STATE_MESSAGE_MNPT, completeMsgs);					
				}
				else if(role.contains("PT_APPROVER")) {
					return_message = notifUtil.getMessageTemplate(UPDATE_PT_APPROVER_REJECT_STATE_MESSAGE_MNPT, completeMsgs);
				}
			}
			else if(state.equalsIgnoreCase(PT_CORRECTION_PENDING)) {

				return_message = notifUtil.getMessageTemplate(UPDATE_PT_DOC_VERIFIER_SENDBACK_STATE_MESSAGE_MNPT, completeMsgs);					

			}
		}
		else {
			return_message = notifUtil.getMessageTemplate(msgCode, completeMsgs);
		}
		return return_message;
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

	private String replaceCommonValuesForManipur(Property property, String msg, String localisedState,boolean replace) {
		if(replace)
			msg = msg.replace(NOTIFICATION_UPIN, property.getPropertyId());

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


	private void prepareMsgAndSendNew(PropertyRequest request, String msg, String state,String templateId) {

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


		List<SMSRequest> smsRequests = notifUtil.createSMSRequestNew(msg, mobileNumberToOwner,templateId);

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


	private void sendNotificationUpdateForMutation(Property property, String localizationMsgs, String serviceType,String msgCode,String templateID) {

		String citizenFeedackMessage = notifUtil.getMsgForMutaiotnNotification(property, localizationMsgs, serviceType,msgCode);
		Map<String, String> mobileNumberToOwner = new HashMap<>();

		property.getOwners().forEach(owner -> {
			if (owner.getMobileNumber() != null)
				mobileNumberToOwner.put(owner.getMobileNumber(), owner.getName());
		});

		List<SMSRequest> smsRequests = notifUtil.createSMSRequestNew(citizenFeedackMessage, mobileNumberToOwner,templateID);
		notifUtil.sendSMS(smsRequests);

	}
	
	
	public void processForBifurcation(PropertyRequest request, String topic) {

		Property property = request.getProperty();
		String completeMsgs = notifUtil.getLocalizationMessages(property.getTenantId(), request.getRequestInfo());
		property =mapper.convertValue(property.getAdditionalDetails().get("parentProperty"), Property.class);
		RequestInfo requestInfo = request.getRequestInfo();
		Map<String, String> mobileNumberToOwner = new HashMap<>();
		String tenantId = request.getProperty().getTenantId();
		String moduleName = "PT";
		String msg = null;
		String action;
		String templateId = null;
		action = "APPROVE";
		if(topic.equalsIgnoreCase(configs.getUpdatePropertyForDeactivaingForBifurcationTopic())) {
			msg = notifUtil.getMessageTemplate(BIFURCATION_DEACT_NOTIF_PARENT_PROP_OWNER_MNPT, completeMsgs);
			templateId=BIFURCATION_DEACT_NOTIF_PARENT_PROP_OWNER_MNPT_TEMPLATE_ID;
			msg = msg.replace(NOTIFICATION_UPIN,property.getPropertyId());
		}
		
		

		List<String> configuredChannelNames =  notifUtil.fetchChannelList(new RequestInfo(), tenantId, moduleName, action);
		Set<String> mobileNumbers = new HashSet<>();

		property.getOwners().forEach(owner -> {
			if (owner.getMobileNumber() != null)
				mobileNumberToOwner.put(owner.getMobileNumber(), owner.getName());
			mobileNumbers.add(owner.getMobileNumber());
		});


		List<SMSRequest> smsRequests = notifUtil.createSMSRequestNew(msg, mobileNumberToOwner,templateId);

		if(configuredChannelNames.contains(CHANNEL_NAME_SMS)){
			notifUtil.sendSMS(smsRequests);
			Boolean isActionReq = false;
			List<Event> events = notifUtil.enrichEventNew(smsRequests, requestInfo, property.getTenantId(), property, isActionReq);
			notifUtil.sendEventNotification(new EventRequest(requestInfo, events));
		}
		
		if(configuredChannelNames.contains(CHANNEL_NAME_EMAIL)){
			List<EmailRequest> emailRequests = notifUtil.createEmailRequestFromSMSRequests(requestInfo,smsRequests, tenantId);
			notifUtil.sendEmail(emailRequests);
		}
	}
	
	public void sendPaymentReminder(Assessment asmt,RequestInfo requestInfo)
	{
		Map<String, String> mobileNumberToOwner = new HashMap<>();
		Set<String> mobileNumbers = new HashSet<>();
		
		String completeMsgs = notifUtil.getLocalizationMessages(asmt.getTenantId(), requestInfo);
		List<String> configuredChannelNames =  notifUtil.fetchChannelList(new RequestInfo(), asmt.getTenantId(), PTConstants.PT_BUSINESSSERVICE, PTConstants.WF_NO_WORKFLOW);
		String msg = notifUtil.getMessageTemplate(NOTICE_FOR_PAYMENT_REMINDER, completeMsgs);
		String templateId=NOTICE_FOR_PAYMENT_REMINDER_TEMPLATE_ID;
		
		PropertyCriteria propertyCriteria=new PropertyCriteria();
		Set<String> propertyIds=new HashSet<String>();
		propertyIds.add(asmt.getPropertyId());
		propertyCriteria.setPropertyIds(propertyIds);
		propertyCriteria.setTenantId(asmt.getTenantId());
		List<Property> propertylist=propertyService.searchProperty(propertyCriteria, requestInfo);
		Property property=propertylist.get(0);
		property.getOwners().forEach(owner -> {
			if (owner.getMobileNumber() != null)
				mobileNumberToOwner.put(owner.getMobileNumber(), owner.getName());
			mobileNumbers.add(owner.getMobileNumber());
		});
		
		List<SMSRequest> smsRequests = notifUtil.createSMSRequestNew(msg, mobileNumberToOwner,templateId);

		if(configuredChannelNames.contains(CHANNEL_NAME_SMS)){
			notifUtil.sendSMS(smsRequests);
			Boolean isActionReq = false;
			List<Event> events = notifUtil.enrichEventNew(smsRequests, requestInfo, property.getTenantId(), property, isActionReq);
			notifUtil.sendEventNotification(new EventRequest(requestInfo, events));
		}
		
		if(configuredChannelNames.contains(CHANNEL_NAME_EMAIL)){
			List<EmailRequest> emailRequests = notifUtil.createEmailRequestFromSMSRequests(requestInfo,smsRequests, property.getTenantId());
			notifUtil.sendEmail(emailRequests);
		}
		
		System.out.println("noticeRequest::"+property.getPropertyId());
	}
	
	public void sendNoticeInformationForEntryPremises(NoticeRequest noticeRequest)
	{
		Map<String, String> mobileNumberToOwner = new HashMap<>();
		Set<String> mobileNumbers = new HashSet<>();
		
		String completeMsgs = notifUtil.getLocalizationMessages(noticeRequest.getNotice().getTenantId(), noticeRequest.getRequestInfo());
		List<String> configuredChannelNames =  notifUtil.fetchChannelList(new RequestInfo(), noticeRequest.getNotice().getTenantId(), PTConstants.PT_BUSINESSSERVICE, PTConstants.WF_NO_WORKFLOW);
		String msg = notifUtil.getMessageTemplate(NOTICE_TO_ENTER_PREMISE, completeMsgs);
		String templateId=NOTICE_TO_ENTER_PREMISE_TEMPLATE_ID;
		msg = msg.replace("{DATE_TIME}", noticeRequest.getNotice().getEntryDate()+" "+noticeRequest.getNotice().getEntryTime());
		
		PropertyCriteria propertyCriteria=new PropertyCriteria();
		Set<String> propertyIds=new HashSet<String>();
		propertyIds.add(noticeRequest.getNotice().getPropertyId());
		propertyCriteria.setPropertyIds(propertyIds);
		propertyCriteria.setTenantId(noticeRequest.getNotice().getTenantId());
		List<Property> propertylist=propertyService.searchProperty(propertyCriteria, noticeRequest.getRequestInfo());
		Property property=propertylist.get(0);
		property.getOwners().forEach(owner -> {
			if (owner.getMobileNumber() != null)
				mobileNumberToOwner.put(owner.getMobileNumber(), owner.getName());
			mobileNumbers.add(owner.getMobileNumber());
		});
		
		List<SMSRequest> smsRequests = notifUtil.createSMSRequestNew(msg, mobileNumberToOwner,templateId);

		if(configuredChannelNames.contains(CHANNEL_NAME_SMS)){
			notifUtil.sendSMS(smsRequests);
			Boolean isActionReq = false;
			List<Event> events = notifUtil.enrichEventNew(smsRequests, noticeRequest.getRequestInfo(), property.getTenantId(), property, isActionReq);
			notifUtil.sendEventNotification(new EventRequest(noticeRequest.getRequestInfo(), events));
		}
		
		if(configuredChannelNames.contains(CHANNEL_NAME_EMAIL)){
			List<EmailRequest> emailRequests = notifUtil.createEmailRequestFromSMSRequests(noticeRequest.getRequestInfo(),smsRequests, noticeRequest.getNotice().getTenantId());
			notifUtil.sendEmail(emailRequests);
		}
		
		System.out.println("noticeRequest::"+noticeRequest.getNotice().getPropertyId());
	}


}