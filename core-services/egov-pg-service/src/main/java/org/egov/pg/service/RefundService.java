package org.egov.pg.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pg.config.AppProperties;
import org.egov.pg.models.Refund;
import org.egov.pg.models.RefundDump;
import org.egov.pg.models.RefundRequest;
import org.egov.pg.producer.Producer;
import org.egov.pg.repository.RefundRepository;
import org.egov.pg.validator.RefundValidator;
import org.egov.pg.web.models.RefundCriteria;
import org.egov.pg.web.models.TransactionRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

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

	@Autowired
	public RefundService(RefundValidator refundValidator, EnrichmentService enrichmentService,
			RefundRepository refundRepository, Producer producer, AppProperties appProperties,
			GatewayService gatewayService) {
		this.refundValidator = refundValidator;
		this.enrichmentService = enrichmentService;
		this.refundRepository = refundRepository;
		this.producer = producer;
		this.appProperties = appProperties;
		this.gatewayService = gatewayService;
	}

	public Refund initiateRefund(@Valid RefundRequest refundRequest) {
		Refund refund = refundRequest.getRefund();
		RequestInfo requestInfo = refundRequest.getRequestInfo();
		refundValidator.validateInitiateRefundRequest(refundRequest);

		enrichmentService.enrichInitiateRefundRequest(refundRequest);

		producer.push(appProperties.getSaveRefundTxnsTopic(), new RefundRequest(requestInfo, refund));
		Refund gatewayResponse = gatewayService.initiateRefund(refund);
		producer.push(appProperties.getSaveRefundTxnsTopic(), new RefundRequest(requestInfo, gatewayResponse));
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

		Refund newRefundTxn = null;
		if (refundValidator.skipGateway(currentRefund)) {
			newRefundTxn = currentRefund;

		} else {
			newRefundTxn = gatewayService.getRefundLiveStatus(currentRefund);

			// Enrich the new Refund transaction status before persisting
			enrichmentService.enrichUpdateRefundTransaction(new RefundRequest(requestInfo, currentRefund),
					newRefundTxn);
		}

		producer.push(appProperties.getUpdateRefundTxnsTopic(), new RefundRequest(requestInfo, newRefundTxn));

		return Collections.singletonList(newRefundTxn);
	}

}
