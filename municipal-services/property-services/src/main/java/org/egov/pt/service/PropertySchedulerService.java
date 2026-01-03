package org.egov.pt.service;

import java.math.BigDecimal;
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

import org.apache.commons.lang3.StringUtils;
import org.egov.mdms.model.MdmsResponse;
import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.models.CalculateTaxRequest;
import org.egov.pt.models.CalculateTaxResponse;
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
	
	@Value("${egov.sms.host}")
	private String smsHost;

	@Value("${egov.sms.tracker.create.endpoint}")
	private String smsTrackerCreateEndpoint;

	public CalculateTaxResponse calculateTax(CalculateTaxRequest calculateTaxRequest) {

    List<PtTaxCalculatorTracker> taxCalculatorTrackers = new ArrayList<>();
    
    Boolean isBilling = false;

    JsonNode ulbModules = null;
    JsonNode propertyTaxRateModules = null;
    JsonNode zones = null;
    JsonNode buildingStructures = null;
    JsonNode buildingEstablishmentYears = null;
    JsonNode buildingPurposes = null;
    JsonNode buildingUses = null;
    JsonNode overAllRebatePercentages = null;
    JsonNode earlyPaymentRebatePercentages = null;
    JsonNode propertyTaxRates = null;

    BigDecimal days = calculateDays(calculateTaxRequest);

    Map<String, Set<String>> errorMap = new HashMap<>();

    MdmsResponse mdmsResponse = mdmsService.getMdmsData(calculateTaxRequest.getRequestInfo(), null);

    if (mdmsResponse != null && mdmsResponse.getMdmsRes() != null
            && mdmsResponse.getMdmsRes().get(PTConstants.MDMS_MODULE_ULBS) != null) {

        ulbModules = objectMapper.valueToTree(mdmsResponse.getMdmsRes().get(PTConstants.MDMS_MODULE_ULBS));
        propertyTaxRateModules = objectMapper
                .valueToTree(mdmsResponse.getMdmsRes().get(PTConstants.MDMS_MODULE_PROPERTYTAXRATE));

        zones = objectMapper.valueToTree(ulbModules.get(PTConstants.MDMS_MASTER_DETAILS_ZONES));
        buildingStructures = objectMapper.valueToTree(ulbModules.get(PTConstants.MDMS_MASTER_DETAILS_BUILDINGSTRUCTURE));
        buildingEstablishmentYears = objectMapper.valueToTree(ulbModules.get(PTConstants.MDMS_MASTER_DETAILS_BUILDINGESTABLISHMENTYEAR));
        buildingPurposes = objectMapper.valueToTree(ulbModules.get(PTConstants.MDMS_MASTER_DETAILS_BUILDINGPURPOSE));
        buildingUses = objectMapper.valueToTree(ulbModules.get(PTConstants.MDMS_MASTER_DETAILS_BUILDINGUSE));
        overAllRebatePercentages = objectMapper.valueToTree(ulbModules.get(PTConstants.MDMS_MASTER_DETAILS_OVERALLREBATE));
        earlyPaymentRebatePercentages = objectMapper.valueToTree(ulbModules.get(PTConstants.MDMS_MASTER_DETAILS_EARLYPAYMENTREBATE));
        propertyTaxRates = objectMapper.valueToTree(propertyTaxRateModules.get(PTConstants.MDMS_MASTER_DETAILS_PROPERTYTAXRATE));
    }

    List<Property> properties = getProperties(calculateTaxRequest);
    properties = removeAlreadyTaxCalculatedProperties(properties, calculateTaxRequest);

    for (Property property : properties) {
    	
    	if (!Boolean.TRUE.equals(property.getIsBilling())) {
            log.info("Skipping tax calculation for property {} as isBilling={}",
                    property.getPropertyId(), property.getIsBilling());
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

        for (Unit unit : property.getUnits()) {
            BigDecimal totalRateableValue = BigDecimal.ZERO;
            BigDecimal netRateableValue = BigDecimal.ZERO;
            BigDecimal structuralFactor = null;
            BigDecimal ageFactor = null;
            BigDecimal occupancyFactor = null;
            BigDecimal useFactor = null;
            BigDecimal locationFactor = null;
            BigDecimal oAndMRebateAmount = BigDecimal.ZERO;
            BigDecimal oAndMRebatePercentage = null;
            BigDecimal propertyTaxRatePercentage = null;
            BigDecimal propertyTax = BigDecimal.ZERO;

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
                if (ulbName.equalsIgnoreCase(zone.get("ulbName").asText())
                        && addressAdditionalDetails.get("zone").asText().equalsIgnoreCase(zone.get("zoneName").asText())) {
                    locationFactor = new BigDecimal(zone.get("propertyRate").asText());
                }
            }
            for (JsonNode overAllRebatePercentage : objectMapper.valueToTree(overAllRebatePercentages)) {
                if (ulbName.equalsIgnoreCase(overAllRebatePercentage.get("ulbName").asText())) {
                    oAndMRebatePercentage = new BigDecimal(overAllRebatePercentage.get("rate").asText());
                }
            }

            // Validate factors
            if (structuralFactor == null) errorSet.add(PTConstants.MDMS_MASTER_DETAILS_BUILDINGSTRUCTURE + " (F2) is missing in mdms");
            if (ageFactor == null) errorSet.add(PTConstants.MDMS_MASTER_DETAILS_BUILDINGESTABLISHMENTYEAR + " (F3) is missing in mdms");
            if (occupancyFactor == null) errorSet.add(PTConstants.MDMS_MASTER_DETAILS_BUILDINGPURPOSE + " (F4) is missing in mdms");
            if (useFactor == null) errorSet.add(PTConstants.MDMS_MASTER_DETAILS_BUILDINGUSE + " (F5) is missing in mdms");
            if (locationFactor == null) errorSet.add(PTConstants.MDMS_MASTER_DETAILS_ZONES + " (F1) is missing in mdms");
            if (oAndMRebatePercentage == null) errorSet.add(PTConstants.MDMS_MASTER_DETAILS_OVERALLREBATE + " is missing in mdms");

            // Calculate totalRateableValue
            if (structuralFactor != null && ageFactor != null && occupancyFactor != null && useFactor != null
                    && locationFactor != null && property.getPropertyType().equalsIgnoreCase("BUILTUP")) {
                totalRateableValue = new BigDecimal(unitAdditionalDetails.get("propArea").asText())
                        .multiply(structuralFactor).multiply(ageFactor).multiply(occupancyFactor)
                        .multiply(useFactor).multiply(locationFactor);
            } else if (property.getPropertyType().equalsIgnoreCase("VACANT") && locationFactor != null) {
                totalRateableValue = new BigDecimal(unitAdditionalDetails.get("propArea").asText())
                        .multiply(locationFactor);
            } else {
                errorSet.add("PropertyType issue factor value is missing in mdms");
            }

            // Net rateable value after rebate
            if (!BigDecimal.ZERO.equals(totalRateableValue) && oAndMRebatePercentage != null) {
                oAndMRebateAmount = totalRateableValue.multiply(oAndMRebatePercentage.divide(BigDecimal.valueOf(100)));
                netRateableValue = totalRateableValue.subtract(oAndMRebateAmount);
            }

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
                    BigDecimal unitPropertyArea = new BigDecimal(unitAdditionalDetails.get("propArea").asText());

                    if (isAreaWithinRange(propertyAreaString, unitPropertyArea)) {
                        propertyTaxRatePercentage = new BigDecimal(propertyTaxRate.get("rate").asText());
                    }
                }
            }

            if (!BigDecimal.ZERO.equals(netRateableValue) && propertyTaxRatePercentage != null) {
                propertyTax = netRateableValue.multiply(propertyTaxRatePercentage.divide(BigDecimal.valueOf(100)));
            } else {
                errorSet.add(PTConstants.MDMS_MASTER_DETAILS_PROPERTYTAXRATE + " is missing in mdms");
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
                        .put(PTConstants.MDMS_MASTER_DETAILS_ZONES+"(f1)", locationFactor != null ? locationFactor.doubleValue() : 0)
                        .put(PTConstants.MDMS_MASTER_DETAILS_OVERALLREBATE, oAndMRebatePercentage != null ? oAndMRebatePercentage.doubleValue() : 0)
                        .put(PTConstants.MDMS_MASTER_DETAILS_PROPERTYTAXRATE, propertyTaxRatePercentage != null ? propertyTaxRatePercentage.doubleValue() : 0)
                        .put("netRateableValue", netRateableValue.doubleValue())
                        .put("propertyArea", unitAdditionalDetails.get("propArea").asText())
                        .put("propertyTaxCalculated", propertyTax.doubleValue())
                        .put("propertyTaxGenerated", propertyTaxGenerated.doubleValue())
                        .put("days", days.doubleValue())
                        .put("unitId", unit.getId());

                if (property.getPropertyType().equalsIgnoreCase("BUILTUP")) {
                    node.put(PTConstants.MDMS_MASTER_DETAILS_BUILDINGSTRUCTURE+"(f2)", structuralFactor != null ? structuralFactor.doubleValue() : 0)
                        .put(PTConstants.MDMS_MASTER_DETAILS_BUILDINGESTABLISHMENTYEAR+"(f3)", ageFactor != null ? ageFactor.doubleValue() : 0)
                        .put(PTConstants.MDMS_MASTER_DETAILS_BUILDINGPURPOSE+"(f4)", occupancyFactor != null ? occupancyFactor.doubleValue() : 0)
                        .put(PTConstants.MDMS_MASTER_DETAILS_BUILDINGUSE+"(f5)", useFactor != null ? useFactor.doubleValue() : 0);
                }

                trackeradditionalDetails.add(node);
            }
        }

        if (!errorMap.containsKey(property.getPropertyId()) && !BigDecimal.ZERO.equals(totalPropertyTax)) {

            oneDayPropertyTax = totalPropertyTax.divide(BigDecimal.valueOf(365), 6, RoundingMode.HALF_UP);
            finalPropertyTax = oneDayPropertyTax.multiply(days);
            propertyTaxWithoutRebate = finalPropertyTax;

            // early payment rebate
            BigDecimal epRebatePercentage = null;
            for (JsonNode earlyPaymentRebatePercentage : objectMapper.valueToTree(earlyPaymentRebatePercentages)) {
                if (ulbName.equalsIgnoreCase(earlyPaymentRebatePercentage.get("ulbName").asText())) {
                    epRebatePercentage = new BigDecimal(earlyPaymentRebatePercentage.get("rate").asText());
                }
            }
            if (epRebatePercentage != null) {
                rebateAmount = finalPropertyTax.multiply(epRebatePercentage.divide(BigDecimal.valueOf(100)));
                finalPropertyTax = finalPropertyTax.subtract(rebateAmount);
            }

            BillResponse billResponse = generateDemandAndBill(calculateTaxRequest, property, finalPropertyTax);

            if (billResponse != null && !CollectionUtils.isEmpty(billResponse.getBill())) {

                PtTaxCalculatorTrackerRequest ptTaxCalculatorTrackerRequest = enrichmentService
                        .enrichTaxCalculatorTrackerCreateRequest(property, calculateTaxRequest, finalPropertyTax,
                                trackeradditionalDetails, billResponse.getBill(), rebateAmount,
                                propertyTaxWithoutRebate);

                PropertyBillFailure propertyBillFailure = enrichmentService.enrichPtBillFailure(property, calculateTaxRequest,billResponse,null);
                propertyService.removePtBillFailure(propertyBillFailure);

                PtTaxCalculatorTracker ptTaxCalculatorTracker = propertyService
                        .saveToPtTaxCalculatorTracker(ptTaxCalculatorTrackerRequest);

                taxCalculatorTrackers.add(ptTaxCalculatorTracker);

                // 1️ Notification flow
                try {
                    notificationService.triggerNotificationsGenerateBill(
                            ptTaxCalculatorTracker,
                            billResponse.getBill().get(0),
                            ptTaxCalculatorTrackerRequest.getRequestInfo()
                    );
                } catch (Exception ex) {
                    log.error("Notification flow failed for property billId {}", billResponse.getBill().get(0).getId(), ex);
                }

                // SMS tracker creation flow
                try {
                    if (CollectionUtils.isEmpty(property.getOwners())) {
                        throw new RuntimeException("No owners found for property " + property.getPropertyId());
                    }

                    ObjectNode smsRequestJson = notificationService.buildGeneratePropertyBillSmsRequest(
                            property, billResponse.getBill().get(0), ptTaxCalculatorTracker);
                    
                    log.info("SMS Request ObjectNode : {}", smsRequestJson);

                    // Convert ObjectNode to Map
                    Map<String, Object> smsRequestMap = new ObjectMapper().convertValue(smsRequestJson, Map.class);

                    
                    log.info("SMS Request Map : {}", smsRequestMap);
                    
                    StringBuilder smsTrackerUri = new StringBuilder();
                    smsTrackerUri.append(smsHost).append(smsTrackerCreateEndpoint);

                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                    String fromDateStr = formatter.format(calculateTaxRequest.getFromDate());
                    String toDateStr = formatter.format(calculateTaxRequest.getToDate());
                    
                    
                    String ward = null;
                    if (property.getAddress() != null
                            && property.getAddress().getAdditionalDetails() != null) {
                        JsonNode wardNumberNode = addressAdditionalDetails.get("wardNumber");

                        if (wardNumberNode != null && !wardNumberNode.isNull()) {
                            ward = wardNumberNode.asText();
                        }
                    }
                    
                    Map<String, Object> smsTrackerRequest = new HashMap<>();
                    smsTrackerRequest.put("uuid", UUID.randomUUID().toString());
                    smsTrackerRequest.put("amount", finalPropertyTax);
                    smsTrackerRequest.put("applicationNo", property.getPropertyId());
                    smsTrackerRequest.put("tenantId", property.getTenantId());
                    smsTrackerRequest.put("service", "PROPERTY");
                    smsTrackerRequest.put("fromDate", fromDateStr);
                    smsTrackerRequest.put("toDate", toDateStr);
                    smsTrackerRequest.put("createdBy", "system");
                    smsTrackerRequest.put("createdTime", System.currentTimeMillis());
                    smsTrackerRequest.put("billId", billResponse.getBill().get(0).getId());
                    smsTrackerRequest.put("smsStatus", false);
                    smsTrackerRequest.put("additionalDetail", trackeradditionalDetails);
                    smsTrackerRequest.put("ownerMobileNo", property.getOwners().get(0).getMobileNumber());
                    smsTrackerRequest.put("ownerName", property.getOwners().get(0).getName());
                    smsTrackerRequest.put("smsRequest", smsRequestMap);
                    smsTrackerRequest.put("smsResponse", null);
                    smsTrackerRequest.put("ward", ward);
                    

                    Object response = restCallRepository.fetchResult(smsTrackerUri, smsTrackerRequest);
                    log.info("SMS Tracker Response : {}", response);
                    log.info("SMS tracker entry created for property billId {}", billResponse.getBill().get(0).getId());
                    
                    log.info("SMS Tracker URI : {}", smsTrackerUri.toString());
                    log.info("SMS Tracker Request JSON : {}",smsTrackerRequest);

                } catch (Exception e) {
                    log.error("SMS tracker creation failed for property billId {}", billResponse.getBill().get(0).getId(), e);
                }

            } else {
                createFailureLog(property, calculateTaxRequest, billResponse, null);
            }
        } else {
            createFailureLog(property, calculateTaxRequest, null, errorMap.get(property.getPropertyId()));
        }
    }

    return CalculateTaxResponse.builder().taxCalculatorTrackers(taxCalculatorTrackers).build();
}
	
	private void createFailureLog(Property property,CalculateTaxRequest generateBillRequest, BillResponse billResponse,Set<String> errorMap) {
		PropertyBillFailure propertyBillFailure	= enrichmentService.enrichPtBillFailure(property, generateBillRequest,billResponse,errorMap);
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
//			int financialYearStart = currentDate.isBefore(LocalDate.of(currentYear, 4, 1)) ? currentYear
//					: currentYear - 1;
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

		PropertyCriteria propertyCriteria = PropertyCriteria.builder().isSchedulerCall(true)
				.status(Collections.singleton(Status.APPROVED)).propertyIds(calculateTaxRequest.getPropertyIds()).isActiveUnit(true)
				.build();

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

			return trackers == null || trackers.stream().noneMatch(tracker ->
		    tracker.getBillStatus() != BillStatus.CANCELLED &&
		    isOverlapping(
		        tracker.getFromDate(),
		        tracker.getToDate(),
		        calculateTaxRequest.getFromDate(),
		        calculateTaxRequest.getToDate()
		    )
		);

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

	public Map<String, Integer> getPenaltyRateMap(MdmsResponse mdmsResponse) {
		return Optional.ofNullable(mdmsResponse).map(MdmsResponse::getMdmsRes)
				.map(mdmsRes -> mdmsRes.get(PTConstants.MDMS_MODULE_ULBS)).map(objectMapper::valueToTree)
				.map(ulbsNode -> {
					JsonNode jsonNode = (JsonNode) ulbsNode; // explicit cast
					return jsonNode.get(PTConstants.MDMS_MASTER_DETAILS_PENALTYRATE);
				}).filter(JsonNode::isArray)
				.map(rebateDaysNode -> StreamSupport.stream(rebateDaysNode.spliterator(), false)
						.collect(Collectors.toMap(node -> propertyConfiguration.getStateLevelTenantId() + "."
								+ node.get("ulbName").asText(), node -> node.get("rate").asInt())))
				.orElseGet(HashMap::new);
	}

	private List<PtTaxCalculatorTracker> getTrackersForTenantAndDays(String tenantId, int days) {
		long startDateTime = LocalDate.now().minusDays(days).atStartOfDay(ZoneId.systemDefault()).toInstant()
				.toEpochMilli();
		long endDateTime = LocalDate.now().minusDays(days).atTime(23, 59, 59, 999_999_999)
				.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

		PtTaxCalculatorTrackerSearchCriteria criteria = PtTaxCalculatorTrackerSearchCriteria.builder()
				.startDateTime(startDateTime).endDateTime(endDateTime).tenantId(tenantId)
				.billStatus(Collections.singleton(BillStatus.ACTIVE)).build();

		return propertyService.getTaxCalculatedProperties(criteria);
	}

	private List<PtTaxCalculatorTracker> getTrackersForTenantAndStartDays(String tenantId, int days) {
		long startDateTime = LocalDate.now().minusDays(days).atStartOfDay(ZoneId.systemDefault()).toInstant()
				.toEpochMilli();

		PtTaxCalculatorTrackerSearchCriteria criteria = PtTaxCalculatorTrackerSearchCriteria.builder()
				.startDateTime(startDateTime).tenantId(tenantId).billStatus(Collections.singleton(BillStatus.ACTIVE))
				.build();

		return propertyService.getTaxCalculatedProperties(criteria);
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
			RequestInfoWrapper requestInfoWrapper, Map<String, Integer> tenantIdPenaltyRateMap) {
		List<PtTaxCalculatorTracker> updatedTrackers = new ArrayList<>();

		for (PtTaxCalculatorTracker tracker : trackers) {
			if (!isValidTracker(tracker)) {
				continue;
			}

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
				tracker.setRebateAmount(BigDecimal.ZERO);
			}

			if (!newAmount.equals(BigDecimal.ZERO)) {
				try {
					updateBillAndDemandAmounts(bill, demandIdToDemandMap, newAmount, requestInfoWrapper);

					tracker.setPropertyTax(newAmount);

					PtTaxCalculatorTrackerRequest updateRequest = enrichmentService
							.enrichTaxCalculatorTrackerUpdateRequest(tracker, requestInfoWrapper.getRequestInfo());

					PtTaxCalculatorTracker updatedTracker = propertyService.updatePtTaxCalculatorTracker(updateRequest);

					updatedTrackers.add(updatedTracker);	
				}catch(Exception e) {
					log.error(e.getMessage());
				}
			}
		}

		return updatedTrackers;
	}

	private BigDecimal calculatePenalty(PtTaxCalculatorTracker tracker, int penaltyRatePercent) {
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

	private void updateBillAndDemandAmounts(Bill bill, Map<String, Demand> demandIdToDemandMap, BigDecimal newAmount,
			RequestInfoWrapper requestInfoWrapper) {

		for (BillDetail billDetail : bill.getBillDetails()) {
			Demand demand = demandIdToDemandMap.get(billDetail.getDemandId());
			if (demand != null) {
				demand.setMinimumAmountPayable(newAmount);
				if (demand.getDemandDetails() != null) {
					demand.getDemandDetails().forEach(demandDetail -> demandDetail.setTaxAmount(newAmount));
				}
				demandService.updateDemand(requestInfoWrapper.getRequestInfo(), Collections.singletonList(demand));
			}

			if (billDetail.getBillAccountDetails() != null) {
				billDetail.getBillAccountDetails().forEach(billAccountDetail -> billAccountDetail.setAmount(newAmount));
			}

			billDetail.setAmount(newAmount);
		}

		bill.setTotalAmount(newAmount);
		billService.updateBill(requestInfoWrapper.getRequestInfo(), Collections.singletonList(bill));
	}

	public Object updatePenaltyAmount(RequestInfoWrapper requestInfoWrapper) {
		List<PtTaxCalculatorTracker> latestTaxCalculatorTrackers = new ArrayList<>();

		List<String> taxCalculatedTenantIds = getActiveTaxCalculatedTenantIds();

		if (CollectionUtils.isEmpty(taxCalculatedTenantIds)) {
			return Collections.emptyList();
		}

		MdmsResponse mdmsResponse = mdmsService.getPenaltyRateMdmsData(requestInfoWrapper.getRequestInfo(), null);

		Map<String, Integer> tenantIdPenaltyRateMap = getPenaltyRateMap(mdmsResponse);

		for (String tenantId : taxCalculatedTenantIds) {
			Integer days = 30; // need to change if needed

			List<PtTaxCalculatorTracker> ptTaxCalculatorTrackers = getTrackersForTenantAndStartDays(tenantId, days);

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

}
