package upyog.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.common.contract.user.CreateUserRequest;
import org.egov.common.contract.user.UserDetailResponse;
import org.egov.common.contract.user.UserSearchRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import upyog.config.EstateConfiguration;
import upyog.repository.ServiceRequestRepository;
import upyog.web.models.Allotment;
import upyog.web.models.AllotmentRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private EstateConfiguration estateConfiguration;

    /**
     * Creates or retrieves user for allotment based on mobile number
     * Optimized method to check if user exists by mobile number and create if not
     *
     * @param requestInfo The request information
     * @param allotment The allotment details containing user information
     * @return UserDetailResponse containing the user details
     */
    public UserDetailResponse createOrGetUser(RequestInfo requestInfo, Allotment allotment) {
        // Check if user exists by mobile number
        UserSearchRequest userSearchRequest = getBaseUserSearchRequest(allotment.getTenantId(), requestInfo);
        userSearchRequest.setMobileNumber(allotment.getMobileNo());
        
        StringBuilder uri = new StringBuilder(estateConfiguration.getUserHost())
                .append(estateConfiguration.getUserSearchEndpoint());
        
        UserDetailResponse userDetailResponse = userCall(userSearchRequest, uri);
        
        // If user exists, return the existing user
        if (userDetailResponse != null && !CollectionUtils.isEmpty(userDetailResponse.getUser())) {
            log.info("User already exists with mobile number: {}", allotment.getMobileNo());
            return userDetailResponse;
        }
        
        // User doesn't exist, create a new user
        log.info("User does not exist. Creating new user with mobile number: {}", allotment.getMobileNo());
        return createUser(requestInfo, allotment);
    }

    /**
     * Creates user if it is not created already (Legacy method - kept for backward compatibility)
     *
     * @param request AllotmentRequest received for creating application
     */
    @Deprecated
    public void createUser(AllotmentRequest request) {

        Allotment allotment = request.getAllotments().get(0);
        RequestInfo requestInfo = request.getRequestInfo();
        Role role = getCitizenRole();
        User owner = new User();
        addUserDefaultFields(allotment.getTenantId(), role, owner);
        UserDetailResponse userDetailResponse = userExists(owner, requestInfo);
        List<User> existingUsersFromService = userDetailResponse.getUser();
        Map<String, User> ownerMapFromSearch = existingUsersFromService.stream()
                .collect(Collectors.toMap(User::getUuid, Function.identity()));

        if (CollectionUtils.isEmpty(existingUsersFromService)) {

            owner.setUserName(UUID.randomUUID().toString());
            userDetailResponse = createUserWithUser(requestInfo, owner);

        } else {

            String uuid = owner.getUuid();
            if (uuid != null && ownerMapFromSearch.containsKey(uuid)) {
                userDetailResponse = updateExistingUser(allotment, requestInfo, role, owner,
                        ownerMapFromSearch.get(uuid));
            } else {

                owner.setUserName(UUID.randomUUID().toString());
                userDetailResponse = createUserWithUser(requestInfo, owner);
            }
        }
        setOwnerFields(owner, userDetailResponse, requestInfo);
    }

    /**
     * update existing user
     */
    private UserDetailResponse updateExistingUser(Allotment allotment, RequestInfo requestInfo,
                                                  Role role, User ownerFromRequest, User ownerInfoFromSearch) {

        UserDetailResponse userDetailResponse;

        ownerFromRequest.setId(ownerInfoFromSearch.getId());
        ownerFromRequest.setUuid(ownerInfoFromSearch.getUuid());
        addUserDefaultFields(allotment.getTenantId(), role, ownerFromRequest);

        StringBuilder uri = new StringBuilder(estateConfiguration.getUserHost())
                .append(estateConfiguration.getUserContextPath()).append(estateConfiguration.getUserSearchEndpoint());
        userDetailResponse = userCall(
                new CreateUserRequest(requestInfo, ownerFromRequest), uri);
        if (userDetailResponse.getUser().get(0).getUuid() == null) {
            throw new CustomException("INVALID USER RESPONSE", "The user updated has uuid as null");
        }
        return userDetailResponse;
    }

    /**
     * Creates a new user in the user service
     * 
     * @param requestInfo The request information
     * @param allotment The allotment details containing user information
     * @return UserDetailResponse containing the created user details
     */
    public UserDetailResponse createUser(RequestInfo requestInfo, Allotment allotment) {
        UserDetailResponse userDetailResponse;
        StringBuilder uri = new StringBuilder(estateConfiguration.getUserHost())
                .append(estateConfiguration.getUserContextPath()).append(estateConfiguration.getUserCreateEndpoint());

        User owner = new User();
        Role role = getCitizenRole();
        addUserDefaultFields(allotment.getTenantId(), role, owner);
        owner.setUserName(UUID.randomUUID().toString());
        owner.setMobileNumber(allotment.getMobileNo());
        owner.setName(allotment.getAlloteeName());
        owner.setEmailId(allotment.getEmailId());

        CreateUserRequest userRequest = new CreateUserRequest(requestInfo, owner);

        userDetailResponse = userCall(userRequest, uri);

        if (ObjectUtils.isEmpty(userDetailResponse) || CollectionUtils.isEmpty(userDetailResponse.getUser())) {

            throw new CustomException("INVALID_USER_RESPONSE",
                    "The user creation has failed for the mobile number : " + allotment.getMobileNo());

        }
        
        log.info("User created successfully with UUID: {}", userDetailResponse.getUser().get(0).getUuid());
        return userDetailResponse;
    }

    /**
     * Creates a new user in the user service using User object (for backward compatibility)
     * 
     * @param requestInfo The request information
     * @param owner The user object containing user information
     * @return UserDetailResponse containing the created user details
     */
    public UserDetailResponse createUserWithUser(RequestInfo requestInfo, User owner) {
        UserDetailResponse userDetailResponse;
        StringBuilder uri = new StringBuilder(estateConfiguration.getUserHost())
                .append(estateConfiguration.getUserContextPath()).append(estateConfiguration.getUserCreateEndpoint());

        CreateUserRequest userRequest = new CreateUserRequest(requestInfo, owner);

        userDetailResponse = userCall(userRequest, uri);

        if (ObjectUtils.isEmpty(userDetailResponse) || CollectionUtils.isEmpty(userDetailResponse.getUser())) {

            throw new CustomException("INVALID_USER_RESPONSE",
                    "The user creation has failed for the mobile number : " + owner.getMobileNumber());

        }
        
        log.info("User created successfully with UUID: {}", userDetailResponse.getUser().get(0).getUuid());
        return userDetailResponse;
    }

    /**
     * Sets the role,type,active and tenantId for a Citizen
     *
     * @param tenantId TenantId of the pet application
     * @param role     The role of the user set in this case to CITIZEN
     * @param owner    The user whose fields are to be set
     */
    private void addUserDefaultFields(String tenantId, Role role, User owner) {

        owner.setTenantId(tenantId);
        owner.setRoles(Collections.singletonList(role));
        owner.setType("CITIZEN");
    }

    private Role getCitizenRole() {

        return Role.builder().code("CITIZEN").name("Citizen").build();
    }

    /**
     * Searches if the owner is already created. Search is based on name of owner,
     * uuid and mobileNumber
     *
     * @param owner       Owner which is to be searched
     * @param requestInfo RequestInfo from the PetRegistrationRequest
     * @return UserDetailResponse containing the user if present and the
     * responseInfo
     */
    private UserDetailResponse userExists(User owner, RequestInfo requestInfo) {

        UserSearchRequest userSearchRequest = getBaseUserSearchRequest(owner.getTenantId(), requestInfo);
        userSearchRequest.setMobileNumber(owner.getMobileNumber());
        userSearchRequest.setUserType(owner.getType());
        userSearchRequest.setName(owner.getName());

        StringBuilder uri = new StringBuilder(estateConfiguration.getUserHost())
                .append(estateConfiguration.getUserSearchEndpoint());
        return userCall(userSearchRequest, uri);
    }

    /**
     * Returns user using user search based on allotmentCriteria(owner
     * name,mobileNumber,userName)
     *
     * @param userSearchRequest
     * @return serDetailResponse containing the user if present and the responseInfo
     */
    public UserDetailResponse getUser(UserSearchRequest userSearchRequest) {

        StringBuilder uri = new StringBuilder(estateConfiguration.getUserHost())
                .append(estateConfiguration.getUserSearchEndpoint());
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

        String dobFormat = determineDobFormat(url.toString());
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
     * Determines the date format based on the URL endpoint
     *
     * @param url The URL of the endpoint
     * @return The appropriate date format
     */
    private String determineDobFormat(String url) {
        if (url.contains(estateConfiguration.getUserSearchEndpoint()) || url.contains(estateConfiguration.getUserSearchEndpoint())) {
            return "yyyy-MM-dd";
        } else if (url.contains(estateConfiguration.getUserSearchEndpoint())) {
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

    /**
     * Converts date to long
     *
     * @param date   date to be parsed
     * @param format Format of the date
     * @return Long value of date
     */
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

    /**
     * Sets owner fields (so that the owner table can be linked to user table)
     *
     * @param owner              Owner in the pet detail whose user is created
     * @param userDetailResponse userDetailResponse from the user Service
     *                           corresponding to the given owner
     */
    private void setOwnerFields(User owner, UserDetailResponse userDetailResponse, RequestInfo requestInfo) {

        owner.setUuid(userDetailResponse.getUser().get(0).getUuid());
        owner.setId(userDetailResponse.getUser().get(0).getId());
        owner.setUserName((userDetailResponse.getUser().get(0).getUserName()));
    }

    /**
     * Updates user if present else creates new user
     *
     * @param request PetRequest received from update
     */

    /**
     * provides a user search request with basic mandatory parameters
     *
     * @param tenantId
     * @param requestInfo
     * @return
     */
    public UserSearchRequest getBaseUserSearchRequest(String tenantId, RequestInfo requestInfo) {

        UserSearchRequest userSearchRequest = new UserSearchRequest();
        userSearchRequest.setUserType("CITIZEN");
        userSearchRequest.setTenantId(tenantId);
        userSearchRequest.setActive(true);
        userSearchRequest.setRequestInfo(requestInfo);
        return userSearchRequest;
    }

    public UserDetailResponse searchByUserName(String userName, String tenantId) {
        UserSearchRequest userSearchRequest = new UserSearchRequest();
        userSearchRequest.setUserType("CITIZEN");
        userSearchRequest.setUserName(userName);
        userSearchRequest.setTenantId(tenantId);
        return getUser(userSearchRequest);
    }

    private String getStateLevelTenant(String tenantId) {
        return tenantId.split("\\.")[0];
    }

    private UserDetailResponse searchedSingleUserExists(User owner, RequestInfo requestInfo) {

        UserSearchRequest userSearchRequest = getBaseUserSearchRequest(owner.getTenantId(), requestInfo);
        userSearchRequest.setUserType(owner.getType());
        List<String> uuids = new ArrayList<>();
        uuids.add(owner.getUuid());
        userSearchRequest.setUuid(uuids);

        StringBuilder uri = new StringBuilder(estateConfiguration.getUserHost())
                .append(estateConfiguration.getUserSearchEndpoint());
        return userCall(userSearchRequest, uri);
    }

}
