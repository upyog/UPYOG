package org.upyog.service;

import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;
import org.upyog.config.MainConfiguration;
import org.upyog.config.ModuleConfig;
import org.upyog.mapper.CommonDetailsMapper;
import org.upyog.mapper.CommonDetailsMapperFactory;
import org.upyog.repository.ServiceRequestRepository;
import org.upyog.web.models.CommonDetails;
import org.upyog.web.models.ModuleSearchRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import digit.models.coremodels.RequestInfoWrapper;
import digit.models.coremodels.UserDetailResponse;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommonServiceImpl implements CommonService {

	private final Map<String, String> moduleHosts;
	private final Map<String, String> moduleEndpoints;
	private final Map<String, String> moduleUniqueIdParams;
	private final CommonDetailsMapperFactory mapperFactory;
	private final ObjectMapper objectMapper;
	private final ServiceRequestRepository serviceRequestRepository;
	private final UserService userService;
	private final MainConfiguration mainConfiguration;

	public CommonServiceImpl(ModuleConfig moduleConfig, CommonDetailsMapperFactory mapperFactory,
			ObjectMapper objectMapper, ServiceRequestRepository serviceRequestRepository,
			UserService userService, MainConfiguration mainConfiguration) {

		this.moduleHosts = moduleConfig.getHost();
		this.moduleEndpoints = moduleConfig.getEndpoint();
		this.moduleUniqueIdParams = moduleConfig.getUniqueIdParam();
		this.mapperFactory = mapperFactory;
		this.objectMapper = objectMapper;
		this.serviceRequestRepository = serviceRequestRepository;
		this.userService = userService;
		this.mainConfiguration = mainConfiguration;
	}

	@Override
	public CommonDetails getApplicationCommonDetails(ModuleSearchRequest request) {
		RequestInfo requestInfo = request.getRequestInfo();
		String moduleName = request.getModuleSearchCriteria().getModuleName();
		String applicationNumber = request.getModuleSearchCriteria().getApplicationNumber();
		String tenantId = request.getModuleSearchCriteria().getTenantId();

		log.info("Module: {}, Host: {}, Endpoint: {}", moduleName, moduleHosts.get(moduleName), moduleEndpoints.get(moduleName));

		String host = moduleHosts.get(moduleName);
		if (host == null) {
			throw new IllegalArgumentException("Invalid module name or host not configured: " + moduleName);
		}

		String endpoint = moduleEndpoints.get(moduleName);
		if (endpoint == null) {
			throw new IllegalArgumentException("Invalid module name or endpoint not configured: " + moduleName);
		}

		String uniqueIdParam = moduleUniqueIdParams.get(moduleName);
		if (uniqueIdParam == null) {
			throw new IllegalArgumentException("No unique ID parameter configured for module: " + moduleName);
		}

		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(host).append(endpoint).append("?").append(uniqueIdParam).append("=").append(applicationNumber)
				.append("&tenantId=").append(tenantId);
		log.info("Final URL being called: {}", urlBuilder.toString());

		RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder()
				.requestInfo(requestInfo.getUserInfo() != null ? requestInfo : getSystemUserDetails()).build();

		log.info("requestInfoWrapper data : " + requestInfoWrapper);
		Object result = null;
		try {
			log.info("urlBuilder : " + urlBuilder);
			result = serviceRequestRepository.fetchResult(urlBuilder, requestInfoWrapper);
			log.info("API call successful for URL: {}", urlBuilder.toString());
			JsonNode jsonNode = objectMapper.valueToTree(result);
			CommonDetailsMapper mapper = mapperFactory.getMapper(moduleName);
			CommonDetails commonDetails = mapper.mapJsonToCommonDetails(jsonNode);
			if(commonDetails.getStatus().equalsIgnoreCase("Pending")) {
				return null;
			}
			else{
				return commonDetails;
			}
			
		} catch (Exception e) {
			log.error("API call failed for URL: {}, Error: {}", urlBuilder.toString(), e.getMessage());
			throw new CustomException("Error fetching details for module: " + moduleName, "MODULE_API_ERROR");
		}
	}

	/**
	 * Retrieves the system user's RequestInfo based on a predefined system
	 * username.
	 *
	 * @return A RequestInfo object containing the system user's details.
	 * @throws IllegalStateException if the system user is not found.
	 */
	private RequestInfo getSystemUserDetails() {
		UserDetailResponse userDetailResponse = userService.searchByUserName(
				mainConfiguration.getInternalMicroserviceUserName(), mainConfiguration.getStateLevelTenantId());

		if (userDetailResponse == null || userDetailResponse.getUser().isEmpty()) {
			throw new IllegalStateException(
					"SYSTEM user not found for tenant '" + mainConfiguration.getStateLevelTenantId() + "'.");
		}

		RequestInfo systemRequestInfo = RequestInfo.builder().userInfo(userDetailResponse.getUser().get(0)).build();

		log.info("RequestInfo of system User: " + systemRequestInfo);
		return systemRequestInfo;
	}
}
