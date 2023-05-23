package org.ksmart.birth.utils;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.json.JSONObject;
import org.ksmart.birth.birthcommon.model.SMSRequest;
import org.ksmart.birth.common.producer.BndProducer;
import org.ksmart.birth.common.repository.ServiceRequestRepository;
import org.ksmart.birth.config.BirthConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.ksmart.birth.utils.BirthConstants.INITIATOR_NAME_KEY;

@Component
@Slf4j
public class SmsUtil {
    @Autowired
    BirthConfiguration config;
    private BndProducer producer;
    private ServiceRequestRepository serviceRequestRepository;
    @Autowired
    SmsUtil(BndProducer producer, ServiceRequestRepository serviceRequestRepository) {
        this.producer = producer;
        this.serviceRequestRepository = serviceRequestRepository;
    }

    /**
     * Creates sms request for the each owners
     *
     * @param message
     *            The message for the specific tradeLicense
     * @param mobileNumberToOwnerName
     *            Map of mobileNumber to OwnerName
     * @return List of SMSRequest
     */
    public List<SMSRequest> createSMSRequest(String message, Map<String, String> mobileNumberToOwnerName) {
        List<SMSRequest> smsRequest = new LinkedList<>();
        for (Map.Entry<String, String> entryset : mobileNumberToOwnerName.entrySet()) {
            String customizedMsg = message.replace("{1}", entryset.getValue());
            customizedMsg = customizedMsg.replace(INITIATOR_NAME_KEY, entryset.getValue());
            smsRequest.add(new SMSRequest(entryset.getKey(), customizedMsg));
        }
        return smsRequest;
    }
    /**
     * Send the SMSRequest on the SMSNotification kafka topic
     *
     * @param smsRequestList
     *            The list of SMSRequest to be sent
     */
    public void sendSMS(List<SMSRequest> smsRequestList, boolean isSMSEnabled) {
        if (isSMSEnabled) {
            if (CollectionUtils.isEmpty(smsRequestList))
                log.info("Messages from localization couldn't be fetched!");
            for (SMSRequest smsRequest : smsRequestList) {
                producer.push(config.getSmsNotifTopic(), smsRequest);
                log.info("SMS SENT!");
            }
        }
    }

    /**
     * Fetches messages from localization service
     *
     * @param tenantId
     *            tenantId of the tradeLicense
     * @param requestInfo
     *            The requestInfo of the request
     * @return Localization messages for the module
     */
    public String getLocalizationMessages(String tenantId, RequestInfo requestInfo) {
        LinkedHashMap responseMap = (LinkedHashMap) serviceRequestRepository.fetchResult(getUri(tenantId, requestInfo),
                requestInfo);
        String jsonString = new JSONObject(responseMap).toString();
        return jsonString;
    }


    /**
     * Returns the uri for the localization call
     *
     * @param tenantId
     *            TenantId of the propertyRequest
     * @return The uri for localization search call
     */
    public StringBuilder getUri(String tenantId, RequestInfo requestInfo) {

        if (config.getIsLocalizationStateLevel())
            tenantId = tenantId.split("\\.")[0];

        String locale = BirthConstants.NOTIFICATION_LOCALE;
        if (!StringUtils.isEmpty(requestInfo.getMsgId()) && requestInfo.getMsgId().split("|").length >= 2)
            locale = requestInfo.getMsgId().split("\\|")[1];

        StringBuilder uri = new StringBuilder();
        uri.append(config.getLocalizationHost()).append(config.getLocalizationContextPath())
                .append(config.getLocalizationSearchEndpoint()).append("?").append("locale=").append(locale)
                .append("&tenantId=").append(tenantId).append("&module=").append(BirthConstants.CR_MDMS_MODULE);

        return uri;
    }


}
