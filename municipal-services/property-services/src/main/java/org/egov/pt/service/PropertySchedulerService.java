package org.egov.pt.service;

import java.math.BigDecimal;
import java.time.Instant;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.Optional;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.Arrays;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.egov.mdms.model.MdmsResponse;
import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.models.CalculateTaxRequest;
import org.egov.pt.models.CalculateTaxResponse;
import org.egov.pt.models.CalculateTaxPreviewResponse;
import org.egov.pt.models.OwnerInfo;
import org.egov.pt.models.Property;
import org.egov.pt.models.PropertyBillFailure;
import org.egov.pt.models.PropertyCriteria;
import org.egov.pt.models.PtTaxCalculatorTracker;
import org.egov.pt.models.PtTaxCalculatorTrackerSearchCriteria;
import org.egov.pt.models.Unit;
import org.egov.pt.models.bill.BillSearchCriteria;
import org.egov.pt.models.bill.Demand;
import org.egov.pt.models.bill.GenerateBillCriteria;
import org.egov.pt.models.collection.Bill;
import org.egov.pt.models.collection.BillDetail;
import org.egov.pt.models.collection.BillResponse;
import org.egov.pt.models.enums.BillStatus;
import org.egov.pt.models.enums.Status;
import org.egov.pt.util.PTConstants;
import org.egov.pt.web.contracts.PtTaxCalculatorTrackerRequest;
import org.egov.pt.web.contracts.RequestInfoWrapper;
import org.egov.pt.web.contracts.SendPropertyEmailRequest;
import org.egov.pt.web.contracts.SendPropertyNoticeRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.egov.pt.repository.RestCallRepository;
import org.springframework.beans.factory.annotation.Value;
import java.util.Comparator;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.egov.pt.repository.PropertyRepository;
import org.egov.pt.models.CustomAmountUpdateRequest;
import org.egov.pt.models.CustomAmountUpdateResponse;

//pdf
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.egov.pt.service.AlfrescoService;
import org.egov.pt.web.contracts.alfresco.DmsResponse;
import org.egov.pt.web.contracts.alfresco.DmsRequest;
import org.egov.pt.util.PTConstants;
import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.models.bill.DemandDetail;
import org.egov.pt.models.BillIdRequest;
import java.util.concurrent.atomic.AtomicInteger;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;


