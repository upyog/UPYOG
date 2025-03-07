package org.egov.user.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpRequest;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.tracer.model.CustomException;
import org.egov.user.domain.exception.*;
import org.egov.user.domain.model.LoggedInUserUpdatePasswordRequest;
import org.egov.user.domain.model.NonLoggedInUserUpdatePasswordRequest;
import org.egov.user.domain.model.User;
import org.egov.user.domain.model.UserSearchCriteria;
import org.egov.user.domain.model.enums.UserType;
import org.egov.user.domain.service.utils.EncryptionDecryptionUtil;
import org.egov.user.domain.service.utils.NotificationUtil;
import org.egov.user.persistence.dto.FailedLoginAttempt;
import org.egov.user.persistence.dto.SessionDetails;
import org.egov.user.persistence.dto.UserLoginAttemptAudit;
import org.egov.user.persistence.repository.FileStoreRepository;
import org.egov.user.persistence.repository.OtpRepository;
import org.egov.user.persistence.repository.UserRepository;
import org.egov.user.web.contract.Captcha;
import org.egov.user.web.contract.CaptchaResponse;
import org.egov.user.web.contract.Otp;
import org.egov.user.web.contract.OtpValidateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationPid;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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

	@Value("${egov.user.pwd.pattern}")
	private String pwdRegex;

	@Value("${egov.user.pwd.pattern.min.length}")
	private Integer pwdMinLength;

	@Value("${egov.user.pwd.pattern.max.length}")
	private Integer pwdMaxLength;

	@Value("${secret.algorithm}")
	private String algorithm;

	@Value("${secret.transformation}")
	private String transformation;

	@Value("${secret.key}")
	private String key;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private NotificationUtil notificationUtil;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	public UserService(UserRepository userRepository, OtpRepository otpRepository, FileStoreRepository fileRepository,
			PasswordEncoder passwordEncoder, EncryptionDecryptionUtil encryptionDecryptionUtil, TokenStore tokenStore,
			@Value("${default.password.expiry.in.days}") int defaultPasswordExpiryInDays,
			@Value("${citizen.login.password.otp.enabled}") boolean isCitizenLoginOtpBased,
			@Value("${employee.login.password.otp.enabled}") boolean isEmployeeLoginOtpBased,
			@Value("${egov.user.pwd.pattern}") String pwdRegex,
			@Value("${egov.user.pwd.pattern.max.length}") Integer pwdMaxLength,
			@Value("${egov.user.pwd.pattern.min.length}") Integer pwdMinLength) {
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

	}

	/**
	 * get user By UserName And TenantId
	 *
	 * @param userName
	 * @param tenantId
	 * @return
	 */
	public User getUniqueUser(String userName, String tenantId, UserType userType) {

		UserSearchCriteria userSearchCriteria = UserSearchCriteria.builder().userName(userName)
				.tenantId(getStateLevelTenantForCitizen(tenantId, userType)).type(userType).build();

		if (isEmpty(userName) || isEmpty(tenantId) || isNull(userType)) {
			log.error("Invalid lookup, mandatory fields are absent");
			throw new UserNotFoundException(userSearchCriteria);
		}

		/* encrypt here */

		userSearchCriteria = encryptionDecryptionUtil.encryptObject(userSearchCriteria, "User",
				UserSearchCriteria.class);
		List<User> users = userRepository.findAll(userSearchCriteria);

		if (users.isEmpty())
			throw new UserNotFoundException(userSearchCriteria);
		if (users.size() > 1)
			throw new DuplicateUserNameException(userSearchCriteria);

		return users.get(0);
	}

	public User getUserByUuid(String uuid) {

		UserSearchCriteria userSearchCriteria = UserSearchCriteria.builder().uuid(Collections.singletonList(uuid))
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

	/**
	 * get the users based on on userSearch criteria
	 *
	 * @param searchCriteria
	 * @return
	 */

	public List<org.egov.user.domain.model.User> searchUsers(UserSearchCriteria searchCriteria,
			boolean isInterServiceCall, RequestInfo requestInfo) {

		searchCriteria.validate(isInterServiceCall);

		searchCriteria
				.setTenantId(getStateLevelTenantForCitizen(searchCriteria.getTenantId(), searchCriteria.getType()));
		/* encrypt here / encrypted searchcriteria will be used for search */

		String altmobnumber = null;

		if (searchCriteria.getMobileNumber() != null) {
			altmobnumber = searchCriteria.getMobileNumber();
		}

		searchCriteria = encryptionDecryptionUtil.encryptObject(searchCriteria, "User", UserSearchCriteria.class);

		if (altmobnumber != null) {
			searchCriteria.setAlternatemobilenumber(altmobnumber);
		}

		List<org.egov.user.domain.model.User> list = userRepository.findAll(searchCriteria);

		/* decrypt here / final reponse decrypted */

		list = encryptionDecryptionUtil.decryptObject(list, null, User.class, requestInfo);

		setFileStoreUrlsByFileStoreIds(list);
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
		if(user.isOtpValidationMandatory()&&!validateCaptcha(user.getCaptchaUuid(),user.getCaptcha())) {
			throw new CustomException("WRONG_CAPTCHA", "Wrong Captcha Entered");
		}
		
		/* encrypt here */
		user = encryptionDecryptionUtil.encryptObject(user, "User", User.class);
		validateUserUniqueness(user);
		// validateUserMobileNumberUniqueness(user);
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

		/* decrypt here because encrypted data coming from DB */

	}
	
	public User createUserNoValidate(User user, RequestInfo requestInfo) {
		user.setUuid(UUID.randomUUID().toString());
		user.validateNewUser(createUserValidateName);
		conditionallyValidateOtp(user);
		
		  if(user.isOtpValidationMandatory()&&!validateCaptcha(user.getCaptchaUuid(),user.getCaptcha())) { throw new
		  CustomException("WRONG_CAPTCHA", "Wrong Captcha Entered"); }
		 
		
		/* encrypt here */
		user = encryptionDecryptionUtil.encryptObject(user, "User", User.class);
		validateUserUniqueness(user);
		// validateUserMobileNumberUniqueness(user);
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

		/* decrypt here because encrypted data coming from DB */

	}

	private void validateUserUniqueness(User user) {
		if (userRepository.isUserPresent(user.getUsername(),
				getStateLevelTenantForCitizen(user.getTenantId(), user.getType()), user.getType(), "username"))
			throw new DuplicateUserNameException(UserSearchCriteria.builder().userName(user.getUsername())
					.type(user.getType()).tenantId(user.getTenantId()).build());
	}

	private void validateUserMobileNumberUniqueness(User user) {
		if (userRepository.isUserPresent(user.getMobileNumber(),
				getStateLevelTenantForCitizen(user.getTenantId(), user.getType()), user.getType(), "mobilenumber"))
			throw new DuplicateMobileNumberException(UserSearchCriteria.builder().userName(user.getMobileNumber())
					.type(user.getType()).tenantId(user.getTenantId()).build(), "Mobile Number Already Exist");
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
			//String captcha = createCaptcha(new ResponseInfo()).getCaptcha().getCaptcha();
			//String uuid = createCaptcha(new ResponseInfo()).getCaptcha().getUuid();
			map.add("captcha", user.getCaptcha());
			map.add("captchaUuid", user.getCaptchaUuid());
					
			//System.out.println(captcha);
			//System.out.println(uuid);

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map,
					headers);
			return restTemplate.postForEntity(userHost + "/user/oauth/token", request, Map.class).getBody();

		} catch (Exception e) {
			log.error("Error occurred while logging-in via register flow", e);
			throw new CustomException("LOGIN_ERROR",
					"Error occurred while logging in via register flow: " + e.getMessage());
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
		Otp otp = Otp.builder().otp(user.getOtpReference()).identity(user.getMobileNumber())
				.tenantId(user.getTenantId()).userType(user.getType()).build();
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
		final User existingUser = getUserByUuid(user.getUuid());
		user.setTenantId(getStateLevelTenantForCitizen(user.getTenantId(), user.getType()));
		validateUserRoles(user);
		user.validateUserModification();
		validatePassword(user.getPassword());
		user.setPassword(encryptPwd(user.getPassword()));
		/* encrypt */
		user = encryptionDecryptionUtil.encryptObject(user, "User", User.class);
		userRepository.update(user, existingUser, requestInfo.getUserInfo().getId(),
				requestInfo.getUserInfo().getUuid());

		// If user is being unlocked via update, reset failed login attempts
		if (user.getAccountLocked() != null && !user.getAccountLocked() && existingUser.getAccountLocked())
			resetFailedLoginAttempts(user);

		User encryptedUpdatedUserfromDB = getUserByUuid(user.getUuid());
		User decryptedupdatedUserfromDB = encryptionDecryptionUtil.decryptObject(encryptedUpdatedUserfromDB, "UserSelf",
				User.class, requestInfo);
		return decryptedupdatedUserfromDB;
	}

	public void removeTokensByUser(User user) {
		Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientIdAndUserName(USER_CLIENT_ID,
				user.getUsername());

		for (OAuth2AccessToken token : tokens) {
			if (token.getAdditionalInformation() != null
					&& token.getAdditionalInformation().containsKey("UserRequest")) {
				if (token.getAdditionalInformation()
						.get("UserRequest") instanceof org.egov.user.web.contract.auth.User) {
					org.egov.user.web.contract.auth.User userInfo = (org.egov.user.web.contract.auth.User) token
							.getAdditionalInformation().get("UserRequest");
					if (user.getUsername().equalsIgnoreCase(userInfo.getUserName())
							&& user.getTenantId().equalsIgnoreCase(userInfo.getTenantId())
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
		userRepository.update(user, existingUser, requestInfo.getUserInfo().getId(),
				requestInfo.getUserInfo().getUuid());
		User updatedUser = getUserByUuid(user.getUuid());

		/* decrypt here */
		existingUser = encryptionDecryptionUtil.decryptObject(existingUser, "UserSelf", User.class, requestInfo);
		updatedUser = encryptionDecryptionUtil.decryptObject(updatedUser, "UserSelf", User.class, requestInfo);

		setFileStoreUrlsByFileStoreIds(Collections.singletonList(updatedUser));
		String oldEmail = existingUser.getEmailId();
		String newEmail = updatedUser.getEmailId();
		if ((oldEmail != null && !oldEmail.isEmpty()) && newEmail != null && !(newEmail.equalsIgnoreCase(oldEmail))) {
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
		String existingDecryptedPass = null;
		String newDecryptedPass = null;
		BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
		updatePasswordRequest.validate();
		final User user = getUniqueUser(updatePasswordRequest.getUserName(), updatePasswordRequest.getTenantId(),
				updatePasswordRequest.getType());

		if (user.getType().toString().equals(UserType.CITIZEN.toString()) && isCitizenLoginOtpBased)
			throw new InvalidUpdatePasswordRequestException();
		if (user.getType().toString().equals(UserType.EMPLOYEE.toString()) && isEmployeeLoginOtpBased)
			throw new InvalidUpdatePasswordRequestException();

		try {
			existingDecryptedPass = decrypt(updatePasswordRequest.getExistingPassword(), key);
			newDecryptedPass = decrypt(updatePasswordRequest.getNewPassword(), key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LoggedInUserUpdatePasswordRequest loggedInUserUpdatePasswordRequest = LoggedInUserUpdatePasswordRequest
				.builder().existingPassword(existingDecryptedPass).newPassword(newDecryptedPass)
				.tenantId(updatePasswordRequest.getTenantId()).type(updatePasswordRequest.getType())
				.userName(updatePasswordRequest.getUserName()).build();

		// updatePasswordRequest.setExistingPassword(existingDecryptedPass);
		// updatePasswordRequest.setNewPassword(newDecryptedPass);
		validateExistingPassword(user, loggedInUserUpdatePasswordRequest.getExistingPassword());
		validatePassword(loggedInUserUpdatePasswordRequest.getNewPassword());
		if (bcrypt.matches(loggedInUserUpdatePasswordRequest.getNewPassword(), user.getPassword())) {
			log.info("Password Already Used Previously");
			throw new InvalidLoggedInUserUpdatePasswordRequestException(loggedInUserUpdatePasswordRequest);
		}

		user.updatePassword(encryptPwd(loggedInUserUpdatePasswordRequest.getNewPassword()));
		userRepository.update(user, user, user.getId(), user.getUuid());
	}

	/**
	 * This Api will update the password for non logged-in user
	 *
	 * @param request
	 */
	public void updatePasswordForNonLoggedInUser(NonLoggedInUserUpdatePasswordRequest request,
			RequestInfo requestInfo) {

		String newDecryptedPass = null;
		request.validate();
		BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
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
		/*
		 * the reason for decryption here is the otp service requires decrypted username
		 */
		user = encryptionDecryptionUtil.decryptObject(user, "User", User.class, requestInfo);
		user.setOtpReference(request.getOtpReference());
		validateOtp(user);

		try {

			newDecryptedPass = decrypt(request.getNewPassword(), key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		NonLoggedInUserUpdatePasswordRequest nonLoggedInUserUpdatePasswordRequest = NonLoggedInUserUpdatePasswordRequest
				.builder().newPassword(newDecryptedPass).otpReference(request.getOtpReference())
				.tenantId(request.getTenantId()).type(request.getType()).userName(request.getUserName()).build();
		// request.setNewPassword(newDecryptedPass);
		validatePassword(nonLoggedInUserUpdatePasswordRequest.getNewPassword());

		if (bcrypt.matches(nonLoggedInUserUpdatePasswordRequest.getNewPassword(), user.getPassword())) {
			log.info("Password Already Used Previously");
			throw new InvalidNonLoggedInUserUpdatePasswordRequestException(nonLoggedInUserUpdatePasswordRequest);
		}
		user.updatePassword(encryptPwd(nonLoggedInUserUpdatePasswordRequest.getNewPassword()));
		/* encrypt here */
		/* encrypted value is stored in DB */
		user = encryptionDecryptionUtil.encryptObject(user, "User", User.class);
		userRepository.update(user, user, user.getId(), user.getUuid());
		// userRepository.update(user, user,requestInfo.getUserInfo().getId() ,
		// requestInfo.getUserInfo().getUuid());
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
	 * Checks if user is eligible for unlock returns true, - If configured cool down
	 * period has passed since last lock else false
	 *
	 * @param user to be checked for eligibility for unlock
	 * @return if unlock able
	 */
	public boolean isAccountUnlockAble(User user) {
		if (user.getAccountLocked()) {
			boolean unlockAble = System.currentTimeMillis() - user.getAccountLockedDate() > TimeUnit.MINUTES
					.toMillis(accountUnlockCoolDownPeriod);

			log.info("Account eligible for unlock - " + unlockAble);
			log.info("Current time {}, last lock time {} , cool down period {} ", System.currentTimeMillis(),
					user.getAccountLockedDate(), TimeUnit.MINUTES.toMillis(accountUnlockCoolDownPeriod));
			return unlockAble;
		} else
			return true;
	}

	/**
	 * Perform actions where a user login fails - Fetch existing failed login
	 * attempts within configured time
	 * period{@link UserService#maxInvalidLoginAttemptsPeriod} - If failed login
	 * attempts exceeds configured {@link UserService#maxInvalidLoginAttempts} -
	 * then lock account - Add failed login attempt entry to repository
	 *
	 * @param user      user whose failed login attempt to be handled
	 * @param ipAddress IP address of remote
	 */
	public void handleFailedLogin(User user, String ipAddress, RequestInfo requestInfo) {
		if (!Objects.isNull(user.getUuid())) {
			List<FailedLoginAttempt> failedLoginAttempts = userRepository.fetchFailedAttemptsByUserAndTime(
					user.getUuid(),
					System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(maxInvalidLoginAttemptsPeriod));

			if (failedLoginAttempts.size() + 1 >= maxInvalidLoginAttempts) {
				User userToBeUpdated = user.toBuilder().accountLocked(true).password(null)
						.accountLockedDate(System.currentTimeMillis()).build();

				user = updateWithoutOtpValidation(userToBeUpdated, requestInfo);
				removeTokensByUser(user);
				log.info(
						"Locked account with uuid {} for {} minutes as exceeded max allowed attempts of {} within {} "
								+ "minutes",
						user.getUuid(), accountUnlockCoolDownPeriod, maxInvalidLoginAttempts,
						maxInvalidLoginAttemptsPeriod);
				throw new OAuth2Exception("Account locked");
			}

			userRepository.insertFailedLoginAttempt(
					new FailedLoginAttempt(user.getUuid(), ipAddress, System.currentTimeMillis(), true));
		}
	}

	public void userLoginFaliedAuditReport(User user, HttpServletRequest req, String status) {

		ApplicationPid pid = new ApplicationPid();
		HttpSession session = req.getSession();
		SessionDetails sessionDetails = new SessionDetails(session.getId(), session.getLastAccessedTime(),
				session.getCreationTime(), session.getMaxInactiveInterval());
		String uuid = UUID.randomUUID().toString();
		Long attempt_date = System.currentTimeMillis();
		String ip = null;
		String user_name = user.getUsername();
		String user_uuid = user.getUuid();
		String attempt_status = status;
		
		String x_forwarded_for = (null != req.getHeader("X-Forwarded-For") && !req.getHeader("X-Forwarded-For").isEmpty()
				? req.getHeader("X-Forwarded-For")
				: null);
		if(null!=x_forwarded_for) {
			ip=x_forwarded_for;
		}else {
			ip=req.getRemoteAddr();
		}
		System.out.println("X-Forwarded-For========================>>>>>>>>>>>"+x_forwarded_for);
		
		String user_agent = (null != req.getHeader("User-Agent") && !req.getHeader("User-Agent").isEmpty()
				? req.getHeader("User-Agent")
				: null);
		String referrer = (null != req.getHeader("Referer") && !req.getHeader("Referer").isEmpty()
				? req.getHeader("Referer")
				: null);
		;
		String url = (null != req.getHeader("Referer") && !req.getHeader("Referer").isEmpty() ? req.getHeader("Referer")
				: null);
		JsonNode session_details = objectMapper.convertValue(sessionDetails, JsonNode.class);
		String corelation_id = req.getHeader("x-correlation-id");

		UserLoginAttemptAudit audit = new UserLoginAttemptAudit(uuid, attempt_date, ip, user_name, user_uuid,
				attempt_status, user_agent, referrer, url, session_details, pid.toString(), corelation_id);
		userRepository.userLoginAttemptAudit(audit);

	}

	/**
	 * This api will validate existing password and current password matching or not
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
	 * this api will validate, updating the profile for same logged-in user or not
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
				errorMap.put("INVALID_PWD_LENGTH", "Password must be of minimum: " + pwdMinLength + " and maximum: "
						+ pwdMaxLength + " characters.");
			Pattern p = Pattern.compile(pwdRegex);
			Matcher m = p.matcher(password);
			if (!m.find()) {
				errorMap.put("INVALID_PWD_PATTERN",
						"Password MUST HAVE: Atleast one digit, one upper case, one lower case, one special character (@#$%) and MUST NOT contain any spaces");
			}
		}
		if (!CollectionUtils.isEmpty(errorMap.keySet())) {
			throw new CustomException(errorMap);
		}
	}

	public CaptchaResponse createCaptcha(ResponseInfo responseInfo) {
		CaptchaResponse captchaResponse = new CaptchaResponse();
		Captcha captcha = new Captcha();
		final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		final int CAPTCHA_LENGTH = 6;
		String uuid = UUID.randomUUID().toString();

		StringBuilder captchaText = new StringBuilder(CAPTCHA_LENGTH);
		Random random = new Random();
		for (int i = 0; i < CAPTCHA_LENGTH; i++) {
			captchaText.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
		}
		/*
		 * ByteArrayOutputStream baos = new ByteArrayOutputStream(); try {
		 * ImageIO.write(generateCaptchaImage(captchaText.toString()), "jpeg", baos); }
		 * catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } byte[] imageBytes = baos.toByteArray(); String
		 * encodedfile = Base64.getEncoder().encodeToString(imageBytes);
		 */

		// capMap.put(uuid, captchaText.toString());
		String CaptchaKey=captchaText.toString()+key.substring(6);
		redisTemplate.opsForValue().set(uuid, CaptchaKey, 5, TimeUnit.MINUTES);
		captchaResponse.setResponseInfo(responseInfo);
		captcha.setCaptchaUuid(uuid);
		captcha.setCaptcha(captchaText.toString());
		captchaResponse.setCaptcha(captcha);
		return captchaResponse;
	}

	public static BufferedImage generateCaptchaImage(String captchaText) {
		int width = 160;
		int height = 40;
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bufferedImage.createGraphics();

		// Background
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, width, height);

		// Text
		g2d.setFont(new Font("Arial", Font.BOLD, 24));
		g2d.setColor(Color.BLACK);
		g2d.drawString(captchaText, 20, 30);

		g2d.dispose();
		return bufferedImage;
	}

	public boolean validateCaptcha(String uuid, String captcha) {
		String storedCaptcha = redisTemplate.opsForValue().get(uuid);
		System.out.println("storedCaptchaKey::" + storedCaptcha);
		if(storedCaptcha!=null)
		storedCaptcha=storedCaptcha.substring(0,6);
		System.out.println("storedCaptcha::" + storedCaptcha);
		if (storedCaptcha != null) {
			if (captcha.contentEquals(storedCaptcha))
				return true;
			else
				throw new CustomException("WRONG_CAPTCHA", "Wrong Captcha Entered");
		}

		return false;
	}

	public String decrypt(String encryptedText, String uuid) throws Exception {
		final String ALGORITHM = algorithm;
		final String TRANSFORMATION = transformation;
		String decryptionKey=null;
		if(uuid!=null)
		{
			decryptionKey=redisTemplate.opsForValue().get(uuid);
			if(decryptionKey==null)
				throw new CustomException("INVALID_LOGIN","Login Failed Please Try Again");
			else
				redisTemplate.delete(uuid);
		}
		else
			decryptionKey=key;

		try {
			// Step 1: Split IV and encrypted data
			String[] parts = encryptedText.split(":");
			if (parts.length != 2) {
				throw new IllegalArgumentException("Invalid encrypted text format");
			}

			byte[] iv = Base64.getDecoder().decode(parts[0]); // Decode IV
			byte[] encryptedBytes = Base64.getDecoder().decode(parts[1]); // Decode encrypted text

			// Step 2: Set up AES cipher for CBC mode
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			SecretKeySpec keySpec = new SecretKeySpec(decryptionKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
			IvParameterSpec ivSpec = new IvParameterSpec(iv);
			cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

			// Step 3: Decrypt
			byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
			return new String(decryptedBytes, StandardCharsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Decryption failed: " + e.getMessage());
		}
	}

}
