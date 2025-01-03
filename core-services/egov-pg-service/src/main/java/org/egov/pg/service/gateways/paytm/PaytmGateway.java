package org.egov.pg.service.gateways.paytm;

import com.paytm.pg.merchant.CheckSumServiceHelper;

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
import org.egov.common.contract.request.RequestInfo;
import org.egov.pg.models.Transaction;
import org.egov.pg.web.models.CheckSumTransaction;
import org.egov.pg.service.Gateway;
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
import java.util.TreeMap;

@Service
@Slf4j
public class PaytmGateway implements Gateway {

    private static final String GATEWAY_NAME = "PAYTM";
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
    public PaytmGateway(RestTemplate restTemplate, Environment environment) {
        this.restTemplate = restTemplate;

        ACTIVE = Boolean.valueOf(environment.getRequiredProperty("paytm.active"));
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
        TreeMap<String, String> paramMap = new TreeMap<>();
        paramMap.put("MID", MID);
        paramMap.put("ORDER_ID", transaction.getTxnId());
        paramMap.put("CUST_ID", transaction.getUser().getUserName());
        paramMap.put("INDUSTRY_TYPE_ID", INDUSTRY_TYPE_ID);
        paramMap.put("CHANNEL_ID", CHANNEL_ID);
        paramMap.put("TXN_AMOUNT", Utils.formatAmtAsRupee(transaction.getTxnAmount()));
        paramMap.put("WEBSITE", WEBSITE);
        paramMap.put("EMAIL", transaction.getUser().getEmailId());
        paramMap.put("MOBILE_NO", transaction.getUser().getMobileNumber());
        paramMap.put("CALLBACK_URL", transaction.getCallbackUrl());

        try {

            String checkSum = CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(MERCHANT_KEY, paramMap);
            paramMap.put("CHECKSUMHASH", checkSum);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            paramMap.forEach((key, value) -> params.put(key, Collections.singletonList(value)));


            UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(MERCHANT_URL_DEBIT).queryParams
                    (params).build().encode();

            return uriComponents.toUri();
        } catch (Exception e) {
            log.error("Paytm Checksum generation failed", e);
            throw new CustomException("CHECKSUM_GEN_FAILED", "Hash generation failed, gateway redirect URI cannot be generated");
        }
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
    
    @Override
    public String generateChecksum(CheckSumTransaction transaction,RequestInfo requestInfo ) {
//    	 TreeMap<String, String> paramMap = new TreeMap<>();
//         paramMap.put("MID", MID);
//         paramMap.put("ORDER_ID", transaction.getTxnId());
//         paramMap.put("CUST_ID", requestInfo.getUserInfo().getUserName());
//         paramMap.put("INDUSTRY_TYPE_ID", INDUSTRY_TYPE_ID);
//         paramMap.put("CHANNEL_ID", CHANNEL_ID);
//         paramMap.put("TXN_AMOUNT", Utils.formatAmtAsRupee(transaction.getTxnAmount()));
//         paramMap.put("WEBSITE", WEBSITE);
//         paramMap.put("EMAIL", requestInfo.getUserInfo().getEmailId());
//         paramMap.put("MOBILE_NO", requestInfo.getUserInfo().getMobileNumber());
//         paramMap.put("CALLBACK_URL", transaction.getCallbackUrl());
    	   LocalDateTime now = LocalDateTime.now();
           
           // Define the desired format
           DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
           
           // Format the current date and time
           String formattedDateTime = now.format(formatter);
           System.out.println(formattedDateTime+"jgjhkj");
           TreeMap<String, String> Map = new TreeMap<>();
			
   		
   		Map.put("paytmMid", MID);//put  mid  here Testin39635949826983//Gaurav09015494045385//s-Prachi65794223240821  , p-Prachi57804451957605
   		Map.put("paytmTid", TID);//put  tid  here 70000398//10714205  //10955450
   		Map.put("transactionDateTime", formattedDateTime);// ("2022-03-15 9:42:00")); //
   		Map.put("merchantTransactionId", "8778943322132908"+ new Random().nextInt(10000));
   		Map.put("transactionAmount", "100");
//    	 paramMap.put("paytmMid", MID);
//    	 paramMap.put("paytmTid", TID);
//    	 paramMap.put("transactionDateTime", formattedDateTime);
//    	 paramMap.put("merchantTransactionId", transaction.getMerchantTransactionId());
//    	 if(transaction.getMerchantReferenceNo() != null) {
//    		 paramMap.put("merchantReferenceNo", transaction.getMerchantReferenceNo());
//    	 }
//    	 
//    	 paramMap.put("transactionAmount", transaction.getTransactionAmount());
//    	 if(transaction.getMerchantExtendedInfo() != null) {
//        	 paramMap.put("merchantExtendedInfo", transaction.getMerchantExtendedInfo().toString()); 
//    	 }

         try {
        	 for (Map.Entry<String, String> entry : Map.entrySet()) {
        		    if (entry.getValue() == null || entry.getValue().isEmpty()) {
        		        throw new IllegalArgumentException("Missing or empty parameter: " + entry.getKey());
        		    }
        		}
        	 
     		String checksum = CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(MERCHANT_KEY,Map); // oon!1%!ftW!#HnB4//URclyb7U2QNj2L3G  , s- XtR3WxzZkWmlVlt3	//Merchant key
    		JSONObject Obj = new JSONObject(Map);
    		String post_data = Obj.toString();
    		//("2022-01-12 16:27:00")//2022-03-15 9:27:00//
    		System.out.println("Request:\n" + post_data);
    						
    		  String body = "{\"head\":{\"version\":\"3.1\",\"requestTimeStamp\":\"" +
    				  (formattedDateTime)+ "\"," + "\"channelId\":\"EDC\",\"checksum\":\"" + checksum
    		  + "\"},\"body\":" + post_data + "}";
    		
    		
    		System.out.println("Request:\n" + body);
    		
    		URL url = new URL("https://securegw-stage.paytm.in/ecr/payment/request");
    		
    	
//    		URL url = new URL("https://securegw-edc.paytm.in/ecr/payment/request");
    		HttpURLConnection connection = null;
    		connection = (HttpURLConnection) url.openConnection();
    		connection.setRequestMethod("POST");
    		connection.setRequestProperty("Content-Type", "application/json");
    		connection.setUseCaches(false);
    		connection.setDoOutput(true);
    		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
    		wr.writeBytes(body);
    		wr.close();
    		InputStream is = connection.getInputStream();
    		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
    		StringBuilder response = new StringBuilder();
    		String line = "";
    		while ((line = rd.readLine()) != null) {
    			response.append(line);
    			response.append('\r');				
    	}
    		rd.close();
    		System.out.println("URL:\n" + url);
    		System.out.println("Request:\n" + body);
    		System.out.println("Response:\n" + response);
        	 
        	 
        	 
//        	  String checkSum = CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(MERCHANT_KEY, paramMap);
//              paramMap.put("CHECKSUMHASH", checkSum);
              return checksum;
         }
         catch (Exception e) {
             log.error("Paytm Checksum generation failed", e);
             throw new CustomException("CHECKSUM_GEN_FAILED", "Hash generation failed, gateway redirect URI cannot be generated");
         }
    }
}