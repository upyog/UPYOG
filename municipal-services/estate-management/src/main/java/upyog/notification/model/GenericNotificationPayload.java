package upyog.notification.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.egov.common.contract.request.RequestInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Generic envelope published to the central notification Kafka topic / downstream services.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericNotificationPayload {

    private RequestInfo requestInfo;
    private String tenantId;
    private String module;
    private String action;
    private NotificationChannel channel;
    private String message;
    private String localizationCode;
    private Set<String> mobileNumbers;
    private Set<String> userUuids;
    private Set<String> emails;
    private String actionLink;
    private Map<String, String> additionalDetails;
    private List<Map<String, Object>> events;
}
