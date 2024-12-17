package org.egov.asset.calculator.services;

import java.util.LinkedHashMap;

import org.egov.common.contract.request.RequestInfo;
import org.egov.asset.calculator.config.CalculatorConfig;
import org.egov.asset.calculator.repository.ServiceRequestRepository;
import org.egov.asset.calculator.utils.CalculatorConstants;
import org.egov.asset.calculator.web.models.RequestInfoWrapper;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AssetService {

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private CalculatorConfig config;



	private StringBuilder getBPASearchURL() {
		StringBuilder url = new StringBuilder(config.getAssetHost());
		//url.append(config.getFsmContextPath());
		//url.append(config.getFsmSearchEndpoint());
		url.append("?");
		return url;
	}


}
