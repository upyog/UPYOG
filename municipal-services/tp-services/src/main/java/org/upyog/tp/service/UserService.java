package org.upyog.tp.service;
import java.lang.reflect.Method;
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
import org.upyog.tp.config.TreePruningConfiguration;
import org.upyog.tp.constant.TreePruningConstants;
import org.upyog.tp.repository.ServiceRequestRepository;
import org.upyog.tp.util.TreePruningUtil;
import org.upyog.tp.util.UserUtil;
import org.upyog.tp.web.models.Address;
import org.upyog.tp.web.models.ApplicantDetail;
import org.upyog.tp.web.models.AuditDetails;
import org.upyog.tp.web.models.user.*;
import org.upyog.tp.web.models.treePruning.TreePruningBookingDetail;
import org.upyog.tp.web.models.treePruning.TreePruningBookingRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import javax.validation.Valid;

@Slf4j
@Service
public class UserService {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private TreePruningConfiguration requestConfig;

    /**
     * Retrieves an existing user or creates a new user if not found.
     *
     * @param tenantId         tenant ID.
	 * @param applicantDetail  applicant details.
	 * @param requestInfo      request information.
     * @return The existing or newly created user.
     */
    public User fetchExistingUser(String tenantId, ApplicantDetail applicantDetail, RequestInfo requestInfo) {
        // Fetch existing user details
        UserDetailResponse userDetailResponse = getUserDetails(applicantDetail, requestInfo, tenantId);
        List<User> existingUsers = userDetailResponse.getUser();
        if (CollectionUtils.isEmpty(existingUsers)) {
            log.info("No existing user found for mobile number: {}", applicantDetail.getMobileNumber());
            return null;
        }
        return existingUsers.get(0);
    }

    private UserDetailResponse createUser(RequestInfo requestInfo, User user, String tenantId) {

        StringBuilder uri = new StringBuilder(requestConfig.getUserHost())
                .append(requestConfig.getUserCreateEndpointV2());
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
        userRequest.setType(TreePruningConstants.CITIZEN);
        userRequest.setCreatedDate(null);
        userRequest.setCreatedBy(null);
        userRequest.setLastModifiedDate(null);
        userRequest.setLastModifiedBy(null);
        return userRequest;
    }

    private Role getCitizenRole() {

        return Role.builder().code(TreePruningConstants.CITIZEN).name(TreePruningConstants.CITIZEN_NAME).build();
    }

