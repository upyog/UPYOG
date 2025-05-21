package org.upyog.cdwm.notification.impl;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.cdwm.config.CNDConfiguration;
import org.upyog.cdwm.notification.CNDNotificationService;
import org.upyog.cdwm.notification.constants.NotificationConstants;
import org.upyog.cdwm.notification.util.NotificationUtil;
import org.upyog.cdwm.web.models.CNDApplicationRequest;
import org.upyog.cdwm.web.models.events.EventRequest;
import org.upyog.cdwm.web.models.notification.EmailRequest;
import org.upyog.cdwm.web.models.notification.SMSRequest;

import java.util.*;

/**
 * Service implementation for handling CND notifications.
 */
@Service
@Slf4j
public class CNDNotificationServiceImpl implements CNDNotificationService {

    @Autowired
    private NotificationUtil util;

    @Autowired
    private CNDConfiguration config;

    @Autowired
    private MessageServiceImpl messageService;

    /**
     * Processes the CND application request and sends notifications via different channels.
     *
     * @param request The {@link CNDApplicationRequest} containing application details and request information.
     * @param status  The current status of the CND application.
     *
     * This method:
     * <ul>
     *   <li>Retrieves localized messages based on the tenant ID and request info.</li>
     *   <li>Fetches customized messages for the notification.</li>
     *   <li>Prepares an {@link EventRequest} for app notifications.</li>
     *   <li>Fetches user mobile numbers and configured notification channels.</li>
     *   <li>Sends app notifications if an event request is available.</li>
     *   <li>Sends SMS notifications if enabled and applicable.</li>
     *   <li>Sends email notifications if enabled and applicable.</li>
     * </ul>
     */
    public void process(CNDApplicationRequest request, String status) {
        String tenantId = request.getCndApplication().getTenantId();
        RequestInfo requestInfo = request.getRequestInfo();

        String localizationMessages = messageService.getLocalizationMessages(tenantId, requestInfo);
        Map<String, String> messageMap = messageService.getCustomizedMsg(requestInfo, request.getCndApplication(), localizationMessages);

        EventRequest eventRequest = util.getEventsForCND(request, messageMap.get(NotificationConstants.ACTION_LINK), messageMap);
        log.info("Event Request in CND service process method: {}", eventRequest);

        Set<String> mobileNumbers = new HashSet<>(util.fetchUserUUIDs(new HashSet<>(), requestInfo, tenantId).keySet());
        List<String> configuredChannelNames = util.fetchChannelList(new RequestInfo(), tenantId.split("\\.")[0], config.getModuleName(), status);

        // Send app notification
        if (eventRequest != null && configuredChannelNames.contains(NotificationConstants.CHANNEL_NAME_EVENT)) {
            util.sendEventNotification(eventRequest);
        }

        // Send SMS notification
        if (configuredChannelNames.contains(NotificationConstants.CHANNEL_NAME_SMS)) {
            List<SMSRequest> smsRequests = new LinkedList<>();
            util.enrichSMSRequest(request, smsRequests);
            if (!CollectionUtils.isEmpty(smsRequests)) {
                util.sendSMS(smsRequests);
            }
        }

        // Send Email notification
        if (configuredChannelNames.contains(NotificationConstants.CHANNEL_NAME_SMS)) {
            Map<String, String> mapOfPhnoAndEmail = util.fetchUserEmailIds(mobileNumbers, requestInfo, tenantId);
            List<EmailRequest> emailRequests = util.createEmailRequest(requestInfo, messageMap.get(NotificationConstants.MESSAGE_TEXT), mapOfPhnoAndEmail);
            util.sendEmail(emailRequests);
        }
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

