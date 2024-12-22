package org.upyog.service;

import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.upyog.config.ModuleConfig;
import org.upyog.mapper.CommonDetailsMapper;
import org.upyog.mapper.CommonDetailsMapperFactory;
import org.upyog.repository.ServiceRequestRepository;
import org.upyog.web.models.CommonDetails;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import digit.models.coremodels.RequestInfoWrapper;

@Service
public class CommonServiceImpl implements CommonService {

	private final Map<String, String> moduleEndpoints;
	private final Map<String, String> moduleUniqueIdParams;
	private final CommonDetailsMapperFactory mapperFactory;

	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	public CommonServiceImpl(ModuleConfig moduleConfig, CommonDetailsMapperFactory mapperFactory) {
		this.moduleEndpoints = moduleConfig.getEndpoints();
		this.moduleUniqueIdParams = moduleConfig.getUniqueIdParams();
		this.mapperFactory = mapperFactory;
	}

	@Override
	public CommonDetails getApplicationCommonDetails(RequestInfo requestInfo, String moduleName,
			String applicationNumber) {
		String endpoint = moduleEndpoints.get(moduleName);
		if (endpoint == null) {
			throw new IllegalArgumentException("Invalid module name: " + moduleName);
		}

		String uniqueIdParam = moduleUniqueIdParams.get(moduleName);
		if (uniqueIdParam == null) {
			throw new IllegalArgumentException("No unique ID parameter configured for module: " + moduleName);
		}

		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(endpoint).append("?").append(uniqueIdParam).append("=").append(applicationNumber);
		RequestInfoWrapper requestInfoWrapper= RequestInfoWrapper.builder().requestInfo(requestInfo).build();
		Object result = null;
		try {
			result = serviceRequestRepository.fetchResult(urlBuilder, requestInfoWrapper);
			JsonNode jsonNode = objectMapper.valueToTree(result);
			CommonDetailsMapper mapper = mapperFactory.getMapper(moduleName);
			return mapper.mapJsonToCommonDetails(jsonNode);
		} catch (Exception e) {
			throw new CustomException("Error fetching details for module: " + moduleName, "MODULE_API_ERROR");
		}
	}
}
