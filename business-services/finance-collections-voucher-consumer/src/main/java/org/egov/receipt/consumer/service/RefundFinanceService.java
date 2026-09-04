package org.egov.receipt.consumer.service;

import java.util.Collection;
import java.util.Map;

import org.egov.receipt.consumer.model.ProcessStatus;
import org.egov.receipt.consumer.model.RefundFinanceRequest;
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
public class RefundFinanceService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RefundFinanceService.class);

	@Autowired
	private PropertiesManager propertiesManager;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * Forwards refund details received through Kafka to the Finance refund-approval
	 * endpoint.
	 */
	public Map<String, Object> createRefundApplication(final RefundFinanceRequest refundRequest)
			throws VoucherCustomException {

		validateRequest(refundRequest);

		final String tenantId = refundRequest.getTenantId();

		final String erpHost = propertiesManager.getErpURLBytenantId(tenantId);

		final String refundCreateUrl = propertiesManager.getRefundCreateUrl();

		if (!StringUtils.hasText(erpHost)) {
			throw new VoucherCustomException(ProcessStatus.FAILED,
					"Finance ERP host could not be resolved " + "for tenant: " + tenantId);
		}

		if (!StringUtils.hasText(refundCreateUrl)) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Finance refund create URL is not configured");
		}

		final String separator = erpHost.endsWith("/") || refundCreateUrl.startsWith("/") ? "" : "/";

		final StringBuilder url = new StringBuilder(erpHost).append(separator).append(refundCreateUrl)
				.append("?tenantId=").append(tenantId);
		try {
			LOGGER.info("Calling Finance refund endpoint for " + "refund application: {}, URL: {}",
					refundRequest.getRefund().getRefundApplicationNumber(), url);

			final Object rawResponse = serviceRequestRepository.fetchResult(url, refundRequest, tenantId);

			final Map<String, Object> response = objectMapper.convertValue(rawResponse,
					new TypeReference<Map<String, Object>>() {
					});

			validateResponse(response);

			return response;

		} catch (VoucherCustomException exception) {
			throw exception;

		} catch (Exception exception) {
			LOGGER.error("Error while forwarding refund " + "application {} to Finance",
					refundRequest.getRefund().getRefundApplicationNumber(), exception);

			throw new VoucherCustomException(ProcessStatus.FAILED,
					"Unable to create refund application " + "in Finance: " + exception.getMessage());
		}
	}

	private void validateRequest(final RefundFinanceRequest refundRequest) throws VoucherCustomException {

		if (refundRequest == null || refundRequest.getRefund() == null) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Refund Finance request is mandatory");
		}

		if (!StringUtils.hasText(refundRequest.getTenantId())) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Tenant ID is mandatory");
		}

		if (!StringUtils.hasText(refundRequest.getRefund().getRefundApplicationNumber())) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Refund application number is mandatory");
		}
	}

	private void validateResponse(final Map<String, Object> response) throws VoucherCustomException {

		if (response == null || response.isEmpty()) {
			throw new VoucherCustomException(ProcessStatus.FAILED, "Empty response received from Finance");
		}

		final Object refundApplications = response.get("RefundApplications");

		if (!(refundApplications instanceof Collection) || ((Collection<?>) refundApplications).isEmpty()) {
			throw new VoucherCustomException(ProcessStatus.FAILED,
					"Finance did not return the created " + "refund application");
		}
	}
}