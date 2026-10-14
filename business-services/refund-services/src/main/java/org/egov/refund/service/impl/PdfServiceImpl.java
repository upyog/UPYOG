package org.egov.refund.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.refund.Repository.ServiceRequestRepository;
import org.egov.refund.config.ApplicationProperties;
import org.egov.refund.service.PdfService;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PdfServiceImpl implements PdfService {

	private final ApplicationProperties config;
	private final ServiceRequestRepository serviceRequestRepository;
	private final ObjectMapper objectMapper;

	public PdfServiceImpl(ApplicationProperties config, ServiceRequestRepository serviceRequestRepository,
			ObjectMapper objectMapper) {

		this.config = config;
		this.serviceRequestRepository = serviceRequestRepository;
		this.objectMapper = objectMapper;
	}

	@Override
	public String generateReceipt(String tenantId, String receiptKey, Object pdfRequest, RequestInfo requestInfo) {

		StringBuilder url = new StringBuilder(config.getPdfHost()).append(config.getPdfCreate()).append("?tenantId=")
				.append(tenantId).append("&key=").append(receiptKey);

		Map<String, Object> request = new HashMap<>();

		if (pdfRequest != null) {
			if (pdfRequest instanceof Map) {
				request.putAll((Map<String, Object>) pdfRequest);
			} else {
				request.put("Payments", pdfRequest);
			}
		}

		request.put("RequestInfo", requestInfo);

		log.info("Generating receipt PDF. tenantId={}, receiptKey={}", tenantId, receiptKey);

		log.debug("PDF service URL: {}", url);

		Object responseObject = serviceRequestRepository.fetchResult(url, request);

		log.debug("PDF service response: {}", responseObject);

		String fileStoreId = extractFilestoreId(responseObject);

		log.info("Receipt PDF generated. tenantId={}, receiptKey={}, fileStoreId={}", tenantId, receiptKey,
				fileStoreId);

		return fileStoreId;
	}

	private String extractFilestoreId(Object responseObject) {

		if (responseObject == null) {
			log.warn("PDF service returned null response");
			return null;
		}

		try {

			JsonNode root = objectMapper.valueToTree(responseObject);

			/*
			 * Expected PDF service response is generally:
			 *
			 * { "filestoreIds": [ "..." ] }
			 */

			JsonNode filestoreIds = root.path("filestoreIds");

			if (filestoreIds.isArray() && !filestoreIds.isEmpty()) {

				String fileStoreId = filestoreIds.get(0).asText(null);

				if (fileStoreId != null && !fileStoreId.isBlank()) {

					return fileStoreId;
				}
			}

			/*
			 * Fallback in case response contains a single filestoreId.
			 */
			JsonNode filestoreId = root.get("filestoreId");

			if (filestoreId != null && !filestoreId.isNull()) {

				String value = filestoreId.asText(null);

				if (value != null && !value.isBlank()) {
					return value;
				}
			}

			log.warn("filestoreId not found in PDF service response: {}", root);

		} catch (Exception e) {

			log.error("Error while extracting filestoreId from PDF service response", e);
		}

		return null;
	}
}