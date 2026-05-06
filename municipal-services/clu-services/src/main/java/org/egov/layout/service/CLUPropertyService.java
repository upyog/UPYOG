package org.egov.layout.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.layout.config.CLUConfiguration;
import org.egov.layout.repository.ServiceRequestRepository;
import org.egov.layout.web.model.Clu;
import org.egov.layout.web.model.CluRequest;
import org.egov.layout.web.model.bpa.Address;
import org.egov.layout.web.model.bpa.Boundary;
import org.egov.layout.web.model.bpa.GeoLocation;
import org.egov.layout.web.model.property.Property;
import org.egov.layout.web.model.property.PropertyCriteria;
import org.egov.layout.web.model.property.PropertyRequest;
import org.egov.layout.web.model.property.PropertyResponse;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CLUPropertyService {

	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private CLUConfiguration config;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	public StringBuilder getPropertyCreateURL() {
		return new StringBuilder().append(config.getPropertyHost()).append(config.getPropertyCreateEndpoint());
	}

	public StringBuilder getPropertyURL() {
		return new StringBuilder().append(config.getPropertyHost()).append(config.getPropertySearchEndpoint());
	}



	public void createProperty(CluRequest cluRequest, Object mdmsData) {
		Clu clu = cluRequest.getLayout();
		Property property = createPropertFromCLU(clu, mdmsData);

		PropertyRequest propertyRequest = PropertyRequest.builder().property(property)
				.requestInfo(cluRequest.getRequestInfo()).build();

		Object result = serviceRequestRepository.fetchResult(getPropertyCreateURL(), propertyRequest);
		List<Property> propertyList = getPropertyDetails(result);
		String propertyId = propertyList.isEmpty() ? null : propertyList.stream().findFirst().get().getPropertyId();
		if (propertyId == null) {
			throw new CustomException("PROPERTY CREATION ERROR", "Property services error");

		}
		Object object = cluRequest.getLayout().getNocDetails().getAdditionalDetails();
		if (object instanceof Map) {
			Map<String, Object> additionalDetails = (Map<String, Object>) object;
			additionalDetails.put("propertyuid", propertyId);
			cluRequest.getLayout().getNocDetails().setAdditionalDetails(additionalDetails);
		} else {
			Map<String, Object> additionalDetails = new HashMap<>();
			additionalDetails.put("propertyuid", propertyId);
			cluRequest.getLayout().getNocDetails().setAdditionalDetails(additionalDetails);
		}

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

	private Property createPropertFromCLU(Clu clu, Object mdmsData) {
		
		Map<String,Object> additionalDetails = (Map<String, Object>)clu.getNocDetails().getAdditionalDetails();
		Map<String, Object> siteDetails = (Map<String, Object>) additionalDetails.get("siteDetails");
		Map<String, String> coordinates = (Map<String, String>) additionalDetails.get("coordinates");
		String buildingStatus = JsonPath.read(siteDetails, "$.buildingStatus.code");
		String buildingCategory = JsonPath.read(siteDetails, "$.buildingCategory.code");
		
		List<String> propertyUsageList = JsonPath.read(mdmsData, "$.MdmsRes.CLU.BuildingCategory.[?(@.code == '" + buildingCategory + "')].propertyUsage");

		if(CollectionUtils.isEmpty(propertyUsageList))
			throw new CustomException("UPDATE ERROR", "Property Usage not found for the Building Category : " + buildingCategory);
		
		Address address = Address.builder()
				.tenantId(clu.getTenantId())
				.plotNo(siteDetails.getOrDefault("plotNo", "").toString())
				.district(siteDetails.getOrDefault("district", "").toString())
				.city(siteDetails.getOrDefault("ulbName", "").toString())
				.geoLocation(GeoLocation.builder()
						.latitude(coordinates.get("Latitude1") != null ? Double.valueOf(coordinates.get("Latitude1")) : null )
						.longitude(coordinates.get("Longitude1") != null ? Double.valueOf(coordinates.get("Longitude1")) : null ).build())
				.locality(Boundary.builder().code("ALOC5").build())
				.build();

		
		clu.getOwners().stream().forEach(owner -> {
			if(owner.getOwnerType() == null)
				owner.setOwnerType("NONE");
		});
		
		ObjectNode propertyAdditionalDetails = (ObjectNode)JsonNodeFactory.instance.objectNode();
		propertyAdditionalDetails.put("vasikaNo", clu.getVasikaNumber());
		propertyAdditionalDetails.put("vasikaDate", clu.getVasikaDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
		
		return Property.builder()
				.address(address).accountId(clu.getAccountId())
				.landArea(new BigDecimal(siteDetails.get("netTotalArea").toString())
						.multiply(BigDecimal.valueOf(1.19599)).setScale(2, RoundingMode.HALF_UP).doubleValue())
				.usageCategory(propertyUsageList.get(0))
				.ownershipCategory(clu.getOwners().size() == 1 ? "INDIVIDUAL.SINGLEOWNER" : "INDIVIDUAL.MULTIPLEOWNERS" )
				.owners(clu.getOwners())
				.tenantId(clu.getTenantId())
				.propertyType(buildingStatus)
				.additionalDetails(propertyAdditionalDetails)
				.build();
		
	}

}
