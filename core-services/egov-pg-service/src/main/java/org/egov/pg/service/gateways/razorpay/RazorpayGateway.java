package org.egov.pg.service.gateways.razorpay;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Base64;

import org.apache.commons.lang3.StringUtils;
import org.egov.pg.models.Transaction;
import org.egov.pg.service.Gateway;
import org.egov.pg.service.gateways.razorpay.models.Order;
import org.egov.pg.service.gateways.razorpay.models.Payment;
import org.egov.pg.service.gateways.razorpay.models.PaymentResponse;
import org.egov.pg.utils.Utils;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RazorpayGateway implements Gateway {
	private static final String GATEWAY_NAME = "RAZORPAY";

	private static final String PAYMENT_AUTO_CAPTURE = "1";

	private static final String ORDER_ID = "ORDER_ID";

	private final RestTemplate restTemplate;

	private ObjectMapper objectMapper;

	private final boolean ACTIVE;

	private final String CURRENCY;

	private final String MERCHANT_KEY_ID;

	private final String MERCHANT_KEY_SECRET;

	private final String MERCHANT_CREATE_ORDER_URL;

	private final String MERCHANT_FETCH_PAYMENT_BY_ORDER_ID_URL;

	@Autowired
	public RazorpayGateway(RestTemplate restTemplate, Environment environment, ObjectMapper objectMapper) {
		this.restTemplate = restTemplate;
		this.objectMapper = objectMapper;

		ACTIVE = Boolean.valueOf(environment.getRequiredProperty("razorpay.active"));
		CURRENCY = environment.getRequiredProperty("razorpay.currency");
		MERCHANT_KEY_ID = environment.getRequiredProperty("razorpay.key.id");
		MERCHANT_KEY_SECRET = environment.getRequiredProperty("razorpay.merchant.secret.key");
		MERCHANT_CREATE_ORDER_URL = environment.getRequiredProperty("razorpay.order.create.url");
		MERCHANT_FETCH_PAYMENT_BY_ORDER_ID_URL = environment
				.getRequiredProperty("razorpay.merchant.payments.orderid.fetch.url");
	}

	@Override
	public URI generateRedirectURI(Transaction transaction) {
		// Create Order
		Order order = createOrder(Utils.formatAmtAsPaise(transaction.getTxnAmount()), transaction.getTxnId());
		@SuppressWarnings("unchecked") // Suppress the unchecked cast warning
		LinkedHashMap<Object, Object> additionalDetails = Optional.ofNullable(transaction.getAdditionalDetails())
				.map(obj -> objectMapper.convertValue(obj, LinkedHashMap.class)).orElseGet(LinkedHashMap::new);

		additionalDetails.put(ORDER_ID, order.getOrderId());
		transaction.setAdditionalDetails(additionalDetails);

		return URI.create(StringUtils.EMPTY); // Return an empty URI
	}

	private Order createOrder(String amount, String transactionId) {

		// HttpHeaders
		HttpHeaders headers = buildHttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		// Create request body
		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
		requestBody.add("amount", amount);
		requestBody.add("currency", CURRENCY);
		requestBody.add("receipt", transactionId);
		requestBody.add("payment_capture", PAYMENT_AUTO_CAPTURE);

		try {
			// Make the POST request with Basic Authentication and get the response as an
			// object
			ResponseEntity<Order> responseEntity = restTemplate.exchange(MERCHANT_CREATE_ORDER_URL, HttpMethod.POST,
					new HttpEntity<>(requestBody, headers), Order.class);

			return responseEntity.getBody();
		} catch (Exception e) {
			log.error("Unable to create order for the transactionId: " + transactionId, e);
			throw new CustomException("ERR_HDFC_PG_SERVICE",
					"Unable to create order for the transactionId: " + transactionId);
		}
	}

	private HttpHeaders buildHttpHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		// Create Basic Auth credentials
		String authCredentials = MERCHANT_KEY_ID + ":" + MERCHANT_KEY_SECRET;
		String base64Credentials = Base64.getEncoder().encodeToString(authCredentials.getBytes());

		headers.set("Authorization", "Basic " + base64Credentials);

		return headers;
	}

	@Override
	public Transaction fetchStatus(Transaction currentStatus, Map<String, String> params) {

		String transactionAmount = currentStatus.getTxnAmount();

		String razorpayPaymentId = params.get("razorpay_payment_id");
		String razorpayOrderId = params.get("razorpay_order_id");
		String razorpaySignature = params.get("razorpay_signature");

		if (StringUtils.isNotEmpty(razorpayPaymentId) && StringUtils.isNotEmpty(razorpayOrderId)
				&& StringUtils.isNotEmpty(razorpaySignature)) {
			// Requesting from call-back url

			if (RazorPayModelUtils.isSignatureVerified(razorpayOrderId, razorpayPaymentId, razorpaySignature,
					MERCHANT_KEY_SECRET)) {

				Transaction transaction = validateAndEnrichPaymentStatus(currentStatus, transactionAmount,
						razorpayOrderId);

				if (null != transaction) {
					return transaction;
				}

			}

		} else {
			Map<String, Object> additionDetailsMap = objectMapper.convertValue(currentStatus.getAdditionalDetails(),
					Map.class);
			razorpayOrderId = additionDetailsMap.get(ORDER_ID).toString();

			// Call from CRON JOB
			Transaction transaction = validateAndEnrichPaymentStatus(currentStatus, transactionAmount, razorpayOrderId);

			if (null != transaction) {
				return transaction;
			}

		}

		return Transaction.builder().txnId(currentStatus.getTxnId()).txnAmount(currentStatus.getTxnAmount())
				.txnStatus(Transaction.TxnStatusEnum.FAILURE).build();

	}

	private Transaction validateAndEnrichPaymentStatus(Transaction currentStatus, String transactionAmount,
			String razorpayOrderId) {
		PaymentResponse paymentResponse = fetchPaymentsByOrderId(razorpayOrderId);

		if (null != paymentResponse) {

			List<Payment> successPayments = Optional.ofNullable(paymentResponse.getPayments())
					.orElse(Collections.emptyList()).stream()
					.filter(payment -> StringUtils.equals(Utils.formatAmtAsPaise(transactionAmount),
							String.valueOf(payment.getPaymentAmount())) && StringUtils.isEmpty(payment.getErrorCode())
							&& payment.isCaptured())
					.collect(Collectors.toList());

			if (!CollectionUtils.isEmpty(successPayments)) {
				Payment successPayment = successPayments.get(0);
				return Transaction.builder().txnId(currentStatus.getTxnId()).txnAmount(currentStatus.getTxnAmount())
						.txnStatus(Transaction.TxnStatusEnum.SUCCESS).gatewayTxnId(successPayment.getPaymentId())
						.gatewayStatusCode(successPayment.getPaymentStatus())
						.gatewayStatusMsg(successPayment.getPaymentStatus()).responseJson(successPayment).build();
			}

			Set<String> successPaymentIds = successPayments.stream().map(Payment::getPaymentId)
					.collect(Collectors.toSet());

			List<Payment> failedPayments = Optional.ofNullable(paymentResponse.getPayments())
					.orElse(Collections.emptyList()).stream()
					.filter(payment -> !successPaymentIds.contains(payment.getPaymentId()))
					.collect(Collectors.toList());

			if (!CollectionUtils.isEmpty(failedPayments)) {
				Payment failedPayment = failedPayments.get(0);
				return Transaction.builder().txnId(currentStatus.getTxnId()).txnAmount(currentStatus.getTxnAmount())
						.txnStatus(Transaction.TxnStatusEnum.FAILURE).gatewayTxnId(failedPayment.getPaymentId())
						.gatewayStatusCode(failedPayment.getErrorCode())
						.gatewayStatusMsg(failedPayment.getErrorReason()).responseJson(failedPayment).build();
			}

		}
		return null;
	}

	private PaymentResponse fetchPaymentsByOrderId(String orderId) {

		String url = String.format(MERCHANT_FETCH_PAYMENT_BY_ORDER_ID_URL, orderId);

		try {
			// Make a GET request to fetch payments using Basic Authentication
			ResponseEntity<PaymentResponse> responseEntity = restTemplate.exchange(url, HttpMethod.GET,
					new HttpEntity<>(buildHttpHeaders()), PaymentResponse.class);

			return responseEntity.getBody();
		} catch (Exception e) {
			log.error("Unable to fetch payments for the orderId: " + orderId, e);
			throw new CustomException("ERR_PNB_PG_SERVICE", "Unable to fetch payments for the orderId: " + orderId);
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
		return "hdfc_TxnRef";
	}
	 public String generateRedirectFormData(Transaction transaction ) {
		 return null;
	 }
}
