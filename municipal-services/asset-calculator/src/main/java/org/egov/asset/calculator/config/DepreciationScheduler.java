//package org.egov.asset.calculator.config;
//
//import lombok.RequiredArgsConstructor;
//import org.egov.asset.calculator.services.DepreciationService;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class DepreciationScheduler {
//
//    private final DepreciationService depreciationService;
//
//    @Scheduled(cron = "0 0 0 31 3 ?") // Midnight on March 31st
//    public void runYearEndDepreciation() {
//        depreciationService.calculateBulkDepreciation();
//    }
//}
