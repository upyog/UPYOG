package upyog.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import upyog.notification.model.GenericNotificationPayload;
import upyog.notification.model.NotificationChannel;
import upyog.notification.model.NotificationContext;

import java.util.HashSet;
import java.util.Set;

/**
 * End-to-end orchestration: MDMS message templates → channel Kafka publish.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationOrchestrator {

    private final NotificationMessageService notificationMessageService;
    private final NotificationPublisher notificationPublisher;

    public void send(NotificationContext context) {
        if (context == null || context.getRequestInfo() == null) {
            log.warn("Notification skipped: incomplete context");
            return;
        }

        Set<NotificationChannel> channels = context.getChannels();
        if (CollectionUtils.isEmpty(channels)) {
            log.info("No channels configured for module={}, action={}", context.getModule(), context.getAction());
            return;
        }

        for (NotificationChannel channel : channels) {
            String message = notificationMessageService.resolveMessage(context, channel);
            if (!StringUtils.hasText(message)) {
                continue;
            }

            GenericNotificationPayload payload = GenericNotificationPayload.builder()
                    .requestInfo(context.getRequestInfo())
                    .tenantId(context.getTenantId())
                    .module(context.getModule())
                    .action(context.getAction())
                    .channel(channel)
                    .message(message)
                    .localizationCode(context.getAction() + "_" + channel.name())
                    .mobileNumbers(context.getMobileNumbers())
                    .userUuids(context.getUserUuids())
                    .emails(context.getEmailByMobile() == null
                            ? Set.of()
                            : new HashSet<>(context.getEmailByMobile().values()))
                    .actionLink(context.getActionLink())
                    .additionalDetails(context.getTemplateVariables())
                    .build();

            notificationPublisher.publish(payload);
        }
    }
}
