package com.mseva.gisintegration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mseva.gisintegration.config.TenantStaticMapper;
import com.mseva.gisintegration.model.WaterTax;
import com.mseva.gisintegration.repository.WaterTaxRepository;
import com.mseva.gisintegration.repository.ServiceRequestRepository;
import com.mseva.gisintegration.repository.TenantMappingRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.egov.common.contract.request.RequestInfo; // Now this will resolve
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Service
public class WaterTaxService {

    private static final Logger logger = LoggerFactory.getLogger(WaterTaxService.class);

    @Autowired
    private WaterTaxRepository waterTaxRepository;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;
    
    @Autowired
    private TenantMappingRepository tenantMappingRepository;

    @Autowired
    private ObjectMapper mapper;

    @Value("${egov.waterservice.host}")
    private String waterHost;

    @Value("${egov.waterservice.search.endpoint}")
    private String waterSearchEndpoint;

    @Value("${egov.propertyservice.host}")
    private String propertyHost;

    @Value("${egov.propertyservice.search.endpoint}")
    private String propertySearchEndpoint;

    /**
     * Entry point for Consumer: Maps A-Z data using external API calls.
     */
    public void processPayment(JsonNode detail, JsonNode payment, RequestInfo requestInfo ) {
        WaterTax waterTax = new WaterTax();
        String connectionNo = detail.path("bill").path("consumerCode").asText();
        String tenantId = detail.path("tenantId").asText();
        
        // Extract RequestInfo from the incoming payload for authentication

        try {
            // Step 1: Call Water Service to get propertyid using Connection Number
            String propertyId = fetchPropertyIdFromWaterService(connectionNo, tenantId, requestInfo);
            
            if (propertyId != null && !propertyId.isEmpty()) {
                waterTax.setPropertyid(propertyId);
                
                // Step 2: Call Property Service to get GIS Master Data using Property ID
                JsonNode propertyNode = fetchPropertyData(propertyId, tenantId, requestInfo);
                if (propertyNode != null) {
                    mapGisFields(waterTax, propertyNode);
                }
            }

            // Step 3: Map Transactional Data from the Kafka message
            mapTransactionalFields(waterTax, detail);
            
            // Step 4: Persist to database
            this.createOrUpdateWaterTax(waterTax);

        } catch (Exception e) {
            logger.error("Error enriching WaterTax for connection: " + connectionNo, e);
        }
    }

    private String fetchPropertyIdFromWaterService(String connectionNo, String tenantId, RequestInfo requestInfo) {
        StringBuilder url = new StringBuilder(waterHost)
                .append(waterSearchEndpoint)
                .append("?searchType=CONNECTION&connectionNumber=")
                .append(connectionNo)
                .append("&tenantId=").append(tenantId);

        Map<String, Object> request = new HashMap<>();
        request.put("RequestInfo", requestInfo);

        // Making POST call to WS-Services
        JsonNode response = mapper.valueToTree(serviceRequestRepository.fetchResult(url, request));
        JsonNode connections = response.path("WaterConnection");
        
        return (connections.isArray() && connections.size() > 0) 
                ? connections.get(0).path("propertyId").asText() : null;
    }

    private JsonNode fetchPropertyData(String propertyId, String tenantId, RequestInfo requestInfo) {
        StringBuilder url = new StringBuilder(propertyHost)
                .append(propertySearchEndpoint)
                .append("?tenantId=").append(tenantId)
                .append("&propertyIds=").append(propertyId);

        Map<String, Object> request = new HashMap<>();
        request.put("RequestInfo", requestInfo);

        // Making POST call to Property-Services
        JsonNode response = mapper.valueToTree(serviceRequestRepository.fetchResult(url, request));
        JsonNode properties = response.path("Properties");

        return (properties.isArray() && properties.size() > 0) ? properties.get(0) : null;
    }

