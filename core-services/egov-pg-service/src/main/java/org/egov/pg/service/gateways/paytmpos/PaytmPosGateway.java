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
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.MdmsResponse;
import org.egov.mdms.model.ModuleDetail;
import org.egov.pg.repository.ServiceCallRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;

@Service
@Slf4j
public class PaytmPosGateway implements Gateway {

    private static final String GATEWAY_NAME = "PAYTMPOS";
    private  String MID;
    private  String MERCHANT_KEY;
    private final String MERCHANT_URL_DEBIT;
    private final String MERCHANT_URL_STATUS;
    private final String INDUSTRY_TYPE_ID;
    private final String CHANNEL_ID;
    private final String WEBSITE;
    private  String TID;

    private final boolean ACTIVE;

    private RestTemplate restTemplate;
    
    @Autowired
    private ServiceCallRepository serviceCallRepository;
    
	@Autowired
	private ObjectMapper objectMapper;
    

    
    
    @Autowired
    public PaytmPosGateway(RestTemplate restTemplate, Environment environment) {
        this.restTemplate = restTemplate;

        ACTIVE = Boolean.valueOf(environment.getRequiredProperty("paytmpos.active"));
//        MID = environment.getRequiredProperty("paytmpos.merchant.id");
//        MERCHANT_KEY = environment.getRequiredProperty("paytmpos.merchant.secret.key");
        INDUSTRY_TYPE_ID = environment.getRequiredProperty("paytmpos.merchant.industry.type");
        CHANNEL_ID = environment.getRequiredProperty("paytmpos.merchant.channel.id");
        WEBSITE = environment.getRequiredProperty("paytmpos.merchant.website");
        MERCHANT_URL_DEBIT = environment.getRequiredProperty("paytmpos.url.debit");
        MERCHANT_URL_STATUS = environment.getRequiredProperty("paytmpos.url.status");
//        TID = environment.getRequiredProperty("paytmpos.merchant.secret.tid");
    }

    @Override
    public URI generateRedirectURI(Transaction transaction) {
		// Create Order
    	String ORDERID = transaction.getTxnId().replaceAll("_", "");
		MdmsCriteriaReq mdmsCriteriaReq = createCriteria(transaction.getTenantId());
    	MdmsResponse mdmsResponse = serviceCallRepository.getMdmsMasterData(mdmsCriteriaReq);
    	ObjectNode object = refineMdmsData(mdmsResponse);
    	if(object !=null) {
        	if(setCreds(object)) {
        		transaction.setGatewayTxnId(ORDERID);
            	transaction.setOrderId(ORDERID);
        		createTransaction(Utils.formatAmtAsPaise(transaction.getTxnAmount()), transaction.getTxnId());
        		return URI.create(StringUtils.EMPTY); // Return an empty URI
        	}else {
                throw new CustomException("PAYTMTRANSACTIONFAIL", "mdms request no creds found");
        	}
    	}else {
            throw new CustomException("PAYTMTRANSACTIONFAIL", "mdms request no creds found");
    	}
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
    	
		MdmsCriteriaReq mdmsCriteriaReq = createCriteria(transaction.getTenantId());
    	MdmsResponse mdmsResponse = serviceCallRepository.getMdmsMasterData(mdmsCriteriaReq);
    	ObjectNode object = refineMdmsData(mdmsResponse);
    	if(object !=null) {
        	if(setCreds(object)) {
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
        	else {
                throw new CustomException("PAYTMTRANSACTIONFAIL", "mdms request no creds found");
        	}
    	}else {
            throw new CustomException("PAYTMTRANSACTIONFAIL", "mdms request no creds found");
    	}	
    }
    
    private void createTransaction(String amount, String transactionId  ) {


    	   LocalDateTime now = LocalDateTime.now();
           
           // Define the desired format
           DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
           
           // Format the current date and time
           String formattedDateTime = now.format(formatter);
           TreeMap<String, String> Body = new TreeMap<>();
   		    log.info(MID.toString()+"mid"+TID+"tid"+MERCHANT_KEY+"key");

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
            log.info("Paytm Transaction generation message "+responseEntity.getBody().getBody().getResultInfo().getResultMsg());
            System.out.println(responseEntity.getBody().getBody().getResultInfo().getResultStatus());
//              return Body.get("merchantTransactionId");
         }
         catch (Exception e) {
             log.error("Paytm Transaction generation failed", e);
             throw new CustomException("PAYTMTRANSACTIONFAIL", "transaction could not be generated");
         }
    }
    
    private ObjectNode refineMdmsData(MdmsResponse mdmsResponse) {
        JsonNode mdmsResNode = objectMapper.valueToTree(mdmsResponse.getMdmsRes());
        if (mdmsResNode.has("PaytmPos")) {
            JsonNode PaytmPos = mdmsResNode.get("PaytmPos");

            // Check if PaytmPos contains "PaytmPos"
            if (PaytmPos.has("PaytmPos")) {
                JsonNode PaytmPosLvl2 = PaytmPos.get("PaytmPos");

                // Ensure PaytmPosLvl2 is an array
                if (PaytmPosLvl2.isArray() && PaytmPosLvl2.size() > 0) {
                    // Extract first element
                    JsonNode Data = PaytmPosLvl2.get(0);

                    // Check if Data is an ObjectNode
                    if (Data.isObject()) {
                        ObjectNode object = (ObjectNode) Data;
                        return object;
//                        String tId = object.get("tId").asText();  // Get value as text

                        // Successfully extracted ObjectNode
//                        System.out.println("Extracted ObjectNode: " + object.toPrettyString());
                    } else {
                        System.out.println("Error: Data is not an ObjectNode");
                    }
                } else {
                    System.out.println("Error: PaytmPosLvl2 is not an array or is empty");
                }
            } else {
                System.out.println("Error: PaytmPos does not contain 'PaytmPos'");
            }
        } else {
            System.out.println("Error: mdmsResNode does not contain 'PaytmPos'");
        }
        return null;
    }
    
    private boolean setCreds(ObjectNode obj) {
    	if(obj.has("mercIhantId") &&obj.has("mercIhantKey")&&obj.has("tId")) {
    		MID = obj.get("mercIhantId").asText();
            MERCHANT_KEY = obj.get("mercIhantKey").asText();
            TID = obj.get("tId").asText();
            return true;
    	}
    	   return false;
    }

    private MdmsCriteriaReq createCriteria(String tenantId) {
		List<ModuleDetail> moduleDetails = new ArrayList<>();
		ModuleDetail moduleDetail = new ModuleDetail();
		moduleDetail.setModuleName("PaytmPos");
		String filter = "[?(@.ulbName == \"" +tenantId+ "\")]";			 
		List<MasterDetail> masterDetails = new ArrayList<>();
		MasterDetail masterDetail = MasterDetail.builder().name("PaytmPos").filter(filter).build();
		masterDetails.add(masterDetail);
		moduleDetail.setMasterDetails(masterDetails);
		moduleDetails.add(moduleDetail);
		MdmsCriteria mdmsCriteria = MdmsCriteria.builder().tenantId(tenantId)
		.moduleDetails(moduleDetails).build();
//		RequestInfo requestInfo = new RequestInfo().builder().build();
		MdmsCriteriaReq mdmsCriteriaReq = MdmsCriteriaReq.builder().mdmsCriteria(mdmsCriteria)
		.build();
		return mdmsCriteriaReq;
    }
}
