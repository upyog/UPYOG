package com.example.hpgarbageservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.hpgarbageservice.model.GarbageAccount;
import com.example.hpgarbageservice.model.GarbageAccountRequest;
import com.example.hpgarbageservice.model.SearchCriteriaGarbageAccount;
import com.example.hpgarbageservice.service.GarbageAccountService;

@RestController
@RequestMapping("/api/garbage-accounts")
public class GarbageAccountController {

    @Autowired
    private GarbageAccountService service;

    @PostMapping("/_create")
    public ResponseEntity<GarbageAccount> create(@RequestBody GarbageAccountRequest createGarbageRequest) {
        return ResponseEntity.ok(service.create(createGarbageRequest));
    }

    @PutMapping("/_update")
    public ResponseEntity<GarbageAccount> update(@RequestBody GarbageAccountRequest createGarbageRequest) {
        return ResponseEntity.ok(service.update(createGarbageRequest));
    }

    @PutMapping("/_search")
    public ResponseEntity<List<GarbageAccount>> findById(@RequestParam SearchCriteriaGarbageAccount searchCriteriaGarbageAccount) {
        return ResponseEntity.ok(service.searchGarbageAccounts(searchCriteriaGarbageAccount));
    }

}
