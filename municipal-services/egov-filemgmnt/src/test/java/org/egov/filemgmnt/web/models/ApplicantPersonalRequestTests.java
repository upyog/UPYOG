package org.egov.filemgmnt.web.models;

import java.util.List;
import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.filemgmnt.service.ApplicantPersonalService;
import org.egov.filemgmnt.util.CoreUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Disabled
@SpringBootTest
@ActiveProfiles({ "local" })
@SuppressWarnings({ "PMD.JUnitTestsShouldIncludeAssert" })
@Slf4j
class ApplicantPersonalRequestTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApplicantPersonalService service;

    @Test
    void requestJson() {
        ApplicantPersonalRequest request = ApplicantPersonalRequest.builder()
                                                                   .requestInfo(RequestInfo.builder()
                                                                                           .userInfo(User.builder()
                                                                                                         .uuid(UUID.randomUUID()
                                                                                                                   .toString())
                                                                                                         .build())
                                                                                           .build())
                                                                   .build();
        request.addApplicantPersonal(ApplicantPersonal.builder()
                                                      .id(UUID.randomUUID()
                                                              .toString())
                                                      .firstName("FirstName")
                                                      .auditDetails(new AuditDetails())
                                                      .build());
        // objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        try {
            log.info(" *** APPLICANT PERSONAL JSON \n {}",
                     objectMapper.writerWithDefaultPrettyPrinter()
                                 .writeValueAsString(request));
        } catch (JsonProcessingException e) {
            CoreUtils.ignore(e);
        }
    }

    @Test
    void search() {
        List<ApplicantPersonal> result = service.search(ApplicantPersonalSearchCriteria.builder()
                                                                                       .build());
        result.forEach(personal -> {
            try {
                log.info(" *** APPLICANT PERSONAL JSON \n {}",
                         objectMapper.writerWithDefaultPrettyPrinter()
                                     .writeValueAsString(personal));
            } catch (JsonProcessingException e) {
                CoreUtils.ignore(e);
            }
        });
    }
}
