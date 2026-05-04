package com.mseva.gisintegration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mseva.gisintegration.config.TenantStaticMapper;
import com.mseva.gisintegration.model.Property;
import com.mseva.gisintegration.repository.MdmsRepository;
import com.mseva.gisintegration.repository.PropertyRepository;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PropertyService {

    private static final Logger log = LoggerFactory.getLogger(PropertyService.class);

    @Autowired
    private PropertyRepository propertyRepository;
    
    @Autowired
    private MdmsRepository mdmsRepository;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private ObjectMapper mapper;
    


    @Value("${egov.propertyservice.host}")
    private String propertyHost;

    @Value("${egov.propertyservice.search.endpoint}")
    private String propertySearchEndpoint;

    
    /**
     * Searches local backup for a property by survey ID.
     */
    public List<Property> findBySurveyid(String surveyid, String tenantid) {
        log.info("Controller request: Searching for surveyid: {} in tenant: {}", surveyid, tenantid);
        return propertyRepository.findBySurveyid(surveyid, tenantid);
    }

    /**
     * Searches local backup for a property by property ID.
     */
    public List<Property> findByPropertyid(String propertyid, String tenantid) {
        log.info("Controller request: Searching for propertyid: {} in tenant: {}", propertyid, tenantid);
        return propertyRepository.findByPropertyid(propertyid, tenantid);
    }

    /**
     * Searches local backup for a property by tenant ID.
     */
    public List<Property> findByTenantid(String tenantid) {
        log.info("Controller request: Searching for tenantid: {}", tenantid);
        return propertyRepository.findByTenantid(tenantid);
    }

    /**
     * Searches local backup for a property by tenant ID and assessment year.
     */
    public List<Property> findByTenantidAndAssessmentyear(String tenantid, String assessmentyear) {
        log.info("Controller request: Searching for tenantid: {} and assessmentyear: {}", tenantid, assessmentyear);
        return propertyRepository.findByTenantidAndAssessmentyear(tenantid, assessmentyear);
    }
    /**
     * Entry point for Assessment Topic Consumer.
     */
    public void handleAssessmentSync(JsonNode assessment, RequestInfo requestInfo) {
        String propertyId = assessment.path("propertyId").asText();
        String tenantId = assessment.path("tenantId").asText();
        String financialYear = assessment.path("financialYear").asText();

        log.info("Syncing Assessment for Property: {} Year: {}", propertyId, financialYear);

        try {
            // Step 1: Search Property Service for full master data
            JsonNode propertyNode = fetchPropertyDataFromService(propertyId, tenantId, requestInfo);

            if (propertyNode != null) {
                Property p = new Property();
                
                // Step 2: Map GIS Master Data (Survey ID, Address, etc.)
                mapGisFields(p, propertyNode);
                
                // Step 3: Map Assessment-specific data
            	String townName = TenantStaticMapper.getTownName(tenantId);                
                if (townName != null) {
                    // CAUTION: You were setting match.setTenantid(townName). 
                    // Usually, you want to keep the ID as "pb.amritsar" and set a 
                    // separate field like setTownname(townName).
                    p.setTenantid(townName); 
                }
                p.setPropertyid(propertyId);
                p.setAssessmentyear(financialYear);
                
                // Since this is just an assessment, we set payment fields to empty/zero
                p.setAmoutpaid("0");
                p.setReceiptnumber("ASSESSMENT_DONE");

                // Step 4: Persist
                this.createOrUpdateProperty(p);
            }
        } catch (Exception e) {
            log.error("Error in handleAssessmentSync for propertyId: " + propertyId, e);
        }
    }

    /**
     * Entry point for Payment Consumer.
     */
    public void processPayment(JsonNode detail, JsonNode payment, RequestInfo requestInfo) {
        String propertyId = detail.path("bill").path("consumerCode").asText();
        String tenantId = detail.path("tenantId").asText();

        try {
            JsonNode propertyNode = fetchPropertyDataFromService(propertyId, tenantId, requestInfo);
            
            if (propertyNode != null) {
                Property p = new Property();
                mapGisFields(p, propertyNode);
                mapTransactionalFields(p, detail, payment);
                this.createOrUpdateProperty(p);
            }
        } catch (Exception e) {
            log.error("Error in processPayment for propertyId: " + propertyId, e);
        }
    }

    // --- HELPER METHODS ---

    private JsonNode fetchPropertyDataFromService(String propertyId, String tenantId, RequestInfo requestInfo) {
        StringBuilder url = new StringBuilder(propertyHost)
                .append(propertySearchEndpoint)
                .append("?tenantId=").append(tenantId)
                .append("&propertyIds=").append(propertyId);

        Map<String, Object> request = new HashMap<>();
        request.put("RequestInfo", requestInfo);

        Object response = serviceRequestRepository.fetchResult(url, request);
        JsonNode root = mapper.valueToTree(response);
        JsonNode properties = root.path("Properties");

        return (properties.isArray() && properties.size() > 0) ? properties.get(0) : null;
    }

    private void mapGisFields(Property p, JsonNode node) {
        p.setPropertyid(node.path("propertyId").asText());
        p.setSurveyid(node.path("surveyId").asText());
        p.setOldpropertyid(node.path("oldPropertyId").asText());
       
        p.setFirmbusinessname(
                node.path("additionalDetails")
                    .path("businessName")
                    .asText(null) // Returns null if businessName doesn't exist
        );


        p.setPropertytype(node.path("propertyType").asText());
        p.setPropertyusagetype(node.path("usageCategory").asText());
        p.setOwnershipcategory(node.path("ownershipCategory").asText());
        p.setPlotsize(node.path("landArea").asText());
        
        JsonNode addr = node.path("address");
        JsonNode loc = addr.path("locality");
        String tenantId = node.path("tenantId").asText();
        String localityCode = node.path("address").path("locality").path("code").asText();
        Map<String, String> boundary =
                mdmsRepository.getBoundaryByLocalityCode(localityCode, tenantId);        // Collect all parts in an array to join them cleanly
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

        // Filter out empties/nulls and join with commas
        String fullAddress = Arrays.stream(addressParts)
                .filter(s -> s != null && !s.trim().isEmpty() && !s.equalsIgnoreCase("null"))
                .collect(Collectors.joining(", "));

        p.setAddress(fullAddress);
        p.setLocalitycode(addr.path("locality").path("code").asText());
        if (boundary != null) {
            p.setZonename(boundary.getOrDefault("zonename", "UNKNOWN"));
            p.setBlockname(boundary.getOrDefault("blockname", "UNKNOWN"));
            p.setLocalityname(boundary.getOrDefault("localityname", "UNKNOWN"));
        } else {
            p.setZonename("UNKNOWN");
            p.setBlockname("UNKNOWN");
            p.setLocalityname("UNKNOWN");
        }
    }

    private void mapTransactionalFields(Property p, JsonNode detail, JsonNode payment) {
    	String townName = TenantStaticMapper.getTownName(detail.path("tenantId").asText());                
        if (townName != null) {
            // CAUTION: You were setting match.setTenantid(townName). 
            // Usually, you want to keep the ID as "pb.amritsar" and set a 
            // separate field like setTownname(townName).
            p.setTenantid(townName); 
        }
        p.setReceiptnumber(detail.path("receiptNumber").asText());
        p.setAmoutpaid(detail.path("totalAmountPaid").asText());
     // 1. Get the epoch as long
        long epoch = payment.path("transactionDate").asLong();

        // 2. Convert and Format to IST
        if (epoch > 0) {
            String readableDate = java.time.Instant.ofEpochMilli(epoch)
                    .atZone(java.time.ZoneId.of("Asia/Kolkata"))
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                    
            p.setPaymentdate(readableDate);
        }
        JsonNode billDetails = detail.path("bill").path("billDetails");
        if (billDetails.isArray() && billDetails.size() > 0) {
            long fromPeriod = billDetails.get(0).path("fromPeriod").asLong();
            int year = Instant.ofEpochMilli(fromPeriod).atZone(ZoneId.systemDefault()).toLocalDate().getYear();
            p.setAssessmentyear(year + "-" + String.valueOf(year + 1).substring(2));
        }
    }

    // --- REPOSITORY LOGIC ---

    public Map<String, Object> createOrUpdateProperty(Property property) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> responseInfo = new HashMap<>();
        responseInfo.put("status", "successful");

        List<Property> existing = propertyRepository.findByPropertyid(property.getPropertyid(), property.getTenantid());

        Property match = null;
        if (existing != null) {
            match = existing.stream()
                    .filter(e -> property.getAssessmentyear() != null && property.getAssessmentyear().equals(e.getAssessmentyear()))
                    .findFirst().orElse(null);
        }

        if (match != null) {
            updateFields(match, property);

            // 1. Safe Boundary Lookup
            if (match.getLocalitycode() != null && match.getTenantid() != null) {
            	Map<String, String> boundary =
            		    mdmsRepository.getBoundaryByLocalityCode(match.getLocalitycode(), match.getTenantid());      if (boundary != null) {
                    // Use .getOrDefault or check for null before casting
                    Object zone = boundary.get("zonename");
                    if (zone != null) {
                        match.setZonename(zone.toString());
                    }
                    
                    Object block = boundary.get("blockname");
                    if (block != null) {
                        match.setBlockname(block.toString());
                    }
                }
            }
            
            // 2. Safe Town Name Lookup
            if (match.getTenantid() != null) {
            	String townName = TenantStaticMapper.getTownName(match.getTenantid());                
                if (townName != null) {
                    // CAUTION: You were setting match.setTenantid(townName). 
                    // Usually, you want to keep the ID as "pb.amritsar" and set a 
                    // separate field like setTownname(townName).
                    match.setTenantid(townName); 
                }
            }

            response.put("Property", propertyRepository.save(match));
            responseInfo.put("method", "update");
        }else {
        	
        	 if (property.getLocalitycode() != null && property.getTenantid() != null) {
        		 Map<String, String> boundary =
        			        mdmsRepository.getBoundaryByLocalityCode(property.getLocalitycode(),  property.getTenantid() );                 
                 if (boundary != null) {
                     // Use .getOrDefault or check for null before casting
                     Object zone = boundary.getOrDefault("zonename", "UNKNOWN");
                     if (zone != null) {
                    	 property.setZonename(zone.toString());
                     }
                     
                     Object block =boundary.getOrDefault("blockname", "UNKNOWN");
                     if (block != null) {
                    	 property.setBlockname(block.toString());
                     }
                 }
                 	else {
                 		property.setZonename("UNKNOWN");
                 		property.setBlockname("UNKNOWN");
                 		property.setLocalityname("UNKNOWN");
                	}
             }

             // 2. Safe Town Name Lookup
             if (property.getTenantid() != null) {
            	 String townName = TenantStaticMapper.getTownName(property.getTenantid());                 
                 if (townName != null) {
                     // CAUTION: You were setting match.setTenantid(townName). 
                     // Usually, you want to keep the ID as "pb.amritsar" and set a 
                     // separate field like setTownname(townName).
                	 property.setTenantid(townName); 
                 }
             }
            response.put("Property", propertyRepository.save(property));
            responseInfo.put("method", "create");
        }
        
        response.put("ResponseInfo", responseInfo);
        return response;
    }

    private void updateFields(Property existing, Property source) {
        if (source.getTenantid() != null) existing.setTenantid(source.getTenantid());
        if (source.getSurveyid() != null) existing.setSurveyid(source.getSurveyid());
        if (source.getAddress() != null) existing.setAddress(source.getAddress());
        if (source.getPlotsize() != null) existing.setPlotsize(source.getPlotsize());
        
        // Only update payment fields if they aren't default "0" or "PENDING"
        if (source.getAmoutpaid() != null && !source.getAmoutpaid().equals("0")) {
            existing.setAmoutpaid(source.getAmoutpaid());
        }
        if (source.getReceiptnumber() != null && !source.getReceiptnumber().equals("ASSESSMENT_DONE")) {
            existing.setReceiptnumber(source.getReceiptnumber());
        }
        if (source.getPaymentdate() != null) existing.setPaymentdate(source.getPaymentdate());
    }
    
    
    /**
     * Maps data directly from the update-property-registry topic.
     * firmbusinessname is now mapped to buildingName.
     */
    public void syncPropertyMaster(JsonNode propertyNode, RequestInfo requestInfo) {
        try {
            Property p = new Property();
            try {
                log.info("Full Property Object: {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(propertyNode));
            } catch (Exception e) {
                log.error("Could not print property object", e);
            }
            // Step 1: Map GIS and Master Fields
            p.setPropertyid(propertyNode.path("propertyId").asText());
            p.setSurveyid(propertyNode.path("surveyId").asText());
            p.setOldpropertyid(propertyNode.path("oldPropertyId").asText());           
            // MAPPING CHANGE: buildingName -> firmbusinessname
			p.setFirmbusinessname(propertyNode.path("additionalDetails").path("businessName").asText(null));
			// Legacy fallback to buildingName if businessName is not present
           // p.setFirmbusinessname(propertyNode.path("address").path("buildingName").asText());

            p.setPropertytype(propertyNode.path("propertyType").asText());
            p.setOwnershipcategory(propertyNode.path("ownershipCategory").asText());
            p.setPropertyusagetype(propertyNode.path("usageCategory").asText());
            p.setPlotsize(String.valueOf(propertyNode.path("landArea").asDouble()));
            
            // Step 2: Map Address & Locality details
            JsonNode addr = propertyNode.path("address");
            p.setLocalitycode(addr.path("locality").path("code").asText());
            p.setLocalityname(addr.path("locality").path("name").asText());
            p.setBlockname(addr.path("locality").path("name").asText());
            p.setZonename(addr.path("city").asText());
            
            JsonNode loc = addr.path("locality");

            // Collect all parts in an array to join them cleanly
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

            // Filter out empties/nulls and join with commas
            String fullAddress = Arrays.stream(addressParts)
                    .filter(s -> s != null && !s.trim().isEmpty() && !s.equalsIgnoreCase("null"))
                    .collect(Collectors.joining(", "));
            p.setTenantid(propertyNode.path("tenantId").asText());
            p.setAddress(fullAddress);
            // Defaulting assessment year for registry updates
            p.setAssessmentyear("N/A");
            // Step 3: Persist to DB (Create/Update logic)
            this.createOrUpdateProperty(p);
            log.info("Successfully synced Property Registry for ID: {}", p.getPropertyid());
            
        } catch (Exception e) {
            log.error("Error mapping Property Master from Registry topic", e);
        }
    }
}