package org.upyog.tp.service.impl;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.tp.config.TreePruningConfiguration;
import org.upyog.tp.constant.TreePruningConstants;
import org.upyog.tp.service.NotificationService;
import org.upyog.tp.util.NotificationUtil;
import org.upyog.tp.web.models.events.Action;
import org.upyog.tp.web.models.events.ActionItem;
import org.upyog.tp.web.models.events.Event;
import org.upyog.tp.web.models.events.EventRequest;
import org.upyog.tp.web.models.events.Recepient;
import org.upyog.tp.web.models.events.Source;
import org.upyog.tp.web.models.notification.EmailRequest;
import org.upyog.tp.web.models.notification.SMSRequest;
import org.upyog.tp.web.models.treePruning.TreePruningBookingRequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private TreePruningConfiguration config;

    @Autowired
    private NotificationUtil util;

    /**
     * Processes a booking request and sends notifications based on the application status.
     *
     * @param request The booking request object, which can be either a {@link TreePruningBookingRequest}
     * @param status  The application status associated with the booking request.
     * @throws IllegalArgumentException If the request type is not supported.
     */

    public void process(Object request, String status) {
        String tenantId;
        RequestInfo requestInfo;
        Map<String, String> messageMap;
        String localizationMessages;

        if (request instanceof TreePruningBookingRequest) {
            TreePruningBookingRequest treePruningRequest = (TreePruningBookingRequest) request;
            tenantId = treePruningRequest.getTreePruningBookingDetail().getTenantId();
            requestInfo = treePruningRequest.getRequestInfo();
            localizationMessages = util.getLocalizationMessages(tenantId, requestInfo);
            messageMap = util.getCustomizedMsg(requestInfo, treePruningRequest.getTreePruningBookingDetail(), localizationMessages);
        } else {
            throw new IllegalArgumentException("Unsupported request type: " + request.getClass().getSimpleName());
        }

        EventRequest eventRequest = getEventsForRS(request, messageMap.get(NotificationUtil.ACTION_LINK), messageMap);
        log.info("Event Request in RequestService process method: " + eventRequest);

        Set<String> mobileNumbers = new HashSet<>(util.fetchUserUUIDs(new HashSet<>(), requestInfo, tenantId).keySet());
        List<String> configuredChannelNames = util.fetchChannelList(new RequestInfo(), tenantId.split("\\.")[0], config.getModuleName(), status);

        // Send app notification
        if (eventRequest != null) {
            util.sendEventNotification(eventRequest);
        }

        // Send SMS notification
        if (isNotificationEnabled(config.getIsSMSNotificationEnabled(), configuredChannelNames, TreePruningConstants.CHANNEL_NAME_SMS)) {
            List<SMSRequest> smsRequests = new LinkedList<>();
            util.enrichSMSRequest(request, smsRequests);
            if (!CollectionUtils.isEmpty(smsRequests)) {
                util.sendSMS(smsRequests);
            }
        }

        // Send Email notification
        if (isNotificationEnabled(config.getIsEmailNotificationEnabled(), configuredChannelNames, TreePruningConstants.CHANNEL_NAME_EMAIL)) {
            Map<String, String> mapOfPhnoAndEmail = util.fetchUserEmailIds(mobileNumbers, requestInfo, tenantId);
            List<EmailRequest> emailRequests = util.createEmailRequest(requestInfo, messageMap.get(TreePruningConstants.MESSAGE_TEXT), mapOfPhnoAndEmail);
            util.sendEmail(emailRequests);
        }
    }



    /**
     * Generates an event request for notification purposes.
     *
     * @param request    The booking request object (
     *                   {@link TreePruningBookingRequest}).
     * @param actionLink The action link for the notification.
     * @param messageMap The message content map for the notification.
     * @return An {@link EventRequest} object containing event details, or null if
     *         an error occurs.
     */

    private EventRequest getEventsForRS(Object request, String actionLink, Map<String, String> messageMap) {
        List<Event> events = new ArrayList<>();
        String tenantId;
        String mobileNumber;
        RequestInfo requestInfo;

        if (request instanceof TreePruningBookingRequest) {
            TreePruningBookingRequest tankerRequest = (TreePruningBookingRequest) request;
            tenantId = tankerRequest.getTreePruningBookingDetail().getTenantId();
            mobileNumber = tankerRequest.getTreePruningBookingDetail().getApplicantDetail().getMobileNumber();
            requestInfo = tankerRequest.getRequestInfo();
        } else {
            log.error("Unsupported request type: " + request.getClass().getName());
            return null;
        }

        String localizationMessages = util.getLocalizationMessages(tenantId, requestInfo);
        List<String> toUsers = new ArrayList<>();
        Map<String, String> mapOfPhoneNoAndUUIDs = util.fetchUserUUIDs(mobileNumber, requestInfo, tenantId);

        if (CollectionUtils.isEmpty(mapOfPhoneNoAndUUIDs.keySet())) {
            log.info("UUID search failed!");
        }

        toUsers.add(mapOfPhoneNoAndUUIDs.get(mobileNumber));
        String message = messageMap.get(NotificationUtil.MESSAGE_TEXT);
        log.info("Message for event in RequestService: " + message);

        Recepient recepient = Recepient.builder().toUsers(toUsers).toRoles(null).build();
        log.info("Recipient object in RequestService: " + recepient);

        ActionItem actionItem = ActionItem.builder().actionUrl(actionLink).code("LINK").build();
        List<ActionItem> actionItems = new ArrayList<>();
        actionItems.add(actionItem);

        Action action = Action.builder().tenantId(tenantId).id(mobileNumber).actionUrls(actionItems)
                .eventId(TreePruningConstants.CHANNEL_NAME_EVENT).build();

        events.add(Event.builder().tenantId(tenantId).description(message)
                .eventType(TreePruningConstants.USREVENTS_EVENT_TYPE)
                .name(TreePruningConstants.USREVENTS_EVENT_NAME)
                .postedBy(TreePruningConstants.USREVENTS_EVENT_POSTEDBY).source(Source.WEBAPP).recepient(recepient)
                .actions(null).eventDetails(null).build());

        return CollectionUtils.isEmpty(events) ? null
                : EventRequest.builder().requestInfo(requestInfo).events(events).build();
    }



    /**
     * Checks if a specific notification type is enabled.
     *
     * @param isEnabled        the flag indicating if the notification type is enabled
     * @param channels         the list of configured channels
     * @param notificationType the notification type to check
     * @return true if the notification is enabled, false otherwise
     */
    private boolean isNotificationEnabled(Boolean isEnabled, List<String> channels, String notificationType) {
        return Boolean.TRUE.equals(isEnabled) && channels.contains(notificationType);
    }

}
