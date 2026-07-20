package org.egov.garbageservice.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.egov.garbageservice.contract.bill.CancleBillRequest;
import org.egov.garbageservice.contract.bill.UpdateBillRequest;
import org.egov.garbageservice.model.GarbageBill;
import org.egov.garbageservice.model.GarbageBillRequest;
import org.egov.garbageservice.model.SearchGarbageBillRequest;
import org.egov.garbageservice.service.GarbageBillService;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Garbage Bills", description = "APIs for managing garbage bills")
@RequestMapping("/garbage-bills")
/**
 * REST controller for garbage bill records linked to garbage accounts.
 *
 * Behavior:
 * - POST /_create — persist new {@link org.egov.garbageservice.model.GarbageBill} rows via {@link GarbageBillService#createGarbageBills}.
 * - POST /_update — update existing garbage bill records.
 * - POST /_search — search bills using {@link org.egov.garbageservice.model.SearchGarbageBillRequest} criteria.
 * - POST _cancelbill — cancel bills using {@link CancleBillRequest}; returns success message or throws INVALID UPDATE.
 *
 * Notes:
 * - Base path: /garbage-bills; Swagger tag "Garbage Bills".
 * - Cancel endpoint path has no leading slash ({@code _cancelbill} vs {@code /_cancelbill}) — matches existing API mapping.
 * - Bill cancellation also invokes billing-service flows inside GarbageBillService.
 */
public class GarbageBillController {

    @Autowired
    private GarbageBillService service;

    @Operation(summary = "Create garbage bills")
    @PostMapping("/_create")
    public ResponseEntity<List<GarbageBill>> create(@RequestBody GarbageBillRequest garbageBillRequest) {
        return ResponseEntity.ok(service.createGarbageBills(garbageBillRequest));
    }

    @Operation(summary = "Update garbage bills")
    @PostMapping("/_update")
    public ResponseEntity<List<GarbageBill>> update(@RequestBody GarbageBillRequest garbageBillRequest) {
        return ResponseEntity.ok(service.updateGarbageBills(garbageBillRequest));
    }

    @Operation(summary = "Search garbage bills")
    @PostMapping("/_search")
    public ResponseEntity<List<GarbageBill>> search(@RequestBody SearchGarbageBillRequest searchGarbageBillRequest) {
        return ResponseEntity.ok(service.searchGarbageBills(searchGarbageBillRequest));
    }
    
	@Operation(summary = "Cancel garbage bill")
	@PostMapping("_cancelbill")
	public ResponseEntity<?> cancelBill(@RequestBody @Valid CancleBillRequest cancleBillRequest){
		Map<String,String> res = new HashMap<>();
		if(service.cancelGarbageBill(cancleBillRequest)) {
			res.put("message", "Bill Successfully Cancelled");
			return ResponseEntity.ok(res);
		}else {
			throw new CustomException("INVALID UPDATE",
					"Bill could not be Cancelled");
		}

	}

}
