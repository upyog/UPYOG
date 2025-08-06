package org.upyog.rs.service;

import java.util.List;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.rs.config.RequestServiceConfiguration;
import org.upyog.rs.enums.RequestServiceStatus;
import org.upyog.rs.repository.IdGenRepository;
import org.upyog.rs.util.RequestServiceUtil;
import org.upyog.rs.util.UserUtil;
import org.upyog.rs.web.models.ApplicantDetail;
import org.upyog.rs.web.models.AuditDetails;
import org.upyog.rs.web.models.mobileToilet.MobileToiletBookingDetail;
import org.upyog.rs.web.models.mobileToilet.MobileToiletBookingRequest;
import org.upyog.rs.web.models.user.AddressV2;
import org.upyog.rs.web.models.user.User;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingDetail;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingRequest;
import org.apache.commons.lang3.StringUtils;

import digit.models.coremodels.IdResponse;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EnrichmentService {

	@Autowired
	private RequestServiceConfiguration config;

	@Autowired
	private IdGenRepository idGenRepository;

	@Autowired
	private UserService userService;

	public void enrichCreateWaterTankerRequest(WaterTankerBookingRequest waterTankerRequest) {
		String bookingId = RequestServiceUtil.getRandonUUID();
		log.info("Enriching water tanker booking id :" + bookingId);

		WaterTankerBookingDetail waterTankerDetail = waterTankerRequest.getWaterTankerBookingDetail();
		RequestInfo requestInfo = waterTankerRequest.getRequestInfo();
		String userUuid = requestInfo.getUserInfo().getUuid();
		AuditDetails auditDetails = RequestServiceUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
		if(config.getIsUserProfileEnabled()) {
			// If the mobile number in the request matches the applicant's mobile number, then set the applicantDetailId as userUuid
			if (UserUtil.isCurrentUserApplicant(waterTankerRequest)) {
				waterTankerDetail.setApplicantUuid(userUuid);
			} else {
				// If the mobile number does not match, set the applicantDetailId to null and addressDetailId to null
				// Setting applicantDetailId and addressDetailId to null to ensure new user and address creation
				waterTankerDetail.setApplicantUuid(null);
				waterTankerDetail.setAddressDetailId(null);
			}

			String applicantDetailId = waterTankerDetail.getApplicantUuid();
			String addressDetailId = waterTankerDetail.getAddressDetailId();

			if (StringUtils.isBlank(applicantDetailId)) {
				// Enrich user details for existing user or user details with address for new user
				enrichUserDetails(waterTankerRequest);
			}
			if (StringUtils.isBlank(addressDetailId)) {
				// Enrich address details only
				enrichAddressDetails(waterTankerRequest, waterTankerDetail);
			}
		}else{
			/*
			 * If the currently logged-in user is not the same as the applicant mobile number entered in the form is different from the login mobile number,
			 * then we proceed to enrich user details, which will create a new user with the provided details.
			 * If the currently logged-in user is the same as the applicant mobile number, we do not enrich user details
			 * user profile is not enabled for this service, we explicitly set `applicantDetailId` and `addressDetailId` to null in the application details
			 */
			if (!UserUtil.isCurrentUserApplicant(waterTankerRequest)) {
				// Enrich user details for existing user or user details with address for new user
				enrichUserDetails(waterTankerRequest);
			}
			waterTankerDetail.setApplicantUuid(null);
			waterTankerDetail.setAddressDetailId(null);
			log.info("User profile is not enabled, setting applicantDetailId and addressDetailId to null");
		}
		waterTankerDetail.setBookingId(bookingId);
		waterTankerDetail.setApplicationDate(auditDetails.getCreatedTime());
		waterTankerDetail.setBookingStatus(RequestServiceStatus.valueOf(waterTankerDetail.getBookingStatus()).toString());
		waterTankerDetail.setAuditDetails(auditDetails);
		waterTankerDetail.setTenantId(waterTankerRequest.getWaterTankerBookingDetail().getTenantId());	
		
	    List<String> customIds = getIdList(requestInfo, waterTankerDetail.getTenantId(),
				config.getWaterTankerApplicationKey(), config.getWaterTankerApplicationFormat(), 1);

		log.info("Enriched application request application no :" + customIds.get(0));

		waterTankerDetail.setBookingNo(customIds.get(0)); 

		
		waterTankerDetail.setTankerType(waterTankerRequest.getWaterTankerBookingDetail().getTankerType());			
		waterTankerDetail.setTankerQuantity(waterTankerRequest.getWaterTankerBookingDetail().getTankerQuantity());	
		waterTankerDetail.setWaterQuantity(waterTankerRequest.getWaterTankerBookingDetail().getWaterQuantity());
		waterTankerDetail.setDescription(waterTankerRequest.getWaterTankerBookingDetail().getDescription());
		waterTankerDetail.setDeliveryDate(waterTankerRequest.getWaterTankerBookingDetail().getDeliveryDate());
		waterTankerDetail.setDeliveryTime(waterTankerRequest.getWaterTankerBookingDetail().getDeliveryTime());
		waterTankerDetail.setMobileNumber(waterTankerRequest.getWaterTankerBookingDetail().getApplicantDetail().getMobileNumber());
		waterTankerDetail.setLocalityCode(waterTankerRequest.getWaterTankerBookingDetail().getAddress().getLocalityCode());
		String roles = waterTankerRequest.getRequestInfo().getUserInfo().getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.joining(", "));
		waterTankerDetail.setBookingCreatedBy(roles);
		
		waterTankerDetail.getApplicantDetail().setBookingId(bookingId);
		waterTankerDetail.getApplicantDetail().setApplicantId(RequestServiceUtil.getRandonUUID());
		waterTankerDetail.getAddress().setAddressId(RequestServiceUtil.getRandonUUID());
		waterTankerDetail.getApplicantDetail().setAuditDetails(auditDetails);
		waterTankerDetail.getAddress().setApplicantId(waterTankerDetail.getApplicantDetail().getApplicantId());
		
		log.info("Enriched application request data :" + waterTankerDetail);

	}

	/**
	 * Enriches the applicant and address detail IDs in the given application detail.
	 * <p>
	 * If the applicantDetailId is present, it attempts to fetch an existing user based on the request.
	 * - If an existing user is found, sets the applicantDetailId accordingly.
	 * - If not found, it creates a new user and sets both applicantDetailId and addressDetailId
	 *   from the newly created user and their associated address.
	 * </p>
	 *
	 * @param waterTankerRequest The full application request containing applicant and address info.
	 */
	private void enrichUserDetails(WaterTankerBookingRequest waterTankerRequest) {
		// Try fetching an existing user for the given request
		WaterTankerBookingDetail waterTankerDetail = waterTankerRequest.getWaterTankerBookingDetail();
		RequestInfo requestInfo = waterTankerRequest.getRequestInfo();
		ApplicantDetail applicantDetail = waterTankerDetail.getApplicantDetail();
		String tenantId = waterTankerDetail.getTenantId();
		User existingUsers = userService.fetchExistingUser(tenantId, applicantDetail, requestInfo);

		if (existingUsers != null) {
			waterTankerDetail.setApplicantUuid(existingUsers.getUuid());
			log.info("Existing user found with ID: {}", existingUsers.getUuid());
			return;
		}

		// Create a new user with address details if no existing user was found
		User newUser = userService.createUserHandler(waterTankerRequest.getRequestInfo(), waterTankerRequest.getWaterTankerBookingDetail().getApplicantDetail(),
				waterTankerRequest.getWaterTankerBookingDetail().getAddress(), waterTankerRequest.getWaterTankerBookingDetail().getTenantId());
		log.info("New user created with ID: {}", newUser.getUuid());
		waterTankerDetail.setApplicantUuid(newUser.getUuid());

		// Set addressDetailId from the first address with a non-null ID, if present
		newUser.getAddresses().stream()
				.filter(addr -> addr.getId() != null)
				.findFirst()
				.ifPresent(addr -> waterTankerDetail.setAddressDetailId(String.valueOf(addr.getId())));
	}


	/**
	 * Enriches the address details in the given WaterTankerBookingDetail object by creating a new address
	 * based on the user UUID provided in the WaterTankerBookingRequest object. If the new address is created
	 * successfully, the addressDetailId in the WaterTankerBookingDetail object is updated.
	 *
	 * @param waterTankerRequest The request object containing necessary data for address creation.
	 * @param waterTankerDetail The application details object to be enriched with the new address ID.
	 */
	private void enrichAddressDetails(WaterTankerBookingRequest waterTankerRequest, WaterTankerBookingDetail waterTankerDetail) {

		// If applicant UUID is null or blank, throw custom exception
		if (StringUtils.isBlank(waterTankerRequest.getWaterTankerBookingDetail().getApplicantUuid())) {
			throw new CustomException("APPLICANT_UUID_NULL", "Applicant UUID is blank");
		}

		// Fetch the new address associated with the user's UUID
		AddressV2 addressDetails = UserService.convertApplicantAddressToUserAddress(waterTankerRequest.getWaterTankerBookingDetail().getAddress(), RequestServiceUtil.extractTenantId(waterTankerRequest.getWaterTankerBookingDetail().getTenantId()));
		AddressV2 address = userService.createNewAddressV2ByUserUuid(addressDetails,waterTankerRequest.getRequestInfo(),waterTankerRequest.getWaterTankerBookingDetail().getApplicantUuid());

		if (address != null) {
			// Set the address detail ID in the booking detail
			waterTankerDetail.setAddressDetailId(String.valueOf(address.getId()));
			log.info("Address successfully enriched with ID: {}", address.getId());
		} else {
			throw new CustomException("ADDRESS_CREATION_FAILED", "Failed to create address for the given applicant UUID");
		}
	}


	/**
	 * Enriches the mobile toilet booking request with necessary details such as booking ID, application date,
	 * booking status, and address details. It also generates a unique booking number using the ID generation service.
	 *
	 * @param mobileToiletRequest The request object containing necessary data for mobile toilet booking.
	 */
	public void enrichCreateMobileToiletRequest(MobileToiletBookingRequest mobileToiletRequest) {
		String bookingId = RequestServiceUtil.getRandonUUID();
		log.info("Enriching water tanker booking id :" + bookingId);

		MobileToiletBookingDetail mobileToiletDetail = mobileToiletRequest.getMobileToiletBookingDetail();
		RequestInfo requestInfo = mobileToiletRequest.getRequestInfo();
		String userUuid = requestInfo.getUserInfo().getUuid();
		AuditDetails auditDetails = RequestServiceUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
		if(config.getIsUserProfileEnabled()) {
			// If the mobile number in the request matches the applicant's mobile number, then set the applicantDetailId as userUuid
			if (UserUtil.isCurrentUserApplicant(mobileToiletRequest)) {
				mobileToiletDetail.setApplicantUuid(userUuid);
			} else {
				// If the mobile number does not match, set the applicantDetailId to null and addressDetailId to null
				mobileToiletDetail.setApplicantUuid(null);
				mobileToiletDetail.setAddressDetailId(null);
			}

			String applicantDetailId = mobileToiletDetail.getApplicantUuid();
			if (StringUtils.isBlank(applicantDetailId)) {
				// Enrich user details for existing user or user details with address for new user
				enrichUserDetails(mobileToiletRequest);
			}
			String addressDetailId = mobileToiletDetail.getAddressDetailId();
			if (StringUtils.isBlank(addressDetailId)) {
				// Enrich address details only
				enrichAddressDetails(mobileToiletRequest, mobileToiletDetail);
			}
		}else{
			/*
			 * If the currently logged-in user is not the same as the applicant mobile number entered in the form is different from the login mobile number,
			 * then we proceed to enrich user details, which will create a new user with the provided details.
			 * If the currently logged-in user is the same as the applicant mobile number, we do not enrich user details
			 * user profile is not enabled for this service, we explicitly set `applicantDetailId` and `addressDetailId` to null in the application details
			 */
			if (!UserUtil.isCurrentUserApplicant(mobileToiletRequest)) {
				// Enrich user details for existing user or user details with address for new user
				enrichUserDetails(mobileToiletRequest);
			}
			mobileToiletDetail.setApplicantUuid(null);
			mobileToiletDetail.setAddressDetailId(null);
		}
		mobileToiletDetail.setBookingId(bookingId);
		mobileToiletDetail.setApplicationDate(auditDetails.getCreatedTime());
		mobileToiletDetail.setBookingStatus(RequestServiceStatus.valueOf(mobileToiletDetail.getBookingStatus()).toString());
		mobileToiletDetail.setAuditDetails(auditDetails);
		mobileToiletDetail.setTenantId(mobileToiletRequest.getMobileToiletBookingDetail().getTenantId());
		mobileToiletDetail.setMobileNumber(mobileToiletRequest.getMobileToiletBookingDetail().getApplicantDetail().getMobileNumber());
		mobileToiletDetail.setLocalityCode(mobileToiletRequest.getMobileToiletBookingDetail().getAddress().getLocalityCode());
		List<String> customIds = getIdList(requestInfo, mobileToiletDetail.getTenantId(),
				config.getMobileToiletApplicationKey(), config.getMobileToiletApplicationFormat(), 1);

		log.info("Enriched application request application no :" + customIds.get(0));

		mobileToiletDetail.setBookingNo(customIds.get(0));


		mobileToiletDetail.setNoOfMobileToilet(mobileToiletRequest.getMobileToiletBookingDetail().getNoOfMobileToilet());
		mobileToiletDetail.setDescription(mobileToiletRequest.getMobileToiletBookingDetail().getDescription());
		mobileToiletDetail.setDeliveryFromDate(mobileToiletRequest.getMobileToiletBookingDetail().getDeliveryFromDate());
		mobileToiletDetail.setDeliveryToDate(mobileToiletRequest.getMobileToiletBookingDetail().getDeliveryToDate());
		mobileToiletDetail.setDeliveryFromTime(mobileToiletRequest.getMobileToiletBookingDetail().getDeliveryFromTime());
		mobileToiletDetail.setDeliveryToTime(mobileToiletRequest.getMobileToiletBookingDetail().getDeliveryToTime());
		String roles = mobileToiletRequest.getRequestInfo().getUserInfo().getRoles()
				.stream()
				.map(Role::getName)
				.collect(Collectors.joining(", "));
		mobileToiletDetail.setBookingCreatedBy(roles);

		mobileToiletDetail.getApplicantDetail().setBookingId(bookingId);
		mobileToiletDetail.getApplicantDetail().setApplicantId(RequestServiceUtil.getRandonUUID());
		mobileToiletDetail.getAddress().setAddressId(RequestServiceUtil.getRandonUUID());
		mobileToiletDetail.getApplicantDetail().setAuditDetails(auditDetails);
		mobileToiletDetail.getAddress().setApplicantId(mobileToiletDetail.getApplicantDetail().getApplicantId());

		log.info("Enriched application request data :" + mobileToiletDetail);

	}

	/**
	 * Enriches the applicant and address detail IDs in the given application detail.
	 * <p>
	 * If the applicantDetailId is present, it attempts to fetch an existing user based on the request.
	 * - If an existing user is found, sets the applicantDetailId accordingly.
	 * - If not found, it creates a new user and sets both applicantDetailId and addressDetailId
	 *   from the newly created user and their associated address.
	 * </p>
	 *
	 * @param mobileToiletRequest The full application request containing applicant and address info.
	 */
	private void enrichUserDetails(MobileToiletBookingRequest mobileToiletRequest) {
		// Try fetching an existing user for the given request
		MobileToiletBookingDetail mobileToiletDetail = mobileToiletRequest.getMobileToiletBookingDetail();
		RequestInfo requestInfo = mobileToiletRequest.getRequestInfo();
		ApplicantDetail applicantDetail = mobileToiletDetail.getApplicantDetail();
		String tenantId = mobileToiletDetail.getTenantId();
		User existingUsers = userService.fetchExistingUser(tenantId, applicantDetail, requestInfo);

		if (existingUsers != null && StringUtils.isNotBlank(existingUsers.getUuid())) {
			mobileToiletDetail.setApplicantUuid(existingUsers.getUuid());
			log.info("Existing user found with ID: {}", existingUsers.getUuid());
			return;
		}

		// Create a new user with address details if no existing user was found
		User newUser = userService.createUserHandler(mobileToiletRequest.getRequestInfo(), mobileToiletRequest.getMobileToiletBookingDetail().getApplicantDetail(),
				mobileToiletRequest.getMobileToiletBookingDetail().getAddress(), mobileToiletRequest.getMobileToiletBookingDetail().getTenantId());
		log.info("New user created with ID: {}", newUser.getUuid());
		mobileToiletDetail.setApplicantUuid(newUser.getUuid());

		// Set addressDetailId from the first address with a non-null ID, if present
		newUser.getAddresses().stream()
				.filter(addr -> addr.getId() != null)
				.findFirst()
				.ifPresent(addr -> mobileToiletDetail.setAddressDetailId(String.valueOf(addr.getId())));
	}


	/**
	 * Enriches the address details in the given mobileToiletDetail object by creating a new address
	 * based on the user UUID provided in the mobileToiletRequest object. If the new address is created
	 * successfully, the addressDetailId in the mobileToiletDetail object is updated.
	 *
	 * @param mobileToiletRequest The request object containing necessary data for address creation.
	 * @param mobileToiletDetail The application details object to be enriched with the new address ID.
	 */

	private void enrichAddressDetails(MobileToiletBookingRequest mobileToiletRequest, MobileToiletBookingDetail mobileToiletDetail) {
		// If applicant UUID is null or blank, throw custom exception
		if (StringUtils.isBlank(mobileToiletRequest.getMobileToiletBookingDetail().getApplicantUuid())) {
			throw new CustomException("APPLICANT_UUID_NULL", "Applicant UUID is blank");
		}

		// Fetch the new address associated with the user's UUID
		AddressV2 addressDetails = UserService.convertApplicantAddressToUserAddress(mobileToiletRequest.getMobileToiletBookingDetail().getAddress(), RequestServiceUtil.extractTenantId(mobileToiletRequest.getMobileToiletBookingDetail().getTenantId()));
		AddressV2 address = userService.createNewAddressV2ByUserUuid(addressDetails,mobileToiletRequest.getRequestInfo(),mobileToiletRequest.getMobileToiletBookingDetail().getApplicantUuid());

			if (address != null) {
				// Set the address detail ID in the application details object
				mobileToiletDetail.setAddressDetailId(String.valueOf(address.getId()));
				log.info("Address details successfully enriched with ID: {}", address.getId());
			} else {
				throw new CustomException("ADDRESS_CREATION_FAILED", "Failed to create address for the given applicant UUID");
			}
	}

	/**
	 * Returns a list of numbers generated from idgen
	 *
	 * @param requestInfo RequestInfo from the request
	 * @param tenantId    tenantId of the city
	 * @param idKey       code of the field defined in application properties for
	 *                    which ids are generated for
	 * @param idformat    format in which ids are to be generated
	 * @param count       Number of ids to be generated
	 * @return List of ids generated using idGen service
	 */
	private List<String> getIdList(RequestInfo requestInfo, String tenantId, String idKey, String idformat, int count) {
		List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idKey, idformat, count)
				.getIdResponses();

		if (CollectionUtils.isEmpty(idResponses))
			throw new CustomException("IDGEN_ERROR", "No ids returned from idgen Service");

		return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
	}
	
	public void enrichWaterTankerBookingUponUpdate(String bookingStatus, WaterTankerBookingRequest waterTankerRequest) {
		WaterTankerBookingDetail waterTankerDetail = waterTankerRequest.getWaterTankerBookingDetail();
		waterTankerDetail.setVendorId(waterTankerRequest.getWaterTankerBookingDetail().getVendorId());
		waterTankerDetail.setDriverId(waterTankerRequest.getWaterTankerBookingDetail().getDriverId());
		waterTankerDetail.setVehicleId(waterTankerRequest.getWaterTankerBookingDetail().getVehicleId());
		waterTankerDetail.getAuditDetails().setLastModifiedBy(waterTankerRequest.getRequestInfo().getUserInfo().getUuid());
		waterTankerDetail.getAuditDetails().setLastModifiedTime(System.currentTimeMillis());
		waterTankerDetail.setBookingStatus(bookingStatus);
		
	}

	public void enrichMobileToiletBookingUponUpdate(String bookingStatus, MobileToiletBookingRequest mobileToiletRequest) {
		MobileToiletBookingDetail mobileToiletDetail = mobileToiletRequest.getMobileToiletBookingDetail();
		mobileToiletDetail.setVendorId(mobileToiletRequest.getMobileToiletBookingDetail().getVendorId());
		mobileToiletDetail.setDriverId(mobileToiletRequest.getMobileToiletBookingDetail().getDriverId());
		mobileToiletDetail.setVehicleId(mobileToiletRequest.getMobileToiletBookingDetail().getVehicleId());
		mobileToiletDetail.getAuditDetails().setLastModifiedBy(mobileToiletRequest.getRequestInfo().getUserInfo().getUuid());
		mobileToiletDetail.getAuditDetails().setLastModifiedTime(System.currentTimeMillis());
		mobileToiletDetail.setBookingStatus(bookingStatus);

	}

	


}
