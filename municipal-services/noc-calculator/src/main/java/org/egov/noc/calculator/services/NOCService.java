package org.egov.noc.calculator.services;

import java.util.LinkedHashMap;
import org.egov.common.contract.request.RequestInfo;
import org.egov.noc.calculator.config.NOCCalculatorConfig;
import org.egov.noc.calculator.repository.ServiceRequestRepository;
import org.egov.noc.calculator.utils.NOCConstants;
import org.egov.noc.calculator.web.models.NOCResponse;
import org.egov.noc.calculator.web.models.Noc;
import org.egov.noc.calculator.web.models.RequestInfoWrapper;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class NOCService {

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private NOCCalculatorConfig config;

	public Noc getNOC(RequestInfo requestInfo, String tenantId, String applicationNo) {
		StringBuilder url = getNOCSearchURL();
		url.append("tenantId=");
		url.append(tenantId);
		
		url.append("&");
		url.append("applicationNo=");
		url.append(applicationNo);
		LinkedHashMap responseMap = null;
		responseMap = (LinkedHashMap) serviceRequestRepository.fetchResult(url, new RequestInfoWrapper(requestInfo));

		NOCResponse nocResponse = null;

		try {
			nocResponse = mapper.convertValue(responseMap, NOCResponse.class);
		} catch (IllegalArgumentException e) {
			throw new CustomException(NOCConstants.PARSING_ERROR, "Error while parsing response of TradeLicense Search");
		}

		return nocResponse.getNOC().size() > 0 ? nocResponse.getNOC().get(0) : null ;
	}

	private StringBuilder getNOCSearchURL() {
		// TODO Auto-generated method stub
		StringBuilder url = new StringBuilder(config.getNocHost());
		url.append(config.getNocContextPath());
		url.append(config.getNocSearchEndpoint());
		url.append("?");
		return url;
	}
}
