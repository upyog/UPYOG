package org.egov.echallan.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.*;

import org.egov.echallan.model.*;
import org.egov.echallan.producer.Producer;
import org.egov.echallan.web.models.collection.PaymentResponse;
import org.egov.echallan.web.models.uservevents.EventRequest;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.echallan.config.ChallanConfiguration;
import org.egov.echallan.repository.ServiceRequestRepository;
import org.json.JSONObject;
import org.springframework.stereotype.Component;


import java.math.BigDecimal;
import java.util.*;

import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;
import static org.egov.echallan.util.ChallanConstants.*;
import static org.springframework.util.StringUtils.capitalize;


@Component
@Slf4j
public class NotificationUtil {
	public static final String NOTIFICATION_LOCALE = "en_IN";
	public static final String MODULE ="rainmaker-uc";
	private static final String CODES = "echallan.create.sms";
	public static final String BILL_AMOUNT_JSONPATH = "$.Bill[0].totalAmount";
	public static final String BILL_DUEDATE = "$.Bill[0].billDetails[0].expiryDate";
	public static final String BUSINESSSERVICELOCALIZATION_CODE_PREFIX = "BILLINGSERVICE_BUSINESSSERVICE_";
	public static final String LOCALIZATION_CODES_JSONPATH = "$.messages[0].code";
	public static final String LOCALIZATION_MSGS_JSONPATH = "$.messages[0].message";
	public static final String LOCALIZATION_TEMPLATEID_JSONPATH = "$.messages[0].templateId";
	public static final String MSG_KEY="message";
	public static final String TEMPLATE_KEY="templateId";
	private static final String CREATE_CODE = "echallan.create.sms";
	private static final String UPDATE_CODE = "echallan.update.sms";
	private static final String CANCEL_CODE = "echallan.cancel.sms";
	private static final String AMOUNT_PLACEHOLDER = "<amount>";
	private static final String ULB_PLACEHOLDER = "{ULB}";

	private final ChallanConfiguration config;
	private final ServiceRequestRepository serviceRequestRepository;
	private final RestTemplate restTemplate;
	private final Producer producer;
	private final ObjectMapper mapper;

	public NotificationUtil(ChallanConfiguration config, ServiceRequestRepository serviceRequestRepository,
			RestTemplate restTemplate, Producer producer, ObjectMapper mapper) {
		this.config = config;
		this.serviceRequestRepository = serviceRequestRepository;
		this.restTemplate = restTemplate;
		this.producer = producer;
		this.mapper = mapper;
	}

	private String getReplacedMsg(RequestInfo requestInfo, Challan challan, String message) {
		message = replaceAmountPlaceholder(requestInfo, challan, message);
		message = message.replace("{User}", challan.getCitizen().getName());
		message = message.replace("<challanno>", challan.getChallanNo());
		message = replaceUlbPlaceholder(challan, message);

		String service = buildServiceName(challan.getBusinessService());
		String result = truncateAndSplitString(service, 33);
		message = message.replace("<service>", result);
		String newLink = "https://mseva.lgpunjab.gov.in/citizen";
		String updatedMessage = message.replace("<Link>", newLink);

		log.info("update{}", updatedMessage);
		log.info("Final msg after all rep: {}", updatedMessage);
		return updatedMessage;
	}

	private String replaceAmountPlaceholder(RequestInfo requestInfo, Challan challan, String message) {
		if (challan.getApplicationStatus() == Challan.StatusEnum.CANCELLED) {
			return message;
		}
		try {
			String billDetails = getBillDetails(requestInfo, challan);
			if (billDetails != null && !billDetails.isEmpty()) {
				Object obj = JsonPath.parse(billDetails).read(BILL_AMOUNT_JSONPATH);
				if (obj != null) {
					message = message.replace(AMOUNT_PLACEHOLDER, new BigDecimal(obj.toString()).toString());
					log.info("Replaced Amount");
					return message;
				}
			}
		} catch (Exception e) {
			log.warn("Failed to get bill amount for challan {}, using challan amount: {}",
					challan.getChallanNo(), e.getMessage());
		}
		return replaceAmountFromChallan(challan, message);
	}

	private String replaceAmountFromChallan(Challan challan, String message) {
		if (challan.getChallanAmount() != null) {
			return message.replace(AMOUNT_PLACEHOLDER, challan.getChallanAmount().toString());
		}
		return message;
	}

