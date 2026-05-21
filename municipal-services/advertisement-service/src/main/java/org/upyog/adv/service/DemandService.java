package org.upyog.adv.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.adv.config.BookingConfiguration;
import org.upyog.adv.constants.BookingConstants;
import org.upyog.adv.repository.DemandRepository;
import org.upyog.adv.repository.ServiceRequestRepository;
import org.upyog.adv.util.BookingUtil;
import org.upyog.adv.util.DemandUtil;
import org.upyog.adv.util.MdmsUtil;
import org.upyog.adv.validator.BookingValidator;
import org.upyog.adv.web.models.AdvertisementDemandEstimationCriteria;
import org.upyog.adv.web.models.BookingDetail;
import org.upyog.adv.web.models.BookingRequest;
import org.upyog.adv.web.models.RequestInfoWrapper;
import org.upyog.adv.web.models.billing.Demand;
import org.upyog.adv.web.models.billing.DemandDetail;

import lombok.extern.slf4j.Slf4j;
import org.upyog.adv.web.models.billing.DemandResponse;
import org.upyog.adv.web.models.billing.GetBillCriteria;

import static org.upyog.adv.constants.BookingConstants.EMPTY_DEMAND_ERROR_CODE;
import static org.upyog.adv.constants.BookingConstants.EMPTY_DEMAND_ERROR_MESSAGE;

@Service
@Slf4j
public class DemandService {

	@Autowired
	private BookingConfiguration config;

	@Autowired
	private CalculationService calculationService;

	@Autowired
	private DemandRepository demandRepository;

	@Autowired
	private BookingValidator bookingValidator;

	@Autowired
	private MdmsUtil mdmsUtil;

	@Autowired
	private DemandUtil demandUtil;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;


	/**
	 * 1. Fetch tax heads from mdms tax-heads.json
	 * 2. Map amount to tax heads from CalculateType.json
	 * 3. Create Demand for particular tax heads
	 * 4. Bill will be automatically generated when fetch bill api is called for demand created by this API
	 *
	 * @param bookingRequest
	 * @return
	 */
	public List<Demand> createDemand(BookingRequest bookingRequest, Object mdmsData, boolean generateDemand) throws JsonProcessingException {
		String tenantId = bookingRequest.getBookingApplication().getTenantId();
		String consumerCode = bookingRequest.getBookingApplication().getBookingNo();
		BookingDetail bookingDetail = bookingRequest.getBookingApplication();
		
		// Get the first owner from the owners list and convert to User
		List<org.upyog.adv.web.models.OwnerInfo> owners = bookingRequest.getBookingApplication().getOwners();
		if (CollectionUtils.isEmpty(owners)) {
			throw new CustomException("OWNER_NOT_FOUND", "No owner found in booking application for demand creation");
		}
		org.upyog.adv.web.models.OwnerInfo ownerInfo = owners.get(0);
		
		User owner = ownerInfo.toCommonUser();

		Map<String, Object> mdmsDataMap = (Map<String, Object>) mdmsData;

		List<Map<String, Object>> taxRateList = (List<Map<String, Object>>) ((Map<String, Object>) ((Map<String, Object>) mdmsDataMap
				.get("MdmsRes")).get("Advertisement")).get("TaxAmount");

		List<String> taxRateCodes = taxRateList.stream()
				.map(tax -> (String) tax.get("feeType"))
				.collect(Collectors.toList());

		List<DemandDetail> demandDetails = calculationService.calculateDemand(bookingRequest, taxRateCodes, mdmsData);

		// 🔹 Round off each tax head independently before creating Demand
		demandDetails.forEach(detail -> {
			if (detail.getTaxAmount() != null) {
				BigDecimal rounded = new BigDecimal(Math.ceil(detail.getTaxAmount().doubleValue()));
				detail.setTaxAmount(rounded);
			}
		});

		LocalDate maxdate = getMaxBookingDate(bookingDetail);

		// Calculate total amount for minimumAmountPayable
		BigDecimal minimumPayable = demandDetails.stream()
				.map(DemandDetail::getTaxAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		Demand demand = Demand.builder()
				.consumerCode(consumerCode)
				.demandDetails(demandDetails)
				.payer(owner)
				.tenantId(tenantId)
				.taxPeriodFrom(BookingUtil.getCurrentTimestamp())
				.taxPeriodTo(BookingUtil.minusOneDay(maxdate))
				.consumerType(config.getModuleName())
				.businessService(config.getBusinessServiceName())
				.minimumAmountPayable(minimumPayable)
				.additionalDetails(null)
				.build();

		List<Demand> demands = new ArrayList<>();
		demands.add(demand);

		if (!generateDemand) {
			BigDecimal totalAmount = demandDetails.stream()
					.map(DemandDetail::getTaxAmount)
					.reduce(BigDecimal.ZERO, BigDecimal::add);
			demand.setAdditionalDetails(totalAmount);
			return demands;
		}

		log.info("Sending call to billing service for generating demand for booking no : " + consumerCode);
		return demandRepository.saveDemand(bookingRequest.getRequestInfo(), demands);
	}


	// This gets the demand for request without getting booking number by calling the create demand method
	public List<Demand> getDemand(AdvertisementDemandEstimationCriteria estimationCriteria) throws JsonProcessingException {
		log.info("Getting demand for request without booking no");

		String tenantId = estimationCriteria.getTenantId();
		if (estimationCriteria.getTenantId().split("\\.").length == 1) {
			throw new CustomException(BookingConstants.INVALID_TENANT, "Please provide valid tenant id for booking creation");
		}
		BookingDetail bookingDetail = BookingDetail.builder()
				.tenantId(tenantId)
				.CartDetails(estimationCriteria.getCartDetails())
				.build();
		BookingRequest bookingRequest = BookingRequest.builder()
				.bookingApplication(bookingDetail)
				.requestInfo(estimationCriteria.getRequestInfo())
				.build();
		Object mdmsData = mdmsUtil.mDMSCall(bookingRequest.getRequestInfo(), tenantId);
		List<Demand> demands = createDemand(bookingRequest, mdmsData, false);
		return demands;
	}

	private LocalDate getMaxBookingDate(BookingDetail bookingDetail) {
		return bookingDetail.getCartDetails().stream()
				.map(detail -> detail.getBookingDate())
				.max(LocalDate::compareTo)
				.get();
	}

	public DemandResponse updateDemands(GetBillCriteria getBillCriteria, RequestInfoWrapper requestInfoWrapper) {
		if (getBillCriteria.getAmountExpected() == null)
			getBillCriteria.setAmountExpected(BigDecimal.ZERO);

		DemandResponse res = mapper.convertValue(
				serviceRequestRepository.fetchResult(demandUtil.getDemandSearchUrl(getBillCriteria), requestInfoWrapper),
				DemandResponse.class);

		if (CollectionUtils.isEmpty(res.getDemands())) {
			Map<String, String> map = new HashMap<>();
			map.put(EMPTY_DEMAND_ERROR_CODE, EMPTY_DEMAND_ERROR_MESSAGE);
		}

		Map<String, List<Demand>> consumerCodeToDemandMap = new HashMap<>();
		res.getDemands().forEach(demand -> {
			if (consumerCodeToDemandMap.containsKey(demand.getConsumerCode()))
				consumerCodeToDemandMap.get(demand.getConsumerCode()).add(demand);
			else {
				List<Demand> demands = new LinkedList<>();
				demands.add(demand);
				consumerCodeToDemandMap.put(demand.getConsumerCode(), demands);
			}
		});
		return res;
	}
}
