package org.egov.gis.adapters;

import lombok.extern.slf4j.Slf4j;
import org.egov.gis.config.GisConfiguration;
import org.egov.gis.interfaces.MunicipalServiceAdapter;
import org.egov.gis.models.Entity;
import org.egov.gis.models.GisRequest;
import org.egov.gis.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

/**
 * Adapter for Property Service
 */
@Slf4j
@Component
public class PropertyServiceAdapter implements MunicipalServiceAdapter {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private GisConfiguration config;

    @Override
    public String getBusinessService() {
        return "PT";
    }

    /**
     * Fetches property entities from Property Service
     * @param request GIS request containing tenant, search criteria, and date range
     * @return List of normalized Entity objects with geometry data
     */
    @Override
    public List<Entity> fetchEntities(GisRequest request) {
        log.info("Fetching property entities for tenant: {}, fromDate: {}, toDate: {}", 
                request.getTenantId(), request.getFromDate(), request.getToDate());

        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getServiceEndpoint())
                    .queryParam("tenantId", request.getTenantId());

            if (request.getSearchCriteria() != null && !request.getSearchCriteria().isEmpty()) {
                for (Map.Entry<String, Object> entry : request.getSearchCriteria().entrySet()) {
                    if (entry.getValue() != null && !"tenantId".equals(entry.getKey())) {
                        builder.queryParam(entry.getKey(), entry.getValue());
                    }
                }
            } else {
                builder.queryParam("status", "ACTIVE");
                if (request.getFromDate() != null) {
                    builder.queryParam("fromDate", request.getFromDate().toString());
                }
                if (request.getToDate() != null) {
                    builder.queryParam("toDate", request.getToDate().toString());
                }
            }

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("RequestInfo", request.getRequestInfo());

            StringBuilder uri = new StringBuilder(builder.toUriString());
            Optional<Object> responseOptional = serviceRequestRepository.fetchResult(uri, requestBody);

            if (responseOptional.isPresent()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> response = (Map<String, Object>) responseOptional.get();
                List<Entity> entities = transformResponseToEntities(response);
                log.info("Successfully fetched {} property entities", entities.size());
                return entities;
            }

            log.warn("Empty response from Property Service for tenant: {}", request.getTenantId());
            return new ArrayList<>();

        } catch (Exception e) {
            log.error("Error fetching property entities for tenant: {}", request.getTenantId(), e);
            throw new RuntimeException("Failed to fetch property entities", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> extractPointGeometry(Entity entity) {
        return (Map<String, Object>) entity.getAttributes().get("pointGeometry");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> extractPolygonGeometry(Entity entity) {
        return (Map<String, Object>) entity.getAttributes().get("polygonGeometry");
    }

    @Override
    public String getServiceEndpoint() {
        return config.getPropertyHost() + config.getPropertySearchEndpoint();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Entity transformToGenericEntity(Map<String, Object> municipalResponse) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("propertyType", getStringValue(municipalResponse, "propertyType"));
        attributes.put("locality", getStringValue(municipalResponse, "locality"));
        attributes.put("ownershipCategory", getStringValue(municipalResponse, "ownershipCategory"));
        attributes.put("usageCategory", getStringValue(municipalResponse, "usageCategory"));
        attributes.put("constructionType", getStringValue(municipalResponse, "constructionType"));
        attributes.put("landArea", getStringValue(municipalResponse, "landArea"));
        attributes.put("buildUpArea", getStringValue(municipalResponse, "buildUpArea"));
        
        extractGeometryData(municipalResponse, attributes);

        return Entity.builder()
                .id(getStringValue(municipalResponse, "propertyId"))
                .applicationNumber(getStringValue(municipalResponse, "propertyId"))
                .tenantId(getStringValue(municipalResponse, "tenantId"))
                .status(getStringValue(municipalResponse, "status"))
                .businessService("PT")
                .createdTime(getLongValue(municipalResponse, "createdTime"))
                .lastModifiedTime(getLongValue(municipalResponse, "lastModifiedTime"))
                .createdBy(getStringValue(municipalResponse, "createdBy"))
                .lastModifiedBy(getStringValue(municipalResponse, "lastModifiedBy"))
                .additionalDetails((Map<String, Object>) municipalResponse.get("additionalDetails"))
                .address((Map<String, Object>) municipalResponse.get("address"))
                .attributes(attributes)
                .build();
    }

    @Override
    public Map<String, String> getSearchCriteriaMapping() {
        Map<String, String> mapping = new HashMap<>();
        mapping.put("propertyType", "propertyType");
        mapping.put("locality", "locality");
        mapping.put("status", "status");
        mapping.put("ownershipCategory", "ownershipCategory");
        mapping.put("usageCategory", "usageCategory");
        return mapping;
    }

    @SuppressWarnings("unchecked")
    private List<Entity> transformResponseToEntities(Map<String, Object> response) {
        if (response == null || !response.containsKey("Properties")) {
            return new ArrayList<>();
        }

        List<Map<String, Object>> properties = (List<Map<String, Object>>) response.get("Properties");
        List<Entity> entities = new ArrayList<>();
        for (Map<String, Object> property : properties) {
            entities.add(transformToGenericEntity(property));
        }

        return entities;
    }

    /**
     * Extracts point and polygon geometry from property response
     * Point geometry: address.geoLocation.{latitude, longitude}
     * Polygon geometry: Not implemented (dummy)
     */
    @SuppressWarnings("unchecked")
    private void extractGeometryData(Map<String, Object> municipalResponse, Map<String, Object> attributes) {
        String propertyId = getStringValue(municipalResponse, "propertyId");

        Map<String, Object> address = (Map<String, Object>) municipalResponse.get("address");
        if (address != null) {
            Map<String, Object> geoLocation = (Map<String, Object>) address.get("geoLocation");
            if (geoLocation != null) {
                Double lat = getDoubleValue(geoLocation, "latitude");
                Double lon = getDoubleValue(geoLocation, "longitude");
                if (lat != null && lon != null) {
                    Map<String, Object> pointGeometry = new HashMap<>();
                    pointGeometry.put("type", "Point");
                    pointGeometry.put("coordinates", Arrays.asList(lon, lat));
                    attributes.put("pointGeometry", pointGeometry);
                    log.debug("Extracted point geometry for property: {} at [{}, {}]", propertyId, lon, lat);
                } else {
                    log.warn("Missing lat/long for property: {}", propertyId);
                }
            }
        }
    }

    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    private Long getLongValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
    }
    
    private Double getDoubleValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Double) {
            return (Double) value;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            log.warn("Cannot convert {} to Double: {}", key, value);
            return null;
        }
    }
}

