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
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingDetail;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingRequest;

import digit.models.coremodels.IdResponse;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EnrichmentService {

	@Autowired
	private RequestServiceConfiguration config;

	@Autowired
	private IdGenRepository idGenRepository;

	public void enrichCreateWaterTankerRequest(WaterTankerBookingRequest waterTankerRequest) {
		String bookingId = RequestServiceUtil.getRandonUUID();
		log.info("Enriching water tanker booking id :" + bookingId);

		WaterTankerBookingDetail waterTankerDetail = waterTankerRequest.getWaterTankerBookingDetail();
		RequestInfo requestInfo = waterTankerRequest.getRequestInfo();
		AuditDetails auditDetails = RequestServiceUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
		
		
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
		String roles = waterTankerRequest.getRequestInfo().getUserInfo().getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.joining(", "));
		waterTankerDetail.setBookingCreatedBy(roles);
		
		waterTankerDetail.getApplicantDetail().setBookingId(bookingId);
		waterTankerDetail.getApplicantDetail().setApplicantId(RequestServiceUtil.getRandonUUID());
		waterTankerDetail.getApplicantDetail().setAuditDetails(auditDetails);
	

		waterTankerDetail.getAddress().
			setAddressId(RequestServiceUtil.getRandonUUID());
		waterTankerDetail.getAddress().setApplicantId(waterTankerDetail.getApplicantDetail().getApplicantId());
		
		log.info("Enriched application request data :" + waterTankerDetail);

	}

	public void enrichCreateMobileToiletRequest(MobileToiletBookingRequest mobileToiletRequest) {
		String bookingId = RequestServiceUtil.getRandonUUID();
		log.info("Enriching water tanker booking id :" + bookingId);

		MobileToiletBookingDetail mobileToiletDetail = mobileToiletRequest.getMobileToiletBookingDetail();
		RequestInfo requestInfo = mobileToiletRequest.getRequestInfo();
		AuditDetails auditDetails = RequestServiceUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);


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


		mobileToiletDetail.getAddress().
				setAddressId(RequestServiceUtil.getRandonUUID());
		mobileToiletDetail.getAddress().setApplicantId(mobileToiletDetail.getApplicantDetail().getApplicantId());

		log.info("Enriched application request data :" + mobileToiletDetail);

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
