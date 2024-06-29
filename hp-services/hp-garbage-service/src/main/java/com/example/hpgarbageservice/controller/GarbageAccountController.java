package com.example.hpgarbageservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hpgarbageservice.model.GarbageAccount;
import com.example.hpgarbageservice.model.GarbageAccountRequest;
import com.example.hpgarbageservice.model.SearchCriteriaGarbageAccountRequest;
import com.example.hpgarbageservice.service.GarbageAccountService;

@RestController
@RequestMapping("/garbage-accounts")
public class GarbageAccountController {

    @Autowired
    private GarbageAccountService service;

    @PostMapping("/_create")
    public ResponseEntity<List<GarbageAccount>> create(@RequestBody GarbageAccountRequest createGarbageRequest) {
        return ResponseEntity.ok(service.create(createGarbageRequest));
    }

    @PostMapping("/_update")
    public ResponseEntity<List<GarbageAccount>> update(@RequestBody GarbageAccountRequest createGarbageRequest) {
        return ResponseEntity.ok(service.updateGrbgAccount(createGarbageRequest));
    }

    @PostMapping("/_search")
    public ResponseEntity<List<GarbageAccount>> search(@RequestBody SearchCriteriaGarbageAccountRequest searchCriteriaGarbageAccountRequest) {
        return ResponseEntity.ok(service.searchGarbageAccounts(searchCriteriaGarbageAccountRequest));
    }

}
