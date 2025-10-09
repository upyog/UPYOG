package org.egov.gis.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.gis.interfaces.MunicipalServiceAdapter;
import org.egov.gis.models.Entity;
import org.egov.gis.models.GisRequest;
import org.egov.gis.models.GisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * GIS Service for fetching and normalizing municipal service data with geometry
 */
@Slf4j
@Service
public class GisService {

    @Autowired
    private ServiceRegistry serviceRegistry;

    /**
     * Fetches entities with point geometry
     * @param request GIS request with business service, tenant, and search criteria
     * @return GIS response with entities containing point geometry
     */
    public GisResponse getEntitiesWithPoints(GisRequest request) {
        log.info("Fetching entities with points for businessService: {}, tenant: {}", 
                request.getBusinessService(), request.getTenantId());

        MunicipalServiceAdapter adapter = serviceRegistry.getAdapter(request.getBusinessService());
        List<Entity> entities = adapter.fetchEntities(request);

        for (Entity entity : entities) {
            if (!entity.getAttributes().containsKey("pointGeometry")) {
                Map<String, Object> pointGeometry = adapter.extractPointGeometry(entity);
                if (pointGeometry != null) {
                    entity.getAttributes().put("pointGeometry", pointGeometry);
                }
            }
        }

        log.info("Returning {} entities with point geometry", entities.size());
        return GisResponse.builder()
                .tenantId(request.getTenantId())
                .businessService(request.getBusinessService())
                .totalCount(entities.size())
                .entities(entities)
                .build();
    }

    /**
     * Fetches entities with polygon geometry
     * @param request GIS request with business service, tenant, and search criteria
     * @return GIS response with entities containing polygon geometry
     */
    public GisResponse getEntitiesWithPolygons(GisRequest request) {
        log.info("Fetching entities with polygons for businessService: {}, tenant: {}", 
                request.getBusinessService(), request.getTenantId());

        MunicipalServiceAdapter adapter = serviceRegistry.getAdapter(request.getBusinessService());
        List<Entity> entities = adapter.fetchEntities(request);

        for (Entity entity : entities) {
            if (!entity.getAttributes().containsKey("polygonGeometry")) {
                Map<String, Object> polygonGeometry = adapter.extractPolygonGeometry(entity);
                if (polygonGeometry != null) {
                    entity.getAttributes().put("polygonGeometry", polygonGeometry);
                }
            }
        }

        log.info("Returning {} entities with polygon geometry", entities.size());
        return GisResponse.builder()
                .tenantId(request.getTenantId())
                .businessService(request.getBusinessService())
                .totalCount(entities.size())
                .entities(entities)
                .build();
    }

    public List<String> getAvailableBusinessServices() {
        return serviceRegistry.getAvailableBusinessServices();
    }
}
