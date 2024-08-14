package com.example.hpgarbageservice.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hpgarbageservice.model.GarbageAccountActionRequest;
import com.example.hpgarbageservice.model.GarbageAccountActionResponse;
import com.example.hpgarbageservice.model.GarbageAccountRequest;
import com.example.hpgarbageservice.model.GarbageAccountResponse;
import com.example.hpgarbageservice.model.SearchCriteriaGarbageAccountRequest;
import com.example.hpgarbageservice.service.GarbageAccountService;

@RestController
@RequestMapping("/garbage-accounts")
public class GarbageAccountController {

    @Autowired
    private GarbageAccountService service;

    @CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
    @PostMapping("/_create")
    public ResponseEntity<GarbageAccountResponse> create(@RequestBody GarbageAccountRequest createGarbageRequest) {
        return ResponseEntity.ok(service.create(createGarbageRequest));
    }

    @CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
    @PostMapping("/_update")
    public ResponseEntity<GarbageAccountResponse> update(@RequestBody GarbageAccountRequest createGarbageRequest) {
        return ResponseEntity.ok(service.update(createGarbageRequest));
    }

    @CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
    @PostMapping("/_search")
    public ResponseEntity<GarbageAccountResponse> search(@RequestBody SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest) {
        return ResponseEntity.ok(service.searchGarbageAccounts(searchCriteriaGarbageAccountRequest));
    }
    
    @CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
    @PostMapping({"/fetch","/fetch/{value}"})
    public ResponseEntity<?> calculateTLFee(@RequestBody GarbageAccountActionRequest garbageAccountActionRequest
    										, @PathVariable String value){
    	
    	GarbageAccountActionResponse response = null;
    	
    	if(StringUtils.equalsIgnoreCase(value, "CALCULATEFEE")) {
    		response = service.getApplicationDetails(garbageAccountActionRequest);
    	}else if(StringUtils.equalsIgnoreCase(value, "ACTIONS")){
    		response = service.getActionsOnApplication(garbageAccountActionRequest);
    	}else {
    		return new ResponseEntity("Provide parameter to be fetched in URL.", HttpStatus.BAD_REQUEST);
    	}
    	
    	return new ResponseEntity(response, HttpStatus.OK);
    }
    
}
