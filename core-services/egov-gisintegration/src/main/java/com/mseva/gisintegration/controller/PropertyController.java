package com.mseva.gisintegration.controller;

import com.mseva.gisintegration.model.Property;
import com.mseva.gisintegration.repository.PropertyRepository;
import com.mseva.gisintegration.model.LoginRequest;
import com.mseva.gisintegration.service.PropertyService;
import com.mseva.gisintegration.validator.PropertyValidator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private static final Logger log = LoggerFactory.getLogger(PropertyController.class);

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private PropertyValidator propertyValidator;

    @Autowired
    private PropertyRepository propertyRepository;

    public PropertyController() {
    }

    @PostMapping("/_createOrUpdate")
    public ResponseEntity<?> createOrUpdateProperty(@RequestBody Property property) {
        if (!propertyValidator.isValid(property)) {
            java.util.Map<String, String> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", "Property ID and Locality Code must be provided");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        try {
            java.util.Map<String, Object> response = propertyService.createOrUpdateProperty(property);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            java.util.Map<String, String> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/_search")
    public ResponseEntity<?> searchBySurveyidOrPropertyid(@RequestParam(name = "town_name", required = true) String tenantid,
                                                         @RequestParam(required = false) String surveyid,
                                                         @RequestParam(required = false) String propertyid,
                                                         @RequestParam(required = false) String assessmentyear) {
        if (tenantid == null || tenantid.isEmpty()) {
            java.util.Map<String, String> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", "'town_name' query parameter must be provided");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        java.util.List<Property> properties = null;
        if (surveyid != null && !surveyid.isEmpty() && propertyid != null && !propertyid.isEmpty()) {
            properties = propertyRepository.findBySurveyidAndPropertyid(surveyid, propertyid, tenantid);
        } else if (surveyid != null && !surveyid.isEmpty()) {
            properties = propertyService.findBySurveyid(surveyid, tenantid);
        } else if (propertyid != null && !propertyid.isEmpty()) {
            properties = propertyService.findByPropertyid(propertyid, tenantid);
        } else if (assessmentyear != null && !assessmentyear.isEmpty()) {
            properties = propertyService.findByTenantidAndAssessmentyear(tenantid, assessmentyear);
        } else {
            java.util.Map<String, String> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error",
                    "If not searching by surveyid or propertyid, 'assessmentyear' query parameter must be provided");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        if (properties == null || properties.isEmpty()) {
            java.util.Map<String, String> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", "Property not found for the given search criteria");
            return ResponseEntity.status(404).body(errorResponse);
        }
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/v2/_search")
    public ResponseEntity<?> searchBySurveyidOrPropertyidV2(@RequestParam(name = "town_name", required = true) String tenantid,
                                                         @RequestParam(required = false) String surveyid,
                                                         @RequestParam(required = false) String propertyid,
                                                         @RequestParam(required = false) String assessmentyear) {
        return searchBySurveyidOrPropertyid(tenantid, surveyid, propertyid, assessmentyear);
    }

}
