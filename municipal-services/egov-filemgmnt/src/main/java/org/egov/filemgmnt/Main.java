package org.egov.filemgmnt;

import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import lombok.extern.slf4j.Slf4j;

@Import({ TracerConfiguration.class })
@SpringBootApplication
@ComponentScan(basePackages = { "org.egov.filemgmnt" })
@Slf4j
public class Main { // NOPMD

    public static void main(String[] args) { // NOPMD
        SpringApplication.run(Main.class, args);
    }

    @Autowired
    @Bean
    public ApplicationRunner debugJdbcUrl(Environment env) {
        return args -> {
            log.info("JDBC Url = {}", env.getProperty("spring.datasource.url"));
        };
    }

}
