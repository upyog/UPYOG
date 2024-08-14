package digit.service;

import java.util.ArrayList;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import digit.bmc.model.AadharUser;
import digit.bmc.model.UserOtherDetails;
import digit.config.BmcConfiguration;
import digit.kafka.Producer;
import digit.repository.BmcUserRepository;
import digit.repository.UserRepository;
import digit.repository.UserSearchCriteria;
import digit.util.UserUtil;
import digit.web.models.BankDetails;
import digit.web.models.BmcUser;
import digit.web.models.SchemeApplication;
import digit.web.models.SchemeApplicationRequest;
import digit.web.models.user.InputTest;
import digit.web.models.user.QualificationSave;
import digit.web.models.user.UserDetails;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {

	private UserUtil userUtils;
	private BmcConfiguration config;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private Producer producer;

	@Autowired
	BmcUserRepository bmcUserRepository;

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

	public List<UserDetails> getUserDetails(RequestInfo requestInfo, UserSearchCriteria searchcriteria) {
		// Fetch applications from database according to the given search criteria
		List<UserDetails> common = userRepository.getUserDetails(searchcriteria);
		// If no applications are found matching the given criteria, return an empty
		// list
		if (CollectionUtils.isEmpty(common))
			return new ArrayList<>();
		return common;
	}

	public Object saveUserDetails(InputTest userRequest) throws Exception {

		Long userId = userRequest.getRequestInfo().getUserInfo().getId();
		String tenantId = userRequest.getRequestInfo().getUserInfo().getTenantId();
		Long time = System.currentTimeMillis();

		userRequest.setAadharUser(new AadharUser());
		userRequest.getAadharUser().setUserId(userId);
		userRequest.getAadharUser().setTenantId(tenantId);
		userRequest.getAadharUser().setAadharDob(userRequest.getPersonalDetails().getAadharDob());
		userRequest.getAadharUser().setAadharName(userRequest.getPersonalDetails().getTitle().getName() + ". " + userRequest.getPersonalDetails().getAadharName());
		userRequest.getAadharUser().setAadharFatherName(userRequest.getPersonalDetails().getAadharFatherName());
		userRequest.getAadharUser().setGender(userRequest.getPersonalDetails().getGender().getName());
		userRequest.getAadharUser().setAadharMobile(userRequest.getRequestInfo().getUserInfo().getMobileNumber());
		userRequest.getAadharUser().setCreatedBy("system");
		userRequest.getAadharUser().setCreatedOn(time);
		userRequest.getAadharUser().setAadharRef(userRequest.getPersonalDetails().getAadharRef());

		userRequest.setUserOtherDetails(new UserOtherDetails());
		userRequest.getUserOtherDetails().setCaste(userRequest.getPersonalDetails().getCaste());
		userRequest.getUserOtherDetails().setReligion(userRequest.getPersonalDetails().getReligion());
		userRequest.getUserOtherDetails().setTransgenderId(userRequest.getPersonalDetails().getTransgenderId());
		userRequest.getUserOtherDetails().setUserId(userId);
		userRequest.getUserOtherDetails().setTenantId(tenantId);
		userRequest.getUserOtherDetails().setCreatedBy("system");
		userRequest.getUserOtherDetails().setCreatedOn(time);
		userRequest.getUserOtherDetails().setDivyang(userRequest.getDivyangDetails().getDivyangtype());
		userRequest.getUserOtherDetails().setModifiedBy("system");
		userRequest.getUserOtherDetails().setModifiedOn(time);
		userRequest.getUserOtherDetails().setDivyangCardId(userRequest.getDivyangDetails().getDivyangcardid());
		userRequest.getUserOtherDetails().setDivyangPercent(userRequest.getDivyangDetails().getDivyangpercent());
		userRequest.getUserOtherDetails().setZone(userRequest.getUserAddressDetails().getZoneName());
		userRequest.getUserOtherDetails().setBlock(userRequest.getUserAddressDetails().getBlockName());
		userRequest.getUserOtherDetails().setWard(userRequest.getUserAddressDetails().getWardName().getCode());

        Long addressId = null;
		UserSearchCriteria userSearchCriteria = new UserSearchCriteria();
		userSearchCriteria.setOption("address");
		userSearchCriteria.setTenantId(tenantId);
		userSearchCriteria.setUserId(userId);
		List<UserDetails> userDetails = userRepository.getUserDetails(userSearchCriteria);
		userRequest.getUserAddressDetails().setUserId(userId);
		userRequest.getUserAddressDetails().setTenantId(tenantId);
		if(!userDetails.isEmpty()){
           addressId = userDetails.get(0).getAddress().getId();
		}
		if(addressId != null && addressId != 0){
			userRequest.getUserAddressDetails().setId(addressId);
 			producer.push("update-useraddress", userRequest);
		} else {
			addressId = userRepository.getUserAddressMaxId() + 1;
			userRequest.getUserAddressDetails().setId(addressId);

			producer.push("insert-useraddress", userRequest);
		}

		for (BankDetails details : userRequest.getBankDetailsList()) {
			details.setUserId(userId);
			details.setTenantId(tenantId);
			details.setCreatedBy("system");
			details.setModifiedBy("system");
			details.setModifiedOn(time);
			userRequest.setBankDetails(details);
			producer.push("upsert-userbank", userRequest);
		}
		if (!ObjectUtils.isEmpty(userRequest.getQualificationDetailsList())) {
			for (QualificationSave details : userRequest.getQualificationDetailsList()) {

				details.setUserId(userId);

				details.setCreatedBy("system");
				details.setCreatedOn(time);
				details.setModifiedBy("system");
				details.setModifiedOn(time);
				userRequest.setQualificationDetails(details);
				producer.push("upsert-userqualification", userRequest);
			}
		}
		producer.push("upsert-aadharuser", userRequest);
		producer.push("upsert-userotherdetails", userRequest);

		
		return userRequest;
	}

}
