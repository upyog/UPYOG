package org.egov.garbagecollection.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.garbagecollection.config.GCConfiguration;
import org.egov.garbagecollection.constants.GCConstants;
import org.egov.garbagecollection.util.GcServicesUtil;
import org.egov.garbagecollection.web.models.GarbageConnectionRequest;
import org.egov.tracer.model.CustomException;

import org.egov.garbagecollection.repository.ServiceRequestRepository;
import org.egov.garbagecollection.util.NotificationUtil;
import org.egov.garbagecollection.validator.ValidateProperty;
import org.egov.garbagecollection.web.models.*;
import org.egov.garbagecollection.web.models.collection.PaymentResponse;
import org.egov.garbagecollection.web.models.users.UserDetailResponse;
import org.egov.garbagecollection.web.models.users.UserSearchRequest;
import org.egov.garbagecollection.web.models.workflow.BusinessService;
import org.egov.garbagecollection.web.models.workflow.ProcessInstance;
import org.egov.garbagecollection.web.models.workflow.State;
import org.egov.garbagecollection.workflow.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static org.egov.garbagecollection.constants.GCConstants.*;

@Service
@Slf4j
public class WorkflowNotificationService {

    @Autowired
    private NotificationUtil notificationUtil;

    @Autowired
    private GCConfiguration config;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private GcServicesUtil gcServicesUtil;

    @Autowired
    private ValidateProperty validateProperty;

    @Autowired
    private UserService userService;

    String tenantIdReplacer = "$tenantId";
    String urlReplacer = "url";
    String requestInfoReplacer = "RequestInfo";
    String WaterConnectionReplacer = "WaterConnection";
    String fileStoreIdReplacer = "$fileStoreIds";
    String totalAmount= "totalAmount";
    String applicationFee = "applicationFee";
    String serviceFee = "serviceFee";
    String tax = "tax";
    String applicationNumberReplacer = "$applicationNumber";
    String consumerCodeReplacer = "$consumerCode";
    String connectionNoReplacer = "$connectionNumber";
    String mobileNoReplacer = "$mobileNo";
    String applicationKey = "$applicationkey";
    String propertyKey = "property";
    String businessService = "GC.ONE_TIME_FEE";



    /**
     *
     * @param request record is bill response.
     * @param topic topic is bill generation topic for water.
     */
    public void process(GarbageConnectionRequest request, String topic) {
        try {
        	log.info("In process of consumer to generate notifications");
            String applicationStatus = request.getGarbageConnection().getApplicationStatus();
            List<String> configuredChannelNames=new ArrayList<String>();
            
            log.info("configuredChannelNames for request Type " + request.getGarbageConnection().getApplicationType() + " is :: "  + businessService+" action :  : " +request.getGarbageConnection().getProcessInstance().getAction());
            
            
			if(request.getGarbageConnection().getApplicationType().equalsIgnoreCase(GCConstants.GARBAGE_RECONNECTION) || request.getGarbageConnection().getApplicationType().equalsIgnoreCase(GCConstants.DISCONNECT_GARBAGE_CONNECTION))
            	configuredChannelNames=  notificationUtil.fetchChannelList(request.getRequestInfo(), request.getGarbageConnection().getTenantId(), "GC.CREATE", request.getGarbageConnection().getProcessInstance().getAction());
            else
                configuredChannelNames =  notificationUtil.fetchChannelList(request.getRequestInfo(), request.getGarbageConnection().getTenantId(), businessService, request.getGarbageConnection().getProcessInstance().getAction());
            log.info("configuredChannelNames for request Type " + request.getGarbageConnection().getApplicationType() + " is :: "  + configuredChannelNames);
			User userInfoCopy = request.getRequestInfo().getUserInfo();
            User userInfo = notificationUtil.getInternalMicroserviceUser(request.getGarbageConnection().getTenantId());
            request.getRequestInfo().setUserInfo(userInfo);

            Property property = validateProperty.getOrValidateProperty(request);

            request.getRequestInfo().setUserInfo(userInfoCopy);
            if(configuredChannelNames.contains(CHANNEL_NAME_EVENT)){
                if (config.getIsUserEventsNotificationEnabled() != null && config.getIsUserEventsNotificationEnabled()) {
                    EventRequest eventRequest = getEventRequest(request, topic, property, applicationStatus);
                    if (eventRequest != null) {
                        notificationUtil.sendEventNotification(eventRequest);
                    }
                }}
            if(configuredChannelNames.contains(CHANNEL_NAME_SMS)){
                if (config.getIsSMSEnabled() != null && config.getIsSMSEnabled()) {
                    List<SMSRequest> smsRequests = getSmsRequest(request, topic, property, applicationStatus);
                    if (!CollectionUtils.isEmpty(smsRequests)) {
                        notificationUtil.sendSMS(smsRequests);
                    }
                }}
            if(configuredChannelNames.contains(CHANNEL_NAME_EMAIL)){
                if (config.getIsEmailNotificationEnabled() != null && config.getIsEmailNotificationEnabled()) {
                    List<EmailRequest> emailRequests = getEmailRequest(request, topic, property, applicationStatus);
                    if (!CollectionUtils.isEmpty(emailRequests)) {
                        notificationUtil.sendEmail(emailRequests);
                    }
                }}
        } catch (Exception ex) {
            log.error("Error occured while processing the record from topic : " + topic, ex);
        }

    }

