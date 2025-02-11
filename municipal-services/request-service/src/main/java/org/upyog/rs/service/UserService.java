package org.upyog.rs.service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.upyog.rs.config.RequestServiceConfiguration;
import org.upyog.rs.repository.ServiceRequestRepository;
import org.upyog.rs.util.RequestServiceUtil;
import org.upyog.rs.web.models.ApplicantDetail;
import org.upyog.rs.web.models.WaterTankerBookingDetail;
import org.upyog.rs.web.models.WaterTankerBookingRequest;
import org.upyog.rs.web.models.user.CreateUserRequest;
import org.upyog.rs.web.models.user.User;
import org.upyog.rs.web.models.user.UserDetailResponse;
import org.upyog.rs.web.models.user.UserSearchRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserService {

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private RequestServiceConfiguration requestConfig;

	public String createUser(WaterTankerBookingRequest bookingRequest) {

		WaterTankerBookingDetail bookingDetail = bookingRequest.getWaterTankerBookingDetail();
		RequestInfo requestInfo = bookingRequest.getRequestInfo();
		ApplicantDetail applicantDetail = bookingDetail.getApplicantDetail();

		if (applicantDetail.getMobileNumber().equals(requestInfo.getUserInfo().getMobileNumber())) {
			return requestInfo.getUserInfo().getUuid();// return uuid which is present in user info under requestinfo
		}

		UserDetailResponse userDetailResponse = userExists(applicantDetail, requestInfo, bookingDetail.getTenantId());
		List<User> existingUsersFromService = userDetailResponse.getUser();
		if (CollectionUtils.isEmpty(existingUsersFromService)) {

			userDetailResponse = createUser(requestInfo, applicantDetail, bookingDetail.getTenantId());
			return userDetailResponse.getUser().get(0).getUuid();// return user uuid which is newly created
		}
		return userDetailResponse.getUser().get(0).getUuid();// return uuid of which user already exists
	}

	private UserDetailResponse createUser(RequestInfo requestInfo, ApplicantDetail applicant, String tenantId) {
		UserDetailResponse userDetailResponse;
		StringBuilder uri = new StringBuilder(requestConfig.getUserHost()).append(requestConfig.getUserContextPath())
				.append(requestConfig.getUserCreateEndpoint());
		Role role = getCitizenRole();
		User user = convertApplicantToUserRequest(applicant, role, tenantId);
		CreateUserRequest userRequest = CreateUserRequest.builder().requestInfo(requestInfo).user(user).build();

		userDetailResponse = userCall(userRequest, uri);

		if (ObjectUtils.isEmpty(userDetailResponse)) {

			throw new CustomException("INVALID USER RESPONSE",
					"The user create has failed for the mobileNumber : " + applicant.getMobileNumber());

		}
		return userDetailResponse;
	}

	private User convertApplicantToUserRequest(ApplicantDetail applicant, Role role, String tenantId) {
		if (applicant == null) {
			return null;
		}

		User userRequest = new User();
		userRequest.setName(applicant.getName());
		userRequest.setUserName(applicant.getMobileNumber()); // Username will be mobile number
		userRequest.setMobileNumber(applicant.getMobileNumber());
		userRequest.setAlternatemobilenumber(applicant.getAlternateNumber());
		userRequest.setEmailId(applicant.getEmailId());
		userRequest.setUuid(UUID.randomUUID().toString()); // System-generated UUID
		userRequest.setActive(true);
		userRequest.setTenantId(tenantId);
		userRequest.setRoles(Collections.singletonList(role));
		userRequest.setType("CITIZEN");
		userRequest.setCreatedDate(null);
		userRequest.setCreatedBy(null);
		userRequest.setLastModifiedDate(null);
		userRequest.setLastModifiedBy(null);
		return userRequest;
	}

	private Role getCitizenRole() {

		return Role.builder().code("CITIZEN").name("Citizen").build();
	}

	/**
	 * Searches if the applicant is already created in user registry with the mobile
	 * number entered. Search is based on name of owner, uuid and mobileNumber
	 * 
	 * @param owner       Owner which is to be searched
	 * @param requestInfo RequestInfo from the propertyRequest
	 * @return UserDetailResponse containing the user if present and the
	 *         responseInfo
	 */
	private UserDetailResponse userExists(ApplicantDetail applicant, RequestInfo requestInfo, String tenantId) {

		UserSearchRequest userSearchRequest = getBaseUserSearchRequest(tenantId, requestInfo);
		userSearchRequest.setMobileNumber(applicant.getMobileNumber());
		userSearchRequest.setUserType("CITIZEN");
		userSearchRequest.setUserName(applicant.getMobileNumber());

		StringBuilder uri = new StringBuilder(requestConfig.getUserHost())
				.append(requestConfig.getUserSearchEndpoint());
		return userCall(userSearchRequest, uri);
	}

	/**
	 * Returns user using user search based on propertyCriteria(owner
	 * name,mobileNumber,userName)
	 * 
	 * @param userSearchRequest
	 * @return serDetailResponse containing the user if present and the responseInfo
	 */
	public UserDetailResponse getUser(UserSearchRequest userSearchRequest) {

		StringBuilder uri = new StringBuilder(requestConfig.getUserHost())
				.append(requestConfig.getUserSearchEndpoint());
		UserDetailResponse userDetailResponse = userCall(userSearchRequest, uri);
		return userDetailResponse;
	}

	/**
	 * Returns UserDetailResponse by calling user service with given uri and object
	 * 
	 * @param userRequest Request object for user service
	 * @param url         The address of the endpoint
	 * @return Response from user service as parsed as userDetailResponse
	 */
	@SuppressWarnings("unchecked")
	private UserDetailResponse userCall(Object userRequest, StringBuilder url) {

		String dobFormat = null;
		if (url.indexOf(requestConfig.getUserSearchEndpoint()) != -1
				|| url.indexOf(requestConfig.getUserUpdateEndpoint()) != -1)
			dobFormat = "yyyy-MM-dd";
		else if (url.indexOf(requestConfig.getUserCreateEndpoint()) != -1)
			dobFormat = "dd/MM/yyyy";
		try {
			Object response = serviceRequestRepository.fetchResult(url, userRequest);

			if (response != null) {
				LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response;
				parseResponse(responseMap, dobFormat);
				UserDetailResponse userDetailResponse = mapper.convertValue(responseMap, UserDetailResponse.class);
				return userDetailResponse;
			} else {
				return new UserDetailResponse();
			}
		}
		// Which Exception to throw?
		catch (IllegalArgumentException e) {
			throw new CustomException("IllegalArgumentException", "ObjectMapper not able to convertValue in userCall");
		}
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

				map.put("createdDate", RequestServiceUtil.dateTolong((String) map.get("createdDate"), format1));
				if ((String) map.get("lastModifiedDate") != null)
					map.put("lastModifiedDate",
							RequestServiceUtil.dateTolong((String) map.get("lastModifiedDate"), format1));
				if ((String) map.get("dob") != null)
					map.put("dob", RequestServiceUtil.dateTolong((String) map.get("dob"), dobFormat));
				if ((String) map.get("pwdExpiryDate") != null)
					map.put("pwdExpiryDate", RequestServiceUtil.dateTolong((String) map.get("pwdExpiryDate"), format1));
			});
		}
	}

	/**
	 * provides a user search request with basic mandatory parameters
	 * 
	 * @param tenantId
	 * @param requestInfo
	 * @return
	 */
	public UserSearchRequest getBaseUserSearchRequest(String tenantId, RequestInfo requestInfo) {

		return UserSearchRequest.builder().requestInfo(requestInfo).userType("CITIZEN").tenantId(tenantId).active(true)
				.build();
	}

}
