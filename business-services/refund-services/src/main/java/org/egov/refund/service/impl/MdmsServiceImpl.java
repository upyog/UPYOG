package org.egov.refund.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.refund.Repository.ServiceRequestRepository;
import org.egov.refund.config.ApplicationProperties;
import org.egov.refund.service.MdmsService;
import org.egov.refund.util.RefundConstants;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MdmsServiceImpl implements MdmsService {

	private final ApplicationProperties config;
	private final ServiceRequestRepository serviceRequestRepository;
	private final ObjectMapper objectMapper;

	public MdmsServiceImpl(ApplicationProperties config, ServiceRequestRepository serviceRequestRepository,
			ObjectMapper objectMapper) {
		this.config = config;
		this.serviceRequestRepository = serviceRequestRepository;
		this.objectMapper = objectMapper;
	}

	@Override
	public String getReceiptKey(String tenantId, String businessService, RequestInfo requestInfo) {

		String code = isBlank(businessService) ? RefundConstants.DEFAULT_CODE : businessService;

		Object responseObject = fetchMdmsData(tenantId, requestInfo);

		JsonNode root = objectMapper.valueToTree(responseObject);

		JsonNode uiCommonPay = root.path("MdmsRes").path(RefundConstants.MODULE_NAME).path(RefundConstants.MASTER_NAME);

		// First try actual business service code
		String receiptKey = findReceiptKey(uiCommonPay, code);

		if (!isBlank(receiptKey)) {
			return receiptKey;
		}

		// If business service code is not available, use DEFAULT
		if (!RefundConstants.DEFAULT_CODE.equals(code)) {
			log.warn("ReceiptKey not found for code={}, trying default code={}", code, RefundConstants.DEFAULT_CODE);

			receiptKey = findReceiptKey(uiCommonPay, RefundConstants.DEFAULT_CODE);

			if (!isBlank(receiptKey)) {
				return receiptKey;
			}
		}

		log.warn("ReceiptKey not found for code={} or default code={}", code, RefundConstants.DEFAULT_CODE);

		return null;
	}

	private Object fetchMdmsData(String tenantId, RequestInfo requestInfo) {
		StringBuilder url = new StringBuilder(config.getMdmsHost()).append(config.getMdmsSearch()).append("?tenantId=")
				.append(tenantId);

		Map<String, Object> mdmsCriteria = new HashMap<>();

		mdmsCriteria.put("tenantId", tenantId);
		mdmsCriteria.put("moduleDetails", new Object[] { Map.of("moduleName", RefundConstants.MODULE_NAME,
				"masterDetails", new Object[] { Map.of("name", RefundConstants.MASTER_NAME) }) });

		Map<String, Object> request = new HashMap<>();
		request.put("MdmsCriteria", mdmsCriteria);
		request.put("RequestInfo", requestInfo);

		return serviceRequestRepository.fetchResult(url, request);
	}

	private String findReceiptKey(JsonNode uiCommonPay, String code) {

		if (!uiCommonPay.isArray()) {
			return null;
		}

		for (JsonNode item : uiCommonPay) {

			if (code.equals(item.path("code").asText())) {

				String receiptKey = item.path("receiptKey").asText(null);

				if (!isBlank(receiptKey)) {
					return receiptKey;
				}
			}
		}

		return null;
	}

	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}
}