    /**
     *
     * @param request Water Connection Request
     * @param topic Topic Name
     * @param property Property Object
     * @param applicationStatus Application Status
     * @return EventRequest Object
     */
    private EventRequest getEventRequest(GarbageConnectionRequest request, String topic, Property property, String applicationStatus) {
        String localizationMessage = notificationUtil
                .getLocalizationMessages(property.getTenantId(), request.getRequestInfo());
        ProcessInstance workflow = request.getGarbageConnection().getProcessInstance();

        int reqType = GCConstants.UPDATE_APPLICATION;
        if ((!workflow.getAction().equalsIgnoreCase(GCConstants.ACTIVATE_CONNECTION))
                && gcServicesUtil.isModifyConnectionRequestForNotification(request)) {
            reqType = GCConstants.MODIFY_CONNECTION;
        }
        if((!workflow.getAction().equalsIgnoreCase(GCConstants.ACTIVATE_CONNECTION)) &&
            (!workflow.getAction().equalsIgnoreCase(APPROVE_CONNECTION)) &&
                gcServicesUtil.isDisconnectConnectionRequest(request))
        {
            reqType = DISCONNECT_CONNECTION;
        }
        
        if(workflow.getAction().equalsIgnoreCase(GCConstants.ACTIVATE_CONNECTION) &&
                    gcServicesUtil.isReconnectConnectionRequest(request))
            {
                reqType = RECONNECTION ;
            }

        String message = notificationUtil.getCustomizedMsgForInApp(workflow.getAction(), applicationStatus,
                localizationMessage, reqType);
        if(workflow.getAction().equalsIgnoreCase(APPROVE_DISCONNECTION_CONST) && workflow.getComment()!=null
                && workflow.getComment().contains(WORKFLOW_NO_PAYMENT_CODE))
        {
            message = notificationUtil.getCustomizedMsgForInApp(workflow.getAction(), PENDING_FOR_DISCONNECTION_EXECUTION_STATUS_CODE,
                    localizationMessage, reqType);
        }
        if(workflow.getAction().equalsIgnoreCase(ACTION_PAY) && workflow.getComment()!=null
                && workflow.getComment().contains(WORKFLOW_NO_PAYMENT_CODE))
        {
           log.info("Skipping for action status -> "+workflow.getAction().equalsIgnoreCase(ACTION_PAY)+"_"+request.getGarbageConnection().getApplicationStatus()
                   +" because -> "+workflow.getComment());
           return null;
        }

        if (message == null) {
            log.info("No message Found For Topic : " + topic);
            return null;
        }

        Map<String, String> mobileNumbersAndNames = new HashMap<>();
        Map<String, String> mapOfPhoneNoAndUUIDs = new HashMap<>();

        Set<String> ownersMobileNumbers = new HashSet<>();
        //Send the notification to all owners
        property.getOwners().forEach(owner -> {
            if (owner.getMobileNumber() != null)
                ownersMobileNumbers.add(owner.getMobileNumber());
        });

        //send the notification to the connection holders
        if (!CollectionUtils.isEmpty(request.getGarbageConnection().getConnectionHolders())) {
            request.getGarbageConnection().getConnectionHolders().forEach(holder -> {
                if (!StringUtils.isEmpty(holder.getMobileNumber())) {
                    ownersMobileNumbers.add(holder.getMobileNumber());
                }
            });
        }

        for (String mobileNumber : ownersMobileNumbers) {
            UserDetailResponse userDetailResponse = fetchUserByUsername(mobileNumber, request.getRequestInfo(), request.getGarbageConnection().getTenantId());
            if (!CollectionUtils.isEmpty(userDetailResponse.getUser())) {
                OwnerInfo user = userDetailResponse.getUser().get(0);
                mobileNumbersAndNames.put(user.getMobileNumber(), user.getName());
                mapOfPhoneNoAndUUIDs.put(user.getMobileNumber(), user.getUuid());
            } else {
                log.info("No User for mobile {} skipping event", mobileNumber);
            }
        }

        //Send the notification to applicant
        if (!StringUtils.isEmpty(request.getRequestInfo().getUserInfo().getMobileNumber())) {
            mobileNumbersAndNames.put(request.getRequestInfo().getUserInfo().getMobileNumber(), request.getRequestInfo().getUserInfo().getName());
            mapOfPhoneNoAndUUIDs.put(request.getRequestInfo().getUserInfo().getMobileNumber(), request.getRequestInfo().getUserInfo().getUuid());
        }


        Map<String, String> mobileNumberAndMessage = getMessageForMobileNumber(mobileNumbersAndNames, request,
                message, property);
        if (message.contains("{receipt download link}"))
            mobileNumberAndMessage = setRecepitDownloadLink(mobileNumberAndMessage, request, message, property);
        Set<String> mobileNumbers = mobileNumberAndMessage.keySet().stream().collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(mapOfPhoneNoAndUUIDs.keySet())) {
            log.info("UUID search failed here !");
        }
        List<Event> events = new ArrayList<>();
        for (String mobile : mobileNumbers) {
            if (null == mapOfPhoneNoAndUUIDs.get(mobile) || null == mobileNumberAndMessage.get(mobile)) {
                log.error("No UUID/SMS for mobile {} skipping event", mobile);
                continue;
            }
            List<String> toUsers = new ArrayList<>();
            toUsers.add(mapOfPhoneNoAndUUIDs.get(mobile));
            Recepient recepient = Recepient.builder().toUsers(toUsers).toRoles(null).build();
            // List<String> payTriggerList =
            // Arrays.asList(config.getPayTriggers().split("[,]"));

            Action action = getActionForEventNotification(mobileNumberAndMessage, mobile, request, property);
            events.add(Event.builder().tenantId(property.getTenantId())
                    .description(mobileNumberAndMessage.get(mobile)).eventType(GCConstants.USREVENTS_EVENT_TYPE)
                    .name(GCConstants.USREVENTS_EVENT_NAME).postedBy(GCConstants.USREVENTS_EVENT_POSTEDBY)
                    .source(Source.WEBAPP).recepient(recepient).eventDetails(null).actions(action).build());
        }
        if (!CollectionUtils.isEmpty(events)) {
            return EventRequest.builder().requestInfo(request.getRequestInfo()).events(events).build();
        } else {
            return null;
        }
    }

    /**
     *
     * @param mobileNumberAndMessage List of Mobile Numbers and messages
     * @param mobileNumber MobileNumber
     * @param connectionRequest Connection Request
     * @param property Property
     * @return return action link
     */
    public Action getActionForEventNotification(Map<String, String> mobileNumberAndMessage,
                                                String mobileNumber, GarbageConnectionRequest connectionRequest, Property property) {
        String messageTemplate = mobileNumberAndMessage.get(mobileNumber);
        List<ActionItem> items = new ArrayList<>();
        if (messageTemplate.contains("{Action Button}")) {
            String code = StringUtils.substringBetween(messageTemplate, "{Action Button}", "{/Action Button}");
            messageTemplate = messageTemplate.replace("{Action Button}", "");
            messageTemplate = messageTemplate.replace("{/Action Button}", "");
            messageTemplate = messageTemplate.replace(code, "");
            String actionLink = "";
            if (code.equalsIgnoreCase("Download Application")) {
                actionLink = config.getNotificationUrl() + config.getViewHistoryLink();
                actionLink = actionLink.replace(applicationNumberReplacer, connectionRequest.getGarbageConnection().getApplicationNo());
            }
            if (code.equalsIgnoreCase("PAY NOW")||code.equalsIgnoreCase("Pay Dues")) {
                actionLink = config.getNotificationUrl() + config.getViewHistoryLink();
                actionLink = actionLink.replace(applicationNumberReplacer, connectionRequest.getGarbageConnection().getApplicationNo());
            }
            if (code.equalsIgnoreCase("DOWNLOAD RECEIPT")) {
                actionLink = config.getNotificationUrl() + config.getMyPaymentsLink();
            }
            if (code.equalsIgnoreCase("View History Link")) {
                actionLink = config.getNotificationUrl() + config.getViewHistoryLink();
                actionLink = actionLink.replace(mobileNoReplacer, mobileNumber);
                actionLink = actionLink.replace(applicationNumberReplacer, connectionRequest.getGarbageConnection().getApplicationNo());
                actionLink = actionLink.replace(tenantIdReplacer, property.getTenantId());
            }
            if (code.equalsIgnoreCase("Connection Detail Page")) {
                actionLink = config.getNotificationUrl() + config.getConnectionDetailsLink();
                actionLink = actionLink.replace(applicationNumberReplacer, connectionRequest.getGarbageConnection().getApplicationNo());
            }
            ActionItem item = ActionItem.builder().actionUrl(actionLink).code(code).build();
            items.add(item);
            mobileNumberAndMessage.replace(mobileNumber, messageTemplate);
        }
        return Action.builder().actionUrls(items).build();
    }

    /**
     *
     * @param garbageConnectionRequest Water Connection Request
     * @param topic Topic Name
     * @param property Property Object
     * @param applicationStatus Application Status
     * @return Returns list of SMSRequest
     */
    private List<SMSRequest> getSmsRequest(GarbageConnectionRequest garbageConnectionRequest, String topic,
                                           Property property, String applicationStatus) {
        String localizationMessage = notificationUtil.getLocalizationMessages(property.getTenantId(),
                garbageConnectionRequest.getRequestInfo());
        ProcessInstance workflow = garbageConnectionRequest.getGarbageConnection().getProcessInstance();

        int reqType = GCConstants.UPDATE_APPLICATION;
        if ((!workflow.getAction().equalsIgnoreCase(GCConstants.ACTIVATE_CONNECTION))
                && gcServicesUtil.isModifyConnectionRequestForNotification(garbageConnectionRequest)) {
            reqType = GCConstants.MODIFY_CONNECTION;
        }
        if((!workflow.getAction().equalsIgnoreCase(GCConstants.ACTIVATE_CONNECTION)) &&
                (!workflow.getAction().equalsIgnoreCase(APPROVE_CONNECTION)) &&
                gcServicesUtil.isDisconnectConnectionRequest(garbageConnectionRequest))
        {
            reqType = DISCONNECT_CONNECTION;
        }

        if((workflow.getAction().equalsIgnoreCase(GCConstants.ACTIVATE_CONNECTION)) &&
        		gcServicesUtil.isReconnectConnectionRequest(garbageConnectionRequest))
        {
        	reqType = RECONNECTION;
        }
        
        String message = notificationUtil.getCustomizedMsgForSMS(
                workflow.getAction(), applicationStatus,
                localizationMessage, reqType);
        if(workflow.getAction().equalsIgnoreCase(APPROVE_DISCONNECTION_CONST) && workflow.getComment()!=null
                && workflow.getComment().contains(WORKFLOW_NO_PAYMENT_CODE))
        {
            message = notificationUtil.getCustomizedMsgForSMS(workflow.getAction(), PENDING_FOR_DISCONNECTION_EXECUTION_STATUS_CODE,
                    localizationMessage, reqType);
        }
        if(workflow.getAction().equalsIgnoreCase(ACTION_PAY) && workflow.getComment()!=null
                && workflow.getComment().contains(WORKFLOW_NO_PAYMENT_CODE))
        {
            log.info("Skipping for action status -> "+workflow.getAction().equalsIgnoreCase(ACTION_PAY)+"_"+garbageConnectionRequest.getGarbageConnection().getApplicationStatus()
                    +" because -> "+workflow.getComment());
            return Collections.emptyList();
        }
        if (message == null) {
            log.info("No message Found For Topic : " + topic);
            return Collections.emptyList();
        }
        log.info("SMS body is "+message);

           //Send the notification to all owners
            Map<String, String> mobileNumbersAndNames = new HashMap<>();
            property.getOwners().forEach(owner -> {
                if (owner.getMobileNumber() != null)
                {
                    mobileNumbersAndNames.put(owner.getMobileNumber(),owner.getName());
                }
            });

            //send the notification to the connection holders
            if (!CollectionUtils.isEmpty(garbageConnectionRequest.getGarbageConnection().getConnectionHolders())) {
                garbageConnectionRequest.getGarbageConnection().getConnectionHolders().forEach(holder -> {
                    if (!StringUtils.isEmpty(holder.getMobileNumber())) {
                        mobileNumbersAndNames.put(holder.getMobileNumber(),holder.getName());
                    }
                });
            }

            
            //Send the notification to applicant
            if(!StringUtils.isEmpty(garbageConnectionRequest.getRequestInfo().getUserInfo().getMobileNumber())&& garbageConnectionRequest.getRequestInfo().getUserInfo().getType().equalsIgnoreCase("CITIZEN"))
            {
                mobileNumbersAndNames.put(garbageConnectionRequest.getRequestInfo().getUserInfo().getMobileNumber(), garbageConnectionRequest.getRequestInfo().getUserInfo().getName());
            }


        Map<String, String> mobileNumberAndMessage = getMessageForMobileNumber(mobileNumbersAndNames,
                garbageConnectionRequest, message, property);
        if (message.contains("{receipt download link}"))
            mobileNumberAndMessage = setRecepitDownloadLink(mobileNumberAndMessage, garbageConnectionRequest, message, property);
        List<SMSRequest> smsRequest = new ArrayList<>();
        mobileNumberAndMessage.forEach((mobileNumber, msg) -> {
            SMSRequest req = SMSRequest.builder().mobileNumber(mobileNumber).message(msg).category(Category.NOTIFICATION).build();
            smsRequest.add(req);
        });
        return smsRequest;
    }

    /**
     * Creates email request for each owner
     *
     * @param garbageConnectionRequest Water Connection Request
     * @param topic Topic Name
     * @param property Property Object
     * @param applicationStatus Application Status
     * @return List of EmailRequest
     */
    private List<EmailRequest> getEmailRequest(GarbageConnectionRequest garbageConnectionRequest, String topic,
                                           Property property, String applicationStatus) {
        String localizationMessage = notificationUtil.getLocalizationMessages(property.getTenantId(),
                garbageConnectionRequest.getRequestInfo());
        ProcessInstance workflow = garbageConnectionRequest.getGarbageConnection().getProcessInstance();

        int reqType = GCConstants.UPDATE_APPLICATION;
        if ((!workflow.getAction().equalsIgnoreCase(GCConstants.ACTIVATE_CONNECTION))
                && gcServicesUtil.isModifyConnectionRequestForNotification(garbageConnectionRequest)) {
            reqType = GCConstants.MODIFY_CONNECTION;
        }
        if((!workflow.getAction().equalsIgnoreCase(GCConstants.ACTIVATE_CONNECTION)) &&
                (!workflow.getAction().equalsIgnoreCase(APPROVE_CONNECTION)) &&
                gcServicesUtil.isDisconnectConnectionRequest(garbageConnectionRequest))
        {
            reqType = DISCONNECT_CONNECTION;
        }

        if((workflow.getAction().equalsIgnoreCase(GCConstants.ACTIVATE_CONNECTION)) &&
        		gcServicesUtil.isReconnectConnectionRequest(garbageConnectionRequest))
        {
        	reqType = RECONNECTION;
        }
        
        String message = notificationUtil.getCustomizedMsgForEmail(
                workflow.getAction(), applicationStatus,
                localizationMessage, reqType);
        if(workflow.getAction().equalsIgnoreCase(APPROVE_DISCONNECTION_CONST) && workflow.getComment()!=null
                && workflow.getComment().contains(WORKFLOW_NO_PAYMENT_CODE))
        {
            message = notificationUtil.getCustomizedMsgForEmail(workflow.getAction(), PENDING_FOR_DISCONNECTION_EXECUTION_STATUS_CODE,
                    localizationMessage, reqType);
        }
        if(workflow.getAction().equalsIgnoreCase(ACTION_PAY) && workflow.getComment()!=null
                && workflow.getComment().contains(WORKFLOW_NO_PAYMENT_CODE))
        {
            log.info("Skipping for action status -> "+workflow.getAction().equalsIgnoreCase(ACTION_PAY)+"_"+garbageConnectionRequest.getGarbageConnection().getApplicationStatus()
                    +" because -> "+workflow.getComment());
            return Collections.emptyList();
        }
        if (message == null) {
            log.info("No message Found For Topic : " + topic);
            return Collections.emptyList();
        }

        //Send the notification to all owners
        Set<String> ownersUuids = new HashSet<>();

        property.getOwners().forEach(owner -> {
            if (owner.getUuid() != null)
                ownersUuids.add(owner.getUuid());
        });

        //send the notification to the connection holders
        if (!CollectionUtils.isEmpty(garbageConnectionRequest.getGarbageConnection().getConnectionHolders())) {
            garbageConnectionRequest.getGarbageConnection().getConnectionHolders().forEach(holder -> {
                if (!StringUtils.isEmpty(holder.getUuid())) {
                    ownersUuids.add(holder.getUuid());
                }
            });
        }

        UserDetailResponse userDetailResponse = fetchUserByUUID(ownersUuids,garbageConnectionRequest.getRequestInfo(),garbageConnectionRequest.getGarbageConnection().getTenantId());
        Map<String, String> mobileNumbersAndNames = new HashMap<>();
        for(OwnerInfo user:userDetailResponse.getUser())
        {
            mobileNumbersAndNames.put(user.getMobileNumber(),user.getName());
        }

        Set<String> mobileNumbers = new HashSet<String>();
        mobileNumbers.addAll(mobileNumbersAndNames.keySet());

        Map<String,String> mobileNumberAndEmailId = new HashMap<>();
        for(OwnerInfo user:userDetailResponse.getUser()) {
            mobileNumberAndEmailId.put(user.getMobileNumber(), user.getEmailId());
        }

        //Send the notification to applicant
        if(!StringUtils.isEmpty(garbageConnectionRequest.getRequestInfo().getUserInfo().getMobileNumber()))
        {
            mobileNumbersAndNames.put(garbageConnectionRequest.getRequestInfo().getUserInfo().getMobileNumber(), garbageConnectionRequest.getRequestInfo().getUserInfo().getName());
            mobileNumbers.add(garbageConnectionRequest.getRequestInfo().getUserInfo().getMobileNumber());
            mobileNumberAndEmailId.put(garbageConnectionRequest.getRequestInfo().getUserInfo().getMobileNumber(), garbageConnectionRequest.getRequestInfo().getUserInfo().getEmailId());
        }

        Map<String, String> mobileNumberAndMessage = getMessageForMobileNumber(mobileNumbersAndNames,
                garbageConnectionRequest, message, property);

        if (message.contains("{receipt download link}"))
            mobileNumberAndMessage = setRecepitDownloadLink(mobileNumberAndMessage, garbageConnectionRequest, message, property);

        List<EmailRequest> emailRequest = new LinkedList<>();
        for (Map.Entry<String, String> entryset : mobileNumberAndEmailId.entrySet()) {
            String customizedMsg = mobileNumberAndMessage.get(entryset.getKey());
            String subject = customizedMsg.substring(customizedMsg.indexOf("<h2>")+4,customizedMsg.indexOf("</h2>"));
            String body = customizedMsg.substring(customizedMsg.indexOf("</h2>")+5);
            Email emailobj = Email.builder().emailTo(Collections.singleton(entryset.getValue())).isHTML(true).body(body).subject(subject).build();
            EmailRequest email = new EmailRequest(garbageConnectionRequest.getRequestInfo(),emailobj);
            emailRequest.add(email);
        }
        return emailRequest;

    }

    public Map<String, String> getMessageForMobileNumber(Map<String, String> mobileNumbersAndNames,
                                                         GarbageConnectionRequest garbageConnectionRequest, String message, Property property) {
        Map<String, String> messageToReturn = new HashMap<>();
        for (Entry<String, String> mobileAndName : mobileNumbersAndNames.entrySet()) {
            String messageToReplace = message;
            Boolean isConnectionNoPresent = !StringUtils.isEmpty(garbageConnectionRequest.getGarbageConnection().getConnectionNo());

            if (messageToReplace.contains("{Owner Name}"))
                messageToReplace = messageToReplace.replace("{Owner Name}", mobileAndName.getValue());
            if (messageToReplace.contains("{Service}"))
                messageToReplace = messageToReplace.replace("{Service}", GCConstants.SERVICE_FIELD_VALUE_NOTIFICATION);

//            if (messageToReplace.contains("{Plumb Info}"))
//                messageToReplace = getMessageForPlumberInfo(garbageConnectionRequest.getGarbageConnection(), messageToReplace);

            if (messageToReplace.contains("{SLA}"))
                messageToReplace = messageToReplace.replace("{SLA}", getSLAForState(garbageConnectionRequest, property, config.getBusinessServiceValue()));

            if (messageToReplace.contains("{Application number}"))
                messageToReplace = messageToReplace.replace("{Application number}", garbageConnectionRequest.getGarbageConnection().getApplicationNo());

            if (messageToReplace.contains("{Connection number}"))
                messageToReplace = messageToReplace.replace("{Connection number}", isConnectionNoPresent ? garbageConnectionRequest.getGarbageConnection().getConnectionNo() : "NA");

            if(messageToReplace.contains("{Reason for Rejection}"))
                messageToReplace = messageToReplace.replace("{Reason for Rejection}",  garbageConnectionRequest.getGarbageConnection().getProcessInstance().getComment());

            if (messageToReplace.contains("{Application download link}")) {
                String actionLink = config.getNotificationUrl() + config.getViewHistoryLink();
                actionLink = actionLink.replace(applicationNumberReplacer, garbageConnectionRequest.getGarbageConnection().getApplicationNo());
                messageToReplace = messageToReplace.replace("{Application download link}", gcServicesUtil.getShortnerURL(actionLink));
            }

            if (messageToReplace.contains("{mseva URL}"))
                messageToReplace = messageToReplace.replace("{mseva URL}",
                        gcServicesUtil.getShortnerURL(config.getNotificationUrl()));

            if (messageToReplace.contains("{mseva app link}"))
                messageToReplace = messageToReplace.replace("{mseva app link}",
                        gcServicesUtil.getShortnerURL(config.getMSevaAppLink()));

            if (messageToReplace.contains("{View History Link}")) {
                String historyLink = config.getNotificationUrl() + config.getViewHistoryLink();
                historyLink = historyLink.replace(mobileNoReplacer, mobileAndName.getKey());
                historyLink = historyLink.replace(applicationNumberReplacer, garbageConnectionRequest.getGarbageConnection().getApplicationNo());
                historyLink = historyLink.replace(tenantIdReplacer, property.getTenantId());
                messageToReplace = messageToReplace.replace("{View History Link}",
                        gcServicesUtil.getShortnerURL(historyLink));
            }
            if (messageToReplace.contains("{payment link}")) {
                String paymentLink = config.getNotificationUrl() +  config.getViewHistoryLink();
                paymentLink = paymentLink.replace(mobileNoReplacer, mobileAndName.getKey());
                paymentLink = paymentLink.replace(applicationNumberReplacer, garbageConnectionRequest.getGarbageConnection().getApplicationNo());
                paymentLink = paymentLink.replace(tenantIdReplacer, property.getTenantId());
                messageToReplace = messageToReplace.replace("{payment link}",
                        gcServicesUtil.getShortnerURL(paymentLink));
            }
			/*if (messageToReplace.contains("{receipt download link}")){
				messageToReplace = messageToReplace.replace("{receipt download link}",
						gcServicesUtil.getShortnerURL(config.getNotificationUrl()));
			}*/
            if (messageToReplace.contains("{connection details page}")) {
                String connectionDetaislLink = config.getNotificationUrl() + config.getConnectionDetailsLink();
                connectionDetaislLink = connectionDetaislLink.replace(applicationNumberReplacer,
                        garbageConnectionRequest.getGarbageConnection().getApplicationNo());
                messageToReplace = messageToReplace.replace("{connection details page}",
                        gcServicesUtil.getShortnerURL(connectionDetaislLink));
            }
            if (messageToReplace.contains("{Date effective from}")) {
                if (garbageConnectionRequest.getGarbageConnection().getDateEffectiveFrom() != null) {
                    LocalDate date = Instant
                            .ofEpochMilli(garbageConnectionRequest.getGarbageConnection().getDateEffectiveFrom() > 10
                                    ? garbageConnectionRequest.getGarbageConnection().getDateEffectiveFrom()
                                    : garbageConnectionRequest.getGarbageConnection().getDateEffectiveFrom() * 1000)
                            .atZone(ZoneId.systemDefault()).toLocalDate();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    messageToReplace = messageToReplace.replace("{Date effective from}", date.format(formatter));
                } else {
                    messageToReplace = messageToReplace.replace("{Date effective from}", "");
                }
            }
            messageToReturn.put(mobileAndName.getKey(), messageToReplace);
        }
        return messageToReturn;
    }

    /**
     * This method returns message to replace for plumber info depending upon
     * whether the plumber info type is either SELF or ULB
     *
     * @param waterConnection Water Connection Object
     * @param messageTemplate Message Template
     * @return updated messageTemplate
     */

