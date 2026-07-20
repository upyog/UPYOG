package org.egov.garbageservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.egov.garbageservice.service.GarbageSmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/garbage/sms")
@Tag(name = "Garbage SMS", description = "APIs for garbage SMS operations")
/**
 * REST controller for batch SMS notification jobs on pending garbage bills.
 *
 * Behavior:
 * - POST /process-pending — calls {@link org.egov.garbageservice.service.GarbageSmsService#addPendingBillsToSmsTracker}
 *   to queue pending bills for SMS delivery tracking.
 * - Returns 200 with success message on completion, 500 on unexpected errors.
 *
 * Notes:
 * - Base path: /garbage/sms; Swagger tag "Garbage SMS".
 * - Intended for scheduler or ops-triggered runs, not citizen-facing UI.
 * - Errors are logged in-controller; failed runs return a plain-text error body.
 */
@Slf4j
public class GarbageSmsController {

    @Autowired
    private GarbageSmsService garbageSmsService;

    @Operation(summary = "Process pending bills for SMS")
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
