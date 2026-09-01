package org.egov.refund.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.refund.config.ApplicationProperties;
import org.egov.refund.web.contracat.Payment;
import org.egov.refund.web.contracat.PaymentRefundResponse;
import org.egov.refund.web.contracat.PaymentRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentGatewayService {

	private static final String SUCCESS_CODE = "OTS0000";

	private final RestTemplate restTemplate;

	private final ApplicationProperties properties;

	public PaymentRefundResponse initiateRefund(Payment payment, RequestInfo requestInfo) {

		String url = properties.getPghost() + properties.getPgrefundEndpoint();

		PaymentRequest request = PaymentRequest.builder().payment(payment).requestInfo(requestInfo).build();

		try {

			log.info("Calling PG Refund API : {}", url);

			PaymentRefundResponse response = restTemplate.postForObject(url, request, PaymentRefundResponse.class);

			validateResponse(response);

			log.info("Refund initiated successfully.");

			return response;

		} catch (RestClientException ex) {

			log.error("PG Refund API failed.", ex);

			throw new CustomException("PG_REFUND_FAILED", "Unable to initiate refund.");
		}
	}

	private void validateResponse(PaymentRefundResponse response) {

		if (response == null) {
			throw new CustomException("NULL_RESPONSE", "Payment Gateway returned null response.");
		}

		if (response.getPaymentRefund() == null) {
			throw new CustomException("NULL_REFUND", "Refund response is empty.");
		}

		if (!SUCCESS_CODE.equalsIgnoreCase(response.getPaymentRefund().getGatewayStatusCode())) {

			throw new CustomException("REFUND_FAILED", response.getPaymentRefund().getGatewayStausMsg());
		}
	}
}