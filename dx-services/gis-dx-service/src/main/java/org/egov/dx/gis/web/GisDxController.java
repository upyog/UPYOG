package org.egov.dx.gis.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egov.dx.gis.models.GeoJsonRequest;
import org.egov.dx.gis.models.GeoJsonResponse;
import org.egov.dx.gis.service.GisDxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * GIS DX Controller that works with any municipal service
 */
@RestController
@RequestMapping("/gis-dx/v1")
@RequiredArgsConstructor
@Slf4j
public class GisDxController {

    @Autowired
    private GisDxService gisDxService;

    /**
     * Generate GeoJSON data for any municipal service
     */
    @PostMapping("/_search")
    public ResponseEntity<GeoJsonResponse> search(
            @Valid @RequestBody GeoJsonRequest request) {
        
        log.info("Received GeoJSON search request for business service: {} and tenant: {}",
                request.getBusinessService(), request.getTenantId());
        
        try {
            GeoJsonResponse response = gisDxService.fetchGeoJson(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing GeoJSON search request: ", e);
            return ResponseEntity.status(500).build();
        }
    }
}


