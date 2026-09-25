package upyog.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import upyog.notification.config.NotificationProperties;
import upyog.notification.model.NotificationChannel;
import upyog.notification.model.NotificationContext;

/**
 * Resolves notification text from MDMS templates with optional localization fallback.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationMessageService {

    private final NotificationProperties properties;
    private final LocalizationMessageService localizationMessageService;

    public String resolveMessage(NotificationContext context, NotificationChannel channel) {
        String channelKey = channel.name();
        String mdmsTemplate = context.getMessageTemplates() == null
                ? null
                : context.getMessageTemplates().get(channelKey);

        if (properties.isMdmsMessagesEnabled() && StringUtils.hasText(mdmsTemplate)) {
            return localizationMessageService.injectVariables(mdmsTemplate, context.getTemplateVariables());
        }

        if (!properties.isLocalizationFallbackEnabled()) {
            log.warn("No MDMS message for module={}, action={}, channel={}",
                    context.getModule(), context.getAction(), channelKey);
            return null;
        }

        if (!StringUtils.hasText(context.getLocalizationModule())) {
            return null;
        }

        String localizationMessages = localizationMessageService.fetchMessages(
                context.getTenantId(), context.getLocalizationModule(), context.getRequestInfo());
        return localizationMessageService.resolveMessage(
                localizationMessages, context.getAction(), channel, context.getTemplateVariables());
    }
}
