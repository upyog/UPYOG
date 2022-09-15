package org.egov.filemgmnt.web.models;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class ApplicantPersonalTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void applicantPersonalJson() {
        ApplicantPersonal applicant = ApplicantPersonal.builder()
                                                       .id("87600")
                                                       .firstName("FirstName")
                                                       .build();
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        try {
            log.info(" *** APPLICANT PERSONAL JSON \n {}",
                     objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(applicant));
        } catch (JsonProcessingException e) {
            log.error("Json serialization failed", e);
        }
    }
}
