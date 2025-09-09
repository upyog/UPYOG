package org.egov.user.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.egov.user.domain.exception.*;
import org.egov.user.domain.model.*;
import org.egov.user.domain.model.enums.AddressType;
import org.egov.user.domain.model.enums.UserType;
import org.egov.user.domain.service.utils.EncryptionDecryptionUtil;
import org.egov.user.domain.service.utils.NotificationUtil;
import org.egov.user.domain.service.utils.UserConstants;
import org.egov.user.persistence.dto.FailedLoginAttempt;
import org.egov.user.persistence.repository.AddressRepository;
import org.egov.user.persistence.repository.FileStoreRepository;
import org.egov.user.persistence.repository.OtpRepository;
import org.egov.user.persistence.repository.UserRepository;
import org.egov.user.web.contract.Otp;
import org.egov.user.web.contract.OtpValidateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.egov.user.config.UserServiceConstants.USER_CLIENT_ID;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@Slf4j
public class UserService {

    private UserRepository userRepository;
    private OtpRepository otpRepository;
    private PasswordEncoder passwordEncoder;
    private int defaultPasswordExpiryInDays;
    private boolean isCitizenLoginOtpBased;
    private boolean isEmployeeLoginOtpBased;
    private FileStoreRepository fileRepository;
    private EncryptionDecryptionUtil encryptionDecryptionUtil;
    private TokenStore tokenStore;
    private AddressRepository addressRepository;

    @Value("${egov.user.host}")
    private String userHost;

    @Value("${account.unlock.cool.down.period.minutes}")
    private Long accountUnlockCoolDownPeriod;

    @Value("${max.invalid.login.attempts.period.minutes}")
    private Long maxInvalidLoginAttemptsPeriod;

    @Value("${create.user.validate.name}")
    private boolean createUserValidateName;

    @Value("${max.invalid.login.attempts}")
    private Long maxInvalidLoginAttempts;

    @Value("${digilocker.search}")
    private boolean isDigiLockerSearch;

    @Value("${egov.user.pwd.pattern}")
    private String pwdRegex;

    @Value("${egov.user.pwd.pattern.min.length}")
    private Integer pwdMinLength;

    @Value("${egov.user.pwd.pattern.max.length}")
    private Integer pwdMaxLength;

    @Value("${user.address.mandatory.fields.enabled}")
    private boolean addressMandatoryFieldsEnabled;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private NotificationUtil notificationUtil;

    public UserService(UserRepository userRepository, OtpRepository otpRepository, FileStoreRepository fileRepository,
                       PasswordEncoder passwordEncoder, EncryptionDecryptionUtil encryptionDecryptionUtil, TokenStore tokenStore,
                       @Value("${default.password.expiry.in.days}") int defaultPasswordExpiryInDays,
                       @Value("${citizen.login.password.otp.enabled}") boolean isCitizenLoginOtpBased,
                       @Value("${employee.login.password.otp.enabled}") boolean isEmployeeLoginOtpBased,
                       @Value("${egov.user.pwd.pattern}") String pwdRegex,
                       @Value("${egov.user.pwd.pattern.max.length}") Integer pwdMaxLength,
                       @Value("${egov.user.pwd.pattern.min.length}") Integer pwdMinLength, AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
        this.defaultPasswordExpiryInDays = defaultPasswordExpiryInDays;
        this.isCitizenLoginOtpBased = isCitizenLoginOtpBased;
        this.isEmployeeLoginOtpBased = isEmployeeLoginOtpBased;
        this.fileRepository = fileRepository;
        this.encryptionDecryptionUtil = encryptionDecryptionUtil;
        this.tokenStore = tokenStore;
        this.pwdRegex = pwdRegex;
        this.pwdMaxLength = pwdMaxLength;
        this.pwdMinLength = pwdMinLength;
        this.addressRepository = addressRepository;
    }

    /**
     * get user By UserName And TenantId
     *
     * @param userName
     * @param tenantId
     * @return
     */
    public User getUniqueUser(String userName, String tenantId, UserType userType) {

        UserSearchCriteria userSearchCriteria = UserSearchCriteria.builder()
                .userName(userName)
                .tenantId(getStateLevelTenantForCitizen(tenantId, userType))
                .type(userType)
                .build();

        if (isEmpty(userName) || isEmpty(tenantId) || isNull(userType)) {
            log.error("Invalid lookup, mandatory fields are absent");
            throw new UserNotFoundException(userSearchCriteria);
        }

        /* encrypt here */

        userSearchCriteria = encryptionDecryptionUtil.encryptObject(userSearchCriteria, "User", UserSearchCriteria.class);
        List<User> users = userRepository.findAll(userSearchCriteria);

        if (users.isEmpty())
            throw new UserNotFoundException(userSearchCriteria);
        if (users.size() > 1)
            throw new DuplicateUserNameException(userSearchCriteria);

        return users.get(0);
    }

