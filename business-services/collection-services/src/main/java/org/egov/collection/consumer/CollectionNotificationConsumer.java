package org.egov.collection.consumer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.egov.collection.model.Payment;
import org.egov.collection.model.PaymentDetail;
import org.egov.collection.model.PaymentRequest;
import org.egov.collection.producer.CollectionProducer;
import org.egov.collection.web.contract.Bill;
import org.egov.common.contract.request.RequestInfo;
import org.egov.collection.config.ApplicationProperties;

import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;

import static org.egov.collection.config.CollectionServiceConstants.*;

@Slf4j
@Component
public class CollectionNotificationConsumer {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ApplicationProperties applicationProperties;

	@Autowired
	private CollectionProducer producer;

	@Autowired
	private RestTemplate restTemplate;

	@KafkaListener(topics = { "${kafka.topics.payment.create.name}", "${kafka.topics.payment.receiptlink.name}" },
			concurrency =  "${kafka.topics.bankaccountservicemapping.concurreny.count}" )
	public void listen(HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
		try {
			PaymentRequest req = objectMapper.convertValue(record, PaymentRequest.class);
			sendNotification(req);
		} catch (Exception e) {
			log.error("Exception while reading from the queue: ", e);
		}
	}
	private void sendNotification(PaymentRequest paymentRequest) {
	    Payment payment = paymentRequest.getPayment();
	    RequestInfo requestInfo = paymentRequest.getRequestInfo();
	    
	    for (PaymentDetail paymentDetail : payment.getPaymentDetails()) {
	        Bill bill = paymentDetail.getBill();
	        String mobNo = payment.getMobileNumber();
	        String emailId = (bill != null) ? bill.getPayerEmail() : null;

	        String paymentStatus = (payment.getPaymentStatus() == null) ? "NEW" : payment.getPaymentStatus().toString();
	        
	        // 1. Build the dynamic body content (text part used for SMS and [MAIL_CONTENT])
	        String bodyContent = buildSmsBody(bill, paymentDetail, requestInfo, paymentStatus);
	        
	        if (!StringUtils.isEmpty(bodyContent)) {
	            // --- SMS Notification ---
	            HashMap<String, Object> smsRequest = new HashMap<>();
	            smsRequest.put("mobileNumber", mobNo);
	            smsRequest.put("message", bodyContent);
	            producer.producer(applicationProperties.getSmsTopic(), smsRequest);

	            // --- HTML Email Notification ---
	            if (!StringUtils.isEmpty(emailId) && emailId.contains("@")) {
	                String subject = "Payment Confirmation - " + paymentDetail.getReceiptNumber();
	                
	                // 2. Wrap the localized bodyContent inside the professional HTML Template
	                String htmlContent = buildHtmlEmailFromLocalization(requestInfo, bill, paymentDetail, payment,subject, bodyContent);

	                HashMap<String, Object> emailRequest = new HashMap<>();
	                emailRequest.put("emailTo", emailId);
	                emailRequest.put("body", htmlContent);
	                emailRequest.put("subject", subject);
	                emailRequest.put("isHTML", true);

	                producer.producer(applicationProperties.getEmailTopic(), emailRequest);
	            }
	        } else {
	            log.error("Message content empty! No notification sent for Receipt: " + paymentDetail.getReceiptNumber());
	        }
	    }
	}
	private String buildHtmlEmailFromLocalization(RequestInfo requestInfo, Bill bill, PaymentDetail paymentDetail, Payment payment, String subject, String bodyContent) {
	    
	    // 1. Fetch the Template
	    String template = fetchContentFromLocalization(requestInfo, paymentDetail.getTenantId(),
	            COLLECTION_LOCALIZATION_MODULE, EMAIL_MESSAGE );

	    if (StringUtils.isEmpty(template)) {
	        log.error("Email Template not found in localization!");
	        return bodyContent; 
	    }

	    // 2. Fetch the actual PDF Download Link
	    // We pass the payment as a list because the service expects a List<Payment>
	    List<Payment> paymentList = Collections.singletonList(payment);
	    String downloadUrl = getPublicReceiptUrl(paymentList, requestInfo);
	    
	    // Fallback if PDF service fails
	    if (StringUtils.isEmpty(downloadUrl)) {
	        downloadUrl = "https://mseva.lgpunjab.gov.in/citizen";
	    }

	    // 3. Map Service Type (Using your helper method)
	    String serviceType = mapServiceCode(paymentDetail.getBusinessService());

	    // 4. City Name Title Case (pb.amritsar -> Amritsar)
	    String cityName = "Punjab";
	    if (paymentDetail.getTenantId() != null && paymentDetail.getTenantId().contains(".")) {
	        String rawCity = paymentDetail.getTenantId().split("\\.")[1];
	        cityName = rawCity.substring(0, 1).toUpperCase() + rawCity.substring(1).toLowerCase();
	    }

	    // 5. BigDecimal Calculation for Balance
	    java.math.BigDecimal totalDue = paymentDetail.getTotalDue() != null ? paymentDetail.getTotalDue() : java.math.BigDecimal.ZERO;
	    java.math.BigDecimal amountPaid = paymentDetail.getTotalAmountPaid() != null ? paymentDetail.getTotalAmountPaid() : java.math.BigDecimal.ZERO;
	    java.math.BigDecimal balance = totalDue.subtract(amountPaid);
	    
	    if (balance.compareTo(java.math.BigDecimal.ZERO) < 0) {
	        balance = java.math.BigDecimal.ZERO;
	    }

	    // 6. Date formatting (IST)
	    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MMM-yyyy HH:mm");
	    sdf.setTimeZone(java.util.TimeZone.getTimeZone("Asia/Kolkata")); 
	    String formattedDate = sdf.format(new java.util.Date(payment.getTransactionDate()));

	    // 7. Execute Replacements
	    return template
	            .replace("{cityName}", cityName)
	            .replace("{serviceType}", serviceType)
	            .replace("{ownerName}", (bill.getPayerName() != null) ? bill.getPayerName() : "Citizen")
	            .replace("{consumerCode}", (bill.getConsumerCode() != null) ? bill.getConsumerCode() : "N/A")
	            .replace("{referenceNo}", paymentDetail.getReceiptNumber())
	            .replace("{transactionDate}", formattedDate)
	            .replace("{paymentMode}", payment.getPaymentMode() != null ? payment.getPaymentMode().toString() : "CASH")
	            .replace("{transactionId}", payment.getTransactionNumber() != null ? payment.getTransactionNumber() : "N/A")
	            .replace("{totalPaid}", String.format("%.2f", amountPaid))
	            .replace("{balanceAmount}", String.format("%.2f", balance))
	            .replace("{actionButtonText}", "Download Official Receipt")
	            .replace("https://mseva.lgpunjab.gov.in/citizen", downloadUrl); // Injects the PDF link
	}
	
