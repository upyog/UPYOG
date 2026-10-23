package org.upyog.dx.upyogdata.pfms.client;

import org.upyog.dx.upyogdata.pfms.models.PFMSAuthRequest;
import org.upyog.dx.upyogdata.pfms.models.PFMSAuthResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client for the PFMS authentication API.
 * Sends username and password, receives a Bearer access token in response.
 */


@FeignClient(name = "pfms-auth-client", url = "${pfms.auth.url}")
public interface PFMSAuthFeignClient {

    @PostMapping
    PFMSAuthResponse authenticate(@RequestBody PFMSAuthRequest request);
}
