package org.upyog.chb.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import java.util.HashMap;

/**
 * This utility class provides functionality for shortening URLs in the
 * Community Hall Booking module.
 * 
 * Purpose:
 * - To shorten long URLs for use in notifications, messages, or other communications.
 * - To interact with an external URL shortening service for generating shortened URLs.
 * 
 * Dependencies:
 * - RestTemplate: Sends HTTP requests to the URL shortening service.
 * - urlShortnerHost: The base URL of the URL shortening service.
 * - urShortnerPath: The endpoint path for the URL shortening service.
 * 
 * Features:
 * - Sends a POST request to the URL shortening service with the original URL.
 * - Returns the shortened URL or the original URL in case of an error.
 * - Logs errors and exceptions for debugging and monitoring purposes.
 * 
 * Methods:
 * 1. getShortenedUrl:
 *    - Accepts a long URL as input.
 *    - Sends a request to the URL shortening service and returns the shortened URL.
 *    - Handles errors gracefully and logs them for debugging.
 * 
 * Usage:
 * - This class is used throughout the module to generate shortened URLs for notifications or communications.
 * - It ensures consistent and reusable logic for URL shortening across the application.
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