	private String replaceUlbPlaceholder(Challan challan, String message) {
		if (!message.contains(ULB_PLACEHOLDER)) {
			return message;
		}
		String[] tenantParts = challan.getTenantId().split("\\.");
		if (tenantParts.length > 1) {
			return message.replace(ULB_PLACEHOLDER, capitalize(tenantParts[1]));
		}
		return message.replace(ULB_PLACEHOLDER, capitalize(challan.getTenantId()));
	}

	private String buildServiceName(String businessServiceStr) {
		if (businessServiceStr == null) {
			return "";
		}
		String[] businessServiceParts = businessServiceStr.split("\\.");
		String serviceName = businessServiceParts.length > 1 ? businessServiceParts[1] : businessServiceParts[0];
		String[] splitArray = capitalize(serviceName).split("_");
		return String.join(" ", splitArray);
	}

	public static String truncateAndSplitString(String inputString, int truncateLength) {
        if (inputString.length() <= truncateLength) {
            return inputString;
        }

        String truncatedString = inputString.substring(0, truncateLength);

        int splitPosition = truncatedString.lastIndexOf(' ', 27);

        if (splitPosition == -1) {
            splitPosition = truncateLength;
        }
        return truncatedString.substring(0, splitPosition);
    }

	private String getPaymentMsg(RequestInfo requestInfo, Challan challan, String message) {
		ChallanRequest challanRequest = new ChallanRequest(requestInfo, challan);
		message = message.replace("{User}", challan.getCitizen().getName());
		message = message.replace("{challanno}", challan.getChallanNo());

		PaymentResponse paymentResponse = getPaymentObject(challanRequest);

		message = message.replace("{Payment_Amount}", paymentResponse.getPayments().get(0).getTotalAmountPaid().toString());
		message = message.replace("{Payment_Mode}", paymentResponse.getPayments().get(0).getPaymentMode().toLowerCase());
		message = message.replace("{Payment_No}", paymentResponse.getPayments().get(0).getPaymentDetails().get(0).getReceiptNumber());
		message = message.replace("{challanno}", paymentResponse.getPayments().get(0).getPaymentMode());

		if (message.contains("{Online_Receipt_Link}")) {
			message = message.replace("{Online_Receipt_Link}",
					getRecepitDownloadLink(challanRequest, paymentResponse, challanRequest.getChallan().getCitizen().getMobileNumber()));
		}

		if (message.contains(ULB_PLACEHOLDER)) {
			message = message.replace(ULB_PLACEHOLDER, capitalize(challan.getTenantId().split("\\.")[1]));
		}

		return message;
	}

