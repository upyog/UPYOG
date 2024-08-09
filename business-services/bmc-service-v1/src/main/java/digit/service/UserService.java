package digit.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.common.contract.user.CreateUserRequest;
import org.egov.common.contract.user.UserDetailResponse;
import org.egov.common.contract.user.UserSearchRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import digit.config.BmcConfiguration;
import digit.util.UserUtil;
import digit.web.models.SchemeApplication;
import digit.web.models.SchemeApplicationRequest;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {

	private UserUtil userUtils;
	private BmcConfiguration config;

	@Inject
	public UserService(UserUtil userUtils, BmcConfiguration config) {
		this.userUtils = userUtils;
		this.config = config;
	}

	/**
	 * Calls user service to enrich user from search or upsert user
	 * 
	 * @param request
	 */
	public void callUserService(SchemeApplicationRequest request) {

		
		  request.getSchemeApplications().forEach(application -> { 
			  if (application != null && application.getUser() != null && application.getUser().getId() != null) {
		            enrichUser(application, request.getRequestInfo());
		        }
		    });
		 

	}

	private User createUserEntity(SchemeApplication application) {
		User userEntity = application.getUser();
		return User.builder().userName(userEntity.getUserName()).name(userEntity.getName())
				.mobileNumber(userEntity.getMobileNumber()).emailId(userEntity.getEmailId())
				.tenantId(userEntity.getTenantId()).type(userEntity.getType()).roles(userEntity.getRoles()).build();
	}

	private User upsertUser(User user, RequestInfo requestInfo) {
		String tenantId = user.getTenantId();
		User userServiceResponse = null;

		// Search on mobile number as user name
		UserDetailResponse userDetailResponse = searchUser(userUtils.getStateLevelTenant(tenantId), null,
				user.getUserName());
		if (!userDetailResponse.getUser().isEmpty()) {
			User userFromSearch = userDetailResponse.getUser().get(0);
			log.info(userFromSearch.toString());
			if (!user.getUserName().equalsIgnoreCase(userFromSearch.getUserName())) {
				userServiceResponse = updateUser(requestInfo, user, userFromSearch);
			} else {
				userServiceResponse = userDetailResponse.getUser().get(0);
			}
		} else {
			userServiceResponse = createUser(requestInfo, tenantId, user);
		}

		return userServiceResponse;
	}

	private void enrichUser(SchemeApplication application, RequestInfo requestInfo) {
		Long accountId = application.getUser().getId();
		String tenantId = application.getTenantId();

		UserDetailResponse userDetailResponse = searchUser(userUtils.getStateLevelTenant(tenantId), accountId, null);
		if (userDetailResponse.getUser().isEmpty())
			throw new CustomException("INVALID_ACCOUNTID", "No user exists for the given accountId");

		application.getUser().setId(userDetailResponse.getUser().get(0).getId());
	}

	/**
	 * Creates the user from the given userInfo by calling user service
	 * 
	 * @param requestInfo
	 * @param tenantId
	 * @param userInfo
	 * @return
	 */
	private User createUser(RequestInfo requestInfo, String tenantId, User userInfo) {
		userUtils.addUserDefaultFields(userInfo.getMobileNumber(), tenantId, userInfo);
		StringBuilder uri = new StringBuilder(config.getUserHost()).append(config.getUserContextPath())
				.append(config.getUserCreateEndpoint());

		CreateUserRequest user = new CreateUserRequest(requestInfo, userInfo);
		log.info(user.getUser().toString());
		UserDetailResponse userDetailResponse = userUtils.userCall(user, uri);

		return userDetailResponse.getUser().get(0);
	}

	/**
	 * Updates the given user by calling user service
	 * 
	 * @param requestInfo
	 * @param user
	 * @param userFromSearch
	 * @return
	 */
	private User updateUser(RequestInfo requestInfo, User user, User userFromSearch) {
		userFromSearch.setName(user.getName());

		StringBuilder uri = new StringBuilder(config.getUserHost()).append(config.getUserContextPath())
				.append(config.getUserUpdateEndpoint());

		UserDetailResponse userDetailResponse = userUtils.userCall(new CreateUserRequest(requestInfo, userFromSearch),
				uri);

		return userDetailResponse.getUser().get(0);
	}

	/**
	 * Calls the user search API based on the given accountId and userName
	 * 
	 * @param stateLevelTenant
	 * @param accountId
	 * @param userName
	 * @return
	 */
	public UserDetailResponse searchUser(String stateLevelTenant, Long accountId, String userName) {
		UserSearchRequest userSearchRequest = new UserSearchRequest();
		userSearchRequest.setActive(false);
		userSearchRequest.setTenantId(stateLevelTenant);

		if (accountId == null && StringUtils.isEmpty(userName))
			return null;

		if (accountId != null)
			userSearchRequest.setId(Collections.singletonList(accountId.toString()));

		if (!StringUtils.isEmpty(userName))
			userSearchRequest.setUserName(userName);

		StringBuilder uri = new StringBuilder(config.getUserHost()).append(config.getUserSearchEndpoint());
		return userUtils.userCall(userSearchRequest, uri);
	}

	/**
	 * Calls the user search API based on the given list of user ids
	 * 
	 * @param ids
	 * @return
	 */
	public Map<Long, User> searchBulkUser(List<Long> ids) {
		UserSearchRequest userSearchRequest = new UserSearchRequest();
		userSearchRequest.setActive(false);
		userSearchRequest.setUserType("CITIZEN");

		if (!CollectionUtils.isEmpty(ids))
			userSearchRequest.setId(ids.stream().map(String::valueOf).collect(Collectors.toList()));

		StringBuilder uri = new StringBuilder(config.getUserHost()).append(config.getUserSearchEndpoint());
		UserDetailResponse userDetailResponse = userUtils.userCall(userSearchRequest, uri);
		List<User> users = userDetailResponse.getUser();

		if (CollectionUtils.isEmpty(users))
			throw new CustomException("USER_NOT_FOUND", "No user found for the ids");

		return users.stream().collect(Collectors.toMap(User::getId, Function.identity()));
	}
}