	public String getPublicReceiptUrl(List<Payment> validatedPayments, RequestInfo requestInfo) {
	    if (CollectionUtils.isEmpty(validatedPayments)) return null;
	    
	    String stateId = validatedPayments.get(0).getTenantId().split("\\.")[0];
	    String fileStoreId = null;

	    try {
	        // --- STEP 1: Generate the PDF and get FileStoreId ---
	        String pdfUri = applicationProperties.getEgovServiceHost() 
	                      + applicationProperties.getEgovPdfCreate() 
	                      + "?key=consolidatedreceipt&tenantId=" + stateId;

	        Map<String, Object> pdfRequest = new HashMap<>();
	        pdfRequest.put("RequestInfo", requestInfo);
	        pdfRequest.put("Payments", validatedPayments);

	        Map<String, Object> pdfResponse = restTemplate.postForObject(pdfUri, pdfRequest, Map.class);
	        
	        if (pdfResponse != null && pdfResponse.containsKey("filestoreIds")) {
	            List<String> ids = (List<String>) pdfResponse.get("filestoreIds");
	            if (!ids.isEmpty()) {
	                fileStoreId = ids.get(0); // Pick the first ID
	            }
	        }

	        if (StringUtils.isEmpty(fileStoreId)) {
	            log.error("PDF Service failed to return a filestoreId");
	            return null;
	        }

	        // --- STEP 2: Convert FileStoreId to a Public URL ---
	        String fileStoreUri = applicationProperties.getFileStoreHost()  
	                            + "/filestore/v1/files/url" 
	                            + "?tenantId=" + stateId 
	                            + "&fileStoreIds=" + fileStoreId;

	        // Note: The /url endpoint is a GET request
	        Map<String, Object> urlResponse = restTemplate.getForObject(fileStoreUri, Map.class);

	        if (urlResponse != null && urlResponse.containsKey("fileStoreIds")) {
	            List<Map<String, String>> fileDetails = (List<Map<String, String>>) urlResponse.get("fileStoreIds");
	            if (!fileDetails.isEmpty()) {
	                String publicUrl = fileDetails.get(0).get("url");
	                log.info("Successfully generated public receipt link: " + publicUrl);
	                return publicUrl;
	            }
	        }

	    } catch (Exception e) {
	        log.error("Error in the Receipt Generation/URL flow: ", e);
	    }

	    return null;
	}
	
	
	private String mapServiceCode(String code) {
	    if (code == null || code.isEmpty()) return "General Municipal Service";
	    
	    String upperCode = code.toUpperCase();

	    // 1. Direct Overrides for common/important codes
	    switch (upperCode) {
	        case "PT": return "Property Tax";
	        case "WS": return "Water Supply";
	        case "SW": return "Sewerage Service";
	        case "BPA": return "Building Plan Approval";
	        case "TL": return "Trade License";
	        case "FIRENOC": return "Fire NOC";
	        case "NDC": return "No Due Certificate";
	        case "BPAREG": return "Building Plan Registration";
	        case "ADVT.HOARDINGS": return "Advertisement Hoardings";
	    }

	    // 2. Prefix-Based Logic for the groups in your list
	    if (upperCode.startsWith("NKS.")) return "Building Control (NKS)";
	    if (upperCode.startsWith("CH.")) return "Enforcement Challan";
	    if (upperCode.startsWith("RT.")) return "Municipal Rent/Lease";
	    if (upperCode.startsWith("FTP.")) return "Town Planning (FTP)";
	    if (upperCode.startsWith("ADVT.")) return "Advertisement Tax";
	    if (upperCode.startsWith("OTHER.")) return "Miscellaneous Fees";
	    if (upperCode.startsWith("TX.")) return "Tax & Revenue";
	    if (upperCode.startsWith("FN.")) return "Finance/Account Deposit";
	    if (upperCode.startsWith("SNT.")) return "Sanitation & Health";
	    if (upperCode.startsWith("ADMN.")) return "Administration Fees";
	    if (upperCode.startsWith("OM.")) return "Operations & Maintenance";
	    if (upperCode.startsWith("WF.")) return "Works/Tender Fees";
	    if (upperCode.startsWith("LAYOUT.")) return "Layout Approval";
	    if (upperCode.startsWith("GC.")) return "Garbage Collection";

	    // 3. Fallback: Clean up the code if no match (e.g., "BPA.NC_APP_FEE" -> "Bpa Nc App Fee")
	    return formatCodeString(code);
	}

