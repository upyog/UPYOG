package org.upyog.rs.service;

import java.math.BigDecimal;
import java.util.Collections;
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
import org.upyog.rs.web.models.WaterTankerBookingDetail;
import org.upyog.rs.web.models.WaterTankerBookingRequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DemandService {


	@Autowired
	private CalculationService calculationService;

	@Autowired
	private DemandRepository demandRepository;



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
		log.info("Final amount payable after calculation : " + amountPayable);
		User owner = buildUser(waterTankerBookingDetail.getApplicantDetail(), tenantId);
		List<DemandDetail> demandDetails = buildDemandDetails(amountPayable, tenantId);
		Demand demand = buildDemand(tenantId, consumerCode, owner, demandDetails, amountPayable);
		log.info("Final demand generation object" + demand.toString());
		return demandRepository.saveDemand(waterTankerRequest.getRequestInfo(), Collections.singletonList(demand));
	}

	private User buildUser(ApplicantDetail applicantDetail, String tenantId) {
		return User.builder().name(applicantDetail.getName()).emailId(applicantDetail.getEmailId())
				.mobileNumber(applicantDetail.getMobileNumber()).tenantId(tenantId).build();
	}

	private List<DemandDetail> buildDemandDetails(BigDecimal amountPayable, String tenantId) {
		return Collections.singletonList(DemandDetail.builder().collectionAmount(BigDecimal.ZERO)
				.taxAmount(amountPayable).taxHeadMasterCode(RequestServiceConstants.REQUEST_SERVICE_TAX_MASTER_CODE)
				.tenantId(tenantId).build());
	}

	private Demand buildDemand(String tenantId, String consumerCode, User owner, List<DemandDetail> demandDetails,
			BigDecimal amountPayable) {
		return Demand.builder().consumerCode(consumerCode).demandDetails(demandDetails).payer(owner).tenantId(tenantId)
				.taxPeriodFrom(RequestServiceUtil.getCurrentTimestamp()).taxPeriodTo(RequestServiceUtil.getFinancialYearEnd())
				.consumerType(RequestServiceConstants.WATER_TANKER_SERVICE_NAME)
				.businessService(RequestServiceConstants.REQUEST_SERVICE_MODULE_NAME).additionalDetails(null).build();
	}

}
