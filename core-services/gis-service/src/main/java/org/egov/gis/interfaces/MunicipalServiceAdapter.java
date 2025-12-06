package org.egov.gis.interfaces;

import org.egov.gis.models.Entity;
import org.egov.gis.models.GisRequest;

import java.util.List;
import java.util.Map;

/**
 * Interface for adapting different municipal services to the generic GIS service
 * Each municipal service (Property, Trade License, etc.) implements this interface
 */
public interface MunicipalServiceAdapter {

    /**
     * Get the business service name this adapter handles
     */
    String getBusinessService();

    /**
     * Fetch entities from the municipal service
     */
    List<Entity> fetchEntities(GisRequest request);

    /**
     * Extract point coordinates from entity address
     */
    Map<String, Object> extractPointGeometry(Entity entity);

    /**
     * Extract polygon coordinates from entity additionalDetails
     */
    Map<String, Object> extractPolygonGeometry(Entity entity);

    /**
     * Get the municipal service endpoint configuration
     */
    String getServiceEndpoint();

    /**
     * Transform municipal service response to Entity
     */
    Entity transformToGenericEntity(Map<String, Object> municipalResponse);

    /**
     * Get search criteria mapping for the municipal service
     */
    Map<String, String> getSearchCriteriaMapping();
}
