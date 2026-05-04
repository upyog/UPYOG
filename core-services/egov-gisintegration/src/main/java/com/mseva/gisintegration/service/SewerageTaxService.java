package com.mseva.gisintegration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mseva.gisintegration.config.TenantStaticMapper;
import com.mseva.gisintegration.model.SewerageTax;
import com.mseva.gisintegration.repository.SewerageTaxRepository;
import com.mseva.gisintegration.repository.TenantMappingRepository;
import com.mseva.gisintegration.repository.ServiceRequestRepository;
import org.egov.common.contract.request.RequestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Service
public class SewerageTaxService {

    private static final Logger logger = LoggerFactory.getLogger(SewerageTaxService.class);

    @Autowired
    private SewerageTaxRepository sewerageTaxRepository;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;
    


    @Autowired
    private ObjectMapper mapper;

    @Value("${egov.sewerageservice.host}")
    private String sewerageHost;

    @Value("${egov.sewerageservice.search.endpoint}")
    private String sewerageSearchEndpoint;

    @Value("${egov.propertyservice.host}")
    private String propertyHost;

    @Value("${egov.propertyservice.search.endpoint}")
    private String propertySearchEndpoint;

    /**
     * Entry point for Consumer: Maps A-Z data using external API calls for Sewerage.
     */
    public void processPayment(JsonNode detail, JsonNode payment, RequestInfo requestInfo) {
        SewerageTax sewerageTax = new SewerageTax();
        String connectionNo = detail.path("bill").path("consumerCode").asText();
        String tenantId = detail.path("tenantId").asText();

        try {
            // Step 1: Call Sewerage Service to get propertyId using Connection Number
            String propertyId = fetchPropertyIdFromSewerageService(connectionNo, tenantId, requestInfo);
            
            if (propertyId != null && !propertyId.isEmpty()) {
                sewerageTax.setPropertyid(propertyId);
                
                // Step 2: Call Property Service to get GIS Master Data using Property ID
                JsonNode propertyNode = fetchPropertyData(propertyId, tenantId, requestInfo);
                if (propertyNode != null) {
                    mapGisFields(sewerageTax, propertyNode);
                }
            }

            // Step 3: Map Transactional Data from the Kafka message
            mapTransactionalFields(sewerageTax, detail);
            
            // Step 4: Persist to database
            this.createOrUpdateSewerageTax(sewerageTax);

        } catch (Exception e) {
            logger.error("Error enriching SewerageTax for connection: " + connectionNo, e);
        }
    }

    private String fetchPropertyIdFromSewerageService(String connectionNo, String tenantId, RequestInfo requestInfo) {
        StringBuilder url = new StringBuilder(sewerageHost)
                .append(sewerageSearchEndpoint)
                .append("?searchType=CONNECTION&connectionNumber=")
                .append(connectionNo)
                .append("&tenantId=").append(tenantId);

        Map<String, Object> request = new HashMap<>();
        request.put("RequestInfo", requestInfo);

        // Making POST call to SW-Services
        JsonNode response = mapper.valueToTree(serviceRequestRepository.fetchResult(url, request));
        JsonNode connections = response.path("SewerageConnections");
        
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

    private void mapGisFields(SewerageTax s, JsonNode p) {
        s.setSurveyid(p.path("surveyId").asText());
        s.setProperty_id(p.path("propertyId").asText());
        s.setOldpropertyid(p.path("oldPropertyId").asText());
        s.setPropertytype(p.path("propertyType").asText());
        s.setOwnershipcategory(p.path("ownershipCategory").asText());
        s.setPropertyusagetype(p.path("usageCategory").asText());
        s.setNooffloors(p.path("noOfFloors").asText());
        s.setPlotsize(p.path("landArea").asText());
        s.setSuperbuilduparea(p.path("superBuiltUpArea").asText());
        
        JsonNode addr = p.path("address");
        JsonNode loc = addr.path("locality");

        // Collecting every available bit of information from the address node
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

        // Filter out empty strings or literal "null" strings, then join with commas
        String fullAddress = java.util.Arrays.stream(addressParts)
                .filter(part -> part != null && !part.trim().isEmpty() && !part.equalsIgnoreCase("null"))
                .collect(java.util.stream.Collectors.joining(", "));
        
        s.setAddress(fullAddress);
        s.setLocalityname(addr.path("locality").path("name").asText());
        s.setBlockname(addr.path("locality").path("name").asText());
    }

    private void mapTransactionalFields(SewerageTax s, JsonNode detail) {
    	String tenantId = detail.path("tenantId").asText();
    	s.setTenantid(tenantId);   // always set first

    	String townName = TenantStaticMapper.getTownName(tenantId);
    	// optional: s.setTownname(townName);    	
    	s.setTenantid(townName);
        s.setConnectionno(detail.path("bill").path("consumerCode").asText());
        
        JsonNode billDetails = detail.path("bill").path("billDetails");
        if (billDetails.isArray() && billDetails.size() > 0) {
            long fromPeriod = billDetails.get(0).path("fromPeriod").asLong();
            int year = Instant.ofEpochMilli(fromPeriod).atZone(ZoneId.systemDefault()).toLocalDate().getYear();
            s.setAssessmentyear(year + "-" + String.valueOf(year + 1).substring(2));
        }
    }

    // --- RETAINED METHODS ---

    public SewerageTax insertSewerageTax(SewerageTax sewerageTax) {
        return sewerageTaxRepository.save(sewerageTax);
    }

    public List<SewerageTax> findByConnectionno(String connectionno) {
        return sewerageTaxRepository.findByConnectionno(connectionno);
    }

    public List<SewerageTax> findByTenantid(String tenantid) {
        return sewerageTaxRepository.findByTenantid(tenantid);
    }

    public List<SewerageTax> findByTenantidAndAssessmentyear(String tenantid, String assessmentyear) {
        return sewerageTaxRepository.findByTenantidAndAssessmentyear(tenantid, assessmentyear);
    }

    public Map<String, Object> createOrUpdateSewerageTax(SewerageTax sewerageTax) {
        Map<String, Object> response = new HashMap<>();
        List<SewerageTax> existing = findByConnectionno(sewerageTax.getConnectionno());
        
        SewerageTax match = existing.stream()
                .filter(t -> sewerageTax.getAssessmentyear() != null && sewerageTax.getAssessmentyear().equals(t.getAssessmentyear()))
                .findFirst().orElse(null);

        if (match == null) {
            response.put("SewerageTax", insertSewerageTax(sewerageTax));
        } else {
            updateFields(match, sewerageTax);
            response.put("SewerageTax", sewerageTaxRepository.save(match));
        }
        return response;
    }

    private void updateFields(SewerageTax existing, SewerageTax source) {
        if (source.getTenantid() != null) existing.setTenantid(source.getTenantid());
        if (source.getSurveyid() != null) existing.setSurveyid(source.getSurveyid());
        if (source.getAddress() != null) existing.setAddress(source.getAddress());
        if (source.getAssessmentyear() != null) existing.setAssessmentyear(source.getAssessmentyear());
        if (source.getPropertyid() != null) existing.setPropertyid(source.getPropertyid());
        if (source.getPlotsize() != null) existing.setPlotsize(source.getPlotsize());
    }
}