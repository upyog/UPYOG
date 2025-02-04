package org.egov.pg;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.egov.common.utils.MultiStateInstanceUtil;
import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@SpringBootApplication
@Component
@Import({TracerConfiguration.class, MultiStateInstanceUtil.class})
public class Main {
	

	@Value("${app.timezone}")
	private String timeZone;

    public static void main(String[] args) {
        SpringApplication.run(Main.class);

    }
    
	@PostConstruct
	public void initialize() {
		TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
	}
}
