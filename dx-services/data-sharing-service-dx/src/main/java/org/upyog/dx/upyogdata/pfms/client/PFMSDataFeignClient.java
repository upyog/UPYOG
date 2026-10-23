package org.upyog.dx.upyogdata.pfms.client;

import org.upyog.dx.upyogdata.pfms.models.PFMSTransactionFormData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Feign client for the PFMS transaction data push API.
 * Sends transaction details as form-urlencoded with a Bearer token in the header.
 */


@FeignClient(name = "pfms-data-client", url = "${pfms.data.url}")
public interface PFMSDataFeignClient {

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String pushTransaction(@RequestHeader("Authorization") String bearerToken,
                           @RequestBody PFMSTransactionFormData formData);
}
