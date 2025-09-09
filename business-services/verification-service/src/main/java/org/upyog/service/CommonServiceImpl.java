package org.upyog.service;

import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.config.ModuleConfig;
import org.upyog.constants.VerificationSearchConstants;
import org.upyog.mapper.CommonDetailsMapper;
import org.upyog.mapper.CommonDetailsMapperFactory;
import org.upyog.repository.ServiceRequestRepository;
import org.upyog.web.models.CommonDetails;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import digit.models.coremodels.RequestInfoWrapper;
import digit.models.coremodels.UserDetailResponse;
import lombok.extern.slf4j.Slf4j;
import org.upyog.web.models.ModuleSearchRequest;

@Service
@Slf4j
public class CommonServiceImpl implements CommonService {

	private final Map<String, String> moduleHosts;
	private final Map<String, String> moduleEndpoints;
	private final Map<String, String> moduleUniqueIdParams;
	private final CommonDetailsMapperFactory mapperFactory;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private UserService userService;

	@Autowired
	public CommonServiceImpl(ModuleConfig moduleConfig, CommonDetailsMapperFactory mapperFactory) {

		this.moduleHosts = moduleConfig.getHost();
		this.moduleEndpoints = moduleConfig.getEndpoint();
		this.moduleUniqueIdParams = moduleConfig.getUniqueIdParam();
		this.mapperFactory = mapperFactory;
	}

	@Override
	public CommonDetails getApplicationCommonDetails(ModuleSearchRequest request) {
		RequestInfo requestInfo = request.getRequestInfo();
		String moduleName = request.getModuleSearchCriteria().getModuleName();
		String applicationNumber = request.getModuleSearchCriteria().getApplicationNumber();
		String tenantId = request.getModuleSearchCriteria().getTenantId();
		String host = moduleHosts.get(moduleName);
		if (host == null) {
			throw new IllegalArgumentException("Invalid module name or host not configured: " + moduleName);
		}

		String endpoint = moduleEndpoints.get(moduleName);
		if (endpoint == null) {
			throw new IllegalArgumentException("Invalid module name or endpoint not configured: " + moduleName);
		}

		// Retrieve the unique ID parameter for the given module
		String uniqueIdParam = moduleUniqueIdParams.get(moduleName);
		if (uniqueIdParam == null) {
			throw new IllegalArgumentException("No unique ID parameter configured for module: " + moduleName);
		}

		// Construct the full URL dynamically
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(host).append(endpoint).append("?").append(uniqueIdParam).append("=").append(applicationNumber)
				.append("&tenantId=").append(tenantId);
		RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder()
				.requestInfo(requestInfo.getUserInfo() != null ? requestInfo : getSystemUserDetails()).build();

		log.info("requestInfoWrapper data : " + requestInfoWrapper);
		Object result = null;
		try {
			log.info("urlBuilder : " + urlBuilder);
			result = serviceRequestRepository.fetchResult(urlBuilder, requestInfoWrapper);
			JsonNode jsonNode = objectMapper.valueToTree(result);
			CommonDetailsMapper mapper = mapperFactory.getMapper(moduleName);
			return mapper.mapJsonToCommonDetails(jsonNode);
		} catch (Exception e) {
			throw new CustomException("Error fetching details for module: " + moduleName, "MODULE_API_ERROR");
		}
	}

	/**
	 * Retrieves the system user’s RequestInfo based on a predefined system
	 * username.
	 * 
	 * @return A RequestInfo object containing the system user’s details.
	 * @throws IllegalStateException if the system user is not found.
	 */
	private RequestInfo getSystemUserDetails() {
		UserDetailResponse userDetailResponse = userService.searchByUserName(
				VerificationSearchConstants.INTERNALMICROSERVICEUSER_USERNAME, VerificationSearchConstants.VS_TENANTID);

		if (userDetailResponse == null || userDetailResponse.getUser().isEmpty()) {
			throw new IllegalStateException(
					"SYSTEM user not found for tenant '" + VerificationSearchConstants.VS_TENANTID + "'.");
		}

		RequestInfo systemRequestInfo = RequestInfo.builder().userInfo(userDetailResponse.getUser().get(0)).build();

		log.info("RequestInfo of System User: " + systemRequestInfo);
		return systemRequestInfo;
	}
}
