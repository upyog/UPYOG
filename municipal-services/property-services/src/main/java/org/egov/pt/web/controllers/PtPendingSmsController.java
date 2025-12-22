package org.egov.pt.web.controllers;

import lombok.extern.slf4j.Slf4j;
import org.egov.pt.service.PropertySmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;


@RestController
@RequestMapping("/property/sms")
@Slf4j
public class PtPendingSmsController {

    @Autowired
    private PropertySmsService propertySmsService;

    @PostMapping("/process-pending")
    public ResponseEntity<String> processPendingBills() {
        try {
        	propertySmsService.addPendingBillsToSmsTracker();
            log.info("Pending bills added to SMS tracker successfully");
            return ResponseEntity.ok("Pending bills added to SMS tracker successfully");
        } catch (Exception e) {
            log.error("Error while adding pending bills to SMS tracker", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing pending bills");
        }
    }
}
