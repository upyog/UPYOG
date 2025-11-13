package org.egov.garbageservice.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

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
@RequestMapping("/garbage-bills")
public class GarbageBillController {

    @Autowired
    private GarbageBillService service;

    @PostMapping("/_create")
    public ResponseEntity<List<GarbageBill>> create(@RequestBody GarbageBillRequest garbageBillRequest) {
        return ResponseEntity.ok(service.createGarbageBills(garbageBillRequest));
    }

    @PostMapping("/_update")
    public ResponseEntity<List<GarbageBill>> update(@RequestBody GarbageBillRequest garbageBillRequest) {
        return ResponseEntity.ok(service.updateGarbageBills(garbageBillRequest));
    }

    @PostMapping("/_search")
    public ResponseEntity<List<GarbageBill>> search(@RequestBody SearchGarbageBillRequest searchGarbageBillRequest) {
        return ResponseEntity.ok(service.searchGarbageBills(searchGarbageBillRequest));
    }
    
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
