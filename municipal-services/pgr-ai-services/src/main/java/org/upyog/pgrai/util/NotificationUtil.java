package org.upyog.pgrai.util;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.utils.MultiStateInstanceUtil;
import org.upyog.pgrai.config.PGRConfiguration;
import org.upyog.pgrai.producer.Producer;
import org.upyog.pgrai.repository.ServiceRequestRepository;
import org.upyog.pgrai.web.models.Notification.EventRequest;
import org.upyog.pgrai.web.models.Notification.SMSRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.upyog.pgrai.util.PGRConstants.*;

/**
 * Utility class for handling notifications in the PGR system.
 * Provides methods for sending SMS, event notifications, and fetching localization messages.
 */
@Component
@Slf4j
public class NotificationUtil {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private PGRConfiguration config;

    @Autowired
    private Producer producer;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MultiStateInstanceUtil centralInstanceUtil;

    /**
     * Fetches localization messages for the given tenant, request info, and module.
     *
     * @param tenantId   The tenant ID.
     * @param requestInfo The request information.
     * @param module     The module name.
     * @return The localization messages as a JSON string.
     */
    public String getLocalizationMessages(String tenantId, RequestInfo requestInfo, String module) {
        @SuppressWarnings("rawtypes")
        LinkedHashMap responseMap = (LinkedHashMap) serviceRequestRepository.fetchResult(getUri(tenantId, requestInfo, module), requestInfo);
        return new JSONObject(responseMap).toString();
    }

    /**
     * Constructs the URI for fetching localization messages.
     *
     * @param tenantId   The tenant ID.
     * @param requestInfo The request information.
     * @param module     The module name.
     * @return The constructed URI.
     */
    public StringBuilder getUri(String tenantId, RequestInfo requestInfo, String module) {
        tenantId = centralInstanceUtil.getStateLevelTenant(tenantId);
        log.info("tenantId after calling central instance method: " + tenantId);
        String locale = NOTIFICATION_LOCALE;
        if (!StringUtils.isEmpty(requestInfo.getMsgId()) && requestInfo.getMsgId().split("\\|").length >= 2)
            locale = requestInfo.getMsgId().split("\\|")[1];
        StringBuilder uri = new StringBuilder();
        uri.append(config.getLocalizationHost()).append(config.getLocalizationContextPath())
                .append(config.getLocalizationSearchEndpoint()).append("?").append("locale=").append(locale)
                .append("&tenantId=").append(tenantId).append("&module=").append(module);

        return uri;
    }

    /**
     * Fetches a customized message based on the action, application status, roles, and localization message.
     *
     * @param action               The action performed.
     * @param applicationStatus    The application status.
     * @param roles                The roles (e.g., CITIZEN or EMPLOYEE).
     * @param localizationMessage  The localization message.
     * @return The customized message.
     */
    public String getCustomizedMsg(String action, String applicationStatus, String roles, String localizationMessage) {
        StringBuilder notificationCode = new StringBuilder();
        notificationCode.append("PGR_").append(roles.toUpperCase()).append("_").append(action.toUpperCase())
                .append("_").append(applicationStatus.toUpperCase()).append("_SMS_MESSAGE");

        String path = "$..messages[?(@.code==\"{}\")].message";
        path = path.replace("{}", notificationCode);
        String message = null;
        try {
            ArrayList<String> messageObj = JsonPath.parse(localizationMessage).read(path);
            if (messageObj != null && messageObj.size() > 0) {
                message = messageObj.get(0);
            }
        } catch (Exception e) {
            log.warn("Fetching from localization failed", e);
        }

        return message;
    }

    /**
     * Fetches the default message for the given roles and localization message.
     *
     * @param roles               The roles (e.g., EMPLOYEE or CITIZEN).
     * @param localizationMessage The localization message.
     * @return The default message.
     */
    public String getDefaultMsg(String roles, String localizationMessage) {
        StringBuilder notificationCode = new StringBuilder();
        notificationCode.append("PGR_").append("DEFAULT_").append(roles.toUpperCase()).append("_SMS_MESSAGE");

        String path = "$..messages[?(@.code==\"{}\")].message";
        path = path.replace("{}", notificationCode);
        String message = null;
        try {
            ArrayList<String> messageObj = JsonPath.parse(localizationMessage).read(path);
            if (messageObj != null && messageObj.size() > 0) {
                message = messageObj.get(0);
            }
        } catch (Exception e) {
            log.warn("Fetching from localization failed", e);
        }

        return message;
    }

    /**
     * Sends SMS requests to the SMS notification Kafka topic.
     *
     * @param tenantId       The tenant ID.
     * @param smsRequestList The list of SMS requests to be sent.
     */
    public void sendSMS(String tenantId, List<SMSRequest> smsRequestList) {
        if (config.getIsSMSEnabled()) {
            if (CollectionUtils.isEmpty(smsRequestList)) {
                log.info("Messages from localization couldn't be fetched!");
                return;
            }
            for (SMSRequest smsRequest : smsRequestList) {
                producer.push(tenantId, config.getSmsNotifTopic(), smsRequest);
                log.info("Messages: " + smsRequest.getMessage());
            }
        }
    }

    /**
     * Sends event notifications by pushing the event request to the Kafka queue.
     *
     * @param tenantId The tenant ID.
     * @param request  The event request object.
     */
    public void sendEventNotification(String tenantId, EventRequest request) {
        producer.push(tenantId, config.getSaveUserEventsTopic(), request);
        log.info("Events added to send: " + request.getEvents());
    }

    /**
     * Shortens the given URL using the URL shortener service.
     *
     * @param actualURL The actual URL to be shortened.
     * @return The shortened URL.
     */
    public String getShortnerURL(String actualURL) {
        HashMap<String, String> body = new HashMap<>();
        body.put("url", actualURL);
        StringBuilder builder = new StringBuilder(config.getUrlShortnerHost());
        builder.append(config.getUrlShortnerEndpoint());
        String res = restTemplate.postForObject(builder.toString(), body, String.class);

        if (StringUtils.isEmpty(res)) {
            log.error("URL_SHORTENING_ERROR", "Unable to shorten url: " + actualURL);
            return actualURL;
        } else return res;
    }

    /**
     * Fetches a customized message for a given localization message and notification code.
     *
     * @param localizationMessage The localization message.
     * @param notificationCode    The notification code.
     * @return The customized message.
     */
    public String getCustomizedMsgForPlaceholder(String localizationMessage, String notificationCode) {
        String path = "$..messages[?(@.code==\"{}\")].message";
        path = path.replace("{}", notificationCode);
        String message = null;
        try {
            ArrayList<String> messageObj = (ArrayList<String>) JsonPath.parse(localizationMessage).read(path);
            if (messageObj != null && messageObj.size() > 0) {
                message = messageObj.get(0);
            }
        } catch (Exception e) {
            log.warn("Fetching from localization for placeholder failed", e);
        }
        return message;
    }
}