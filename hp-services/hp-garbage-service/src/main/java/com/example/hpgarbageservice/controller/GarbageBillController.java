package com.example.hpgarbageservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hpgarbageservice.model.GarbageBill;
import com.example.hpgarbageservice.model.GarbageBillRequest;
import com.example.hpgarbageservice.model.SearchGarbageBillRequest;
import com.example.hpgarbageservice.service.GarbageBillService;

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

}
