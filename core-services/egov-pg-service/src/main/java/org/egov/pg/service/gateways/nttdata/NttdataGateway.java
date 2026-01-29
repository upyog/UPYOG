package org.egov.pg.service.gateways.nttdata;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.egov.pg.models.Refund;
import org.egov.pg.models.Transaction;
import org.egov.pg.service.Gateway;
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
    private final String MERCHANT_PATH_ATOM;
    private final String MERCHANT_PATH_STATUS;
    private final String MERCHANT_ID;
    private final boolean ACTIVE;
    private ObjectMapper objectMapper;

    private final String ORIGINAL_RETURN_URL_KEY;
    private final String REDIRECT_URL;
    private final String REFUND_API;
    private final String REFUND_MERCHANT_ID;
    private final String REFUND_STATUS_API;
    private RestTemplate restTemplate;

    @Autowired
    public NttdataGateway(RestTemplate restTemplate, ObjectMapper objectMapper, Environment environment) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;

        ACTIVE = Boolean.valueOf(environment.getRequiredProperty("nttdata.active"));
        MERCHANT_ID =  environment.getRequiredProperty("nttdata.merchant.id");
        MERCHANT_HOST = environment.getRequiredProperty("nttdata.merchant.checkout.host");
        MERCHANT_PATH_ATOM=environment.getRequiredProperty("nttdata.gateway.url.atom");
        MERCHANT_PATH_STATUS = environment.getRequiredProperty("nttdata.gateway.url.status");
        REDIRECT_URL = environment.getRequiredProperty("nttdata.redirect.url");
        ORIGINAL_RETURN_URL_KEY = environment.getRequiredProperty("nttdata.original.return.url.key");
        REFUND_API = environment.getRequiredProperty("nttdata.refund.url");
        REFUND_MERCHANT_ID=environment.getRequiredProperty("nttdata.refund.merchant.id");
        REFUND_STATUS_API=environment.getRequiredProperty("nttdata.refund.url.status");
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

		String merchantId = MERCHANT_ID;

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
		DateTimeFormatter myFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime date = LocalDateTime.now();
		String dateFormat = myFormat.format(date);
		merchDetails.setMerchTxnDate(dateFormat);
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
			serverResp = AipayService.getAtomTokenId(merchantId, encryptedData,MERCHANT_PATH_ATOM);
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
            params.add("returnURL", getReturnUrl(transaction.getCallbackUrl(), REDIRECT_URL));
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
        
		String merchantId = MERCHANT_ID;
		String merchantTxnId = currentStatus.getTxnId();
		System.out.println("MerchantId------: " + merchantId);
		System.out.println("Amount----------: " + currentStatus.getTxnAmount());
		System.out.println("MerchantTxnId---: " + merchantTxnId);

		MerchDetails merchDetails = new MerchDetails();
		merchDetails.setMerchId(merchantId);
		merchDetails.setMerchTxnId(merchantTxnId);
		merchDetails.setPassword("Test@123");
		DateTimeFormatter myFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDateTime date = LocalDateTime.now();
		String dateFormat = myFormat.format(date);
		merchDetails.setMerchTxnDate(dateFormat);
		PayDetails payDetails = new PayDetails();
		payDetails.setAmount(currentStatus.getTxnAmount());
		payDetails.setTxnCurrency("INR");
		String signature=merchantId + "Test@123" + merchantTxnId + currentStatus.getTxnAmount() + "INR" + "TXNVERIFICATION";
		payDetails.setSignature(AtomSignature.generateSignature("KEY123657234",signature));
	
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
		String decryptResponsee = "";
		try {
		
			encryptedData = AuthEncryption.getAuthEncrypted(json, "A4476C2062FFA58980DC8F79EB6A799E");
			System.out.println("EncryptedData------: " + encryptedData);
			try {
				serverResp = AipayService.getTransactionStatus(merchantId, encryptedData,MERCHANT_PATH_STATUS);
				//serverResp="merchId";
				System.out.println("serverResp Result------: " + serverResp);
				System.out.println("serverResp Length------: " + serverResp.length());
				System.out.println("serverResp Condition---: " + serverResp.startsWith("encData"));

				if ((serverResp != null) && (serverResp.startsWith("encData"))) {
					decryptResponsee = serverResp.split("\\&merchId=")[0];
					String decryptResponse = decryptResponsee.split("encData=")[1];

					System.out.println("serrResp---: " + decryptResponse);

					decryptedData = AuthEncryption.getAuthDecrypted(decryptResponse, "75AEF0FA1B94B3C10D4F5B268F757F11");
					System.out.println("DecryptedData------: " + decryptedData);
					if(!decryptedData.contains("OTS0000"))
						throw new CustomException(null, "Response from Fetch API is "+decryptedData);
					ResponseParser resp = objectMapper.readValue(decryptedData, ResponseParser.class);
		           return transformRawResponse(resp, currentStatus);
	
				} else {
					String errorDescription="Something Went Wrong!";
					System.out.println("Something Went Wrong!");
		            throw new CustomException(null, "Auth API response is not Valid!! ");

					
				}
			}
			catch (Exception e) {
	            log.error("Some Error Occured in Status API Call"+ e);
	            throw new CustomException(null, "Some Error Occured in Status API Call");
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
        if (resp.getPayInstrument().get(0).getResponseDetails().getStatusCode().equals("OTS0000")) {
            status = Transaction.TxnStatusEnum.SUCCESS;

            return Transaction.builder()
                    .txnId(currentStatus.getTxnId())
                    .txnAmount(resp.getPayInstrument().get(0).getPayDetails().getTotalAmount())
                    .txnStatus(status)
                    .gatewayTxnId(resp.getPayInstrument().get(0).getPayModeSpecificData().getBankDetails().getBankTxnId())
                    .gatewayStatusCode(resp.getPayInstrument().get(0).getResponseDetails().getStatusCode())
                    .gatewayStatusMsg(resp.getPayInstrument().get(0).getResponseDetails().getMessage())
                    .responseJson(resp)
                    .build();
        } else {
            if (resp.getPayInstrument().get(0).getResponseDetails().getStatusCode().equalsIgnoreCase("OTS0551"))
                status = Transaction.TxnStatusEnum.PENDING;
            else
                status = Transaction.TxnStatusEnum.FAILURE;
            return Transaction.builder()
                    .txnId(currentStatus.getTxnId())
                    .txnStatus(status)
                    .gatewayTxnId(resp.getPayInstrument().get(0).getPayModeSpecificData().getBankDetails().getBankTxnId())
                    .gatewayStatusCode(resp.getPayInstrument().get(0).getResponseDetails().getStatusCode())
                    .gatewayStatusMsg(resp.getPayInstrument().get(0).getResponseDetails().getMessage())
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
    private String getReturnUrl(String callbackUrl, String baseurl) {
        return UriComponentsBuilder.fromHttpUrl(baseurl).queryParam(ORIGINAL_RETURN_URL_KEY, callbackUrl).build().toUriString();
    }

    @Override
    public String generateRedirectFormData(Transaction transaction) {
        // TODO Auto-generated method stub
        return null;
    }

    
    @Override
	public Refund initiateRefund(Refund refundRequest) {
		try {
			Gson gson = new Gson();
			String password = "Test@123";
			String atomTxnId = refundRequest.getOriginalTxnId();
			String merchantId = REFUND_MERCHANT_ID;
			String currency = "INR";
			String api = "REFUNDINIT";
			String source = "OTS";
			String merchantTxnId = refundRequest.getOriginalTxnId();

			MerchDetails merchDetails = new MerchDetails();
			merchDetails.setMerchId(merchantId);
			merchDetails.setMerchTxnId(merchantTxnId);
			merchDetails.setPassword(password);
			merchDetails.setAtomTxnId(atomTxnId);

			PayDetails payDetails = new PayDetails();
			payDetails.setTxnCurrency(currency);
			List<ProdDetails> prodDetailsList = new ArrayList<>();
			ProdDetails prodDetails = new ProdDetails();
			prodDetails.setProdRefundId(refundRequest.getRefundId());
			prodDetails.setProdName(refundRequest.getConsumerCode());
			prodDetails.setProdRefundAmount(Double.valueOf((refundRequest.getRefundAmount())));
			prodDetailsList.add(prodDetails);
			payDetails.setProdDetails(prodDetailsList);
			double totalRefundAmount = calculateTotalRefundAmount(prodDetailsList);
			payDetails.setTotalRefundAmount(totalRefundAmount);

			// merchId + password + merchTxnId + total amount + txnCurrency + api
			String signature = merchantId + password + merchantTxnId + totalRefundAmount + currency + api;
			payDetails.setSignature(AtomSignature.generateSignature("KEY1234567234", signature));

			HeadDetails headDetails = new HeadDetails();
			headDetails.setApi(api);
			headDetails.setSource(source);

			PayInstrument payInstrument = new PayInstrument();

			payInstrument.setMerchDetails(merchDetails);
			payInstrument.setPayDetails(payDetails);
			payInstrument.setHeadDetails(headDetails);

			RefundTransaction refundTxn = new RefundTransaction();
			refundTxn.setPayInstrument(payInstrument);

			String refundJson = gson.toJson(refundTxn);
			// Encrypt
			String encryptedData, decryptedData = "";
			String decryptResponsee = "";
			String serverResp = "";
			encryptedData = AuthEncryption.getAuthEncrypted(refundJson, "7813E3E5E93548B096675AC27FE2C850");

			serverResp = AipayService.processRefund(merchantId, encryptedData, REFUND_API);
			if ((serverResp != null) && (serverResp.startsWith("encData"))) {
				decryptResponsee = serverResp.split("\\&merchId=")[0];
				String decryptResponse = decryptResponsee.split("encData=")[1];

				System.out.println("serrResp---: " + decryptResponse);

				decryptedData = AuthEncryption.getAuthDecrypted(decryptResponse, "58BE879B7DD635698764745511C704AB");
				System.out.println("DecryptedData------: " + decryptedData);
				if (!decryptedData.contains("OTS0000"))
					throw new CustomException(null, "Response from Fetch API is " + decryptedData);
				ResponseParser resp = objectMapper.readValue(decryptedData, ResponseParser.class);
				return transformRawRefundResponse(resp, refundRequest);

			} else {
				String errorDescription = "Something Went Wrong!";
				System.out.println(errorDescription);
				throw new CustomException(null, "Auth API response is not Valid!! ");

			}

		} catch (Exception e) {
			log.error("Refund failed", e);
			throw new CustomException("REFUND_ERROR", "Refund initiation failed");
		}
	}
	
	private Refund transformRawRefundResponse(ResponseParser resp, Refund refundRequest) {
		Refund.RefundStatusEnum status;

		if (resp != null && resp.getPayInstrument() != null) {
			if (resp.getPayInstrument().get(0).getResponseDetails().getStatusCode().equals("OTS0000")
					|| resp.getPayInstrument().get(0).getResponseDetails().getStatusCode().equals("OTS0001")) {
				status = Refund.RefundStatusEnum.SUCCESS;

				return Refund.builder().refundId(refundRequest.getRefundId()).status(status)
						.refundAmount(
								String.valueOf(resp.getPayInstrument().get(0).getPayDetails().getTotalRefundAmount()))
						.originalTxnId(refundRequest.getOriginalTxnId())
						.gatewayStatusCode(resp.getPayInstrument().get(0).getResponseDetails().getStatusCode())
						.gatewayTxnId(resp.getPayInstrument().get(0).getPayDetails().getAtomTxnId())
						.gatewayStatusMsg(resp.getPayInstrument().get(0).getResponseDetails().getDescription())
						.build();
			} else {
				if (resp.getPayInstrument().get(0).getResponseDetails().getStatusCode().equals("OTS0551"))
					status = Refund.RefundStatusEnum.PENDING;
				 else 
					status = Refund.RefundStatusEnum.FAILURE;
				return Refund.builder().refundId(refundRequest.getRefundId()).status(status)
						.refundAmount(
								String.valueOf(resp.getPayInstrument().get(0).getPayDetails().getTotalRefundAmount()))
						.originalTxnId(refundRequest.getOriginalTxnId())
						.gatewayStatusCode(resp.getPayInstrument().get(0).getResponseDetails().getStatusCode())
						.gatewayTxnId(resp.getPayInstrument().get(0).getPayDetails().getAtomTxnId())
						.gatewayStatusMsg(resp.getPayInstrument().get(0).getResponseDetails().getDescription())
						.build();
			}
		} else {
			log.error("Some Error Occured in Status API Call");
			throw new CustomException(null, "Some Error Occured in token API Call");
		}
	}
	
	@Override
	public Refund fetchRefundStatus(Refund refundRequest) {

	    try {
	        Gson gson = new Gson();

	        String merchantId = REFUND_MERCHANT_ID;
	        String api = "REFUNDSTATUS";
	        String source = "OTS_ARS";

	        // Atom transaction id (original payment txn id)
	        String atomTxnId = refundRequest.getOriginalTxnId();

	        
	        String password = "Test@123";
	        String encodedPassword = generateBase64Password(password);

	      
	        MerchDetails merchDetails = new MerchDetails();
	        merchDetails.setMerchId(merchantId);
	        merchDetails.setPassword(encodedPassword);

	        ProdDetails prodDetails = new ProdDetails();
	        prodDetails.setProdName(refundRequest.getConsumerCode());

	        List<ProdDetails> prodDetailsList = new ArrayList<>();
	        prodDetailsList.add(prodDetails);

	        PayDetails payDetails = new PayDetails();
	        payDetails.setAtomTxnId(atomTxnId);
	        payDetails.setProdDetails(prodDetailsList);
	       
	        HeadDetails headDetails = new HeadDetails();
	        headDetails.setApi(api);
	        headDetails.setSource(source);
	       
	        PayInstrument payInstrument = new PayInstrument();
	        payInstrument.setHeadDetails(headDetails);
	        payInstrument.setMerchDetails(merchDetails);
	        payInstrument.setPayDetails(payDetails);
	       
	        RefundTransaction refundTxn = new RefundTransaction();
	        refundTxn.setPayInstrument(payInstrument);

	        String statusRequestJson = gson.toJson(refundTxn);
	        log.info("Refund Status Request JSON: {}", statusRequestJson);

	        
	        String encryptedData = AuthEncryption.getAuthEncrypted(
	                statusRequestJson,
	                "7813E3E5E93548B096675AC27FE2C850"
	        );

	       
	        String serverResp = AipayService.getTransactionStatus(
	                merchantId,
	                encryptedData,
	                REFUND_STATUS_API
	        );

	        if (serverResp == null || !serverResp.startsWith("encData")) {
	            throw new CustomException(null, "Invalid response from Refund Status API");
	        }

	     
	        String encData = serverResp.split("\\&merchId=")[0]
	                .replace("encData=", "");

	        String decryptedData = AuthEncryption.getAuthDecrypted(
	                encData,
	                "58BE879B7DD635698764745511C704AB"
	        );

	        log.info("Refund Status Decrypted Response: {}", decryptedData);

	        ResponseParser response =
	                objectMapper.readValue(decryptedData, ResponseParser.class);

	        return transformRefundStatusResponse(response, refundRequest);

	    } catch (Exception e) {
	        log.error("Refund status fetch failed", e);
	        throw new CustomException("REFUND_STATUS_ERROR", "Failed to fetch refund status");
	    }
	}


	private Refund transformRefundStatusResponse(ResponseParser resp, Refund currentRefund) {

	    if (resp == null || resp.getPayInstrument() == null) {
	        log.error("Null response received from Refund Status API");
	        throw new CustomException(null, "Invalid response from Refund Status API");
	    }

	    Refund.RefundStatusEnum status;
	    String statusCode = resp.getPayInstrument().get(0).getResponseDetails().getStatusCode();
	    String statusMsg = resp.getPayInstrument().get(0).getResponseDetails().getMessage();

	    // Status mapping
	    if ("OTS0000".equalsIgnoreCase(statusCode)) {
	        status = Refund.RefundStatusEnum.SUCCESS;

	    } else if ("OTS0551".equalsIgnoreCase(statusCode)) {

	        status = Refund.RefundStatusEnum.PENDING;

	    } else {
	        status = Refund.RefundStatusEnum.FAILURE;
	    }

	   
	    String refundTxnId = null;
	    String refundAmt = null;

	    try {
	        if (resp.getPayInstrument().get(0).getRefundStatusDetails() != null
	                && resp.getPayInstrument().get(0).getRefundStatusDetails().getRefundDetails() != null
	                && !resp.getPayInstrument().get(0).getRefundStatusDetails().getRefundDetails().isEmpty()) {

	            RefundDetails refundDetails =
	                    resp.getPayInstrument().get(0).getRefundStatusDetails().getRefundDetails().get(0);

	            if (refundDetails.getRefundStatus() != null
	                    && !refundDetails.getRefundStatus().isEmpty()) {

	                RefundStatus refundStatus = refundDetails.getRefundStatus().get(0);

	                refundTxnId = String.valueOf(resp.getPayInstrument().get(0).getPayDetails().getAtomTxnId());
	                refundAmt = String.valueOf(refundStatus.getRefundAmt());
	            }
	        }
	    } catch (Exception e) {
	        log.warn("Unable to parse refundTxnId or refundAmt from refund status response", e);
	    }

	    return Refund.builder()
	            .id(currentRefund.getId())
	            .tenantId(currentRefund.getTenantId())
	            .refundId(currentRefund.getRefundId())
	            .originalTxnId(currentRefund.getOriginalTxnId())
	            .serviceCode(currentRefund.getServiceCode())
	            .originalAmount(currentRefund.getOriginalAmount())
	            .refundAmount(refundAmt != null ? refundAmt : currentRefund.getRefundAmount())
	            .gateway(currentRefund.getGateway())
	            .consumerCode(currentRefund.getConsumerCode())
	            .gatewayTxnId(refundTxnId)
	            .gatewayStatusCode(statusCode)
	            .gatewayStatusMsg(statusMsg)
	            .status(status)
	            .auditDetails(currentRefund.getAuditDetails())
	            .build();
	}


	private String generateBase64Password(String password) {
		return Base64.getEncoder()
                .encodeToString(password.getBytes());
	}

	private double calculateTotalRefundAmount(List<ProdDetails> prodDetailsList) {

		if (prodDetailsList == null || prodDetailsList.isEmpty()) {
			throw new CustomException("TOTAL_REFUND_AMOUNT_ERROR", "Product refund details cannot be empty");
		}
		BigDecimal total = prodDetailsList.stream().map(ProdDetails::getProdRefundAmount).map(BigDecimal::valueOf)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		return total.doubleValue();
	}



}
