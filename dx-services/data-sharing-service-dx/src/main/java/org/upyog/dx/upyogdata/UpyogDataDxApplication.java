package org.upyog.dx.upyogdata;

import org.egov.tracer.config.TracerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * UPYOG Data Sharing Service.
 * <p>
 * {@link TracerConfiguration} is imported so Feign clients get correlation-id headers and
 * request/response logs (parity with RestTemplate). Kafka audit of PFMS calls is separate:
 * {@link org.upyog.externalaudit.service.ExternalApiAuditLogger} is auto-configured from
 * {@code external-api-audit} when {@code KafkaTemplate} is present. See
 * {@code core-services/libraries/external-api-audit/README.md}.
 * </p>
 */
@EnableFeignClients(basePackages = "org.upyog.dx.upyogdata.pfms.client")
@EnableScheduling
@SpringBootApplication
@Import(TracerConfiguration.class)
public class UpyogDataDxApplication {

    public static void main(String[] args) {
        SpringApplication.run(UpyogDataDxApplication.class, args);
    }
}