    /**
     * Creates a new user and returns the generated user details.
     *
     * @param requestInfo     The request information.
     * @param applicantDetail The applicant details.
     * @param tenantId        The tenant ID.
     * @return The created user.
     */
    public User createUserHandler(RequestInfo requestInfo,  ApplicantDetail applicantDetail, Address address, String tenantId) {
        Role role = getCitizenRole();
        User user = convertApplicantToUserRequest(applicantDetail, role, tenantId);
        AddressV2 addressV2 = convertApplicantAddressToUserAddress(address, tenantId);
        user.addAddressItem(addressV2);
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
    private UserDetailResponse getUserDetails(ApplicantDetail applicant, RequestInfo requestInfo, String tenantId) {

        UserSearchRequest userSearchRequest = getBaseUserSearchRequest(UserUtil.getStateLevelTenant(tenantId), requestInfo);
        userSearchRequest.setMobileNumber(applicant.getMobileNumber());
        userSearchRequest.setUserType(TreePruningConstants.CITIZEN);
        userSearchRequest.setUserName(applicant.getMobileNumber());
        StringBuilder uri = new StringBuilder(requestConfig.getUserHost())
                .append(requestConfig.getUserSearchEndpointV2());
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
                .append(requestConfig.getUserSearchEndpointV2());
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
        if (url.indexOf(requestConfig.getUserSearchEndpointV2()) != -1)
            dobFormat = "yyyy-MM-dd";
        else if (url.indexOf(requestConfig.getUserCreateEndpointV2()) != -1)
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

                map.put("createdDate", TreePruningUtil.dateTolong((String) map.get("createdDate"), format1));
                if ((String) map.get("lastModifiedDate") != null)
                    map.put("lastModifiedDate",
                            TreePruningUtil.dateTolong((String) map.get("lastModifiedDate"), format1));
                if ((String) map.get("dob") != null)
                    map.put("dob", TreePruningUtil.dateTolong((String) map.get("dob"), dobFormat));
                if ((String) map.get("pwdExpiryDate") != null)
                    map.put("pwdExpiryDate", TreePruningUtil.dateTolong((String) map.get("pwdExpiryDate"), format1));
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

        return UserSearchRequest.builder().requestInfo(requestInfo).userType(TreePruningConstants.CITIZEN)
                .tenantId(tenantId).active(true).build();
    }

    /**
     * Converts a user object to an applicant detail object.
     *
     * @param user The user object.
     * @return The converted applicant detail.
     */
    public ApplicantDetail convertUserToApplicantDetail(User user, String applicantUuid, String bookingId, AuditDetails auditDetails) {
        if (user == null) {
            return null;
        }
        // Convert User to ApplicantDetail
        return ApplicantDetail.builder()
                .name(user.getName())
                .emailId(user.getEmailId())
                .mobileNumber(user.getMobileNumber())
                .alternateNumber(user.getAltContactNumber())
                .bookingId(bookingId)
                .applicantId(applicantUuid)
                .auditDetails(auditDetails)
                .build();
    }

    /**
     * Converts a user address to an address detail object.
     *
     * @param user The set of addresses.
     * @return The converted address detail.
     */
    public Address convertUserAddressToAddressDetail(User user, String applicantUuid) {
        if (CollectionUtils.isEmpty(user.getAddresses())) {
            return null;
        }
        AddressV2 addressV2 = user.getAddresses().get(0);
        return Address.builder()
                .addressLine1(addressV2.getAddress())
                .addressLine2(addressV2.getAddress2())
                .city(addressV2.getCity())
                .pincode(addressV2.getPinCode())
                .streetName(addressV2.getStreetName())
                .landmark(addressV2.getLandmark())
                .houseNo(addressV2.getHouseNumber())
                .locality(addressV2.getLocality())
                .addressId(addressV2.getId().toString())
                .addressType(addressV2.getType())
                .applicantId(applicantUuid)
                .build();
    }

    /**
     * Enriches a generic booking object with user details by using reflection to dynamically
     * access and invoke methods on TreePruningBookingDetail.
     *
     * This method:
     * - Extracts the applicant UUID, address detail ID, and booking ID from the booking object.
     * - Uses the applicant UUID to fetch user details via a user search service.
     * - Populates the booking object with applicant and optionally address details.
     * - Supports TreePruning bookings using reflection.
     *
     * @param booking         The booking object TreePruningBookingDetail.
     * @param searchCriteria  The corresponding search criteria object used to determine enrichment behavior.
     */
    public void enrichBookingWithUserDetails(Object booking, Object searchCriteria) {
        try {
            Method getApplicantUuid = booking.getClass().getMethod("getApplicantUuid");
            String applicantUuid = (String) getApplicantUuid.invoke(booking);

            if (applicantUuid == null) {
                return;
            }

            Method getBookingNo = searchCriteria.getClass().getMethod("getBookingNo");
            boolean excludeAddressDetails = getBookingNo.invoke(searchCriteria) != null;

            Method getAddressDetailId = booking.getClass().getMethod("getAddressDetailId");
            String addressDetailId = (String) getAddressDetailId.invoke(booking);

            Method getBookingId = booking.getClass().getMethod("getBookingId");
            String bookingId = (String) getBookingId.invoke(booking);

            UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                    .uuid(Collections.singleton(applicantUuid))
                    .excludeAddressDetails(excludeAddressDetails)
                    .build();

            if (excludeAddressDetails) {
                userSearchRequest.setAddressId(addressDetailId);
            }

            UserDetailResponse userDetailResponse = getUser(userSearchRequest);

            if (userDetailResponse != null && !CollectionUtils.isEmpty(userDetailResponse.getUser())) {
                User user = userDetailResponse.getUser().get(0);

                // Step 1: Get method named "getAuditDetails"
                Method getAuditDetails = booking.getClass().getMethod("getAuditDetails");
                // Step 2: Invoke the method and cast the result to AuditDetails
                AuditDetails auditDetails = (AuditDetails) getAuditDetails.invoke(booking);

                Object applicantDetail = convertUserToApplicantDetail(user, applicantUuid, bookingId, auditDetails);
                Method setApplicantDetail = booking.getClass().getMethod("setApplicantDetail", applicantDetail.getClass());
                setApplicantDetail.invoke(booking, applicantDetail);

                if (excludeAddressDetails) {
                    Object address = convertUserAddressToAddressDetail(user, applicantUuid);
                    Method setAddress = booking.getClass().getMethod("setAddress", address.getClass());
                    setAddress.invoke(booking, address);
                }
            }
        } catch (Exception e) {
            log.error("Error while enriching booking with user details: " + e.getMessage(), e);
        }
    }

    /**
     * Converts an applicant address to a User address object to send in user create call with address object.
     *
     * @param address The address details.
     * @param tenantId         The tenant ID.
     * @return The converted User address object.
     */
    public static AddressV2 convertApplicantAddressToUserAddress(Address address, String tenantId) {
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
     * Creates a new address for the user UUID provided in the treePruningRequest.
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
     * @param address The request object containing the application data and user information.
     * @return The newly created Address object, or null if creation fails.
     */
    public AddressV2 createNewAddressV2ByUserUuid(AddressV2 address, @Valid RequestInfo requestInfo, String applicantUuid) {
        AddressRequestV2 addressRequest = AddressRequestV2.builder().requestInfo(requestInfo).address(address).userUuid(applicantUuid).build();

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
