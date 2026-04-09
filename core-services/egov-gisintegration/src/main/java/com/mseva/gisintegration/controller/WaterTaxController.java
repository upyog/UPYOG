package com.mseva.gisintegration.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mseva.gisintegration.model.WaterTax;
import com.mseva.gisintegration.service.WaterTaxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/watertax")
public class WaterTaxController {

    @Autowired
    private WaterTaxService waterTaxService;

 private JsonNode usersNode;
    
    public WaterTaxController() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            usersNode = mapper.readTree(new ClassPathResource("user.json").getInputStream()).get("users");
        } catch (IOException e) {
            e.printStackTrace();
            usersNode = null;
        }
    }
    
    @PostMapping("/_createOrUpdate")
    public ResponseEntity<?> createOrUpdateWaterTax(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                    @RequestBody WaterTax waterTax) {
        if (!com.mseva.gisintegration.util.AuthUtil.isAuthorized(authorization, usersNode)) {
            java.util.Map<String, String> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", "Unauthorized: Invalid or missing Authorization header");
            return ResponseEntity.status(401).body(errorResponse);
        }
        Map<String, Object> response = waterTaxService.createOrUpdateWaterTax(waterTax);
        return ResponseEntity.ok(response);
    }

    // Additional endpoints can be added here as needed

    @PostMapping("/_search")
    public ResponseEntity<?> searchByConnectionno(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                  @RequestParam(required = true) String tenantid,
                                                  @RequestParam(required = true) String connectionno) {
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
        if (connectionno == null || connectionno.isEmpty()) {
            java.util.Map<String, String> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", "'connectionno' query parameter must be provided");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        java.util.List<WaterTax> waterTaxes = waterTaxService.findByConnectionno(connectionno);
        if (waterTaxes == null || waterTaxes.isEmpty()) {
            java.util.Map<String, String> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", "WaterTax not found for connectionno: " + connectionno);
            return ResponseEntity.status(404).body(errorResponse);
        }
        return ResponseEntity.ok(waterTaxes);
    }
}
