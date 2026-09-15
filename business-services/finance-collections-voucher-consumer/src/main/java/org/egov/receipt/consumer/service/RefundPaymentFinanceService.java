package org.egov.receipt.consumer.service;

import java.util.Map;

import org.egov.mdms.service.MicroServiceUtil;
import org.egov.receipt.consumer.model.ProcessStatus;
import org.egov.receipt.consumer.model.RefundKafkaDetail;
import org.egov.receipt.consumer.model.RefundKafkaRequest;
import org.egov.receipt.consumer.model.RefundPaymentFinanceDetail;
import org.egov.receipt.consumer.model.RefundPaymentFinanceRequest;
import org.egov.receipt.consumer.model.RefundPaymentMapping;
import org.egov.receipt.consumer.model.RequestInfo;
import org.egov.receipt.consumer.repository.ServiceRequestRepository;
import org.egov.receipt.custom.exception.VoucherCustomException;
import org.egov.reciept.consumer.config.PropertiesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RefundPaymentFinanceService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RefundPaymentFinanceService.class);

	@Autowired
	private PropertiesManager propertiesManager;

	@Autowired
	private MicroServiceUtil microServiceUtil;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * Processes a completed refund received from Refund Service and forwards the
	 * accounting details to Finance for direct Payment Voucher creation.
	 */
	public Map<String, Object> createRefundPaymentVoucher(final RefundKafkaRequest kafkaRequest)
			throws VoucherCustomException {

		validateKafkaRequest(kafkaRequest);

		final RefundKafkaDetail refund = kafkaRequest.getRefund();

		/*
		 * Use a separate RequestInfo instance. Setting authToken to null allows
		 * ServiceRequestRepository to generate the configured SYSTEM token. The
		 * original Kafka request is not modified.
		 */
		final RequestInfo serviceRequestInfo = objectMapper.convertValue(kafkaRequest.getRequestInfo(),
				RequestInfo.class);

		serviceRequestInfo.setAuthToken(null);

		final RefundPaymentMapping paymentMapping = microServiceUtil.getRefundPaymentMapping(refund.getTenantId(),
				refund.getBusinessService(), refund.getRefundMode(), serviceRequestInfo);

		validatePaymentMapping(paymentMapping);

		final RefundPaymentFinanceRequest financeRequest = buildFinanceRequest(refund, serviceRequestInfo,
				paymentMapping);

		final StringBuilder financeUrl = buildFinanceUrl(refund.getTenantId());

		try {
			LOGGER.info("Calling Finance refund Payment Voucher endpoint " + "for refund: {}, tenant: {}, URL: {}",
					refund.getRefundNo(), refund.getTenantId(), financeUrl);

			final Object rawResponse = serviceRequestRepository.fetchResult(financeUrl, financeRequest,
					refund.getTenantId());

			final Map<String, Object> response = objectMapper.convertValue(rawResponse,
					new TypeReference<Map<String, Object>>() {
					});

			validateFinanceResponse(response);

			LOGGER.info("Finance refund Payment Voucher processed " + "successfully for refund: {}",
					refund.getRefundNo());

			return response;

		} catch (VoucherCustomException exception) {
			throw exception;

		} catch (Exception exception) {
			LOGGER.error("Error while creating Finance Payment Voucher " + "for refund: {}", refund.getRefundNo(),
					exception);

			throw new VoucherCustomException(ProcessStatus.FAILED, "Unable to create Finance Payment Voucher "
					+ "for refund " + refund.getRefundNo() + ": " + exception.getMessage());
		}
	}

	private void validateKafkaRequest(final RefundKafkaRequest kafkaRequest) throws VoucherCustomException {

		if (kafkaRequest == null) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Refund payment Kafka request is missing");
		}

		if (kafkaRequest.getRequestInfo() == null) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "RequestInfo is missing in refund payment request");
		}

		final RefundKafkaDetail refund = kafkaRequest.getRefund();

		if (refund == null) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Refund payment details are missing");
		}

		if (!StringUtils.hasText(refund.getTenantId())) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Tenant ID is missing in refund payment request");
		}

		if (!StringUtils.hasText(refund.getRefundNo())) {
			throw new VoucherCustomException(ProcessStatus.FAILED,
					"Refund number is missing in refund payment request");
		}

		if (!StringUtils.hasText(refund.getBusinessService())) {
			throw new VoucherCustomException(ProcessStatus.FAILED,
					"Business service is missing in refund payment request");
		}

		if (!StringUtils.hasText(refund.getStatus())) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Refund payment status is missing");
		}

		if (!propertiesManager.getRefundPaymentSuccessStatus().equalsIgnoreCase(refund.getStatus().trim())) {

			throw new VoucherCustomException(ProcessStatus.FAILED,
					"Payment Voucher cannot be created for refund status " + refund.getStatus());
		}

		if (refund.getRefundAmount() == null || refund.getRefundAmount().signum() <= 0) {

			throw new VoucherCustomException(ProcessStatus.FAILED, "Refund amount must be greater than zero");
		}

		if (!StringUtils.hasText(refund.getRefundMode())) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Refund mode is missing");
		}
	}

	private void validatePaymentMapping(final RefundPaymentMapping paymentMapping) throws VoucherCustomException {

		if (paymentMapping == null) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Refund Payment Voucher MDMS mapping is missing");
		}

		if (!StringUtils.hasText(paymentMapping.getPayableGlCode())) {

			throw new VoucherCustomException(ProcessStatus.FAILED,
					"Payable GL code is missing in RefundPaymentMapping");
		}

		if (!StringUtils.hasText(paymentMapping.getBankAccountNumber())) {

			throw new VoucherCustomException(ProcessStatus.FAILED,
					"Bank account number is missing " + "in RefundPaymentMapping");
		}

		if (!StringUtils.hasText(paymentMapping.getFund())) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Fund code is missing in RefundPaymentMapping");
		}

		if (!StringUtils.hasText(paymentMapping.getDepartment())) {

			throw new VoucherCustomException(ProcessStatus.FAILED,
					"Department code is missing " + "in RefundPaymentMapping");
		}
	}

	private RefundPaymentFinanceRequest buildFinanceRequest(final RefundKafkaDetail refund,
			final RequestInfo requestInfo, final RefundPaymentMapping mapping) {

		final String refundMode = StringUtils.hasText(refund.getRefundMode()) ? refund.getRefundMode().trim()
				: mapping.getPaymentMode();

		final RefundPaymentFinanceDetail paymentDetail = RefundPaymentFinanceDetail.builder()
				.refundApplicationNumber(refund.getRefundNo()).gatewayRefundId(refund.getGatewayRefundId())
				.refundAmount(refund.getRefundAmount()).refundMode(refundMode).paymentStatus(refund.getStatus().trim())
				.payableGlCode(mapping.getPayableGlCode()).bankAccountNumber(mapping.getBankAccountNumber())
				.fundCode(mapping.getFund()).departmentCode(mapping.getDepartment())
				.functionCode(normalizeOptional(mapping.getFunction())).build();

		return RefundPaymentFinanceRequest.builder().requestInfo(requestInfo).tenantId(refund.getTenantId())
				.refundPayment(paymentDetail).build();
	}

	private StringBuilder buildFinanceUrl(final String tenantId) throws VoucherCustomException {

		final String financeHost = propertiesManager.getErpURLBytenantId(tenantId);

		final String endpoint = propertiesManager.getRefundPaymentCreateUrl();

		if (!StringUtils.hasText(financeHost)) {
			throw new VoucherCustomException(ProcessStatus.FAILED,
					"Tenant-specific Finance host could not be generated");
		}

		if (!StringUtils.hasText(endpoint)) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Finance refund payment endpoint is not configured");
		}

		final String separator = financeHost.endsWith("/") || endpoint.startsWith("/") ? "" : "/";

		return new StringBuilder(financeHost).append(separator).append(endpoint).append("?tenantId=").append(tenantId);
	}

	private void validateFinanceResponse(final Map<String, Object> response) throws VoucherCustomException {

		if (response == null || response.isEmpty()) {
			throw new VoucherCustomException(ProcessStatus.FAILED,
					"Empty response received from Finance " + "while creating Payment Voucher");
		}

		if (response.get("RefundPayment") == null) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Finance did not return refund payment details");
		}
	}

	private String normalizeOptional(final String value) {
		return StringUtils.hasText(value) ? value.trim() : null;
	}
}