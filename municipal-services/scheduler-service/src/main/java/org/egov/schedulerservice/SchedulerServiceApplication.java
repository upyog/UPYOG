package org.egov.schedulerservice;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.egov.common.utils.MultiStateInstanceUtil;
import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
@EnableScheduling
@Import({ TracerConfiguration.class, MultiStateInstanceUtil.class })
public class SchedulerServiceApplication {

	@Value("${app.timezone}")
	private String timeZone;

	public static void main(String[] args) {
		SpringApplication.run(SchedulerServiceApplication.class, args);
	}

	@Bean
	ObjectMapper objectMapper() {
		return new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@PostConstruct
	public void initialize() {
		TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
	}

}
