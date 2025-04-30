package org.upyog.rs.service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.upyog.rs.config.RequestServiceConfiguration;
import org.upyog.rs.constant.RequestServiceConstants;
import org.upyog.rs.repository.ServiceRequestRepository;
import org.upyog.rs.util.RequestServiceUtil;
import org.upyog.rs.util.UserUtil;
import org.upyog.rs.web.models.Address;
import org.upyog.rs.web.models.ApplicantDetail;
import org.upyog.rs.web.models.mobileToilet.MobileToiletBookingDetail;
import org.upyog.rs.web.models.mobileToilet.MobileToiletBookingRequest;
import org.upyog.rs.web.models.mobileToilet.MobileToiletBookingSearchCriteria;
import org.upyog.rs.web.models.user.*;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingDetail;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingSearchCriteria;

@Slf4j
@Service
public class UserService {

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private RequestServiceConfiguration requestConfig;

	/**
	 * Retrieves an existing user or creates a new user if not found.
	 *
	 * @param bookingRequest The application request containing user details.
	 * @return The existing or newly created user.
	 */
	public User getExistingOrNewUser(WaterTankerBookingRequest bookingRequest) {

		WaterTankerBookingDetail bookingDetail = bookingRequest.getWaterTankerBookingDetail();
		RequestInfo requestInfo = bookingRequest.getRequestInfo();
		ApplicantDetail applicantDetail = bookingDetail.getApplicantDetail();
		String tenantId = bookingDetail.getTenantId();

		// Fetch existing user details
		UserDetailResponse userDetailResponse = userExists(applicantDetail, requestInfo, tenantId);
		List<User> existingUsers = userDetailResponse.getUser();

		// Create a new user if no existing user found
		if (CollectionUtils.isEmpty(existingUsers)) {
			return createUserHandler(requestInfo, applicantDetail, tenantId);
		}

		return existingUsers.get(0);
	}

	/**
	 * Retrieves an existing user or creates a new user if not found.
	 *
	 * @param bookingRequest The application request containing user details.
	 * @return The existing or newly created user.
	 */
	public User getExistingOrNewUser(MobileToiletBookingRequest bookingRequest) {

		MobileToiletBookingDetail bookingDetail = bookingRequest.getMobileToiletBookingDetail();
		RequestInfo requestInfo = bookingRequest.getRequestInfo();
		ApplicantDetail applicantDetail = bookingDetail.getApplicantDetail();
		String tenantId = bookingDetail.getTenantId();

		// Fetch existing user details
		UserDetailResponse userDetailResponse = userExists(applicantDetail, requestInfo, tenantId);
		List<User> existingUsers = userDetailResponse.getUser();

		// Create a new user if no existing user found
		if (CollectionUtils.isEmpty(existingUsers)) {
			return createUserHandler(requestInfo, applicantDetail, tenantId);
		}

		return existingUsers.get(0);
	}

