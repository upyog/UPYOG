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
		AuditDetails auditDetails = RequestServiceUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);

		// If addressDetailId is null or blank, create a new address and set the ID
		if (StringUtils.isBlank(waterTankerDetail.getAddressDetailId())) {
			// Fetch the existing or new user based on the request
			User user = userService.getExistingOrNewUser(waterTankerRequest);
			waterTankerDetail.setApplicantUuid(user.getUuid());
			enrichAddressDetails(waterTankerRequest, waterTankerDetail);
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
		waterTankerDetail.getApplicantDetail().setAuditDetails(auditDetails);
		waterTankerDetail.getAddress().setApplicantId(waterTankerDetail.getApplicantDetail().getApplicantId());
		
		log.info("Enriched application request data :" + waterTankerDetail);

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

		// If applicantDetailId is null or blank, throw custom exception
		if (StringUtils.isBlank(waterTankerRequest.getWaterTankerBookingDetail().getApplicantUuid())) {
			throw new CustomException("APPLICANTID_IS_BLANK", "Applicant Detail ID is blank");
		}
		try {
			// Fetch the new address associated with the user's UUID
			AddressV2 address = userService.createNewAddressV2ByUserUuid(waterTankerRequest);

			if (address != null) {
				// Set the address detail ID in the application details object
				waterTankerDetail.setAddressDetailId(String.valueOf(address.getId()));
				log.info("Address details successfully enriched with ID: {}", address.getId());
			} else {
				log.warn("Failed to create new address for user UUID: {}", waterTankerRequest.getWaterTankerBookingDetail().getApplicantUuid());
			}
		} catch (Exception e) {
			log.error("Error while enriching address details: {}", e.getMessage(), e);
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
		AuditDetails auditDetails = RequestServiceUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);

		// If addressDetailId is null or blank, create a new address and set the ID
		if (StringUtils.isBlank(mobileToiletDetail.getAddressDetailId())) {
			// Fetch the existing or new user based on the request
			User user = userService.getExistingOrNewUser(mobileToiletRequest);
			mobileToiletDetail.setApplicantUuid(user.getUuid());
			enrichAddressDetails(mobileToiletRequest, mobileToiletDetail);
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
		mobileToiletDetail.getApplicantDetail().setAuditDetails(auditDetails);
		mobileToiletDetail.getAddress().setApplicantId(mobileToiletDetail.getApplicantDetail().getApplicantId());

		log.info("Enriched application request data :" + mobileToiletDetail);

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

		// If applicantDetailId is null or blank, throw custom exception
		if (StringUtils.isBlank(mobileToiletRequest.getMobileToiletBookingDetail().getApplicantUuid())) {
			throw new CustomException("APPLICANTID_IS_BLANK", "Applicant Detail ID is blank");
		}
		try {
			// Fetch the new address associated with the user's UUID
			AddressV2 address = userService.createNewAddressV2ByUserUuid(mobileToiletRequest);

			if (address != null) {
				// Set the address detail ID in the application details object
				mobileToiletDetail.setAddressDetailId(String.valueOf(address.getId()));
				log.info("Address details successfully enriched with ID: {}", address.getId());
			} else {
				log.warn("Failed to create new address for user UUID: {}", mobileToiletRequest.getMobileToiletBookingDetail().getApplicantUuid());
			}
		} catch (Exception e) {
			log.error("Error while enriching address details: {}", e.getMessage(), e);
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
