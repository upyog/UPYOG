package org.egov.persistence.repository;

import lombok.extern.slf4j.Slf4j;

import org.egov.domain.exception.InvalidOtpRequestException;
import org.egov.domain.exception.OtpNumberNotPresentException;
import org.egov.domain.exception.OtpNumberTimeOutException;
import org.egov.domain.model.OtpRequest;
import org.egov.persistence.contract.OtpResponse;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class OtpRepository {

    private final String otpCreateUrl;
    private RestTemplate restTemplate;
    private final String otpExistChekUrl;
    

    public OtpRepository(RestTemplate restTemplate,
                         @Value("${otp.host}") String otpHost,
                         @Value("${otp.create.url}") String otpCreateUrl,
                         @Value("${otp.check.url}") String otpExistUrl) {
        this.restTemplate = restTemplate;
        this.otpCreateUrl = otpHost + otpCreateUrl;
        this.otpExistChekUrl=otpHost+otpExistUrl;
    }

    public String fetchOtp(OtpRequest otpRequest) {
        final org.egov.persistence.contract.OtpRequest request =
                new org.egov.persistence.contract.OtpRequest(otpRequest);
        try {
            final OtpResponse otpResponse =
                    restTemplate.postForObject(otpCreateUrl, request, OtpResponse.class);
            if (isOtpNumberAbsent(otpResponse)) {
                throw new OtpNumberNotPresentException();
            }
            return otpResponse.getOtpNumber();
        } catch (Exception e) {
            log.error("Exception while fetching OTP: ", e);
            throw new OtpNumberNotPresentException();
        }
    }
    
    
    public void checkOtpTime(OtpRequest otpRequest) {
        final org.egov.persistence.contract.OtpRequest request =
                new org.egov.persistence.contract.OtpRequest(otpRequest);
       
            final OtpResponse otpResponse =
                    restTemplate.postForObject(otpExistChekUrl, request, OtpResponse.class);
            if (!isOtpNumberAbsent(otpResponse)) {
                throw new OtpNumberTimeOutException();
            }
            //return otpResponse.getOtpNumber();
        
    }

    private boolean isOtpNumberAbsent(OtpResponse otpResponse) {
        return otpResponse == null || otpResponse.isOtpNumberAbsent();
    }
}
