package org.egov.pt.service;

import java.math.BigDecimal;
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
import java.util.Set;

import org.egov.mdms.model.MdmsResponse;
import org.egov.pt.models.CalculateTaxRequest;
import org.egov.pt.models.Property;
import org.egov.pt.models.PropertyCriteria;
import org.egov.pt.models.Unit;
import org.egov.pt.models.bill.Demand;
import org.egov.pt.models.bill.GenerateBillCriteria;
import org.egov.pt.models.collection.BillResponse;
import org.egov.pt.models.enums.Status;
import org.egov.pt.util.PTConstants;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

	public void calculateTax(CalculateTaxRequest calculateTaxRequest) {

		JsonNode ulbModules = null;
		JsonNode propertyTaxRateModules = null;
		JsonNode zones = null;
		JsonNode buildingStructures = null;
		JsonNode buildingEstablishmentYears = null;
		JsonNode buildingPurposes = null;
		JsonNode buildingUses = null;
		JsonNode overAllRebatePercentages = null;
		JsonNode propertyTaxRates = null;

		Map<String, Set<String>> errorMap = new HashMap<>();

		BigDecimal days = calculateDays(calculateTaxRequest.getFromDate(), calculateTaxRequest.getToDate());

		MdmsResponse mdmsResponse = mdmsService.getMdmsData(calculateTaxRequest.getRequestInfo(), null);

		if (null != mdmsResponse && null != mdmsResponse.getMdmsRes()
				&& null != mdmsResponse.getMdmsRes().get(PTConstants.MDMS_MODULE_ULBS)) {
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

			propertyTaxRates = objectMapper
					.valueToTree(propertyTaxRateModules.get(PTConstants.MDMS_MASTER_DETAILS_PROPERTYTAXRATE));
		}

		PropertyCriteria propertyCriteria = PropertyCriteria.builder().isSchedulerCall(true)
				.status(Collections.singleton(Status.APPROVED)).build();

		List<Property> properties = propertyService.searchProperty(propertyCriteria,
				calculateTaxRequest.getRequestInfo());

		for (Property property : properties) {

			BigDecimal totalPropertyTax = BigDecimal.ZERO;
			BigDecimal oneDayPropertyTax = BigDecimal.ZERO;
			BigDecimal finalPropertyTax = BigDecimal.ZERO;
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

				for (JsonNode buildingStructure : objectMapper.valueToTree(buildingStructures)) {
					if (ulbName.equalsIgnoreCase(buildingStructure.get("ulbName").asText())
							&& buildingStructure.get("structureType").asText()
									.equalsIgnoreCase(unitAdditionalDetails.get("propBuildingType").asText())) {
						structuralFactor = new BigDecimal(buildingStructure.get("rate").asText());
					}
				}
				for (JsonNode buildingEstablishmentYear : objectMapper.valueToTree(buildingEstablishmentYears)) {
					if (ulbName.equalsIgnoreCase(buildingEstablishmentYear.get("ulbName").asText())
							&& buildingEstablishmentYear.get("yearRange").asText()
									.equalsIgnoreCase(unitAdditionalDetails.get("propYearOfCons").asText())) {
						ageFactor = new BigDecimal(buildingEstablishmentYear.get("rate").asText());
					}
				}
				for (JsonNode buildingPurpose : objectMapper.valueToTree(buildingPurposes)) {
					if (ulbName.equalsIgnoreCase(buildingPurpose.get("ulbName").asText())
							&& buildingPurpose.get("purposeName").asText()
									.equalsIgnoreCase(unitAdditionalDetails.get("propType").asText())) {
						occupancyFactor = new BigDecimal(buildingPurpose.get("rate").asText());
					}
				}
				for (JsonNode buildingUse : objectMapper.valueToTree(buildingUses)) {
					if (ulbName.equalsIgnoreCase(buildingUse.get("ulbName").asText())
							&& buildingUse.get("useOfBuilding").asText()
									.equalsIgnoreCase(unitAdditionalDetails.get("uses").asText())) {
						useFactor = new BigDecimal(buildingUse.get("rate").asText());
					}
				}

				for (JsonNode zone : objectMapper.valueToTree(zones)) {
					if (addressAdditionalDetails.get("zone").asText().equalsIgnoreCase(zone.get("zoneName").asText())) {
						locationFactor = new BigDecimal(zone.get("propertyRate").asText());
					}
				}
				for (JsonNode overAllRebatePercentage : objectMapper.valueToTree(overAllRebatePercentages)) {
					if (ulbName.equalsIgnoreCase(overAllRebatePercentage.get("ulbName").asText())) {
						oAndMRebatePercentage = new BigDecimal(overAllRebatePercentage.get("rate").asText());
					}
				}

				if (null == structuralFactor) {
					errorSet.add(PTConstants.MDMS_MASTER_DETAILS_BUILDINGSTRUCTURE + " is missing in mdms");
				}
				if (null == ageFactor) {
					errorSet.add(PTConstants.MDMS_MASTER_DETAILS_BUILDINGESTABLISHMENTYEAR + " is missing in mdms");
				}
				if (null == occupancyFactor) {
					errorSet.add(PTConstants.MDMS_MASTER_DETAILS_BUILDINGPURPOSE + " is missing in mdms");
				}
				if (null == useFactor) {
					errorSet.add(PTConstants.MDMS_MASTER_DETAILS_BUILDINGUSE + " is missing in mdms");
				}
				if (null == locationFactor) {
					errorSet.add(PTConstants.MDMS_MASTER_DETAILS_ZONES + " is missing in mdms");
				}
				if (null == oAndMRebatePercentage) {
					errorSet.add(PTConstants.MDMS_MASTER_DETAILS_OVERALLREBATE + " is missing in mdms");
				}

				if (null != structuralFactor && null != ageFactor && null != occupancyFactor && null != useFactor
						&& null != locationFactor) {
					totalRateableValue = new BigDecimal(unitAdditionalDetails.get("propArea").asText())
							.multiply(structuralFactor).multiply(ageFactor).multiply(occupancyFactor)
							.multiply(useFactor).multiply(locationFactor);
				}

				if (!BigDecimal.ZERO.equals(totalRateableValue)) {
					oAndMRebateAmount = totalRateableValue
							.multiply(oAndMRebatePercentage.divide(BigDecimal.valueOf(100)));
					netRateableValue = totalRateableValue.subtract(oAndMRebateAmount);
				}

				for (JsonNode propertyTaxRate : propertyTaxRates) {
					// Check if the relevant details match
					if (ulbName.equalsIgnoreCase(propertyTaxRate.get("ulbName").asText())
							&& addressAdditionalDetails.get("zone").asText().equalsIgnoreCase(
									propertyTaxRate.get("zoneName").asText().replaceFirst(ulbName + ".", ""))
							&& propertyTaxRate.get("purposeName").asText()
									.equalsIgnoreCase(unitAdditionalDetails.get("propType").asText())
							&& propertyTaxRate.get("useOfBuilding").asText()
									.equalsIgnoreCase(unitAdditionalDetails.get("uses").asText())) {

						String propertyAreaString = propertyTaxRate.get("propertyArea").asText();
						BigDecimal unitPropertyArea = new BigDecimal(unitAdditionalDetails.get("propArea").asText());

						if (isAreaWithinRange(propertyAreaString, unitPropertyArea)) {
							propertyTaxRatePercentage = new BigDecimal(propertyTaxRate.get("rate").asText());
						}
					}
				}

				if (!BigDecimal.ZERO.equals(netRateableValue) && null != propertyTaxRatePercentage) {
					propertyTax = netRateableValue.multiply(propertyTaxRatePercentage.divide(BigDecimal.valueOf(100)));
				} else {
					errorSet.add(PTConstants.MDMS_MASTER_DETAILS_PROPERTYTAXRATE + " is missing in mdms");
				}

				if (!BigDecimal.ZERO.equals(propertyTax)) {
					totalPropertyTax = totalPropertyTax.add(propertyTax);
				}

				if (!CollectionUtils.isEmpty(errorSet)) {
					errorMap.put(property.getPropertyId(), errorSet);
				}
			}

			if (!errorMap.containsKey(property.getPropertyId()) && !BigDecimal.ZERO.equals(totalPropertyTax)) {

				oneDayPropertyTax = totalPropertyTax.divide(BigDecimal.valueOf(365), 0);

				finalPropertyTax = oneDayPropertyTax.multiply(days);

				List<Demand> savedDemands = new ArrayList<>();
				// generate demand
				savedDemands = demandService.generateDemand(calculateTaxRequest.getRequestInfo(), property,
						property.getBusinessService(), finalPropertyTax);

				if (CollectionUtils.isEmpty(savedDemands)) {
					throw new CustomException("INVALID_CONSUMERCODE",
							"Bill not generated due to no Demand found for the given consumerCode");
				}

				// fetch/create bill
				GenerateBillCriteria billCriteria = GenerateBillCriteria.builder().tenantId(property.getTenantId())
						.businessService(property.getBusinessService()).consumerCode(property.getPropertyId()).build();

				BillResponse billResponse = billService.generateBill(calculateTaxRequest.getRequestInfo(),
						billCriteria);
			}

		}

	}

	private BigDecimal calculateDays(Date fromDate, Date toDate) {
		BigDecimal days = new BigDecimal(365);
		if (null != fromDate && null != toDate) {
			LocalDate fromLocalDate = fromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate toLocalDate = toDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

			// Calculate the number of days between fromDate and toDate
			days = BigDecimal.valueOf(ChronoUnit.DAYS.between(fromLocalDate, toLocalDate));
		}

		return days;
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

}
