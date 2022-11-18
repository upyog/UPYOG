package org.egov.filemgmnt.web.models;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.assertj.core.api.Assertions;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.filemgmnt.TestConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Import(TestConfig.class)
@TestPropertySource(locations = { "classpath:test.properties" })
@SuppressWarnings({ "PMD.JUnitTestsShouldIncludeAssert" })
@Slf4j
class ApplicantPersonalRequestTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Disabled
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
                                                      .serviceDetails(new ServiceDetails())
                                                      .applicantAddress(new ApplicantAddress())
                                                      .applicantServiceDocuments(new ApplicantServiceDocuments())
                                                      .applicantDocuments(new ApplicantDocuments())
                                                      .fileDetail(new FileDetail())
                                                      .auditDetails(new AuditDetails())
                                                      .build());
        try {
            log.info(" *** APPLICANT PERSONAL JSON \n {}",
                     objectMapper.writerWithDefaultPrettyPrinter()
                                 .writeValueAsString(request));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Disabled
    @ParameterizedTest
    @MethodSource("validateArguments")
    void validateApplicantPersonalRequest(Validator validator, ApplicantPersonalRequest request) {
        request.addApplicantPersonal(ApplicantPersonal.builder()
                                                      .id(UUID.randomUUID()
                                                              .toString())
                                                      .firstName("FirstName")
                                                      .lastName("LastName")
                                                      .mobileNo("9446903827")
                                                      .tenantId("kl")
                                                      .aadhaarNo("123456789123")
                                                      .email("demo@gmail.com")
//                                                      .serviceDetails(new ServiceDetails())
//                                                      .applicantAddress(new ApplicantAddress())
//                                                      .applicantServiceDocuments(new ApplicantServiceDocuments())
//                                                      .applicantDocuments(new ApplicantDocuments())
//                                                      .fileDetail(new FileDetail())
//                                                      .auditDetails(new AuditDetails())
                                                      .build());

        Set<ConstraintViolation<ApplicantPersonalRequest>> constraintViolations = validator.validate(request);
        constraintViolations.forEach(System.out::println);

        Assertions.assertThat(constraintViolations)
                  .describedAs("Applicant personal request")
                  .isEmpty();
    }

    static Stream<Arguments> validateArguments() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

        ApplicantPersonalRequest request = ApplicantPersonalRequest.builder()
                                                                   .requestInfo(RequestInfo.builder()
                                                                                           .userInfo(User.builder()
                                                                                                         .uuid(UUID.randomUUID()
                                                                                                                   .toString())
                                                                                                         .build())
                                                                                           .build())
                                                                   .build();

        return Stream.of(Arguments.of(validatorFactory.getValidator(), request));
    }
}
