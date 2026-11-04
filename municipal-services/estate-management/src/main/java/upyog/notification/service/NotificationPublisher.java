package upyog.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import upyog.kafka.Producer;
import upyog.notification.config.NotificationProperties;
import upyog.notification.model.GenericNotificationPayload;
import upyog.notification.model.SMSRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pushes channel-specific payloads (and optionally a generic envelope) to Kafka.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationPublisher {

    private final Producer producer;
    private final NotificationProperties properties;

    public void publish(GenericNotificationPayload payload) {
        if (payload == null || payload.getChannel() == null) {
            return;
        }

        if (StringUtils.hasText(properties.getGenericNotificationTopic())) {
            producer.push(properties.getGenericNotificationTopic(), payload);
            log.info("Published generic notification to topic={}, module={}, action={}, channel={}",
                    properties.getGenericNotificationTopic(), payload.getModule(),
                    payload.getAction(), payload.getChannel());
        }

        switch (payload.getChannel()) {
            case SMS -> publishSms(payload);
            case EVENT -> publishEvent(payload);
            case EMAIL -> publishEmail(payload);
            default -> log.warn("Unsupported notification channel: {}", payload.getChannel());
        }
    }

    private void publishSms(GenericNotificationPayload payload) {
        if (CollectionUtils.isEmpty(payload.getMobileNumbers()) || !StringUtils.hasText(payload.getMessage())) {
            log.warn("Skipping SMS: missing mobile/message for action={}", payload.getAction());
            return;
        }
        for (String mobile : payload.getMobileNumbers()) {
            SMSRequest sms = SMSRequest.builder()
                    .mobileNumber(mobile)
                    .message(payload.getMessage())
                    .build();
            producer.push(properties.getSmsTopic(), sms);
            log.info("SMS notification queued for mobile ending ****{}",
                    mobile != null && mobile.length() > 4 ? mobile.substring(mobile.length() - 4) : "????");
        }
    }

    private void publishEvent(GenericNotificationPayload payload) {
        Map<String, Object> eventRequest = new HashMap<>();
        eventRequest.put("RequestInfo", payload.getRequestInfo());

        List<Map<String, Object>> events = payload.getEvents();
        if (CollectionUtils.isEmpty(events)) {
            Map<String, Object> event = new HashMap<>();
            event.put("tenantId", payload.getTenantId());
            event.put("description", payload.getMessage());
            event.put("eventType", "SYSTEMGENERATED");
            event.put("name", payload.getModule() + "-" + payload.getAction());
            event.put("postedBy", "SYSTEM-NOTIFICATION");
            event.put("source", "WEBAPP");

            Map<String, Object> recipient = new HashMap<>();
            recipient.put("toUsers", payload.getUserUuids() == null
                    ? List.of()
                    : new ArrayList<>(payload.getUserUuids()));
            recipient.put("toRoles", null);
            event.put("recepient", recipient);

            if (StringUtils.hasText(payload.getActionLink())) {
                Map<String, Object> actionItem = new HashMap<>();
                actionItem.put("actionUrl", payload.getActionLink());
                actionItem.put("code", "VIEW_DETAILS");
                Map<String, Object> action = new HashMap<>();
                action.put("tenantId", payload.getTenantId());
                action.put("actionUrls", List.of(actionItem));
                event.put("actions", action);
            }
            events = List.of(event);
        }
        eventRequest.put("events", events);
        producer.push(properties.getEventTopic(), eventRequest);
        log.info("EVENT notification queued for module={}, action={}", payload.getModule(), payload.getAction());
    }

    private void publishEmail(GenericNotificationPayload payload) {
        if (CollectionUtils.isEmpty(payload.getEmails()) || !StringUtils.hasText(payload.getMessage())) {
            log.warn("Skipping EMAIL: missing email/message for action={}", payload.getAction());
            return;
        }
        Map<String, Object> emailRequest = new HashMap<>();
        emailRequest.put("RequestInfo", payload.getRequestInfo());
        Map<String, Object> email = new HashMap<>();
        email.put("emailTo", payload.getEmails());
        email.put("isHTML", false);
        email.put("body", payload.getMessage());
        email.put("subject", payload.getModule() + " - " + payload.getAction());
        emailRequest.put("email", email);
        producer.push(properties.getEmailTopic(), emailRequest);
        log.info("EMAIL notification queued for action={}", payload.getAction());
    }
}
