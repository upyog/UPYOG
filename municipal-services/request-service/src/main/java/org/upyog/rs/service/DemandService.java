package org.upyog.rs.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.egov.common.contract.request.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.rs.config.RequestServiceConfiguration;
import org.upyog.rs.constant.RequestServiceConstants;
import org.upyog.rs.repository.DemandRepository;
import org.upyog.rs.util.RequestServiceUtil;
import org.upyog.rs.web.models.ApplicantDetail;
import org.upyog.rs.web.models.Demand;
import org.upyog.rs.web.models.DemandDetail;
import org.upyog.rs.web.models.billing.CalculationType;
import org.upyog.rs.web.models.mobileToilet.MobileToiletBookingDetail;
import org.upyog.rs.web.models.mobileToilet.MobileToiletBookingRequest;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingDetail;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingRequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DemandService {


	@Autowired
	private CalculationService calculationService;

	@Autowired
	private DemandRepository demandRepository;

	@Autowired
	private RequestServiceConfiguration config;



	/**
	 * Create demand by bringing tanker price from mdms
	 * 
	 * @param waterTankerRequest
	 * @return
	 */

	public List<Demand> createDemand(WaterTankerBookingRequest waterTankerRequest) {
		if (waterTankerRequest == null) {
			throw new IllegalArgumentException("WaterTanker Booking Request is Empty");
		}
		
		String tenantId = waterTankerRequest.getWaterTankerBookingDetail().getTenantId();
		log.info("Creating demand upon approve action for bboking no : {}", waterTankerRequest.getWaterTankerBookingDetail().getBookingNo());
		WaterTankerBookingDetail waterTankerBookingDetail = waterTankerRequest.getWaterTankerBookingDetail();
		String consumerCode = waterTankerBookingDetail.getBookingNo();
		String tankerCapacityType = waterTankerBookingDetail.getTankerType() + "_"
				+ waterTankerBookingDetail.getWaterQuantity();
		BigDecimal amountPayable = calculationService.calculateFee(waterTankerBookingDetail.getTankerQuantity(),
				tankerCapacityType, waterTankerRequest.getRequestInfo(), tenantId);
		BigDecimal immediateDeliveryFee = calculationService.immediateDeliveryFee(waterTankerBookingDetail.getExtraCharge(),
				waterTankerRequest.getRequestInfo(), tenantId);
		log.info("immediateDeliveryFee for tanker booking : {}", immediateDeliveryFee);
		log.info("Final amount payable after calculation : " + amountPayable);

		User owner = buildUser(waterTankerBookingDetail.getApplicantDetail(), tenantId);
		List<DemandDetail> demandDetails = buildDemandDetails(amountPayable, tenantId,immediateDeliveryFee);
		Demand demand = buildDemand(tenantId, consumerCode, owner, demandDetails, amountPayable);
		log.info("Final demand generation object" + demand.toString());
		return demandRepository.saveDemand(waterTankerRequest.getRequestInfo(), Collections.singletonList(demand));
	}

	public List<Demand> createMTDemand(MobileToiletBookingRequest mobileToiletRequest) {
		if (mobileToiletRequest == null) {
			throw new IllegalArgumentException("Mobile Toilet Booking Request is Empty");
		}

		String tenantId = mobileToiletRequest.getMobileToiletBookingDetail().getTenantId();
		log.info("Creating demand upon approve action for booking no : {}", mobileToiletRequest.getMobileToiletBookingDetail().getBookingNo());
		MobileToiletBookingDetail mobileToiletBookingDetail = mobileToiletRequest.getMobileToiletBookingDetail();
		String consumerCode = mobileToiletBookingDetail.getBookingNo();
		CalculationType calculationType = calculationService.mtCalculateFee(mobileToiletBookingDetail.getNoOfMobileToilet(),mobileToiletBookingDetail.getDeliveryFromDate(),mobileToiletBookingDetail.getDeliveryToDate(),mobileToiletRequest.getRequestInfo(), tenantId);
		log.info("Final amount payable after calculation : " + calculationType.getAmount());
		User owner = buildUser(mobileToiletBookingDetail.getApplicantDetail(), tenantId);
		List<DemandDetail> demandDetails = buildMTDemandDetails(calculationType.getAmount(), calculationType.getFeeType(),tenantId);
		Demand demand = buildMTDemand(tenantId, consumerCode, owner, demandDetails);
		log.info("Final demand generation object" + demand.toString());
		return demandRepository.saveDemand(mobileToiletRequest.getRequestInfo(), Collections.singletonList(demand));
	}

	private User buildUser(ApplicantDetail applicantDetail, String tenantId) {
		return User.builder().name(applicantDetail.getName()).emailId(applicantDetail.getEmailId())
				.mobileNumber(applicantDetail.getMobileNumber()).tenantId(tenantId).build();
	}

	/**
	 * Builds a list of DemandDetail objects for the given payable amounts.
	 * 
	 * Steps:
	 * 1. Checks if an immediate delivery fee is applicable:
	 *    - If greater than 0, adds a DemandDetail for the immediate delivery fee.
	 * 2. Adds a DemandDetail for the main payable amount.
	 * 3. Logs the final list of demand details.
	 * 
	 * Parameters:
	 * - amountPayable: The main amount to be paid.
	 * - tenantId: The tenant ID for which the demand is created.
	 * - immediateDeliveryFee: The fee for immediate delivery, if applicable.
	 * 
	 * Returns:
	 * - A list of DemandDetail objects containing tax details.
	 */
	private List<DemandDetail> buildDemandDetails(BigDecimal amountPayable, String tenantId, BigDecimal immediateDeliveryFee) {
		List<DemandDetail> demandDetails = new LinkedList<>();
		demandDetails.add(DemandDetail.builder().collectionAmount(BigDecimal.ZERO)
				.taxAmount(amountPayable).taxHeadMasterCode(RequestServiceConstants.REQUEST_SERVICE_TAX_MASTER_CODE)
				.tenantId(tenantId).build());
		if(immediateDeliveryFee.compareTo(BigDecimal.ZERO) > 0) {
			demandDetails.add(DemandDetail.builder().collectionAmount(BigDecimal.ZERO)
					.taxAmount(immediateDeliveryFee).taxHeadMasterCode(RequestServiceConstants.IMMEDIATE_DELIVERY_TAX_HEAD)
					.tenantId(tenantId).build());
		}

		log.info("Final demand details: {}", demandDetails);

		return demandDetails;
	}

	private List<DemandDetail> buildMTDemandDetails(BigDecimal amountPayable, String feeType, String tenantId) {

		return Collections.singletonList(DemandDetail.builder().collectionAmount(BigDecimal.ZERO)
				.taxAmount(amountPayable).taxHeadMasterCode(feeType)
				.tenantId(tenantId).build());
	}

	private Demand buildMTDemand(String tenantId, String consumerCode, User owner, List<DemandDetail> demandDetails) {
		return Demand.builder().consumerCode(consumerCode).demandDetails(demandDetails).payer(owner).tenantId(tenantId)
				.taxPeriodFrom(RequestServiceUtil.getCurrentTimestamp()).taxPeriodTo(RequestServiceUtil.getFinancialYearEnd())
				.consumerType(config.getMobileToiletBusinessService())
				.businessService(config.getMtModuleName()).additionalDetails(null).build();
	}

	private Demand buildDemand(String tenantId, String consumerCode, User owner, List<DemandDetail> demandDetails,
			BigDecimal amountPayable) {
		return Demand.builder().consumerCode(consumerCode).demandDetails(demandDetails).payer(owner).tenantId(tenantId)
				.taxPeriodFrom(RequestServiceUtil.getCurrentTimestamp()).taxPeriodTo(RequestServiceUtil.getFinancialYearEnd())
				.consumerType(RequestServiceConstants.WATER_TANKER_SERVICE_NAME)
				.businessService(config.getWtModuleName()).additionalDetails(null).build();
	}

}
