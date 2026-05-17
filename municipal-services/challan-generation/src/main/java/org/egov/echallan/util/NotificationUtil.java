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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
	private ChallanConfiguration config;

	private ServiceRequestRepository serviceRequestRepository;

	private RestTemplate restTemplate;

	private Producer producer;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	public NotificationUtil(ChallanConfiguration config, ServiceRequestRepository serviceRequestRepository,
			RestTemplate restTemplate, Producer producer) {
		this.config = config;
		this.serviceRequestRepository = serviceRequestRepository;
		this.restTemplate = restTemplate;
		this.producer=producer;
	}

/*
	public HashMap<String, String> getCustomizedMsg(RequestInfo requestInfo, Challan echallan ) {
		HashMap<String, String> msgDetail  = fetchContentFromLocalization(requestInfo,echallan.getTenantId(),MODULE,CREATE_CODE);
		msgDetail.put(MSG_KEY, getCreateMsg(requestInfo,echallan,msgDetail.get(MSG_KEY)));
		return msgDetail;
	}
	
	
	public HashMap<String, String> getCustomizedMsgForUpdate(RequestInfo requestInfo, Challan echallan ) {
		HashMap<String, String> msgDetail  =  fetchContentFromLocalization(requestInfo,echallan.getTenantId(),MODULE,UPDATE_CODE);
		msgDetail.put(MSG_KEY, getCreateMsg(requestInfo,echallan,msgDetail.get(MSG_KEY)));
		return msgDetail;
	}
	
	public HashMap<String, String> getCustomizedMsgForCancel(RequestInfo requestInfo, Challan echallan ) {
		HashMap<String, String> msgDetail  =  fetchContentFromLocalization(requestInfo,echallan.getTenantId(),MODULE,CANCEL_CODE);
		msgDetail.put(MSG_KEY, getCancelMsg(requestInfo,echallan,msgDetail.get(MSG_KEY)));
		return msgDetail;
	}

	private String getCancelMsg(RequestInfo requestInfo,Challan echallan, String message) {
		 HashMap<String, String> businessMsg  =  fetchContentFromLocalization(requestInfo,echallan.getTenantId(),MODULE,formatCodes(echallan.getBusinessService()));
		 message = message.replace("<citizen>",echallan.getCitizen().getName());
	     message = message.replace("<challanno>", echallan.getChallanNo());
	     message = message.replace("<service>", businessMsg.get(MSG_KEY));
	     return message;
	}
	*/
	private String getReplacedMsg(RequestInfo requestInfo, Challan challan, String message) {
	    // ======================================================
	    // CRITICAL CHECK 1: Ensure root objects are not null
	    // ======================================================
	    if (message == null || message.trim().isEmpty()) {
	        log.error("Email template is NULL. Database localization is missing.");
	        return "Your Municipal Challan has been generated. Please login to the portal to view details.";
	    }
	    if (challan == null) {
	        log.error("Challan object is NULL inside getReplacedMsg.");
	        return message;
	    }

	    // ======================================================
	    // CRITICAL CHECK 2: Safely extract nested variables
	    // ======================================================
	    String challanNo = challan.getChallanNo() != null ? challan.getChallanNo() : "N/A";
	    String tenantId = challan.getTenantId() != null ? challan.getTenantId() : "pb";
	    
	    String citizenName = "Citizen";
	    String mobileNo = "N/A";
	    if (challan.getCitizen() != null) {
	        citizenName = challan.getCitizen().getName() != null ? challan.getCitizen().getName() : "Citizen";
	        mobileNo = challan.getCitizen().getMobileNumber() != null ? challan.getCitizen().getMobileNumber() : "N/A";
	    }

	    String employeeName = "Enforcement Officer";
	    if (requestInfo != null && requestInfo.getUserInfo() != null && requestInfo.getUserInfo().getName() != null) {
	        employeeName = requestInfo.getUserInfo().getName();
	    }


	    // ==========================================
	    // 1. ORIGINAL LEGACY CODE
	    // ==========================================
	    if (challan.getApplicationStatus() != null && challan.getApplicationStatus() != Challan.StatusEnum.CANCELLED) {
	        try {
	            String billDetails = getBillDetails(requestInfo, challan);
	            if (billDetails != null && !billDetails.isEmpty()) {
	                Object obj = JsonPath.parse(billDetails).read(BILL_AMOUNT_JSONPATH);
	                if (obj != null) {
	                    BigDecimal amountToBePaid = new BigDecimal(obj.toString());
	                    message = message.replace("<amount>", amountToBePaid.toString());
	                    message = message.replace("{totalAmount}", amountToBePaid.toString());
	                } else if (challan.getChallanAmount() != null) {
	                    message = message.replace("<amount>", challan.getChallanAmount().toString());
	                    message = message.replace("{totalAmount}", challan.getChallanAmount().toString());
	                }
	            } else if (challan.getChallanAmount() != null) {
	                message = message.replace("<amount>", challan.getChallanAmount().toString());
	                message = message.replace("{totalAmount}", challan.getChallanAmount().toString());
	            }
	        } catch (Exception e) {
	            log.warn("Failed to get bill amount for challan {}, using challan amount: {}", challanNo, e.getMessage());
	            if (challan.getChallanAmount() != null) {
	                message = message.replace("<amount>", challan.getChallanAmount().toString());
	                message = message.replace("{totalAmount}", challan.getChallanAmount().toString());
	            }
	        }
	    }

	    message = message.replace("{User}", citizenName);
	    message = message.replace("<challanno>", challanNo);
	    
	    if (message.contains("{ULB}") && tenantId != null) {
	        String[] tenantParts = tenantId.split("\\.");
	        String ulbName = (tenantParts.length > 1) ? capitalize(tenantParts[1]) : capitalize(tenantId);
	        message = message.replace("{ULB}", ulbName);
	    }

	    String businessServiceStr = challan.getBusinessService();
	    String service = "";
	    if (businessServiceStr != null) {
	        String[] businessServiceParts = businessServiceStr.split("\\.");
	        String serviceName = businessServiceParts.length > 1 ? businessServiceParts[1] : businessServiceParts[0];
	        String[] split_array = capitalize(serviceName).split("_");
	        service = String.join(" ", split_array);
	    }

	    // CRITICAL CHECK 3: Ensure Config isn't null before calling .replace()
	    String paymentPath = (config != null) ? config.getPayLinkSMS() : null;
	    if (paymentPath != null) {
	        paymentPath = paymentPath.replace("$consumercode", challanNo);
	        paymentPath = paymentPath.replace("$tenantId", tenantId);
	        paymentPath = paymentPath.replace("$businessservice", businessServiceStr != null ? businessServiceStr : "");
	    }

	    String result = truncateAndSplitString(service, 33);
	    message = message.replace("<service>", result != null ? result : "");
	    
	    String portalFallbackLink = "https://mseva.lgpunjab.gov.in/citizen";
	    message = message.replace("<Link>", portalFallbackLink);


	    // ==========================================
	    // 2. NEW PROFESSIONAL TEMPLATE MAPPINGS
	    // ==========================================
	    
	    message = message.replace("{challanNumber}", challanNo);
	    message = message.replace("{citizenName}", citizenName);
	    message = message.replace("{mobileNumber}", mobileNo);
	    message = message.replace("{employeeName}", employeeName);
	    
	    String address = "Address not provided";
	    if (challan.getAddress() != null && challan.getAddress().getAddressLine1() != null) {
	        address = challan.getAddress().getAddressLine1();
	    }
	    message = message.replace("{address}", address);
	    
	    String cityName = "Municipal";
	    if (tenantId != null) {
	        String[] tenantParts = tenantId.split("\\.");
	        if (tenantParts.length > 1) {
	            cityName = capitalize(tenantParts[1]); 
	        } else {
	            cityName = capitalize(tenantId);
	        }
	    }
	    message = message.replace("{cityName}", cityName != null ? cityName : "Municipal");
	    
	    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
	    message = message.replace("{createdDate}", sdf.format(new Date(System.currentTimeMillis())));

	    message = message.replace("{offenceCategory}", challan.getOffenceCategoryName() != null ? challan.getOffenceCategoryName() : "General Violation");
	    message = message.replace("{offenceType}", challan.getOffenceSubCategoryName() != null ? challan.getOffenceSubCategoryName() : "Municipal Offence");
	    message = message.replace("{offenceDescription}", challan.getOffenceTypeName() != null ? challan.getOffenceTypeName() : "Violation of Municipal Bye-Laws");
	    
	    String act = "Punjab Municipal Bye-Laws";
	    if (challan.getAdditionalDetail() instanceof java.util.Map) {
	        java.util.Map<String, Object> ad = (java.util.Map<String, Object>) challan.getAdditionalDetail();
	        if (ad.get("offenceActs") != null) {
	            act = ad.get("offenceActs").toString();
	        }
	    }
	    message = message.replace("{offenceAct}", act);


	    // ==========================================
	    // 3. DOCUMENT AND PDF URL MAPPINGS
	    // ==========================================

	    String officialPdfUrl = getPdfAndPublicUrl(challan, requestInfo);
	    message = message.replace("{pdfDownloadUrl}", officialPdfUrl != null ? officialPdfUrl : portalFallbackLink);

	    return message;
	}
	
	// =========================================================
    // HELPER METHODS FOR PDF GENERATION (Paste inside NotificationUtil)
    // =========================================================

    public String getPdfAndPublicUrl(Challan challan, RequestInfo requestInfo) {
        try {
            // Using config to get the host (Ensure config.getPdfServiceHost() exists in your ChallanConfiguration)
            String pdfUrl = config.getPdfServiceHost() + "/pdf-service/v1/_create?tenantId=" + challan.getTenantId() + "&key=challan-notice";
            
            java.util.Map<String, Object> pdfRequest = new java.util.HashMap<>();
            pdfRequest.put("RequestInfo", requestInfo);
            pdfRequest.put("challan", preparePdfData(challan, requestInfo)); 

            // Call PDF Service
            java.util.Map<String, Object> response = restTemplate.postForObject(pdfUrl, pdfRequest, java.util.Map.class);
            
            if (response != null && response.get("filestoreIds") != null) {
                java.util.List<String> filestoreIds = (java.util.List<String>) response.get("filestoreIds");
                if (!filestoreIds.isEmpty()) {
                    String pdfFileStoreId = filestoreIds.get(0);
                    // Get the public URL for the newly generated PDF
                    return getPublicUrlFromFilestore(pdfFileStoreId, challan.getTenantId());
                }
            }
        } catch (Exception e) {
            log.error("Failed to generate PDF for challan: " + challan.getChallanNo(), e);
        }
        // Fallback if PDF generation fails
        return "https://mseva.lgpunjab.gov.in/citizen"; 
    }
 // =========================================================
    // HELPER METHOD FOR FILESTORE IMAGES
    // =========================================================
    public String getPublicUrlFromFilestore(String fileStoreId, String tenantId) {
        if (fileStoreId == null || fileStoreId.isEmpty()) return "";

        try {
            // Build the internal API URL using your config
            String url = config.getFileStoreHost() + config.getFileStoreViewPath()
                       + "?tenantId=" + tenantId
                       + "&fileStoreIds=" + fileStoreId;

            // Fetch the response from the Filestore API
            java.util.Map<String, Object> response = restTemplate.getForObject(url, java.util.Map.class);

            String devUrl = null;

            if (response != null) {
                // OPTION 1: Check if the ID is a direct key in the map (Common in mSeva)
                if (response.containsKey(fileStoreId)) {
                    devUrl = (String) response.get(fileStoreId);
                }
                // OPTION 2: Check if it is nested inside the fileStoreIds list
                else if (response.get("fileStoreIds") instanceof java.util.List) {
                    java.util.List<java.util.Map<String, Object>> fileList = 
                        (java.util.List<java.util.Map<String, Object>>) response.get("fileStoreIds");
                    
                    for (java.util.Map<String, Object> fileData : fileList) {
                        if (fileStoreId.equals(fileData.get("id"))) {
                            devUrl = (String) fileData.get("url");
                            break;
                        }
                    }
                }
            }

            // Apply Dev Proxy to bypass Gmail's security block for internal IPs
            if (devUrl != null && !devUrl.isEmpty()) {
                if (devUrl.contains("mseva-dev")) {
                    String cleanUrl = devUrl.replace("https://", "");
                    // Using weserv.nl public proxy so Gmail can render the image
                    return "https://images.weserv.nl/?url=" + cleanUrl;
                }
                return devUrl;
            }

        } catch (Exception e) {
            log.error("Filestore API Error for ID " + fileStoreId, e);
        }
        
        // Return empty string if failed so the fallback logic triggers
        return "";
    }
    private java.util.Map<String, Object> preparePdfData(Challan challan, RequestInfo requestInfo) {
        java.util.Map<String, Object> pdfData = new java.util.HashMap<>();

        // 1. Wrap the single challan into a List
        java.util.List<Challan> challanList = new java.util.ArrayList<>();
        challanList.add(challan);
        pdfData.put("challans", challanList);
        pdfData.put("countOfServices", 1);
        
        // 2. Calculate Total Amount
        java.math.BigDecimal totalAmount = java.math.BigDecimal.ZERO;
        if (challan.getAmount() != null) {
            for (Amount amt : challan.getAmount()) {
                if (amt.getAmount() != null) {
                    totalAmount = totalAmount.add(amt.getAmount()); 
                }
            }
        }
        pdfData.put("totalAmountCollected", totalAmount);
        
        // 3. Map the Officer/Employee details
        java.util.Map<String, Object> officer = new java.util.HashMap<>();
        if (requestInfo != null && requestInfo.getUserInfo() != null) {
            officer.put("name", requestInfo.getUserInfo().getName());
            officer.put("code", requestInfo.getUserInfo().getUserName());
        } else {
            officer.put("name", "System");
            officer.put("code", "SYS");
        }
        pdfData.put("officer", officer);

        // 4. Add Location
        pdfData.put("location", (challan.getAddress() != null) ? challan.getAddress().getAddressLine1() : "Address not provided");

        return pdfData;
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

	private String getPaymentMsg(RequestInfo requestInfo,Challan challan, String message) {
		ChallanRequest challanRequest = new ChallanRequest(requestInfo,challan);
		message = message.replace("{User}",challan.getCitizen().getName());
		message = message.replace("{challanno}", challan.getChallanNo());

        PaymentResponse paymentResponse = getPaymentObject(challanRequest);

		message = message.replace("{Payment_Amount}",paymentResponse.getPayments().get(0).getTotalAmountPaid().toString());
		message = message.replace("{Payment_Mode}",paymentResponse.getPayments().get(0).getPaymentMode().toLowerCase());
		message = message.replace("{Payment_No}",paymentResponse.getPayments().get(0).getPaymentDetails().get(0).getReceiptNumber());
		message = message.replace("{challanno}",paymentResponse.getPayments().get(0).getPaymentMode());

		if(message.contains("{Online_Receipt_Link}"))
			message = message.replace("{Online_Receipt_Link}", getRecepitDownloadLink(challanRequest,paymentResponse,challanRequest.getChallan().getCitizen().getMobileNumber()));

	    if(message.contains("{ULB}"))
			message = message.replace("{ULB}", capitalize(challan.getTenantId().split("\\.")[1]));

		return message;
	}

	private String formatCodes(String code) {
		String regexForSpecialCharacters = "[$&+,:;=?@#|'<>.^*()%!-]";
		code = code.replaceAll(regexForSpecialCharacters, "_");
		code = code.replaceAll(" ", "_");

		return BUSINESSSERVICELOCALIZATION_CODE_PREFIX + code.toUpperCase();
	}

	
	private String getBillDetails(RequestInfo requestInfo, Challan challan) {

		LinkedHashMap responseMap = (LinkedHashMap) serviceRequestRepository.fetchResult(getBillUri(challan),
				new RequestInfoWrapper(requestInfo));
		
		String jsonString = new JSONObject(responseMap).toString();

		return jsonString;
	}
	
	public String getShortenedUrl(String url){
		HashMap<String,String> body = new HashMap<>();
		body.put("url",url);
		StringBuilder builder = new StringBuilder(config.getUrlShortnerHost());
		builder.append(config.getUrlShortnerEndpoint());
		String res = restTemplate.postForObject(builder.toString(), body, String.class);
		if(StringUtils.isEmpty(res)){
			log.error("URL_SHORTENING_ERROR","Unable to shorten url: "+url); ;
			return url;
		}
		else return res;
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
		System.out.println("notificationCode=="+notificationCode);
		String message = null;
		try {
			Object messageObj = JsonPath.parse(localizationMessage).read(path);
			if (messageObj != null && messageObj instanceof ArrayList) {
				@SuppressWarnings("unchecked")
				ArrayList<String> messageList = (ArrayList<String>) messageObj;
				if (!messageList.isEmpty()) {
					message = messageList.get(0);
				}
			}
		} catch (Exception e) {
			log.warn("Fetching from localization failed", e);
		}
		log.info("Final msg: "+message);
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

		if (config.getIsLocalizationStateLevel())
			tenantId = tenantId.split("\\.")[0];
		
		String locale = NOTIFICATION_LOCALE;
		if (!StringUtils.isEmpty(requestInfo.getMsgId()) && requestInfo.getMsgId().split("|").length >= 2)
			locale = requestInfo.getMsgId().split("\\|")[1];

		StringBuilder uri = new StringBuilder();
		uri.append(config.getLocalizationHost()).append(config.getLocalizationContextPath())
				.append(config.getLocalizationSearchEndpoint()).append("?").append("locale=").append(locale)
				.append("&tenantId=").append(tenantId).append("&module=").append(MODULE);
//				.append("&codes=").append(CODES);

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
		if(StringUtils.isEmpty(tenantId))
			return masterData;
		MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestForChannelList(requestInfo, tenantId.split("\\.")[0]);

		Filter masterDataFilter = filter(
				where(ChallanConstants.MODULE).is(moduleName).and(ACTION).is(action)
		);

		try {
			Object response = restTemplate.postForObject(uri.toString(), mdmsCriteriaReq, Map.class);
			masterData = JsonPath.parse(response).read("$.MdmsRes.Channel.channelList[?].channelNames[*]", masterDataFilter);
		}catch(Exception e) {
			log.error("Exception while fetching workflow states to ignore: ",e);
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
			if (CollectionUtils.isEmpty(emailRequestList))
				log.debug("Messages from localization couldn't be fetched!");
			for (EmailRequest emailRequest : emailRequestList) {
				producer.push(config.getEmailNotifTopic(), emailRequest);
				log.debug("Email Request -> "+emailRequest.getEmail().toString());
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
			if (CollectionUtils.isEmpty(smsRequestList))
				log.debug("Messages from localization couldn't be fetched!");
			for (SMSRequest smsRequest : smsRequestList) {
				producer.push(config.getSmsNotifTopic(), smsRequest);
				log.debug("MobileNumber: " + smsRequest.getMobileNumber() + " Messages: " + smsRequest.getMessage());
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
		for(String mobileNo: mobileNumbers) {
			userSearchRequest.put("userName", mobileNo);
			try {
				Object user = serviceRequestRepository.fetchResult(uri, userSearchRequest);
				if(null != user) {
					if(JsonPath.read(user, "$.user[0].emailId")!=null) {
						String email = JsonPath.read(user, "$.user[0].emailId");
						if(email!=null && !StringUtils.isEmpty(email) )
							mapOfPhnoAndEmailIds.put(mobileNo, email);
					}
					else {
						log.error("Service returned null while fetching email for username - "+mobileNo);
					}
				}else {
					log.error("Service returned null while fetching user for username - "+mobileNo);
				}
			}catch(Exception e) {
				log.error("Exception while fetching user for username - "+mobileNo);
				log.error("Exception trace: ",e);
				continue;
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
	@SuppressWarnings("rawtypes")
	public String getLocalizationMessages(String tenantId, RequestInfo requestInfo) {

		LinkedHashMap responseMap = (LinkedHashMap) serviceRequestRepository.fetchResult(getUri(tenantId, requestInfo),
				requestInfo);
		String jsonString = new JSONObject(responseMap).toString();
		return jsonString;
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
	@SuppressWarnings("unchecked")
	public String getCustomizedMsg(RequestInfo requestInfo, Challan challan, String messageCode) {
		String localizationMessages = getLocalizationMessages(challan.getTenantId(), requestInfo);
		String message = null, messageTemplate;

		if(messageCode.equals(CREATE_CODE) || messageCode.equals(CREATE_CODE_INAPP))
		{
			messageTemplate = getMessageTemplate(messageCode, localizationMessages);
			message  = getReplacedMsg(requestInfo,challan,messageTemplate);
		}
		else if(messageCode.equals(UPDATE_CODE) || messageCode.equals(UPDATE_CODE_INAPP))
		{
			messageTemplate = getMessageTemplate(messageCode, localizationMessages);
			message  = getReplacedMsg(requestInfo,challan,messageTemplate);
		}
		else if(messageCode.equals(CANCEL_CODE) || messageCode.equals(CANCEL_CODE_INAPP))
		{
			messageTemplate = getMessageTemplate(messageCode, localizationMessages);
			message  = getReplacedMsg(requestInfo,challan,messageTemplate);
		}
		else if(messageCode.equals(PAYMENT_CODE) || messageCode.equals(PAYMENT_CODE_INAPP))
		{
			messageTemplate = getMessageTemplate(messageCode, localizationMessages);
			message  = getPaymentMsg(requestInfo,challan,messageTemplate);
		}

		return message;
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
	@SuppressWarnings("unchecked")
	public String getEmailCustomizedMsg(RequestInfo requestInfo, Challan challan, String messageCode) {
		String localizationMessages = getLocalizationMessages(challan.getTenantId(), requestInfo);
		String message = null, messageTemplate;

		if(messageCode.equals(CREATE_CODE_EMAIL))
		{
			messageTemplate = getMessageTemplate(messageCode, localizationMessages);
			message  = getReplacedMsg(requestInfo,challan,messageTemplate);
		}
		else if(messageCode.equals(UPDATE_CODE_EMAIL))
		{
			messageTemplate = getMessageTemplate(messageCode, localizationMessages);
			message  = getReplacedMsg(requestInfo,challan,messageTemplate);
		}
		else if(messageCode.equals(CANCEL_CODE_EMAIL))
		{
			messageTemplate = getMessageTemplate(messageCode, localizationMessages);
			message  = getReplacedMsg(requestInfo,challan,messageTemplate);
		}
		else if(messageCode.equals(PAYMENT_CODE_EMAIL))
		{
			messageTemplate = getMessageTemplate(messageCode, localizationMessages);
			message  = getPaymentMsg(requestInfo,challan,messageTemplate);
		}

		return message;
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
		String consumerCode,service;

		consumerCode = challanRequest.getChallan().getChallanNo();
		service = challanRequest.getChallan().getBusinessService();

		StringBuilder URL = getcollectionURL();
		URL.append(service).append("/_search").append("?").append("consumerCodes=").append(consumerCode)
				.append("&").append("tenantId=").append(challanRequest.getChallan().getTenantId());
		RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(challanRequest.getRequestInfo()).build();
		Object response = serviceRequestRepository.fetchResult(URL,requestInfoWrapper);
		PaymentResponse paymentResponse = mapper.convertValue(response, PaymentResponse.class);
		return paymentResponse;
	}

	public StringBuilder getcollectionURL() {
		StringBuilder builder = new StringBuilder();
		return builder.append(config.getCollectionServiceHost()).append(config.getCollectionServiceSearchEndPoint());
	}

}
