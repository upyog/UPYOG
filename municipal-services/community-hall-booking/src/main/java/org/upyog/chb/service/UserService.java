package org.upyog.chb.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.repository.ServiceRequestRepository;
import org.upyog.chb.util.CommunityHallBookingUtil;
import org.upyog.chb.web.models.UserSearchRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import digit.models.coremodels.UserDetailResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

	private static final String LAST_MODIFIED_DATE = "lastModifiedDate";
	private static final String PWD_EXPIRY_DATE = "pwdExpiryDate";

	private final ObjectMapper mapper;
	private final ServiceRequestRepository serviceRequestRepository;
	private final CommunityHallBookingConfiguration config;

	public UserService(ObjectMapper mapper, ServiceRequestRepository serviceRequestRepository,
			CommunityHallBookingConfiguration config) {
		this.mapper = mapper;
		this.serviceRequestRepository = serviceRequestRepository;
		this.config = config;
	}

	public Map<String, String> fetchUserUUIDs(String mobileNumber, RequestInfo requestInfo, String tenantId) {
		Map<String, String> mapOfPhoneNoAndUUIDs = new HashMap<>();
		StringBuilder uri = new StringBuilder();
		uri.append(config.getUserHost()).append(config.getUserSearchEndpoint());
		Map<String, Object> userSearchRequest = new HashMap<>();
		userSearchRequest.put("RequestInfo", requestInfo);
		userSearchRequest.put("tenantId", tenantId);
		userSearchRequest.put("userType", CommunityHallBookingConstants.CITIZEN);
		userSearchRequest.put("userName", mobileNumber);
		try {

			Object user = serviceRequestRepository.fetchResult(uri, userSearchRequest);
			log.info("User fetched in fetUserUUID method of CHB notfication consumer" + user.toString());
			if (user != null) {
				String uuid = JsonPath.read(user, "$.user[0].uuid");
				mapOfPhoneNoAndUUIDs.put(mobileNumber, uuid);
				log.info("mapOfPhoneNoAndUUIDs : " + mapOfPhoneNoAndUUIDs);
			}
		} catch (Exception e) {
			log.error("Exception while fetching user for username - " + mobileNumber);
			log.error("Exception trace: ", e);
		}

		return mapOfPhoneNoAndUUIDs;
	}

	public UserDetailResponse getUser(UserSearchRequest userSearchRequest) {
		StringBuilder uri = new StringBuilder(config.getUserHost()).append(config.getUserSearchEndpoint());
		return userCall(userSearchRequest, uri);
	}

	@SuppressWarnings("unchecked")
	private UserDetailResponse userCall(Object userRequest, StringBuilder url) {
		String dobFormat = determineDobFormat(url.toString());

		try {
			Object response = serviceRequestRepository.fetchResult(url, userRequest);

			if (response instanceof LinkedHashMap) {
				LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response;

				parseResponse(responseMap, dobFormat);

				return mapper.convertValue(responseMap, UserDetailResponse.class);
			}
			return new UserDetailResponse();
		} catch (IllegalArgumentException e) {
			throw new CustomException("IllegalArgumentException",
					"ObjectMapper was not able to convert the value in userCall");
		}
	}

	private String determineDobFormat(String url) {
		if (url.contains(config.getUserSearchEndpoint()) || url.contains(config.getUserSearchEndpoint())) {
			return "yyyy-MM-dd";
		} else if (url.contains(config.getUserSearchEndpoint())) {
			return "dd/MM/yyyy";
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private void parseResponse(LinkedHashMap<String, Object> responeMap, String dobFormat) {

		List<LinkedHashMap<String, Object>> users = (List<LinkedHashMap<String, Object>>) responeMap.get("user");
		String format1 = "dd-MM-yyyy HH:mm:ss";

		if (null != users) {

			users.forEach(map -> {

				map.put("createdDate", CommunityHallBookingUtil.dateTolong((String) map.get("createdDate"), format1));
				if ((String) map.get(LAST_MODIFIED_DATE) != null)
					map.put(LAST_MODIFIED_DATE, CommunityHallBookingUtil.dateTolong((String) map.get(LAST_MODIFIED_DATE), format1));
				if ((String) map.get("dob") != null)
					map.put("dob", CommunityHallBookingUtil.dateTolong((String) map.get("dob"), dobFormat));
				if ((String) map.get(PWD_EXPIRY_DATE) != null)
					map.put(PWD_EXPIRY_DATE, CommunityHallBookingUtil.dateTolong((String) map.get(PWD_EXPIRY_DATE), format1));
			});
		}
	}

	public UserDetailResponse searchByUserName(String userName, String tenantId) {
		UserSearchRequest userSearchRequest = new UserSearchRequest();
		userSearchRequest.setUserType(config.getInternalMicroserviceUserType());
		userSearchRequest.setUserName(userName);
		userSearchRequest.setTenantId(tenantId);
		return getUser(userSearchRequest);
	}

}
