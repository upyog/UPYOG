package org.egov.garbageservice.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.garbageservice.model.GarbageAccountRequest;
import org.egov.garbageservice.model.UserResponse;
import org.egov.garbageservice.model.UserSearchRequest;
import org.egov.garbageservice.model.UserSearchResponse;
import org.egov.garbageservice.model.contract.CreateUserRequest;
import org.egov.garbageservice.model.contract.OwnerInfo;
import org.egov.garbageservice.model.contract.Role;
import org.egov.garbageservice.model.contract.UserDetailResponse;
import org.egov.garbageservice.repository.ServiceRequestRepository;
import org.egov.garbageservice.util.GrbgConstants;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {

	@Value("${egov.user.context.path}")
	private String userContextPath;

	@Value("${egov.user.create.path}")
	private String userCreateEndpoint;

	@Value("${egov.user.update.path}")
	private String userUpdateEndpoint;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private GrbgConstants grbgConfig;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private ObjectMapper mapper;

	public UserSearchResponse searchUser(String userUuid) {
		StringBuilder url = new StringBuilder(grbgConfig.getUserServiceHostUrl());
		url.append(grbgConfig.getUserSearchEndpoint());

		UserSearchRequest userSearchRequest = UserSearchRequest.builder()
				.requestInfo(RequestInfo.builder().userInfo(User.builder().uuid(userUuid).build()).build())
				.uuid(Collections.singletonList(userUuid)).build();

		UserSearchResponse userSearchResponse = null;
		try {
			userSearchResponse = restTemplate.postForObject(url.toString(), userSearchRequest,
					UserSearchResponse.class);
		} catch (Exception e) {
			log.error("Error occured while user search.", e);
			throw new CustomException("USER SEARCH ERROR",
					"Error occured while user search. Message: " + e.getMessage());
		}

		return userSearchResponse;
	}

	public Map<String, User> searchUser(UserSearchRequest userSearchRequest) {

		StringBuilder url = new StringBuilder(grbgConfig.getUserServiceHostUrl());
		url.append(grbgConfig.getUserSearchEndpoint());

		UserResponse userSearchResponse = null;

		try {
			userSearchResponse = restTemplate.postForObject(url.toString(), userSearchRequest, UserResponse.class);
		} catch (HttpServerErrorException e) {

			LinkedHashMap<?, ?> customException = new Gson().fromJson(e.getResponseBodyAsString(), LinkedHashMap.class);

			String errorMessage = String.format("Message: %s", customException.get("errorMessage"));
			throw new CustomException("ERR_USER_SERVICE_ERROR", errorMessage);
		} catch (Exception e) {
			log.error("Error occured while update user role.", e);
			throw new CustomException("ERR_USER_SERVICE_ERROR",
					"Error occured while update user role. Message: " + e.getMessage());
		}

		Map<String, User> uuidToUserMap = new HashMap<>();
		if (null != userSearchResponse && !CollectionUtils.isEmpty(userSearchResponse.getUser())) {
			userSearchResponse.getUser().forEach(user -> {
				uuidToUserMap.put(user.getUuid(), user);
			});
		}
		return uuidToUserMap;
	}

	public void createUser(GarbageAccountRequest createGarbageRequest) {

		RequestInfo requestInfo = createGarbageRequest.getRequestInfo();
		List<OwnerInfo> owners = new ArrayList<>();
		Role role = getCitizenRole();

		// Build the owners list from the garbage accounts
		owners.addAll(createGarbageRequest.getGarbageAccounts().stream()
				.map(garbageAccount -> OwnerInfo.builder().name(garbageAccount.getName())
						.mobileNumber(garbageAccount.getMobileNumber()).tenantId(garbageAccount.getTenantId()).build())
				.collect(Collectors.toList()));

		owners.forEach(owner -> {
			addUserDefaultFields(role, owner);
			UserDetailResponse userDetailResponse = userExists(owner, requestInfo);
			List<OwnerInfo> existingUsersFromService = userDetailResponse.getUser();

			if (existingUsersFromService.isEmpty()) {
				owner.setUserName(UUID.randomUUID().toString());
				userDetailResponse = createUser(requestInfo, owner);
			} else {
				String mobileNumber = owner.getMobileNumber();
				List<OwnerInfo> existingUserWithMobile = findUserByMobile(existingUsersFromService, mobileNumber);
				if (!existingUserWithMobile.isEmpty()) {
					userDetailResponse = updateExistingUser(requestInfo, role, owner, existingUserWithMobile.get(0));
				} else {
					owner.setUserName(UUID.randomUUID().toString());
					userDetailResponse = createUser(requestInfo, owner);
				}
			}
		});

	}

	private List<OwnerInfo> findUserByMobile(List<OwnerInfo> users, String mobileNumber) {
		return users.stream().filter(user -> mobileNumber.equals(user.getMobileNumber())).collect(Collectors.toList());
	}

	private UserDetailResponse updateExistingUser(RequestInfo requestInfo, Role role, OwnerInfo ownerFromRequest,
			OwnerInfo ownerInfoFromSearch) {

		UserDetailResponse userDetailResponse;

		ownerFromRequest.setId(ownerInfoFromSearch.getId());
		ownerFromRequest.setUuid(ownerInfoFromSearch.getUuid());
		addUserDefaultFields(role, ownerFromRequest);

		StringBuilder uri = new StringBuilder(grbgConfig.getUserServiceHostUrl()).append(userContextPath)
				.append(userUpdateEndpoint);
		userDetailResponse = userCall(new CreateUserRequest(requestInfo, ownerFromRequest), uri);
		if (userDetailResponse.getUser().get(0).getUuid() == null) {
			throw new CustomException("INVALID USER RESPONSE", "The user updated has uuid as null");
		}
		return userDetailResponse;
	}

	private UserDetailResponse createUser(RequestInfo requestInfo, OwnerInfo owner) {
		UserDetailResponse userDetailResponse;
		StringBuilder uri = new StringBuilder(grbgConfig.getUserServiceHostUrl()).append(userContextPath)
				.append(userCreateEndpoint);

		CreateUserRequest userRequest = CreateUserRequest.builder().requestInfo(requestInfo).user(owner).build();

		userDetailResponse = userCall(userRequest, uri);

		if (ObjectUtils.isEmpty(userDetailResponse)) {

			throw new CustomException("INVALID USER RESPONSE",
					"The user create has failed for the mobileNumber : " + owner.getUserName());

		}
		return userDetailResponse;
	}

	private UserDetailResponse userExists(OwnerInfo owner, RequestInfo requestInfo) {

		UserSearchRequest userSearchRequest = getBaseUserSearchRequest(owner.getTenantId(), requestInfo);
		userSearchRequest.setMobileNumber(owner.getMobileNumber());
		userSearchRequest.setUserType(owner.getType());
		userSearchRequest.setName(owner.getName());

		StringBuilder uri = new StringBuilder(grbgConfig.getUserServiceHostUrl())
				.append(grbgConfig.getUserSearchEndpoint());
		return userCall(userSearchRequest, uri);
	}

	private UserDetailResponse userCall(Object userRequest, StringBuilder url) {

		String dobFormat = null;
		if (url.indexOf(grbgConfig.getUserServiceHostUrl()) != -1 || url.indexOf(userUpdateEndpoint) != -1)
			dobFormat = "yyyy-MM-dd";
		else if (url.indexOf(userCreateEndpoint) != -1)
			dobFormat = "dd/MM/yyyy";
		try {
			Optional<Object> response = serviceRequestRepository.fetchResult(url, userRequest);

			if (response.isPresent()) {
				LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.get();
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

	private void parseResponse(LinkedHashMap<String, Object> responeMap, String dobFormat) {

		List<LinkedHashMap<String, Object>> users = (List<LinkedHashMap<String, Object>>) responeMap.get("user");
		String format1 = "dd-MM-yyyy HH:mm:ss";

		if (null != users) {

			users.forEach(map -> {

				map.put("createdDate", dateTolong((String) map.get("createdDate"), format1));
				if ((String) map.get("lastModifiedDate") != null)
					map.put("lastModifiedDate", dateTolong((String) map.get("lastModifiedDate"), format1));
				if ((String) map.get("dob") != null)
					map.put("dob", dateTolong((String) map.get("dob"), dobFormat));
				if ((String) map.get("pwdExpiryDate") != null)
					map.put("pwdExpiryDate", dateTolong((String) map.get("pwdExpiryDate"), format1));
			});
		}
	}

	private Long dateTolong(String date, String format) {
		SimpleDateFormat f = new SimpleDateFormat(format);
		Date d = null;
		try {
			d = f.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return d.getTime();
	}

	public UserSearchRequest getBaseUserSearchRequest(String tenantId, RequestInfo requestInfo) {

		return UserSearchRequest.builder().requestInfo(requestInfo).userType("CITIZEN").tenantId(tenantId).active(true)
				.build();
	}

	private void addUserDefaultFields(Role role, OwnerInfo owner) {

		owner.setActive(true);
		owner.setRoles(Collections.singletonList(role));
		owner.setType("CITIZEN");
		owner.setCreatedDate(null);
		owner.setCreatedBy(null);
		owner.setLastModifiedDate(null);
		owner.setLastModifiedBy(null);
	}

	private Role getCitizenRole() {

		return Role.builder().code("CITIZEN").name("Citizen").build();
	}

}
