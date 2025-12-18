package org.egov.garbageservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.egov.garbageservice.service.GarbageSmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/garbage/sms")
@Slf4j
public class GarbageSmsController {

    @Autowired
    private GarbageSmsService garbageSmsService;

    @PostMapping("/process-pending")
    public ResponseEntity<String> processPendingBills() {
        try {
            garbageSmsService.addPendingBillsToSmsTracker();
            log.info("Pending bills added to SMS tracker successfully");
            return ResponseEntity.ok("Pending bills added to SMS tracker successfully");
        } catch (Exception e) {
            log.error("Error while adding pending bills to SMS tracker", e);
            return ResponseEntity.internalServerError().body("Error processing pending bills");
        }
    }
}
