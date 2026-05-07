package org.egov.noc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import org.egov.noc.config.NOCConfiguration;
import org.egov.noc.repository.ServiceRequestRepository;
import org.egov.noc.web.model.Noc;
import org.egov.noc.web.model.NocRequest;
import org.egov.noc.web.model.bpa.Address;
import org.egov.noc.web.model.bpa.Boundary;
import org.egov.noc.web.model.bpa.GeoLocation;
import org.egov.noc.web.model.property.Property;
import org.egov.noc.web.model.property.PropertyRequest;
import org.egov.noc.web.model.property.PropertyResponse;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NOCPropertyService {

	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private NOCConfiguration config;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	public StringBuilder getPropertyCreateURL() {
		return new StringBuilder().append(config.getPropertyHost()).append(config.getPropertyCreateEndpoint());
	}

	public StringBuilder getPropertyURL() {
		return new StringBuilder().append(config.getPropertyHost()).append(config.getPropertySearchEndpoint());
	}



	public void createProperty(NocRequest nocRequest, Object mdmsData) {
		Noc noc = nocRequest.getNoc();
		Property property = createPropertFromNOC(noc, mdmsData);

		PropertyRequest propertyRequest = PropertyRequest.builder().property(property)
				.requestInfo(nocRequest.getRequestInfo()).build();

		Object result = serviceRequestRepository.fetchResult(getPropertyCreateURL(), propertyRequest);
		List<Property> propertyList = getPropertyDetails(result);
		String propertyId = propertyList.isEmpty() ? null : propertyList.stream().findFirst().get().getPropertyId();
		if (propertyId == null) {
			throw new CustomException("PROPERTY CREATION ERROR", "Property services error");

		}
		Object object = nocRequest.getNoc().getNocDetails().getAdditionalDetails();
		DocumentContext context = JsonPath.parse(object);
		context.put("$.applicationDetails.owners.*", "propertyId", propertyId);

	}

	public List<Property> getPropertyDetails(Object result) {

		try {
			DocumentContext context = JsonPath.using(Configuration.defaultConfiguration()).parse(result);
			context.put("Properties.*.owners.*", "status", true);
			PropertyResponse propertyResponse = objectMapper.convertValue(result, PropertyResponse.class);
			return propertyResponse.getProperties();
		} catch (Exception ex) {
			throw new CustomException("PARSING_ERROR", "The property json cannot be parsed");
		}
	}

	private Property createPropertFromNOC(Noc noc, Object mdmsData) {
		
		Map<String,Object> additionalDetails = (Map<String, Object>)noc.getNocDetails().getAdditionalDetails();
		Map<String, Object> siteDetails = (Map<String, Object>) additionalDetails.get("siteDetails");
		Map<String, String> coordinates = (Map<String, String>) additionalDetails.get("coordinates");		
		String buildingStatus = siteDetails.getOrDefault("buildingStatus", "").toString();
		String specificationBuildingCategory = siteDetails.getOrDefault("specificationBuildingCategory", "").toString();
		String localityCode = JsonPath.read(siteDetails, "$.localityAreaType.code");
		List<Object> floorArea = (List<Object>)siteDetails.getOrDefault("floorArea", Collections.EMPTY_LIST);
		
		List<String> buildingTypeList = JsonPath.read(mdmsData, "$.MdmsRes.NOC.BuildingType.[?(@.name == '" + buildingStatus + "')].code");
		List<String> propertyUsageList = JsonPath.read(mdmsData, "$.MdmsRes.NOC.BuildingCategory.[?(@.name == '" + specificationBuildingCategory + "')].propertyUsage");
		
		if(CollectionUtils.isEmpty(propertyUsageList))
			throw new CustomException("UPDATE ERROR", "Property Usage not found for the Building Category : " + specificationBuildingCategory);
		
		Address address = Address.builder()
				.tenantId(noc.getTenantId())
				.plotNo(siteDetails.getOrDefault("plotNo", "").toString())
				.district(siteDetails.getOrDefault("district", "").toString())
				.city(siteDetails.getOrDefault("ulbName", "").toString())
				.geoLocation(GeoLocation.builder()
						.latitude(coordinates.get("Latitude1") != null ? Double.valueOf(coordinates.get("Latitude1")) : null )
						.longitude(coordinates.get("Longitude1") != null ? Double.valueOf(coordinates.get("Longitude1")) : null ).build())
				.locality(Boundary.builder().code(localityCode).build())
				.build();
		
		noc.getOwners().stream().forEach(owner -> {
			if(owner.getOwnerType() == null)
				owner.setOwnerType("NONE");
		});
		
		ObjectNode propertyAdditionalDetails = (ObjectNode)JsonNodeFactory.instance.objectNode();
		propertyAdditionalDetails.put("vasikaNo", noc.getVasikaNumber());
		propertyAdditionalDetails.put("vasikaDate", noc.getVasikaDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
		
		return Property.builder()
				.address(address).accountId(noc.getAccountId())
				.landArea(new BigDecimal(siteDetails.get("netTotalArea").toString())
						.multiply(BigDecimal.valueOf(1.19599)).setScale(2, RoundingMode.HALF_UP).doubleValue())
				.usageCategory(propertyUsageList.get(0))
				.ownershipCategory(noc.getOwners().size() == 1 ? "INDIVIDUAL.SINGLEOWNER" : "INDIVIDUAL.MULTIPLEOWNERS" )
				.owners(noc.getOwners())
				.tenantId(noc.getTenantId())
				.propertyType(buildingTypeList.get(0))
				.noOfFloors(Long.valueOf(floorArea != null ? floorArea.size() : 0 ))
				.additionalDetails(propertyAdditionalDetails)
				.build();
		
	}

}
