package com.example.hpgarbageservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hpgarbageservice.model.GarbageAccount;
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

}