import java.io.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PropertySchedulerService {

	@Autowired
	private PropertyService propertyService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MDMSService mdmsService;

	@Autowired
	private DemandService demandService;

	@Autowired
	private BillService billService;

	@Autowired
	private EnrichmentService enrichmentService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private PropertyConfiguration propertyConfiguration;

	@Autowired
	private RestCallRepository restCallRepository;

	@Autowired
	private AlfrescoService alfrescoService;

	@Value("${egov.sms.host}")
	private String smsHost;

	@Value("${egov.sms.tracker.create.endpoint}")
	private String smsTrackerCreateEndpoint;
	
	@Value("${egov.mail.tracker.create.endpoint}")
	private String mailTrackerCreateEndpoint;

//add new
	@Autowired
	private PropertyRepository repository;

	public CalculateTaxResponse calculateTax(CalculateTaxRequest calculateTaxRequest) {

		List<PtTaxCalculatorTracker> taxCalculatorTrackers = new ArrayList<>();
		AtomicInteger skippedCount = new AtomicInteger(0);

		Boolean isBilling = false;
		JsonNode ulbModules = null;
		JsonNode propertyTaxRateModules = null;
		JsonNode propertyTaxLandRateModules = null;
		JsonNode zones = null;
		JsonNode buildingStructures = null;
		JsonNode buildingEstablishmentYears = null;
		JsonNode buildingPurposes = null;
		JsonNode buildingUses = null;
		JsonNode overAllRebatePercentages = null;
		JsonNode earlyPaymentRebatePercentages = null;
		JsonNode propertyTaxRates = null;
		JsonNode propertyTaxLandRates = null;

		BigDecimal days = calculateDays(calculateTaxRequest);

		Map<String, Set<String>> errorMap = new HashMap<>();

		MdmsResponse mdmsResponse = mdmsService.getMdmsData(calculateTaxRequest.getRequestInfo(), null);

		if (mdmsResponse != null && mdmsResponse.getMdmsRes() != null
				&& mdmsResponse.getMdmsRes().get(PTConstants.MDMS_MODULE_ULBS) != null) {

			ulbModules = objectMapper.valueToTree(mdmsResponse.getMdmsRes().get(PTConstants.MDMS_MODULE_ULBS));
			propertyTaxRateModules = objectMapper
					.valueToTree(mdmsResponse.getMdmsRes().get(PTConstants.MDMS_MODULE_PROPERTYTAXRATE));

			zones = objectMapper.valueToTree(ulbModules.get(PTConstants.MDMS_MASTER_DETAILS_ZONES));
			buildingStructures = objectMapper
					.valueToTree(ulbModules.get(PTConstants.MDMS_MASTER_DETAILS_BUILDINGSTRUCTURE));
			buildingEstablishmentYears = objectMapper
					.valueToTree(ulbModules.get(PTConstants.MDMS_MASTER_DETAILS_BUILDINGESTABLISHMENTYEAR));
			buildingPurposes = objectMapper
					.valueToTree(ulbModules.get(PTConstants.MDMS_MASTER_DETAILS_BUILDINGPURPOSE));
			buildingUses = objectMapper.valueToTree(ulbModules.get(PTConstants.MDMS_MASTER_DETAILS_BUILDINGUSE));
			overAllRebatePercentages = objectMapper
					.valueToTree(ulbModules.get(PTConstants.MDMS_MASTER_DETAILS_OVERALLREBATE));
			earlyPaymentRebatePercentages = objectMapper
					.valueToTree(ulbModules.get(PTConstants.MDMS_MASTER_DETAILS_EARLYPAYMENTREBATE));
			propertyTaxRates = objectMapper
					.valueToTree(propertyTaxRateModules.get(PTConstants.MDMS_MASTER_DETAILS_PROPERTYTAXRATE));
			propertyTaxLandRateModules = objectMapper
					.valueToTree(mdmsResponse.getMdmsRes().get(PTConstants.MDMS_MODULE_PROPERTYTAXRATE_LAND));
			propertyTaxLandRates = objectMapper
					.valueToTree(ulbModules.get(PTConstants.MDMS_MASTER_DETAILS_PROPERTYTAXRATE_LAND));
		}

		List<Property> properties = getProperties(calculateTaxRequest);
//		properties = removeAlreadyTaxCalculatedProperties(properties, calculateTaxRequest);
		Set<String> propertyIds = properties.stream()
				                            .map(Property::getPropertyId)
				                            .collect(Collectors.toSet());
		
		PtTaxCalculatorTrackerSearchCriteria criteria = PtTaxCalculatorTrackerSearchCriteria.builder()
				.propertyIds(propertyIds)
				.build();

		List<PtTaxCalculatorTracker> existingTrackers = propertyService.getTaxCalculatedProperties(criteria);
		
		Map<String, List<PtTaxCalculatorTracker>> trackerMap = existingTrackers.stream()
				.collect(Collectors.groupingBy(PtTaxCalculatorTracker::getPropertyId));

		for (Property property : properties) {

			if (!Boolean.TRUE.equals(property.getIsBilling())) {
				log.info("Skipping tax calculation for property {} as isBilling={}", property.getPropertyId(),
						property.getIsBilling());
				continue;
			}
			
			if (validateExistingBillOverlap(calculateTaxRequest, property, trackerMap)) {
				skippedCount.incrementAndGet();
				Set<String> overlapErrors = new HashSet<>();
				overlapErrors.add(
						"Bill already exists for property " + property.getPropertyId() + " for the selected period");
//				createFailureLog(property, calculateTaxRequest, null, overlapErrors);
				continue;
			}

			ArrayNode trackeradditionalDetails = objectMapper.createArrayNode();
			BigDecimal totalPropertyTax = BigDecimal.ZERO;
			BigDecimal oneDayPropertyTax = BigDecimal.ZERO;
			BigDecimal finalPropertyTax = BigDecimal.ZERO;
			BigDecimal rebateAmount = BigDecimal.ZERO;
			BigDecimal propertyTaxWithoutRebate = BigDecimal.ZERO;
			String ulbName = property.getTenantId().split("\\.")[1];
			JsonNode addressAdditionalDetails = objectMapper.valueToTree(property.getAddress().getAdditionalDetails());
			
			boolean previousBillUnpaid = false;

			if ("Shimla".equalsIgnoreCase(ulbName)) {
				previousBillUnpaid = hasPreviousUnpaidBill(calculateTaxRequest.getRequestInfo(),property.getPropertyId());
			}

			for (Unit unit : property.getUnits()) {
				BigDecimal totalRateableValue = BigDecimal.ZERO;
				BigDecimal upto100RateableValue = BigDecimal.ZERO;
				BigDecimal above100RateableValue = BigDecimal.ZERO;
				BigDecimal netRateableValue = BigDecimal.ZERO;
				BigDecimal netUpto100RateableValue = BigDecimal.ZERO;
				BigDecimal netAbove100RateableValue = BigDecimal.ZERO;
				BigDecimal structuralFactor = null;
				BigDecimal ageFactor = null;
				BigDecimal occupancyFactor = null;
				BigDecimal useFactor = null;
				BigDecimal locationFactor = null;
				BigDecimal oAndMRebateAmount = BigDecimal.ZERO;
				BigDecimal oAndMRebateUpto100Amount = BigDecimal.ZERO;
				BigDecimal oAndMRebateAbove100Amount = BigDecimal.ZERO;
				BigDecimal oAndMRebatePercentage = null;
				BigDecimal propertyTaxRatePercentage = null;
				BigDecimal propertyTaxLandRatePercentage = null;
				BigDecimal propertyTax = BigDecimal.ZERO;
				BigDecimal propertyTaxUpto100 = BigDecimal.ZERO;
				BigDecimal propertyTaxAbove100 = BigDecimal.ZERO;

				Set<String> errorSet = new HashSet<>();
				JsonNode unitAdditionalDetails = objectMapper.valueToTree(unit.getAdditionalDetails());

				// Populate factors from MDMS
				for (JsonNode buildingStructure : objectMapper.valueToTree(buildingStructures)) {
					if (ulbName.equalsIgnoreCase(buildingStructure.get("ulbName").asText())
							&& buildingStructure.get("structureType").asText()
									.equalsIgnoreCase(unitAdditionalDetails.get("propBuildingType").asText())) {
						structuralFactor = new BigDecimal(buildingStructure.get("rate").asText());
					}
				}
				for (JsonNode buildingEstablishmentYear : objectMapper.valueToTree(buildingEstablishmentYears)) {
					if (ulbName.equalsIgnoreCase(buildingEstablishmentYear.get("ulbName").asText())
							&& buildingEstablishmentYear.get("yearRange").asText().equalsIgnoreCase(
									ulbName + "." + unitAdditionalDetails.get("propYearOfCons").asText())) {
						ageFactor = new BigDecimal(buildingEstablishmentYear.get("rate").asText());
					}
				}
				for (JsonNode buildingPurpose : objectMapper.valueToTree(buildingPurposes)) {
					if (ulbName.equalsIgnoreCase(buildingPurpose.get("ulbName").asText())
							&& buildingPurpose.get("purposeName").asText()
									.equalsIgnoreCase(unitAdditionalDetails.get("propType").asText() + "." + ulbName)) {
						occupancyFactor = new BigDecimal(buildingPurpose.get("rate").asText());
					}
				}
				for (JsonNode buildingUse : objectMapper.valueToTree(buildingUses)) {
					if (ulbName.equalsIgnoreCase(buildingUse.get("ulbName").asText())
							&& buildingUse.get("useOfBuilding").asText().equalsIgnoreCase(
									ulbName + "." + unitAdditionalDetails.get("useOfBuilding").asText())) {
						useFactor = new BigDecimal(buildingUse.get("rate").asText());
					}
				}
				for (JsonNode zone : objectMapper.valueToTree(zones)) {
					if (ulbName.equalsIgnoreCase(zone.get("ulbName").asText()) && addressAdditionalDetails.get("zone")
							.asText().equalsIgnoreCase(zone.get("zoneName").asText())) {
						locationFactor = new BigDecimal(zone.get("propertyRate").asText());
					}
				}
				for (JsonNode overAllRebatePercentage : objectMapper.valueToTree(overAllRebatePercentages)) {
					if (ulbName.equalsIgnoreCase(overAllRebatePercentage.get("ulbName").asText())) {
						oAndMRebatePercentage = new BigDecimal(overAllRebatePercentage.get("rate").asText());
					}
				}

				// Validate factors
				if (structuralFactor == null && !property.getPropertyType().equalsIgnoreCase("VACANT"))
					errorSet.add(PTConstants.MDMS_MASTER_DETAILS_BUILDINGSTRUCTURE + " (F2) is missing in mdms");
				if (ageFactor == null && !property.getPropertyType().equalsIgnoreCase("VACANT"))
					errorSet.add(
							PTConstants.MDMS_MASTER_DETAILS_BUILDINGESTABLISHMENTYEAR + " (F3) is missing in mdms");
				if (occupancyFactor == null && !property.getPropertyType().equalsIgnoreCase("VACANT"))
					errorSet.add(PTConstants.MDMS_MASTER_DETAILS_BUILDINGPURPOSE + " (F4) is missing in mdms");
				if (useFactor == null && !property.getPropertyType().equalsIgnoreCase("VACANT"))
					errorSet.add(PTConstants.MDMS_MASTER_DETAILS_BUILDINGUSE + " (F5) is missing in mdms");
				if (locationFactor == null )
					errorSet.add(PTConstants.MDMS_MASTER_DETAILS_ZONES + " (F1) is missing in mdms");
				if (oAndMRebatePercentage == null)
					errorSet.add(PTConstants.MDMS_MASTER_DETAILS_OVERALLREBATE + " is missing in mdms");
				
				BigDecimal unitPropertyArea = new BigDecimal(unitAdditionalDetails.get("propArea").asText());
				BigDecimal newAreaunitPropertyArea = unitPropertyArea;
				newAreaunitPropertyArea = unitPropertyArea.subtract(BigDecimal.valueOf(100));
				    
				boolean isShimlaAreaAbove100 = unitPropertyArea.compareTo(BigDecimal.valueOf(100)) > 0
						&& ulbName.equalsIgnoreCase("Shimla")
						&& unitAdditionalDetails.get("propType").asText().equalsIgnoreCase("Self Residential")
					&& unitAdditionalDetails.get("useOfBuilding").asText().equalsIgnoreCase("Residential");

				// Calculate totalRateableValue
				if (structuralFactor != null && ageFactor != null && occupancyFactor != null && useFactor != null
						&& locationFactor != null && property.getPropertyType().equalsIgnoreCase("BUILTUP")) {
					
					if (isShimlaAreaAbove100) {

						upto100RateableValue = BigDecimal.valueOf(100).multiply(structuralFactor).multiply(ageFactor)
								.multiply(occupancyFactor).multiply(useFactor).multiply(locationFactor);

						above100RateableValue = newAreaunitPropertyArea.multiply(structuralFactor).multiply(ageFactor)
								.multiply(occupancyFactor).multiply(useFactor).multiply(locationFactor);

					} else {

						totalRateableValue = new BigDecimal(unitAdditionalDetails.get("propArea").asText())
								.multiply(structuralFactor).multiply(ageFactor).multiply(occupancyFactor)
								.multiply(useFactor).multiply(locationFactor);
					}
					
				} else if (property.getPropertyType().equalsIgnoreCase("VACANT") && locationFactor != null) {
					totalRateableValue = new BigDecimal(unitAdditionalDetails.get("propArea").asText())
							.multiply(locationFactor);
				} else {
					errorSet.add("PropertyType issue factor value is missing in mdms");
				}
				
				boolean isShimlaPlotOfLand = "Shimla".equalsIgnoreCase(ulbName) && (("PLOT OF LAND"
						.equalsIgnoreCase(unitAdditionalDetails.get("propType").asText())
						|| "RCC FRAME STRUCTURE".equalsIgnoreCase(unitAdditionalDetails.get("propType").asText()))
						&& ("VACANT".equalsIgnoreCase(unitAdditionalDetails.get("useOfBuilding").asText())
								|| "PLOT OF LAND".equalsIgnoreCase(unitAdditionalDetails.get("useOfBuilding").asText())
								|| "RCC FRAME STRUCTURE AND LANTER"
										.equalsIgnoreCase(unitAdditionalDetails.get("useOfBuilding").asText())
										|| "PLOT".equalsIgnoreCase(unitAdditionalDetails.get("useOfBuilding").asText())));
				
				// Net rateable value after rebate
				if (isShimlaPlotOfLand) {
				    netRateableValue = totalRateableValue;
				    oAndMRebatePercentage = BigDecimal.ZERO;
				}else if (!BigDecimal.ZERO.equals(totalRateableValue) && oAndMRebatePercentage != null || ((oAndMRebateUpto100Amount !=null && netUpto100RateableValue !=null) && (oAndMRebateAbove100Amount !=null && netAbove100RateableValue !=null))) {
					if (isShimlaAreaAbove100) {

						oAndMRebateUpto100Amount = upto100RateableValue
								.multiply(oAndMRebatePercentage.divide(BigDecimal.valueOf(100)));
						netUpto100RateableValue = upto100RateableValue.subtract(oAndMRebateUpto100Amount);

						oAndMRebateAbove100Amount = above100RateableValue
								.multiply(oAndMRebatePercentage.divide(BigDecimal.valueOf(100)));
						netAbove100RateableValue = above100RateableValue.subtract(oAndMRebateAbove100Amount);

					} else {
						oAndMRebateAmount = totalRateableValue
								.multiply(oAndMRebatePercentage.divide(BigDecimal.valueOf(100)));
						netRateableValue = totalRateableValue.subtract(oAndMRebateAmount);
					}
				}


				BigDecimal rateUpto100 = BigDecimal.ZERO;
				BigDecimal rateAbove100 = BigDecimal.ZERO;
				
				// Find property tax rate
				for (JsonNode propertyTaxRate : propertyTaxRates) {
					if (ulbName.equalsIgnoreCase(propertyTaxRate.get("ulbName").asText())
							&& addressAdditionalDetails.get("zone").asText().equalsIgnoreCase(
									propertyTaxRate.get("zoneName").asText().replaceFirst(ulbName + ".", ""))
							&& propertyTaxRate.get("purposeName").asText()
									.equalsIgnoreCase(unitAdditionalDetails.get("propType").asText() + "." + ulbName)
							&& propertyTaxRate.get("useOfBuilding").asText().equalsIgnoreCase(
									ulbName + "." + unitAdditionalDetails.get("useOfBuilding").asText())) {

						String propertyAreaString = propertyTaxRate.get("propertyArea").asText();
						

						if (isShimlaAreaAbove100) {

							if (isAreaWithinRange(propertyAreaString, BigDecimal.valueOf(100))) {
								rateUpto100 = new BigDecimal(propertyTaxRate.get("rate").asText());
							}

							if (isAreaWithinRange(propertyAreaString, unitPropertyArea)) {
								rateAbove100 = new BigDecimal(propertyTaxRate.get("rate").asText());
							}
						} else {
							if (isAreaWithinRange(propertyAreaString, unitPropertyArea)) {
								propertyTaxRatePercentage = new BigDecimal(propertyTaxRate.get("rate").asText());
							}

						}
					}
				}
				
				for (JsonNode propertyTaxLandRate : propertyTaxLandRates) {
					if (ulbName.equalsIgnoreCase(propertyTaxLandRate.get("ulbName").asText())
							&& addressAdditionalDetails.get("zone").asText().equalsIgnoreCase(
									propertyTaxLandRate.get("zone").asText().replaceFirst(ulbName + ".", ""))
							) {

						propertyTaxLandRatePercentage = new BigDecimal(propertyTaxLandRate.get("rate").asText());
						
					}
				}

				if ((!BigDecimal.ZERO.equals(netRateableValue)  ||  !BigDecimal.ZERO.equals(netUpto100RateableValue) && !BigDecimal.ZERO.equals(netAbove100RateableValue))
				        && (propertyTaxRatePercentage != null || (rateUpto100 !=null && rateAbove100 !=null))
				        && !property.getPropertyType().equalsIgnoreCase("VACANT")) {

				    // Case 1: NOT VACANT
					if (isShimlaAreaAbove100) {

						propertyTaxUpto100 = netUpto100RateableValue
								.multiply(rateUpto100.divide(BigDecimal.valueOf(100)));

						propertyTaxAbove100 = netAbove100RateableValue
								.multiply(rateAbove100.divide(BigDecimal.valueOf(100)));

						propertyTax = propertyTaxUpto100.add(propertyTaxAbove100);

					} else {
						
						 propertyTax = netRateableValue.multiply(
				            propertyTaxRatePercentage.divide(BigDecimal.valueOf(100)));
					}

				} else if (!BigDecimal.ZERO.equals(netRateableValue) 
				        && propertyTaxLandRatePercentage != null 
				        && property.getPropertyType().equalsIgnoreCase("VACANT")) {

				    // Case 2: VACANT
				    propertyTax = netRateableValue.multiply(
				            propertyTaxLandRatePercentage.divide(BigDecimal.valueOf(100)));

				} else {
				    // Common error for both cases
				    if (property.getPropertyType().equalsIgnoreCase("VACANT")) {
				        errorSet.add(PTConstants.MDMS_MASTER_DETAILS_PROPERTYTAXRATE_LAND + " is missing in mdms");
				    } else {
				        errorSet.add(PTConstants.MDMS_MASTER_DETAILS_PROPERTYTAXRATE + " is missing in mdms");
				    }
				}

				if (!BigDecimal.ZERO.equals(propertyTax)) {
					totalPropertyTax = totalPropertyTax.add(propertyTax);
				}

				if (BigDecimal.ZERO.equals(totalPropertyTax)) {
					errorSet.add("Calculated tax is " + totalPropertyTax);
				}

				if (!CollectionUtils.isEmpty(errorSet)) {
					errorMap.put(property.getPropertyId(), errorSet);
				} else {
					// Add details for tracker
					BigDecimal taxPerDay = propertyTax.divide(BigDecimal.valueOf(365), 6, RoundingMode.HALF_UP);
					BigDecimal propertyTaxGenerated = taxPerDay.multiply(days);
					ObjectNode node = objectMapper.createObjectNode();

					node.put("propertyType", property.getPropertyType())
							.put(PTConstants.MDMS_MASTER_DETAILS_ZONES + "(f1)",
									locationFactor != null ? locationFactor.doubleValue() : 0)
							.put(PTConstants.MDMS_MASTER_DETAILS_OVERALLREBATE,
									oAndMRebatePercentage != null ? oAndMRebatePercentage.doubleValue() : 0)
							.put(PTConstants.MDMS_MASTER_DETAILS_PROPERTYTAXRATE,
									propertyTaxRatePercentage != null ? propertyTaxRatePercentage.doubleValue() : 0)
							.put("netRateableValue", netRateableValue.doubleValue())
							.put("propertyArea", unitAdditionalDetails.get("propArea").asText())
							.put("propertyTaxCalculated", propertyTax.doubleValue())
							.put("propertyTaxGenerated", propertyTaxGenerated.doubleValue())
							.put("days", days.doubleValue()).put("unitId", unit.getId());

					if (property.getPropertyType().equalsIgnoreCase("BUILTUP")) {
						node.put(PTConstants.MDMS_MASTER_DETAILS_BUILDINGSTRUCTURE + "(f2)",
								structuralFactor != null ? structuralFactor.doubleValue() : 0)
								.put(PTConstants.MDMS_MASTER_DETAILS_BUILDINGESTABLISHMENTYEAR + "(f3)",
										ageFactor != null ? ageFactor.doubleValue() : 0)
								.put(PTConstants.MDMS_MASTER_DETAILS_BUILDINGPURPOSE + "(f4)",
										occupancyFactor != null ? occupancyFactor.doubleValue() : 0)
								.put(PTConstants.MDMS_MASTER_DETAILS_BUILDINGUSE + "(f5)",
										useFactor != null ? useFactor.doubleValue() : 0);
					}

					trackeradditionalDetails.add(node);
				}
			}

			if (!errorMap.containsKey(property.getPropertyId()) && !BigDecimal.ZERO.equals(totalPropertyTax)) {

				oneDayPropertyTax = totalPropertyTax.divide(BigDecimal.valueOf(365), 6, RoundingMode.HALF_UP);
				finalPropertyTax = oneDayPropertyTax.multiply(days);
				
				if ("Dalhousie".equalsIgnoreCase(ulbName) && finalPropertyTax.compareTo(BigDecimal.valueOf(1200)) < 0) {
					finalPropertyTax = BigDecimal.valueOf(1200);
				}
				
				propertyTaxWithoutRebate = finalPropertyTax;

				// early payment rebate
				BigDecimal epRebatePercentage = null;
				for (JsonNode earlyPaymentRebatePercentage : objectMapper.valueToTree(earlyPaymentRebatePercentages)) {
					if (ulbName.equalsIgnoreCase(earlyPaymentRebatePercentage.get("ulbName").asText())) {
						epRebatePercentage = new BigDecimal(earlyPaymentRebatePercentage.get("rate").asText());
					}
				}
				if (epRebatePercentage != null && !previousBillUnpaid) {
					rebateAmount = finalPropertyTax.multiply(epRebatePercentage.divide(BigDecimal.valueOf(100)));
					finalPropertyTax = finalPropertyTax.subtract(rebateAmount);
				}
				
				BillResponse billResponse = generateDemandAndBill(calculateTaxRequest, property, finalPropertyTax);

				if (billResponse != null && !CollectionUtils.isEmpty(billResponse.getBill())) {

					PropertyBillFailure propertyBillFailure = enrichmentService.enrichPtBillFailure(property,
							calculateTaxRequest, billResponse, null);
					propertyService.removePtBillFailure(propertyBillFailure);
					List<BillDetail> relevantBillDetails = billResponse.getBill().stream()
							.flatMap(bill -> bill.getBillDetails().stream()).filter(bd -> {
								long from = bd.getFromPeriod();
								long to = bd.getToPeriod();
								long fyFrom = calculateTaxRequest.getFromDate().getTime();
								long fyTo = calculateTaxRequest.getToDate().getTime();
								return (from >= fyFrom && to <= fyTo);
							}).collect(Collectors.toList());

					for (BillDetail billDetail : relevantBillDetails) {
						String demandId = billDetail.getDemandId();
						String ward = null;

						if (property.getAddress() != null && property.getAddress().getAdditionalDetails() != null) {
							JsonNode addrDetails = objectMapper
									.valueToTree(property.getAddress().getAdditionalDetails());
							JsonNode wardNode = addrDetails.get("wardNumber");

							if (wardNode != null && !wardNode.isNull()) {
								ward = wardNode.asText();
							}
						}
						Bill parentBill = billResponse.getBill().stream()
								.filter(b -> b.getBillDetails().contains(billDetail)).findFirst().orElse(null);

						PtTaxCalculatorTrackerRequest trackerRequest = enrichmentService
								.enrichTaxCalculatorTrackerCreateRequest(property, calculateTaxRequest,
										finalPropertyTax, trackeradditionalDetails,
										Collections.singletonList(parentBill), rebateAmount, propertyTaxWithoutRebate,
										demandId, ward);

						PtTaxCalculatorTracker tracker = propertyService.saveToPtTaxCalculatorTracker(trackerRequest);
						taxCalculatorTrackers.add(tracker);
						try {
							PtTaxCalculatorTrackerSearchCriteria prevCriteria = PtTaxCalculatorTrackerSearchCriteria
									.builder().propertyIds(Collections.singleton(property.getPropertyId()))
									.billStatus(new HashSet<>(Arrays.asList(
										    BillStatus.ACTIVE, 
										    BillStatus.PARTIALLY_PAID,
										    BillStatus.ADVANCE_ADJUSTED
										)))
									.build();

							List<PtTaxCalculatorTracker> prevTrackers = propertyService
									.getTaxCalculatedProperties(prevCriteria);

							if (!CollectionUtils.isEmpty(prevTrackers)) {
								for (PtTaxCalculatorTracker prev : prevTrackers) {
									if (prev.getUuid() != null && prev.getUuid().equals(tracker.getUuid()))
										continue;
									if (prev.getBillStatus() == BillStatus.PAID)
										continue;

									prev.setBillStatus(BillStatus.EXPIRED);

									PtTaxCalculatorTrackerRequest prevUpdateRequest = enrichmentService
											.enrichTaxCalculatorTrackerUpdateRequest(prev,
													trackerRequest.getRequestInfo());

									try {
										propertyService.updatePtTaxCalculatorTracker(prevUpdateRequest);
										log.info("Marked previous tracker {} as EXPIRED for property {}",
												prev.getUuid(), property.getPropertyId());
									} catch (Exception e) {
										log.error("Failed to update previous tracker {} for property {}",
												prev.getUuid(), property.getPropertyId(), e);
									}
								}
							}
						} catch (Exception ex) {
							log.error("Failed to expire previous trackers for property {}", property.getPropertyId(),
									ex);
						}

						// Notification
						try {
							notificationService.triggerNotificationsGenerateBill(tracker, parentBill,
									trackerRequest.getRequestInfo(), calculateTaxRequest.getUlbNames().toString());
						} catch (Exception ex) {
							log.error("Notification failed for billId {}", parentBill.getId(), ex);
						}

						// SMS Tracker (same as your existing code)
						try {
							if (CollectionUtils.isEmpty(property.getOwners())) {
								throw new RuntimeException("No owners found for property " + property.getPropertyId());
							}

							ObjectNode smsRequestJson = notificationService
									.buildGeneratePropertyBillSmsRequest(property, parentBill, tracker);

							Map<String, Object> smsRequestMap = new ObjectMapper().convertValue(smsRequestJson,
									Map.class);

							StringBuilder smsTrackerUri = new StringBuilder();
							smsTrackerUri.append(smsHost).append(smsTrackerCreateEndpoint);

							Map<String, Object> smsTrackerRequest = new HashMap<>();
							smsTrackerRequest.put("uuid", UUID.randomUUID().toString());
							smsTrackerRequest.put("amount", finalPropertyTax);
							smsTrackerRequest.put("applicationNo", property.getPropertyId());
							smsTrackerRequest.put("tenantId", property.getTenantId());
							smsTrackerRequest.put("service", "PROPERTY");
							smsTrackerRequest.put("fromDate",
									new SimpleDateFormat("dd-MM-yyyy").format(calculateTaxRequest.getFromDate()));
							smsTrackerRequest.put("toDate",
									new SimpleDateFormat("dd-MM-yyyy").format(calculateTaxRequest.getToDate()));
							smsTrackerRequest.put("createdBy", "system");
							smsTrackerRequest.put("createdTime", System.currentTimeMillis());
							smsTrackerRequest.put("billId", parentBill.getId());
							smsTrackerRequest.put("demandId", demandId);
							smsTrackerRequest.put("smsStatus", false);
							smsTrackerRequest.put("additionalDetail", trackeradditionalDetails);
							smsTrackerRequest.put("ownerMobileNo", property.getOwners().get(0).getMobileNumber());
							smsTrackerRequest.put("ownerName", property.getOwners().get(0).getName());
							smsTrackerRequest.put("smsRequest", smsRequestMap);
							smsTrackerRequest.put("smsResponse", null);
							smsTrackerRequest.put("ward", ward);

							restCallRepository.fetchResult(smsTrackerUri, smsTrackerRequest);
							log.info("SMS tracker entry created for billId {} demandId {}", parentBill.getId(),
									demandId);

						} catch (Exception e) {
							log.error("SMS tracker creation failed for billId {}", parentBill.getId(), e);
						}
						syncTrackerWithBillStatus(tracker, calculateTaxRequest);
					}
				} else {
					createFailureLog(property, calculateTaxRequest, billResponse, null);
				}

			
			} else {
				createFailureLog(property, calculateTaxRequest, null, errorMap.get(property.getPropertyId()));
			}
		}

		int generatedCount = taxCalculatorTrackers.size();
		int skipped = skippedCount.get();

		String message = null;
		

		if (generatedCount > 0 && skipped > 0) {
			message = "Bills generated successfully for " + generatedCount + " Property Id(s). Failed for " + skipped
					+ " Property Id(s) due to overlapping bill periods.";
		} else if (generatedCount > 0) {
			message = "Bills generated successfully for " + generatedCount + " Property Id(s).";
		} else if (skipped > 0) {
			message = "No bills generated. " + skipped + " Property Id(s) failed due to overlapping bill periods.";
		}

		return CalculateTaxResponse.builder().taxCalculatorTrackers(taxCalculatorTrackers).message(message).build();
	}
	
	private boolean validateExistingBillOverlap(CalculateTaxRequest calculateTaxRequest, Property property,
			Map<String, List<PtTaxCalculatorTracker>> trackerMap) {

		List<PtTaxCalculatorTracker> trackers = trackerMap.get(property.getPropertyId());

		if (CollectionUtils.isEmpty(trackers)) {
			return false;
		}

		Long newFrom = calculateTaxRequest.getFromDate().getTime();
		Long newTo = calculateTaxRequest.getToDate().getTime();

		for (PtTaxCalculatorTracker tracker : trackers) {

			if (tracker.getBillStatus() == BillStatus.CANCELLED) {
				continue;
			}

			Long existingFrom = tracker.getFromDate().getTime();
			Long existingTo = tracker.getToDate().getTime();

			boolean overlap = !(existingTo < newFrom || existingFrom > newTo);

			if (overlap) {
				return true;
			}
		}

		return false;
		}

	public List<CalculateTaxPreviewResponse> taxCalculatorPreview(CalculateTaxRequest calculateTaxRequest) {

		List<CalculateTaxPreviewResponse> previewResponses = new ArrayList<>();

		JsonNode ulbModules = null;
		JsonNode propertyTaxRateModules = null;
		JsonNode propertyTaxLandRateModules = null;
		JsonNode zones = null;
		JsonNode buildingStructures = null;
		JsonNode buildingEstablishmentYears = null;
		JsonNode buildingPurposes = null;
		JsonNode buildingUses = null;
		JsonNode overAllRebatePercentages = null;
		JsonNode earlyPaymentRebatePercentages = null;
		JsonNode propertyTaxRates = null;
		JsonNode propertyTaxLandRates = null;
		BigDecimal days = calculateDays(calculateTaxRequest);

		Map<String, Set<String>> errorMap = new HashMap<>();
		String u = "[?(@.ulbName == \"" + calculateTaxRequest.getUlbNames().iterator().next() + "\")]";
		MdmsResponse mdmsResponse = mdmsService.getMdmsData(calculateTaxRequest.getRequestInfo(), u );
		if (mdmsResponse != null && mdmsResponse.getMdmsRes() != null
				&& mdmsResponse.getMdmsRes().get(PTConstants.MDMS_MODULE_ULBS) != null) {

			ulbModules = objectMapper.valueToTree(mdmsResponse.getMdmsRes().get(PTConstants.MDMS_MODULE_ULBS));
			propertyTaxRateModules = objectMapper
					.valueToTree(mdmsResponse.getMdmsRes().get(PTConstants.MDMS_MODULE_PROPERTYTAXRATE));
			
			propertyTaxLandRateModules = objectMapper
					.valueToTree(mdmsResponse.getMdmsRes().get(PTConstants.MDMS_MODULE_PROPERTYTAXRATE_LAND));

			zones = objectMapper.valueToTree(ulbModules.get(PTConstants.MDMS_MASTER_DETAILS_ZONES));
			buildingStructures = objectMapper
					.valueToTree(ulbModules.get(PTConstants.MDMS_MASTER_DETAILS_BUILDINGSTRUCTURE));
			buildingEstablishmentYears = objectMapper
					.valueToTree(ulbModules.get(PTConstants.MDMS_MASTER_DETAILS_BUILDINGESTABLISHMENTYEAR));
			buildingPurposes = objectMapper
					.valueToTree(ulbModules.get(PTConstants.MDMS_MASTER_DETAILS_BUILDINGPURPOSE));
			buildingUses = objectMapper.valueToTree(ulbModules.get(PTConstants.MDMS_MASTER_DETAILS_BUILDINGUSE));
			overAllRebatePercentages = objectMapper
					.valueToTree(ulbModules.get(PTConstants.MDMS_MASTER_DETAILS_OVERALLREBATE));
			earlyPaymentRebatePercentages = objectMapper
					.valueToTree(ulbModules.get(PTConstants.MDMS_MASTER_DETAILS_EARLYPAYMENTREBATE));
			propertyTaxRates = objectMapper
					.valueToTree(propertyTaxRateModules.get(PTConstants.MDMS_MASTER_DETAILS_PROPERTYTAXRATE));
			propertyTaxLandRates = objectMapper
					.valueToTree(ulbModules.get(PTConstants.MDMS_MASTER_DETAILS_PROPERTYTAXRATE_LAND));
		}

		List<Property> properties = getProperties(calculateTaxRequest);
		properties = removeAlreadyTaxCalculatedProperties(properties, calculateTaxRequest);
		Property property = properties.get(0);

		if (Boolean.TRUE.equals(property.getIsBilling())) {
			log.info("Skipping tax calculation for property {} as isBilling={}", property.getPropertyId(),
					property.getIsBilling());

			ArrayNode trackeradditionalDetails = objectMapper.createArrayNode();
			BigDecimal totalPropertyTax = BigDecimal.ZERO;
			BigDecimal oneDayPropertyTax = BigDecimal.ZERO;
			BigDecimal finalPropertyTax = BigDecimal.ZERO;
			BigDecimal rebateAmount = BigDecimal.ZERO;
			BigDecimal propertyTaxWithoutRebate = BigDecimal.ZERO;
			String ulbName = property.getTenantId().split("\\.")[1];
			JsonNode addressAdditionalDetails = objectMapper.valueToTree(property.getAddress().getAdditionalDetails());

			for (Unit unit : property.getUnits()) {
				BigDecimal totalRateableValue = BigDecimal.ZERO;
				BigDecimal upto100RateableValue = BigDecimal.ZERO;
				BigDecimal above100RateableValue = BigDecimal.ZERO;
				BigDecimal netRateableValue = BigDecimal.ZERO;
				BigDecimal netUpto100RateableValue = BigDecimal.ZERO;
				BigDecimal netAbove100RateableValue = BigDecimal.ZERO;
				BigDecimal structuralFactor = null;
				BigDecimal ageFactor = null;
				BigDecimal occupancyFactor = null;
				BigDecimal useFactor = null;
				BigDecimal locationFactor = null;
				BigDecimal oAndMRebateAmount = BigDecimal.ZERO;
				BigDecimal oAndMRebateUpto100Amount = BigDecimal.ZERO;
				BigDecimal oAndMRebateAbove100Amount = BigDecimal.ZERO;
				BigDecimal oAndMRebatePercentage = null;
				BigDecimal propertyTaxRatePercentage = null;
				BigDecimal propertyTaxLandRatePercentage = null;
				BigDecimal propertyTax = BigDecimal.ZERO;
				BigDecimal propertyTaxUpto100 = BigDecimal.ZERO;
				BigDecimal propertyTaxAbove100 = BigDecimal.ZERO;

				Set<String> errorSet = new HashSet<>();
				JsonNode unitAdditionalDetails = objectMapper.valueToTree(unit.getAdditionalDetails());

				// Populate factors from MDMS
				for (JsonNode buildingStructure : objectMapper.valueToTree(buildingStructures)) {
					if (ulbName.equalsIgnoreCase(buildingStructure.get("ulbName").asText())
							&& buildingStructure.get("structureType").asText()
									.equalsIgnoreCase(unitAdditionalDetails.get("propBuildingType").asText())) {
						structuralFactor = new BigDecimal(buildingStructure.get("rate").asText());
					}
				}
				for (JsonNode buildingEstablishmentYear : objectMapper.valueToTree(buildingEstablishmentYears)) {
					if (ulbName.equalsIgnoreCase(buildingEstablishmentYear.get("ulbName").asText())
							&& buildingEstablishmentYear.get("yearRange").asText().equalsIgnoreCase(
									ulbName + "." + unitAdditionalDetails.get("propYearOfCons").asText())) {
						ageFactor = new BigDecimal(buildingEstablishmentYear.get("rate").asText());
					}
				}
				for (JsonNode buildingPurpose : objectMapper.valueToTree(buildingPurposes)) {
					if (ulbName.equalsIgnoreCase(buildingPurpose.get("ulbName").asText())
							&& buildingPurpose.get("purposeName").asText()
									.equalsIgnoreCase(unitAdditionalDetails.get("propType").asText() + "." + ulbName)) {
						occupancyFactor = new BigDecimal(buildingPurpose.get("rate").asText());
					}
				}
				for (JsonNode buildingUse : objectMapper.valueToTree(buildingUses)) {
					if (ulbName.equalsIgnoreCase(buildingUse.get("ulbName").asText())
							&& buildingUse.get("useOfBuilding").asText().equalsIgnoreCase(
									ulbName + "." + unitAdditionalDetails.get("useOfBuilding").asText())) {
						useFactor = new BigDecimal(buildingUse.get("rate").asText());
					}
				}
				for (JsonNode zone : objectMapper.valueToTree(zones)) {
					if (ulbName.equalsIgnoreCase(zone.get("ulbName").asText()) && addressAdditionalDetails.get("zone")
							.asText().equalsIgnoreCase(zone.get("zoneName").asText())) {
						locationFactor = new BigDecimal(zone.get("propertyRate").asText());
					}
				}
				for (JsonNode overAllRebatePercentage : objectMapper.valueToTree(overAllRebatePercentages)) {
					if (ulbName.equalsIgnoreCase(overAllRebatePercentage.get("ulbName").asText())) {
						oAndMRebatePercentage = new BigDecimal(overAllRebatePercentage.get("rate").asText());
					}
				}

				// Validate factors
				if (structuralFactor == null && !property.getPropertyType().equalsIgnoreCase("VACANT"))
					errorSet.add(PTConstants.MDMS_MASTER_DETAILS_BUILDINGSTRUCTURE + " (F2) is missing in mdms");
				if (ageFactor == null && !property.getPropertyType().equalsIgnoreCase("VACANT"))
					errorSet.add(
							PTConstants.MDMS_MASTER_DETAILS_BUILDINGESTABLISHMENTYEAR + " (F3) is missing in mdms");
				if (occupancyFactor == null && property.getPropertyType().equalsIgnoreCase("VACANT"))
					errorSet.add(PTConstants.MDMS_MASTER_DETAILS_BUILDINGPURPOSE + " (F4) is missing in mdms");
				if (useFactor == null && !property.getPropertyType().equalsIgnoreCase("VACANT"))
					errorSet.add(PTConstants.MDMS_MASTER_DETAILS_BUILDINGUSE + " (F5) is missing in mdms");
				if (locationFactor == null)
					errorSet.add(PTConstants.MDMS_MASTER_DETAILS_ZONES + " (F1) is missing in mdms");
				if (oAndMRebatePercentage == null)
					errorSet.add(PTConstants.MDMS_MASTER_DETAILS_OVERALLREBATE + " is missing in mdms");
				BigDecimal unitPropertyArea = new BigDecimal(unitAdditionalDetails.get("propArea").asText());
				BigDecimal newAreaunitPropertyArea = unitPropertyArea;
				 newAreaunitPropertyArea =
				            unitPropertyArea.subtract(BigDecimal.valueOf(100));
				    
					boolean isShimlaAreaAbove100 = unitPropertyArea.compareTo(BigDecimal.valueOf(100)) > 0
							&& ulbName.equalsIgnoreCase("Shimla")
							&& unitAdditionalDetails.get("propType").asText().equalsIgnoreCase("Self Residential")
						&& unitAdditionalDetails.get("useOfBuilding").asText().equalsIgnoreCase("Residential");

				// Calculate totalRateableValue
				if (structuralFactor != null && ageFactor != null && occupancyFactor != null && useFactor != null
						&& locationFactor != null && property.getPropertyType().equalsIgnoreCase("BUILTUP")) {

					if (isShimlaAreaAbove100) {

						upto100RateableValue = BigDecimal.valueOf(100).multiply(structuralFactor).multiply(ageFactor)
								.multiply(occupancyFactor).multiply(useFactor).multiply(locationFactor);

						above100RateableValue = newAreaunitPropertyArea.multiply(structuralFactor).multiply(ageFactor)
								.multiply(occupancyFactor).multiply(useFactor).multiply(locationFactor);

					} else {

						totalRateableValue = new BigDecimal(unitAdditionalDetails.get("propArea").asText())
								.multiply(structuralFactor).multiply(ageFactor).multiply(occupancyFactor)
								.multiply(useFactor).multiply(locationFactor);
					}
				} else if (property.getPropertyType().equalsIgnoreCase("VACANT") && locationFactor != null) {
					totalRateableValue = new BigDecimal(unitAdditionalDetails.get("propArea").asText())
							.multiply(locationFactor);
				} else {
					errorSet.add("PropertyType issue factor value is missing in mdms");
				}
				
				boolean isShimlaPlotOfLand = "Shimla".equalsIgnoreCase(ulbName) && (("PLOT OF LAND"
						.equalsIgnoreCase(unitAdditionalDetails.get("propType").asText())
						|| "RCC FRAME STRUCTURE".equalsIgnoreCase(unitAdditionalDetails.get("propType").asText()))
						&& ("VACANT".equalsIgnoreCase(unitAdditionalDetails.get("useOfBuilding").asText())
								|| "PLOT OF LAND".equalsIgnoreCase(unitAdditionalDetails.get("useOfBuilding").asText())
								|| "RCC FRAME STRUCTURE AND LANTER"
										.equalsIgnoreCase(unitAdditionalDetails.get("useOfBuilding").asText())
								|| "PLOT".equalsIgnoreCase(unitAdditionalDetails.get("useOfBuilding").asText())));

				// Net rateable value after rebate
				if (isShimlaPlotOfLand) {
				    netRateableValue = totalRateableValue;
				    oAndMRebatePercentage = BigDecimal.ZERO;
				}else if (!BigDecimal.ZERO.equals(totalRateableValue) && oAndMRebatePercentage != null || ((oAndMRebateUpto100Amount !=null && netUpto100RateableValue !=null) && (oAndMRebateAbove100Amount !=null && netAbove100RateableValue !=null))) {
					if (isShimlaAreaAbove100) {

						oAndMRebateUpto100Amount = upto100RateableValue
								.multiply(oAndMRebatePercentage.divide(BigDecimal.valueOf(100)));
						netUpto100RateableValue = upto100RateableValue.subtract(oAndMRebateUpto100Amount);

						oAndMRebateAbove100Amount = above100RateableValue
								.multiply(oAndMRebatePercentage.divide(BigDecimal.valueOf(100)));
						netAbove100RateableValue = above100RateableValue.subtract(oAndMRebateAbove100Amount);

					} else {
						oAndMRebateAmount = totalRateableValue
								.multiply(oAndMRebatePercentage.divide(BigDecimal.valueOf(100)));
						netRateableValue = totalRateableValue.subtract(oAndMRebateAmount);
					}
				}

				BigDecimal rateUpto100 = BigDecimal.ZERO;
				BigDecimal rateAbove100 = BigDecimal.ZERO;

				// Find property tax rate
				for (JsonNode propertyTaxRate : propertyTaxRates) {
					if (ulbName.equalsIgnoreCase(propertyTaxRate.get("ulbName").asText())
							&& addressAdditionalDetails.get("zone").asText().equalsIgnoreCase(
									propertyTaxRate.get("zoneName").asText().replaceFirst(ulbName + ".", ""))
							&& propertyTaxRate.get("purposeName").asText()
									.equalsIgnoreCase(unitAdditionalDetails.get("propType").asText() + "." + ulbName)
							&& propertyTaxRate.get("useOfBuilding").asText().equalsIgnoreCase(
									ulbName + "." + unitAdditionalDetails.get("useOfBuilding").asText())) {

						String propertyAreaString = propertyTaxRate.get("propertyArea").asText();

						if (isShimlaAreaAbove100) {

							if (isAreaWithinRange(propertyAreaString, BigDecimal.valueOf(100))) {
								rateUpto100 = new BigDecimal(propertyTaxRate.get("rate").asText());
							}

							if (isAreaWithinRange(propertyAreaString, unitPropertyArea)) {
								rateAbove100 = new BigDecimal(propertyTaxRate.get("rate").asText());
							}
						} else {
							if (isAreaWithinRange(propertyAreaString, unitPropertyArea)) {
								propertyTaxRatePercentage = new BigDecimal(propertyTaxRate.get("rate").asText());
							}

						}

					}
				}

				// Find property tax rate for land
				for (JsonNode propertyTaxLandRate : propertyTaxLandRates) {
					if (ulbName.equalsIgnoreCase(propertyTaxLandRate.get("ulbName").asText())
							&& addressAdditionalDetails.get("zone").asText().equalsIgnoreCase(
									propertyTaxLandRate.get("zone").asText().replaceFirst(ulbName + ".", ""))
							) {

						propertyTaxLandRatePercentage = new BigDecimal(propertyTaxLandRate.get("rate").asText());
						
					}
				}
				if ((!BigDecimal.ZERO.equals(netRateableValue)  ||  !BigDecimal.ZERO.equals(netUpto100RateableValue) && !BigDecimal.ZERO.equals(netAbove100RateableValue))
				        && (propertyTaxRatePercentage != null || (rateUpto100 !=null && rateAbove100 !=null))
				        && !property.getPropertyType().equalsIgnoreCase("VACANT")) {

				    // Case 1: NOT VACANT
					if (isShimlaAreaAbove100) {

						propertyTaxUpto100 = netUpto100RateableValue
								.multiply(rateUpto100.divide(BigDecimal.valueOf(100)));

						propertyTaxAbove100 = netAbove100RateableValue
								.multiply(rateAbove100.divide(BigDecimal.valueOf(100)));

						propertyTax = propertyTaxUpto100.add(propertyTaxAbove100);

					} else {
						
						 propertyTax = netRateableValue.multiply(
				            propertyTaxRatePercentage.divide(BigDecimal.valueOf(100)));
					}

				} else if (!BigDecimal.ZERO.equals(netRateableValue) 
				        && propertyTaxLandRatePercentage != null 
				        && property.getPropertyType().equalsIgnoreCase("VACANT")) {

				    // Case 2: VACANT
				    propertyTax = netRateableValue.multiply(
				            propertyTaxLandRatePercentage.divide(BigDecimal.valueOf(100)));

				} else {
				    // Common error for both cases
				    if (property.getPropertyType().equalsIgnoreCase("VACANT")) {
				        errorSet.add(PTConstants.MDMS_MASTER_DETAILS_PROPERTYTAXRATE_LAND + " is missing in mdms");
				    } else {
				        errorSet.add(PTConstants.MDMS_MASTER_DETAILS_PROPERTYTAXRATE + " is missing in mdms");
				    }
				}

				if (!BigDecimal.ZERO.equals(propertyTax)) {
					totalPropertyTax = totalPropertyTax.add(propertyTax);
				}

				if (BigDecimal.ZERO.equals(totalPropertyTax)) {
					errorSet.add("Calculated tax is " + totalPropertyTax);
				}

				if (!CollectionUtils.isEmpty(errorSet)) {
					errorMap.put(property.getPropertyId(), errorSet);
				} else {
					// Add details for tracker
					BigDecimal taxPerDay = propertyTax.divide(BigDecimal.valueOf(365), 6, RoundingMode.HALF_UP);
					BigDecimal propertyTaxGenerated = taxPerDay.multiply(days);
					ObjectNode node = objectMapper.createObjectNode();

					String zoneLabel = null;
					String structureLabel = null;
					String ageLabel = null;
					String occupancyLabel = null;
					String useLabel = null;
					String floorNo = unitAdditionalDetails.has("floorNo")
							? unitAdditionalDetails.get("floorNo").asText()
							: "0";

					for (JsonNode n : buildingStructures)
						if (ulbName.equalsIgnoreCase(n.get("ulbName").asText()) && n.get("structureType").asText()
								.equalsIgnoreCase(unitAdditionalDetails.get("propBuildingType").asText())) {

							structuralFactor = new BigDecimal(n.get("rate").asText());
							structureLabel = n.get("structureType").asText();
						}

					for (JsonNode n : buildingEstablishmentYears)
						if (ulbName.equalsIgnoreCase(n.get("ulbName").asText())
								&& n.get("yearRange").asText().equalsIgnoreCase(
										ulbName + "." + unitAdditionalDetails.get("propYearOfCons").asText())) {

							ageFactor = new BigDecimal(n.get("rate").asText());
							ageLabel = unitAdditionalDetails.get("propYearOfCons").asText();
						}

					for (JsonNode n : buildingPurposes)
						if (ulbName.equalsIgnoreCase(n.get("ulbName").asText()) && n.get("purposeName").asText()
								.equalsIgnoreCase(unitAdditionalDetails.get("propType").asText() + "." + ulbName)) {

							occupancyFactor = new BigDecimal(n.get("rate").asText());
							occupancyLabel = unitAdditionalDetails.get("propType").asText();
						}

					for (JsonNode n : buildingUses)
						if (ulbName.equalsIgnoreCase(n.get("ulbName").asText())
								&& n.get("useOfBuilding").asText().equalsIgnoreCase(
										ulbName + "." + unitAdditionalDetails.get("useOfBuilding").asText())) {

							useFactor = new BigDecimal(n.get("rate").asText());
							useLabel = unitAdditionalDetails.get("useOfBuilding").asText();
						}

					for (JsonNode n : zones)
						if (ulbName.equalsIgnoreCase(n.get("ulbName").asText()) && addressAdditionalDetails.get("zone")
								.asText().equalsIgnoreCase(n.get("zoneName").asText())) {

							locationFactor = new BigDecimal(n.get("propertyRate").asText());
							zoneLabel = n.get("zoneName").asText();
						}

					node.put("floorNo", floorNo);
					node.put("zone", zoneLabel);
					node.put("structure", structureLabel);
					node.put("age", ageLabel);
					node.put("occupancy", occupancyLabel);
					node.put("use", useLabel);
					node.put("plinthArea", unitAdditionalDetails.get("propArea").asText());
					node.put("taxAmount", propertyTax.setScale(2, RoundingMode.HALF_UP));
					trackeradditionalDetails.add(node);
				}
			}

			if (!errorMap.containsKey(property.getPropertyId()) && !BigDecimal.ZERO.equals(totalPropertyTax)) {

				oneDayPropertyTax = totalPropertyTax.divide(BigDecimal.valueOf(365), 6, RoundingMode.HALF_UP);
				finalPropertyTax = oneDayPropertyTax.multiply(days);
				
				if ("Dalhousie".equalsIgnoreCase(ulbName) && finalPropertyTax.compareTo(BigDecimal.valueOf(1200)) < 0) {
					finalPropertyTax = BigDecimal.valueOf(1200);
				}
				
				propertyTaxWithoutRebate = finalPropertyTax;

				// early payment rebate
				BigDecimal epRebatePercentage = null;
				for (JsonNode earlyPaymentRebatePercentage : objectMapper.valueToTree(earlyPaymentRebatePercentages)) {
					if (ulbName.equalsIgnoreCase(earlyPaymentRebatePercentage.get("ulbName").asText())) {
						epRebatePercentage = new BigDecimal(earlyPaymentRebatePercentage.get("rate").asText());
					}
				}
				
				boolean previousBillUnpaid = false;

				if ("Shimla".equalsIgnoreCase(ulbName)) {
					previousBillUnpaid = hasPreviousUnpaidBill(calculateTaxRequest.getRequestInfo(),property.getPropertyId());
				}

				if (epRebatePercentage != null  && !previousBillUnpaid) {
					rebateAmount = finalPropertyTax.multiply(epRebatePercentage.divide(BigDecimal.valueOf(100)));
					finalPropertyTax = finalPropertyTax.subtract(rebateAmount);
				}

			} else {
				createFailureLog(property, calculateTaxRequest, null, errorMap.get(property.getPropertyId()));
			}

			Object address = objectMapper.convertValue(property.getAddress(), Object.class);
			previewResponses.add(CalculateTaxPreviewResponse.builder().propertyId(properties.get(0).getPropertyId())
					.tenantId(properties.get(0).getTenantId())
					.totalAnnualTax(totalPropertyTax.setScale(2, RoundingMode.HALF_UP)).address(address)
					.propertyTaxWithoutRebate(finalPropertyTax.setScale(2, RoundingMode.HALF_UP))
					.rebateAmount(rebateAmount.setScale(2, RoundingMode.HALF_UP)).days(days)
					.calculationDetails(trackeradditionalDetails).build());

		}

		return previewResponses;
	}
	
	private boolean hasPreviousUnpaidBill(RequestInfo requestInfo, String propertyId) {

		PtTaxCalculatorTrackerSearchCriteria criteria = PtTaxCalculatorTrackerSearchCriteria.builder()
				.propertyIds(Collections.singleton(propertyId))
				.billStatus(new HashSet<>(Arrays.asList(
					    BillStatus.ACTIVE,
					    BillStatus.PARTIALLY_PAID,
					    BillStatus.EXPIRED
					)))
				.build();
		List<PtTaxCalculatorTracker> trackers = propertyService.getTaxCalculatedProperties(criteria);
		if (CollectionUtils.isEmpty(trackers)) {
			return false;
		}

		for (PtTaxCalculatorTracker tracker : trackers) {
			if (StringUtils.isBlank(tracker.getDemandId())) {
				continue;
			}
			List<Demand> demand = demandService.searchDemand(tracker.getTenantId(),
					Collections.singleton(tracker.getDemandId()), null, requestInfo, "PROPERTY");
			if (!CollectionUtils.isEmpty(demand) && Boolean.FALSE.equals(demand.get(0).getIsPaymentCompleted())) {
				return true;
			}
		}
		return false;
		}

	private void createFailureLog(Property property, CalculateTaxRequest generateBillRequest, BillResponse billResponse,
			Set<String> errorMap) {
		PropertyBillFailure propertyBillFailure = enrichmentService.enrichPtBillFailure(property, generateBillRequest,
				billResponse, errorMap);
		propertyService.saveToPtBillFailure(propertyBillFailure);

	}

	private BigDecimal calculateDays(CalculateTaxRequest calculateTaxRequest) {
		BigDecimal days = new BigDecimal(365);

		if (!StringUtils.isEmpty(calculateTaxRequest.getFinancialYear())) {
			int financialYearStart = Integer.valueOf(calculateTaxRequest.getFinancialYear().split("-")[0]);
			LocalDate startDate = LocalDate.of(financialYearStart, 4, 1);
			LocalDate endDate = LocalDate.of(financialYearStart + 1, 3, 31);
			calculateTaxRequest.setFromDate(Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
			calculateTaxRequest.setToDate(Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
		} else if (null != calculateTaxRequest.getFromDate() && null != calculateTaxRequest.getToDate()) {
			LocalDate fromLocalDate = calculateTaxRequest.getFromDate().toInstant().atZone(ZoneId.systemDefault())
					.toLocalDate();
			LocalDate toLocalDate = calculateTaxRequest.getToDate().toInstant().atZone(ZoneId.systemDefault())
					.toLocalDate();

			days = BigDecimal.valueOf(ChronoUnit.DAYS.between(fromLocalDate, toLocalDate));
		} else {
			LocalDate currentDate = LocalDate.now();
			// Calculate the current financial year
			int currentYear = currentDate.getYear();
//                                          int financialYearStart = currentDate.isBefore(LocalDate.of(currentYear, 4, 1)) ? currentYear
//                                                                        : currentYear - 1;
			int financialYearStart = currentDate.isBefore(LocalDate.of(currentYear, 4, 1)) ? currentYear - 1
					: currentYear;

			int financialYearEnd = financialYearStart + 1;
			LocalDate startDate = LocalDate.of(financialYearStart, 4, 1);
			LocalDate endDate = LocalDate.of(financialYearStart + 1, 3, 31);
			calculateTaxRequest.setFromDate(Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
			calculateTaxRequest.setToDate(Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
			calculateTaxRequest
					.setFinancialYear(financialYearStart + "-" + String.valueOf(financialYearEnd).substring(2));
		}

		return days;
	}

	private List<Property> getProperties(CalculateTaxRequest calculateTaxRequest) {

		Set<String> ulbNames = calculateTaxRequest.getUlbNames();
		Set<String> wardNumbers = calculateTaxRequest.getWardNumbers();
		Set<String> mobileNumbers = calculateTaxRequest.getMobileNumbers();

		String tenantId = calculateTaxRequest.getUlbNames().stream().findFirst().map(ulb -> "hp." + ulb).orElse(null);

		PropertyCriteria propertyCriteria = PropertyCriteria.builder().isSchedulerCall(true).tenantId(tenantId)
				.status(Collections.singleton(Status.APPROVED)).propertyIds(calculateTaxRequest.getPropertyIds())
				.isActiveUnit(true).build();

		List<Property> properties = propertyService.searchProperty(propertyCriteria,
				calculateTaxRequest.getRequestInfo(), null);

		if (!CollectionUtils.isEmpty(mobileNumbers)) {
			properties = properties.stream()
					.filter(property -> !Collections.disjoint(mobileNumbers,
							property.getOwners().stream().map(OwnerInfo::getMobileNumber).collect(Collectors.toSet())))
					.collect(Collectors.toList());
		}
		if (!CollectionUtils.isEmpty(ulbNames)) {
			properties = properties.stream().filter(
					property -> property.getAddress() != null && property.getAddress().getAdditionalDetails() != null)
					.filter(property -> {
						JsonNode addressAdditionalDetails = objectMapper
								.valueToTree(property.getAddress().getAdditionalDetails());
						JsonNode ulbNameNode = addressAdditionalDetails.get("ulbName");
						return null != ulbNameNode && ulbNames.contains(ulbNameNode.asText());
					}).collect(Collectors.toList());
		}
		if (!CollectionUtils.isEmpty(ulbNames) && !CollectionUtils.isEmpty(wardNumbers)) {
			properties = properties.stream().filter(
					property -> property.getAddress() != null && property.getAddress().getAdditionalDetails() != null)
					.filter(property -> {
						JsonNode addressAdditionalDetails = objectMapper
								.valueToTree(property.getAddress().getAdditionalDetails());
						JsonNode ulbNameNode = addressAdditionalDetails.get("ulbName");
						JsonNode wardNumberNode = addressAdditionalDetails.get("wardNumber");
						return null != ulbNameNode && null != wardNumberNode && ulbNames.contains(ulbNameNode.asText())
								&& wardNumbers.contains(wardNumberNode.asText());
					}).collect(Collectors.toList());
		}

		return properties;
	}

	private List<Property> removeAlreadyTaxCalculatedProperties(List<Property> properties,
			CalculateTaxRequest calculateTaxRequest) {
		List<Property> finalProperty = new ArrayList<>();

		Set<String> propertyIds = properties.stream().map(Property::getPropertyId).collect(Collectors.toSet());

		PtTaxCalculatorTrackerSearchCriteria ptTaxCalculatorTrackerSearchCriteria = PtTaxCalculatorTrackerSearchCriteria
				.builder().propertyIds(propertyIds).build();

		List<PtTaxCalculatorTracker> ptTaxCalculatorTrackers = propertyService
				.getTaxCalculatedProperties(ptTaxCalculatorTrackerSearchCriteria);

		Map<String, List<PtTaxCalculatorTracker>> ptTaxCalculatorTrackerMap = ptTaxCalculatorTrackers.stream()
				.collect(Collectors.groupingBy(PtTaxCalculatorTracker::getPropertyId));

		finalProperty = properties.stream().filter(property -> {
			List<PtTaxCalculatorTracker> trackers = ptTaxCalculatorTrackerMap.get(property.getPropertyId());

			return trackers == null || trackers.stream().noneMatch(
					tracker -> tracker.getBillStatus() != BillStatus.CANCELLED && isOverlapping(tracker.getFromDate(),
							tracker.getToDate(), calculateTaxRequest.getFromDate(), calculateTaxRequest.getToDate()));

		}).collect(Collectors.toList());

		return finalProperty;
	}

	private boolean isOverlapping(Date trackerFrom, Date trackerTo, Date requestFrom, Date requestTo) {
		// Request is fully inside tracker OR tracker is fully inside request
		boolean requestInsideTracker = !trackerFrom.after(requestFrom) && !trackerTo.before(requestTo);
		boolean trackerInsideRequest = !requestFrom.after(trackerFrom) && !requestTo.before(trackerTo);
		return requestInsideTracker || trackerInsideRequest;
	}

	private BillResponse generateDemandAndBill(CalculateTaxRequest calculateTaxRequest, Property property,
			BigDecimal finalPropertyTax) {
		try {
			List<Demand> savedDemands = new ArrayList<>();
			// generate demand
			savedDemands = demandService.generateDemand(calculateTaxRequest, property, property.getBusinessService(),
					finalPropertyTax);

			if (CollectionUtils.isEmpty(savedDemands)) {
				throw new CustomException("INVALID_CONSUMERCODE",
						"Bill not generated due to no Demand found for the given consumerCode");
			}

			// fetch/create bill
			GenerateBillCriteria billCriteria = GenerateBillCriteria.builder().tenantId(property.getTenantId())
					.businessService(PTConstants.MODULE_PROPERTY).consumerCode(property.getPropertyId()).build();

			BillResponse billResponse = billService.generateBill(calculateTaxRequest.getRequestInfo(), billCriteria);
			return billResponse;
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}

	private boolean isAreaWithinRange(String propertyAreaString, BigDecimal propertyArea) {
		if (propertyAreaString.contains("-")) {
			String[] range = propertyAreaString.split("-");
			BigDecimal min = new BigDecimal(range[0]);
			BigDecimal max = new BigDecimal(range[1]);
			return propertyArea.compareTo(min) >= 0 && propertyArea.compareTo(max) <= 0;
		} else if (propertyAreaString.contains(">")) {
			BigDecimal threshold = new BigDecimal(propertyAreaString.split(">")[1]);
			return propertyArea.compareTo(threshold) > 0;
		} else if (propertyAreaString.contains("<")) {
			BigDecimal threshold = new BigDecimal(propertyAreaString.split("<")[1]);
			return propertyArea.compareTo(threshold) < 0;
		} else if (propertyAreaString.contains(">=")) {
			BigDecimal threshold = new BigDecimal(propertyAreaString.split(">=")[1]);
			return propertyArea.compareTo(threshold) >= 0;
		} else if (propertyAreaString.contains("<=")) {
			BigDecimal threshold = new BigDecimal(propertyAreaString.split("<=")[1]);
			return propertyArea.compareTo(threshold) <= 0;
		}
		return false;
	}

	public Object updateTrackerBillStatus(RequestInfoWrapper requestInfoWrapper) {

		List<PtTaxCalculatorTracker> latestTaxCalculatorTrackers = new ArrayList<>();

		PtTaxCalculatorTrackerSearchCriteria taxCalculatorTrackerSearchCriteria = PtTaxCalculatorTrackerSearchCriteria
				.builder().billStatus(Collections.singleton(BillStatus.ACTIVE)).build();

		List<PtTaxCalculatorTracker> ptTaxCalculatorTrackers = propertyService
				.getTaxCalculatedProperties(taxCalculatorTrackerSearchCriteria);

		Set<String> billIds = ptTaxCalculatorTrackers.stream().map(PtTaxCalculatorTracker::getBillId)
				.filter(Objects::nonNull).collect(Collectors.toSet());

		// Batch size
		final int batchSize = 100;

		Map<String, Bill> billIdBillMap = new HashMap<>();

		List<String> billIdList = new ArrayList<>(billIds);
		for (int start = 0; start < billIdList.size(); start += batchSize) {
			int end = Math.min(start + batchSize, billIdList.size());

			Set<String> batch = new HashSet<>(billIdList.subList(start, end));

			BillSearchCriteria billSearchCriteria = BillSearchCriteria.builder().billId(batch).skipValidation(true)
					.build();

			BillResponse billResponse = billService.searchBill(billSearchCriteria, requestInfoWrapper.getRequestInfo());

			Map<String, Bill> batchMap = Optional.ofNullable(billResponse).map(BillResponse::getBill)
					.filter(bills -> !bills.isEmpty())
					.map(bills -> bills.stream().collect(Collectors.toMap(Bill::getId, Function.identity())))
					.orElse(Collections.emptyMap());

			billIdBillMap.putAll(batchMap);
		}

		for (PtTaxCalculatorTracker ptTaxCalculatorTracker : ptTaxCalculatorTrackers) {
			Optional.ofNullable(billIdBillMap.get(ptTaxCalculatorTracker.getBillId())).map(Bill::getStatus)
					.map(Enum::name).map(BillStatus::fromValue).ifPresent(ptTaxCalculatorTracker::setBillStatus);

			PtTaxCalculatorTrackerRequest ptTaxCalculatorTrackerRequest = enrichmentService
					.enrichTaxCalculatorTrackerUpdateRequest(ptTaxCalculatorTracker,
							requestInfoWrapper.getRequestInfo());

			PtTaxCalculatorTracker ptTaxCalculatorTrackerUpdated = propertyService
					.updatePtTaxCalculatorTracker(ptTaxCalculatorTrackerRequest);

			latestTaxCalculatorTrackers.add(ptTaxCalculatorTrackerUpdated);
		}

		return latestTaxCalculatorTrackers;
	}

	public Object reverseRebateAmount(RequestInfoWrapper requestInfoWrapper) {
		List<PtTaxCalculatorTracker> latestTaxCalculatorTrackers = new ArrayList<>();

		List<String> taxCalculatedTenantIds = getActiveTaxCalculatedTenantIds();

		if (CollectionUtils.isEmpty(taxCalculatedTenantIds)) {
			return Collections.emptyList();
		}

		MdmsResponse mdmsResponse = mdmsService.getPropertyReabateDaysMdmsData(requestInfoWrapper.getRequestInfo(),
				null);
		Map<String, Integer> tenantIdDaysMap = getUlbDaysMap(mdmsResponse);

		for (String tenantId : taxCalculatedTenantIds) {
			Integer days = tenantIdDaysMap.getOrDefault(tenantId, 15);

			List<PtTaxCalculatorTracker> ptTaxCalculatorTrackers = getTrackersForTenantAndDays(tenantId, days);

			Set<String> billIds = ptTaxCalculatorTrackers.stream().map(PtTaxCalculatorTracker::getBillId)
					.filter(Objects::nonNull).collect(Collectors.toSet());

			Map<String, Bill> billIdBillMap = fetchBillsByBatch(billIds, requestInfoWrapper);
			Map<String, String> demandIdToTenantMap = extractDemandIdToTenantMap(billIdBillMap);

			Map<String, Demand> demandIdToDemandMap = fetchDemandsByBatch(demandIdToTenantMap, requestInfoWrapper);

			latestTaxCalculatorTrackers.addAll(processTrackers(PTConstants.PROPERTY_CONSTANT_REABATE,
					ptTaxCalculatorTrackers, billIdBillMap, demandIdToDemandMap, requestInfoWrapper, null));
		}

		return latestTaxCalculatorTrackers;
	}

	private List<String> getActiveTaxCalculatedTenantIds() {
		PtTaxCalculatorTrackerSearchCriteria criteria = PtTaxCalculatorTrackerSearchCriteria.builder()
				.billStatus(Collections.singleton(BillStatus.ACTIVE)).build();

		return propertyService.getTaxCalculatedTenantIds(criteria);
	}

	private List<String> getNotPaidTaxCalculatedTenantIds() {
		PtTaxCalculatorTrackerSearchCriteria criteria = PtTaxCalculatorTrackerSearchCriteria.builder()
				.notInBillStatus(Collections.singleton(BillStatus.PAID)).build();

		return propertyService.getTaxCalculatedTenantIds(criteria);
	}

	public Map<String, Integer> getUlbDaysMap(MdmsResponse mdmsResponse) {
		return Optional.ofNullable(mdmsResponse).map(MdmsResponse::getMdmsRes)
				.map(mdmsRes -> mdmsRes.get(PTConstants.MDMS_MODULE_ULBS)).map(objectMapper::valueToTree)
				.map(ulbsNode -> {
					JsonNode jsonNode = (JsonNode) ulbsNode; // explicit cast
					return jsonNode.get(PTConstants.MDMS_MASTER_DETAILS_PROPERTYREBATEDAYS);
				}).filter(JsonNode::isArray)
				.map(rebateDaysNode -> StreamSupport.stream(rebateDaysNode.spliterator(), false)
						.collect(Collectors.toMap(node -> propertyConfiguration.getStateLevelTenantId() + "."
								+ node.get("ulbName").asText(), node -> node.get("days").asInt())))
				.orElseGet(HashMap::new);
	}

	public Map<String, Double> getPenaltyRateMap(MdmsResponse mdmsResponse) {
		return Optional.ofNullable(mdmsResponse).map(MdmsResponse::getMdmsRes)
				.map(mdmsRes -> mdmsRes.get(PTConstants.MDMS_MODULE_ULBS)).map(objectMapper::valueToTree)
				.map(ulbsNode -> {
					JsonNode jsonNode = (JsonNode) ulbsNode; // explicit cast
					return jsonNode.get(PTConstants.MDMS_MASTER_DETAILS_PENALTYRATE);
				}).filter(JsonNode::isArray)
				.map(rebateDaysNode -> StreamSupport.stream(rebateDaysNode.spliterator(), false)
						.collect(Collectors.toMap(node -> propertyConfiguration.getStateLevelTenantId() + "."
								+ node.get("ulbName").asText(), node -> node.get("rate").asDouble())))
				.orElseGet(HashMap::new);
	}
	
	public Map<String, Integer> getPenaltyDaysMap(MdmsResponse mdmsResponse) {
	    return Optional.ofNullable(mdmsResponse)
	            .map(MdmsResponse::getMdmsRes)
	            .map(mdmsRes -> mdmsRes.get(PTConstants.MDMS_MODULE_ULBS))
	            .map(objectMapper::valueToTree)
	            .map(ulbsNode -> {
	                JsonNode jsonNode = (JsonNode) ulbsNode;
	                return jsonNode.get(PTConstants.MDMS_MASTER_DETAILS_PENALTYDAYS);
	            })
	            .filter(JsonNode::isArray)
	            .map(penaltyDaysNode -> StreamSupport.stream(penaltyDaysNode.spliterator(), false)
	                    .collect(Collectors.toMap(
	                            node -> propertyConfiguration.getStateLevelTenantId() + "." + node.get("ulbName").asText(),
	                            node -> node.get("days").asInt()
	                    )))
	            .orElseGet(HashMap::new);
	}

	private List<PtTaxCalculatorTracker> getTrackersForTenantAndDays(String tenantId, int days) {
		long startDateTime = LocalDate.now().minusDays(days).atStartOfDay(ZoneId.systemDefault()).toInstant()
				.toEpochMilli();
		long endDateTime = LocalDate.now().minusDays(days).atTime(23, 59, 59, 999_999_999)
				.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

		PtTaxCalculatorTrackerSearchCriteria criteria = PtTaxCalculatorTrackerSearchCriteria.builder()
				.startDateTime(startDateTime).endDateTime(endDateTime).tenantId(tenantId).type("CYCLIC")
				.rebateamount(BigDecimal.ZERO).billStatus(Collections.singleton(BillStatus.ACTIVE)).build();

		return propertyService.getTaxCalculatedProperties(criteria).stream()
				.filter(tracker -> !Boolean.TRUE.equals(tracker.getIsPaymentProcessing())).collect(Collectors.toList());
	}

	private List<PtTaxCalculatorTracker> getTrackersForTenantAndStartDays(String tenantId, int days, int penaltyCycleDays) {
		long startDateTime = LocalDate.now().minusDays(days).atStartOfDay(ZoneId.systemDefault()).toInstant()
				.toEpochMilli();

		PtTaxCalculatorTrackerSearchCriteria criteria = PtTaxCalculatorTrackerSearchCriteria.builder()
				.startDateTime(startDateTime).tenantId(tenantId).billStatus(Collections.singleton(BillStatus.ACTIVE))
		        .type("CYCLIC") 
				.build();

		List<PtTaxCalculatorTracker> trackers = propertyService.getTaxCalculatedProperties(criteria);

		long todayStart = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

		return trackers.stream()
			.filter(tracker -> !Boolean.TRUE.equals(tracker.getIsPaymentProcessing()))
			.filter(tracker -> {
			long createdTime = tracker.getAuditDetails().getCreatedTime();
			long diffDays = ChronoUnit.DAYS.between(
					Instant.ofEpochMilli(createdTime).atZone(ZoneId.systemDefault()).toLocalDate(),
					Instant.ofEpochMilli(todayStart).atZone(ZoneId.systemDefault()).toLocalDate());
			
			int baseCycle = 30;
	        int buffer = penaltyCycleDays - baseCycle;
	        if (buffer <= 0) {
	            return diffDays >= baseCycle && diffDays % baseCycle == 0;
	        }

	        int firstPenaltyDay = baseCycle + buffer;

	        return diffDays >= firstPenaltyDay &&
	               (diffDays - firstPenaltyDay) % baseCycle == 0;
		}).collect(Collectors.toList());

	}

	private Map<String, Bill> fetchBillsByBatch(Set<String> billIds, RequestInfoWrapper requestInfoWrapper) {
		Map<String, Bill> billIdBillMap = new HashMap<>();
		final int batchSize = 100;
		List<String> billIdList = new ArrayList<>(billIds);

		for (int start = 0; start < billIdList.size(); start += batchSize) {
			int end = Math.min(start + batchSize, billIdList.size());
			Set<String> batch = new HashSet<>(billIdList.subList(start, end));

			BillSearchCriteria searchCriteria = BillSearchCriteria.builder().billId(batch).skipValidation(true).build();

			BillResponse billResponse = billService.searchBill(searchCriteria, requestInfoWrapper.getRequestInfo());

			if (billResponse != null && !CollectionUtils.isEmpty(billResponse.getBill())) {
				billIdBillMap.putAll(
						billResponse.getBill().stream().collect(Collectors.toMap(Bill::getId, Function.identity())));
			}
		}
		return billIdBillMap;
	}

	private Map<String, String> extractDemandIdToTenantMap(Map<String, Bill> billIdBillMap) {
		Map<String, String> demandIdToTenantMap = new HashMap<>();
		for (Bill bill : billIdBillMap.values()) {
			if (bill.getBillDetails() != null) {
				for (BillDetail billDetail : bill.getBillDetails()) {
					if (billDetail.getDemandId() != null && bill.getTenantId() != null) {
						demandIdToTenantMap.put(billDetail.getDemandId(), bill.getTenantId());
					}
				}

			}
		}
		return demandIdToTenantMap;
	}

	private Map<String, Demand> fetchDemandsByBatch(Map<String, String> demandIdToTenantMap,
			RequestInfoWrapper requestInfoWrapper) {
		Map<String, Demand> demandIdToDemandMap = new HashMap<>();
		final int batchSize = 100;
		List<Map.Entry<String, String>> entries = new ArrayList<>(demandIdToTenantMap.entrySet());

		for (int start = 0; start < entries.size(); start += batchSize) {
			int end = Math.min(start + batchSize, entries.size());
			List<Map.Entry<String, String>> batch = entries.subList(start, end);

			// Group demandIds by tenantId
			Map<String, Set<String>> tenantToDemandIds = batch.stream().collect(Collectors
					.groupingBy(Map.Entry::getValue, Collectors.mapping(Map.Entry::getKey, Collectors.toSet())));

			for (Map.Entry<String, Set<String>> tenantEntry : tenantToDemandIds.entrySet()) {
				String tenantId = tenantEntry.getKey();
				Set<String> demandIds = tenantEntry.getValue();

				List<Demand> demands = demandService.searchDemand(tenantId, demandIds, null,
						requestInfoWrapper.getRequestInfo(), null);

				if (demands != null) {
					demandIdToDemandMap.putAll(demands.stream().filter(demand -> demand.getId() != null)
							.collect(Collectors.toMap(Demand::getId, Function.identity())));
				}
			}
		}
		return demandIdToDemandMap;
	}

	private List<PtTaxCalculatorTracker> processTrackers(String constantValue, List<PtTaxCalculatorTracker> trackers,
			Map<String, Bill> billIdBillMap, Map<String, Demand> demandIdToDemandMap,
			RequestInfoWrapper requestInfoWrapper, Map<String, Double> tenantIdPenaltyRateMap) {
		List<PtTaxCalculatorTracker> updatedTrackers = new ArrayList<>();

		for (PtTaxCalculatorTracker tracker : trackers) {
			if (!isValidTracker(tracker)) {
				continue;
			}

			Map<String, Bill> demandIdBillMap = new HashMap<>();

			billIdBillMap.values().forEach(bill -> {
				if (bill.getBillDetails() != null) {
					bill.getBillDetails().forEach(detail -> {
						demandIdBillMap.put(detail.getDemandId(), bill);
					});
				}
			});
			Bill bill = billIdBillMap.get(tracker.getBillId());

			if (bill == null || CollectionUtils.isEmpty(bill.getBillDetails())) {
				log.warn("No bill or bill details found for billId [{}]. Skipping tracker [{}].", tracker.getBillId(),
						tracker.getUuid());
				continue;
			}
			BigDecimal penaltyAmount = BigDecimal.ZERO;
			BigDecimal newAmount = BigDecimal.ZERO;

			if (constantValue.equals(PTConstants.PROPERTY_CONSTANT_PENALTY) && tenantIdPenaltyRateMap != null
					&& tenantIdPenaltyRateMap.containsKey(tracker.getTenantId())) {
				penaltyAmount = calculatePenalty(tracker, tenantIdPenaltyRateMap.get(tracker.getTenantId()));
				newAmount = tracker.getPropertyTax().add(penaltyAmount);

				tracker.setPenaltyAmount(
						(tracker.getPenaltyAmount() != null ? tracker.getPenaltyAmount() : BigDecimal.ZERO)
								.add(penaltyAmount));
			} else if (constantValue.equals(PTConstants.PROPERTY_CONSTANT_REABATE)) {
				newAmount = tracker.getPropertyTaxWithoutRebate();
//                                                tracker.setRebateAmount(BigDecimal.ZERO);
			}

			if (!newAmount.equals(BigDecimal.ZERO)) {
				try {
					updateBillAndDemandAmounts(bill, demandIdToDemandMap, newAmount, requestInfoWrapper, tracker);

					tracker.setPropertyTax(newAmount);
					tracker.setRebateAmount(BigDecimal.ZERO);

					PtTaxCalculatorTrackerRequest updateRequest = enrichmentService
							.enrichTaxCalculatorTrackerUpdateRequest(tracker, requestInfoWrapper.getRequestInfo());

					PtTaxCalculatorTracker updatedTracker = propertyService.updatePtTaxCalculatorTracker(updateRequest);

					updatedTrackers.add(updatedTracker);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
		}

		return updatedTrackers;
	}

	private BigDecimal calculatePenalty(PtTaxCalculatorTracker tracker, Double penaltyRatePercent) {
		return tracker.getPropertyTaxWithoutRebate().multiply(BigDecimal.valueOf(penaltyRatePercent))
				.divide(BigDecimal.valueOf(100));
	}

	private boolean isValidTracker(PtTaxCalculatorTracker tracker) {
		if (tracker.getBillId() == null) {
			log.warn("Tracker Uuid [{}] has null billId. Skipping.", tracker.getUuid());
			return false;
		}
		if (tracker.getPropertyTaxWithoutRebate() == null) {
			log.warn("PropertyTaxWithoutRebate is null in tracker [{}]. Skipping.", tracker.getUuid());
			return false;
		}
		return true;
	}
//
//	private void updateBillAndDemandAmounts(Bill bill, Map<String, Demand> demandIdToDemandMap, BigDecimal newAmount,
//			RequestInfoWrapper requestInfoWrapper, PtTaxCalculatorTracker tracker) {
//
//		for (BillDetail billDetail : bill.getBillDetails()) {
//			Demand demand = demandIdToDemandMap.get(billDetail.getDemandId());
//			if (demand != null) {
//				demand.setMinimumAmountPayable(newAmount);
//				if (demand.getDemandDetails() != null) {
//					demand.getDemandDetails().forEach(demandDetail -> demandDetail.setTaxAmount(newAmount));
//				}
//				demandService.updateDemand(requestInfoWrapper.getRequestInfo(), Collections.singletonList(demand));
//			}
//
//			if (billDetail.getBillAccountDetails() != null) {
//				billDetail.getBillAccountDetails().forEach(billAccountDetail -> billAccountDetail.setAmount(newAmount));
//			}
//
//			billDetail.setAmount(newAmount);
//		}
//		bill.setTotalAmount(newAmount);
//		billService.updateBill(requestInfoWrapper.getRequestInfo(), Collections.singletonList(bill));
//	}
	
	private void updateBillAndDemandAmounts(
	        Bill bill,
	        Map<String, Demand> demandIdToDemandMap,
	        BigDecimal newAmount,
	        RequestInfoWrapper requestInfoWrapper,
	        PtTaxCalculatorTracker tracker) {

	    for (BillDetail billDetail : bill.getBillDetails()) {

	        //ONLY update matching demand
	        if (!billDetail.getDemandId().equals(tracker.getDemandId())) {
	            continue;
	        }

	        Demand demand = demandIdToDemandMap.get(billDetail.getDemandId());

	        if (demand != null) {

	            // Update ONLY this demand
	            demand.setMinimumAmountPayable(newAmount);

	            if (demand.getDemandDetails() != null) {
	                demand.getDemandDetails().forEach(demandDetail ->
	                        demandDetail.setTaxAmount(newAmount));
	            }

	            demandService.updateDemand(
	                    requestInfoWrapper.getRequestInfo(),
	                    Collections.singletonList(demand)
	            );
	        }

	        // Update ONLY this billDetail
	        if (billDetail.getBillAccountDetails() != null) {
	            billDetail.getBillAccountDetails().forEach(billAccountDetail ->
	                    billAccountDetail.setAmount(newAmount));
	        }

	        billDetail.setAmount(newAmount);
	    }

	    // IMPORTANT: totalAmount recompute 
	    BigDecimal total = bill.getBillDetails().stream()
	            .map(BillDetail::getAmount)
	            .reduce(BigDecimal.ZERO, BigDecimal::add);

	    bill.setTotalAmount(total);

	    billService.updateBill(
	            requestInfoWrapper.getRequestInfo(),
	            Collections.singletonList(bill)
	    );
	}

	public Object updatePenaltyAmount(RequestInfoWrapper requestInfoWrapper) {
		List<PtTaxCalculatorTracker> latestTaxCalculatorTrackers = new ArrayList<>();

		List<String> taxCalculatedTenantIds = getActiveTaxCalculatedTenantIds();

		if (CollectionUtils.isEmpty(taxCalculatedTenantIds)) {
			return Collections.emptyList();
		}

		MdmsResponse mdmsResponse = mdmsService.getPenaltyRateMdmsData(requestInfoWrapper.getRequestInfo(), null);
		MdmsResponse mdmsPenaltyDaysResponse = mdmsService.getPenaltyDaysMdmsData(requestInfoWrapper.getRequestInfo(), null);

		Map<String, Double> tenantIdPenaltyRateMap = getPenaltyRateMap(mdmsResponse);
		Map<String, Integer> tenantIdPenaltyDaysMap = getPenaltyDaysMap(mdmsPenaltyDaysResponse);

		for (String tenantId : taxCalculatedTenantIds) {
			Integer penaltyDays = tenantIdPenaltyDaysMap.get(tenantId);
			if (penaltyDays == null || penaltyDays == 0) {
			    penaltyDays = 30;
			}

			List<PtTaxCalculatorTracker> ptTaxCalculatorTrackers = getTrackersForTenantAndStartDays(tenantId, 365, penaltyDays);

			Set<String> billIds = ptTaxCalculatorTrackers.stream().map(PtTaxCalculatorTracker::getBillId)
					.filter(Objects::nonNull).collect(Collectors.toSet());

			Map<String, Bill> billIdBillMap = fetchBillsByBatch(billIds, requestInfoWrapper);
			Map<String, String> demandIdToTenantMap = extractDemandIdToTenantMap(billIdBillMap);

			Map<String, Demand> demandIdToDemandMap = fetchDemandsByBatch(demandIdToTenantMap, requestInfoWrapper);

			latestTaxCalculatorTrackers
					.addAll(processTrackers(PTConstants.PROPERTY_CONSTANT_PENALTY, ptTaxCalculatorTrackers,
							billIdBillMap, demandIdToDemandMap, requestInfoWrapper, tenantIdPenaltyRateMap));
		}

		return latestTaxCalculatorTrackers;
	}

	public boolean uploadBulkBills(RequestInfoWrapper requestInfoWrapper, String isforce,String ulb,String ward , String created_at) throws Exception {
		String[] tenants = new String[0];

		if(ulb ==null && ward == null) {
			MdmsResponse mdmsResponse = mdmsService.getDownloadPdfMdmsData(requestInfoWrapper.getRequestInfo(), null);
			Map<String, Map<String, JSONArray>> mdmsRes = mdmsResponse.getMdmsRes();

			if (mdmsRes != null) {
				Map<String, JSONArray> moduleData = mdmsRes.get(PTConstants.MDMS_MODULE_ULBS);
				if (moduleData != null) {
					List<Object> masterList = (List<Object>) moduleData.get(PTConstants.DOWNLOADPDF);

					if (masterList != null) {
						List<String> tenantList = new ArrayList<>();

						for (Object obj : masterList) {

							Map<String, Object> map = (Map<String, Object>) obj;

							String getulbName = map.get("ulbName").toString();
							String tenantId = "hp." + getulbName;

							tenantList.add(tenantId);
						}

						tenants = tenantList.toArray(new String[0]);
					}
				}
			}


		} else  {
		    tenants = new String[]{"hp." +ulb};

		}
		String ulbName = null;
		List<Map<String, Object>> bills = new ArrayList<>();

		for (String tenant : tenants) {

			List<Map<String, Object>> result = repository.getActiveBills("ACTIVE", tenant, isforce, ward, created_at);

			if (result != null && !result.isEmpty()) {
				bills.addAll(result);
			}
		}

		if (bills.isEmpty()) {
			return false;
		}

		Map<String, List<Map<String, Object>>> billsByUlb = new HashMap<>();

		for (Map<String, Object> bill : bills) {

			String wardName = String.valueOf(bill.get("ward"));
			ulbName = String.valueOf(bill.get("tenantid"));
			if (ulbName != null && ulbName.contains(".")) {
				ulbName = ulbName.split("\\.")[1];
			}
			billsByUlb.computeIfAbsent(wardName, k -> new ArrayList<>()).add(bill);
		}

		for (Map.Entry<String, List<Map<String, Object>>> entry : billsByUlb.entrySet()) {

			String wardName = entry.getKey();
			List<Map<String, Object>> ulbBills = entry.getValue();

			PDFMergerUtility merger = new PDFMergerUtility();
			ByteArrayOutputStream mergedOutput = new ByteArrayOutputStream();

			// 4 Generate PDF for each bill
			for (Map<String, Object> bill : ulbBills) {

				String propertyId = String.valueOf(bill.get("propertyid"));
				String billId = String.valueOf(bill.get("bill_id"));
				String status = String.valueOf(bill.get("bill_status"));
				String UlbNames = String.valueOf(bill.get("tenantId"));
				if (UlbNames != null && UlbNames.contains(".")) {
					UlbNames = UlbNames.split("\\.")[1];
				}
				ResponseEntity<Resource> billResponse = propertyService
						.generatePropertyTaxBillReceipt(requestInfoWrapper, propertyId, billId, status);

//				if (billResponse != null && billResponse.getBody() != null) {
//
//					InputStream inputStream = billResponse.getBody().getInputStream();
//
//					merger.addSource(inputStream);
//				}
				Resource resource = billResponse.getBody();

				if (resource != null) {
				    InputStream is = resource.getInputStream();
				    
				    byte[] pdfBytes = convertInputStreamToByteArray(is);
				    
				    is.close();   

				    if (pdfBytes.length > 4 &&
				        pdfBytes[0] == '%' &&
				        pdfBytes[1] == 'P' &&
				        pdfBytes[2] == 'D' &&
				        pdfBytes[3] == 'F') {

				        merger.addSource(new ByteArrayInputStream(pdfBytes));
				    } else {
				        System.out.println("❌ Skipping invalid PDF for billId: " + billId);
				    }
				}
				
				
			}

			// Merge PDFs of that ULB
			merger.setDestinationStream(mergedOutput);
			merger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());

			byte[] finalPdf = mergedOutput.toByteArray();

        // 6️ Convert to resource
        ByteArrayResource resource = new ByteArrayResource(finalPdf) {
            @Override
            public String getFilename() {
				String timestamp = String.valueOf(System.currentTimeMillis());
        		return timestamp + "_" + wardName + "_Bills.pdf"; 
            }
        };

			// 7️ Prepare DMS request
			DmsRequest dmsRequest = generateDmsRequestFromBulkBillUpload(resource, wardName, ulbName,
					requestInfoWrapper.getRequestInfo());

			// 8️ Upload to Alfresco
			try {

				alfrescoService.uploadAttachment(dmsRequest, requestInfoWrapper.getRequestInfo());

			} catch (IOException e) {

				throw new CustomException("UPLOAD_ATTACHMENT_FAILED", "Upload Attachment failed " + e.getMessage());
			}
		}

		return true;
	}

	private DmsRequest generateDmsRequestFromBulkBillUpload(Resource resource, String wardName, String ulbName,
			RequestInfo requestInfo) {
		return DmsRequest.builder().userId(requestInfo.getUserInfo().getId().toString())
				.objectId(UUID.randomUUID().toString()).description(PTConstants.ALFRESCO_COMMON_CERTIFICATE_DESCRIPTION)
				.id(PTConstants.ALFRESCO_COMMON_CERTIFICATE_ID).type(PTConstants.ALFRESCO_COMMON_CERTIFICATE_TYPE)
				.objectName(PTConstants.BUSINESS_SERVICE).comments(PTConstants.ALFRESCO_TL_CERTIFICATE_COMMENT)
				.status(PTConstants.APPLICATION_STATUS_APPROVED).file(resource)
				.servicetype(PTConstants.BUSINESS_SERVICE).documentType(PTConstants.ALFRESCO_DOCUMENT_TYPE)
				.documentId(PTConstants.ALFRESCO_COMMON_DOCUMENT_ID).ward_name(wardName).ulb_name(ulbName).build();

	}

	private byte[] convertInputStreamToByteArray(InputStream inputStream) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[4096];

		while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}

		buffer.flush();
		return buffer.toByteArray();
	}
	
	public PtTaxCalculatorTracker getTrackerByBillId(BillIdRequest request) {
		if (request.getBillId() == null) {
			throw new CustomException("INVALID_REQUEST", "billId is required");
		}
		PtTaxCalculatorTrackerSearchCriteria criteria = PtTaxCalculatorTrackerSearchCriteria.builder()
				.billId(request.getBillId())
				.build();
		List<PtTaxCalculatorTracker> trackers = repository.extractTrackers(criteria);
		if (CollectionUtils.isEmpty(trackers)) {
			throw new CustomException("NOT_FOUND", "No active tracker found for given billId");
		}
		return trackers.get(0);
	}
	
	public void sendPropertyEmail(SendPropertyEmailRequest request) {

		BillSearchCriteria billSearchCriteria = BillSearchCriteria.builder()
				.billId(Collections.singleton(request.getBillId()))
				.demandId(Collections.singleton(request.getDemandId()))
				.tenantId(request.getTenantId())
				.skipValidation(true).build();
		
		String ulbName = request.getTenantId().replaceFirst("^hp\\.", "");

		BillResponse billResponse = billService.searchBill(billSearchCriteria, request.getRequestInfo());
		
		PropertyCriteria propertyCriteria = PropertyCriteria.builder()
		        .isSchedulerCall(true)
		        .tenantId(request.getTenantId())
		        .status(Collections.singleton(Status.APPROVED))
		        .propertyIds(Collections.singleton(request.getPropertyId()))
		        .isActiveUnit(true)
		        .build();

		List<Property> property = propertyService.searchProperty(
		        propertyCriteria,
		        request.getRequestInfo(),
		        null);

		if (CollectionUtils.isEmpty(billResponse.getBill())) {
			throw new CustomException("BILL_NOT_FOUND", "No bill found for given billId and demandId");
		}

		Bill bill = billResponse.getBill().get(0);
		PtTaxCalculatorTracker tracker = getTrackerByBillId(BillIdRequest.builder()
				                         .billId(request.getBillId())
				                         .build());

		notificationService.triggerPropertyMail(tracker, bill, request.getRequestInfo(), ulbName, property);
	}
	
	public void sendPropertyNotice(SendPropertyNoticeRequest request) {

		BillSearchCriteria billSearchCriteria = BillSearchCriteria.builder()
				.billId(Collections.singleton(request.getBillId()))
				.demandId(Collections.singleton(request.getDemandId()))
				.tenantId(request.getTenantId())
				.skipValidation(true).build();
		
		String ulbName = request.getTenantId().replaceFirst("^hp\\.", "");
		
		String tenantId = request.getTenantId();

		BillResponse billResponse = billService.searchBill(billSearchCriteria, request.getRequestInfo());
		
		PropertyCriteria propertyCriteria = PropertyCriteria.builder()
		        .isSchedulerCall(true)
		        .tenantId(request.getTenantId())
		        .status(Collections.singleton(Status.APPROVED))
		        .propertyIds(Collections.singleton(request.getPropertyId()))
		        .isActiveUnit(true)
		        .build();

		List<Property> property = propertyService.searchProperty(
		        propertyCriteria,
		        request.getRequestInfo(),
		        null);
		
		Property propertyDetail = property.get(0);
		
		if (CollectionUtils.isEmpty(billResponse.getBill())) {
			throw new CustomException("BILL_NOT_FOUND", "No bill found for given billId and demandId");
		}

		Bill bill = billResponse.getBill().get(0);
		PtTaxCalculatorTracker tracker = getTrackerByBillId(BillIdRequest.builder()
				                         .billId(request.getBillId())
				                         .build());

		notificationService.triggerPropertyNotice(tracker, bill, request.getRequestInfo(), ulbName, property, tenantId);
		
		try {
			if (CollectionUtils.isEmpty(property.get(0).getOwners())) {
				throw new RuntimeException("No owners found for property " + propertyDetail.getPropertyId());
			}

			ObjectNode smsRequestJson = notificationService.buildGeneratePropertyBillSmsRequest(propertyDetail, bill, tracker);
			Map<String, Object> smsRequestMap = new ObjectMapper().convertValue(smsRequestJson,Map.class);

			StringBuilder smsTrackerUri = new StringBuilder();
			smsTrackerUri.append(smsHost).append(smsTrackerCreateEndpoint);

			Map<String, Object> smsTrackerRequest = new HashMap<>();
			smsTrackerRequest.put("uuid", UUID.randomUUID().toString());
			smsTrackerRequest.put("amount", bill.getTotalAmount());
			smsTrackerRequest.put("applicationNo", propertyDetail.getPropertyId());
			smsTrackerRequest.put("tenantId", propertyDetail.getTenantId());
			smsTrackerRequest.put("service", "PROPERTY");
			smsTrackerRequest.put("fromDate",tracker.getFromDate());
			smsTrackerRequest.put("toDate", tracker.getToDate());
			smsTrackerRequest.put("createdBy", "system");
			smsTrackerRequest.put("createdTime", System.currentTimeMillis());
			smsTrackerRequest.put("billId", bill.getId());
			smsTrackerRequest.put("demandId", tracker.getDemandId());
			smsTrackerRequest.put("smsStatus", false);
			smsTrackerRequest.put("additionalDetail", tracker.getAdditionalDetails());
			smsTrackerRequest.put("ownerMobileNo", propertyDetail.getOwners().get(0).getMobileNumber());
			smsTrackerRequest.put("ownerName", propertyDetail.getOwners().get(0).getName());
			smsTrackerRequest.put("smsRequest", smsRequestMap);
			smsTrackerRequest.put("smsResponse", null);
			smsTrackerRequest.put("ward", tracker.getWard());

			restCallRepository.fetchResult(smsTrackerUri, smsTrackerRequest);
			log.info("Notice SMS tracker entry created for billId {} demandId {}", bill.getId(), tracker.getDemandId());

		} catch (Exception e) {
			log.error("Notice SMS tracker creation failed for billId {}", bill.getId(), e);
		}
	}

	private void syncTrackerWithBillStatus(PtTaxCalculatorTracker tracker, CalculateTaxRequest calculateTaxRequest) {
		
		if (tracker == null || tracker.getBillId() == null) {
			return;
		}
		
		BillSearchCriteria billSearchCriteria = BillSearchCriteria.builder()
                .tenantId(tracker.getTenantId())
                .consumerCode(Collections.singleton(tracker.getPropertyId()))
                .service("PROPERTY")
                .billId(Collections.singleton(tracker.getBillId()))
                .build();
		
		List<Bill> bill = billService
							.searchBill(billSearchCriteria, calculateTaxRequest.getRequestInfo())
							.getBill();
		
		Bill currentBill = bill.get(0);
		
		if (Bill.StatusEnum.ADVANCE_ADJUSTED.equals(currentBill.getStatus())) {
			JsonNode additionalDetails = tracker.getAdditionalDetails();

			if (additionalDetails != null && additionalDetails.isArray() && additionalDetails.size() > 0) {

				ObjectNode firstObject = (ObjectNode) additionalDetails.get(0);
				firstObject.put("advanceAdjusted", true);

				tracker.setAdditionalDetails(additionalDetails);
				repository.updateTrackerAdditionalDetails(tracker);
			}
		}
		
		tracker.setBillStatus(BillStatus.valueOf(currentBill.getStatus().name()));
     	propertyService.UpdatePtTrackerStatus(tracker);
	}

	public CustomAmountUpdateResponse updateCustomAmount(CustomAmountUpdateRequest request) {

		BillSearchCriteria billSearchCriteria = BillSearchCriteria.builder()
				.billId(Collections.singleton(request.getBillId()))
				.tenantId(request.getTenantId())
				.skipValidation(true)
				.build();

		BillResponse billResponse = billService.searchBill(billSearchCriteria, request.getRequestInfo());

		if (CollectionUtils.isEmpty(billResponse.getBill())) {
			throw new CustomException("BILL_NOT_FOUND", "No bill found for given ID");
	    }

	    Bill bill = billResponse.getBill().get(0);

	    if (Bill.StatusEnum.PAID.equals(bill.getStatus())) {
	        throw new CustomException("INVALID_UPDATE", "Cannot update paid bill");
	    }
	    
	    if (Bill.StatusEnum.PARTIALLY_PAID.equals(bill.getStatus())) {
	        throw new CustomException("INVALID_UPDATE", "Cannot update partially paid bill");
	    }
	    
	    if (request.getBillId() == null || request.getBillId().trim().isEmpty()) {
	        throw new CustomException("INVALID_REQUEST", "billId is required");
	    }

	    if (request.getDemandId() == null || request.getDemandId().trim().isEmpty()) {
	        throw new CustomException("INVALID_REQUEST", "demandId is required");
	    }

	    if (request.getCustomAmount() == null) {
	        throw new CustomException("INVALID_REQUEST", "customAmount is required");
	    }
	    
	    String DemandId =  request.getDemandId(); 
	    repository.updateCustomTrackerAmount(request);
	    
	    Object updatedResponse = billService.searchBill(billSearchCriteria, request.getRequestInfo());
	    BillResponse updatedBillResponse = objectMapper.convertValue(updatedResponse, BillResponse.class);
	    Bill updatedBill = updatedBillResponse.getBill().get(0);
	    
	    CustomAmountUpdateResponse responseObj = new CustomAmountUpdateResponse();
	    responseObj.setBillId(request.getBillId());
	    responseObj.setDemandId(DemandId);
	    responseObj.setCustomAmount(request.getCustomAmount());
	    responseObj.setMessage("Custom amount updated successfully");
	    responseObj.setOldAmount(bill.getTotalAmount());
	    responseObj.setUpdatedTotalAmount(updatedBill.getTotalAmount());
	    responseObj.setTenantId(request.getTenantId());
	    responseObj.setReason(request.getReason());
	    responseObj.setIsCustomAmountApplied(true);
	    responseObj.setConsumerCode(bill.getConsumerCode());
	    responseObj.setStatus(bill.getStatus() != null ? bill.getStatus().toString() : null);
	    responseObj.setUpdatedTime(System.currentTimeMillis());
	    responseObj.setUpdatedBy(request.getRequestInfo().getUserInfo().getUuid());
	    return responseObj;
	}

}