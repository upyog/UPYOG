package org.egov.user.persistence.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.egov.user.Resources;
import org.egov.user.domain.model.OtpValidationRequest;
import org.egov.user.web.contract.Otp;
import org.egov.user.web.contract.OtpValidateRequest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
public class OtpRepositoryTest {

    private OtpRepository otpRepository;
    private MockRestServiceServer server;

    @Before
    public void setUp() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).build();
        final String otpSearchContext = "otp/_search";
        final String otpValidateContext = "otp/_validate";
        final String otpHost = "http://otp-host.com/";
        this.otpRepository = new OtpRepository(otpHost, otpSearchContext, otpValidateContext, restTemplate);
    }

    

    @Test
    @Ignore
    public void testShouldReturnTrueWhenOtpInValidated() throws Exception {
        server.expect(once(), requestTo("http://otp-host.com/otp/_validate")).andExpect(method(HttpMethod.POST))
                .andExpect(content().string(new Resources().getFileContents("otpValidationRequest.json")))
                .andRespond(withSuccess(new Resources().getFileContents("otpNonValidateResponse.json"),
                        MediaType.APPLICATION_JSON_UTF8));
        final OtpValidateRequest request = buildValidateRequest();

        boolean isOtpValidated = otpRepository.validateOtp(request);

        server.verify();
        assertEquals(Boolean.FALSE, isOtpValidated);
    }

    @Test
    @Ignore
    public void testShouldReturnTrueWhenOtpValidated() throws Exception {
        server.expect(once(), requestTo("http://otp-host.com/otp/_validate")).andExpect(method(HttpMethod.POST))
                .andExpect(content().string(new Resources().getFileContents("otpValidationRequest.json")))
                .andRespond(withSuccess(new Resources().getFileContents("otpValidateResponse.json"),
                        MediaType.APPLICATION_JSON_UTF8));
        final OtpValidateRequest request = buildValidateRequest();

        boolean isOtpValidated = otpRepository.validateOtp(request);

        server.verify();
        assertEquals(Boolean.TRUE, isOtpValidated);
    }


   

    

    private OtpValidationRequest buildRequest() {
        return OtpValidationRequest.builder().otpReference("2b936aae-c3b6-4c89-b3b3-a098cdcbb706")
                .mobileNumber("9988776655").tenantId("tenantId").build();
    }

    private OtpValidateRequest buildValidateRequest() {
        Otp otp = Otp.builder().otp("1234").tenantId("default").identity("9988776655").build();

        return OtpValidateRequest.builder().otp(otp).build();
    }

}