	private String getBillDetails(RequestInfo requestInfo, Challan challan) {
		LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) serviceRequestRepository.fetchResult(
				getBillUri(challan), new RequestInfoWrapper(requestInfo));
		return new JSONObject(responseMap).toString();
	}
	
	public String getShortenedUrl(String url){
		HashMap<String,String> body = new HashMap<>();
		body.put("url",url);
		StringBuilder builder = new StringBuilder(config.getUrlShortnerHost());
		builder.append(config.getUrlShortnerEndpoint());
		String res = restTemplate.postForObject(builder.toString(), body, String.class);
		if (StringUtils.isEmpty(res)) {
			log.error("URL_SHORTENING_ERROR", "Unable to shorten url: " + url);
			return url;
		}
		return res;
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
	private String getMessageTemplate(String notificationCode, String localizationMessage) {
		String path = "$..messages[?(@.code==\"{}\")].message";
		path = path.replace("{}", notificationCode);
		log.debug("notificationCode=={}", notificationCode);
		String message = null;
		try {
			Object messageObj = JsonPath.parse(localizationMessage).read(path);
			if (messageObj instanceof ArrayList) {
				@SuppressWarnings("unchecked")
				ArrayList<String> messageList = (ArrayList<String>) messageObj;
				if (!messageList.isEmpty()) {
					message = messageList.get(0);
				}
			}
		} catch (Exception e) {
			log.warn("Fetching from localization failed", e);
		}
		log.info("Final msg: {}", message);
		return message;
	}

	/**
	 * Returns the uri for the localization call
	 * 
	 * @param tenantId
	 *            TenantId of the echallan
	 * @return The uri for localization search call
	 */
	public StringBuilder getUri(String tenantId, RequestInfo requestInfo) {

		if (config.getIsLocalizationStateLevel()) {
			tenantId = tenantId.split("\\.")[0];
		}
		
		String locale = NOTIFICATION_LOCALE;
		if (!StringUtils.isEmpty(requestInfo.getMsgId()) && requestInfo.getMsgId().split("|").length >= 2) {
			locale = requestInfo.getMsgId().split("\\|")[1];
		}

		StringBuilder uri = new StringBuilder();
		uri.append(config.getLocalizationHost()).append(config.getLocalizationContextPath())
				.append(config.getLocalizationSearchEndpoint()).append("?").append("locale=").append(locale)
				.append("&tenantId=").append(tenantId).append("&module=").append(MODULE)
				.append("&codes=").append(CODES);

		return uri;
	}
	
	private StringBuilder getBillUri(Challan challan) {
		StringBuilder builder = new StringBuilder(config.getBillingHost());
		builder.append(config.getFetchBillEndpoint());
		builder.append("?tenantId=");
		builder.append(challan.getTenantId());
		builder.append("&consumerCode=");
		builder.append(challan.getChallanNo());
		builder.append("&businessService=");
		builder.append(challan.getBusinessService());
		return builder;
	}

	public List<String> fetchChannelList(RequestInfo requestInfo, String tenantId, String moduleName, String action){
		List<String> masterData = new ArrayList<>();
		StringBuilder uri = new StringBuilder();
		uri.append(config.getMdmsHost()).append(config.getMdmsEndPoint());
		if (StringUtils.isEmpty(tenantId)) {
			return masterData;
		}
		MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestForChannelList(requestInfo, tenantId.split("\\.")[0]);

		Filter masterDataFilter = filter(
				where(ChallanConstants.MODULE).is(moduleName).and(ACTION).is(action)
		);

		try {
			Object response = restTemplate.postForObject(uri.toString(), mdmsCriteriaReq, Map.class);
			masterData = JsonPath.parse(response).read("$.MdmsRes.Channel.channelList[?].channelNames[*]", masterDataFilter);
		} catch (Exception e) {
			log.error("Exception while fetching workflow states to ignore: ", e);
		}
		return masterData;
	}

	private MdmsCriteriaReq getMdmsRequestForChannelList(RequestInfo requestInfo, String tenantId){
		MasterDetail masterDetail = new MasterDetail();
		masterDetail.setName(CHANNEL_LIST);
		List<MasterDetail> masterDetailList = new ArrayList<>();
		masterDetailList.add(masterDetail);

		ModuleDetail moduleDetail = new ModuleDetail();
		moduleDetail.setMasterDetails(masterDetailList);
		moduleDetail.setModuleName(CHANNEL);
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
	 * Send the EmailRequest on the EmailNotification kafka topic
	 *
	 * @param emailRequestList
	 *            The list of EmailRequest to be sent
	 */
	public void sendEmail(List<EmailRequest> emailRequestList) {

		if (config.getIsEmailNotificationEnabled()) {
			if (CollectionUtils.isEmpty(emailRequestList)) {
				log.debug("Messages from localization couldn't be fetched!");
			}
			for (EmailRequest emailRequest : emailRequestList) {
				producer.push(config.getEmailNotifTopic(), emailRequest);
				log.debug("Email Request -> {}", emailRequest.getEmail().toString());
				log.debug("EMAIL notification sent!");
			}
		}
	}

	/**
	 * Send the SMSRequest on the SMSNotification kafka topic
	 *
	 * @param smsRequestList
	 *            The list of SMSRequest to be sent
	 */
	public void sendSMS(List<SMSRequest> smsRequestList, boolean isSMSEnabled) {
		if (isSMSEnabled) {
			if (CollectionUtils.isEmpty(smsRequestList)) {
				log.debug("Messages from localization couldn't be fetched!");
			}
			for (SMSRequest smsRequest : smsRequestList) {
				producer.push(config.getSmsNotifTopic(), smsRequest);
				log.debug("MobileNumber: {} Messages: {}", smsRequest.getMobileNumber(), smsRequest.getMessage());
			}
		}
	}


	public void sendEventNotification(EventRequest request) {
		producer.push(config.getSaveUserEventsTopic(), request);
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
				if (user == null) {
					log.error("Service returned null while fetching user for username - {}", mobileNo);
				} else {
					String email = JsonPath.read(user, "$.user[0].emailId");
					if (!StringUtils.isEmpty(email)) {
						mapOfPhnoAndEmailIds.put(mobileNo, email);
					} else {
						log.error("Service returned null while fetching email for username - {}", mobileNo);
					}
				}
			} catch (Exception e) {
				log.error("Exception while fetching user for username - {}", mobileNo);
				log.error("Exception trace: ", e);
			}
		}
		return mapOfPhnoAndEmailIds;
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
	@SuppressWarnings("unchecked")
	public String getLocalizationMessages(String tenantId, RequestInfo requestInfo) {
		LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) serviceRequestRepository.fetchResult(
				getUri(tenantId, requestInfo), requestInfo);
		return new JSONObject(responseMap).toString();
	}

	/**
	 * Creates customized message based on bpa
	 *
	 * @param challan
	 *            The echallan for which message is to be sent
	 * @param messageCode
	 *            The message code for localization
	 * @return customized message based on echallan and code
	 */
	public String getCustomizedMsg(RequestInfo requestInfo, Challan challan, String messageCode) {
		String localizationMessages = getLocalizationMessages(challan.getTenantId(), requestInfo);
		String messageTemplate = getMessageTemplate(messageCode, localizationMessages);

		if (messageCode.equals(CREATE_CODE) || messageCode.equals(CREATE_CODE_INAPP)
				|| messageCode.equals(UPDATE_CODE) || messageCode.equals(UPDATE_CODE_INAPP)
				|| messageCode.equals(CANCEL_CODE) || messageCode.equals(CANCEL_CODE_INAPP)) {
			return getReplacedMsg(requestInfo, challan, messageTemplate);
		}
		if (messageCode.equals(PAYMENT_CODE) || messageCode.equals(PAYMENT_CODE_INAPP)) {
			return getPaymentMsg(requestInfo, challan, messageTemplate);
		}
		return null;
	}

	/**
	 * Creates customized message based on bpa
	 *
	 * @param challan
	 *            The echallan for which message is to be sent
	 * @param messageCode
	 *            The message code for localization
	 * @return customized message based on bpa
	 */
	public String getEmailCustomizedMsg(RequestInfo requestInfo, Challan challan, String messageCode) {
		String localizationMessages = getLocalizationMessages(challan.getTenantId(), requestInfo);
		String messageTemplate = getMessageTemplate(messageCode, localizationMessages);

		if (messageCode.equals(CREATE_CODE_EMAIL) || messageCode.equals(UPDATE_CODE_EMAIL)
				|| messageCode.equals(CANCEL_CODE_EMAIL)) {
			return getReplacedMsg(requestInfo, challan, messageTemplate);
		}
		if (messageCode.equals(PAYMENT_CODE_EMAIL)) {
			return getPaymentMsg(requestInfo, challan, messageTemplate);
		}
		return null;
	}

	public String getRecepitDownloadLink(ChallanRequest challanRequest, PaymentResponse paymentResponse, String mobileno) {

		String receiptNumber = paymentResponse.getPayments().get(0).getPaymentDetails().get(0).getReceiptNumber();
		String consumerCode = challanRequest.getChallan().getChallanNo();

		String link = config.getUiAppHost() + config.getReceiptDownloadLink();
		link = link.replace("$consumerCode", consumerCode);
		link = link.replace("$tenantId", challanRequest.getChallan().getTenantId());
		link = link.replace("$businessService", challanRequest.getChallan().getBusinessService());
		link = link.replace("$receiptNumber", receiptNumber);
		link = link.replace("$mobile", mobileno);
		link = getShortenedUrl(link);
		log.info(link);
		return link;
	}

	public PaymentResponse getPaymentObject(ChallanRequest challanRequest){
		String consumerCode = challanRequest.getChallan().getChallanNo();
		String service = challanRequest.getChallan().getBusinessService();

		StringBuilder collectionUrl = getcollectionURL();
		collectionUrl.append(service).append("/_search").append("?").append("consumerCodes=").append(consumerCode)
				.append("&").append("tenantId=").append(challanRequest.getChallan().getTenantId());
		RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(challanRequest.getRequestInfo()).build();
		Object response = serviceRequestRepository.fetchResult(collectionUrl, requestInfoWrapper);
		return mapper.convertValue(response, PaymentResponse.class);
	}

	public StringBuilder getcollectionURL() {
		StringBuilder builder = new StringBuilder();
		return builder.append(config.getCollectionServiceHost()).append(config.getCollectionServiceSearchEndPoint());
	}

}
