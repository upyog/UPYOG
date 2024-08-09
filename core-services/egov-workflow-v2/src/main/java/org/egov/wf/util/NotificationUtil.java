package org.egov.wf.util;

import static org.egov.wf.util.WorkflowConstants.NOTIFICATION_LOCALE;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.wf.config.SmsConfig;
import org.egov.wf.config.WorkflowConfig;
import org.egov.wf.repository.ServiceRequestRepository;
import org.egov.wf.service.UserService;
import org.egov.wf.web.models.SMSRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NotificationUtil {
    
    private WorkflowConfig config;
	
	private SmsConfig smsConfig;

	private ServiceRequestRepository serviceRequestRepository;

	
	private RestTemplate restTemplate;

	@Autowired
	private UserService userService;

	@Autowired
	private ObjectMapper mapper;


	@Autowired
	public NotificationUtil(WorkflowConfig config,SmsConfig smsConfig, ServiceRequestRepository serviceRequestRepository) {
		this.config = config;
		this.smsConfig = smsConfig;
		this.serviceRequestRepository = serviceRequestRepository;
		this.restTemplate = restTemplate;
	}

	private String URL = "url";


	/**
	 * Creates customized message based on bpa
	 * 
	 * @param bpa
	 *            The bpa for which message is to be sent
	 * @param localizationMessage
	 *            The messages from localization
	 * @return customized message based on bpa
	 */
	public String getCustomizedMsg(String action, String applicationStatus, String roles, String localizationMessage) {
        StringBuilder notificationCode = new StringBuilder();

        notificationCode.append("BPA_").append(roles.toUpperCase()).append("_").append(action.toUpperCase()).append("_").append(applicationStatus.toUpperCase()).append("_SMS_MESSAGE");

        String path = "$..messages[?(@.code==\"{}\")].message";
        path = path.replace("{}", notificationCode);
        String message = null;
        try {
            ArrayList<String> messageObj = JsonPath.parse(localizationMessage).read(path);
            if(messageObj != null && messageObj.size() > 0) {
                message = messageObj.get(0);
            }
        } catch (Exception e) {
            log.warn("Fetching from localization failed", e);
        }

        return message;
    }


	/**
	 * Extracts message for the specific code
	 * 
	 * @param notificationCode
	 *            The code for which message is required
	 * @param localizationMessage
	 *            The localization messages
	 * @return message for the specific code
	 */
	@SuppressWarnings("rawtypes")
	public String getMessageTemplate(String notificationCode, String localizationMessage) {
		String path = "$..messages[?(@.code==\"{}\")].message";
		path = path.replace("{}", notificationCode);
		String message = null;
		try {
			List data = JsonPath.parse(localizationMessage).read(path);
			if (!CollectionUtils.isEmpty(data))
				message = data.get(0).toString();
			else
				log.error("Fetching from localization failed with code " + notificationCode);
		} catch (Exception e) {
			log.warn("Fetching from localization failed", e);
		}
		return message;
	}


	

	/**
	 * Returns the uri for the localization call
	 * 
	 * @param tenantId
	 *            TenantId of the propertyRequest
	 * @return The uri for localization search call
	 */
	public StringBuilder getUri(String tenantId, RequestInfo requestInfo) {

		// (config.getIsLocalizationStateLevel())
			tenantId = tenantId.split("\\.")[0];

	    String locale = NOTIFICATION_LOCALE;
		if (!StringUtils.isEmpty(requestInfo.getMsgId()) && requestInfo.getMsgId().split("|").length >= 2)
			locale = requestInfo.getMsgId().split("\\|")[1];

		StringBuilder uri = new StringBuilder();
//		uri.append(config.getLocalizationHost()).append(config.getLocalizationContextPath())
//				.append(config.getLocalizationSearchEndpoint()).append("?").append("locale=").append(locale)
//				.append("&tenantId=").append(tenantId).append("&module=").append(BPAConstants.SEARCH_MODULE);
//		
		return uri;
	}

	/**
	 * Fetches messages from localization service
	 * 
	 * @param tenantId
	 *            tenantId of the BPA
	 * @param requestInfo
	 *            The requestInfo of the request
	 * @return Localization messages for the module
	 */
	public String getLocalizationMessages(String tenantId, RequestInfo requestInfo,String module) {
        @SuppressWarnings("rawtypes")
        LinkedHashMap responseMap = (LinkedHashMap) serviceRequestRepository.fetchResult(getUri(tenantId, requestInfo, module),
                requestInfo);
        return new JSONObject(responseMap).toString();
    }

    /**
     *
     * @param tenantId Tenant ID
     * @param requestInfo Request Info object
     * @param module Module name
     * @return Return uri
     */
    public StringBuilder getUri(String tenantId, RequestInfo requestInfo, String module) {

        if (smsConfig.getIsLocalizationStateLevel())
            tenantId = tenantId.split("\\.")[0];        
        String locale = NOTIFICATION_LOCALE;
        if (!StringUtils.isEmpty(requestInfo.getMsgId()) && requestInfo.getMsgId().split("|").length >= 2)
            locale = requestInfo.getMsgId().split("\\|")[1];
        StringBuilder uri = new StringBuilder();
        uri.append(smsConfig.getLocalizationHost()).append(smsConfig.getLocalizationContextPath())
                .append(smsConfig.getLocalizationSearchEndpoint()).append("?").append("locale=").append(locale)
                .append("&tenantId=").append(tenantId).append("&module=").append(module);

        return uri;
    }


	/**
	 * Send the SMSRequest on the SMSNotification kafka topic
	 * 
	 * @param smsRequestList
	 *            The list of SMSRequest to be sent
	 */
	public void sendSMS(List<org.egov.wf.web.models.SMSRequest> smsRequestList, boolean isSMSEnabled) {
		if (isSMSEnabled) {
			if (CollectionUtils.isEmpty(smsRequestList))
				log.info("Messages from localization couldn't be fetched!");
			for (SMSRequest smsRequest : smsRequestList) {
				//producer.push(config.getSmsNotifTopic(), smsRequest);
			    //sms topic
				// SMS
//				@Value("${kafka.topics.notification.sms}")
//				private String smsNotifTopic;


				log.debug("MobileNumber: " + smsRequest.getMobileNumber() + " Messages: " + smsRequest.getMessage());
			}
			log.info("SMS notifications sent!");
		}
	}

	
//    public Object mDMSCall(ServiceRequest request){
//        RequestInfo requestInfo = request.getRequestInfo();
//        String tenantId = request.getService().getTenantId();
//        MdmsCriteriaReq mdmsCriteriaReq = getMDMSRequest(requestInfo,tenantId);
//        Object result = serviceRequestRepository.fetchResult(getMdmsSearchUrl(), mdmsCriteriaReq);
//        return result;
//    }

	
	 public StringBuilder getHRMSURI(List<String> uuids, String tenantId, String role){

	        StringBuilder builder = new StringBuilder(smsConfig.getHrmsHost());
	        builder.append(smsConfig.getHrmsEndPoint());
	        if(uuids!=null) {
	        builder.append("?uuids=");
	        builder.append(StringUtils.join(uuids, ","));
	        builder.append("&tenantId=");
	        builder.append(tenantId);

	        }
	        else
	        {
	        	 builder.append("?tenantId=");
	             builder.append(tenantId);
	        }
	        
	        builder.append("&roles=");
	        builder.append(role);

	        return builder;
	    }
}