//    @SuppressWarnings("unchecked")
//    public String getMessageForPlumberInfo(GarbageConnection waterConnection, String messageTemplate) {
//        HashMap<String, Object> addDetail = mapper.convertValue(waterConnection.getAdditionalDetails(),
//                HashMap.class);
//        if(!StringUtils.isEmpty(String.valueOf(addDetail.get(GCConstants.DETAILS_PROVIDED_BY)))){
//            String detailsProvidedBy = String.valueOf(addDetail.get(GCConstants.DETAILS_PROVIDED_BY));
//            if ( StringUtils.isEmpty(detailsProvidedBy) || detailsProvidedBy.equalsIgnoreCase(GCConstants.SELF)) {
//                String code = StringUtils.substringBetween(messageTemplate, "{Plumb Info}", "{/Plumb Info}");
//                messageTemplate = messageTemplate.replace("{Plumb Info}", "");
//                messageTemplate = messageTemplate.replace("{/Plumb Info}", "");
//                messageTemplate = messageTemplate.replace(code, "");
//            } else {
//                messageTemplate = messageTemplate.replace("{Plumb Info}", "").replace("{/Plumb Info}", "");
//                messageTemplate = messageTemplate.replace("{Plumb name}",
//                        StringUtils.isEmpty(waterConnection.getPlumberInfo().get(0).getName()) ? ""
//                                : waterConnection.getPlumberInfo().get(0).getName());
//                messageTemplate = messageTemplate.replace("{Plumb Licence No.}",
//                        StringUtils.isEmpty(waterConnection.getPlumberInfo().get(0).getLicenseNo()) ? ""
//                                : waterConnection.getPlumberInfo().get(0).getLicenseNo());
//                messageTemplate = messageTemplate.replace("{Plumb Mobile No.}",
//                        StringUtils.isEmpty(waterConnection.getPlumberInfo().get(0).getMobileNumber()) ? ""
//                                : waterConnection.getPlumberInfo().get(0).getMobileNumber());
//            }
//
//        }else{
//            String code = StringUtils.substringBetween(messageTemplate, "{Plumb Info}", "{/Plumb Info}");
//            messageTemplate = messageTemplate.replace("{Plumb Info}", "");
//            messageTemplate = messageTemplate.replace("{/Plumb Info}", "");
//            messageTemplate = messageTemplate.replace(code, "");
//        }
//        return messageTemplate;

