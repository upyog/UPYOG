package org.egov.pg.service.gateways.paytmpos;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.paytm.pg.merchant.*;

import java.io.InputStreamReader;
import lombok.extern.slf4j.Slf4j;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.util.Random;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;



import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.pg.models.Transaction;
import org.egov.pg.web.models.CheckSumTransaction;
import org.egov.pg.service.Gateway;
import org.egov.pg.service.gateways.paytm.PaytmResponse;
import org.egov.pg.service.gateways.razorpay.models.Order;
import org.egov.pg.utils.Utils;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@Service
@Slf4j
public class PaytmPosGateway implements Gateway {

    private static final String GATEWAY_NAME = "PAYTMPOS";
    private final String MID;
    private final String MERCHANT_KEY;
    private final String MERCHANT_URL_DEBIT;
    private final String MERCHANT_URL_STATUS;
    private final String INDUSTRY_TYPE_ID;
    private final String CHANNEL_ID;
    private final String WEBSITE;
    private final String TID;

    private final boolean ACTIVE;

    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    public PaytmPosGateway(RestTemplate restTemplate, Environment environment) {
        this.restTemplate = restTemplate;

        ACTIVE = true;
//        		Boolean.valueOf(environment.getRequiredProperty("paytmpos.active"));
        MID = environment.getRequiredProperty("paytm.merchant.id");
        MERCHANT_KEY = environment.getRequiredProperty("paytm.merchant.secret.key");
        INDUSTRY_TYPE_ID = environment.getRequiredProperty("paytm.merchant.industry.type");
        CHANNEL_ID = environment.getRequiredProperty("paytm.merchant.channel.id");
        WEBSITE = environment.getRequiredProperty("paytm.merchant.website");
        MERCHANT_URL_DEBIT = environment.getRequiredProperty("paytm.url.debit");
        MERCHANT_URL_STATUS = environment.getRequiredProperty("paytm.url.status");
        TID = environment.getRequiredProperty("paytm.merchant.secret.tid");
    }

    @Override
    public URI generateRedirectURI(Transaction transaction) {
		// Create Order
		createTransaction(Utils.formatAmtAsPaise(transaction.getTxnAmount()), transaction.getTxnId());
//		@SuppressWarnings("unchecked") // Suppress the unchecked cast warning
//		LinkedHashMap<Object, Object> additionalDetails = Optional.ofNullable(transaction.getAdditionalDetails())
//		        .map(obj -> (LinkedHashMap<Object, Object>) objectMapper.convertValue(obj, LinkedHashMap.class))
//		        .orElseGet(LinkedHashMap::new);

//
//		transaction.setOrderId(order.getOrderId());
//		additionalDetails.put(ORDER_ID, order.getOrderId());
//		transaction.setAdditionalDetails(additionalDetails);
		return URI.create(StringUtils.EMPTY); // Return an empty URI
    }

