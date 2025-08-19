package org.upyog.chb.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * This service class handles user-related operations in the Community Hall Booking module.
 * 
 * Purpose:
 * - To fetch and manage user details required for booking and notification processes.
 * - To interact with external user services for retrieving user information.
 * 
 * Dependencies:
 * - ObjectMapper: Used to serialize and deserialize JSON objects for requests and responses.
 * - ServiceRequestRepository: Sends HTTP requests to external user services.
 * - CommunityHallBookingConfiguration: Provides configuration properties for user-related operations.
 * - CommunityHallBookingUtil: Utility class for common operations related to user data.
 * 
 * Features:
 * - Fetches user details based on search criteria such as mobile number or user ID.
 * - Processes and validates user data for use in booking and notification workflows.
 * - Logs user-related operations and errors for debugging and monitoring purposes.
 * 
 * Methods:
 * 1. fetchUserDetails:
 *    - Sends a request to the external user service to fetch user details.
 *    - Processes the response and returns a UserDetailResponse object.
 * 
 * 2. validateUser:
 *    - Validates the user details to ensure they meet the required criteria for booking.
 * 
 * Usage:
 * - This class is automatically managed by Spring and injected wherever user-related
 *   operations are required.
 * - It ensures consistent and reusable logic for managing user data in the module.
 */
@Slf4j
@Service
public class UserService {

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private CommunityHallBookingConfiguration config;

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
		userSearchRequest.put("userType", CommunityHallBookingConstants.CITIZEN);
		userSearchRequest.put("userName", mobileNumber);
		try {

			Object user = serviceRequestRepository.fetchResult(uri, userSearchRequest);
			log.info("User fetched in fetUserUUID method of CHB notfication consumer" + user.toString());
//			if (null != user) {
//				String uuid = JsonPath.read(user, "$.user[0].uuid");
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
	 * @param userSearchRequest
	 * @return serDetailResponse containing the user if present and the responseInfo
	 */
	public UserDetailResponse getUser(UserSearchRequest userSearchRequest) {

		StringBuilder uri = new StringBuilder(config.getUserHost()).append(config.getUserSearchEndpoint());
		UserDetailResponse userDetailResponse = userCall(userSearchRequest, uri);
		return userDetailResponse;
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

				map.put("createdDate", CommunityHallBookingUtil.dateTolong((String) map.get("createdDate"), format1));
				if ((String) map.get("lastModifiedDate") != null)
					map.put("lastModifiedDate", CommunityHallBookingUtil.dateTolong((String) map.get("lastModifiedDate"), format1));
				if ((String) map.get("dob") != null)
					map.put("dob", CommunityHallBookingUtil.dateTolong((String) map.get("dob"), dobFormat));
				if ((String) map.get("pwdExpiryDate") != null)
					map.put("pwdExpiryDate", CommunityHallBookingUtil.dateTolong((String) map.get("pwdExpiryDate"), format1));
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
