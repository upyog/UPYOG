package org.upyog.sv.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;
import org.upyog.sv.config.StreetVendingConfiguration;
import org.upyog.sv.constants.StreetVendingConstants;
import org.upyog.sv.repository.ServiceRequestRepository;
import org.upyog.sv.util.StreetVendingUtil;
import org.upyog.sv.web.models.UserSearchRequest;

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
	private final StreetVendingConfiguration config;

	public UserService(ObjectMapper mapper, ServiceRequestRepository serviceRequestRepository,
			StreetVendingConfiguration config) {
		this.mapper = mapper;
		this.serviceRequestRepository = serviceRequestRepository;
		this.config = config;
	}

	/**
	 * Fetches UUIDs of CITIZEN based on the phone number.
	 *
	 * @param mobileNumber - Mobile Numbers
	 * @param requestInfo  - Request Information
	 * @param tenantId     - Tenant Id
	 * @return Returns List of MobileNumbers and UUIDs
	 */
	public Map<String, String> fetchUserUUIDs(String mobileNumber, RequestInfo requestInfo, String tenantId) {
		Map<String, String> mapOfPhoneNoAndUUIDs = new HashMap<>();
		StringBuilder uri = new StringBuilder();
		uri.append(config.getUserHost()).append(config.getUserSearchEndpoint());
		Map<String, Object> userSearchRequest = new HashMap<>();
		userSearchRequest.put("RequestInfo", requestInfo);
		userSearchRequest.put("tenantId", tenantId);
		userSearchRequest.put("userType", StreetVendingConstants.CITIZEN);
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

	/**
	 * Returns user using user search based on ApplicationCriteria(user
	 * name,mobileNumber,userName)
	 * 
	 * @param userSearchRequest user search request
	 * @return UserDetailResponse containing the user if present and the responseInfo
	 */
	public UserDetailResponse getUser(UserSearchRequest userSearchRequest) {
		StringBuilder uri = new StringBuilder(config.getUserHost()).append(config.getUserSearchEndpoint());
		return userCall(userSearchRequest, uri);
	}

	/**
	 * Returns UserDetailResponse by calling the user service with the given URI and
	 * object
	 * 
	 * @param userRequest Request object for the user service
	 * @param url         The address of the endpoint
	 * @return Response from the user service parsed as UserDetailResponse
	 */
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

	/**
	 * Determines the date format based on the URL endpoint
	 * 
	 * @param url The URL of the endpoint
	 * @return The appropriate date format
	 */
	private String determineDobFormat(String url) {
		if (url.contains(config.getUserSearchEndpoint()) || url.contains(config.getUserSearchEndpoint())) {
			return "yyyy-MM-dd";
		} else if (url.contains(config.getUserSearchEndpoint())) {
			return "dd/MM/yyyy";
		}
		return null;
	}

	/**
	 * Parses date formats to long for all users in responseMap
	 * 
	 * @param responeMap LinkedHashMap got from user api response
	 * @param dobFormat  dob format (required because dob is returned in different
	 *                   format's in search and create response in user service)
	 */
	@SuppressWarnings("unchecked")
	private void parseResponse(LinkedHashMap<String, Object> responeMap, String dobFormat) {

		List<LinkedHashMap<String, Object>> users = (List<LinkedHashMap<String, Object>>) responeMap.get("user");
		String format1 = "dd-MM-yyyy HH:mm:ss";

		if (null != users) {

			users.forEach(map -> {

				map.put("createdDate", StreetVendingUtil.dateTolong((String) map.get("createdDate"), format1));
				if ((String) map.get(LAST_MODIFIED_DATE) != null)
					map.put(LAST_MODIFIED_DATE, StreetVendingUtil.dateTolong((String) map.get(LAST_MODIFIED_DATE), format1));
				if ((String) map.get("dob") != null)
					map.put("dob", StreetVendingUtil.dateTolong((String) map.get("dob"), dobFormat));
				if ((String) map.get(PWD_EXPIRY_DATE) != null)
					map.put(PWD_EXPIRY_DATE, StreetVendingUtil.dateTolong((String) map.get(PWD_EXPIRY_DATE), format1));
			});
		}
	}

	/**
	 * provides a user search request with basic mandatory parameters
	 * 
	 * @param tenantId tenant id
	 * @param requestInfo request info
	 * @return user search request
	 */
	public UserSearchRequest getBaseUserSearchRequest(String tenantId, RequestInfo requestInfo) {

		return UserSearchRequest.builder().requestInfo(requestInfo).userType(StreetVendingConstants.CITIZEN).tenantId(tenantId).active(true)
				.build();
	}

	public UserDetailResponse searchByUserName(String userName, String tenantId) {
		UserSearchRequest userSearchRequest = new UserSearchRequest();
		userSearchRequest.setUserType(StreetVendingConstants.CITIZEN);
		userSearchRequest.setUserName(userName);
		userSearchRequest.setTenantId(tenantId);
		return getUser(userSearchRequest);
	}

}
