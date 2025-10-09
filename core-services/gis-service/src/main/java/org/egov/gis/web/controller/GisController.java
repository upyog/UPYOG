package org.egov.gis.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.egov.gis.models.GisRequest;
import org.egov.gis.models.GisResponse;
import org.egov.gis.service.GisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * GIS Controller that works with any municipal service
 */
@RestController
@RequestMapping("/gis/v1")
@Slf4j
public class GisController {

    @Autowired
    private GisService gisService;

    /**
     * Get entities with point geometry
     */
    @PostMapping("/{businessService}/points/_search")
    public ResponseEntity<GisResponse> searchEntitiesWithPoints(
            @PathVariable String businessService,
            @Valid @RequestBody GisRequest request) {
        
        log.info("Received point search request for business service: {} and tenant: {}", 
                businessService, request.getTenantId());
        
        request.setBusinessService(businessService);
        request.setGeometryType("point");
        
        try {
            GisResponse response = gisService.getEntitiesWithPoints(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing point search request: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get entities with polygon geometry
     */
    @PostMapping("/{businessService}/polygons/_search")
    public ResponseEntity<GisResponse> searchEntitiesWithPolygons(
            @PathVariable String businessService,
            @Valid @RequestBody GisRequest request) {
        
        log.info("Received polygon search request for business service: {} and tenant: {}", 
                businessService, request.getTenantId());
        
        request.setBusinessService(businessService);
        request.setGeometryType("polygon");
        
        try {
            GisResponse response = gisService.getEntitiesWithPolygons(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing polygon search request: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get available business services
     */
    @GetMapping("/business-services")
    public ResponseEntity<List<String>> getAvailableBusinessServices() {
        try {
            List<String> businessServices = gisService.getAvailableBusinessServices();
            return ResponseEntity.ok(businessServices);
        } catch (Exception e) {
            log.error("Error getting available business services: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
