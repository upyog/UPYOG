package org.upyog.pgrai.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    /**
     * Calls FastAPI endpoint to update an existing grievance.
     *
     * @param grievanceId ID of the grievance to be updated.
     * @param updateFields Map with fields to be updated.
     * @return Updated GrievanceResponse object.
     */
    @PutMapping("/grievances/{grievance_id}")
    GrievanceResponse updateGrievance(@PathVariable("grievance_id") String grievanceId,
            @RequestBody Map<String, Object> updateFields);

}

