package org.upyog.chb.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.math.RoundingMode;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang.StringUtils;
import org.upyog.chb.web.models.User;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.repository.DemandRepository;
import org.upyog.chb.util.CommunityHallBookingUtil;
import org.upyog.chb.util.MdmsUtil;
import org.upyog.chb.validator.CommunityHallBookingValidator;
import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.CommunityHallBookingRequest;
import org.upyog.chb.web.models.CommunityHallDemandEstimationCriteria;
import org.upyog.chb.web.models.billing.Demand;
import org.upyog.chb.web.models.billing.DemandDetail;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DemandService {

	@Autowired
	private CommunityHallBookingConfiguration config;
	
	@Autowired
	private CalculationService calculationService;

	@Autowired
	private DemandRepository demandRepository;
	
	@Autowired
	private CommunityHallBookingValidator bookingValidator;

	@Autowired
	private MdmsUtil mdmsUtil;
	
	
	/**
	 * 1. Fetch tax heads from mdms tax-heads.json
	 * 2. Map amount to tax heads from CalculateType.json
	 * 3. Create Demand for particular tax heads 
	 * 4. Bill will be automatically generated when fetch bill api is called after demand is created by this API
	 * @param bookingRequest
	 * @return
	 */

	public List<Demand> createDemand(CommunityHallBookingRequest bookingRequest, Object mdmsData, boolean generateDemand) {
		String tenantId = bookingRequest.getHallsBookingApplication().getTenantId();
		String consumerCode = bookingRequest.getHallsBookingApplication().getBookingNo();
		
		CommunityHallBookingDetail bookingDetail = bookingRequest.getHallsBookingApplication();
		User owner = bookingRequest.getHallsBookingApplication().getOwners().get(0);

		// CalculationService now applies discount BEFORE tax calculation
		// The demandDetails returned already have taxes calculated on post-discount amount
		List<DemandDetail> demandDetails = calculationService.calculateDemand(bookingRequest);

		// --- Add discount as line item for bill transparency ---
		try {
			Object addDetails = bookingRequest.getHallsBookingApplication().getAdditionalDetails();
			java.math.BigDecimal discountAmount = java.math.BigDecimal.ZERO;
			if (addDetails != null) {
				// Authorization: only EMPLOYEE users can set discount in additionalDetails
				if (bookingRequest.getRequestInfo() == null || bookingRequest.getRequestInfo().getUserInfo() == null
						|| StringUtils.equalsIgnoreCase(bookingRequest.getRequestInfo().getUserInfo().getType(),
                        CommunityHallBookingConstants.CITIZEN)) {
					throw new CustomException("UNAUTHORIZED_DISCOUNT", "Only users with EMPLOYEE role can set discountAmount in additionalDetails");
				}
				if (addDetails instanceof Map) {
					Object val = ((Map<?, ?>) addDetails).get("discountAmount");
					if (val != null) discountAmount = new java.math.BigDecimal(val.toString());
				} else {
					// try parse as JSON string
					ObjectMapper mapper = new ObjectMapper();
					Map<?, ?> map = mapper.readValue(addDetails.toString(), Map.class);
					Object val = map.get("discountAmount");
					if (val != null) discountAmount = new java.math.BigDecimal(val.toString());
				}
			}

			if (discountAmount != null && discountAmount.compareTo(java.math.BigDecimal.ZERO) > 0) {
				// Add discount as negative line item for transparency
				// Note: Tax was already calculated on (amount - discount) in CalculationService
				DemandDetail discountDetail = DemandDetail.builder()
						.taxAmount(discountAmount.negate().setScale(2, RoundingMode.FLOOR))
						.taxHeadMasterCode("CHB_DISCOUNT")
						.tenantId(tenantId)
						.build();

				demandDetails.add(discountDetail);
				log.info("Added discount line item {} for booking {}", discountAmount, bookingRequest.getHallsBookingApplication().getBookingId());
			}
		} catch (Exception ex) {
			log.warn("Unable to apply discount from additionalDetails for booking {} : {}", bookingRequest.getHallsBookingApplication().getBookingId(), ex.getMessage());
		}

		// --- Add Round-off line item ---
		BigDecimal totalBeforeRoundOff = demandDetails.stream()
				.map(DemandDetail::getTaxAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal roundedTotal = totalBeforeRoundOff.setScale(0, RoundingMode.HALF_UP);
		BigDecimal roundOffAmount = roundedTotal.subtract(totalBeforeRoundOff);
		if (roundOffAmount.compareTo(BigDecimal.ZERO) != 0) {
			DemandDetail roundOffDetail = DemandDetail.builder()
					.taxAmount(roundOffAmount.setScale(2, RoundingMode.HALF_UP))
					.taxHeadMasterCode(CommunityHallBookingConstants.CHB_ROUND_OFF)
					.tenantId(tenantId)
					.build();
			demandDetails.add(roundOffDetail);
			log.info("Added round-off line item {} for booking {}", roundOffAmount, consumerCode);
		}

		LocalDate maxdate = getMaxBookingDate(bookingDetail);
		
		Demand demand = Demand.builder().consumerCode(consumerCode)
				 .demandDetails(demandDetails).payer(owner)
				 .tenantId(tenantId)
				.taxPeriodFrom(CommunityHallBookingUtil.getCurrentTimestamp()).taxPeriodTo(CommunityHallBookingUtil.minusOneDay(maxdate))
				.consumerType(config.getModuleName()).businessService(config.getBusinessServiceName()).additionalDetails(null).build();

		
		List<Demand> demands = new ArrayList<>();
		demands.add(demand);
		if(!generateDemand) {
			BigDecimal totalAmount = demandDetails.stream().map(DemandDetail::getTaxAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
			demand.setAdditionalDetails(totalAmount);
			return demands;
		}
		log.info("Sending call to billing service for generating demand for booking no : " + consumerCode);
		return demandRepository.saveDemand(bookingRequest.getRequestInfo(), demands);
	}
	
	
	public List<Demand> getDemand(CommunityHallDemandEstimationCriteria estimationCriteria){
		log.info("Getting demand for request without booking no");

		if(!bookingValidator.isSameHallCode(estimationCriteria.getBookingSlotDetails())) {
			throw new CustomException(CommunityHallBookingConstants.MULTIPLE_HALL_CODES_ERROR, "Booking of multiple halls are not allowed");
		}
		
		if (estimationCriteria.getTenantId() == null || !estimationCriteria.getTenantId().contains(".")) {
			throw new CustomException(CommunityHallBookingConstants.INVALID_TENANT, "Please provide valid tenant id for booking creation");
		}

		// Use full tenant id (eg: pb.nangak) for MDMS calls
		String tenantId = estimationCriteria.getTenantId();
		
		CommunityHallBookingDetail bookingDetail = CommunityHallBookingDetail.builder().tenantId(tenantId)
				.bookingSlotDetails(estimationCriteria.getBookingSlotDetails())
				.communityHallCode(estimationCriteria.getCommunityHallCode()).build();
		CommunityHallBookingRequest bookingRequest = CommunityHallBookingRequest.builder().hallsBookingApplication(bookingDetail)
				.requestInfo(estimationCriteria.getRequestInfo()).build();
		Object mdmsData = mdmsUtil.mDMSCall(bookingRequest.getRequestInfo(), tenantId);
		List<Demand> demands = createDemand(bookingRequest, mdmsData, false);
		return demands;
	}
	
	private LocalDate getMaxBookingDate(CommunityHallBookingDetail bookingDetail) {
		
		return bookingDetail.getBookingSlotDetails().stream().map(detail -> detail.getBookingDate())
				.max( LocalDate :: compareTo)
		        .get();
	}
	
	
	

}
