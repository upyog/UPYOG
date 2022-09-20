package org.egov.filemgmnt.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.filemgmnt.web.models.ApplicantPersonalRequest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Disabled
//@SpringBootTest
class ApplicantPersonalServiceTests {

    @Autowired
    private ApplicantPersonalService service;

    @Test
    void createApplicantPersonal() {

        ApplicantPersonalRequest request = new ApplicantPersonalRequest();
        request.setRequestInfo(new RequestInfo());
//        request.addApplicantPersonal(ApplicantPersonal.builder().aadhaarNo("123456789").build());
//        request.addApplicantPersonal(ApplicantPersonal.builder().email("priyamalu@gmail.com").build());
//        request.addApplicantPersonal(ApplicantPersonal.builder().firstName("Krishna").build());
//        request.addApplicantPersonal(ApplicantPersonal.builder().lastName("Priya").build());
//        request.addApplicantPersonal(ApplicantPersonal.builder().title("Smt").build());
//        request.addApplicantPersonal(ApplicantPersonal.builder().mobileNo("9746402315").build());
//        request.addApplicantPersonal(ApplicantPersonal.builder().tenantId("1").build());
//        request.addApplicantPersonal(ApplicantPersonal.builder().createdBy("365").build());
//        request.addApplicantPersonal(ApplicantPersonal.builder().createdAt(12/03/2009).build());
//        request.addApplicantPersonal(ApplicantPersonal.builder().lastmodifiedBy("10").build());
//        request.addApplicantPersonal(ApplicantPersonal.builder().lastmodifiedAt(21/11/2020).build());
//        request.addApplicantPersonal(ApplicantPersonal.builder().applicantaddressId("214").build());
// 

//        request.addApplicantPersonal(ApplicantPersonal.builder()
//                                                      .aadhaarNo("123456789")
//                                                      .email("priyamalu@gmail.com")
//                                                      .firstName("Krishna")
//                                                      .lastName("Priya")
//                                                      .title("Smt")
//                                                      .mobileNo("9746402315")
//                                                      .tenantId("1")
//                                                      .createdBy("365")
//                                                      .createdAt(12032009)
//                                                      .lastmodifiedBy("10")
//                                                      .lastmodifiedAt(21112020)
//                                                      .applicantaddressId("214")
//
//                                                      .build());
//
//        service.createApplicantPersonals(request);
    }
}