    public User getUserByUuid(String uuid) {

        UserSearchCriteria userSearchCriteria = UserSearchCriteria.builder()
                .uuid(Collections.singletonList(uuid))
                .build();

        if (isEmpty(uuid)) {
            log.error("UUID is mandatory");
            throw new UserNotFoundException(userSearchCriteria);
        }

        List<User> users = userRepository.findAll(userSearchCriteria);

        if (users.isEmpty())
            throw new UserNotFoundException(userSearchCriteria);
        return users.get(0);
    }

    public User getUserBymobileNumber(String mobileNumber) {

        UserSearchCriteria userSearchCriteria = UserSearchCriteria.builder()
                .mobileNumber(mobileNumber)
                .build();

        if (isEmpty(mobileNumber)) {
            log.error("Mobile Number is mandatory");
            throw new UserNotFoundException(userSearchCriteria);
        }
        //userSearchCriteria.setDigilockersearch(isDigiLockerSearch);
        List<User> users = userRepository.findAll(userSearchCriteria);

        if (users.isEmpty())
            throw new UserNotFoundException(userSearchCriteria);
        return users.get(0);
    }


    /**
     * get the users based on on userSearch criteria
     *
     * @param searchCriteria
     * @return
     */

    public List<org.egov.user.domain.model.User> searchUsers(UserSearchCriteria searchCriteria,
                                                             boolean isInterServiceCall, RequestInfo requestInfo) {

        searchCriteria.validate(isInterServiceCall);

        searchCriteria.setTenantId(getStateLevelTenantForCitizen(searchCriteria.getTenantId(), searchCriteria.getType()));

            String altmobnumber = null;

            if (searchCriteria.getMobileNumber() != null) {
                altmobnumber = searchCriteria.getMobileNumber();
            }

            searchCriteria = encryptionDecryptionUtil.encryptObject(searchCriteria, "User", UserSearchCriteria.class);

            if (altmobnumber != null) {
                searchCriteria.setAlternatemobilenumber(altmobnumber);
            }
        log.info("Search Criteria :-"+ searchCriteria);
        List<org.egov.user.domain.model.User> list = userRepository.findAll(searchCriteria);

        if (list.isEmpty()) {
            return Collections.emptyList();
        }

        /* decrypt here / final reponse decrypted*/

        list = encryptionDecryptionUtil.decryptObject(list, null, User.class, requestInfo);

        //setFileStoreUrlsByFileStoreIds(list);
        return list;
    }

    /**
     * api will create the user based on some validations
     *
     * @param user
     * @return
     */
    public User createUser(User user, RequestInfo requestInfo) {
        user.setUuid(UUID.randomUUID().toString());
        user.validateNewUser(createUserValidateName);
        conditionallyValidateOtp(user);
        /* encrypt here */
        user = encryptionDecryptionUtil.encryptObject(user, "User", User.class);
        validateUserUniqueness(user);
        if (isEmpty(user.getPassword())) {
            user.setPassword(UUID.randomUUID().toString());
        } else {
            validatePassword(user.getPassword());
        }
        user.setPassword(encryptPwd(user.getPassword()));
        user.setDefaultPasswordExpiry(defaultPasswordExpiryInDays);
        user.setTenantId(getStateLevelTenantForCitizen(user.getTenantId(), user.getType()));
        User persistedNewUser = persistNewUser(user);
        return encryptionDecryptionUtil.decryptObject(persistedNewUser, "UserSelf", User.class, requestInfo);

        /* decrypt here  because encrypted data coming from DB*/

    }

    private void validateUserUniqueness(User user) {
        if (userRepository.isUserPresent(user.getUsername(), getStateLevelTenantForCitizen(user.getTenantId(), user
                .getType()), user.getType()))
            throw new DuplicateUserNameException(UserSearchCriteria.builder().userName(user.getUsername()).type(user
                    .getType()).tenantId(user.getTenantId()).build());
    }

    private String getStateLevelTenantForCitizen(String tenantId, UserType userType) {
        if (!isNull(userType) && userType.equals(UserType.CITIZEN) && !isEmpty(tenantId) && tenantId.contains("."))
            return tenantId.split("\\.")[0];
        else
            return tenantId;
    }

