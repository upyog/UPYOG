package org.upyog.adv.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.adv.config.BookingConfiguration;
import org.upyog.adv.constants.BookingConstants;
import org.upyog.adv.util.MdmsUtil;
import org.upyog.adv.web.models.BookingRequest;
import org.upyog.adv.web.models.CalculationType;
import org.upyog.adv.web.models.CartDetail;
import org.upyog.adv.web.models.billing.DemandDetail;
import org.upyog.adv.web.models.billing.TaxHeadMaster;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CalculationService {
	
	@Autowired
	private MdmsUtil mdmsUtil;
	@Autowired
	private BookingConfiguration config;

	/**
	 * @param bookingRequest
	 * @return
	 */
	// Returns Demand detail: 
	//Gets the headMasters from the billing service and calculation type from mdms
	//Calls processCalculationForDemandGeneration to get the demand detail
	public List<DemandDetail> calculateDemand(BookingRequest bookingRequest) { 

		String tenantId = bookingRequest.getBookingApplication().getTenantId().split("\\.")[0];
		
		List<TaxHeadMaster> headMasters = mdmsUtil.getTaxHeadMasterList(bookingRequest.getRequestInfo(), tenantId , BookingConstants.BILLING_SERVICE);
		
		List<CalculationType> calculationTypes = mdmsUtil.getcalculationType(bookingRequest.getRequestInfo(), tenantId , config.getModuleName(), bookingRequest.getBookingApplication().getCartDetails().get(0) );


		log.info("calculationTypes " + calculationTypes);

		List<DemandDetail> demandDetails = processCalculationForDemandGeneration(tenantId, calculationTypes,
				bookingRequest, headMasters);

		return demandDetails;

	}

	 //Returns Demand detail:
	//Demand is generated using billing service, using the taxHeadCode and feeType
	//GST,SGST tax  codes are stored to calculate final amount
	private List<DemandDetail> processCalculationForDemandGeneration(String tenantId,
			List<CalculationType> calculationTypes, BookingRequest bookingRequest, List<TaxHeadMaster> headMasters) {

		Map<String, Long> advBookingDaysMap = bookingRequest.getBookingApplication().getCartDetails()
				.stream().collect(Collectors.groupingBy(CartDetail::getAddType, Collectors.counting()));
		//GST,SGST tax  codes are stored to calculate final amount
		List<String> taxCodes = Arrays.asList(config.getApplicableTaxes().split(","));
		log.info("tax applicable on booking  : " + taxCodes);
		//Demand will be generated using billing service for booking
		final List<DemandDetail> demandDetails = new LinkedList<>();
		
        List<String> taxHeadCodes = headMasters.stream().map(head -> head.getCode()).collect(Collectors.toList());
		
		log.info("tax head codes  : " + taxHeadCodes);

		//Demand for which tax is applicable is stored
		List<CalculationType> taxableFeeType = new ArrayList<>();
		
		BigDecimal advBookingDays = new BigDecimal(advBookingDaysMap.get(bookingRequest.getBookingApplication().getCartDetails().get(0).getAddType()));
		
		for (CalculationType type : calculationTypes) {
			if (taxHeadCodes.contains(type.getFeeType())) {
				
				if (type.isTaxApplicable()) {
					//Add taxable fee 
					taxableFeeType.add(type);
				} else if (!taxCodes.contains(type.getFeeType())) {
					DemandDetail data =  DemandDetail.builder().taxAmount(type.getAmount())
					.taxHeadMasterCode(type.getFeeType()).tenantId(tenantId).build();
					//Add fixed fee for which tax is not applicable
					demandDetails.add(data);
				}
			}
		}
		
		log.info("taxable fee type : " + taxableFeeType);

		List<DemandDetail> taxableDemands = taxableFeeType.stream().map(data ->
		
		DemandDetail.builder().taxAmount(data.getAmount().multiply(advBookingDays))
				.taxHeadMasterCode(data.getFeeType()).tenantId(tenantId).build()).collect(Collectors.toList());
		
		log.info("taxableDemands : " + taxableDemands);
		
		BigDecimal totalTaxableAmount = taxableDemands.stream()
				.map(DemandDetail::getTaxAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

		demandDetails.addAll(taxableDemands);

		log.info("Total Taxable amount for the booking : " + totalTaxableAmount);

		for (CalculationType type : calculationTypes) {
		    if (taxCodes.stream().anyMatch(code -> code.trim().equalsIgnoreCase(type.getFeeType().trim()))) {
		        DemandDetail demandDetail = DemandDetail.builder()
		                .taxAmount(calculateAmount(totalTaxableAmount, type.getAmount()))
		                .taxHeadMasterCode(type.getFeeType()).tenantId(tenantId).build();
		        demandDetails.add(demandDetail);
		    }
		}


		return demandDetails;
	}

	private BigDecimal calculateAmount(BigDecimal base, BigDecimal pct) {
		return base.multiply(pct).divide(BookingConstants.ONE_HUNDRED, RoundingMode.FLOOR);
	}

}
