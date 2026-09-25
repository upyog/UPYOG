package upyog.notification.model;

import lombok.Builder;
import lombok.Data;
import org.egov.common.contract.request.RequestInfo;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Normalized context built from Kafka payload + MDMS JSONPath mappings.
 */
@Data
@Builder
public class NotificationContext {

    private RequestInfo requestInfo;
    private String tenantId;
    private String module;
    private String action;
    private String localizationModule;

    /** Channels resolved from MDMS config for this action. */
    @Builder.Default
    private Set<NotificationChannel> channels = Collections.emptySet();

    /** MDMS message templates keyed by channel (SMS, EVENT, EMAIL). */
    @Builder.Default
    private Map<String, String> messageTemplates = Collections.emptyMap();

    /** Primary recipient mobile numbers. */
    @Builder.Default
    private Set<String> mobileNumbers = Collections.emptySet();

    /** Recipient user UUIDs for in-app events. */
    @Builder.Default
    private Set<String> userUuids = Collections.emptySet();

    /** Recipient emails keyed by mobile (optional). */
    @Builder.Default
    private Map<String, String> emailByMobile = Collections.emptyMap();

    /**
     * Template placeholders, e.g. {@code alloteeName -> "Ravi"}, {@code allotmentNo -> "EST-AL-..."}.
     * Replaced in localization strings as {@code {alloteeName}}.
     */
    @Builder.Default
    private Map<String, String> templateVariables = Collections.emptyMap();

    /** Optional deep-link used by EVENT / SMS action buttons. */
    private String actionLink;
}