    /**
     * api will create the citizen with otp
     *
     * @param user
     * @return
     */
    public User createCitizen(User user, RequestInfo requestInfo) {
        validateAndEnrichCitizen(user);
        return createUser(user, requestInfo);
    }


    private void validateAndEnrichCitizen(User user) {
        log.info("Validating User........");
        if (isCitizenLoginOtpBased && !StringUtils.isNumeric(user.getUsername()))
            throw new UserNameNotValidException();
        else if (isCitizenLoginOtpBased)
            user.setMobileNumber(user.getUsername());
        else if(!isCitizenLoginOtpBased && user.isDigilockerRegistration())
            user.setMobileNumber(user.getUsername());
        if (!isCitizenLoginOtpBased)
            validatePassword(user.getPassword());
        user.setRoleToCitizen();
        user.setTenantId(getStateLevelTenantForCitizen(user.getTenantId(), user.getType()));
    }

    /**
     * api will create the citizen with otp
     *
     * @param user
     * @return
     */
    public Object registerWithLogin(User user, RequestInfo requestInfo) {
        user.setActive(true);
        createCitizen(user, requestInfo);
        return getAccess(user, user.getOtpReference());
    }

    private Object getAccess(User user, String password) {
        log.info("Fetch access token for register with login flow");
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "Basic ZWdvdi11c2VyLWNsaWVudDo=");
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("username", user.getUsername());
            if (!isEmpty(password))
                map.add("password", password);
            else
                map.add("password", user.getPassword());
            map.add("grant_type", "password");
            map.add("scope", "read");
            map.add("tenantId", user.getTenantId());
            map.add("isInternal", "true");
            map.add("userType", UserType.CITIZEN.name());

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map,
                    headers);
            return restTemplate.postForEntity(userHost + "/user/oauth/token", request, Map.class).getBody();

        } catch (Exception e) {
            log.error("Error occurred while logging-in via register flow", e);
            throw new CustomException("LOGIN_ERROR", "Error occurred while logging in via register flow: " + e.getMessage());
        }
    }

    /**
     * dependent on otpValidationMandatory filed,it will validate the otp.
     *
     * @param user
     */
    private void conditionallyValidateOtp(User user) {
        if (user.isOtpValidationMandatory()) {
            if (!validateOtp(user))
                throw new OtpValidationPendingException();
        }
    }

    /**
     * This api will validate the otp
     *
     * @param user
     * @return
     */
    public Boolean validateOtp(User user) {
        Otp otp = Otp.builder().otp(user.getOtpReference()).identity(user.getMobileNumber()).tenantId(user.getTenantId())
                .userType(user.getType()).build();
        RequestInfo requestInfo = RequestInfo.builder().action("validate").ts(System.currentTimeMillis()).build();
        OtpValidateRequest otpValidationRequest = OtpValidateRequest.builder().requestInfo(requestInfo).otp(otp)
                .build();
        return otpRepository.validateOtp(otpValidationRequest);

    }


    /**
     * api will update user details without otp
     *
     * @param user
     * @return
     */
    // TODO Fix date formats
    public User updateWithoutOtpValidation(User user, RequestInfo requestInfo) {
        
         	User existingUser = getUserByUuid(user.getUuid());
            user.setTenantId(getStateLevelTenantForCitizen(user.getTenantId(), user.getType()));
            validateUserRoles(user);
            user.validateUserModification();
            validatePassword(user.getPassword());
            user.setPassword(encryptPwd(user.getPassword()));
            user = encryptionDecryptionUtil.encryptObject(user, "User", User.class);
            userRepository.update(user, existingUser,requestInfo.getUserInfo().getId(), requestInfo.getUserInfo().getUuid() );
        /* encrypt */


        // If user is being unlocked via update, reset failed login attempts
        if (user.getAccountLocked() != null && !user.getAccountLocked() && existingUser.getAccountLocked())
            resetFailedLoginAttempts(user);

        User encryptedUpdatedUserfromDB, decryptedupdatedUserfromDB;

            encryptedUpdatedUserfromDB = getUserByUuid(user.getUuid());
            decryptedupdatedUserfromDB = encryptionDecryptionUtil.decryptObject(encryptedUpdatedUserfromDB, "UserSelf", User.class, requestInfo);
            return decryptedupdatedUserfromDB;
    }

    public void removeTokensByUser(User user) {
        Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientIdAndUserName(USER_CLIENT_ID,
                user.getUsername());

        for (OAuth2AccessToken token : tokens) {
            if (token.getAdditionalInformation() != null && token.getAdditionalInformation().containsKey("UserRequest")) {
                if (token.getAdditionalInformation().get("UserRequest") instanceof org.egov.user.web.contract.auth.User) {
                    org.egov.user.web.contract.auth.User userInfo =
                            (org.egov.user.web.contract.auth.User) token.getAdditionalInformation().get(
                                    "UserRequest");
                    if (user.getUsername().equalsIgnoreCase(userInfo.getUserName()) && user.getTenantId().equalsIgnoreCase(userInfo.getTenantId())
                            && user.getType().equals(UserType.fromValue(userInfo.getType())))
                        tokenStore.removeAccessToken(token);
                }
            }
        }

    }

    /**
     * this api will validate whether user roles exist in Database or not
     *
     * @param user
     */
    private void validateUserRoles(User user) {
        if (user.getRoles() == null || user.getRoles() != null && user.getRoles().isEmpty()) {
            throw new AtleastOneRoleCodeException();
        }
    }

    /**
     * this api will update user profile data except these fields userName ,
     * mobileNumber type , password ,pwsExpiryData, roles
     *
     * @param user
     * @return
     */
    public User partialUpdate(User user, RequestInfo requestInfo) {
        /* encrypt here */
        user = encryptionDecryptionUtil.encryptObject(user, "User", User.class);

        User existingUser = getUserByUuid(user.getUuid());
        validateProfileUpdateIsDoneByTheSameLoggedInUser(user);
        user.nullifySensitiveFields();
        validatePassword(user.getPassword());
        userRepository.update(user, existingUser,requestInfo.getUserInfo().getId(), requestInfo.getUserInfo().getUuid() );
        User updatedUser = getUserByUuid(user.getUuid());
        
        /* decrypt here */
        existingUser = encryptionDecryptionUtil.decryptObject(existingUser, "UserSelf", User.class, requestInfo);
        updatedUser = encryptionDecryptionUtil.decryptObject(updatedUser, "UserSelf", User.class, requestInfo);

        //setFileStoreUrlsByFileStoreIds(Collections.singletonList(updatedUser));
        String oldEmail = existingUser.getEmailId();
        String newEmail = updatedUser.getEmailId();
        if((oldEmail != null && !oldEmail.isEmpty()) && newEmail != null && !(newEmail.equalsIgnoreCase(oldEmail))) {
            // Sending sms and email to old email to notify that email has been changed
            notificationUtil.sendEmail(requestInfo, existingUser, updatedUser);
        }
        return updatedUser;
    }

    /**
     * This api will update the password for logged-in user
     *
     * @param updatePasswordRequest
     */
    public void updatePasswordForLoggedInUser(LoggedInUserUpdatePasswordRequest updatePasswordRequest) {
        updatePasswordRequest.validate();
        final User user = getUniqueUser(updatePasswordRequest.getUserName(), updatePasswordRequest.getTenantId(),
                updatePasswordRequest.getType());

        if (user.getType().toString().equals(UserType.CITIZEN.toString()) && isCitizenLoginOtpBased)
            throw new InvalidUpdatePasswordRequestException();
        if (user.getType().toString().equals(UserType.EMPLOYEE.toString()) && isEmployeeLoginOtpBased)
            throw new InvalidUpdatePasswordRequestException();

        validateExistingPassword(user, updatePasswordRequest.getExistingPassword());
        validatePassword(updatePasswordRequest.getNewPassword());
        user.updatePassword(encryptPwd(updatePasswordRequest.getNewPassword()));
        userRepository.update(user, user, user.getId() , user.getUuid());
    }

    /**
     * This Api will update the password for non logged-in user
     *
     * @param request
     */
    public void updatePasswordForNonLoggedInUser(NonLoggedInUserUpdatePasswordRequest request, RequestInfo requestInfo) {
        request.validate();
        // validateOtp(request.getOtpValidationRequest());
        User user = getUniqueUser(request.getUserName(), request.getTenantId(), request.getType());
        if (user.getType().toString().equals(UserType.CITIZEN.toString()) && isCitizenLoginOtpBased) {
            log.info("CITIZEN forgot password flow is disabled");
            throw new InvalidUpdatePasswordRequestException();
        }
        if (user.getType().toString().equals(UserType.EMPLOYEE.toString()) && isEmployeeLoginOtpBased) {
            log.info("EMPLOYEE forgot password flow is disabled");
            throw new InvalidUpdatePasswordRequestException();
        }
        /* decrypt here */
        /* the reason for decryption here is the otp service requires decrypted username */
        user = encryptionDecryptionUtil.decryptObject(user, "User", User.class, requestInfo);
        user.setOtpReference(request.getOtpReference());
        validateOtp(user);
        validatePassword(request.getNewPassword());
        user.updatePassword(encryptPwd(request.getNewPassword()));
        /* encrypt here */
        /* encrypted value is stored in DB*/
        user = encryptionDecryptionUtil.encryptObject(user, "User", User.class);
        userRepository.update(user, user, requestInfo.getUserInfo().getId(), requestInfo.getUserInfo().getUuid());
    }


    /**
     * Deactivate failed login attempts for provided user
     *
     * @param user whose failed login attempts are to be reset
     */
    public void resetFailedLoginAttempts(User user) {
        if (user.getUuid() != null)
            userRepository.resetFailedLoginAttemptsForUser(user.getUuid());
    }

    /**
     * Checks if user is eligible for unlock
     * returns true,
     * - If configured cool down period has passed since last lock
     * else false
     *
     * @param user to be checked for eligibility for unlock
     * @return if unlock able
     */
    public boolean isAccountUnlockAble(User user) {
        if (user.getAccountLocked()) {
            boolean unlockAble =
                    System.currentTimeMillis() - user.getAccountLockedDate() > TimeUnit.MINUTES.toMillis(accountUnlockCoolDownPeriod);

            log.info("Account eligible for unlock - " + unlockAble);
            log.info("Current time {}, last lock time {} , cool down period {} ", System.currentTimeMillis(),
                    user.getAccountLockedDate(), TimeUnit.MINUTES.toMillis(accountUnlockCoolDownPeriod));
            return unlockAble;
        } else
            return true;
    }

    /**
     * Perform actions where a user login fails
     * - Fetch existing failed login attempts within configured time
     * period{@link UserService#maxInvalidLoginAttemptsPeriod}
     * - If failed login attempts exceeds configured {@link UserService#maxInvalidLoginAttempts}
     * - then lock account
     * - Add failed login attempt entry to repository
     *
     * @param user      user whose failed login attempt to be handled
     * @param ipAddress IP address of remote
     */
    public void handleFailedLogin(User user, String ipAddress, RequestInfo requestInfo) {
        if (!Objects.isNull(user.getUuid())) {
            List<FailedLoginAttempt> failedLoginAttempts =
                    userRepository.fetchFailedAttemptsByUserAndTime(user.getUuid(),
                            System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(maxInvalidLoginAttemptsPeriod));

            if (failedLoginAttempts.size() + 1 >= maxInvalidLoginAttempts) {
                User userToBeUpdated = user.toBuilder()
                        .accountLocked(true)
                        .password(null)
                        .accountLockedDate(System.currentTimeMillis())
                        .build();

                user = updateWithoutOtpValidation(userToBeUpdated, requestInfo);
                removeTokensByUser(user);
                log.info("Locked account with uuid {} for {} minutes as exceeded max allowed attempts of {} within {} " +
                                "minutes",
                        user.getUuid(), accountUnlockCoolDownPeriod, maxInvalidLoginAttempts, maxInvalidLoginAttemptsPeriod);
                throw new OAuth2Exception("Account locked");
            }

            userRepository.insertFailedLoginAttempt(new FailedLoginAttempt(user.getUuid(), ipAddress,
                    System.currentTimeMillis(), true));
        }
    }


    /**
     * This api will validate existing password and current password matching or
     * not
     *
     * @param user
     * @param existingRawPassword
     */
    private void validateExistingPassword(User user, String existingRawPassword) {
        if (!passwordEncoder.matches(existingRawPassword, user.getPassword())) {
            throw new PasswordMismatchException("Invalid username or password");
        }
    }

