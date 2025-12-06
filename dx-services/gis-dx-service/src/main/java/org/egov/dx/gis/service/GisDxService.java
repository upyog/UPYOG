package org.egov.dx.gis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egov.dx.gis.config.GisDxConfiguration;
import org.egov.dx.gis.models.GeoJsonRequest;
import org.egov.dx.gis.models.GeoJsonResponse;
import org.egov.dx.gis.repository.ServiceRequestRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * GIS DX Service orchestrates GIS and payment data to generate GeoJSON
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GisDxService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final PaymentService paymentService;
    private final GisDxConfiguration config;

    /**
     * Fetches entities from GIS service and enriches with payment data
     * @param request GeoJSON request with business service, tenant, geometry type, and date range
     * @return GeoJSON response with FeatureCollection
     */
    public GeoJsonResponse fetchGeoJson(GeoJsonRequest request) {
        log.info("Generating GeoJSON for businessService: {}, tenant: {}, geometryType: {}, includeBillData: {}", 
                request.getBusinessService(), request.getTenantId(), request.getGeometryType(), request.getIncludeBillData());

        try {
            Map<String, Object> gisResponse = callGisService(request);
            List<Map<String, Object>> entities = extractEntities(gisResponse);
            log.info("Received {} entities from GIS service", entities.size());

            Map<String, Object> paymentData = Collections.emptyMap();
            if (Boolean.TRUE.equals(request.getIncludeBillData())) {
                paymentData = fetchPaymentData(request, entities);
                log.info("Fetched payment data for {} entities", paymentData.size());
            }

            Object geoJsonFeatureCollection = createGeoJsonFeatureCollection(entities, paymentData, request);

            GeoJsonResponse response = GeoJsonResponse.builder()
                    .tenantId(request.getTenantId())
                    .businessService(request.getBusinessService())
                    .totalCount(entities == null ? 0 : entities.size())
                    .geoJsonData(geoJsonFeatureCollection)
                    .build();
            
            log.info("Generated GeoJSON with {} features", entities.size());
            return response;

        } catch (Exception e) {
            log.error("Error generating GeoJSON: ", e);
            throw new RuntimeException("Failed to generate GeoJSON", e);
        }
    }

    /**
     * Calls GIS service to fetch entities with geometry
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> callGisService(GeoJsonRequest request) {
        String geometryType = request.getGeometryType() != null ? request.getGeometryType() : "point";
        String endpointTemplate = determineGisEndpoint(geometryType);
        String endpoint = endpointTemplate.replace("{businessService}",
                Optional.ofNullable(request.getBusinessService()).orElse(""));

        Map<String, Object> gisRequest = new HashMap<>();
        gisRequest.put("requestInfo", request.getRequestInfo());
        gisRequest.put("tenantId", request.getTenantId());
        gisRequest.put("businessService", request.getBusinessService());
        gisRequest.put("searchCriteria", request.getFilters() == null ? Collections.emptyMap() : request.getFilters());
        gisRequest.put("geometryType", geometryType);

        if (request.getFromDate() != null) {
            gisRequest.put("fromDate", request.getFromDate());
        }
        if (request.getToDate() != null) {
            gisRequest.put("toDate", request.getToDate());
        }

        StringBuilder uri = new StringBuilder(config.getGisHost() + endpoint);
        log.debug("Calling GIS service at: {}", uri);
        Optional<Object> responseOptional = serviceRequestRepository.fetchResult(uri, gisRequest);

        Object response = responseOptional.orElse(Collections.emptyMap());
        if (response instanceof Map) {
            return (Map<String, Object>) response;
        }
        log.warn("Unexpected GIS response type: {}", response.getClass().getName());
        return Collections.emptyMap();
    }

    private String determineGisEndpoint(String geometryType) {
        if ("polygon".equalsIgnoreCase(geometryType)) {
            return config.getGisPolygonsSearchEndpoint();
        }
        return config.getGisPointsSearchEndpoint();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractEntities(Map<String, Object> gisResponse) {
        if (gisResponse == null) return Collections.emptyList();
        Object entitiesObj = gisResponse.get("entities");
        if (!(entitiesObj instanceof List)) return Collections.emptyList();
        return (List<Map<String, Object>>) entitiesObj;
    }

    /**
     * Fetches payment data from collection service
     */
    private Map<String, Object> fetchPaymentData(GeoJsonRequest request, List<Map<String, Object>> entities) {
        if (entities == null || entities.isEmpty()) {
            log.warn("No entities to fetch payment data");
            return Collections.emptyMap();
        }

        List<String> entityIds = entities.stream()
                .map(e -> Optional.ofNullable(e.get("applicationNumber")).map(Object::toString).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (entityIds.isEmpty()) {
            log.warn("No valid entity IDs found");
            return Collections.emptyMap();
        }

        log.info("Fetching payment data for {} entities, financialYear: {} to {}", 
                entityIds.size(), request.getFromDate(), request.getToDate());
        return paymentService.fetchPaymentData(entityIds, request.getTenantId(), request.getBusinessService(), 
                request.getFromDate(), request.getToDate(), request.getRequestInfo());
    }

    /**
     * Creates GeoJSON FeatureCollection from entities
     */
    @SuppressWarnings("unchecked")
    private Object createGeoJsonFeatureCollection(List<Map<String, Object>> entities,
                                                  Map<String, Object> paymentData,
                                                  GeoJsonRequest request) {

        Map<String, Object> featureCollection = new HashMap<>();
        featureCollection.put("type", "FeatureCollection");

        List<Map<String, Object>> features = new ArrayList<>();
        int skippedCount = 0;
        
        if (entities != null) {
            for (Map<String, Object> entity : entities) {
                Map<String, Object> feature = createFeatureFromEntity(entity, paymentData, request);
                if (feature != null) {
                    features.add(feature);
                } else {
                    skippedCount++;
                }
            }
        }

        if (skippedCount > 0) {
            log.warn("Skipped {} entities without valid geometry", skippedCount);
        }
        
        featureCollection.put("features", features);
        log.debug("Created FeatureCollection with {} features", features.size());
        return featureCollection;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> createFeatureFromEntity(Map<String, Object> entity,
                                                        Map<String, Object> paymentData,
                                                        GeoJsonRequest request) {

        if (entity == null) return null;

        Map<String, Object> feature = new HashMap<>();
        feature.put("type", "Feature");
        feature.put("id", entity.get("applicationNumber"));

        // Determine geometry type and create geometry
        Map<String, Object> geometry = determineGeometry(entity, request.getGeometryType());
        if (geometry == null) {
            log.debug("No geometry found for entity: {}", entity.get("applicationNumber"));
            return null; // Skip features without valid geometry
        }
        feature.put("geometry", geometry);

        // Create properties
        Map<String, Object> properties = new HashMap<>();
        properties.put("applicationNumber", entity.get("applicationNumber"));
        properties.put("id", entity.get("id"));
        properties.put("tenantId", entity.get("tenantId"));
        properties.put("status", entity.get("status"));
        properties.put("businessService", entity.get("businessService"));

        // Add entity attributes (excluding geometry fields to avoid redundancy)
        Map<String, Object> attributes = (Map<String, Object>) entity.get("attributes");
        if (attributes != null) {
            for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                String key = entry.getKey();
                if ("pointGeometry".equals(key) || "polygonGeometry".equals(key)) continue;
                properties.put(key, entry.getValue());
            }
        }

        if (Boolean.TRUE.equals(request.getIncludeBillData())) {
            String entityId = Optional.ofNullable(entity.get("applicationNumber")).map(Object::toString).orElse(null);
            Map<String, Object> paymentInfo = entityId == null ? null : (Map<String, Object>) paymentData.get(entityId);

            if (paymentInfo != null) {
                properties.put("paymentStatus", paymentInfo.get("paymentStatus"));
                properties.put("transactionNumber", paymentInfo.get("transactionNumber"));
                properties.put("transactionDate", paymentInfo.get("transactionDate"));
                properties.put("amountPaid", paymentInfo.get("amountPaid"));
                properties.put("amountDue", paymentInfo.get("amountDue"));
            } else {
                properties.put("paymentStatus", "UNPAID");
            }
        }

        feature.put("properties", properties);
        return feature;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> determineGeometry(Map<String, Object> entity, String geometryType) {
        Map<String, Object> attributes = (Map<String, Object>) entity.get("attributes");
        if (attributes == null) {
            return null;
        }

        if ("polygon".equalsIgnoreCase(geometryType)) {
            Object polygonGeometry = attributes.get("polygonGeometry");
            if (polygonGeometry instanceof Map) {
                return (Map<String, Object>) polygonGeometry;
            }
        }

        Object pointGeometry = attributes.get("pointGeometry");
        if (pointGeometry instanceof Map) {
            return (Map<String, Object>) pointGeometry;
        }

        return null;
    }
}
