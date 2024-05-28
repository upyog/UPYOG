package com.example.hpgarbageservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.hpgarbageservice.model.GarbageBill;
import com.example.hpgarbageservice.service.GarbageBillService;

@RestController
@RequestMapping("/api/garbage-bills")
public class GarbageBillController {

    @Autowired
    private GarbageBillService service;

    @PostMapping
    public ResponseEntity<GarbageBill> create(@RequestBody GarbageBill bill) {
        return ResponseEntity.ok(service.create(bill));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GarbageBill> update(@PathVariable Long id, @RequestBody GarbageBill bill) {
        bill.setId(id);
        return ResponseEntity.ok(service.update(bill));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GarbageBill> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

}
