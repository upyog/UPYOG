package org.egov.gis.adapters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egov.gis.config.GisConfiguration;
import org.egov.gis.interfaces.MunicipalServiceAdapter;
import org.egov.gis.models.Entity;
import org.egov.gis.models.GisRequest;
import org.egov.gis.repository.ServiceRequestRepository;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

/**
 * Adapter for Property Service
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PropertyServiceAdapter implements MunicipalServiceAdapter {

    private static final String TENANT_ID = "tenantId";
    private static final String STATUS = "status";
    private static final String PROPERTY_TYPE = "propertyType";
    private static final String LOCALITY = "locality";
    private static final String OWNERSHIP_CATEGORY = "ownershipCategory";
    private static final String USAGE_CATEGORY = "usageCategory";
    private static final String PROPERTY_ID = "propertyId";
    private static final String POINT_GEOMETRY = "pointGeometry";

    private final ServiceRequestRepository serviceRequestRepository;
    private final GisConfiguration config;

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
                    .queryParam(TENANT_ID, request.getTenantId());

            applyQueryParameters(builder, request, "ACTIVE");

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("RequestInfo", request.getRequestInfo());

            return fetchEntitiesFromService(builder, requestBody, request.getTenantId());

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching property entities for tenant: {}", request.getTenantId(), e);
            throw new CustomException("PROPERTY_FETCH_FAILED", "Failed to fetch property entities: " + e.getMessage());
        }
    }

    private void applyQueryParameters(UriComponentsBuilder builder, GisRequest request, String defaultStatus) {
        if (request.getSearchCriteria() != null && !request.getSearchCriteria().isEmpty()) {
            for (Map.Entry<String, Object> entry : request.getSearchCriteria().entrySet()) {
                if (entry.getValue() != null && !TENANT_ID.equals(entry.getKey())) {
                    builder.queryParam(entry.getKey(), entry.getValue());
                }
            }
            return;
        }

        builder.queryParam(STATUS, defaultStatus);
        if (request.getFromDate() != null) {
            builder.queryParam("fromDate", request.getFromDate().toString());
        }
        if (request.getToDate() != null) {
            builder.queryParam("toDate", request.getToDate().toString());
        }
    }

    @SuppressWarnings("unchecked")
    private List<Entity> fetchEntitiesFromService(UriComponentsBuilder builder,
            Map<String, Object> requestBody, String tenantId) {
        StringBuilder uri = new StringBuilder(builder.toUriString());
        Optional<Object> responseOptional = serviceRequestRepository.fetchResult(uri, requestBody);

        if (responseOptional.isEmpty()) {
            log.warn("Empty response from Property Service for tenant: {}", tenantId);
            return new ArrayList<>();
        }

        Map<String, Object> response = (Map<String, Object>) responseOptional.get();
        List<Entity> entities = transformResponseToEntities(response);
        log.info("Successfully fetched {} property entities", entities.size());
        return entities;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> extractPointGeometry(Entity entity) {
        return (Map<String, Object>) entity.getAttributes().get(POINT_GEOMETRY);
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
        attributes.put(PROPERTY_TYPE, getStringValue(municipalResponse, PROPERTY_TYPE));
        attributes.put(LOCALITY, getStringValue(municipalResponse, LOCALITY));
        attributes.put(OWNERSHIP_CATEGORY, getStringValue(municipalResponse, OWNERSHIP_CATEGORY));
        attributes.put(USAGE_CATEGORY, getStringValue(municipalResponse, USAGE_CATEGORY));
        attributes.put("constructionType", getStringValue(municipalResponse, "constructionType"));
        attributes.put("landArea", getStringValue(municipalResponse, "landArea"));
        attributes.put("buildUpArea", getStringValue(municipalResponse, "buildUpArea"));
        
        extractGeometryData(municipalResponse, attributes);

        return Entity.builder()
                .id(getStringValue(municipalResponse, PROPERTY_ID))
                .applicationNumber(getStringValue(municipalResponse, PROPERTY_ID))
                .tenantId(getStringValue(municipalResponse, TENANT_ID))
                .status(getStringValue(municipalResponse, STATUS))
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
        mapping.put(PROPERTY_TYPE, PROPERTY_TYPE);
        mapping.put(LOCALITY, LOCALITY);
        mapping.put(STATUS, STATUS);
        mapping.put(OWNERSHIP_CATEGORY, OWNERSHIP_CATEGORY);
        mapping.put(USAGE_CATEGORY, USAGE_CATEGORY);
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
        String propertyId = getStringValue(municipalResponse, PROPERTY_ID);

        Map<String, Object> address = (Map<String, Object>) municipalResponse.get("address");
        if (address == null) {
            return;
        }

        Map<String, Object> geoLocation = (Map<String, Object>) address.get("geoLocation");
        if (geoLocation == null) {
            return;
        }

        Double lat = getDoubleValue(geoLocation, "latitude");
        Double lon = getDoubleValue(geoLocation, "longitude");
        if (lat == null || lon == null) {
            log.warn("Missing lat/long for property: {}", propertyId);
            return;
        }

        Map<String, Object> pointGeometry = new HashMap<>();
        pointGeometry.put("type", "Point");
        pointGeometry.put("coordinates", Arrays.asList(lon, lat));
        attributes.put(POINT_GEOMETRY, pointGeometry);
        log.debug("Extracted point geometry for property: {} at [{}, {}]", propertyId, lon, lat);
    }

    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    private Long getLongValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number number) {
            return number.longValue();
        }
        return null;
    }
    
    private Double getDoubleValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Double doubleValue) {
            return doubleValue;
        }
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            log.warn("Cannot convert {} to Double: {}", key, value);
            return null;
        }
    }
}