//    /**
//     * this api will check user is exist or not, If not exist it will throw
//     * exception.
//     *
//     * @param user
//     */
//    private void validateUserPresent(User user) {
//        if (user == null) {
//            throw new UserNotFoundException(null);
//        }
//    }

    /**
     * this api will validate, updating the profile for same logged-in user or
     * not
     *
     * @param user
     */
    private void validateProfileUpdateIsDoneByTheSameLoggedInUser(User user) {
        if (user.isLoggedInUserDifferentFromUpdatedUser()) {
            throw new UserProfileUpdateDeniedException();
        }
    }


    String encryptPwd(String pwd) {
        if (!isNull(pwd))
            return passwordEncoder.encode(pwd);
        else
            return null;
    }

    /**
     * This api will persist the user
     *
     * @param user
     * @return
     */
    private User persistNewUser(User user) {

        return userRepository.create(user);
    }

    /**
     * This api will fetch the fileStoreUrl By fileStoreId
     *
     * @param userList
     * @throws Exception
     */
    private void setFileStoreUrlsByFileStoreIds(List<User> userList) {
        List<String> fileStoreIds = userList.parallelStream().filter(p -> p.getPhoto() != null).map(User::getPhoto)
                .collect(Collectors.toList());
        if (!isEmpty(fileStoreIds)) {
            Map<String, String> fileStoreUrlList = null;
            try {
                fileStoreUrlList = fileRepository.getUrlByFileStoreId(userList.get(0).getTenantId(), fileStoreIds);
            } catch (Exception e) {
                // TODO Auto-generated catch block

                log.error("Error while fetching fileStore url list: " + e.getMessage());
            }

            if (fileStoreUrlList != null && !fileStoreUrlList.isEmpty()) {
                for (User user : userList) {
                    user.setPhoto(fileStoreUrlList.get(user.getPhoto()));
                }
            }
        }
    }


    public void validatePassword(String password) {
        Map<String, String> errorMap = new HashMap<>();
        if (!StringUtils.isEmpty(password)) {
            if (password.length() < pwdMinLength || password.length() > pwdMaxLength)
                errorMap.put("INVALID_PWD_LENGTH", "Password must be of minimum: " + pwdMinLength + " and maximum: " + pwdMaxLength + " characters.");
            Pattern p = Pattern.compile(pwdRegex);
            Matcher m = p.matcher(password);
            if (!m.find()) {
                errorMap.put("INVALID_PWD_PATTERN", "Password MUST HAVE: Atleast one digit, one upper case, one lower case, one special character (@#$%) and MUST NOT contain any spaces");
            }
        }
        if (!CollectionUtils.isEmpty(errorMap.keySet())) {
            throw new CustomException(errorMap);
        }
    }
    
    public Object updateDigilockerID(User user, User existingUser, RequestInfo requestInfo) {        
    	if(existingUser.getDigilockerid() != null && !existingUser.getDigilockerid().equals(user.getDigilockerid())){
            throw new IllegalArgumentException("Digilocker Id provided does not match the Digilocker Id in the database for the user");
        }
    	else if (existingUser.getDigilockerid()!=null) {
        	return getAccess(user, user.getOtpReference());
        	}
    	else {
    	user = encryptionDecryptionUtil.encryptObject(user, "User", User.class);        
    	userRepository.update(user, existingUser, existingUser.getId(), existingUser.getUuid()); 
    	user = encryptionDecryptionUtil.decryptObject(user, null, User.class, requestInfo);
    	//user = decryptionDecryptionUtil.decryptObject(user, "User", User.class);        
    	return getAccess(user, user.getOtpReference());
    	}
    }    /**
     * Creates an address entry for the given user based on the provided UUID.
     * It first retrieves the user ID from the database using the UUID.
     * If the UUID is invalid (i.e., no user found), an exception is thrown.
     * Then, the address is stored in the database using the user ID.
     *
     * @param userUuid The UUID of the user for whom the address is being created.
     * @param address  The Address object containing address details.
     * @return The saved Address object after insertion.
     * @throws IllegalArgumentException If the provided user UUID is not valid.
     */
    public Address createAddress(String userUuid, Address address) {
        Long userId = userRepository.getUserIdByUuid(userUuid);
        if (userId == null) {
            throw new IllegalArgumentException("USER_UUID_NOT_VALID: The provided user UUID:"+userUuid+" is not valid");
        }
        
        // Validate mandatory fields if enabled
        if (addressMandatoryFieldsEnabled && address.isMandatoryFieldsMissing(addressMandatoryFieldsEnabled)) {
            log.error("Address validation failed - mandatory fields missing for address creation for user UUID: {}", userUuid);
            throw new CustomException("ADDRESS_VALIDATION_ERROR", "City, pincode, and address are mandatory fields for address creation");
        }
        
        // Check if Permanent or Correspondence address already exists and not Other category as Other can be created multiple times
        if (AddressType.PERMANENT == address.getType() || AddressType.CORRESPONDENCE == address.getType()) {
            AddressSearchCriteria addressSearchCriteria = AddressSearchCriteria.builder()
                    .userId(userId)
                    .addressType(address.getType())
                    .status(UserConstants.ADDRESS_ACTIVE_STATUS)
                    .build();
            List<Address> existingAddresses = addressRepository.getAddressV2(addressSearchCriteria);
            if (!existingAddresses.isEmpty()) {
                throw new IllegalArgumentException("An address of type " + address.getType() + " already exists for user ID: " + userUuid);
            }
        }
        // Encrypt address before saving
        address = encryptionDecryptionUtil.encryptObject(address, UserConstants.USER_ADDRESS_ENCRYPTION_KEY, Address.class);
        Address savedAddress = addressRepository.createAddressV2(address, userId, address.getTenantId());
        // Decrypt address before returning
        return encryptionDecryptionUtil.decryptObject(savedAddress, UserConstants.USER_ADDRESS_ENCRYPTION_KEY, Address.class, null);
    }

    /**
     * This method fetches the address objects based on user_uuid and tenantId
     *
     * @param user_uuid
     * @param tenantId
     */
    public List<Address> getAddress(String user_uuid, String tenantId) {

        AddressSearchCriteria addressSearchCriteria = AddressSearchCriteria.builder()
                .userUuid(user_uuid)
                .tenantId(tenantId)
                .status(UserConstants.ADDRESS_ACTIVE_STATUS)
                .build();
        List<Address> addressList = addressRepository.getAddressV2(addressSearchCriteria);
        //if addressList is empty, return empty list
        if (addressList.isEmpty()) {
            return Collections.emptyList();
        }
        // Decrypt addresses before returning
        return encryptionDecryptionUtil.decryptObject(addressList, UserConstants.USER_ADDRESS_ENCRYPTION_KEY, Address.class, null);
    }

    /**
     * Updates an existing address based on the provided address ID.
     *
     * @param address   The updated address details.
     * @return List of updated addresses.
     * @throws IllegalArgumentException if the address ID does not exist in the database.
     */
    public Address updateAddress(Address address) {

        // Validate mandatory fields if enabled
        if (addressMandatoryFieldsEnabled && address.isMandatoryFieldsMissing(addressMandatoryFieldsEnabled)) {
            log.error("Address validation failed - mandatory fields missing for address update with ID: {}", address.getId());
            throw new CustomException("ADDRESS_VALIDATION_ERROR", "City, pincode, and address are mandatory fields for address creation");
        }

        AddressSearchCriteria addressSearchCriteria = AddressSearchCriteria.builder()
                .id(address.getId())
                .status(UserConstants.ADDRESS_ACTIVE_STATUS)
                .build();
        Address existingAddress = addressRepository.getAddressV2(addressSearchCriteria).get(0);
        if (existingAddress == null) {
            throw new IllegalArgumentException("ADDRESS_NOT_VALID: Address ID " + address.getId() + " does not exist.");
        }
        // Encrypt address before updating
        address = encryptionDecryptionUtil.encryptObject(address, UserConstants.USER_ADDRESS_ENCRYPTION_KEY, Address.class);
        // Update the old address status to inactive
        addressRepository.updateAddressV2(address.getId(), address.getUserId(), UserConstants.ADDRESS_INACTIVE_STATUS);
        // Create a new address entry with the updated details with the same user id
        Address savedAddress = addressRepository.createAddressV2(address, address.getUserId(), address.getTenantId());
        // Decrypt address before returning
        return encryptionDecryptionUtil.decryptObject(savedAddress, UserConstants.USER_ADDRESS_ENCRYPTION_KEY, Address.class, null);
    }



    /**
     * Creates a new user with address details after performing validations and encryption.
     * Steps:
     * 1. Generate a UUID and validate the user.
     * 2. Validate OTP conditionally.
     * 3. Encrypt the user object.
     * 4. Ensure user uniqueness.
     * 5. Handle password (generate if absent, validate otherwise).
     * 6. Encrypt password and set default expiry.
     * 7. Adjust tenant ID to a state-level tenant.
     * 8. Persist the user in the database.
     * 9. Decrypt before returning the user object.
     *
     * @param user        the user object containing new user details
     * @param requestInfo metadata containing request-related details
     * @return the created and decrypted User object
     */
    public User createUserWithAddressV2(User user, RequestInfo requestInfo) {

        user.setUuid(UUID.randomUUID().toString());

        // Validate user details, including name, mobile number, and addresses
        user.validateNewUser(createUserValidateName);
        conditionallyValidateOtp(user);
        user = encryptionDecryptionUtil.encryptObject(user, "User", User.class);

        // Ensure the user does not already exist
        validateUserUniqueness(user);
        // Handle password: if not provided, generate a random one; otherwise, validate it
        if (isEmpty(user.getPassword())) {
            user.setPassword(UUID.randomUUID().toString());
        } else {
            validatePassword(user.getPassword());
        }

        user.setPassword(encryptPwd(user.getPassword()));
        user.setDefaultPasswordExpiry(defaultPasswordExpiryInDays);
        user.setTenantId(getStateLevelTenantForCitizen(user.getTenantId(), user.getType()));

        // Persist the validated and encrypted user in the database
        User persistedNewUser = persistNewUserWithAddressV2(user);
        // Decrypt the persisted user before returning the response
        return encryptionDecryptionUtil.decryptObject(persistedNewUser, "UserSelf", User.class, requestInfo);
    }

    /**
     * Persists a new user with address details in the database.
     *
     * @param user the validated and encrypted user object
     * @return the persisted User object
     */
    private User persistNewUserWithAddressV2(User user) {
        return userRepository.createWithAddressV2(user);
    }


    /**
     * Updates user details along with address V2 information without requiring OTP verification.
     *
     * This method fetches the existing user details, applies validations, encrypts sensitive
     * information, and updates the user record in the database. It also resets failed login attempts if
     * the account was previously locked and is now being unlocked.
     *
     * @param user The user object containing updated details.
     * @param requestInfo Request information containing user metadata.
     * @return The updated user object with decrypted details.
     */
    public User updateUserV2(User user, RequestInfo requestInfo) {
        final User existingUser = getUserByUuid(user.getUuid());
        user.setTenantId(getStateLevelTenantForCitizen(user.getTenantId(), user.getType()));
        validateUserRoles(user);
        user.validateUserModification();
        validatePassword(user.getPassword());
        user.setPassword(encryptPwd(user.getPassword()));
        /* encrypt */
        user = encryptionDecryptionUtil.encryptObject(user, "User", User.class);
        userRepository.updateV2(user, existingUser, requestInfo.getUserInfo().getId(), requestInfo.getUserInfo().getUuid());

        // If user is being unlocked via update, reset failed login attempts
        if (user.getAccountLocked() != null && !user.getAccountLocked() && existingUser.getAccountLocked())
            resetFailedLoginAttempts(user);

        User encryptedUpdatedUserfromDB = getUserByUuid(user.getUuid());
        User decryptedupdatedUserfromDB = encryptionDecryptionUtil.decryptObject(encryptedUpdatedUserfromDB, "UserSelf", User.class, requestInfo);
        return decryptedupdatedUserfromDB;
    }

    /**
     * Searches for users based on the provided criteria.
     *
     * This method validates the search criteria, applies encryption for secure processing,
     * performs a user search using the repository, decrypts the retrieved user data,
     * and sets file store URLs for any associated files before returning the final user list.
     *
     * @param searchCriteria    The criteria used to filter users.
     * @param isInterServiceCall A flag indicating whether the call is from another service.
     * @param requestInfo       Metadata about the request, used for decryption.
     * @return A list of users matching the search criteria.
     */
    public List<org.egov.user.domain.model.User>  searchUsersV2(UserSearchCriteria searchCriteria,
                                                               boolean isInterServiceCall, RequestInfo requestInfo) {

        searchCriteria.validate(isInterServiceCall);

        searchCriteria.setTenantId(getStateLevelTenantForCitizen(searchCriteria.getTenantId(), searchCriteria.getType()));
        String altmobnumber = null;

        if (searchCriteria.getMobileNumber() != null) {
            altmobnumber = searchCriteria.getMobileNumber();
        }
        /* encrypt here / encrypted searchcriteria will be used for search*/
        searchCriteria = encryptionDecryptionUtil.encryptObject(searchCriteria, "User", UserSearchCriteria.class);

        if (altmobnumber != null) {
            searchCriteria.setAlternatemobilenumber(altmobnumber);
        }

        List<org.egov.user.domain.model.User> list = userRepository.findAllV2(searchCriteria);

        // If list is null or empty, return an empty list
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }

        /* decrypt here / final reponse decrypted*/
        list = encryptionDecryptionUtil.decryptObject(list, "User", User.class, requestInfo);

        setFileStoreUrlsByFileStoreIds(list);
        return list;
    }
}
