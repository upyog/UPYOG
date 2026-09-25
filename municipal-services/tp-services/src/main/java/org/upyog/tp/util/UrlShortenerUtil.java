package org.upyog.tp.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.upyog.tp.config.TreePruningConfiguration;

import java.util.HashMap;

@Slf4j
@Component
public class UrlShortenerUtil {

    private final RestTemplate restTemplate;
    private final TreePruningConfiguration config;

    public UrlShortenerUtil(RestTemplate restTemplate, TreePruningConfiguration config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }

    public String getShortenedUrl(String url){

        HashMap<String,String> body = new HashMap<>();
        body.put("url",url);
        StringBuilder builder = new StringBuilder(config.getUrlShortnerHost());
        builder.append(config.getUrShortnerPath());
        String res = restTemplate.postForObject(builder.toString(), body, String.class);

        if(StringUtils.isEmpty(res)){
            log.error("URL_SHORTENING_ERROR", "Unable to shorten url: " + url);
            return url;
        }
        return res;
    }


}
