package org.upyog.pgrai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.common.utils.MultiStateInstanceUtil;
import org.upyog.pgrai.config.PGRConfiguration;
import org.upyog.pgrai.repository.ServiceRequestRepository;
import org.upyog.pgrai.util.HRMSUtil;
import org.upyog.pgrai.util.MDMSUtils;
import org.upyog.pgrai.util.NotificationUtil;
import org.upyog.pgrai.web.models.notification.*;
import org.upyog.pgrai.web.models.ServiceWrapper;
import org.upyog.pgrai.web.models.RequestInfoWrapper;
import org.upyog.pgrai.web.models.ServiceRequest;
import org.upyog.pgrai.web.models.workflow.ProcessInstance;
import org.upyog.pgrai.web.models.workflow.ProcessInstanceResponse;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.upyog.pgrai.util.PGRConstants.*;

/**
 * Service class for handling notifications related to PGR service requests.
 * Provides methods to process notifications, fetch user details, and send SMS or event notifications.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private static final String LOG_NO_CITIZEN_MSG = "No message Found For Citizen On Topic : ";

    private static final String LOG_NO_DEFAULT_MSG = "No default message Found For Topic : ";

    private static final String LOG_NO_EMPLOYEE_MSG = "No message Found For Employee On Topic : ";

    private static final String LOG_LOCALIZATION_FETCH_FAILED = "Fetching from localization failed";

    private static final String PLACEHOLDER_STATUS = "{status}";

    private static final String PLACEHOLDER_EMP_DEPARTMENT = "{emp_department}";

    private static final String PLACEHOLDER_EMP_DESIGNATION = "{emp_designation}";

    private static final String PLACEHOLDER_EMP_NAME = "{emp_name}";

    private static final String PLACEHOLDER_ULB = "{ulb}";

    private static final String PLACEHOLDER_AO_DESIGNATION = "{ao_designation}";

    private static final String AO_DESIGNATION_MESSAGE_PATH = "$..messages[?(@.code==\"COMMON_MASTERS_DESIGNATION_AO\")].message";

    private static final String LAST_MODIFIED_DATE = "lastModifiedDate";

    private static final String PWD_EXPIRY_DATE = "pwdExpiryDate";

    private final PGRConfiguration config;

    private final NotificationUtil notificationUtil;

    private final WorkflowService workflowService;

    private final ServiceRequestRepository serviceRequestRepository;

    private final MDMSUtils mdmsUtils;

    private final HRMSUtil hrmsUtils;

    private final ObjectMapper mapper;

    private final MultiStateInstanceUtil centralInstanceUtil;

    /**
     * Processes the notification for a given service request and topic.
     *
     * @param request The service request for which the notification is to be processed.
     * @param topic   The topic name for the notification.
     */
    public void process(ServiceRequest request, String topic) {
        try {
            log.info("request for notification :" + request);
            String tenantId = request.getService().getTenantId();
            ServiceWrapper serviceWrapper = ServiceWrapper.builder().service(request.getService()).workflow(request.getWorkflow()).build();
            String applicationStatus = request.getService().getApplicationStatus();
            String action = request.getWorkflow().getAction();
            log.info("Entering process method. Topic: {}, ServiceRequest: {}", topic, request);
            log.info("Process method - TenantId: {}, ApplicationStatus: {}, Action: {}",
                    tenantId, applicationStatus, action);

            if (!(NOTIFICATION_ENABLE_FOR_STATUS.contains(action+"_"+applicationStatus))) {
                log.info("Notification Disabled For State :" + applicationStatus);
                return;
            }
            log.info("In Notification code for PGR");

            Map<String, List<String>> finalMessage = getFinalMessage(request, topic, applicationStatus);
            String citizenMobileNumber = request.getService().getCitizen().getMobileNumber();
            String employeeMobileNumber = resolveEmployeeMobileNumber(request, serviceWrapper, applicationStatus, action);

            if(!CollectionUtils.isEmpty(finalMessage)) {
                dispatchNotifications(request, finalMessage, citizenMobileNumber, employeeMobileNumber);
            }

        } catch (Exception ex) {
            log.error("Error occured while processing the record from topic : " + topic, ex);
        }
    }

    /**
     * Resolves the employee mobile number to notify based on the application status and workflow action.
     *
     * @param request           The service request being processed.
     * @param serviceWrapper    The wrapper holding the service and workflow details.
     * @param applicationStatus The application status of the service request.
     * @param action            The workflow action performed.
     * @return The resolved employee mobile number, or {@code null} when no employee is to be notified.
     */
    private String resolveEmployeeMobileNumber(ServiceRequest request, ServiceWrapper serviceWrapper, String applicationStatus, String action) {

        if (usesProcessInstanceAssignee(applicationStatus, action)) {
            ProcessInstance processInstance = getEmployeeName(serviceWrapper.getService().getTenantId(), serviceWrapper.getService().getServiceRequestId(), request.getRequestInfo(), ASSIGN);
            return processInstance.getAssignes().get(0).getMobileNumber();
        }

        if (clearsEmployeeMobile(applicationStatus, action))
            return null;

        if (usesWorkflowAssignee(applicationStatus, action))
            return fetchUserByUUID(request.getWorkflow().getAssignes().get(0), request.getRequestInfo(), request.getService().getTenantId()).getMobileNumber();

        return fetchUserByUUID(request.getService().getAuditDetails().getCreatedBy(), request.getRequestInfo(), request.getService().getTenantId()).getMobileNumber();
    }

    /**
     * Identifies status/action combinations whose employee contact is read from the workflow process instance.
     *
     * @param applicationStatus The application status of the service request.
     * @param action            The workflow action performed.
     * @return {@code true} when the assignee is sourced from the process instance.
     */
    private boolean usesProcessInstanceAssignee(String applicationStatus, String action) {
        if (applicationStatus.equalsIgnoreCase(PENDINGFORASSIGNMENT) && action.equalsIgnoreCase(PGR_WF_REOPEN))
            return true;
        if (applicationStatus.equalsIgnoreCase(RESOLVED) && action.equalsIgnoreCase(PGR_WF_RESOLVE))
            return true;
        return (applicationStatus.equalsIgnoreCase(CLOSED_AFTER_RESOLUTION) || applicationStatus.equalsIgnoreCase(CLOSED_AFTER_REJECTION))
                && action.equalsIgnoreCase(RATE);
    }

    /**
     * Identifies status/action combinations for which no employee notification is sent.
     *
     * @param applicationStatus The application status of the service request.
     * @param action            The workflow action performed.
     * @return {@code true} when the employee mobile number must be left empty.
     */
    private boolean clearsEmployeeMobile(String applicationStatus, String action) {
        if (applicationStatus.equalsIgnoreCase(PENDINGFORASSIGNMENT) && action.equalsIgnoreCase(APPLY))
            return true;
        return applicationStatus.equalsIgnoreCase(REJECTED) && action.equalsIgnoreCase(REJECT);
    }

    /**
     * Identifies status/action combinations whose employee contact is read from the workflow assignee.
     *
     * @param applicationStatus The application status of the service request.
     * @param action            The workflow action performed.
     * @return {@code true} when the assignee is sourced from the workflow assignees list.
     */
    private boolean usesWorkflowAssignee(String applicationStatus, String action) {
        if (applicationStatus.equalsIgnoreCase(PENDINGATLME) && action.equalsIgnoreCase(ASSIGN))
            return true;
        if (applicationStatus.equalsIgnoreCase(PENDING_FOR_REASSIGNMENT) && action.equalsIgnoreCase(REASSIGN))
            return true;
        return applicationStatus.equalsIgnoreCase(PENDINGATLME) && action.equalsIgnoreCase(REASSIGN);
    }

    /**
     * Dispatches the prepared notifications over the enabled channels (user events and SMS).
     *
     * @param request              The service request being processed.
     * @param finalMessage         The prepared messages grouped by recipient role.
     * @param citizenMobileNumber  The citizen mobile number.
     * @param employeeMobileNumber The employee mobile number.
     */
    private void dispatchNotifications(ServiceRequest request, Map<String, List<String>> finalMessage, String citizenMobileNumber, String employeeMobileNumber) {
        String tenantId = request.getService().getTenantId();

        if (Boolean.TRUE.equals(config.getIsUserEventsNotificationEnabled()))
            sendEventNotifications(request, finalMessage, tenantId);

        if (Boolean.TRUE.equals(config.getIsSMSEnabled()))
            sendSmsNotifications(finalMessage, citizenMobileNumber, employeeMobileNumber, tenantId);
    }

    /**
     * Sends user-event notifications for every prepared message.
     *
     * @param request      The service request being processed.
     * @param finalMessage The prepared messages grouped by recipient role.
     * @param tenantId     The tenant ID.
     */
    private void sendEventNotifications(ServiceRequest request, Map<String, List<String>> finalMessage, String tenantId) {
        for (Map.Entry<String, List<String>> entry : finalMessage.entrySet()) {
            for (String msg : entry.getValue()) {
                EventRequest eventRequest = enrichEventRequest(request, msg);
                if (eventRequest != null) {
                    notificationUtil.sendEventNotification(tenantId, eventRequest);
                }
            }
        }
    }

    /**
     * Sends SMS notifications for every prepared message, routing each message to the citizen or
     * employee mobile number based on the recipient role.
     *
     * @param finalMessage         The prepared messages grouped by recipient role.
     * @param citizenMobileNumber  The citizen mobile number.
     * @param employeeMobileNumber The employee mobile number.
     * @param tenantId             The tenant ID.
     */
    private void sendSmsNotifications(Map<String, List<String>> finalMessage, String citizenMobileNumber, String employeeMobileNumber, String tenantId) {
        for (Map.Entry<String, List<String>> entry : finalMessage.entrySet()) {
            String mobileNumber = entry.getKey().equalsIgnoreCase(CITIZEN) ? citizenMobileNumber : employeeMobileNumber;
            for (String msg : entry.getValue()) {
                List<SMSRequest> smsRequests = enrichSmsRequest(mobileNumber, msg);
                if (!CollectionUtils.isEmpty(smsRequests)) {
                    notificationUtil.sendSMS(tenantId, smsRequests);
                }
            }
        }
    }

    /**
     * Generates the final message for the notification based on the service request and application status.
     *
     * @param request           The service request for which the message is to be generated.
     * @param topic             The topic name for the notification.
     * @param applicationStatus The application status of the service request.
     * @return A map containing the final messages for citizen and employee.
     */
    @SuppressWarnings({"java:S3776", "java:S6541"})
    private Map<String, List<String>> getFinalMessage(ServiceRequest request, String topic, String applicationStatus) {
        log.info("Entering getFinalMessage method for topic: {}, applicationStatus: {}", topic, applicationStatus);
        log.info("Workflow Action in getFinalMessage: {}", request.getWorkflow().getAction());

        String tenantId = request.getService().getTenantId();
        String localizationMessage = notificationUtil.getLocalizationMessages(tenantId, request.getRequestInfo(),PGR_MODULE);
        log.info("Localization Message fetched: {}", localizationMessage != null ? "SUCCESS" : "NULL");

        ServiceWrapper serviceWrapper = ServiceWrapper.builder().service(request.getService()).workflow(request.getWorkflow()).build();
        Map<String, List<String>> message = new HashMap<>();

        String localisedStatus = notificationUtil.getCustomizedMsgForPlaceholder(localizationMessage,"CS_COMMON_"+serviceWrapper.getService().getApplicationStatus());

        StatusMessages statusMessages = buildStatusMessages(request, topic, applicationStatus, localizationMessage, localisedStatus, serviceWrapper);
        if (statusMessages.abort)
            return Collections.emptyMap();

        String messageForCitizen = statusMessages.citizen;
        String messageForEmployee = statusMessages.employee;
        String defaultMessage = statusMessages.defaultMessage;

        String localisedComplaint = notificationUtil.getCustomizedMsgForPlaceholder(localizationMessage,"pgr.complaint.category."+request.getService().getServiceCode());

        Long createdTime = serviceWrapper.getService().getAuditDetails().getCreatedTime();
        LocalDate date = Instant.ofEpochMilli(createdTime > 10 ? createdTime : createdTime * 1000)
                .atZone(ZoneId.systemDefault()).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);

        String appLink = notificationUtil.getShortnerURL(config.getMobileDownloadLink());
        log.info("Before final message replacements - messageForCitizen current value: {}", messageForCitizen);
        log.info("Before final message replacements - messageForEmployee current value: {}", messageForEmployee);
        log.info("Before final message replacements - localisedComplaint current value: {}", localisedComplaint);
        log.info("Before final message replacements - appLink current value: {}", appLink);

        if(messageForCitizen != null) {
            log.info("Attempting to replace placeholders in messageForCitizen...");
            messageForCitizen = messageForCitizen.replace("{complaint_type}", localisedComplaint);
            log.info("messageForCitizen after complaint_type replace: {}", messageForCitizen);
            messageForCitizen = messageForCitizen.replace("{id}", serviceWrapper.getService().getServiceRequestId());
            log.info("messageForCitizen after id replace: {}", messageForCitizen);
            messageForCitizen = messageForCitizen.replace("{date}", date.format(formatter));
            messageForCitizen = messageForCitizen.replace("{download_link}", appLink);
        }

        if(messageForEmployee != null) {
            log.info("Attempting to replace placeholders in messageForEmployee...");
            messageForEmployee = messageForEmployee.replace("{complaint_type}", localisedComplaint);
            log.info("messageForEmployee after complaint_type replace: {}", messageForEmployee);
            messageForEmployee = messageForEmployee.replace("{id}", serviceWrapper.getService().getServiceRequestId());
            log.info("messageForEmployee after id replace: {}", messageForEmployee);
            messageForEmployee = messageForEmployee.replace("{date}", date.format(formatter));
            log.info("messageForEmployee after date replace: {}", messageForEmployee);
            messageForEmployee = messageForEmployee.replace("{download_link}", appLink); // This is around the reported line 517
            log.info("messageForEmployee after download_link replace: {}", messageForEmployee);
        } else {
            log.warn("messageForEmployee is NULL, skipping final replacements for employee.");
        }

        message.put(CITIZEN, List.of(messageForCitizen, defaultMessage));
        message.put(EMPLOYEE, List.of(messageForEmployee));
        log.info("Final messages generated - Citizen: {}, Employee: {}", message.get(CITIZEN), message.get(EMPLOYEE));
        return message;
    }

    /**
     * Holds the citizen, employee and default messages built for a given status/action combination,
     * along with an abort flag used to signal that notification building should stop.
     */
    private static class StatusMessages {
        private String citizen;
        private String employee;
        private String defaultMessage;
        private boolean abort;
    }

    /**
     * Groups immutable notification inputs shared by status/action message builders.
     */
    private static class MessageBuildContext {
        private final ServiceRequest request;
        private final String topic;
        private final String applicationStatus;
        private final String localizationMessage;
        private final String localisedStatus;

        private MessageBuildContext(ServiceRequest request, String topic, String applicationStatus, String localizationMessage, String localisedStatus) {
            this.request = request;
            this.topic = topic;
            this.applicationStatus = applicationStatus;
            this.localizationMessage = localizationMessage;
            this.localisedStatus = localisedStatus;
        }
    }

    /**
     * Builds the citizen, employee and default messages for the matching status/action combination.
     *
     * @param request            The service request being processed.
     * @param topic              The topic name for the notification.
     * @param applicationStatus  The application status of the service request.
     * @param localizationMessage The localization messages payload.
     * @param localisedStatus    The localized status placeholder value.
     * @param serviceWrapper     The wrapper holding the service and workflow details.
     * @return The populated {@link StatusMessages} holder.
     */
    private StatusMessages buildStatusMessages(ServiceRequest request, String topic, String applicationStatus, String localizationMessage, String localisedStatus, ServiceWrapper serviceWrapper) {
        StatusMessages msgs = new StatusMessages();
        String status = serviceWrapper.getService().getApplicationStatus();
        String wfAction = serviceWrapper.getWorkflow().getAction();
        MessageBuildContext context = new MessageBuildContext(request, topic, applicationStatus, localizationMessage, localisedStatus);

        populateCreationMessages(msgs, status, wfAction, context);
        if (msgs.abort)
            return msgs;

        populateResolutionMessages(msgs, status, wfAction, context);
        return msgs;
    }

    /**
     * Populates messages for the apply, assign, re-assignment and reject status/action combinations.
     */
    private void populateCreationMessages(StatusMessages msgs, String status, String wfAction, MessageBuildContext context) {

        if (status.equalsIgnoreCase(PENDINGFORASSIGNMENT) && wfAction.equalsIgnoreCase(APPLY))
            handleApply(msgs, context.request, context.topic, context.applicationStatus, context.localizationMessage, context.localisedStatus);

        if (status.equalsIgnoreCase(PENDINGATLME) && wfAction.equalsIgnoreCase(ASSIGN))
            handleAssign(msgs, context.request, context.topic, context.applicationStatus, context.localizationMessage, context.localisedStatus);

        if (status.equalsIgnoreCase(PENDING_FOR_REASSIGNMENT) && wfAction.equalsIgnoreCase(REASSIGN))
            handleReassign(msgs, context.request, context.topic, context.applicationStatus, context.localizationMessage, context.localisedStatus);

        if (status.equalsIgnoreCase(REJECTED) && wfAction.equalsIgnoreCase(REJECT))
            handleReject(msgs, context.request, context.topic, context.applicationStatus, context.localizationMessage, context.localisedStatus);
    }

    /**
     * Populates messages for the re-open, resolve, rate and LME re-assignment status/action combinations.
     */
    private void populateResolutionMessages(StatusMessages msgs, String status, String wfAction, MessageBuildContext context) {

        if (status.equalsIgnoreCase(PENDINGFORASSIGNMENT) && wfAction.equalsIgnoreCase(PGR_WF_REOPEN))
            handleReopen(msgs, context.request, context.topic, context.applicationStatus, context.localizationMessage, context.localisedStatus);

        if (status.equalsIgnoreCase(RESOLVED) && wfAction.equalsIgnoreCase(PGR_WF_RESOLVE))
            handleResolve(msgs, context.request, context.topic, context.applicationStatus, context.localizationMessage, context.localisedStatus);

        if ((status.equalsIgnoreCase(CLOSED_AFTER_RESOLUTION) || status.equalsIgnoreCase(CLOSED_AFTER_REJECTION)) && wfAction.equalsIgnoreCase(RATE))
            handleRate(msgs, context.request, context.topic, context.applicationStatus, context.localizationMessage, context.localisedStatus);

        if (status.equalsIgnoreCase(PENDINGATLME) && wfAction.equalsIgnoreCase(REASSIGN))
            handleReassign(msgs, context.request, context.topic, context.applicationStatus, context.localizationMessage, context.localisedStatus);
    }

    /**
     * Builds the confirmation message sent to citizens when they raise a complaint.
     */
    private void handleApply(StatusMessages msgs, ServiceRequest request, String topic, String applicationStatus, String localizationMessage, String localisedStatus) {
        msgs.citizen = notificationUtil.getCustomizedMsg(request.getWorkflow().getAction(), applicationStatus, CITIZEN, localizationMessage);
        log.info("messageForCitizen after customizedMsg call for APPLY: {}", msgs.citizen);
        if (msgs.citizen == null) {
            log.info(LOG_NO_CITIZEN_MSG + topic);
            msgs.abort = true;
            return;
        }

        msgs.defaultMessage = notificationUtil.getDefaultMsg(CITIZEN, localizationMessage);
        log.info("defaultMessage after getDefaultMsg call for APPLY: {}", msgs.defaultMessage);
        if (msgs.defaultMessage == null) {
            log.info(LOG_NO_DEFAULT_MSG + topic);
            msgs.abort = true;
            return;
        }

        if (msgs.defaultMessage.contains(PLACEHOLDER_STATUS))
            msgs.defaultMessage = msgs.defaultMessage.replace(PLACEHOLDER_STATUS, localisedStatus);
    }

    /**
     * Builds the citizen and employee messages when a complaint is assigned to an employee.
     */
    private void handleAssign(StatusMessages msgs, ServiceRequest request, String topic, String applicationStatus, String localizationMessage, String localisedStatus) {
        msgs.citizen = notificationUtil.getCustomizedMsg(request.getWorkflow().getAction(), applicationStatus, CITIZEN, localizationMessage);
        if (msgs.citizen == null) {
            log.info(LOG_NO_CITIZEN_MSG + topic);
            msgs.abort = true;
            return;
        }

        msgs.employee = notificationUtil.getCustomizedMsg(request.getWorkflow().getAction(), applicationStatus, EMPLOYEE, localizationMessage);
        if (msgs.employee == null) {
            log.info(LOG_NO_EMPLOYEE_MSG + topic);
            msgs.abort = true;
            return;
        }

        msgs.defaultMessage = notificationUtil.getDefaultMsg(CITIZEN, localizationMessage);
        if (msgs.defaultMessage == null) {
            log.info(LOG_NO_DEFAULT_MSG + topic);
            msgs.abort = true;
            return;
        }

        if (msgs.defaultMessage.contains(PLACEHOLDER_STATUS))
            msgs.defaultMessage = msgs.defaultMessage.replace(PLACEHOLDER_STATUS, localisedStatus);

        Map<String, String> reassigneeDetails = getHRMSEmployee(request);
        msgs.citizen = applyCitizenEmployeePlaceholders(msgs.citizen, request, reassigneeDetails);

        msgs.employee = applyUlbPlaceholder(msgs.employee, request);

        if (msgs.employee.contains(PLACEHOLDER_EMP_NAME))
            msgs.employee = msgs.employee.replace(PLACEHOLDER_EMP_NAME, fetchUserByUUID(request.getWorkflow().getAssignes().get(0), request.getRequestInfo(), request.getService().getTenantId()).getName());

        msgs.employee = applyAoDesignationPlaceholder(msgs.employee, request);
    }

    /**
     * Builds the citizen and employee messages when a complaint is re-assigned (to an employee or to LME).
     */
    private void handleReassign(StatusMessages msgs, ServiceRequest request, String topic, String applicationStatus, String localizationMessage, String localisedStatus) {
        msgs.citizen = notificationUtil.getCustomizedMsg(request.getWorkflow().getAction(), applicationStatus, CITIZEN, localizationMessage);
        if (msgs.citizen == null) {
            log.info(LOG_NO_CITIZEN_MSG + topic);
            msgs.abort = true;
            return;
        }

        msgs.employee = notificationUtil.getCustomizedMsg(request.getWorkflow().getAction(), applicationStatus, EMPLOYEE, localizationMessage);
        if (msgs.employee == null) {
            log.info(LOG_NO_EMPLOYEE_MSG + topic);
            msgs.abort = true;
            return;
        }

        msgs.defaultMessage = notificationUtil.getDefaultMsg(CITIZEN, localizationMessage);
        if (msgs.defaultMessage == null) {
            log.info(LOG_NO_DEFAULT_MSG + topic);
            msgs.abort = true;
            return;
        }

        if (msgs.defaultMessage.contains(PLACEHOLDER_STATUS))
            msgs.defaultMessage = msgs.defaultMessage.replace(PLACEHOLDER_STATUS, localisedStatus);

        Map<String, String> reassigneeDetails = getHRMSEmployee(request);
        msgs.citizen = applyCitizenEmployeePlaceholders(msgs.citizen, request, reassigneeDetails);

        msgs.employee = applyUlbPlaceholder(msgs.employee, request);

        if (msgs.employee.contains(PLACEHOLDER_EMP_NAME))
            msgs.employee = msgs.employee.replace(PLACEHOLDER_EMP_NAME, fetchUserByUUID(request.getRequestInfo().getUserInfo().getUuid(), request.getRequestInfo(), request.getService().getTenantId()).getName());

        msgs.employee = applyAoDesignationPlaceholder(msgs.employee, request);
    }

    /**
     * Builds the citizen message when a complaint is rejected with a reason.
     */
    private void handleReject(StatusMessages msgs, ServiceRequest request, String topic, String applicationStatus, String localizationMessage, String localisedStatus) {
        msgs.citizen = notificationUtil.getCustomizedMsg(request.getWorkflow().getAction(), applicationStatus, CITIZEN, localizationMessage);
        if (msgs.citizen == null) {
            log.info(LOG_NO_CITIZEN_MSG + topic);
            msgs.abort = true;
            return;
        }

        msgs.defaultMessage = notificationUtil.getDefaultMsg(CITIZEN, localizationMessage);
        if (msgs.defaultMessage == null) {
            log.info(LOG_NO_DEFAULT_MSG + topic);
            msgs.abort = true;
            return;
        }

        if (msgs.defaultMessage.contains(PLACEHOLDER_STATUS))
            msgs.defaultMessage = msgs.defaultMessage.replace(PLACEHOLDER_STATUS, localisedStatus);

        if (msgs.citizen.contains("{additional_comments}"))
            msgs.citizen = msgs.citizen.replace("{additional_comments}", request.getWorkflow().getComments());
    }

    /**
     * Builds the citizen and employee messages when a complaint is re-opened on citizen request.
     */
    private void handleReopen(StatusMessages msgs, ServiceRequest request, String topic, String applicationStatus, String localizationMessage, String localisedStatus) {
        msgs.citizen = notificationUtil.getCustomizedMsg(request.getWorkflow().getAction(), applicationStatus, CITIZEN, localizationMessage);
        if (msgs.citizen == null) {
            log.info(LOG_NO_CITIZEN_MSG + topic);
            msgs.abort = true;
            return;
        }

        msgs.employee = notificationUtil.getCustomizedMsg(request.getWorkflow().getAction(), applicationStatus, EMPLOYEE, localizationMessage);
        if (msgs.employee == null) {
            log.info(LOG_NO_EMPLOYEE_MSG + topic);
            msgs.abort = true;
            return;
        }

        msgs.defaultMessage = notificationUtil.getDefaultMsg(CITIZEN, localizationMessage);
        if (msgs.defaultMessage == null) {
            log.info(LOG_NO_DEFAULT_MSG + topic);
            msgs.abort = true;
            return;
        }

        ProcessInstance processInstance = getEmployeeName(request.getService().getTenantId(), request.getService().getServiceRequestId(), request.getRequestInfo(), ASSIGN);

        if (msgs.defaultMessage.contains(PLACEHOLDER_STATUS))
            msgs.defaultMessage = msgs.defaultMessage.replace(PLACEHOLDER_STATUS, localisedStatus);

        msgs.employee = applyUlbPlaceholder(msgs.employee, request);

        if (msgs.employee.contains(PLACEHOLDER_EMP_NAME))
            msgs.employee = msgs.employee.replace(PLACEHOLDER_EMP_NAME, processInstance.getAssignes().get(0).getName());
    }

    /**
     * Builds the citizen message when a complaint is resolved.
     */
    private void handleResolve(StatusMessages msgs, ServiceRequest request, String topic, String applicationStatus, String localizationMessage, String localisedStatus) {
        msgs.citizen = notificationUtil.getCustomizedMsg(request.getWorkflow().getAction(), applicationStatus, CITIZEN, localizationMessage);
        if (msgs.citizen == null) {
            log.info(LOG_NO_CITIZEN_MSG + topic);
            msgs.abort = true;
            return;
        }

        msgs.defaultMessage = notificationUtil.getDefaultMsg(CITIZEN, localizationMessage);
        if (msgs.defaultMessage == null) {
            log.info(LOG_NO_DEFAULT_MSG + topic);
            msgs.abort = true;
            return;
        }

        ProcessInstance processInstance = getEmployeeName(request.getService().getTenantId(), request.getService().getServiceRequestId(), request.getRequestInfo(), ASSIGN);

        if (msgs.defaultMessage.contains(PLACEHOLDER_STATUS))
            msgs.defaultMessage = msgs.defaultMessage.replace(PLACEHOLDER_STATUS, localisedStatus);

        if (msgs.citizen.contains(PLACEHOLDER_EMP_NAME))
            msgs.citizen = msgs.citizen.replace(PLACEHOLDER_EMP_NAME, processInstance.getAssignes().get(0).getName());
    }

    /**
     * Builds the employee message when a resolved or rejected complaint is rated/closed.
     */
    private void handleRate(StatusMessages msgs, ServiceRequest request, String topic, String applicationStatus, String localizationMessage, String localisedStatus) {
        msgs.employee = notificationUtil.getCustomizedMsg(request.getWorkflow().getAction(), applicationStatus, EMPLOYEE, localizationMessage);
        if (msgs.employee == null) {
            log.info(LOG_NO_EMPLOYEE_MSG + topic);
            msgs.abort = true;
            return;
        }

        msgs.defaultMessage = notificationUtil.getDefaultMsg(CITIZEN, localizationMessage);
        if (msgs.defaultMessage == null) {
            log.info(LOG_NO_DEFAULT_MSG + topic);
            msgs.abort = true;
            return;
        }

        ProcessInstance processInstance = getEmployeeName(request.getService().getTenantId(), request.getService().getServiceRequestId(), request.getRequestInfo(), ASSIGN);

        if (msgs.defaultMessage.contains(PLACEHOLDER_STATUS))
            msgs.defaultMessage = msgs.defaultMessage.replace(PLACEHOLDER_STATUS, localisedStatus);

        if (msgs.employee.contains("{rating}"))
            msgs.employee = msgs.employee.replace("{rating}", request.getService().getRating().toString());

        if (msgs.employee.contains(PLACEHOLDER_EMP_NAME))
            msgs.employee = msgs.employee.replace(PLACEHOLDER_EMP_NAME, processInstance.getAssignes().get(0).getName());
    }

    /**
     * Replaces the employee-related placeholders ({@code emp_department}, {@code emp_designation},
     * {@code emp_name}) in the citizen message using the resolved HRMS reassignee details.
     *
     * @param messageForCitizen  The citizen message to update.
     * @param request            The service request being processed.
     * @param reassigneeDetails  The HRMS reassignee details.
     * @return The updated citizen message.
     */
    private String applyCitizenEmployeePlaceholders(String messageForCitizen, ServiceRequest request, Map<String, String> reassigneeDetails) {
        if (messageForCitizen.contains(PLACEHOLDER_EMP_DEPARTMENT))
            messageForCitizen = messageForCitizen.replace(PLACEHOLDER_EMP_DEPARTMENT, reassigneeDetails.get(DEPARTMENT));

        if (messageForCitizen.contains(PLACEHOLDER_EMP_DESIGNATION))
            messageForCitizen = messageForCitizen.replace(PLACEHOLDER_EMP_DESIGNATION, reassigneeDetails.get(DESIGNATION));

        if (messageForCitizen.contains(PLACEHOLDER_EMP_NAME))
            messageForCitizen = messageForCitizen.replace(PLACEHOLDER_EMP_NAME, fetchUserByUUID(request.getWorkflow().getAssignes().get(0), request.getRequestInfo(), request.getService().getTenantId()).getName());

        return messageForCitizen;
    }

    /**
     * Replaces the {@code ulb} placeholder in the employee message with the localized district name.
     *
     * @param messageForEmployee The employee message to update.
     * @param request            The service request being processed.
     * @return The updated employee message.
     */
    private String applyUlbPlaceholder(String messageForEmployee, ServiceRequest request) {
        if (!messageForEmployee.contains(PLACEHOLDER_ULB))
            return messageForEmployee;

        String localisationMessageForPlaceholder = notificationUtil.getLocalizationMessages(request.getService().getTenantId(), request.getRequestInfo(), COMMON_MODULE);
        String localisedULB = notificationUtil.getCustomizedMsgForPlaceholder(localisationMessageForPlaceholder, request.getService().getAddress().getDistrict());
        return messageForEmployee.replace(PLACEHOLDER_ULB, localisedULB);
    }

    /**
     * Replaces the {@code ao_designation} placeholder in the employee message with the localized
     * assistant officer designation fetched from localization.
     *
     * @param messageForEmployee The employee message to update.
     * @param request            The service request being processed.
     * @return The updated employee message.
     */
    private String applyAoDesignationPlaceholder(String messageForEmployee, ServiceRequest request) {
        if (!messageForEmployee.contains(PLACEHOLDER_AO_DESIGNATION))
            return messageForEmployee;

        String localisationMessageForPlaceholder = notificationUtil.getLocalizationMessages(request.getService().getTenantId(), request.getRequestInfo(), COMMON_MODULE);
        try {
            ArrayList<String> messageObj = JsonPath.parse(localisationMessageForPlaceholder).read(AO_DESIGNATION_MESSAGE_PATH);
            if (messageObj != null && !messageObj.isEmpty()) {
                messageForEmployee = messageForEmployee.replace(PLACEHOLDER_AO_DESIGNATION, messageObj.get(0));
            }
        } catch (Exception e) {
            log.warn(LOG_LOCALIZATION_FETCH_FAILED, e);
        }
        return messageForEmployee;
    }

    /**
     * Fetches the user details based on the UUID.
     *
     * @param uuidstring The UUID of the user.
     * @param requestInfo The request information.
     * @param tenantId The tenant ID.
     * @return The user object corresponding to the given UUID.
     */
    public User fetchUserByUUID(String uuidstring, RequestInfo requestInfo, String tenantId) {
        User userInfoCopy = requestInfo.getUserInfo();

        User userInfo = getInternalMicroserviceUser(tenantId);

        requestInfo.setUserInfo(userInfo);

        StringBuilder uri = new StringBuilder();
        uri.append(config.getUserHost()).append(config.getUserSearchEndpoint());
        Map<String, Object> userSearchRequest = new HashMap<>();
        userSearchRequest.put("RequestInfo", requestInfo);
        userSearchRequest.put("tenantId", tenantId);
        userSearchRequest.put("userType", "EMPLOYEE");
        Set<String> uuid = new HashSet<>() ;
        uuid.add(uuidstring);
        userSearchRequest.put("uuid", uuid);
        User user = null;
        try {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) serviceRequestRepository.fetchResult(uri, userSearchRequest);
            List<LinkedHashMap<String, Object>> users = (List<LinkedHashMap<String, Object>>) responseMap.get("user");
            String dobFormat = "yyyy-MM-dd";
            parseResponse(responseMap,dobFormat);
            user = 	mapper.convertValue(users.get(0), User.class);

        }catch(Exception e) {
            log.error("Exception while trying parse user object: ",e);
        }

        requestInfo.setUserInfo(userInfoCopy);
        return user;
    }

    /**
     * Parses date formats to long for all users in responseMap
     * @param responeMap LinkedHashMap got from user api response
     */
    private void parseResponse(Map<String, Object> responseMap, String dobFormat) {
        List<LinkedHashMap<String, Object>> users = (List<LinkedHashMap<String, Object>>) responseMap.get("user");
        String formatForDate = "dd-MM-yyyy HH:mm:ss";
        if(users!=null){
            users.forEach( map -> {
                        map.put("createdDate",dateTolong((String)map.get("createdDate"),formatForDate));
                        if((String)map.get(LAST_MODIFIED_DATE)!=null)
                            map.put(LAST_MODIFIED_DATE,dateTolong((String)map.get(LAST_MODIFIED_DATE),formatForDate));
                        if((String)map.get("dob")!=null)
                            map.put("dob",dateTolong((String)map.get("dob"),dobFormat));
                        if((String)map.get(PWD_EXPIRY_DATE)!=null)
                            map.put(PWD_EXPIRY_DATE,dateTolong((String)map.get(PWD_EXPIRY_DATE),formatForDate));
                    }
            );
        }
    }

    /**
     * Converts date to long
     * @param date date to be parsed
     * @param format Format of the date
     * @return Long value of date
     */
    private Long dateTolong(String date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            Date parsedDate = dateFormat.parse(date);
            return parsedDate.getTime();
        } catch (ParseException e) {
            throw new CustomException("INVALID_DATE_FORMAT", "Failed to parse date: " + date);
        }
    }

    /**
     * Fetches the employee name based on the tenant ID, service request ID, and action.
     *
     * @param tenantId The tenant ID.
     * @param serviceRequestId The service request ID.
     * @param requestInfo The request information.
     * @param action The action performed.
     * @return The process instance containing the employee details.
     */
    public ProcessInstance getEmployeeName(String tenantId, String serviceRequestId, RequestInfo requestInfo,String action){
        ProcessInstance processInstanceToReturn = new ProcessInstance();
        User userInfoCopy = requestInfo.getUserInfo();

        User userInfo = getInternalMicroserviceUser(tenantId);

        requestInfo.setUserInfo(userInfo);

        RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
        StringBuilder url = workflowService.getprocessInstanceSearchURL(tenantId,serviceRequestId);
        url.append("&").append("history=true");

        Object result = serviceRequestRepository.fetchResult(url, requestInfoWrapper);
        ProcessInstanceResponse processInstanceResponse = null;
        try {
            processInstanceResponse = mapper.convertValue(result, ProcessInstanceResponse.class);
        } catch (IllegalArgumentException e) {
            throw new CustomException("PARSING ERROR", "Failed to parse response of workflow processInstance search");
        }
        if (CollectionUtils.isEmpty(processInstanceResponse.getProcessInstances()))
            throw new CustomException("WORKFLOW_NOT_FOUND", "The workflow object is not found");

        for(ProcessInstance processInstance:processInstanceResponse.getProcessInstances()){
            if(processInstance.getAction().equalsIgnoreCase(action))
                processInstanceToReturn= processInstance;
        }
        requestInfo.setUserInfo(userInfoCopy);
        return processInstanceToReturn;
    }

    public String getDepartment(ServiceRequest request){
        Object mdmsData = mdmsUtils.mDMSCall(request);
        String serviceCode = request.getService().getServiceCode();
        String jsonPath = MDMS_SERVICEDEF_SEARCH.replace("{SERVICEDEF}",serviceCode);

        List<Object> res = null;

        try{
            res = JsonPath.read(mdmsData,jsonPath);
        }
        catch (Exception e){
            throw new CustomException("JSONPATH_ERROR","Failed to parse mdms response");
        }

        if(CollectionUtils.isEmpty(res))
            throw new CustomException("INVALID_SERVICECODE","The service code: "+serviceCode+" is not present in MDMS");

        return res.get(0).toString();

    }

    /**
     * Fetches HRMS employee details for the given service request.
     *
     * @param request The service request.
     * @return A map containing HRMS employee details.
     */
    public Map<String, String> getHRMSEmployee(ServiceRequest request){
        Map<String, String> reassigneeDetails = new HashMap<>();
        List<String> mdmsDepartmentList = null;
        List<String> hrmsDepartmentList = null;
        List<String> designation = null;
        List<String> employeeName = null;
        String departmentFromMDMS;

        String localisationMessageForPlaceholder =  notificationUtil.getLocalizationMessages(request.getService().getTenantId(), request.getRequestInfo(),COMMON_MODULE);
        //HRSMS CALL
        StringBuilder url = hrmsUtils.getHRMSURI(request.getWorkflow().getAssignes());
        RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(request.getRequestInfo()).build();
        Object response = serviceRequestRepository.fetchResult(url, requestInfoWrapper);

        //MDMS CALL
        Object mdmsData = mdmsUtils.mDMSCall(request);
        String jsonPath = MDMS_DEPARTMENT_SEARCH.replace("{SERVICEDEF}",request.getService().getServiceCode());

        try{
            mdmsDepartmentList = JsonPath.read(mdmsData,jsonPath);
            hrmsDepartmentList = JsonPath.read(response, HRMS_DEPARTMENT_JSONPATH);
        }
        catch (Exception e){
            throw new CustomException("JSONPATH_ERROR","Failed to parse mdms response for department");
        }

        if(CollectionUtils.isEmpty(mdmsDepartmentList))
            throw new CustomException("PARSING_ERROR","Failed to fetch department from mdms data for serviceCode: "+request.getService().getServiceCode());
        else departmentFromMDMS = mdmsDepartmentList.get(0);

        if(hrmsDepartmentList.contains(departmentFromMDMS)){
            String localisedDept = notificationUtil.getCustomizedMsgForPlaceholder(localisationMessageForPlaceholder,"COMMON_MASTERS_DEPARTMENT_"+departmentFromMDMS);
            reassigneeDetails.put("department",localisedDept);
        }

        String designationJsonPath = HRMS_DESIGNATION_JSONPATH.replace("{department}",departmentFromMDMS);

        try{
            designation = JsonPath.read(response, designationJsonPath);
            employeeName = JsonPath.read(response, HRMS_EMP_NAME_JSONPATH);
        }
        catch (Exception e){
            throw new CustomException("JSONPATH_ERROR","Failed to parse mdms response for department");
        }

        String localisedDesignation = notificationUtil.getCustomizedMsgForPlaceholder(localisationMessageForPlaceholder,"COMMON_MASTERS_DESIGNATION_"+designation.get(0));

        reassigneeDetails.put("designation",localisedDesignation);
        reassigneeDetails.put("employeeName",employeeName.get(0));

        return reassigneeDetails;
    }

    private List<SMSRequest> enrichSmsRequest(String mobileNumber, String finalMessage) {
        List<SMSRequest> smsRequest = new ArrayList<>();
        SMSRequest req = SMSRequest.builder().mobileNumber(mobileNumber).message(finalMessage).build();
        smsRequest.add(req);
        return smsRequest;
    }

    private EventRequest enrichEventRequest(ServiceRequest request, String finalMessage) {
        String tenantId = request.getService().getTenantId();
        String mobileNumber = request.getService().getCitizen().getMobileNumber();

        Map<String, String> mapOfPhoneNoAndUUIDs = fetchUserUUIDs(mobileNumber, request.getRequestInfo(),tenantId);

        if (CollectionUtils.isEmpty(mapOfPhoneNoAndUUIDs.keySet())) {
            log.info("UUID search failed!");
        }

        List<Event> events = new ArrayList<>();
        List<String> toUsers = new ArrayList<>();
        toUsers.add(mapOfPhoneNoAndUUIDs.get(mobileNumber));

        Action action = null;
        if(request.getWorkflow().getAction().equals("RESOLVE")) {

            List<ActionItem> items = new ArrayList<>();
            String rateLink = "";
            String reopenLink = "";
            String rateUrl = config.getRateLink();
            String reopenUrl = config.getReopenLink();
            rateLink = rateUrl.replace("{application-id}", request.getService().getServiceRequestId());
            reopenLink = reopenUrl.replace("{application-id}", request.getService().getServiceRequestId());
            rateLink = getUiAppHost(tenantId) + rateLink;
            reopenLink = getUiAppHost(tenantId) + reopenLink;
            ActionItem rateItem = ActionItem.builder().actionUrl(rateLink).code(config.getRateCode()).build();
            ActionItem reopenItem = ActionItem.builder().actionUrl(reopenLink).code(config.getReopenCode()).build();
            items.add(rateItem);
            items.add(reopenItem);

            action = Action.builder().actionUrls(items).build();
        }
        Recepient recepient = Recepient.builder().toUsers(toUsers).toRoles(null).build();
        events.add(Event.builder().tenantId(tenantId).description(finalMessage).eventType(USREVENTS_EVENT_TYPE)
                .name(USREVENTS_EVENT_NAME).postedBy(USREVENTS_EVENT_POSTEDBY)
                .source(Source.WEBAPP).recepient(recepient).actions(action).eventDetails(null).build());

        return EventRequest.builder().requestInfo(request.getRequestInfo()).events(events).build();
    }

    /**
     * Fetches UUIDs of citizens based on the phone number.
     *
     * @param mobileNumber The mobile number of the citizen.
     * @param requestInfo The request information.
     * @param tenantId The tenant ID.
     * @return A map containing phone numbers and their corresponding UUIDs.
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
            if(null != user) {
                String uuid = JsonPath.read(user, "$.user[0].uuid");
                mapOfPhoneNoAndUUIDs.put(mobileNumber, uuid);
            }else {
                log.error("Service returned null while fetching user for username - "+mobileNumber);
            }
        }catch(Exception e) {
            log.error("Exception while fetching user for username - "+mobileNumber);
            log.error("Exception trace: ",e);
        }

        return mapOfPhoneNoAndUUIDs;
    }

    /**
     * Creates an internal microservice user object for system operations.
     *
     * @param tenantId The tenant ID.
     * @return The internal microservice user object.
     */
    private User getInternalMicroserviceUser(String tenantId)
    {
        //Creating role with INTERNAL_MICROSERVICE_ROLE
        Role role = Role.builder()
                .name("Internal Microservice Role").code("INTERNAL_MICROSERVICE_ROLE")
                .tenantId(tenantId).build();

        //Creating userinfo with uuid and role of internal micro service role
        return User.builder()
                .uuid(config.getEgovInternalMicroserviceUserUuid())
                .type("SYSTEM")
                .roles(Collections.singletonList(role)).id(0L).build();
    }

    /**
     * Fetches the UI application host URL for the given tenant ID.
     *
     * @param tenantId The tenant ID.
     * @return The UI application host URL.
     */
    public String getUiAppHost(String tenantId)
    {
        String stateLevelTenantId = centralInstanceUtil.getStateLevelTenant(tenantId);
        return config.getUiAppHostMap().get(stateLevelTenantId);
    }

}