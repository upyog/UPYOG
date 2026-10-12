package org.egov.garbageservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.egov.tracer.config.TracerConfiguration;
import org.springframework.kafka.annotation.*;



@SpringBootApplication
@Import({ TracerConfiguration.class })
@EnableKafka
public class HpGarbageServiceApplication {

	@Value("${app.timezone}")
    private String timeZone;

	@Value("${rest.template.connect.timeout.ms:5000}")
	private int restTemplateConnectTimeoutMs;

	@Value("${rest.template.read.timeout.ms:30000}")
	private int restTemplateReadTimeoutMs;

	public static void main(String[] args) {
		SpringApplication.run(HpGarbageServiceApplication.class, args);
	}

	@Bean
    public RestTemplate restTemplate() {
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setConnectTimeout(restTemplateConnectTimeoutMs);
		requestFactory.setReadTimeout(restTemplateReadTimeoutMs);
        return new RestTemplate(requestFactory);
    }
}
