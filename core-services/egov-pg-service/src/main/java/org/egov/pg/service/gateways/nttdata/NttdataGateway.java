package org.egov.pg.service.gateways.nttdata;

import java.net.URI;
import java.util.Map;

import org.egov.pg.models.Transaction;
import org.egov.pg.service.Gateway;
import org.egov.pg.utils.Utils;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NttdataGateway implements Gateway {

    private static final String GATEWAY_NAME = "NTTDATA";
    private final String MERCHANT_HOST;
    private final String MERCHANT_PATH_DEBIT;
    private final String MERCHANT_PATH_STATUS;
    private final String MERCHANT_ID;
    private final String SALT;
    private final String SALT_INDEX;
    private final boolean ACTIVE;
    private ObjectMapper objectMapper;

    private RestTemplate restTemplate;

    @Autowired
    public NttdataGateway(RestTemplate restTemplate, ObjectMapper objectMapper, Environment environment) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;

        ACTIVE = Boolean.TRUE;
        MERCHANT_ID = "317156";
        SALT=null;
        SALT_INDEX=null;
        //SALT = environment.getRequiredProperty("phonepe.merchant.secret.key");
        //SALT_INDEX = environment.getRequiredProperty("phonepe.merchant.secret.index");
        MERCHANT_HOST = "https://pgtest.atomtech.in/staticdata/ots/js/atomcheckout.js";
        MERCHANT_PATH_DEBIT=null;
        //MERCHANT_PATH_DEBIT = environment.getRequiredProperty("phonepe.url.debit");
        MERCHANT_PATH_STATUS = "https://caller.atomtech.in/ots/payment/status?";
    }

    public ResponseParser decryptData(String encData)
    {
		String decData=AuthEncryption.getAuthDecrypted(encData, "75AEF0FA1B94B3C10D4F5B268F757F11");

		try {
			ResponseParser resp = objectMapper.readValue(decData, ResponseParser.class);
			return resp;
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

    }

    @Override
    public URI generateRedirectURI(Transaction transaction) {
    	String encryptedData = "";
		String decryptedData = "";

		System.out.println("############################ NTT DATA AUTH Controller Post #########################");

		String merchantId = "317156";

		String merchantTxnId = transaction.getTxnId();


		System.out.println("MerchantId------: " + merchantId);
		System.out.println("Amount----------: " + transaction.getTxnAmount());
		System.out.println("MerchantTxnId---: " + merchantTxnId);
		//System.out.println("ReturnURL-------: " + returnURL);

		MerchDetails merchDetails = new MerchDetails();
		merchDetails.setMerchId(merchantId);
		merchDetails.setMerchTxnId(merchantTxnId);
		merchDetails.setUserId("");
		merchDetails.setPassword("Test@123");
		merchDetails.setMerchTxnDate(transaction.getTxnDate());

		PayDetails payDetails = new PayDetails();
		payDetails.setAmount(transaction.getTxnAmount());
		payDetails.setCustAccNo(transaction.getConsumerCode());
		payDetails.setTxnCurrency("INR");
		payDetails.setProduct("NSE");
		
		CustDetails custDetails = new CustDetails();
		custDetails.setCustEmail(transaction.getUser().getEmailId());
		custDetails.setCustMobile(transaction.getUser().getMobileNumber());

		HeadDetails headDetails = new HeadDetails();
		headDetails.setApi("AUTH");
		headDetails.setVersion("OTSv1.1");
		headDetails.setPlatform("FLASH");

		Extras extras = new Extras();
		extras.setUdf1("");
		extras.setUdf2("");
		extras.setUdf3("");
		extras.setUdf4("");
		extras.setUdf5("");

		PayInstrument payInstrument = new PayInstrument();

		payInstrument.setMerchDetails(merchDetails);
		payInstrument.setPayDetails(payDetails);
		payInstrument.setCustDetails(custDetails);
		payInstrument.setHeadDetails(headDetails);
		payInstrument.setExtras(extras);

		OtsTransaction otsTxn = new OtsTransaction();
		otsTxn.setPayInstrument(payInstrument);

		Gson gson = new Gson();
		String json = gson.toJson(otsTxn);
		System.out.println("Final JsonOutput----: " + json);

		String serverResp = "";
		String decryptResponse = "";
		String atomTokenId="";
		try {
			//  58BE879B7DD635698764745511C704AB
			
			//  A4476C2062FFA58980DC8F79EB6A799E
			encryptedData = AuthEncryption.getAuthEncrypted(json, "A4476C2062FFA58980DC8F79EB6A799E");
			System.out.println("EncryptedData------: " + encryptedData);
			try {
			serverResp = AipayService.getAtomTokenId(merchantId, encryptedData);
			//serverResp="merchId";
			System.out.println("serverResp Result------: " + serverResp);
			System.out.println("serverResp Length------: " + serverResp.length());
			System.out.println("serverResp Condition---: " + serverResp.startsWith("merchId"));

			if ((serverResp != null) && (serverResp.startsWith("merchId"))) {
				decryptResponse = serverResp.split("\\&encData=")[1];
				System.out.println("serrResp---: " + decryptResponse);

				decryptedData = AuthEncryption.getAuthDecrypted(decryptResponse, "75AEF0FA1B94B3C10D4F5B268F757F11");
				System.out.println("DecryptedData------: " + decryptedData);

				ServerResponse serverResponse = new ServerResponse();
				serverResponse = (ServerResponse) gson.fromJson(decryptedData, ServerResponse.class);

				atomTokenId = serverResponse.getAtomTokenId();
				//atomTokenId="GGGGGG";
				System.out.println("serverResponse-----: " + serverResponse);
				System.out.println("TokenId------------: " + atomTokenId);
				
			} else {
				String errorDescription="Something Went Wrong!";
				System.out.println("Something Went Wrong!");
	            throw new CustomException(null, "Auth API response is not Valid!! ");

				
			}
		}
		catch (Exception e) {
            log.error("Some Error Occured in token API Call", e);
            throw new CustomException(null, "Some Error Occured in token API Call");
		}
			HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("atomTokenId", atomTokenId);
            params.add("merchId", merchantId);
            params.add("custEmail", transaction.getUser().getEmailId());
            params.add("custMobile", transaction.getUser().getMobileNumber());
            UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(MERCHANT_HOST).queryParams(params)
                    .build();

            return uriComponents.toUri();
        } catch (Exception e) {
            log.error("NTT Data fetch Token failed", e);
            throw new CustomException("CHECKSUM_GEN_FAILED", " gateway redirect URI cannot be generated");
        }

    }

    @Override
    public Transaction fetchStatus(Transaction currentStatus, Map<String, String> params) {
        
    	String merchantId = "317156";
		String merchantTxnId = currentStatus.getTxnId();
		System.out.println("MerchantId------: " + merchantId);
		System.out.println("Amount----------: " + currentStatus.getTxnAmount());
		System.out.println("MerchantTxnId---: " + merchantTxnId);

		MerchDetails merchDetails = new MerchDetails();
		merchDetails.setMerchId(merchantId);
		merchDetails.setMerchTxnId(merchantTxnId);
		merchDetails.setPassword("Test@123");
		merchDetails.setMerchTxnDate(currentStatus.getTxnDate());

		PayDetails payDetails = new PayDetails();
		payDetails.setAmount(currentStatus.getTxnAmount());
		payDetails.setTxnCurrency("INR");
		payDetails.setSignature("NSE");
	
		HeadDetails headDetails = new HeadDetails();
		headDetails.setApi("TXNVERIFICATION");
		headDetails.setSource("OTS");
		
		PayInstrument payInstrument = new PayInstrument();

		payInstrument.setMerchDetails(merchDetails);
		payInstrument.setPayDetails(payDetails);
		payInstrument.setHeadDetails(headDetails);

		OtsTransaction otsTxn = new OtsTransaction();
		otsTxn.setPayInstrument(payInstrument);

		Gson gson = new Gson();
		String json = gson.toJson(otsTxn);
		System.out.println("Final JsonOutput----: " + json);

		String encryptedData="",decryptedData="";
		String serverResp = "";
		String decryptResponse = "";
		String atomTokenId="";
		try {
		
			encryptedData = AuthEncryption.getAuthEncrypted(json, "A4476C2062FFA58980DC8F79EB6A799E");
			System.out.println("EncryptedData------: " + encryptedData);
			try {
				serverResp = AipayService.getTransactionStatus(merchantId, encryptedData);
				//serverResp="merchId";
				System.out.println("serverResp Result------: " + serverResp);
				System.out.println("serverResp Length------: " + serverResp.length());
				System.out.println("serverResp Condition---: " + serverResp.startsWith("merchId"));

				if ((serverResp != null) && (serverResp.startsWith("merchId"))) {
					decryptResponse = serverResp.split("\\&encData=")[1];
					System.out.println("serrResp---: " + decryptResponse);

					decryptedData = AuthEncryption.getAuthDecrypted(decryptResponse, "75AEF0FA1B94B3C10D4F5B268F757F11");
					System.out.println("DecryptedData------: " + decryptedData);
					ResponseParser resp = objectMapper.readValue(decryptedData, ResponseParser.class);
		           return transformRawResponse(resp, currentStatus);
	
				} else {
					String errorDescription="Something Went Wrong!";
					System.out.println("Something Went Wrong!");
		            throw new CustomException(null, "Auth API response is not Valid!! ");

					
				}
			}
			catch (Exception e) {
	            log.error("Some Error Occured in Status API Call", e);
	            throw new CustomException(null, "Some Error Occured in token API Call");
			}
		}
		catch (Exception e) {
            log.error("NTT Data fetch status failed", e);
            throw new CustomException("CHECKSUM_GEN_FAILED", "Gateway Status API error!!!");
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
        return "transactionId";
    }

    private Transaction transformRawResponse(ResponseParser resp, Transaction currentStatus) {
        Transaction.TxnStatusEnum status;

        if(resp !=null && resp.getPayInstrument() !=null)
        {
        if (resp.getPayInstrument().getResponseDetails().getStatusCode().equals("OTS00000")) {
            status = Transaction.TxnStatusEnum.SUCCESS;

            return Transaction.builder()
                    .txnId(currentStatus.getTxnId())
                    .txnAmount(resp.getPayInstrument().getPayDetails().getTotalAmount())
                    .txnStatus(status)
                    .gatewayTxnId(resp.getPayInstrument().getPayModeSpecificData().getBankDetails().getBankTxnId())
                    .gatewayStatusCode(resp.getPayInstrument().getResponseDetails().getStatusCode())
                    .gatewayStatusMsg(resp.getPayInstrument().getResponseDetails().getMessage())
                    .responseJson(resp)
                    .build();
        } else {
            if (resp.getPayInstrument().getResponseDetails().getStatusCode().equalsIgnoreCase("OTS0551"))
                status = Transaction.TxnStatusEnum.PENDING;
            else
                status = Transaction.TxnStatusEnum.FAILURE;
            return Transaction.builder()
                    .txnId(currentStatus.getTxnId())
                    .txnStatus(status)
                    .gatewayTxnId(resp.getPayInstrument().getPayModeSpecificData().getBankDetails().getBankTxnId())
                    .gatewayStatusCode(resp.getPayInstrument().getResponseDetails().getStatusCode())
                    .gatewayStatusMsg(resp.getPayInstrument().getResponseDetails().getMessage())
                     .responseJson(resp)
                    .build();
        }
        }
        else
        {
        	log.error("Some Error Occured in Status API Call");
            throw new CustomException(null, "Some Error Occured in token API Call");
		}
        

    }


    @Override
    public String generateRedirectFormData(Transaction transaction) {
        // TODO Auto-generated method stub
        return null;
    }

}