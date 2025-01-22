package org.egov.pg.service.gateways.paytmpos;

import com.paytm.pg.merchant.*;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.commons.lang3.StringUtils;
import org.egov.pg.models.Transaction;
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
import org.springframework.web.client.RestTemplate;


import java.net.URI;
import java.util.Map;
import java.util.HashMap;
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
    public PaytmPosGateway(RestTemplate restTemplate, Environment environment) {
        this.restTemplate = restTemplate;

        ACTIVE = Boolean.valueOf(environment.getRequiredProperty("paytmpos.active"));
        MID = environment.getRequiredProperty("paytmpos.merchant.id");
        MERCHANT_KEY = environment.getRequiredProperty("paytmpos.merchant.secret.key");
        INDUSTRY_TYPE_ID = environment.getRequiredProperty("paytmpos.merchant.industry.type");
        CHANNEL_ID = environment.getRequiredProperty("paytmpos.merchant.channel.id");
        WEBSITE = environment.getRequiredProperty("paytmpos.merchant.website");
        MERCHANT_URL_DEBIT = environment.getRequiredProperty("paytmpos.url.debit");
        MERCHANT_URL_STATUS = environment.getRequiredProperty("paytmpos.url.status");
        TID = environment.getRequiredProperty("paytmpos.merchant.secret.tid");
    }

    @Override
    public URI generateRedirectURI(Transaction transaction) {
		// Create Order
    	String ORDERID = transaction.getTxnId().replaceAll("_", "");
    	transaction.setGatewayTxnId(ORDERID);
    	transaction.setOrderId(ORDERID);
		createTransaction(Utils.formatAmtAsPaise(transaction.getTxnAmount()), transaction.getTxnId());
		return URI.create(StringUtils.EMPTY); // Return an empty URI
    }

    @Override
    public Transaction fetchStatus(Transaction currentStatus, Map<String, String> params) {
       
		Transaction transaction =  getPaytmPaymentStatus(currentStatus);

		if (null != transaction) {
			return transaction;
		}   
		return Transaction.builder().txnId(currentStatus.getTxnId()).txnAmount(currentStatus.getTxnAmount())
				.txnStatus(Transaction.TxnStatusEnum.PENDING).build();
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

    private Transaction transformRawResponse(PaymentStatusResponse resp, Transaction currentStatus) {

        Transaction.TxnStatusEnum status = Transaction.TxnStatusEnum.PENDING;

        if (resp.getBody().getResultInfo().getResultStatus().equalsIgnoreCase("SUCCESS"))
            status = Transaction.TxnStatusEnum.SUCCESS;
        else if (resp.getBody().getResultInfo().getResultStatus().equalsIgnoreCase("FAIL"))
            status = Transaction.TxnStatusEnum.FAILURE;
        else if (resp.getBody().getResultInfo().getResultStatus().equalsIgnoreCase("PENDING"))
            status = Transaction.TxnStatusEnum.PENDING;

        return Transaction.builder()
                .txnId(currentStatus.getTxnId())
                .txnAmount(Utils.formatAmtAsRupee(currentStatus.getTxnAmount()))
                .txnStatus(status)
                .gatewayTxnId(currentStatus.getGatewayTxnId())
                .gatewayPaymentMode("PAYTM-POS")
                .gatewayStatusCode(resp.getBody().getResultInfo().getResultStatus())
                .gatewayStatusMsg(resp.getBody().getResultInfo().getResultMsg())
                .responseJson(resp)
                .build();
    }

    @Override
    public String generateRedirectFormData(Transaction transaction) {
        // TODO Auto-generated method stub
        return null;
    }
    private Transaction getPaytmPaymentStatus(Transaction transaction ) {
    		LocalDateTime now = LocalDateTime.now();
           
           // Define the desired format
           DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
           
           // Format the current date and time
           String formattedDateTime = now.format(formatter);
           TreeMap<String, String> Body = new TreeMap<>();
           Body.put("paytmMid", MID);
           Body.put("paytmTid", TID);
           Body.put("transactionDateTime", formattedDateTime);// ("2022-03-15 9:42:00")); //
           Body.put("merchantTransactionId", transaction.getGatewayTxnId());

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
            ResponseEntity<PaymentStatusResponse> response = restTemplate.postForEntity(MERCHANT_URL_STATUS, 
            		requestEntity,
            		PaymentStatusResponse.class);
            return transformRawResponse(response.getBody(),transaction);
         }
         catch (Exception e) {
             log.error("Paytm Status Check failed", e);
             throw new CustomException("PAYTMCHECKSTATUSFAIL", "failure status could not be fetched");
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
           Body.put("merchantTransactionId",transactionId.replaceAll("_", ""));
           Body.put("merchantReferenceNo", transactionId);
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
            ResponseEntity<PaymentStatusResponse> responseEntity =restTemplate.postForEntity(MERCHANT_URL_DEBIT, 
            		requestEntity,
            		PaymentStatusResponse.class);
//            System.out.println(responseEntity.getBody().getBody().getResultInfo().getResultStatus());
//              return Body.get("merchantTransactionId");
         }
         catch (Exception e) {
             log.error("Paytm Transaction generation failed", e);
             throw new CustomException("PAYTMTRANSACTIONFAIL", "transaction could not be generated");
         }
    }
}