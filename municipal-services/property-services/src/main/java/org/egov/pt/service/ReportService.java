package org.egov.pt.service;

import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.models.report.PDFRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ReportService {

	@Autowired
	private PropertyConfiguration config;

	@Autowired
	private RestTemplate restTemplate;

	public ResponseEntity<Resource> createNoSavePDF(PDFRequest pdfRequest) {

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		HttpEntity<PDFRequest> requestEntity = new HttpEntity<>(pdfRequest, headers);

		ResponseEntity<Resource> responseEntity = restTemplate.exchange(
				config.getReportHost().concat(config.getReportCreateEndPoint()), HttpMethod.POST, requestEntity,
				Resource.class);

		return responseEntity;
	}

}
