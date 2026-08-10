package upyog.notification.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class NotificationProperties {

    @Value("${upyog.notification.enabled:true}")
    private boolean enabled;

    @Value("${est.module.name:EstateManagement}")
    private String moduleName;

    @Value("${egov.mdms.host}")
    private String mdmsHost;

    @Value("${egov.mdms.search.endpoint}")
    private String mdmsEndPoint;

    @Value("${upyog.notification.mdms.config.module:Notification}")
    private String mdmsConfigModule;

    @Value("${upyog.notification.mdms.config.master:notificationConfig}")
    private String mdmsConfigMaster;

    /** MDMS config cache TTL in milliseconds (default 5 minutes). */
    @Value("${upyog.notification.config.cache.ttl.ms:300000}")
    private long configCacheTtlMs;

    /** When true, message text is read from MDMS notificationConfig.messages. */
    @Value("${upyog.notification.messages.from-mdms:true}")
    private boolean mdmsMessagesEnabled;

    /** When true, missing MDMS templates fall back to localization service. */
    @Value("${upyog.notification.messages.localization-fallback:false}")
    private boolean localizationFallbackEnabled;

    @Value("${egov.localization.host}")
    private String localizationHost;

    @Value("${egov.localization.context.path}")
    private String localizationContextPath;

    @Value("${egov.localization.search.endpoint}")
    private String localizationSearchEndpoint;

    @Value("${egov.localization.statelevel:true}")
    private boolean localizationStateLevel;

    @Value("${upyog.notification.default-locale:en_IN}")
    private String defaultLocale;

    @Value("${egov.sms.notification.topic}")
    private String smsTopic;

    @Value("${kafka.topics.notification.email:egov.core.notification.email}")
    private String emailTopic;

    @Value("${kafka.topics.notification.event:persist-user-events-async}")
    private String eventTopic;

    @Value("${upyog.notification.generic.topic:}")
    private String genericNotificationTopic;
}
