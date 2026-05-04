package com.mseva.gisintegration.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mseva.gisintegration.model.SewerageTax;
import com.mseva.gisintegration.service.SewerageTaxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/seweragetax")
public class SewerageTaxController {

    @Autowired
    private SewerageTaxService sewerageTaxService;

    public SewerageTaxController() {
    }

    @PostMapping("/_createOrUpdate")
    public ResponseEntity<Map<String, Object>> createOrUpdateSewerageTax(@RequestBody SewerageTax sewerageTax) {
        Map<String, Object> response = sewerageTaxService.createOrUpdateSewerageTax(sewerageTax);
        return ResponseEntity.ok(response);
    }

    // Additional endpoints can be added here as needed

    @GetMapping("/_search")
    public ResponseEntity<?> searchByConnectionno(@RequestParam(name = "town_name", required = true) String tenantid,
            @RequestParam(required = false) String connectionno,
            @RequestParam(required = false) String assessmentyear) {
        if (tenantid == null || tenantid.isEmpty()) {
            java.util.Map<String, String> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", "'town_name' query parameter must be provided");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        java.util.List<com.mseva.gisintegration.model.SewerageTax> sewerageTaxes = null;
        if (connectionno != null && !connectionno.isEmpty()) {
            sewerageTaxes = sewerageTaxService.findByConnectionno(connectionno);
        } else if (assessmentyear != null && !assessmentyear.isEmpty()) {
            sewerageTaxes = sewerageTaxService.findByTenantidAndAssessmentyear(tenantid, assessmentyear);
        } else {
            java.util.Map<String, String> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error",
                    "If not searching by connectionno, 'assessmentyear' query parameter must be provided");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        if (sewerageTaxes == null || sewerageTaxes.isEmpty()) {
            java.util.Map<String, String> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", "SewerageTax not found for the given search criteria");
            return ResponseEntity.status(404).body(errorResponse);
        }
        return ResponseEntity.ok(sewerageTaxes);
    }

    @GetMapping("/v2/_search")
    public ResponseEntity<?> searchByConnectionnoV2(@RequestParam(name = "town_name", required = true) String tenantid,
                                                  @RequestParam(required = false) String connectionno,
                                                  @RequestParam(required = false) String assessmentyear) {
        return searchByConnectionno(tenantid, connectionno, assessmentyear);
    }
}
