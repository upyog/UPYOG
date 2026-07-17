package org.upyog.extractor.controller;

// import static java.util.Map.entry; // Required for Map.ofEntries
// import java.util.List;
// import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
// import org.upyog.adapter.api.AdapterClient;
// import org.upyog.adapter.loader.impl.HttpLoader;
// import org.upyog.adapter.model.AdapterRequest;
// import org.upyog.adapter.model.DashboardData;
// import org.upyog.adapter.model.DashboardPayload;
import org.upyog.adapter.model.IngestionResult;
import org.upyog.extractor.service.DailyIngestionService;

@RestController
@RequestMapping("/api/v1/test")
public class Test {

    // @Autowired
    // private AdapterClient adapterClient;
    @Autowired
    private DailyIngestionService service;

    @GetMapping
    public ResponseEntity<IngestionResult> pushData() {
        // DashboardData dashboardData = DashboardData.builder().date("15-07-2026").module("PT").ward("Block 4")
        // 		.ulb("pg.citya").region("Test").state("PG")
        // 		.metrics(Map.<String, Object>ofEntries(entry("assessments", 48), entry("todaysTotalApplications", 145),
        // 				entry("todaysClosedApplications", 132), entry("noOfPropertiesPaidToday", 28),
        // 				entry("todaysApprovedApplications", 124), entry("todaysApprovedApplicationsWithinSLA", 118),
        // 				entry("avgDaysForApplicationApproval", 5),

        // 				entry("propertiesRegistered", List.of(Map.of("groupBy", "financialYear", "buckets",
        // 						List.of(Map.of("name", "2018-19", "value", 8), Map.of("name", "2019-20", "value", 35),
        // 								Map.of("name", "2020-21", "value", 46))))),
        // 				entry("assessedProperties",
        // 						List.of(Map.of("groupBy", "usageCategory", "buckets",
        // 								List.of(Map.of("name", "RESIDENTIAL", "value", 48),
        // 										Map.of("name", "COMMERCIAL", "value", 58),
        // 										Map.of("name", "INDUSTRIAL", "value", 16))))),
        // 				entry("transactions",
        // 						List.of(Map.of("groupBy", "usageCategory", "buckets",
        // 								List.of(Map.of("name", "RESIDENTIAL", "value", 24),
        // 										Map.of("name", "COMMERCIAL", "value", 18),
        // 										Map.of("name", "INDUSTRIAL", "value", 14))))),
        // 				entry("todaysCollection", List.of(
        // 						Map.of("groupBy", "usageCategory", "buckets",
        // 								List.of(Map.of("name", "RESIDENTIAL", "value", 8500),
        // 										Map.of("name", "COMMERCIAL", "value", 11200),
        // 										Map.of("name", "INDUSTRIAL", "value", 5200))),
        // 						Map.of("groupBy", "paymentChannelType", "buckets",
        // 								List.of(Map.of("name", "Digital", "value", 18500),
        // 										Map.of("name", "Non Digital", "value", 6400))))),
        // 				entry("propertyTax",
        // 						List.of(Map.of("groupBy", "usageCategory", "buckets",
        // 								List.of(Map.of("name", "RESIDENTIAL", "value", 6800),
        // 										Map.of("name", "COMMERCIAL", "value", 9200),
        // 										Map.of("name", "INDUSTRIAL", "value", 3800))))),
        // 				entry("cess",
        // 						List.of(Map.of("groupBy", "usageCategory", "buckets",
        // 								List.of(Map.of("name", "RESIDENTIAL", "value", 720),
        // 										Map.of("name", "COMMERCIAL", "value", 880),
        // 										Map.of("name", "INDUSTRIAL", "value", 460))))),
        // 				entry("rebate",
        // 						List.of(Map.of("groupBy", "usageCategory", "buckets",
        // 								List.of(Map.of("name", "RESIDENTIAL", "value", -380),
        // 										Map.of("name", "COMMERCIAL", "value", -620),
        // 										Map.of("name", "INDUSTRIAL", "value", -160))))),
        // 				entry("penalty",
        // 						List.of(Map.of("groupBy", "usageCategory", "buckets",
        // 								List.of(Map.of("name", "RESIDENTIAL", "value", 320),
        // 										Map.of("name", "COMMERCIAL", "value", 1100),
        // 										Map.of("name", "INDUSTRIAL", "value", 580))))),
        // 				entry("interest",
        // 						List.of(Map.of("groupBy", "usageCategory", "buckets",
        // 								List.of(Map.of("name", "RESIDENTIAL", "value", 620),
        // 										Map.of("name", "COMMERCIAL", "value", 1280),
        // 										Map.of("name", "INDUSTRIAL", "value", 760)))))))
        // 		.build();
        // // Create your result wrapper to return
        // AdapterRequest adapterRequest = AdapterRequest.builder().module(org.upyog.adapter.common.constants.Module.PT)
        // 		.rawData(List.of(dashboardData)).build();
        // IngestionResult result = adapterClient.execute(adapterRequest);
        // // Set your dashboardData into your result object if needed here:
        IngestionResult result = service.ingestDailyPTData();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