//    }

    /**
     * Fetches SLA of CITIZEN based on the phone number.
     *
     * @param connectionRequest Water Connection Request
     * @param property Property
     * @return string consisting SLA
     */

    public String getSLAForState(GarbageConnectionRequest connectionRequest, Property property, String businessServiceName) {
        String resultSla = "";
        BusinessService businessService = workflowService.getBusinessService(property.getTenantId(),
                connectionRequest.getRequestInfo(), businessServiceName);
        if (businessService != null && businessService.getStates() != null && businessService.getStates().size() > 0) {
            for (State state : businessService.getStates()) {
                // PENDING_FOR_CONNECTION_ACTIVATION removed from NewGC; use PENDING_FOR_PAYMENT SLA
                if (GCConstants.PENDING_FOR_PAYMENT_STATUS_CODE.equalsIgnoreCase(state.getState())) {
                    resultSla = String.valueOf((state.getSla() == null ? 0L : state.getSla()) / 86400000);
                }
            }
        }
        return resultSla;
    }


    /**
     * Fetches UUIDs of CITIZEN based on the phone number.
     *
     * @param mobileNumbers - List of Mobile Numbers
     * @param requestInfo - Request Information
     * @param tenantId - Tenant Id
     * @return Returns List of MobileNumbers and UUIDs
     */
    public Map<String, String> fetchUserUUIDs(Set<String> mobileNumbers, RequestInfo requestInfo, String tenantId) {
        Map<String, String> mapOfPhoneNoAndUUIDs = new HashMap<>();
        StringBuilder uri = new StringBuilder();
        uri.append(config.getUserHost()).append(config.getUserSearchEndpoint());
        Map<String, Object> userSearchRequest = new HashMap<>();
        userSearchRequest.put("RequestInfo", requestInfo);
        userSearchRequest.put("tenantId", tenantId);
        userSearchRequest.put("userType", "CITIZEN");
        for(String mobileNo: mobileNumbers) {
            userSearchRequest.put("userName", mobileNo);
            try {
                Object user = serviceRequestRepository.fetchResult(uri, userSearchRequest);
                if(null != user) {
                    String uuid = JsonPath.read(user, "$.user[0].uuid");
                    mapOfPhoneNoAndUUIDs.put(mobileNo, uuid);
                }else {
                    log.error("Service returned null while fetching user for username - "+mobileNo);
                }
            }catch(Exception e) {
                log.error("Exception while fetching user for username - "+mobileNo);
                log.error("Exception trace: ",e);
            }
        }
        return mapOfPhoneNoAndUUIDs;
    }

    /**
     * Fetch URL for application download link
     *
     * @param garbageConnectionRequest Water Connection Request
     * @param property Property
     * @return application download link
     */
    private String getApplicationDownloadLink(GarbageConnectionRequest garbageConnectionRequest, Property property) {
        CalculationCriteria criteria = CalculationCriteria.builder().applicationNo(garbageConnectionRequest.getGarbageConnection().getApplicationNo())
                .garbageConnection(garbageConnectionRequest.getGarbageConnection()).tenantId(property.getTenantId()).build();
        CalculationReq calRequest = CalculationReq.builder().calculationCriteria(Arrays.asList(criteria))
                .requestInfo(garbageConnectionRequest.getRequestInfo()).isconnectionCalculation(false).isDisconnectionRequest(false).isReconnectionRequest(false).build();
        try {
            Object response = serviceRequestRepository.fetchResult(gcServicesUtil.getEstimationURL(), calRequest);
            CalculationRes calResponse = mapper.convertValue(response, CalculationRes.class);
            JSONObject waterObject = mapper.convertValue(garbageConnectionRequest.getGarbageConnection(), JSONObject.class);
            if (CollectionUtils.isEmpty(calResponse.getCalculation())) {
                throw new CustomException("NO_ESTIMATION_FOUND", "Estimation not found!!!");
            }
            waterObject.put(totalAmount, calResponse.getCalculation().get(0).getTotalAmount());
            waterObject.put(applicationFee, calResponse.getCalculation().get(0).getFee());
            waterObject.put(serviceFee, calResponse.getCalculation().get(0).getCharge());
            waterObject.put(tax, calResponse.getCalculation().get(0).getTaxAmount());
            waterObject.put(propertyKey, property);
            String tenantId = property.getTenantId().split("\\.")[0];
            String fileStoreId = getFielStoreIdFromPDFService(waterObject, garbageConnectionRequest.getRequestInfo(), tenantId);
            return getApplicationDownloadLink(tenantId, fileStoreId);
        } catch (Exception ex) {
            log.error("Calculation response error!!", ex);
            throw new CustomException("WATER_CALCULATION_EXCEPTION", "Calculation response can not parsed!!!");
        }
    }
    /**
     * Get file store id from PDF service
     *
     * @param waterObject Water Connection Object
     * @param requestInfo Request Information
     * @param tenantId Tenant Id
     * @return file store id
     */
    private String getFielStoreIdFromPDFService(JSONObject waterObject, RequestInfo requestInfo, String tenantId) {
        JSONArray waterConnectionList = new JSONArray();
        waterConnectionList.add(waterObject);
        JSONObject requestPayload = new JSONObject();
        requestPayload.put(requestInfoReplacer, requestInfo);
        requestPayload.put(WaterConnectionReplacer, waterConnectionList);
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(config.getPdfServiceHost());
            String pdfLink = config.getPdfServiceLink();
            pdfLink = pdfLink.replace(tenantIdReplacer, tenantId).replace(applicationKey, GCConstants.PDF_APPLICATION_KEY);
            builder.append(pdfLink);
            Object response = serviceRequestRepository.fetchResult(builder, requestPayload);
            DocumentContext responseContext = JsonPath.parse(response);
            List<Object> fileStoreIds = responseContext.read("$.filestoreIds");
            if(CollectionUtils.isEmpty(fileStoreIds)) {
                throw new CustomException("EMPTY_FILESTORE_IDS_FROM_PDF_SERVICE", "File Store Id doesn't exist in pdf service");
            }
            return fileStoreIds.get(0).toString();
        } catch (Exception ex) {
            log.error("PDF file store id response error!!", ex);
            throw new CustomException("WATER_FILESTORE_PDF_EXCEPTION", "PDF response can not parsed!!!");
        }
    }

    /**
     *
     * @param tenantId TenantId
     * @param fileStoreId File Store Id
     * @return file store id
     */
    private String getApplicationDownloadLink(String tenantId, String fileStoreId) {
        String fileStoreServiceLink = config.getFileStoreHost() + config.getFileStoreLink();
        fileStoreServiceLink = fileStoreServiceLink.replace(tenantIdReplacer, tenantId);
        fileStoreServiceLink = fileStoreServiceLink.replace(fileStoreIdReplacer, fileStoreId);
        try {
            Object response = serviceRequestRepository.fetchResultUsingGet(new StringBuilder(fileStoreServiceLink));
            DocumentContext responseContext = JsonPath.parse(response);
            List<Object> fileStoreIds = responseContext.read("$.fileStoreIds");
            if (CollectionUtils.isEmpty(fileStoreIds)) {
                throw new CustomException("EMPTY_FILESTORE_IDS_FROM_PDF_SERVICE",
                        "NO file store id found from pdf service");
            }
            JSONObject obje = mapper.convertValue(fileStoreIds.get(0), JSONObject.class);
            return obje.get(urlReplacer).toString();
        } catch (Exception ex) {
            log.error("PDF file store id response error!!", ex);
            throw new CustomException("WATER_FILESTORE_PDF_EXCEPTION", "PDF response can not parsed!!!");
        }
    }

    public Map<String, String> setRecepitDownloadLink(Map<String, String> mobileNumberAndMessage,
                                                      GarbageConnectionRequest garbageConnectionRequest, String message, Property property) {

        Map<String, String> messageToReturn = new HashMap<>();
        String receiptNumber = getReceiptNumber(garbageConnectionRequest);
        for (Entry<String, String> mobileAndMsg : mobileNumberAndMessage.entrySet()) {
            String messageToReplace = mobileAndMsg.getValue();
            String link = config.getNotificationUrl() + config.getMyPaymentsLink();
            link = gcServicesUtil.getShortnerURL(link);
            messageToReplace = messageToReplace.replace("{receipt download link}", link);
            messageToReturn.put(mobileAndMsg.getKey(), messageToReplace);

        }
        return messageToReturn;

    }

    public String getReceiptNumber(GarbageConnectionRequest garbageConnectionRequest){
        String consumerCode,service;
        if(StringUtils.isEmpty(garbageConnectionRequest.getGarbageConnection().getConnectionNo())){
            consumerCode = garbageConnectionRequest.getGarbageConnection().getApplicationNo();
            service = businessService;
        }
        else{
            consumerCode = garbageConnectionRequest.getGarbageConnection().getConnectionNo();
            service = "GC";
        }
        StringBuilder URL = gcServicesUtil.getcollectionURL();
        URL.append(service).append("/_search").append("?").append("consumerCodes=").append(consumerCode)
                .append("&").append("tenantId=").append(garbageConnectionRequest.getGarbageConnection().getTenantId());
        RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(garbageConnectionRequest.getRequestInfo()).build();
        Object response = serviceRequestRepository.fetchResult(URL,requestInfoWrapper);
        PaymentResponse paymentResponse = mapper.convertValue(response, PaymentResponse.class);
        return paymentResponse.getPayments().get(0).getPaymentDetails().get(0).getReceiptNumber();
    }

    /**
     * Fetches User Object based on the UUID.
     *
     * @param uuids - set of UUIDs of User
     * @param requestInfo - Request Info Object
     * @param tenantId - Tenant Id
     * @return - Returns User object with given UUID
     */
    public UserDetailResponse fetchUserByUUID(Set<String> uuids, RequestInfo requestInfo, String tenantId) {
        User userInfoCopy = requestInfo.getUserInfo();

        User userInfo = notificationUtil.getInternalMicroserviceUser(tenantId);
        requestInfo.setUserInfo(userInfo);

        UserSearchRequest userSearchRequest = userService.getBaseUserSearchRequest(tenantId, requestInfo);
        userSearchRequest.setUuid(uuids);

        UserDetailResponse userDetailResponse = userService.getUser(userSearchRequest);
        requestInfo.setUserInfo(userInfoCopy);
        return userDetailResponse;
    }

    /**
     * Fetches User Object based on the UUID.
     *
     * @param username - username of User
     * @param requestInfo - Request Info Object
     * @param tenantId - Tenant Id
     * @return - Returns User object with given UUID
     */
    public UserDetailResponse fetchUserByUsername(String username, RequestInfo requestInfo, String tenantId) {
        User userInfoCopy = requestInfo.getUserInfo();

        User userInfo = notificationUtil.getInternalMicroserviceUser(tenantId);
        requestInfo.setUserInfo(userInfo);

        UserSearchRequest userSearchRequest = userService.getBaseUserSearchRequest(tenantId, requestInfo);
        userSearchRequest.setUserName(username);

        UserDetailResponse userDetailResponse = userService.getUser(userSearchRequest);
        requestInfo.setUserInfo(userInfoCopy);
        return userDetailResponse;
    }

}
