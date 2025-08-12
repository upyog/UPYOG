package org.upyog.adv.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import java.util.HashMap;

/**
 * Utility class for shortening URLs in the Advertisement Booking Service.
 * 
 * Key Responsibilities:
 * - Shortens long URLs using an external URL shortening service.
 * - Handles errors gracefully by returning the original URL if shortening fails.
 * 
 * Methods:
 * - `getShortenedUrl`: Sends a request to the URL shortening service and returns the shortened URL.
 * 
 * Dependencies:
 * - RestTemplate: Used for making HTTP requests to the URL shortening service.
 * 
 * Configuration:
 * - `egov.url.shortner.host`: Base URL of the URL shortening service.
 * - `egov.url.shortner.endpoint`: API endpoint for the URL shortening service.
 * 
 * Annotations:
 * - @Component: Marks this class as a Spring-managed component.
 * - @Slf4j: Enables logging for debugging and monitoring URL shortening operations.
 */
@Slf4j
@Component
public class UrlShortenerUtil {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${egov.url.shortner.host}")
    private String urlShortnerHost;

    @Value("${egov.url.shortner.endpoint}")
    private String urShortnerPath;

    public String getShortenedUrl(String url){

        HashMap<String,String> body = new HashMap<>();
        body.put("url",url);
        StringBuilder builder = new StringBuilder(urlShortnerHost);
        builder.append(urShortnerPath);
        String res = restTemplate.postForObject(builder.toString(), body, String.class);

        if(StringUtils.isEmpty(res)){
            log.error("URL_SHORTENING_ERROR", "Unable to shorten url: " + url); ;
            return url;
        }
        else return res;
    }


}