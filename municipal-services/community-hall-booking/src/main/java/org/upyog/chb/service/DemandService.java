package org.upyog.chb.service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.egov.common.contract.request.User;
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

import com.google.common.base.Optional;

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
	 * 3. Create XDemand for particular tax heads 
	 * 4. Bill will be automatically generated when fetch bill api is called for demand created by this API
	 * @param bookingRequest
	 * @return
	 */

	public List<Demand> createDemand(CommunityHallBookingRequest bookingRequest, Object mdmsData, boolean generateDemand) {
		String tenantId = bookingRequest.getHallsBookingApplication().getTenantId();
		String consumerCode = bookingRequest.getHallsBookingApplication().getBookingNo();
		
		CommunityHallBookingDetail bookingDetail = bookingRequest.getHallsBookingApplication();
		User user =bookingRequest.getRequestInfo().getUserInfo();
		
		User owner = User.builder().name(user.getName()).emailId(user.getEmailId())
				.mobileNumber(user.getMobileNumber()).tenantId(bookingDetail.getTenantId()).build();
		
		// Calculate Fees for the booking 
        long days = calculateDaysBetween(bookingRequest.getHallsBookingApplication().getBookingSlotDetails().get(0).getBookingDate(), bookingRequest.getHallsBookingApplication().getBookingSlotDetails().get(0).getBookingToDate());    	

		BigDecimal totalPayableAmount = BigDecimal.valueOf(days)
			    .multiply(new BigDecimal(bookingRequest
			            .getHallsBookingApplication()
			            .getRelatedAsset()
			            .getAssetDetails()
			            .get("gstAssetCost")
			            .asText())) // Converts assetCost string to BigDecimal
			    .add(new BigDecimal(bookingRequest
			            .getHallsBookingApplication()
			            .getRelatedAsset()
			            .getAssetDetails()
			            .get("securityAmount")
			            .asText())); // Converts securityAmount string to BigDecimal
    	
    	
		List<DemandDetail> demandDetails = new LinkedList<>();
		demandDetails.add(DemandDetail.builder()
		.collectionAmount(BigDecimal.ZERO)
		.taxAmount(totalPayableAmount)
				.taxHeadMasterCode(CommunityHallBookingConstants.BILLING_TAX_HEAD_MASTER_CODE).tenantId(bookingDetail.getTenantId()).build());
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, Integer.valueOf(config.getChbBillExpiryAfter()));
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 23, 59, 59);
		//List<DemandDetail> demandDetails = calculationService.calculateDemand(bookingRequest);
		
		LocalDate maxdate = getMaxBookingDate(bookingDetail);
		
		Demand demand = Demand.builder().consumerCode(consumerCode)
				 .demandDetails(demandDetails).payer(owner)
				 .minimumAmountPayable(totalPayableAmount)
				 .tenantId(tenantId)
				.taxPeriodFrom(CommunityHallBookingUtil.getCurrentTimestamp()).taxPeriodTo(CommunityHallBookingUtil.minusOneDay(maxdate))
				.fixedBillExpiryDate(cal.getTimeInMillis())
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
	
	public long calculateDaysBetween(String fromDate, String toDate) {
		// Define the date format
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		// Parse the input strings into LocalDate
		LocalDate fromDateParsed = LocalDate.parse(fromDate, formatter);
		LocalDate toDateParsed = LocalDate.parse(toDate, formatter);

		// Calculate the difference in days
		  return ChronoUnit.DAYS.between(fromDateParsed, toDateParsed) + 1;
	}
	
	
	
	
	public List<Demand> getDemand(CommunityHallDemandEstimationCriteria estimationCriteria){
		log.info("Getting demand for request without booking no");

		if(!bookingValidator.isSameHallCode(estimationCriteria.getBookingSlotDetails())) {
			throw new CustomException(CommunityHallBookingConstants.MULTIPLE_HALL_CODES_ERROR, "Booking of multiple halls are not allowed");
		}
		
		String tenantId = estimationCriteria.getTenantId().split("\\.")[0];
		if (estimationCriteria.getTenantId().split("\\.").length == 1) {
			throw new CustomException(CommunityHallBookingConstants.INVALID_TENANT, "Please provide valid tenant id for booking creation");
		}
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
	    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Assuming the incoming format
	    DateTimeFormatter expectedFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy"); // Expected format

	    return bookingDetail.getBookingSlotDetails().stream()
	        .map(detail -> {
	            String bookingDate = detail.getBookingDate();

	            // Convert from input format to the expected format
	            LocalDate parsedDate = LocalDate.parse(bookingDate, inputFormatter);
	            String formattedDate = parsedDate.format(expectedFormatter);

	            // Pass the correctly formatted date to the utility method
	            return CommunityHallBookingUtil.parseStringToLocalDate(formattedDate);
	        })
	        .max(LocalDate::compareTo)
	        .orElseThrow(() -> new IllegalArgumentException("No booking dates available"));
	}

	
	
	

}