	// Helper to make raw codes readable if no mapping is found
	private String formatCodeString(String code) {
	    String formatted = code.replace("_", " ").replace(".", " - ");
	    return formatted.substring(0, 1).toUpperCase() + formatted.substring(1).toLowerCase();
	}
	
	
	private String buildSmsBody(Bill bill, PaymentDetail paymentDetail, RequestInfo requestInfo, String paymentStatus) {
		log.info("Inside BodySms");;
		String message = null;
		String content = null;
		switch (paymentStatus.toUpperCase()) {
		case "NEW":
			content = fetchContentFromLocalization(requestInfo, paymentDetail.getTenantId(),
					COLLECTION_LOCALIZATION_MODULE, WF_MT_STATUS_OPEN_CODE);
			break;
		case "DEPOSITED":
			content = fetchContentFromLocalization(requestInfo, paymentDetail.getTenantId(),
					COLLECTION_LOCALIZATION_MODULE, WF_MT_STATUS_DEPOSITED_CODE);
			break;
		case "CANCELLED":
			content = fetchContentFromLocalization(requestInfo, paymentDetail.getTenantId(),
					COLLECTION_LOCALIZATION_MODULE, WF_MT_STATUS_CANCELLED_CODE);
			break;
		case "DISHONOURED":
			content = fetchContentFromLocalization(requestInfo, paymentDetail.getTenantId(),
					COLLECTION_LOCALIZATION_MODULE, WF_MT_STATUS_DISHONOURED_CODE);
			break;
		default:
			break;
		}
		if (!StringUtils.isEmpty(content)) {
			if (!paymentStatus.equalsIgnoreCase("NEW"))
			{
				StringBuilder link = new StringBuilder();
				link.append(applicationProperties.getUiHost() + "/citizen").append("/otpLogin?mobileNo=")
						.append(bill.getMobileNumber()).append("&redirectTo=")
						.append(applicationProperties.getUiRedirectUrl()).append("&params=")
						.append(paymentDetail.getTenantId() + "," + paymentDetail.getReceiptNumber());

				String receiptLink = getShortenedUrl(link.toString());

				content = content.replaceAll("{rcpt_link}", receiptLink);

				String moduleName = fetchContentFromLocalization(requestInfo, paymentDetail.getTenantId(),
						BUSINESSSERVICE_LOCALIZATION_MODULE, formatCodes(paymentDetail.getBusinessService()));

				if (StringUtils.isEmpty(moduleName))
					moduleName = "Adhoc Tax";
				content = content.replaceAll("<mod_name>", moduleName);
			}
			if (content.contains("<owner_name>"))
				content = content.replaceAll("<owner_name>", bill.getPayerName());
			else
				content = content.replaceAll("<tax_name>", bill.getBusinessService());
			if (content.contains("<amount_paid>"))
				content = content.replaceAll("<amount_paid>", paymentDetail.getTotalAmountPaid().toString());
			Collections.sort(bill.getBillDetails(), (b1, b2) -> b2.getFromPeriod().compareTo(b1.getFromPeriod()));
			String formattedDate = LocalDateTime
					.ofInstant(Instant.ofEpochMilli(bill.getBillDetails().get(0).getFromPeriod()),
							ZoneId.systemDefault())
					.format(DateTimeFormatter.ofPattern("yyyy"));
			String formattedtoDate = LocalDateTime
					.ofInstant(Instant.ofEpochMilli(bill.getBillDetails().get(0).getToPeriod()), ZoneId.systemDefault())
					.format(DateTimeFormatter.ofPattern("yy"));
			if (content.contains("<fin_year>"))

				content = content.replaceAll("<fin_year>", formattedDate + "-" + formattedtoDate);
			content = content.replaceAll("<rcpt_no>", paymentDetail.getReceiptNumber());

			content = content.replaceAll("<unique_id>", bill.getConsumerCode());
			message = content;
		}
		log.info("Final msg: "+message);
		return message;
	}

