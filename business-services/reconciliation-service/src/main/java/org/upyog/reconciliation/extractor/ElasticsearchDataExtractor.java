package org.upyog.reconciliation.extractor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import lombok.extern.slf4j.Slf4j;

@Service("ELASTICSEARCH")
@Slf4j
public class ElasticsearchDataExtractor implements MetricDataExtractor {


    @Value("${egov.indexer.host}")
    private String esHost;

    @Value("${egov.indexer.username:}")
    private String esUsername;

    @Value("${egov.indexer.password:}")
    private String esPassword;

    private RestTemplate restTemplate;

    @PostConstruct
    public void init() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                public void checkServerTrusted(X509Certificate[] certs, String authType) { }
            }
        };
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

        this.restTemplate = new RestTemplate(new SimpleClientHttpRequestFactory());
        
        if (esUsername != null && !esUsername.isEmpty()) {
            this.restTemplate.getInterceptors().add(
                new org.springframework.http.client.support.BasicAuthenticationInterceptor(esUsername, esPassword)
            );
        }
    }

    @Autowired
    private org.springframework.core.env.Environment env;

    @Override
    public List<Map<String, Object>> extractData(String tenantId, String moduleName, LocalDate date) {
        log.info("Fetching data from Elasticsearch for module: {}", moduleName);
        List<Map<String, Object>> result = new ArrayList<>();

        // Dynamically resolve index name based on moduleName from properties
        String indexName = env.getProperty("reconciliation.es.index." + moduleName.toLowerCase());

        if (indexName == null || indexName.isEmpty()) {
            log.warn("No ES extraction index defined in properties for module: {}", moduleName);
            return result;
        }

        String url = esHost + "/" + indexName + "/_search";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Constructing a query, can be enhanced with date and tenant filters
        String requestBody = "{ \"query\": { \"match_all\": {} } }";

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            if (response.getBody() != null && response.getBody().containsKey("hits")) {
                Map<String, Object> hitsMap = (Map<String, Object>) response.getBody().get("hits");
                if (hitsMap.containsKey("hits")) {
                    List<Map<String, Object>> hitsList = (List<Map<String, Object>>) hitsMap.get("hits");
                    for (Map<String, Object> hit : hitsList) {
                        if (hit.containsKey("_source")) {
                            result.add((Map<String, Object>) hit.get("_source"));
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error fetching data from ES for {}: {}", moduleName, e.getMessage(), e);
        }

        return result;
    }
}
