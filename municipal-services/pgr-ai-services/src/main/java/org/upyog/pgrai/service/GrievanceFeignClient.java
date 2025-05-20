package org.upyog.pgrai.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.upyog.pgrai.web.models.grievanceClient.Grievance;
import org.upyog.pgrai.web.models.grievanceClient.GrievanceResponse;

import java.util.Map;

/**
 * Feign client for sending grievance data to the FastAPI service.
 */
@FeignClient(name = "grievance-api", url = "${grievance.api.url}")
public interface GrievanceFeignClient {

    /**
     * Calls FastAPI endpoint to create a new grievance.
     *
     * @param grievance Grievance data to be sent.
     * @return Map with grievance ID and status.
     */
    @PostMapping("/grievances")
    GrievanceResponse createGrievance(@RequestBody Grievance grievance);
}

