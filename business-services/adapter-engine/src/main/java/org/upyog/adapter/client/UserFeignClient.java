package org.upyog.adapter.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.upyog.adapter.model.OAuthTokenResponse;
import org.upyog.adapter.model.UserSearchResponse;
import java.util.Map;
import java.net.URI;

@FeignClient(name = "user-feign-client")
public interface UserFeignClient {

    @PostMapping(consumes = "application/x-www-form-urlencoded")
    OAuthTokenResponse fetchToken(
            URI baseUri,
            @RequestHeader("Authorization") String authHeader,
            Map<String, ?> formParams
    );

    @PostMapping(consumes = "application/json")
    UserSearchResponse searchUser(
            URI baseUri,
            @RequestBody Map<String, Object> requestBody
    );
}
