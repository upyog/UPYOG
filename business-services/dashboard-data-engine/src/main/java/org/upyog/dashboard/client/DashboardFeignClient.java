package org.upyog.dashboard.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.net.URI;

/**
 * OpenFeign client for communicating with the external National Dashboard ingestion API.
 */
@FeignClient(name = "dashboard-feign-client", url = "${national.dashboard.ingest.url}")
public interface DashboardFeignClient {

    @PostMapping(consumes = "application/json", produces = "application/json")
    String ingestMetrics(
            URI baseUri,
            @RequestBody String payloadJson
    );
}
