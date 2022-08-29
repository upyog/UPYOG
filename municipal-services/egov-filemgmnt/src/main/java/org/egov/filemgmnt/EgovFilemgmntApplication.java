package org.egov.filemgmnt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class EgovFilemgmntApplication {

    public static void main(String[] args) {
        SpringApplication.run(EgovFilemgmntApplication.class, args);
    }

}
