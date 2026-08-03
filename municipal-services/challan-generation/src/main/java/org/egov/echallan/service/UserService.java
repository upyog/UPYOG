package org.egov.echallan.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;

import org.egov.echallan.config.ChallanConfiguration;
import org.egov.echallan.model.Challan;
import org.egov.echallan.model.ChallanRequest;
import org.egov.echallan.model.SearchCriteria;
import org.egov.echallan.model.UserInfo;
import org.egov.echallan.repository.ServiceRequestRepository;
import org.egov.echallan.web.models.user.*;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class UserService {

	private static final String LAST_MODIFIED_DATE = "lastModifiedDate";
	private static final String PWD_EXPIRY_DATE = "pwdExpiryDate";

	private final ChallanConfiguration config;
	private final ServiceRequestRepository serviceRequestRepository;
	private final ObjectMapper mapper;

	@Value("${egov.user.host}")
	private String userHost;

	@Value("${egov.user.context.path}")
	private String userContextPath;

	@Value("${egov.user.search.path}")
	private String userSearchEndpoint;

	@Value("${egov.user.create.path}")
	private String userCreateEndpoint;

	@Value("${egov.user.update.path}")
	private String userUpdateEndpoint;

	@Autowired
	public UserService(ChallanConfiguration config, ServiceRequestRepository serviceRequestRepository,
			ObjectMapper mapper) {
		this.config = config;
		this.serviceRequestRepository = serviceRequestRepository;
		this.mapper = mapper;
	}

	/**
	 * Call search in user service based on ownerids from criteria
	 *
	 * @param criteria
	 *            The search criteria containing the ownerids
	 * @param requestInfo
	 *            The requestInfo of the request
	 * @return Search response from user service based on ownerIds
	 */
	public UserDetailResponse getUser(SearchCriteria criteria, RequestInfo requestInfo) {
		UserSearchRequest userSearchRequest = getUserSearchRequest(criteria, requestInfo);
		StringBuilder uri = new StringBuilder(config.getUserHost()).append(config.getUserSearchEndpoint());
		return userCall(userSearchRequest, uri);
	}

	/**
	 * Creates userSearchRequest from nocSearchCriteria
	 *
	 * @param criteria
	 *            The nocSearch criteria
	 * @param requestInfo
	 *            The requestInfo of the request
	 * @return The UserSearchRequest based on ownerIds
	 */
	private UserSearchRequest getUserSearchRequest(SearchCriteria criteria, RequestInfo requestInfo) {
		UserSearchRequest userSearchRequest = new UserSearchRequest();
		userSearchRequest.setRequestInfo(requestInfo);
		userSearchRequest.setTenantId(criteria.getTenantId().split("\\.")[0]);
		userSearchRequest.setMobileNumber(criteria.getMobileNumber());
		userSearchRequest.setActive(true);
		if (!CollectionUtils.isEmpty(criteria.getUserIds()))
			userSearchRequest.setUuid(criteria.getUserIds());
		return userSearchRequest;
	}

	/**
	 * Returns UserDetailResponse by calling user service with given uri and
	 * object
	 *
	 * @param userRequest
	 *            Request object for user service
	 * @param uri
	 *            The address of the end point
	 * @return Response from user service as parsed as userDetailResponse
	 */
	@SuppressWarnings("rawtypes")
	UserDetailResponse userCall(Object userRequest, StringBuilder uri) {
		String dobFormat = null;
		log.info(uri.toString());
		log.info(config.getUserSearchEndpoint());
		if (uri.toString().contains(config.getUserSearchEndpoint()))
			dobFormat = "yyyy-MM-dd";
		try {
			LinkedHashMap responseMap = (LinkedHashMap) serviceRequestRepository.fetchResult(uri, userRequest);
			parseResponse(responseMap, dobFormat);
			return mapper.convertValue(responseMap, UserDetailResponse.class);
		} catch (IllegalArgumentException e) {
			log.error("ObjectMapper not able to convertValue in userCall: {}", e.getMessage());
			return new UserDetailResponse(null, new ArrayList<>());
		} catch (Exception e) {
			log.error("Error calling user service: {}", e.getMessage());
			return new UserDetailResponse(null, new ArrayList<>());
		}
	}

	/**
	 * Parses date formats to long for all users in responseMap
	 *
	 * @param responeMap
	 *            LinkedHashMap got from user api response
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void parseResponse(LinkedHashMap responeMap, String dobFormat) {
		if (responeMap.containsKey("Errors") || responeMap.containsKey("error")) {
			log.warn("User service returned error response, skipping user enrichment");
			return;
		}

		List<LinkedHashMap> users = (List<LinkedHashMap>) responeMap.get("user");
		String format1 = "dd-MM-yyyy HH:mm:ss";
		if (users != null) {
			users.forEach(map -> {
				map.put("createdDate", dateTolong((String) map.get("createdDate"), format1));
				if ((String) map.get(LAST_MODIFIED_DATE) != null)
					map.put(LAST_MODIFIED_DATE, dateTolong((String) map.get(LAST_MODIFIED_DATE), format1));
				if ((String) map.get("dob") != null)
					map.put("dob", dateTolong((String) map.get("dob"), dobFormat));
				if ((String) map.get(PWD_EXPIRY_DATE) != null)
					map.put(PWD_EXPIRY_DATE, dateTolong((String) map.get(PWD_EXPIRY_DATE), format1));
			});
		}
	}

	/**
	 * Converts date to long
	 *
	 * @param date
	 *            date to be parsed
	 * @param format
	 *            Format of the date
	 * @return Long value of date
	 */
	private Long dateTolong(String date, String format) {
		log.info(format);
		log.info(date);
		SimpleDateFormat f = new SimpleDateFormat(format);
		log.info(f.toString());

		Date d = null;
		try {

			d = f.parse(date);
			log.info(d.toString());
			log.info(d.toString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return d.getTime();
	}
	private Role getCitizenRole(String tenantId){
		Role role = new Role();
		role.setCode("CITIZEN");
		role.setName("Citizen");
		role.setTenantId(getStateLevelTenant(tenantId));
		return role;
	}
	private String getStateLevelTenant(String tenantId){
		return tenantId.split("\\.")[0];
	}

	private void addUserDefaultFields(String tenantId, Role role, User owner){
		owner.setActive(true);
		owner.setTenantId(tenantId.split("\\.")[0]);
		owner.setRoles(Collections.singletonList(role));
		owner.setType("CITIZEN");
		owner.setCreatedDate(null);
		owner.setCreatedBy(null);
		owner.setLastModifiedDate(null);
		owner.setLastModifiedBy(null );
	}

	private UserDetailResponse userExists(User owner,RequestInfo requestInfo){
		UserSearchRequest userSearchRequest =new UserSearchRequest();
		userSearchRequest.setTenantId(owner.getTenantId());
		userSearchRequest.setRequestInfo(requestInfo);
		userSearchRequest.setActive(true);
		userSearchRequest.setUserType(owner.getType());
		if(StringUtils.isNotBlank(owner.getMobileNumber())) {
			userSearchRequest.setMobileNumber(owner.getMobileNumber());
			if (StringUtils.isNotBlank(owner.getUserName()))
				userSearchRequest.setUserName(owner.getUserName());
			else
				userSearchRequest.setUserName(owner.getMobileNumber());

		}
		if(StringUtils.isNotBlank(owner.getUuid()))
			userSearchRequest.setUuid(Arrays.asList(owner.getUuid()));
		StringBuilder uri = new StringBuilder(userHost).append(userSearchEndpoint);
		return userCall(userSearchRequest,uri);
	}


	private void setUserName(User owner){
		String username;
		if(StringUtils.isNotBlank(owner.getMobileNumber()))
			username = owner.getMobileNumber();
		else
			username = UUID.randomUUID().toString();



		owner.setUserName(username);

	}

	private void setOwnerFields(User owner, UserDetailResponse userResponse,RequestInfo requestInfo){
		owner.setUuid(userResponse.getUser().get(0).getUuid());
		owner.setId(userResponse.getUser().get(0).getId());
		owner.setUserName((userResponse.getUser().get(0).getUserName()));
		owner.setCreatedBy(requestInfo.getUserInfo().getUuid());
		owner.setLastModifiedBy(requestInfo.getUserInfo().getUuid());
		owner.setCreatedDate(System.currentTimeMillis());
		owner.setLastModifiedDate(System.currentTimeMillis());
		owner.setActive(userResponse.getUser().get(0).getActive());

		if (owner instanceof UserInfo userInfo) {
			if (userResponse.getUser().get(0).getRoles() != null && !userResponse.getUser().get(0).getRoles().isEmpty()) {
				userInfo.setPrimaryrole(userResponse.getUser().get(0).getRoles());
			} else {
				Role citizenRole = getCitizenRole(owner.getTenantId());
				userInfo.setPrimaryrole(Collections.singletonList(citizenRole));
			}
		}
	}

	private void addNonUpdatableFields(User user,User userFromSearchResult){
		user.setUserName(userFromSearchResult.getUserName());
		user.setId(userFromSearchResult.getId());
		user.setActive(userFromSearchResult.getActive());
		user.setPassword(userFromSearchResult.getPassword());
	}
	public void createUser(ChallanRequest request) {
		Challan challan = request.getChallan();
		RequestInfo requestInfo = request.getRequestInfo();
		Role role = getCitizenRole(challan.getTenantId());
		if (challan.getCitizen() == null) {
			throw new CustomException("INVALID USER", "The applications owners list is empty");
		}
		UserInfo owner  = challan.getCitizen();

			if (owner.getUuid() == null) {
				addUserDefaultFields(challan.getTenantId(), role, owner);

				UserDetailResponse existingUserResponse = userExists(owner, requestInfo);

				if (!existingUserResponse.getUser().isEmpty()) {
					UserInfo existingUser = existingUserResponse.getUser().get(0);
					log.info("User already exists with UUID: " + existingUser.getUuid());
					owner.setUuid(existingUser.getUuid());
					setOwnerFields(owner, existingUserResponse, requestInfo);
				} else {
					StringBuilder uri = new StringBuilder(userHost).append(userContextPath).append(userCreateEndpoint);
						setUserName(owner);
					UserDetailResponse userResponse = userCall(new CreateUserRequest(requestInfo, owner), uri);
					if (userResponse.getUser().get(0).getUuid() == null) {
						throw new CustomException("INVALID USER RESPONSE", "The user created has uuid as null");
					}
					log.info("owner created --> " + userResponse.getUser().get(0).getUuid());
					setOwnerFields(owner, userResponse, requestInfo);
				}
			} else {
				UserDetailResponse userResponse = userExists(owner, requestInfo);
				if (userResponse.getUser().isEmpty())
					throw new CustomException("INVALID USER", "The uuid " + owner.getUuid() + " does not exists");
				StringBuilder uri = new StringBuilder(userHost);
				uri.append(userContextPath).append(userUpdateEndpoint);
				UserInfo ownerInfo = new UserInfo();
				addNonUpdatableFields(ownerInfo, userResponse.getUser().get(0));
				userResponse = userCall(new CreateUserRequest(requestInfo, ownerInfo), uri);
				setOwnerFields(owner, userResponse, requestInfo);
			}


	}

}
