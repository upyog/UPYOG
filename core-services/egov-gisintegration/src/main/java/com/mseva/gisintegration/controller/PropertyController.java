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

    
    private JsonNode usersNode;

    public PropertyController() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            usersNode = mapper.readTree(new ClassPathResource("user.json").getInputStream()).get("users");
        } catch (IOException e) {
            e.printStackTrace();
            usersNode = null;
        }
    }

    @PostMapping("/_createOrUpdate")
    public ResponseEntity<?> createOrUpdateProperty(@RequestBody Property property, @RequestHeader(value = "Authorization", required = false) String authorization) {
        log.info("Authorization header: {}", authorization);
        if (!com.mseva.gisintegration.util.AuthUtil.isAuthorized(authorization, usersNode)) {
            java.util.Map<String, String> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", "Unauthorized: Invalid or missing Authorization header");
            return ResponseEntity.status(401).body(errorResponse);
        }
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

    @PostMapping("/_search")
    public ResponseEntity<?> searchBySurveyidOrPropertyid(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                         @RequestParam(required = true) String tenantid,
                                                         @RequestParam(required = false) String surveyid,
                                                         @RequestParam(required = false) String propertyid) {
        if (!com.mseva.gisintegration.util.AuthUtil.isAuthorized(authorization, usersNode)) {
            java.util.Map<String, String> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", "Unauthorized: Invalid or missing Authorization header");
            return ResponseEntity.status(401).body(errorResponse);
        }
        if (tenantid == null || tenantid.isEmpty()) {
            java.util.Map<String, String> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", "'tenantid' query parameter must be provided");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        if ((surveyid == null || surveyid.isEmpty()) && (propertyid == null || propertyid.isEmpty())) {
            java.util.Map<String, String> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", "Either 'surveyid' or 'propertyid' query parameter must be provided");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        java.util.List<Property> properties = null;
        if (surveyid != null && !surveyid.isEmpty() && propertyid != null && !propertyid.isEmpty()) {
            properties = propertyRepository.findBySurveyidAndPropertyid(surveyid, propertyid, tenantid);
        } else if (surveyid != null && !surveyid.isEmpty()) {
            properties = propertyService.findBySurveyid(surveyid, tenantid);
        } else if (propertyid != null && !propertyid.isEmpty()) {
            properties = propertyService.findByPropertyid(propertyid, tenantid);
        }
        if (properties == null || properties.isEmpty()) {
            java.util.Map<String, String> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", "Property not found for identifier: " + (surveyid != null ? surveyid : propertyid));
            return ResponseEntity.status(404).body(errorResponse);
        }
        return ResponseEntity.ok(properties);
    }

    
}
