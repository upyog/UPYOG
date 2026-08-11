package org.upyog.reconciliation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.upyog.reconciliation.service.ReconciliationSchedulerService;

import java.time.LocalDate;

@RestController
@RequestMapping("/test/reconciliation")
public class TestReconciliationController {

    @Autowired
    private ReconciliationSchedulerService reconciliationSchedulerService;

    @PostMapping("/execute")
    public ResponseEntity<String> executeExtractionForDate(
            @RequestParam(name = "date", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        if (date == null) {
            date = LocalDate.now();
        }
        
        reconciliationSchedulerService.executeExtractionForDate(date);
        
        return ResponseEntity.ok("Reconciliation extraction job triggered successfully for date: " + date);
    }
}
