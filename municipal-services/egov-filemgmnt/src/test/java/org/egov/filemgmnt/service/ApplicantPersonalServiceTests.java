package org.egov.filemgmnt.service;

import java.util.List;
import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.filemgmnt.util.FMUtils;
import org.egov.filemgmnt.web.models.ApplicantPersonal;
import org.egov.filemgmnt.web.models.ApplicantPersonalRequest;
import org.egov.filemgmnt.web.models.ApplicantPersonalSearchCriteria;
import org.egov.filemgmnt.web.models.AuditDetails;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import lombok.extern.slf4j.Slf4j;

@Disabled
@SpringBootTest
//@Import(TestConfig.class)
@TestPropertySource(locations = { "classpath:test.properties" })
@SuppressWarnings({ "PMD.JUnitTestsShouldIncludeAssert" })
@Slf4j
class ApplicantPersonalServiceTests {

    @Autowired
    private ApplicantPersonalService service;

    @Test
    @Order(1)
    void create() {
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
                                                      .lastName("LastName")
                                                      .mobileNo("1234567890")
                                                      .auditDetails(new AuditDetails())
                                                      .build());
        service.create(request);
    }

    @Test
    @Order(2)
    void search() {
        List<ApplicantPersonal> result = service.search(ApplicantPersonalSearchCriteria.builder()
                                                                                       .build(),
                                                        null);
        result.forEach(personal -> log.info(" *** APPLICANT PERSONAL JSON \n {}", FMUtils.toJson(personal)));
    }
}