    private void mapGisFields(WaterTax w, JsonNode p) {
        // Mapping strictly based on the field list provided
        w.setSurveyid(p.path("surveyId").asText());
        w.setProperty_id(p.path("propertyId").asText());
        w.setOldpropertyid(p.path("oldPropertyId").asText());
        w.setPropertytype(p.path("propertyType").asText());
        w.setOwnershipcategory(p.path("ownershipCategory").asText());
        w.setPropertyusagetype(p.path("usageCategory").asText());
        w.setNooffloors(p.path("noOfFloors").asText());
        w.setPlotsize(p.path("landArea").asText());
        w.setSuperbuilduparea(p.path("superBuiltUpArea").asText());
        
        JsonNode addr = p.path("address");
        JsonNode loc = addr.path("locality");

        // Collect all potential address components from the JSON
        String[] addressParts = {
            addr.path("doorNo").asText(""),
            addr.path("plotNo").asText(""),
            addr.path("buildingName").asText(""),
            addr.path("street").asText(""),
            addr.path("landmark").asText(""),
            loc.path("name").asText(""), // Locality Name
            addr.path("city").asText(""),
            addr.path("pincode").asText("")
        };

        // Filter out empty/null/whitespace strings and join with clean commas
        String fullAddress = java.util.Arrays.stream(addressParts)
                .filter(part -> part != null && !part.trim().isEmpty() && !part.equalsIgnoreCase("null"))
                .collect(java.util.stream.Collectors.joining(", "));
        
        w.setAddress(fullAddress);
        w.setLocalityname(addr.path("locality").path("name").asText());
        w.setBlockname(addr.path("locality").path("name").asText());
    }

    private void mapTransactionalFields(WaterTax w, JsonNode detail) {
        //w.setTenantid(detail.path("tenantId").asText());
    	String townName = TenantStaticMapper.getTownName(w.getTenantid());        
        w.setTenantid(townName);
        w.setConnectionno(detail.path("bill").path("consumerCode").asText());
        
        JsonNode billDetails = detail.path("bill").path("billDetails");
        if (billDetails.isArray() && billDetails.size() > 0) {
            long fromPeriod = billDetails.get(0).path("fromPeriod").asLong();
            int year = Instant.ofEpochMilli(fromPeriod).atZone(ZoneId.systemDefault()).toLocalDate().getYear();
            w.setAssessmentyear(year + "-" + String.valueOf(year + 1).substring(2));
        }
    }

    // --- RETAINED METHODS ---

    public WaterTax insertWaterTax(WaterTax waterTax) {
        return waterTaxRepository.save(waterTax);
    }

    public List<WaterTax> findByConnectionno(String connectionno) {
        return waterTaxRepository.findByConnectionno(connectionno);
    }

    public List<WaterTax> findByTenantid(String tenantid) {
        return waterTaxRepository.findByTenantid(tenantid);
    }

    public List<WaterTax> findByTenantidAndAssessmentyear(String tenantid, String assessmentyear) {
        return waterTaxRepository.findByTenantidAndAssessmentyear(tenantid, assessmentyear);
    }

    public Map<String, Object> createOrUpdateWaterTax(WaterTax waterTax) {
        Map<String, Object> response = new HashMap<>();
        List<WaterTax> existing = findByConnectionno(waterTax.getConnectionno());
        
        WaterTax match = existing.stream()
                .filter(t -> waterTax.getAssessmentyear() != null && waterTax.getAssessmentyear().equals(t.getAssessmentyear()))
                .findFirst().orElse(null);

        if (match == null) {
            response.put("WaterTax", insertWaterTax(waterTax));
        } else {
            updateFields(match, waterTax);
            response.put("WaterTax", waterTaxRepository.save(match));
        }
        return response;
    }

    private void updateFields(WaterTax existing, WaterTax source) {
        if (source.getTenantid() != null) existing.setTenantid(source.getTenantid());
        if (source.getSurveyid() != null) existing.setSurveyid(source.getSurveyid());
        if (source.getAddress() != null) existing.setAddress(source.getAddress());
        if (source.getAssessmentyear() != null) existing.setAssessmentyear(source.getAssessmentyear());
        if (source.getPropertyid() != null) existing.setPropertyid(source.getPropertyid());
        if (source.getPlotsize() != null) existing.setPlotsize(source.getPlotsize());
    }
}