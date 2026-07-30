package org.egov.garbageservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.garbageservice.config.GarbageServiceConfig;
import org.egov.garbageservice.model.*;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Integrates with eGov user service to search, create, and link citizens to garbage accounts.
 * Maps OwnerInfo to user payloads, assigns roles, and supports account onboarding from GarbageAccountService.
 */
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
    private GarbageServiceConfig grbgConfig;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private ObjectMapper mapper;

    /**
     * Retrieves user details by UUID from the eGov user service.
     *
     * @param userUuid the unique identifier of the user to search for
     * @return a {@link UserSearchResponse} containing the matching user details, or null if not found
     */

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

    /**
     * Searches for multiple users based on generic criteria and returns a mapped response.
     *
     * @param userSearchRequest the generic search criteria
     * @return a {@link Map} linking user UUIDs to their respective {@link User} objects
     * @throws CustomException if the user service responds with an error
     */

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

    /**
     * Orchestrates the creation or linking of user identities for all garbage accounts in a request.
     *
     * <p>The process follows these steps:
     * <ol>
     *   <li>Flattens parent and child garbage accounts into a single processing list.</li>
     *   <li>Processes the accounts in batches to mitigate potential performance or timeout issues.</li>
     *   <li>Validates the phone number and username for each account.</li>
     *   <li>Delegates valid accounts to {@link #processGarbageAccount} for identity resolution.</li>
     * </ol>
     *
     * @param createGarbageRequest the request payload containing the garbage accounts
     * @return the updated request payload with user UUIDs assigned to the accounts
     */

    public GarbageAccountRequest createUser(GarbageAccountRequest createGarbageRequest) {
        RequestInfo requestInfo = createGarbageRequest.getRequestInfo();
        Role role = getCitizenRole();

        List<GarbageAccount> allGarbageAccounts = new ArrayList<>();

        // Step 1: Flatten parent and child garbage accounts into one list
        createGarbageRequest.getGarbageAccounts().forEach(garbageAccount -> {
            allGarbageAccounts.add(garbageAccount);
        });

        // Step 2: Process accounts in batches of 100 to avoid potential failures
        int batchSize = 100;
        for (int i = 0; i < allGarbageAccounts.size(); i += batchSize) {
            int end = Math.min(i + batchSize, allGarbageAccounts.size());
            List<GarbageAccount> batch = allGarbageAccounts.subList(i, end);
            for (GarbageAccount account : batch) {
                if (isValidPhoneNumber(account.getMobileNumber()) && isValidUserName(account.getName())) {
                    processGarbageAccount(requestInfo, role, account);
                }
            }
        }
        return createGarbageRequest;
    }


    /**
     * Validates a username against allowed enterprise patterns, rejecting disallowed special characters.
     *
     * @param name the username to validate
     * @return {@code true} if the name is valid; {@code false} otherwise
     */

    private boolean isValidUserName(String name) {
        if (name == null) return false;
        // Regex pattern: Disallow specified special characters
        String regex = "^[^\\\\$\\\"<>?\\\\\\\\~`!@#%^()+={}\\[\\]*,:;“”‘’]{1,49}$";
        return name.matches(regex);
    }

    /**
     * Validates a mobile phone number to ensure it adheres to a standard 10-digit format starting with 6-9.
     *
     * @param mobileNumber the mobile number to validate
     * @return {@code true} if the format is valid; {@code false} otherwise
     */

    private boolean isValidPhoneNumber(String mobileNumber) {

        String regex = "^[6-9]\\d{9}$";
        return mobileNumber != null && mobileNumber.matches(regex);

    }

    /**
     * Resolves the user identity for a specific garbage account, creating a new user if necessary.
     *
     * <p>The method follows these steps:
     * <ol>
     *   <li>Constructs an {@link OwnerInfo} payload from the garbage account details.</li>
     *   <li>Checks if a user already exists with the same mobile number in the given tenant context.</li>
     *   <li>If no matching user is found, provisions a new user via the eGov user service.</li>
     *   <li>Assigns the resolved or newly created user UUID back to the garbage account.</li>
     * </ol>
     *
     * @param requestInfo    the contextual information for the API request
     * @param role           the default role to assign (e.g., CITIZEN)
     * @param garbageAccount the garbage account being processed
     */

    public void processGarbageAccount(RequestInfo requestInfo, Role role, GarbageAccount garbageAccount) {
        OwnerInfo owner = createOwnerInfoFromAccount(garbageAccount);
        addUserDefaultFields(role, owner);

        // Check if the user already exists
        UserDetailResponse userDetailResponse = userExists(owner, requestInfo);
        List<OwnerInfo> existingUsersFromService = userDetailResponse.getUser();

        if (CollectionUtils.isEmpty(existingUsersFromService)) {
            // Create new user if not found
            owner.setUserName(UUID.randomUUID().toString());
            userDetailResponse = createUser(requestInfo, owner);
        }
//	        // Update existing user if found

        // Assign user UUID to the garbage account
        if (userDetailResponse != null && !CollectionUtils.isEmpty(userDetailResponse.getUser())
                && !StringUtils.isEmpty(userDetailResponse.getUser().get(0).getUuid())) {
            garbageAccount.setUserUuid(userDetailResponse.getUser().get(0).getUuid());
        }
    }

    /**
     * Extracts owner details from a garbage account into an {@link OwnerInfo} structure.
     *
     * @param garbageAccount the source garbage account
     * @return a populated {@link OwnerInfo} object
     */

    private OwnerInfo createOwnerInfoFromAccount(GarbageAccount garbageAccount) {
        String tenantId = garbageAccount.getTenantId();

        return OwnerInfo.builder()
                .mobileNumber(garbageAccount.getMobileNumber())
                .name(garbageAccount.getName())
                .tenantId(tenantId)
                .build();
    }

    /**
     * Determines whether to update an existing user or create a new one based on a mobile number match.
     *
     * @param existingUsersFromService a list of currently known users from the user service
     * @param requestInfo              the contextual information for the API request
     * @param role                     the default role to ensure is assigned
     * @param owner                    the target owner details
     */

    private void updateOrCreateUser(List<OwnerInfo> existingUsersFromService, RequestInfo requestInfo, Role role, OwnerInfo owner) {
        String mobileNumber = owner.getMobileNumber();
        List<OwnerInfo> existingUserWithMobile = findUserByMobile(existingUsersFromService, mobileNumber);

        if (!existingUserWithMobile.isEmpty()) {
            // Update existing user
            updateExistingUser(requestInfo, role, owner, existingUserWithMobile.get(0));
        } else {
            // Create new user if not found
            owner.setUserName(UUID.randomUUID().toString());
            createUser(requestInfo, owner);
        }
    }


    /**
     * Filters a list of users to find one matching the given mobile number.
     *
     * @param users        the list of users to search
     * @param mobileNumber the target mobile number
     * @return a list containing the matched users
     */

    private List<OwnerInfo> findUserByMobile(List<OwnerInfo> users, String mobileNumber) {
        return users.stream().filter(user -> mobileNumber.equals(user.getMobileNumber())).collect(Collectors.toList());
    }

    /**
     * Updates an existing user record with fresh owner details.
     *
     * @param requestInfo         the contextual information for the API request
     * @param role                the default role to assign
     * @param ownerFromRequest    the new owner details
     * @param ownerInfoFromSearch the existing user details from the database
     * @return a {@link UserDetailResponse} containing the updated user details
     */

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

    /**
     * Provisions a brand new user via the eGov user service.
     *
     * @param requestInfo the contextual information for the API request
     * @param owner       the details of the owner to create
     * @return a {@link UserDetailResponse} containing the newly created user details
     * @throws CustomException if the creation process fails
     */

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

    /**
     * Probes the user service to check if a user matching the owner's mobile number already exists.
     *
     * @param owner       the owner details containing the mobile number
     * @param requestInfo the contextual information for the API request
     * @return a {@link UserDetailResponse} with matching users, or empty if none exist
     */

    private UserDetailResponse userExists(OwnerInfo owner, RequestInfo requestInfo) {
        // If mobile number is not present, return an empty response to avoid invalid search
        if (StringUtils.isEmpty(owner.getMobileNumber())) {
            return new UserDetailResponse();
        }

        UserSearchRequest userSearchRequest = getBaseUserSearchRequest(owner.getTenantId(), requestInfo);
        userSearchRequest.setMobileNumber(owner.getMobileNumber());
        userSearchRequest.setUserType(owner.getType());


        StringBuilder uri = new StringBuilder(grbgConfig.getUserServiceHostUrl())
                .append(grbgConfig.getUserSearchEndpoint());
        return userCall(userSearchRequest, uri);
    }

    /**
     * Executes the REST call to the eGov user service and handles response mapping and date parsing.
     *
     * @param userRequest the payload to send
     * @param url         the destination URL
     * @return the mapped {@link UserDetailResponse}
     * @throws CustomException on parsing errors
     */

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

    /**
     * Normalizes date formats in the raw JSON response returned by the user service.
     *
     * @param responeMap the raw response map
     * @param dobFormat  the expected format for date of birth fields
     */

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
     * Converts a formatted date string into an epoch timestamp in milliseconds.
     *
     * @param date   the formatted date string
     * @param format the pattern to parse against
     * @return the epoch timestamp, or null if parsing fails
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
     * Constructs a baseline user search request targeting active citizens for a specific tenant.
     *
     * @param tenantId    the tenant ID to search within
     * @param requestInfo the contextual information for the API request
     * @return a pre-populated {@link UserSearchRequest}
     */

    public UserSearchRequest getBaseUserSearchRequest(String tenantId, RequestInfo requestInfo) {

        return UserSearchRequest.builder().requestInfo(requestInfo).userType("CITIZEN").tenantId(tenantId).active(true)
                .build();
    }

    /**
     * Applies default roles and state flags to an owner payload prior to creation.
     *
     * @param role  the role to assign (e.g., CITIZEN)
     * @param owner the owner object to mutate
     */

    private void addUserDefaultFields(Role role, OwnerInfo owner) {

        owner.setActive(true);
        owner.setRoles(Collections.singletonList(role));
        owner.setType("CITIZEN");
        owner.setCreatedDate(null);
        owner.setCreatedBy(null);
        owner.setLastModifiedDate(null);
        owner.setLastModifiedBy(null);
    }

    /**
     * Generates a default CITIZEN role definition.
     *
     * @return the {@link Role} object for a standard citizen
     */

    private Role getCitizenRole() {

        return Role.builder().code("CITIZEN").name("Citizen").build();
    }

    /**
     * Executes a user search and directly returns the list of matching owner records.
     *
     * @param userSearchRequest the search criteria
     * @return a {@link List} of {@link OwnerInfo} matching the criteria
     */

    public List<OwnerInfo> userSearch(UserSearchRequest userSearchRequest) {

        StringBuilder uri = new StringBuilder(grbgConfig.getUserServiceHostUrl())
                .append(grbgConfig.getUserSearchEndpoint());
        return userCall(userSearchRequest, uri).getUser();
    }

}