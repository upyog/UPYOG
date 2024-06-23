package org.egov.pg.service.gateways.razorpay;

import java.net.URI;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.egov.pg.constants.TransactionAdditionalFields;
import org.egov.pg.models.Transaction;
import org.egov.pg.service.Gateway;
import org.egov.pg.utils.Utils;
import org.egov.tracer.model.ServiceCallException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Razorpay Gateway implementation
 */

@Component
@Slf4j
public class RazorpayGateway implements Gateway{
	private static final String GATEWAY_NAME = "RAZORPAY";
    private final String MERCHANT_ID;
    private final String SECURE_SECRET;
    private final String LOCALE;
    private final String CURRENCY;
    private final String PAYMENT_CAPTURE;
    private RazorpayClient client;
    private final String MERCHANT_URL_PAY;
    private final boolean ACTIVE;
    
 
    @Autowired
    public RazorpayGateway(ObjectMapper objectMapper, Environment environment) {
        ACTIVE = Boolean.valueOf(environment.getRequiredProperty("razorpay.active"));
        CURRENCY = environment.getRequiredProperty("razorpay.currency");
        LOCALE = environment.getRequiredProperty("razorpay.locale");
        MERCHANT_ID = environment.getRequiredProperty("razorpay.merchant.id");
        SECURE_SECRET = environment.getRequiredProperty("razorpay.merchant.secret.key");
        MERCHANT_URL_PAY = environment.getRequiredProperty("razorpay.url.debit");
        PAYMENT_CAPTURE = environment.getRequiredProperty("razorpay.payment_capture");
        try {
        	this.client = new RazorpayClient(this.MERCHANT_ID, this.SECURE_SECRET);
          } catch (RazorpayException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        
    }
    
	@Override
	public URI generateRedirectURI(Transaction transaction) {
		  Map<String, String> fields = new HashMap<>();
		  fields.put("amount", String.valueOf(Utils.formatAmtAsPaise(transaction.getTxnAmount())));
	        fields.put("merchant_key", MERCHANT_ID);
	        fields.put("locale", LOCALE);
	        fields.put("currency", CURRENCY);
	        fields.put("returnURL", transaction.getCallbackUrl());
	        fields.put("merchTxnRef", transaction.getTxnId());
	        fields.put("payment_capture", PAYMENT_CAPTURE);
	        
	        JSONObject request = new JSONObject();
			request.put("amount", String.valueOf(Utils.formatAmtAsPaise(transaction.getTxnAmount())));
			request.put("payment_capture", PAYMENT_CAPTURE);
			request.put("currency", CURRENCY);
			JSONArray transfers = new JSONArray();
			JSONObject transfer = new JSONObject();
			transfer.put("amount", String.valueOf(Utils.formatAmtAsPaise(transaction.getTxnAmount())));
			transfer.put("currency", CURRENCY);
			transfer.put("account", transaction.getAdditionalFields().get(TransactionAdditionalFields.BANK_ACCOUNT_NUMBER));
			JSONObject notesData=new JSONObject();
	         	notesData.put("ConsumerCode",transaction.getConsumerCode());
			notesData.put("ConsumerName",transaction.getUser().getName());
			notesData.put("MobileNumber",transaction.getUser().getMobileNumber());
			notesData.put("ServiceType",transaction.getBusinessService());
			notesData.put("TenantId",transaction.getTenantId());
			notesData.put("BillId",transaction.getBillId());
	        	request.put("notes", notesData);
	        
	     		transfers.put(transfer);
			request.put("transfers", transfers);
			log.info(request.toString());
			Order order = null;
			
			try {
				order = client.Orders.create(request);
			} catch (RazorpayException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
	        fields.forEach(params::add);
	        final String encriptionType = "SHA256";
	        params.add("secureHashType", encriptionType);
	        params.add("orderId", order.get("id"));
	        params.add("amount", String.valueOf(Utils.formatAmtAsPaise(transaction.getTxnAmount())));
	        
	        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(MERCHANT_URL_PAY).queryParams
	                (params).build().encode();
	        return uriComponents.toUri();
	}


	@Override
	public Transaction fetchStatus(Transaction currentStatus, Map<String, String> params) {
       final Object generatedSignature;
        try {
        	RazorpayClient razorpay = null;
     	   	Order order = null;
     	   	List<Payment> ordernew = null;
       	   int j=0,k=0;
        	generatedSignature = hmac_sha256(params.get("razorpayOrderId") + "|" + params.get("razorpayPaymentId"), SECURE_SECRET);
			//Payment payment = client.Payments.fetch(params.get("razorpayPaymentId"));
			if (generatedSignature.equals(params.get("razorpaySignature")))
				{

	            return Transaction.builder()
	                    .txnId(currentStatus.getTxnId())
	                    .txnAmount(currentStatus.getTxnAmount())
	                    .txnStatus(Transaction.TxnStatusEnum.SUCCESS)
	                    .gatewayTxnId(params.get("razorpayOrderId"))
	                    .gatewayPaymentMode(params.get("razorpayOrderId"))
	                    .build();
				}
			else
				{
				razorpay = new RazorpayClient(MERCHANT_ID,SECURE_SECRET);
				 order = razorpay.Orders.fetch(currentStatus.getGatewayTxnId());
		       		order.get("status");
				log.info("Order id :"+currentStatus.getGatewayTxnId());
				log.info("Order id status:"+order.get("status"));
		       		ordernew = razorpay.Orders.fetchPayments(currentStatus.getGatewayTxnId());
		       		if(ordernew.isEmpty() ||(order.get("status")).equals("created")|| (order.get("status").equals("attempted")))
		       		{
					log.info("************ Inside created and attempted status ***************");
			            return Transaction.builder()
			                    .txnId(currentStatus.getTxnId())
			                    .txnAmount(currentStatus.getTxnAmount())
			                    .txnStatus(Transaction.TxnStatusEnum.FAILURE)
			                    .gatewayTxnId(currentStatus.getGatewayTxnId())
			                    .gatewayPaymentMode(currentStatus.getGatewayTxnId())
			            		.build();
		       		}
		       		if(!ordernew.isEmpty())
		       		{
					for(int i=0;i<ordernew.size();i++)
			       		{
			       			if(!ordernew.get(i).get("status").equals("failed"))
			       				{
			       				j=j+1;
			       				k=i;
			       				}
			       		}
				  if(ordernew.get(k).get("status").equals("failed"))
					{
			            return Transaction.builder()
			                    .txnId(currentStatus.getTxnId())
			                    .txnAmount(currentStatus.getTxnAmount())
			                    .txnStatus(Transaction.TxnStatusEnum.FAILURE)
			                    .gatewayTxnId(currentStatus.getGatewayTxnId())
			                    .gatewayPaymentMode(currentStatus.getGatewayTxnId())
			            		.build();
				}
		       		
					else if((ordernew.get(k).get("status").equals("captured")) || (order.get("status")).equals("paid")||(ordernew.get(k).get("status").equals("paid")))
					{
						log.info("************ Inside captured and paid status ***************");
			            return Transaction.builder()
			                    .txnId(currentStatus.getTxnId())
			                    .txnAmount(currentStatus.getTxnAmount())
			                    .txnStatus(Transaction.TxnStatusEnum.SUCCESS)
			                    .gatewayTxnId(currentStatus.getGatewayTxnId())
			                    .gatewayPaymentMode(currentStatus.getGatewayTxnId())
			                    .build();
				  }
	              }
			}
			
        } catch (Exception e){
            throw new ServiceCallException("Error occurred while fetching status from payment gateway");
        }
	log.info("************ Inside PENDING status ***************");
    return Transaction.builder()
                  .txnId(currentStatus.getTxnId())
                  .txnAmount(currentStatus.getTxnAmount())
                  .txnStatus(Transaction.TxnStatusEnum.PENDING)
                  .gatewayTxnId(currentStatus.getGatewayTxnId())
                  .gatewayPaymentMode(currentStatus.getGatewayTxnId())
	               .build();

	}
	
	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return ACTIVE;
	}
	@Override
	public String gatewayName() {
		// TODO Auto-generated method stub
		return GATEWAY_NAME;
	}
	
	@Override
	public String transactionIdKeyInResponse() {
		return "ORDERID";
	}


	    private Object hmac_sha256(String data, String secret) throws SignatureException {
			// TODO Auto-generated method stub
	    	String result;
	        final String hmacSha256Algorithm = "HmacSHA256";

	        try {

	            // get an hmac_sha256 key from the raw secret bytes
	            SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), hmacSha256Algorithm);

	            // get an hmac_sha256 Mac instance and initialize with the signing key
	            Mac mac = Mac.getInstance(hmacSha256Algorithm);
	            mac.init(signingKey);

	            // compute the hmac on input data bytes
	            byte[] rawHmac = mac.doFinal(data.getBytes());

	            // base64-encode the hmac
	            result = DatatypeConverter.printHexBinary(rawHmac).toLowerCase();

	        } catch (Exception e) {
	            throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
	        }
	        return result;
		}

		@Override
		public String generateRedirectFormData(Transaction transaction) {
			// TODO Auto-generated method stub
			return null;
		}


}
