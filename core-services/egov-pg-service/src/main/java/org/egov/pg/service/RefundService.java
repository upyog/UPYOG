package org.egov.pg.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pg.config.AppProperties;
import org.egov.pg.models.CollectionPayment;
import org.egov.pg.models.PaymentRequest;
import org.egov.pg.models.Refund;
import org.egov.pg.models.Refund.RefundStatusEnum;
import org.egov.pg.models.RefundDump;
import org.egov.pg.models.RefundRequest;
import org.egov.pg.models.Transaction;
import org.egov.pg.producer.Producer;
import org.egov.pg.repository.RefundRepository;
import org.egov.pg.repository.TransactionRepository;
import org.egov.pg.validator.RefundValidator;
import org.egov.pg.web.models.RefundCriteria;
import org.egov.pg.web.models.TransactionCriteria;
import org.egov.pg.web.models.TransactionRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RefundService {

	private final RefundValidator refundValidator;
	private final EnrichmentService enrichmentService;
	private final RefundRepository refundRepository;
	private final Producer producer;
	private final AppProperties appProperties;
	private final GatewayService gatewayService;
	private final PaymentsService paymentsService;
	private final TransactionRepository transactionRepository;

	@Autowired
	public RefundService(RefundValidator refundValidator, EnrichmentService enrichmentService,
			RefundRepository refundRepository, Producer producer, AppProperties appProperties,
			GatewayService gatewayService, PaymentsService paymentsService,
			TransactionRepository transactionRepository) {
		this.refundValidator = refundValidator;
		this.enrichmentService = enrichmentService;
		this.refundRepository = refundRepository;
		this.producer = producer;
		this.appProperties = appProperties;
		this.gatewayService = gatewayService;
		this.paymentsService = paymentsService;
		this.transactionRepository = transactionRepository;
	}

	public Refund initiateRefund(RefundRequest refundRequest) {
		Refund refund = refundRequest.getRefund();
		RequestInfo requestInfo = refundRequest.getRequestInfo();

//		enrichmentService.enrichInitiateRefundRequest(refundRequest);

		producer.push(appProperties.getSaveRefundTxnsTopic(), new RefundRequest(requestInfo, refund));
		Refund gatewayResponse = gatewayService.initiateRefund(refund);
		producer.push(appProperties.getUpdateRefundTxnsTopic(), new RefundRequest(requestInfo, gatewayResponse));

		if (gatewayResponse.getStatus().equals(RefundStatusEnum.SUCCESS)) {
			TransactionCriteria criteria = TransactionCriteria.builder().txnId(gatewayResponse.getOriginalTxnId())
					.build();
			List<Transaction> status = transactionRepository.fetchTransactions(criteria);
			TransactionRequest TxnRequest = TransactionRequest.builder().requestInfo(requestInfo)
					.transaction(status.get(0)).build();
			paymentsService.cancelTransaction(TxnRequest);
		}

		return gatewayResponse;
	}

	public List<Refund> getRefundTransaction(@Valid RefundCriteria refundCriteria) {
		log.info(refundCriteria.toString());
		try {
			return refundRepository.fetchRefundTransactions(refundCriteria);
		} catch (DataAccessException e) {
			log.error("Unable to fetch data from the database for criteria: " + refundCriteria.toString(), e);
			throw new CustomException("FETCH_REFUND_FAILED", "Unable to fetch refund transaction from store");
		}

	}

	public List<Refund> updateRefundTransaction(RequestInfo requestInfo, Map<String, String> requestParams) {
		Refund currentRefund = refundValidator.validateUpdateRefundTransaction(requestParams);

		enrichmentService.enrichupdateRefundTransaction(currentRefund);
		Refund newRefundTxn = null;
		if (refundValidator.skipGateway(currentRefund)) {
			newRefundTxn = currentRefund;

		} else {
			newRefundTxn = gatewayService.getRefundLiveStatus(currentRefund);

			if (newRefundTxn.getStatus().equals(RefundStatusEnum.SUCCESS)) {
				TransactionCriteria criteria = TransactionCriteria.builder().txnId(newRefundTxn.getOriginalTxnId())
						.build();
				List<Transaction> status = transactionRepository.fetchTransactions(criteria);
				TransactionRequest TxnRequest = TransactionRequest.builder().requestInfo(requestInfo)
						.transaction(status.get(0)).build();
				paymentsService.cancelTransaction(TxnRequest);
			}
		}

		producer.push(appProperties.getUpdateRefundTxnsTopic(), new RefundRequest(requestInfo, newRefundTxn));

		return Collections.singletonList(newRefundTxn);
	}

	public void processRefund(PaymentRequest payment, String topic) {
		RequestInfo requestInfo = payment.getRequestInfo();
		String transactionNumber = payment.getPayment().getTransactionNumber();

		if (!StringUtils.hasText(transactionNumber)) {
			log.error("Transaction number is missing in collectionPayment: {}", payment.getPayment());
			throw new CustomException("INVALID_TRANSACTION_NUMBER", "Transaction number cannot be null or empty");
		}
		TransactionCriteria criteria = TransactionCriteria.builder().txnId(transactionNumber).build();
		List<Transaction> transactions = transactionRepository.fetchTransactions(criteria);
		refundValidator.validateTransaction(transactions);
		RefundRequest refundRequest = enrichmentService.enrichRefundRequest(transactions, requestInfo);
		this.initiateRefund(refundRequest);
	}

}
