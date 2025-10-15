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
 * Adapter for Asset Service
 */
@Slf4j
@Component
public class AssetServiceAdapter implements MunicipalServiceAdapter {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private GisConfiguration config;

    @Override
    public String getBusinessService() {
        return "ASSET";
    }

    @Override
    public List<Entity> fetchEntities(GisRequest request) {
        log.info("Fetching asset entities for tenant: {}, fromDate: {}, toDate: {}", 
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
                builder.queryParam("status", "APPROVED");
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
                log.info("Successfully fetched {} asset entities", entities.size());
                return entities;
            }

            log.warn("Empty response from Asset Service for tenant: {}", request.getTenantId());
            return new ArrayList<>();

        } catch (Exception e) {
            log.error("Error fetching asset entities for tenant: {}", request.getTenantId(), e);
            throw new RuntimeException("Failed to fetch asset entities", e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Entity> transformResponseToEntities(Map<String, Object> response) {
        if (response == null || !response.containsKey("Assets")) {
            log.warn("Response is null");
            return new ArrayList<>();
        }

        List<Map<String, Object>> assets = (List<Map<String, Object>>) response.get("Assets");
        List<Entity> entities = new ArrayList<>();
        
        for (Map<String, Object> asset : assets) {
            entities.add(transformToGenericEntity(asset));
            }

        return entities;
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
        return config.getAssetHost() + config.getAssetSearchEndpoint();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Entity transformToGenericEntity(Map<String, Object> municipalResponse) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("assetName", getStringValue(municipalResponse, "assetName"));
        attributes.put("assetCategory", getStringValue(municipalResponse, "assetCategory"));
        attributes.put("assetSubCategory", getStringValue(municipalResponse, "assetSubCategory"));
        attributes.put("assetParentCategory", getStringValue(municipalResponse, "assetParentCategory"));
        attributes.put("assetClassification", getStringValue(municipalResponse, "assetClassification"));
        attributes.put("department", getStringValue(municipalResponse, "department"));
        attributes.put("purchaseCost", municipalResponse.get("purchaseCost"));
        attributes.put("acquisitionCost", municipalResponse.get("acquisitionCost"));
        attributes.put("bookValue", municipalResponse.get("bookValue"));
        attributes.put("assetBookRefNo", getStringValue(municipalResponse, "assetBookRefNo"));
        attributes.put("assetUsage", getStringValue(municipalResponse, "assetUsage"));

        extractGeometryData(municipalResponse, attributes);

        return Entity.builder()
                .id(getStringValue(municipalResponse, "id"))
                .applicationNumber(getStringValue(municipalResponse, "applicationNo"))
                .tenantId(getStringValue(municipalResponse, "tenantId"))
                .status(getStringValue(municipalResponse, "status"))
                .businessService("ASSET")
                .createdTime(getLongValue(municipalResponse, "createdTime"))
                .lastModifiedTime(getLongValue(municipalResponse, "lastModifiedTime"))
                .createdBy(getStringValue(municipalResponse, "createdBy"))
                .lastModifiedBy(getStringValue(municipalResponse, "lastModifiedBy"))
                .additionalDetails((Map<String, Object>) municipalResponse.get("additionalDetails"))
                .address((Map<String, Object>) municipalResponse.get("addressDetails"))
                .attributes(attributes)
                .build();
    }

    @Override
    public Map<String, String> getSearchCriteriaMapping() {
        Map<String, String> mapping = new HashMap<>();
        mapping.put("assetCategory", "assetCategory");
        mapping.put("assetParentCategory", "assetParentCategory");
        mapping.put("department", "department");
        mapping.put("status", "status");
        return mapping;
    }

    /**
     * Extracts point and polygon geometry from asset response
     * Point geometry: location field or addressDetails.{latitude, longitude}
     * Polygon geometry: additionalDetails.geometry.geometry
     */
    @SuppressWarnings("unchecked")
    private void extractGeometryData(Map<String, Object> municipalResponse, Map<String, Object> attributes) {
        String assetId = getStringValue(municipalResponse, "applicationNo");
        
        Map<String, Object> additionalDetails = (Map<String, Object>) municipalResponse.get("additionalDetails");
        if (additionalDetails != null && additionalDetails.containsKey("geometry")) {
            Map<String, Object> geometryWrapper = (Map<String, Object>) additionalDetails.get("geometry");
            if (geometryWrapper != null && geometryWrapper.containsKey("geometry")) {
                Map<String, Object> geometry = (Map<String, Object>) geometryWrapper.get("geometry");
                if (geometry != null && "Polygon".equals(geometry.get("type"))) {
                    attributes.put("polygonGeometry", geometry);
                    log.debug("Extracted polygon geometry for asset: {}", assetId);
                }
            }
        }

        Map<String, Object> addressDetails = (Map<String, Object>) municipalResponse.get("addressDetails");
        if (addressDetails != null) {
            Double lat = getDoubleValue(addressDetails, "latitude");
            Double lon = getDoubleValue(addressDetails, "longitude");
            
            if (lat != null && lon != null && lat != 0.0 && lon != 0.0) {
                Map<String, Object> pointGeometry = new HashMap<>();
                pointGeometry.put("type", "Point");
                pointGeometry.put("coordinates", Arrays.asList(lon, lat));
                attributes.put("pointGeometry", pointGeometry);
                log.debug("Extracted point geometry for asset: {} at [{}, {}]", assetId, lon, lat);
                return;
            }
        }
        
        String location = getStringValue(municipalResponse, "location");
        if (location != null && location.contains(",")) {
            try {
                String[] parts = location.split(",");
                if (parts.length == 2) {
                    Double lat = Double.parseDouble(parts[0].trim());
                    Double lon = Double.parseDouble(parts[1].trim());
                    
                    if (lat != 0.0 && lon != 0.0) {
                        Map<String, Object> pointGeometry = new HashMap<>();
                        pointGeometry.put("type", "Point");
                        pointGeometry.put("coordinates", Arrays.asList(lon, lat));
                        attributes.put("pointGeometry", pointGeometry);
                        log.debug("Extracted point geometry from location for asset: {} at [{}, {}]", assetId, lon, lat);
                    }
                }
            } catch (NumberFormatException e) {
                log.warn("Invalid location format for asset: {}, location: {}", assetId, location);
            }
        }
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
}
