package org.upyog.chb.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.util.MdmsUtil;
import org.upyog.chb.web.models.BookingSlotDetail;
import org.upyog.chb.web.models.CalculationType;
import org.upyog.chb.web.models.CommunityHallBookingRequest;
import org.upyog.chb.web.models.billing.DemandDetail;

import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CalculationService {

	public List<DemandDetail> calculateDemand(CommunityHallBookingRequest bookingRequest) {
		// TODO: Take calculationType for hallcode
		Object response = MdmsUtil.getMDMSDataMap();

		log.info("Calculkation data for CHB booking demand service  :"
				+ JsonPath.read(response, CommunityHallBookingConstants.CHB_JSONPATH_CODE + "."
						+ CommunityHallBookingConstants.CHB_CALCULATION_TYPE + ".[0].feeType"));

		/*
		 * log.info("Calculkation data for CHB booking :" + JsonPath.read(response,
		 * CommunityHallBookingConstants.CHB_JSONPATH_CODE + "." +
		 * CommunityHallBookingConstants.CHB_CALCULATION_TYPE +".[0].feeType"));
		 */

		// log.info("first calculation data : " + list.get(0));

		// log.info("first calculation data : " + list.get(0).);

		List<CalculationType> calculationTypes = new ArrayList<>();

		// TODO: Get tax header from tax head master
		for (int i = 0; i < 4; i++) {
			String basePath = CommunityHallBookingConstants.CHB_JSONPATH_CODE + "."
					+ CommunityHallBookingConstants.CHB_CALCULATION_TYPE + ".[" + i + "].";
			/*
			 * log.info("Calculation data for CHB booking :" + JsonPath.read(response,
			 * CommunityHallBookingConstants.CHB_JSONPATH_CODE + "." +
			 * CommunityHallBookingConstants.CHB_CALCULATION_TYPE +".[0].feeType"));
			 */
			Object amount = JsonPath.read(response, basePath + "amount");
			CalculationType calculationType = CalculationType.builder().amount(new BigDecimal(amount.toString()))
					.applicationType(JsonPath.read(response, basePath + "applicationType"))
					.feeType(JsonPath.read(response, basePath + "feeType"))
					.serviceType(JsonPath.read(response, basePath + "serviceType")).build();

			calculationTypes.add(calculationType);
		}

		log.info("calculationTypes " + calculationTypes);

		String tenantId = bookingRequest.getHallsBookingApplication().getTenantId().split("\\.")[0];

		List<DemandDetail> demandDetails = processCalculationForDemandGeneration(tenantId, calculationTypes,
				bookingRequest);

		// TODO: remove this with json and data
		/*
		 * demandDetails.add(DemandDetail.builder().collectionAmount(BigDecimal.ZERO).
		 * taxAmount(BigDecimal.valueOf(500.00))
		 * .taxHeadMasterCode("CHB_BOOKING_FEE").tenantId(tenantId).build());
		 */

		return demandDetails;

	}

	private List<DemandDetail> processCalculationForDemandGeneration(String tenantId,
			List<CalculationType> calculationTypes, CommunityHallBookingRequest bookingRequest) {

		Map<String, Long> hallCodeBookingDaysMap = bookingRequest.getHallsBookingApplication().getBookingSlotDetails()
				.stream().collect(Collectors.groupingBy(BookingSlotDetail::getHallCode, Collectors.counting()));

		final List<DemandDetail> demandDetails = new LinkedList<>();

		List<CalculationType> variableDemand = new ArrayList<CalculationType>();
		
		for (CalculationType type : calculationTypes) {
			if (!type.getFeeType().equals(CommunityHallBookingConstants.RENT_CALCULATION_TYPE)
					&& !type.getFeeType().equals(CommunityHallBookingConstants.CGST_CALCULATION_TYPE)
					&& !type.getFeeType().equals(CommunityHallBookingConstants.SGST_CALCULATION_TYPE)) {
				variableDemand.add(type);
			} else if (type.getFeeType().equals(CommunityHallBookingConstants.RENT_CALCULATION_TYPE)) {
				bookingRequest.getHallsBookingApplication().getBookingSlotDetails().stream().forEach(slot -> {
					DemandDetail demandDetail = DemandDetail.builder().taxAmount(type.getAmount())
							.taxHeadMasterCode(type.getFeeType()).tenantId(tenantId).build();
					demandDetails.add(demandDetail);
				});
				log.info("Updated Rent for hall codes : " + hallCodeBookingDaysMap);
			}
		}

		bookingRequest.getHallsBookingApplication().getBookingSlotDetails().stream().forEach(slot -> {
			BigDecimal hallCodeBookingDays = new BigDecimal(hallCodeBookingDaysMap.get(slot.getHallCode()));

			List<DemandDetail> hallDemands = variableDemand.stream().map(data ->
			// log.info("data :" + data);
			DemandDetail.builder().taxAmount(data.getAmount().multiply(hallCodeBookingDays))
					.taxHeadMasterCode(data.getFeeType()).tenantId(tenantId).build()).collect(Collectors.toList());

			demandDetails.addAll(hallDemands);
		});

		BigDecimal totalTaxableAmount = demandDetails.stream()
				.filter(demandDetail -> !demandDetail.getTaxHeadMasterCode()
						.equals(CommunityHallBookingConstants.RENT_CALCULATION_TYPE))
				.map(DemandDetail::getTaxAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

		log.info("Total Taxable amount for the booking : " + totalTaxableAmount);

		for (CalculationType type : calculationTypes) {
			if (type.getFeeType().equals(CommunityHallBookingConstants.CGST_CALCULATION_TYPE)
					|| type.getFeeType().equals(CommunityHallBookingConstants.SGST_CALCULATION_TYPE)) {
				DemandDetail demandDetail = DemandDetail.builder()
						.taxAmount(percentage(totalTaxableAmount, type.getAmount()))
						.taxHeadMasterCode(type.getFeeType()).tenantId(tenantId).build();
				demandDetails.add(demandDetail);
			}
		}

		return demandDetails;
	}

	private BigDecimal percentage(BigDecimal base, BigDecimal pct) {
		return base.multiply(pct).divide(CommunityHallBookingConstants.ONE_HUNDRED, RoundingMode.FLOOR);
	}

}