	private String fetchContentFromLocalization(RequestInfo requestInfo, String tenantId, String module, String code) {
		String message = null;
		List<String> codes = new ArrayList<>();
		List<String> messages = new ArrayList<>();
		Object result = null;
		String locale = "";
		if (requestInfo.getMsgId().contains("|"))
			locale = requestInfo.getMsgId().split("[\\|]")[1];
		if (StringUtils.isEmpty(locale))
			locale = applicationProperties.getFallBackLocale();
		StringBuilder uri = new StringBuilder();
		uri.append(applicationProperties.getLocalizationHost()).append(applicationProperties.getLocalizationEndpoint());
		uri.append("?tenantId=").append(tenantId.split("\\.")[0]).append("&locale=").append(locale).append("&module=")
				.append(module);

		Map<String, Object> request = new HashMap<>();
		request.put("RequestInfo", requestInfo);
		try {
			result = restTemplate.postForObject(uri.toString(), request, Map.class);
			codes = JsonPath.read(result, LOCALIZATION_CODES_JSONPATH);
			messages = JsonPath.read(result, LOCALIZATION_MSGS_JSONPATH);
		} catch (Exception e) {
			log.error("Exception while fetching from localization: " + e);
		}
		if (CollectionUtils.isEmpty(messages)) {
			throw new CustomException("LOCALIZATION_NOT_FOUND", "Localization not found for the code: " + code);
		}
		for (int index = 0; index < codes.size(); index++) {
			if (codes.get(index).equals(code)) {
				message = messages.get(index);
			}
		}
		return message;
	}

	private String formatCodes(String code) {
		String regexForSpecialCharacters = "[-+$&,:;=?@#|'<>.^*()%!]";
		code = code.replaceAll(regexForSpecialCharacters, "");
		code = code.replaceAll(" ", "_");

		return BUSINESSSERVICELOCALIZATION_CODE_PREFIX + code.toUpperCase();
	}

	/**
	 * Method to shortent the url returns the same url if shortening fails
	 * 
	 * @param url
	 */
	public String getShortenedUrl(String url) {

		HashMap<String, String> body = new HashMap<>();
		body.put("url", url);
		StringBuilder builder = new StringBuilder(applicationProperties.getUrlShortnerHost());
		builder.append(applicationProperties.getUrlShortnerEndpoint());
		String res = restTemplate.postForObject(builder.toString(), body, String.class);

		if (StringUtils.isEmpty(res)) {
			log.error("URL_SHORTENING_ERROR", "Unable to shorten url: " + url);
			;
			return url;
		} else
			return res;
	}
	
	
	
}
