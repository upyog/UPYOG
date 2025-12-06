package org.egov.inbox.util;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.egov.inbox.config.InboxConfiguration;
import org.egov.inbox.web.model.InboxSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class ElasticSearchUtil {
	
	@Autowired
    private InboxConfiguration config;

	 /**
     * Generates elasticsearch search url from application properties
     *
     * @return
     */
    private String getESURL(InboxSearchCriteria criteria) {

        StringBuilder builder = new StringBuilder(config.getIndexServiceHost());
        if (criteria.getProcessSearchCriteria().getModuleName().equals("ws-services"))
            builder.append(config.getEsWSIndex());
        else if (criteria.getProcessSearchCriteria().getModuleName().equals("sw-services")) {
            builder.append(config.getEsSWIndex());
        }
        builder.append(config.getIndexServiceHostSearchEndpoint());

        return builder.toString();
    }

    public HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", getESEncodedCredentials());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        headers.setAccept(mediaTypes);
        return headers;
    }

    public String getESEncodedCredentials() {
        String credentials = config.getUserName() + ":" + config.getPassword();
        byte[] credentialsBytes = credentials.getBytes();
        byte[] base64CredentialsBytes = Base64.getEncoder().encode(credentialsBytes);
        return "Basic " + new String(base64CredentialsBytes);
    }


}