	private UserDetailResponse createUser(RequestInfo requestInfo, User user, String tenantId) {

		StringBuilder uri = new StringBuilder(requestConfig.getUserHost())
				.append(requestConfig.getUserCreateEndpoint());
		CreateUserRequest userRequest = CreateUserRequest.builder().requestInfo(requestInfo).user(user).build();
		UserDetailResponse userDetailResponse = userServiceCall(userRequest, uri);

		if (ObjectUtils.isEmpty(userDetailResponse)) {
			throw new CustomException("INVALID USER RESPONSE",
					"The user create has failed for the mobileNumber : " + user.getUserName());
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
		userRequest.setActive(true);
		userRequest.setTenantId(tenantId);
		userRequest.setRoles(Collections.singletonList(role));
		userRequest.setType(RequestServiceConstants.CITIZEN);
		userRequest.setCreatedDate(null);
		userRequest.setCreatedBy(null);
		userRequest.setLastModifiedDate(null);
		userRequest.setLastModifiedBy(null);
		return userRequest;
	}

	private Role getCitizenRole() {

		return Role.builder().code(RequestServiceConstants.CITIZEN).name(RequestServiceConstants.CITIZEN_NAME).build();
	}

	/**
	 * Creates a new user and returns the generated user details.
	 *
	 * @param requestInfo     The request information.
	 * @param applicantDetail The applicant details.
	 * @param tenantId        The tenant ID.
	 * @return The created user.
	 */
	private User createUserHandler(RequestInfo requestInfo,  ApplicantDetail applicantDetail, String tenantId) {
		Role role = getCitizenRole();
		User user = convertApplicantToUserRequest(applicantDetail, role, tenantId);
		UserDetailResponse userDetailResponse = createUser(requestInfo, user, tenantId);
		String newUuid = userDetailResponse.getUser().get(0).getUuid();
		log.info("New user uuid returned from user service: {}", newUuid);
		return userDetailResponse.getUser().get(0);
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

		UserSearchRequest userSearchRequest = getBaseUserSearchRequest(UserUtil.getStateLevelTenant(tenantId), requestInfo);
		userSearchRequest.setMobileNumber(applicant.getMobileNumber());
		userSearchRequest.setUserType(RequestServiceConstants.CITIZEN);
		userSearchRequest.setUserName(applicant.getMobileNumber());
		StringBuilder uri = new StringBuilder(requestConfig.getUserHost())
				.append(requestConfig.getUserSearchEndpoint());
		return userServiceCall(userSearchRequest, uri);
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
		UserDetailResponse userDetailResponse = userServiceCall(userSearchRequest, uri);
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
	private UserDetailResponse userServiceCall(Object userRequest, StringBuilder url) {

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

		return UserSearchRequest.builder().requestInfo(requestInfo).userType(RequestServiceConstants.CITIZEN)
				.tenantId(tenantId).active(true).build();
	}

	/**
	 * Converts a user object to an applicant detail object.
	 *
	 * @param user The user object.
	 * @return The converted applicant detail.
	 */
	public ApplicantDetail convertUserToApplicantDetail(User user, MobileToiletBookingDetail booking) {
		if (user == null) {
			return null;
		}
		// Convert User to ApplicantDetail
		return ApplicantDetail.builder()
				.name(user.getName())
				.emailId(user.getEmailId())
				.mobileNumber(user.getMobileNumber())
				.alternateNumber(user.getAltContactNumber())
				.bookingId(booking.getBookingId())
				.applicantId(booking.getApplicantUuid())
				.build();
	}

	/**
	 * Converts a user object to an applicant detail object.
	 *
	 * @param user The user object.
	 * @return The converted applicant detail.
	 */
	public ApplicantDetail convertUserToApplicantDetail(User user, WaterTankerBookingDetail booking) {
		if (user == null) {
			return null;
		}
		// Convert User to ApplicantDetail
		return ApplicantDetail.builder()
				.name(user.getName())
				.emailId(user.getEmailId())
				.mobileNumber(user.getMobileNumber())
				.alternateNumber(user.getAltContactNumber())
				.bookingId(booking.getBookingId())
				.applicantId(booking.getApplicantUuid())
				.build();
	}

	/**
	 * Converts a user address to an address detail object.
	 *
	 * @param user The set of addresses.
	 * @return The converted address detail.
	 */
	public Address convertUserAddressToAddressDetail(User user, MobileToiletBookingDetail booking) {
		if (CollectionUtils.isEmpty(user.getAddresses())) {
			return null;
		}
		return Address.builder()
				.addressLine1(user.getAddresses().get(0).getAddress())
				.addressLine2(user.getAddresses().get(0).getAddress2())
				.city(user.getAddresses().get(0).getCity())
				.pincode(user.getAddresses().get(0).getPinCode())
				.streetName(user.getAddresses().get(0).getStreetName())
				.landmark(user.getAddresses().get(0).getLandmark())
				.houseNo(user.getAddresses().get(0).getHouseNumber())
				.locality(user.getAddresses().get(0).getLocality())
				.addressId(user.getAddresses().get(0).getId())
				.addressType(user.getAddresses().get(0).getType())
				.applicantId(booking.getApplicantUuid())
				.build();
	}

	/**
	 * Converts a user address to an address detail object.
	 *
	 * @param user The set of addresses.
	 * @return The converted address detail.
	 */
	public Address convertUserAddressToAddressDetail(User user, WaterTankerBookingDetail booking) {
		if (CollectionUtils.isEmpty(user.getAddresses())) {
			return null;
		}
		return Address.builder()
				.addressLine1(user.getAddresses().get(0).getAddress())
				.addressLine2(user.getAddresses().get(0).getAddress2())
				.city(user.getAddresses().get(0).getCity())
				.pincode(user.getAddresses().get(0).getPinCode())
				.streetName(user.getAddresses().get(0).getStreetName())
				.landmark(user.getAddresses().get(0).getLandmark())
				.houseNo(user.getAddresses().get(0).getHouseNumber())
				.locality(user.getAddresses().get(0).getLocality())
				.addressId(user.getAddresses().get(0).getId())
				.addressType(user.getAddresses().get(0).getType())
				.applicantId(booking.getApplicantUuid())
				.build();
	}

	/**
	 * Enriches the booking details with user information.
	 *
	 * @param booking         The booking details.
	 * @param searchCriteria   The search criteria for the booking.
	 */
	public void enrichBookingWithUserDetails(MobileToiletBookingDetail booking,
											  MobileToiletBookingSearchCriteria searchCriteria) {

		String applicantUuid = booking.getApplicantUuid();

		if (applicantUuid == null) {
			return;
		}

		boolean excludeAddressDetails = searchCriteria.getBookingNo() != null;

		UserSearchRequest userSearchRequest = UserSearchRequest.builder()
				.uuid(Collections.singleton(applicantUuid))
				.excludeAddressDetails(excludeAddressDetails)
				.build();

		if (excludeAddressDetails) {
			userSearchRequest.setAddressId(booking.getAddressDetailId());
		}

		try {
			UserDetailResponse userDetailResponse =getUser(userSearchRequest);

			if (userDetailResponse != null && !CollectionUtils.isEmpty(userDetailResponse.getUser())) {
				User user = userDetailResponse.getUser().get(0);

				booking.setApplicantDetail(convertUserToApplicantDetail(user, booking));

				if (excludeAddressDetails) {
					booking.setAddress(convertUserAddressToAddressDetail(user, booking));
				}
			}
		}catch (Exception e) {
			log.error("Error while fetching user details: " + e.getMessage(), e);
		}
	}

	/**
	 * Enriches the booking details with user information.
	 *
	 * @param booking         The booking details.
	 * @param searchCriteria   The search criteria for the booking.
	 */
	public void enrichBookingWithUserDetails(WaterTankerBookingDetail booking,
											 WaterTankerBookingSearchCriteria searchCriteria) {

		String applicantUuid = booking.getApplicantUuid();

		if (applicantUuid == null) {
			return;
		}

		boolean excludeAddressDetails = searchCriteria.getBookingNo() != null;

		UserSearchRequest userSearchRequest = UserSearchRequest.builder()
				.uuid(Collections.singleton(applicantUuid))
				.excludeAddressDetails(excludeAddressDetails)
				.build();

		if (excludeAddressDetails) {
			userSearchRequest.setAddressId(booking.getAddressDetailId());
		}

		try {
			UserDetailResponse userDetailResponse =getUser(userSearchRequest);

			if (userDetailResponse != null && !CollectionUtils.isEmpty(userDetailResponse.getUser())) {
				User user = userDetailResponse.getUser().get(0);

				booking.setApplicantDetail(convertUserToApplicantDetail(user, booking));

				if (excludeAddressDetails) {
					booking.setAddress(convertUserAddressToAddressDetail(user, booking));
				}
			}
		}catch (Exception e) {
			log.error("Error while fetching user details: " + e.getMessage(), e);
		}
	}

	/**
	 * Converts an applicant address to a User address object to send in user create call with address object.
	 *
	 * @param address The address details.
	 * @param tenantId         The tenant ID.
	 * @return The converted User address object.
	 */
	private AddressV2 convertApplicantAddressToUserAddress(Address address, String tenantId) {
		if (address == null) {
			log.info("The address details are empty or null");
		}
		AddressV2 addressdetails = AddressV2.builder().
				address(address.getAddressLine1()).
				address2(address.getAddressLine2()).
				city(address.getCity()).
				landmark(address.getLandmark()).
				locality(address.getLocality()).
				pinCode(address.getPincode()).
				houseNumber(address.getHouseNo()).
				tenantId(tenantId).
				type(address.getAddressType()).
				build();

		return addressdetails;
	}

	/**
	 * Creates a new address for the user UUID provided in the waterTankerRequest.
	 *
	 * This method:
	 * 1. Converts the address details from the application into a user address.
	 * 2. Builds an AddressRequest object with the converted address, user UUID, and request information.
	 * 3. Sends the AddressRequest to the user service to create the new address.
	 * 4. Parses the response to extract and return the first created address, if available.
	 *
	 * If the response is null or an error occurs during processing, appropriate logs are generated
	 * and the method returns null.
	 *
	 * @param waterTankerRequest The request object containing the application data and user information.
	 * @return The newly created Address object, or null if creation fails.
	 */
	public AddressV2 createNewAddressV2ByUserUuid(WaterTankerBookingRequest waterTankerRequest) {
		AddressV2 address = convertApplicantAddressToUserAddress(waterTankerRequest.getWaterTankerBookingDetail().getAddress(), RequestServiceUtil.extractTenantId(waterTankerRequest.getWaterTankerBookingDetail().getTenantId()));
		AddressRequestV2 addressRequest = AddressRequestV2.builder().requestInfo(waterTankerRequest.getRequestInfo()).address(address).userUuid(waterTankerRequest.getWaterTankerBookingDetail().getApplicantUuid()).build();

		StringBuilder uri = new StringBuilder(requestConfig.getUserHost()).append(requestConfig.getUserCreateAddressEndpointV2());
		Object response = serviceRequestRepository.fetchResult(uri, addressRequest);

		if (response == null) {
			log.warn("Response from user service is null.");
			return null;
		}
		try {
			LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response;
			log.info("Response from user service after address creation: {}", responseMap);
			AddressResponseV2 addressResponse = mapper.convertValue(responseMap, AddressResponseV2.class);
			return Optional.ofNullable(addressResponse).map(AddressResponseV2::getAddress).filter(addresses -> !addresses.isEmpty()).map(addresses -> addresses.get(0)).orElse(null);

		} catch (Exception e) {
			log.error("Error while parsing response from user service: {}", e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Creates a new address for the user UUID provided in the MobileToiletBookingRequest.
	 *
	 * This method:
	 * 1. Converts the address details from the application into a user address.
	 * 2. Builds an AddressRequest object with the converted address, user UUID, and request information.
	 * 3. Sends the AddressRequest to the user service to create the new address.
	 * 4. Parses the response to extract and return the first created address, if available.
	 *
	 * If the response is null or an error occurs during processing, appropriate logs are generated
	 * and the method returns null.
	 *
	 * @param mobileToiletRequest The request object containing the application data and user information.
	 * @return The newly created Address object, or null if creation fails.
	 */
	public AddressV2 createNewAddressV2ByUserUuid(MobileToiletBookingRequest mobileToiletRequest) {
		AddressV2 address = convertApplicantAddressToUserAddress(mobileToiletRequest.getMobileToiletBookingDetail().getAddress(), RequestServiceUtil.extractTenantId(mobileToiletRequest.getMobileToiletBookingDetail().getTenantId()));
		AddressRequestV2 addressRequest = AddressRequestV2.builder().requestInfo(mobileToiletRequest.getRequestInfo()).address(address).userUuid(mobileToiletRequest.getMobileToiletBookingDetail().getApplicantUuid()).build();

		StringBuilder uri = new StringBuilder(requestConfig.getUserHost()).append(requestConfig.getUserCreateAddressEndpointV2());
		Object response = serviceRequestRepository.fetchResult(uri, addressRequest);

		if (response == null) {
			log.warn("Response from user service is null.");
			return null;
		}
		try {
			LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response;
			log.info("Response from user service after address creation: {}", responseMap);
			AddressResponseV2 addressResponse = mapper.convertValue(responseMap, AddressResponseV2.class);
			return Optional.ofNullable(addressResponse).map(AddressResponseV2::getAddress).filter(addresses -> !addresses.isEmpty()).map(addresses -> addresses.get(0)).orElse(null);

		} catch (Exception e) {
			log.error("Error while parsing response from user service: {}", e.getMessage(), e);
			return null;
		}
	}





}
