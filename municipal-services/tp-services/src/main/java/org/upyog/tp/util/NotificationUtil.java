package org.upyog.tp.util;
import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.tracer.model.CustomException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.upyog.tp.config.TreePruningConfiguration;
import org.upyog.tp.constant.TreePruningConstants;
import org.upyog.tp.kafka.Producer;
import org.upyog.tp.repository.ServiceRequestRepository;
import org.upyog.tp.web.models.events.EventRequest;
import org.upyog.tp.web.models.notification.Email;
import org.upyog.tp.web.models.notification.EmailRequest;
import org.upyog.tp.web.models.notification.SMSRequest;
import org.upyog.tp.web.models.treePruning.TreePruningBookingDetail;
import org.upyog.tp.web.models.treePruning.TreePruningBookingRequest;

import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NotificationUtil {

    private ServiceRequestRepository serviceRequestRepository;

    private TreePruningConfiguration config;

    private Producer producer;

    private RestTemplate restTemplate;

    @Autowired
    public NotificationUtil(ServiceRequestRepository serviceRequestRepository, TreePruningConfiguration config,
                            Producer producer, RestTemplate restTemplate) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.config = config;
        this.producer = producer;
        this.restTemplate = restTemplate;
    }

    public static final String ACTION_STATUS_APPLY = "APPLY";

    public static final String ACTION_STATUS_APPROVE = "APPROVE";

    public static final String ACTION_STATUS_VERIFY = "VERIFY";

    public static final String ACTION_STATUS_EXECUTE = "EXECUTE";

    public static final String ACTION_STATUS_REJECT = "REJECT";

    public static final String ACTION_STATUS_PAY = "PAY";

    public static final String ACTION_LINK = "ACTION_LINK";

    public static final String MESSAGE_TEXT = "MESSAGE_TEXT";

    private final String URL = "url";

    /**
     * Extracts message for the specific code
     *
     * @param notificationCode    The code for which message is required
     * @param localizationMessage The localization messages
     * @return message for the specific code
     */
    public String getMessageTemplate(String notificationCode, String localizationMessage) {

        String path = "$..messages[?(@.code==\"{}\")].message";
        path = path.replace("{}", notificationCode);
        String message = "";
        try {
            Object messageObj = JsonPath.parse(localizationMessage).read(path);
            message = ((ArrayList<String>) messageObj).get(0);
        } catch (Exception e) {
            log.warn("Fetching from localization failed", e);
        }
        return message;
    }

    /**
     * Fetches messages from localization service
     *
     * @param tenantId    tenantId of the PTR
     * @param requestInfo The requestInfo of the request
     * @return Localization messages for the module
     */
    public String getLocalizationMessages(String tenantId, RequestInfo requestInfo) {

        String locale = TreePruningConstants.NOTIFICATION_LOCALE;
        Boolean isRetryNeeded = false;
        String jsonString = null;
        LinkedHashMap responseMap = null;

        if (!StringUtils.isEmpty(requestInfo.getMsgId()) && requestInfo.getMsgId().split("\\|").length >= 2) {
            locale = requestInfo.getMsgId().split("\\|")[1];
            isRetryNeeded = true;
        }

        responseMap = (LinkedHashMap) serviceRequestRepository.fetchResult(getUri(tenantId, requestInfo, locale),
                requestInfo);
        jsonString = new JSONObject(responseMap).toString();

        if (StringUtils.isEmpty(jsonString) && isRetryNeeded) {

            responseMap = (LinkedHashMap) serviceRequestRepository.fetchResult(
                    getUri(tenantId, requestInfo, TreePruningConstants.NOTIFICATION_LOCALE), requestInfo);
            jsonString = new JSONObject(responseMap).toString();
            if (StringUtils.isEmpty(jsonString))
                throw new CustomException("UG_RS_LOCALE_ERROR",
                        "Localisation values not found for Request Service notifications");
        }
        return jsonString;
    }

    /**
     * Returns the uri for the localization call
     *
     * @return The uri for localization search call
     */
    public StringBuilder getUri(String tenantId, RequestInfo requestInfo, String locale) {

        if (config.getIsLocalizationStateLevel())
            tenantId = tenantId.split("\\.")[0];

        StringBuilder uri = new StringBuilder();
        uri.append(config.getLocalizationHost()).append(config.getLocalizationContextPath())
                .append(config.getLocalizationSearchEndpoint()).append("?").append("locale=").append(locale)
                .append("&tenantId=").append(tenantId).append("&module=")
                .append(TreePruningConstants.NOTIFICATION_MODULE_NAME);

        return uri;
    }

    /**
     * Creates sms request for the each owners
     *
     * @param mobileNumberToOwnerName Map of mobileNumber to OwnerName
     * @return List of SMSRequest
     */
    public List<SMSRequest> createSMSRequest(String message, Map<String, String> mobileNumberToOwnerName) {

        List<SMSRequest> smsRequest = new LinkedList<>();
        for (Map.Entry<String, String> entryset : mobileNumberToOwnerName.entrySet()) {
            String customizedMsg = message.replace(TreePruningConstants.NOTIFICATION_OWNERNAME, entryset.getValue());
            smsRequest.add(new SMSRequest(entryset.getKey(), customizedMsg));
        }
        return smsRequest;
    }

    /**
     * Send the SMSRequest on the SMSNotification kafka topic
     *
     * @param smsRequestList The list of SMSRequest to be sent
     */
    public void sendSMS(List<SMSRequest> smsRequestList) {

        if (config.getIsSMSNotificationEnabled()) {
            if (CollectionUtils.isEmpty(smsRequestList))
                log.info("Messages from localization couldn't be fetched!");
            for (SMSRequest smsRequest : smsRequestList) {
                producer.push(config.getSmsNotifTopic(), smsRequest);
                log.info("Sending SMS notification: ");
                log.info("MobileNumber: " + smsRequest.getMobileNumber() + " Messages: " + smsRequest.getMessage());
            }
        }
    }

    /**
     * Fetches UUIDs of CITIZEN based on the phone number.
     *
     * @param mobileNumber - Mobile Numbers
     * @param requestInfo  - Request Information
     * @param tenantId     - Tenant Id
     * @return Returns List of MobileNumbers and UUIDs
     */

    public Map<String, String> fetchUserUUIDs(String mobileNumber, RequestInfo requestInfo, String tenantId) {
        Map<String, String> mapOfPhoneNoAndUUIDs = new HashMap<>();
        StringBuilder uri = new StringBuilder();
        uri.append(config.getUserHost()).append(config.getUserSearchEndpoint());
        Map<String, Object> userSearchRequest = new HashMap<>();
        userSearchRequest.put("RequestInfo", requestInfo);
        userSearchRequest.put("tenantId", tenantId);
        userSearchRequest.put("userType", "CITIZEN");
        userSearchRequest.put("userName", mobileNumber);
        try {

            Object user = serviceRequestRepository.fetchResult(uri, userSearchRequest);
            log.info("User fetched in fetUserUUID method of RequestService notfication consumer" + user.toString());
            if (user != null) {
                String uuid = JsonPath.read(user, "$.user[0].uuid");
                mapOfPhoneNoAndUUIDs.put(mobileNumber, uuid);
                log.info("mapOfPhoneNoAndUUIDs : " + mapOfPhoneNoAndUUIDs);
            }
        } catch (Exception e) {
            log.error("Exception while fetching user for username - " + mobileNumber);
            log.error("Exception trace: ", e);
        }

        return mapOfPhoneNoAndUUIDs;
    }


    /**
     * Fetches UUIDs of CITIZENs based on the phone number.
     *
     * @param mobileNumbers
     * @param requestInfo
     * @param tenantId
     * @return
     */
    public Map<String, String> fetchUserUUIDs(Set<String> mobileNumbers, RequestInfo requestInfo, String tenantId) {

        Map<String, String> mapOfPhnoAndUUIDs = new HashMap<>();
        StringBuilder uri = new StringBuilder();
        uri.append(config.getUserHost()).append(config.getUserSearchEndpoint());
        Map<String, Object> userSearchRequest = new HashMap<>();
        userSearchRequest.put("RequestInfo", requestInfo);
        userSearchRequest.put("tenantId", tenantId);
        userSearchRequest.put("userType", "CITIZEN");
        for (String mobileNo : mobileNumbers) {
            userSearchRequest.put("userName", mobileNo);
            try {
                Object user = serviceRequestRepository.fetchResult(uri, userSearchRequest);
                if (null != user) {
                    String uuid = JsonPath.read(user, "$.user[0].uuid");
                    mapOfPhnoAndUUIDs.put(mobileNo, uuid);
                } else {
                    log.error("Service returned null while fetching user for username - " + mobileNo);
                }
            } catch (Exception e) {
                log.error("Exception while fetching user for username - " + mobileNo);
                log.error("Exception trace: ", e);
                continue;
            }
        }
        return mapOfPhnoAndUUIDs;
    }

    /**
     * Pushes the event request to Kafka Queue.
     *
     * @param request
     */
    public void sendEventNotification(EventRequest request) {
        log.info("EVENT notification sent!" + request);
        producer.push(config.getSaveUserEventsTopic(), request);
    }

    /**
     * Creates email request for the each owners
     *
     * @param mobileNumberToEmailId Map of mobileNumber to emailIds
     * @return List of EmailRequest
     */

    public List<EmailRequest> createEmailRequest(RequestInfo requestInfo, String message,
                                                 Map<String, String> mobileNumberToEmailId) {

        List<EmailRequest> emailRequest = new LinkedList<>();
        for (Map.Entry<String, String> entryset : mobileNumberToEmailId.entrySet()) {
            String customizedMsg = "";
            if (message.contains(TreePruningConstants.NOTIFICATION_EMAIL))
                customizedMsg = message.replace(TreePruningConstants.NOTIFICATION_EMAIL, entryset.getValue());

            String subject = "";
            String body = customizedMsg;
            Email emailobj = Email.builder().emailTo(Collections.singleton(entryset.getValue())).isHTML(false)
                    .body(body).subject(subject).build();
            EmailRequest email = new EmailRequest(requestInfo, emailobj);
            emailRequest.add(email);
        }
        return emailRequest;
    }

    /**
     * Send the EmailRequest on the EmailNotification kafka topic
     *
     * @param emailRequestList The list of EmailRequest to be sent
     */
    public void sendEmail(List<EmailRequest> emailRequestList) {

        if (config.getIsEmailNotificationEnabled()) {
            if (CollectionUtils.isEmpty(emailRequestList))
                log.info("Messages from localization couldn't be fetched!");
            for (EmailRequest emailRequest : emailRequestList) {
                if (!StringUtils.isEmpty(emailRequest.getEmail().getBody())) {
                    producer.push(config.getEmailNotifTopic(), emailRequest);
                    log.info("Sending EMAIL notification! ");
                    log.info("Email Id: " + emailRequest.getEmail().toString());
                } else {
                    log.info("Email body is empty, hence no email notification will be sent.");
                }
            }

        }
    }

    /**
     * Enriches the smsRequest with the customized messages
     *
     * @param request
     *            The request from kafka topic
     * @param smsRequests
     *            List of SMSRequets
     */

    public void enrichSMSRequest(Object request, List<SMSRequest> smsRequests) {
        String tenantId;
        String localizationMessages;
        Map<String, String> messageMap;
        String message;
        String mobileNumber;
        RequestInfo requestInfo;

        if (request instanceof TreePruningBookingRequest) {
            TreePruningBookingRequest waterRequest = (TreePruningBookingRequest) request;
            tenantId = waterRequest.getTreePruningBookingDetail().getTenantId();
            requestInfo = waterRequest.getRequestInfo();
            localizationMessages = getLocalizationMessages(tenantId, requestInfo);
            messageMap = getCustomizedMsg(requestInfo, waterRequest.getTreePruningBookingDetail(), localizationMessages);
            message = messageMap.get(TreePruningConstants.MESSAGE_TEXT);
            mobileNumber = waterRequest.getTreePruningBookingDetail().getApplicantDetail().getMobileNumber();
        } else {
            throw new IllegalArgumentException("Unsupported request type: " + request.getClass().getSimpleName());
        }

        Map<String, String> mobileNumberToOwner = fetchUserUUIDs(mobileNumber, requestInfo, tenantId);
        smsRequests.addAll(createSMSRequest(message, mobileNumberToOwner));
    }

    /**
     * Method to fetch the list of channels for a particular action from mdms config
     * from mdms configs returns the message minus some lines to match In App
     * Templates
     *
     * @param requestInfo
     * @param tenantId
     * @param moduleName
     * @param action
     */
    public List<String> fetchChannelList(RequestInfo requestInfo, String tenantId, String moduleName, String action) {
        List<String> masterData = new ArrayList<>();
        StringBuilder uri = new StringBuilder();
        uri.append(config.getMdmsHost()).append(config.getMdmsPath());
        if (StringUtils.isEmpty(tenantId))
            return masterData;
        MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestForChannelList(requestInfo, tenantId, moduleName, action);
        //Can create filter as string using this
        Filter masterDataFilter = filter(where(TreePruningConstants.MODULE).is(moduleName)
                .and(TreePruningConstants.ACTION).is(action));

        try {
            Object response = serviceRequestRepository.fetchResult(uri, mdmsCriteriaReq);
            masterData = JsonPath.parse(response).read("$.MdmsRes.Channel.channelList[?].channelNames[*]",
                    masterDataFilter);
        } catch (Exception e) {
            log.error("Exception while fetching workflow states to ignore: ", e);
        }

        return masterData;
    }

    /**
     * Constructs an MDMS (Master Data Management System) criteria request for retrieving 
     * the channel list based on the specified module and action.
     *
     * @param requestInfo The request information containing metadata about the request.
     * @param tenantId    The tenant ID for which the channel list is to be retrieved.
     * @param moduleName  The module name for filtering the data.
     * @param action      The specific action used to filter the channel list.
     * @return An {@link MdmsCriteriaReq} object containing the criteria for fetching 
     *         the channel list from MDMS.
     *
     * This method:
     * - Creates a {@link MasterDetail} object specifying the required master data.
     * - Builds a {@link ModuleDetail} object containing the master details.
     * - Constructs an {@link MdmsCriteria} with the provided tenant ID and module details.
     * - Wraps the criteria in an {@link MdmsCriteriaReq} and returns it.
     */

    private MdmsCriteriaReq getMdmsRequestForChannelList(RequestInfo requestInfo, String tenantId, String moduleName, String action) {

        MasterDetail masterDetail = new MasterDetail();
        masterDetail.setName(TreePruningConstants.CHANNEL_LIST);
        //	masterDetail.setFilter("[?(@['module'] == 'CND' && @['action'] == '"+ action +"')]");
        List<MasterDetail> masterDetailList = new ArrayList<>();
        masterDetailList.add(masterDetail);

        ModuleDetail moduleDetail = new ModuleDetail();
        moduleDetail.setMasterDetails(masterDetailList);
        moduleDetail.setModuleName(TreePruningConstants.CHANNEL);
        List<ModuleDetail> moduleDetailList = new ArrayList<>();
        moduleDetailList.add(moduleDetail);

        MdmsCriteria mdmsCriteria = new MdmsCriteria();
        mdmsCriteria.setTenantId(tenantId);
        mdmsCriteria.setModuleDetails(moduleDetailList);

        MdmsCriteriaReq mdmsCriteriaReq = new MdmsCriteriaReq();
        mdmsCriteriaReq.setMdmsCriteria(mdmsCriteria);
        mdmsCriteriaReq.setRequestInfo(requestInfo);

        return mdmsCriteriaReq;
    }

    /**
     * Fetches email ids of CITIZENs based on the phone number.
     *
     * @param mobileNumbers
     * @param requestInfo
     * @param tenantId
     * @return
     */

    public Map<String, String> fetchUserEmailIds(Set<String> mobileNumbers, RequestInfo requestInfo, String tenantId) {
        Map<String, String> mapOfPhnoAndEmailIds = new HashMap<>();
        StringBuilder uri = new StringBuilder();
        uri.append(config.getUserHost()).append(config.getUserSearchEndpoint());
        Map<String, Object> userSearchRequest = new HashMap<>();
        userSearchRequest.put("RequestInfo", requestInfo);
        userSearchRequest.put("tenantId", tenantId);
        userSearchRequest.put("userType", "CITIZEN");
        for (String mobileNo : mobileNumbers) {
            userSearchRequest.put("userName", mobileNo);
            try {
                Object user = serviceRequestRepository.fetchResult(uri, userSearchRequest);
                if (null != user) {
                    if (JsonPath.read(user, "$.user[0].emailId") != null) {
                        String email = JsonPath.read(user, "$.user[0].emailId");
                        mapOfPhnoAndEmailIds.put(mobileNo, email);
                    }
                } else {
                    log.error("Service returned null while fetching user for username - " + mobileNo);
                }
            } catch (Exception e) {
                log.error("Exception while fetching user for username - " + mobileNo);
                log.error("Exception trace: ", e);
                continue;
            }
        }
        return mapOfPhnoAndEmailIds;
    }

    /**
     * Generates a customized message based on the action status of the mobile toilet booking request.
     * This method retrieves localized message templates, formats them with booking details,
     * and optionally includes a payment link.
     *
     * @param requestInfo       The request information containing user details.
     * @param treePruningDetail The details of the mobile toilet booking.
     * @param localizationMessage The localization message string for fetching message templates.
     * @return A map containing the customized message and an optional action link.
     */
    public Map<String, String> getCustomizedMsg(RequestInfo requestInfo, TreePruningBookingDetail treePruningDetail,
                                                String localizationMessage) {
        String message = null, messageTemplate;
        String link = null;
        String ACTION_STATUS = treePruningDetail.getWorkflow().getAction();
        switch (ACTION_STATUS) {

            case ACTION_STATUS_APPLY:
                messageTemplate = getMessageTemplate(TreePruningConstants.NOTIFICATION_APPLY, localizationMessage);
                message = getMessageWithNumberAndFinalDetails(treePruningDetail, messageTemplate);
                break;

            case ACTION_STATUS_APPROVE:
                messageTemplate = getMessageTemplate(TreePruningConstants.NOTIFICATION_APPROVED, localizationMessage);
                message = getMessageWithNumberAndFinalDetails(treePruningDetail, messageTemplate);
                //	link = getPayUrl(treePruningDetail, message);
                break;

            case ACTION_STATUS_VERIFY:
                messageTemplate = getMessageTemplate(TreePruningConstants.NOTIFICATION_VEFIFY, localizationMessage);
                message = getMessageWithNumberAndFinalDetails(treePruningDetail, messageTemplate);
                break;

            case ACTION_STATUS_EXECUTE:
                messageTemplate = getMessageTemplate(TreePruningConstants.NOTIFICATION_EXECUTE, localizationMessage);
                message = getMessageWithNumberAndFinalDetails(treePruningDetail, messageTemplate);
                break;

            case ACTION_STATUS_REJECT:
                messageTemplate = getMessageTemplate(TreePruningConstants.NOTIFICATION_REJECT, localizationMessage);
                message = getMessageWithNumberAndFinalDetails(treePruningDetail, messageTemplate);
                break;

            case ACTION_STATUS_PAY:
                messageTemplate = getMessageTemplate(TreePruningConstants.NOTIFICATION_TREEPRUNINGBOOKED,
                        localizationMessage);
                message = getMessageWithNumberAndFinalDetails(treePruningDetail, messageTemplate);
                break;
        }


        if (message.contains("{PAY_LINK}")) {
            link = null;
            message = getPayUrl(treePruningDetail, message);

        }

        Map<String, String> messageMap = new HashMap<String, String>();
        messageMap.put(ACTION_LINK, link);
        messageMap.put(MESSAGE_TEXT, message);

        log.info("getCustomizedMsg messageTemplate : " + message);
        return messageMap;

        //return message;
    }

    private String getMessageWithNumberAndFinalDetails(TreePruningBookingDetail treePruningDetail, String message) {
        return String.format(message, treePruningDetail.getApplicantDetail().getName(), treePruningDetail.getBookingNo());
    }

    /**
     * Prepares and return url for view screen
     *
     * @param treePruningDetail
     * @return
     */
    public String getPayUrl(TreePruningBookingDetail treePruningDetail, String message) {
        String actionLink = String.format("%s?mobile=%s&applicationNo=%s&tenantId=%s&businessService=%s",
                config.getPayLink(),
                treePruningDetail.getApplicantDetail().getMobileNumber(),
                treePruningDetail.getBookingNo(),
                treePruningDetail.getTenantId(),
                config.getTreePruningBusinessService());

        message = message.replace("{PAY_LINK}", getShortenedUrl(config.getUiAppHost() + actionLink));

        return message;
    }

    /**
     * Method to shortent the url returns the same url if shortening fails
     *
     * @param url
     */
    public String getShortenedUrl(String url) {
        String res = null;
        HashMap<String, String> body = new HashMap<>();
        body.put("url", url);
        StringBuilder builder = new StringBuilder(config.getUrlShortnerHost());
        builder.append(config.getShortenerEndpoint());
        try {
            res = restTemplate.postForObject(builder.toString(), body, String.class);

        } catch (Exception e) {
            log.error("Error while shortening the url: " + url, e);

        }
        if (StringUtils.isEmpty(res)) {
            log.error("URL_SHORTENING_ERROR", "Unable to shorten url: " + url);
            return url;
        } else {
            return res;
        }
    }

}
