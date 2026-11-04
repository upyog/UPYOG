package upyog.notification.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * MDMS-driven notification rule. Add or change rules in MDMS — no code change per module/action.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationConfig {

    private String module;
    private String action;

    /** Kafka topic that triggers this notification (e.g. save-allotment-details). */
    private String triggerTopic;

    /** Optional JSONPath field checked before sending (e.g. $.Allotments[0].status). */
    private String triggerField;

    /** Expected value of triggerField when present (e.g. PAID). */
    private String triggerValue;

    private List<String> channelNames;

    /**
     * Channel-specific message templates configured in MDMS.
     * Key: channel name (SMS, EVENT, EMAIL). Value: template with {@code {placeholder}} tokens.
     */
    private Map<String, String> messages;

    /** Optional localization fallback when a channel template is absent in MDMS. */
    private String localizationModule;
    private String tenantIdPath;

    /** Template placeholder name → JSONPath on the Kafka payload. */
    private Map<String, String> variables;

    private String recipientMobilePath;
    private String recipientUuidPath;
    private String recipientEmailPath;
}
