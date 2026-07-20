package org.egov.garbageservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.egov.garbageservice.model.GarbageCommonRequest;
import org.egov.garbageservice.service.GarbageCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Garbage Common", description = "APIs for common garbage operations")
@RequestMapping("/garbage-common")
/**
 * REST controller for shared garbage configuration and aggregate utilities.
 *
 * Behavior:
 * - POST /_create — create/update common garbage master data via {@link org.egov.garbageservice.service.GarbageCommonService#create}.
 * - GET /getAllCounts — return aggregate count metrics from the common service.
 *
 * Notes:
 * - Base path: /garbage-common; Swagger tag "Garbage Common".
 * - Commented-out collection-unit endpoints are legacy stubs, not active APIs.
 * - Thin controller; no workflow or billing logic here.
 */
public class GarbageCommonController {

    @Autowired
    private GarbageCommonService service;

    @Operation(summary = "Create garbage common request")
    @PostMapping("/_create")
    public ResponseEntity<GarbageCommonRequest> create(@RequestBody GarbageCommonRequest garbageCommonRequest) {
        return ResponseEntity.ok(service.create(garbageCommonRequest));
    }
    
    @Operation(summary = "Get all counts")
    @GetMapping("/getAllCounts")
	public ResponseEntity<?> getAllCounts() {
	    return ResponseEntity.ok(service.getAllcounts());
	}

//    @PostMapping("/collection-unit/_update")
//    public ResponseEntity<List<GarbageAccount>> update(@RequestBody GarbageAccountRequest createGarbageRequest) {
//        return ResponseEntity.ok(service.update(createGarbageRequest));
//    }
//
//    @PostMapping("/collection-unit/_search")
//    public ResponseEntity<List<GarbageAccount>> search(@RequestBody SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest) {
//        return ResponseEntity.ok(service.searchGarbageAccounts(searchCriteriaGarbageAccountRequest));
//    }
}
