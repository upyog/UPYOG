package org.upyog.cdwm.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Slf4j
@Component
public class UrlShortenerUtil {

    private final RestTemplate restTemplate;

    private final String urlShortnerHost;

    private final String urShortnerPath;

    public UrlShortenerUtil(RestTemplate restTemplate,
            @Value("${egov.url.shortner.host}") String urlShortnerHost,
            @Value("${egov.url.shortner.endpoint}") String urShortnerPath) {
        this.restTemplate = restTemplate;
        this.urlShortnerHost = urlShortnerHost;
        this.urShortnerPath = urShortnerPath;
    }

    public String getShortenedUrl(String url){

        HashMap<String,String> body = new HashMap<>();
        body.put("url",url);
        StringBuilder builder = new StringBuilder(urlShortnerHost);
        builder.append(urShortnerPath);
        String res = restTemplate.postForObject(builder.toString(), body, String.class);

        if(StringUtils.isEmpty(res)){
            log.error("URL_SHORTENING_ERROR", "Unable to shorten url: " + url);
            return url;
        }
        else return res;
    }


}