    @Override
    public Transaction fetchStatus(Transaction currentStatus, Map<String, String> params) {
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        treeMap.put("MID", MID);
        treeMap.put("ORDER_ID", currentStatus.getTxnId());

        try {
        	
            String checkSum = CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(MERCHANT_KEY, treeMap);
            treeMap.put("CHECKSUMHASH", checkSum);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());

            HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(treeMap, httpHeaders);

            ResponseEntity<PaytmResponse> response = restTemplate.postForEntity(MERCHANT_URL_STATUS, httpEntity,
                    PaytmResponse.class);

            return transformRawResponse(response.getBody(), currentStatus);

        } catch (RestClientException e) {
            log.error("Unable to fetch status from Paytm gateway", e);
            throw new CustomException("UNABLE_TO_FETCH_STATUS", "Unable to fetch status from Paytm gateway");
        } catch (Exception e) {
            log.error("Paytm Checksum generation failed", e);
            throw new CustomException("CHECKSUM_GEN_FAILED", "Hash generation failed, gateway redirect URI cannot be generated");
        }
    }


    @Override
    public boolean isActive() {
        return ACTIVE;
    }

    @Override
    public String gatewayName() {
        return GATEWAY_NAME;
    }

    @Override
    public String transactionIdKeyInResponse() {
        return "ORDERID";
    }

    private Transaction transformRawResponse(PaytmResponse resp, Transaction currentStatus) {

        Transaction.TxnStatusEnum status = Transaction.TxnStatusEnum.PENDING;

        if (resp.getStatus().equalsIgnoreCase("TXN_SUCCESS"))
            status = Transaction.TxnStatusEnum.SUCCESS;
        else if (resp.getStatus().equalsIgnoreCase("TXN_FAILURE"))
            status = Transaction.TxnStatusEnum.FAILURE;

        return Transaction.builder()
                .txnId(currentStatus.getTxnId())
                .txnAmount(Utils.formatAmtAsRupee(resp.getTxnAmount()))
                .txnStatus(status)
                .gatewayTxnId(resp.getTxnId())
                .gatewayPaymentMode(resp.getPaymentMode())
                .gatewayStatusCode(resp.getRespCode())
                .gatewayStatusMsg(resp.getRespMsg())
                .responseJson(resp)
                .build();
    }

    @Override
    public String generateRedirectFormData(Transaction transaction) {
        // TODO Auto-generated method stub
        return null;
    }
    private PaymentStatusResponse getPaytmPaymentStatus(CheckSumTransaction transaction,RequestInfo requestInfo ) {
    		LocalDateTime now = LocalDateTime.now();
           
           // Define the desired format
           DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
           
           // Format the current date and time
           String formattedDateTime = now.format(formatter);
           TreeMap<String, String> Body = new TreeMap<>();
           Body.put("paytmMid", MID);//put  mid  here Testin39635949826983//Gaurav09015494045385//s-Prachi65794223240821  , p-Prachi57804451957605
           Body.put("paytmTid", TID);//put  tid  here 70000398//10714205  //10955450
           Body.put("transactionDateTime", formattedDateTime);// ("2022-03-15 9:42:00")); //
           Body.put("merchantTransactionId", transaction.getMerchantTransactionId());

         try {
        	 for (Map.Entry<String, String> entry : Body.entrySet()) {
        		    if (entry.getValue() == null || entry.getValue().isEmpty()) {
        		        throw new IllegalArgumentException("Missing or empty parameter: " + entry.getKey());
        		    }
        	 }
     		String checksum = CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(MERCHANT_KEY,Body);
      		HashMap<String, String> Head = new HashMap<>();
	        Head.put("version","3.1" );
	        Head.put("requestTimeStamp", formattedDateTime);
	        Head.put("channelId", "EDC");
	        Head.put("checksum", checksum);
    		HashMap<String, Object> requestPayload = new HashMap<>();
            requestPayload.put("head",Head);
            requestPayload.put("body", Body);
    		JSONObject Obj1 = new JSONObject(requestPayload);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(Obj1.toString(), headers);
            ResponseEntity<PaymentStatusResponse> response = restTemplate.postForEntity("https://securegw-stage.paytm.in/ecr/V2/payment/status", 
            		requestEntity,
            		PaymentStatusResponse.class);
            return response.getBody();
         }
         catch (Exception e) {
             log.error("Paytm Checksum generation failed", e);
             throw new CustomException("CHECKSUM_GEN_FAILED", "Hash generation failed, gateway redirect URI cannot be generated");
         }
    
    }
    
    private void createTransaction(String amount, String transactionId  ) {

    	   LocalDateTime now = LocalDateTime.now();
           
           // Define the desired format
           DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
           
           // Format the current date and time
           String formattedDateTime = now.format(formatter);
           TreeMap<String, String> Body = new TreeMap<>();
   		
           Body.put("paytmMid", MID);//put  mid  here Testin39635949826983//Gaurav09015494045385//s-Prachi65794223240821  , p-Prachi57804451957605
           Body.put("paytmTid", TID);//put  tid  here 70000398//10714205  //10955450
           Body.put("transactionDateTime", formattedDateTime);// ("2022-03-15 9:42:00")); //
           Body.put("merchantTransactionId", transactionId);
           Body.put("transactionAmount", amount);
           
           HashMap<String, String> Head = new HashMap<>();
	        Head.put("version","3.1" );
	        Head.put("requestTimeStamp", formattedDateTime);
	        Head.put("channelId", "EDC");

         try {
        	 for (Map.Entry<String, String> entry : Body.entrySet()) {
        		    if (entry.getValue() == null || entry.getValue().isEmpty()) {
        		        throw new IllegalArgumentException("Missing or empty parameter: " + entry.getKey());
        		    }
        		}
        	 
     		String checksum = CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(MERCHANT_KEY,Body);
	        Head.put("checksum", checksum);
	        
	        HashMap<String, Object> requestPayload = new HashMap<>();
            requestPayload.put("head",Head);
            requestPayload.put("body", Body);
    		JSONObject Obj1 = new JSONObject(requestPayload);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(Obj1.toString(), headers);
            ResponseEntity<PaymentStatusResponse> responseEntity =restTemplate.postForEntity("https://securegw-stage.paytm.in/ecr/payment/request", 
            		requestEntity,
            		PaymentStatusResponse.class);
            System.out.println(responseEntity.getBody().getBody().getResultInfo().getResultStatus());
//              return Body.get("merchantTransactionId");
         }
         catch (Exception e) {
             log.error("Paytm Checksum generation failed", e);
             throw new CustomException("CHECKSUM_GEN_FAILED", "Hash generation failed, gateway redirect URI cannot be generated");
         }
    }
}