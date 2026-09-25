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
 * Adapter for Asset Service
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AssetServiceAdapter implements MunicipalServiceAdapter {

    private static final String TENANT_ID = "tenantId";
    private static final String STATUS = "status";
    private static final String POINT_GEOMETRY = "pointGeometry";
    private static final String ASSET_CATEGORY = "assetCategory";
    private static final String ASSET_PARENT_CATEGORY = "assetParentCategory";
    private static final String DEPARTMENT = "department";
    private static final String GEOMETRY = "geometry";

    private final ServiceRequestRepository serviceRequestRepository;
    private final GisConfiguration config;

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
                    .queryParam(TENANT_ID, request.getTenantId())
                    .queryParam("isInterServiceCall", true);

            applyQueryParameters(builder, request, "APPROVED");

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("RequestInfo", request.getRequestInfo());

            return fetchEntitiesFromService(builder, requestBody, request.getTenantId());

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching asset entities for tenant: {}", request.getTenantId(), e);
            throw new CustomException("ASSET_FETCH_FAILED", "Failed to fetch asset entities: " + e.getMessage());
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
            log.warn("Empty response from Asset Service for tenant: {}", tenantId);
            return new ArrayList<>();
        }

        Map<String, Object> response = (Map<String, Object>) responseOptional.get();
        List<Entity> entities = transformResponseToEntities(response);
        log.info("Successfully fetched {} asset entities", entities.size());
        return entities;
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
        return (Map<String, Object>) entity.getAttributes().get(POINT_GEOMETRY);
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
        attributes.put(ASSET_CATEGORY, getStringValue(municipalResponse, ASSET_CATEGORY));
        attributes.put("assetSubCategory", getStringValue(municipalResponse, "assetSubCategory"));
        attributes.put(ASSET_PARENT_CATEGORY, getStringValue(municipalResponse, ASSET_PARENT_CATEGORY));
        attributes.put("assetClassification", getStringValue(municipalResponse, "assetClassification"));
        attributes.put(DEPARTMENT, getStringValue(municipalResponse, DEPARTMENT));
        attributes.put("purchaseCost", municipalResponse.get("purchaseCost"));
        attributes.put("acquisitionCost", municipalResponse.get("acquisitionCost"));
        attributes.put("bookValue", municipalResponse.get("bookValue"));
        attributes.put("assetBookRefNo", getStringValue(municipalResponse, "assetBookRefNo"));
        attributes.put("assetUsage", getStringValue(municipalResponse, "assetUsage"));

        extractGeometryData(municipalResponse, attributes);

        return Entity.builder()
                .id(getStringValue(municipalResponse, "id"))
                .applicationNumber(getStringValue(municipalResponse, "applicationNo"))
                .tenantId(getStringValue(municipalResponse, TENANT_ID))
                .status(getStringValue(municipalResponse, STATUS))
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
        mapping.put(ASSET_CATEGORY, ASSET_CATEGORY);
        mapping.put(ASSET_PARENT_CATEGORY, ASSET_PARENT_CATEGORY);
        mapping.put(DEPARTMENT, DEPARTMENT);
        mapping.put(STATUS, STATUS);
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

        extractPolygonFromAdditionalDetails(municipalResponse, attributes, assetId);

        if (extractPointFromAddressDetails(municipalResponse, attributes, assetId)) {
            return;
        }

        extractPointFromLocation(municipalResponse, attributes, assetId);
    }

    @SuppressWarnings("unchecked")
    private void extractPolygonFromAdditionalDetails(Map<String, Object> municipalResponse,
            Map<String, Object> attributes, String assetId) {
        Map<String, Object> additionalDetails = (Map<String, Object>) municipalResponse.get("additionalDetails");
        if (additionalDetails == null || !additionalDetails.containsKey(GEOMETRY)) {
            return;
        }

        Map<String, Object> geometryWrapper = (Map<String, Object>) additionalDetails.get(GEOMETRY);
        if (geometryWrapper == null || !geometryWrapper.containsKey(GEOMETRY)) {
            return;
        }

        Map<String, Object> geometry = (Map<String, Object>) geometryWrapper.get(GEOMETRY);
        if (geometry == null || !"Polygon".equals(geometry.get("type"))) {
            return;
        }

        attributes.put("polygonGeometry", geometry);
        log.debug("Extracted polygon geometry for asset: {}", assetId);
    }

    @SuppressWarnings("unchecked")
    private boolean extractPointFromAddressDetails(Map<String, Object> municipalResponse,
            Map<String, Object> attributes, String assetId) {
        Map<String, Object> addressDetails = (Map<String, Object>) municipalResponse.get("addressDetails");
        if (addressDetails == null) {
            return false;
        }

        Double lat = getDoubleValue(addressDetails, "latitude");
        Double lon = getDoubleValue(addressDetails, "longitude");
        if (lat == null || lon == null || lat == 0.0 || lon == 0.0) {
            return false;
        }

        putPointGeometry(attributes, lon, lat);
        log.debug("Extracted point geometry for asset: {} at [{}, {}]", assetId, lon, lat);
        return true;
    }

    private void extractPointFromLocation(Map<String, Object> municipalResponse,
            Map<String, Object> attributes, String assetId) {
        String location = getStringValue(municipalResponse, "location");
        if (location == null || !location.contains(",")) {
            return;
        }

        try {
            String[] parts = location.split(",");
            if (parts.length != 2) {
                return;
            }

            Double lat = Double.parseDouble(parts[0].trim());
            Double lon = Double.parseDouble(parts[1].trim());
            if (lat == 0.0 || lon == 0.0) {
                return;
            }

            putPointGeometry(attributes, lon, lat);
            log.debug("Extracted point geometry from location for asset: {} at [{}, {}]", assetId, lon, lat);
        } catch (NumberFormatException e) {
            log.warn("Invalid location format for asset: {}, location: {}", assetId, location);
        }
    }

    private void putPointGeometry(Map<String, Object> attributes, Double lon, Double lat) {
        Map<String, Object> pointGeometry = new HashMap<>();
        pointGeometry.put("type", "Point");
        pointGeometry.put("coordinates", Arrays.asList(lon, lat));
        attributes.put(POINT_GEOMETRY, pointGeometry);
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
}
