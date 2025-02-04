package org.egov.pt.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.egov.mdms.model.MdmsResponse;
import org.egov.pt.models.Property;
import org.egov.pt.models.PropertyCriteria;
import org.egov.pt.models.Unit;
import org.egov.pt.models.bill.Demand;
import org.egov.pt.models.bill.GenerateBillCriteria;
import org.egov.pt.models.collection.BillResponse;
import org.egov.pt.models.enums.Status;
import org.egov.pt.util.PTConstants;
import org.egov.pt.web.contracts.RequestInfoWrapper;
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

	public Object calculateTax(RequestInfoWrapper requestInfoWrapper) {

		JsonNode ulbs = null;
		JsonNode zones = null;
		JsonNode buildingStructures = null;
		JsonNode buildingEstablishmentYears = null;
		JsonNode buildingPurposes = null;
		JsonNode buildingUses = null;

		Map<String, Set<String>> errorMap = new HashMap<>();

		MdmsResponse mdmsResponse = mdmsService.getULBSMdmsData(requestInfoWrapper.getRequestInfo(), null);

		if (null != mdmsResponse && null != mdmsResponse.getMdmsRes()
				&& null != mdmsResponse.getMdmsRes().get(PTConstants.MDMS_MODULE_ULBS)) {
			ulbs = objectMapper.valueToTree(mdmsResponse.getMdmsRes().get(PTConstants.MDMS_MODULE_ULBS));

			zones = objectMapper.valueToTree(ulbs.get(PTConstants.MDMS_MASTER_DETAILS_ZONES));
			buildingStructures = objectMapper.valueToTree(ulbs.get(PTConstants.MDMS_MASTER_DETAILS_BUILDINGSTRUCTURE));
			buildingEstablishmentYears = objectMapper
					.valueToTree(ulbs.get(PTConstants.MDMS_MASTER_DETAILS_BUILDINGESTABLISHMENTYEAR));
			buildingPurposes = objectMapper.valueToTree(ulbs.get(PTConstants.MDMS_MASTER_DETAILS_BUILDINGPURPOSE));
			buildingUses = objectMapper.valueToTree(ulbs.get(PTConstants.MDMS_MASTER_DETAILS_BUILDINGUSE));
		}

		PropertyCriteria propertyCriteria = PropertyCriteria.builder().isSchedulerCall(true)
				.status(Collections.singleton(Status.APPROVED)).build();

		List<Property> properties = propertyService.searchProperty(propertyCriteria,
				requestInfoWrapper.getRequestInfo());

		for (Property property : properties) {

			String ulbName = property.getTenantId().split("\\.")[1];
//			System.err.println("ulbName " + ulbName);
			BigDecimal totalRateableValue = BigDecimal.ZERO;
			BigDecimal netRateableValue = BigDecimal.ZERO;
			BigDecimal locationFactor = null;

			for (Unit unit : property.getUnits()) {
				BigDecimal unitRateableValue = BigDecimal.ZERO;
				BigDecimal structuralFactor = null;
				BigDecimal ageFactor = null;
				BigDecimal occupancyFactor = null;
				BigDecimal useFactor = null;

				Set<String> errorSet = new HashSet<>();

//				System.err.println(unit.getAdditionalDetails());
				JsonNode unitAdditionalDetails = objectMapper.valueToTree(unit.getAdditionalDetails());
//				System.err.println("F2" + unitAdditionalDetails.get("propBuildingType").asText()); // F2
//				System.err.println("F3" + unitAdditionalDetails.get("propYearOfCons").asText()); // F3
//				System.err.println("F4" + unitAdditionalDetails.get("propType").asText()); // F4
//				System.err.println("F5" + unitAdditionalDetails.get("uses").asText()); // F5
				for (JsonNode buildingStructure : objectMapper.valueToTree(buildingStructures)) {
					if (ulbName.equalsIgnoreCase(buildingStructure.get("ulbName").asText())
							&& buildingStructure.get("structureType").asText()
									.equalsIgnoreCase(unitAdditionalDetails.get("propBuildingType").asText())) {
//						System.err.println(buildingStructure.get("rate").asDouble()); // F2
						structuralFactor = new BigDecimal(buildingStructure.get("rate").asText());
					}
				}
				for (JsonNode buildingEstablishmentYear : objectMapper.valueToTree(buildingEstablishmentYears)) {
					if (ulbName.equalsIgnoreCase(buildingEstablishmentYear.get("ulbName").asText())
							&& buildingEstablishmentYear.get("yearRange").asText()
									.equalsIgnoreCase(unitAdditionalDetails.get("propYearOfCons").asText())) {
//						System.err.println(buildingEstablishmentYear.get("rate").asDouble()); // F3
						ageFactor = new BigDecimal(buildingEstablishmentYear.get("rate").asText());
					}
				}
				for (JsonNode buildingPurpose : objectMapper.valueToTree(buildingPurposes)) {
					if (ulbName.equalsIgnoreCase(buildingPurpose.get("ulbName").asText())
							&& buildingPurpose.get("purposeName").asText()
									.equalsIgnoreCase(unitAdditionalDetails.get("propType").asText())) {
//						System.err.println(buildingPurpose.get("rate").asDouble()); // F4
						occupancyFactor = new BigDecimal(buildingPurpose.get("rate").asText());
					}
				}
				for (JsonNode buildingUse : objectMapper.valueToTree(buildingUses)) {
					if (ulbName.equalsIgnoreCase(buildingUse.get("ulbName").asText())
							&& buildingUse.get("useOfBuilding").asText()
									.equalsIgnoreCase(unitAdditionalDetails.get("uses").asText())) {
//						System.err.println(buildingUse.get("rate").asDouble()); // F5
						useFactor = new BigDecimal(buildingUse.get("rate").asText());
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

//				System.err.println("propArea " + unitAdditionalDetails.get("propArea").asDouble() + " structuralFactor "
//						+ structuralFactor + " ageFactor " + ageFactor + " occupancyFactor " + occupancyFactor
//						+ " useFactor " + useFactor);

				if (null != structuralFactor && null != ageFactor && null != occupancyFactor && null != useFactor) {
					unitRateableValue = new BigDecimal(unitAdditionalDetails.get("propArea").asText())
							.multiply(structuralFactor).multiply(ageFactor).multiply(occupancyFactor)
							.multiply(useFactor);
				}
//				System.err.println("unitRateableValue " + unitRateableValue);
				totalRateableValue = totalRateableValue.add(unitRateableValue);

				if (!CollectionUtils.isEmpty(errorSet)) {
					errorMap.put(property.getPropertyId(), errorSet);
				}
			}

			JsonNode addressAdditionalDetails = objectMapper.valueToTree(property.getAddress().getAdditionalDetails());
//			System.err.println("F1" + addressAdditionalDetails.get("zone").asText()); // F1

			for (JsonNode zone : objectMapper.valueToTree(zones)) {
//				System.err.println(zone.get("zoneName").asText());
				if (addressAdditionalDetails.get("zone").asText().equalsIgnoreCase(zone.get("zoneName").asText())) {
//					System.err.println(zone.get("propertyTaxCalculation").asDouble()); // F1
					locationFactor = new BigDecimal(zone.get("propertyRate").asText());
				}
			}
			if (null != locationFactor) {
				totalRateableValue = totalRateableValue.multiply(locationFactor);
			} else {
				if (errorMap.containsKey(property.getPropertyId())) {
					errorMap.get(property.getPropertyId())
							.add(PTConstants.MDMS_MASTER_DETAILS_ZONES + " is missing in mdms");
				} else {
					errorMap.put(property.getPropertyId(),
							Collections.singleton(PTConstants.MDMS_MASTER_DETAILS_ZONES + " is missing in mdms"));
				}
			}

//			System.err.println("ptid " + property.getPropertyId());
//			System.err.println("totalRateableValue " + totalRateableValue);
			// TODO calculation
			if (!BigDecimal.ZERO.equals(totalRateableValue)) {
				netRateableValue = totalRateableValue.multiply(new BigDecimal(0.1)); // TODO
			}

			if (!errorMap.containsKey(property.getPropertyId())) {
//				System.out.println("Calculate bill " + property.getPropertyId());
				List<Demand> savedDemands = new ArrayList<>();
				// generate demand
				savedDemands = demandService.generateDemand(requestInfoWrapper.getRequestInfo(), property,
						property.getBusinessService(), netRateableValue);

				if (CollectionUtils.isEmpty(savedDemands)) {
					throw new CustomException("INVALID_CONSUMERCODE",
							"Bill not generated due to no Demand found for the given consumerCode");
				}

				// fetch/create bill
				GenerateBillCriteria billCriteria = GenerateBillCriteria.builder().tenantId(property.getTenantId())
						.businessService(property.getBusinessService()).consumerCode(property.getPropertyId()).build();

				BillResponse billResponse = billService.generateBill(requestInfoWrapper.getRequestInfo(), billCriteria);
			}

		}

//		System.err.println("errorMap " + errorMap);

		return properties;
	}

}
