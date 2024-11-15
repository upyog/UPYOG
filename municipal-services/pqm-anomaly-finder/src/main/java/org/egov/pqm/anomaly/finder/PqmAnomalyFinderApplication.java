package org.egov.pqm.anomaly.finder;

import org.egov.tracer.config.TracerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import({TracerConfiguration.class})
@SpringBootApplication
public class PqmAnomalyFinderApplication {

  public static void main(String[] args) {
    SpringApplication.run(PqmAnomalyFinderApplication.class, args);
  }
}
