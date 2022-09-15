package org.egov.filemgmnt.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.filemgmnt.TestConfiguration;
import org.egov.filemgmnt.web.models.ApplicantPersonal;
import org.egov.filemgmnt.web.models.ApplicantPersonalRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import({ TestConfiguration.class })
public class ApplicantPersonalServiceTests {

    @Autowired
    private ApplicantPersonalService service;

    @Test
    void createApplicantPersonal() {

        ApplicantPersonalRequest request = new ApplicantPersonalRequest();
        request.setRequestInfo(new RequestInfo());
        request.addApplicantPersonal(ApplicantPersonal.builder().firstName("Gayathri").build());
        request.addApplicantPersonal(ApplicantPersonal.builder().firstName("Tarun").build());

        service.create(request);
    }
}
