package org.egov.domain.service;

import lombok.Getter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.egov.domain.exception.UserAlreadyExistInSystemException;
import org.egov.domain.exception.UserMobileNumberNotFoundException;
import org.egov.domain.exception.UserNotExistingInSystemException;
import org.egov.domain.exception.UserNotFoundException;
import org.egov.domain.model.OtpRequest;
import org.egov.domain.model.User;
import org.egov.persistence.repository.OtpEmailRepository;
import org.egov.persistence.repository.OtpRepository;
import org.egov.persistence.repository.OtpSMSRepository;
import org.egov.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Getter

public class OtpService {
	
	  @org.springframework.beans.factory.annotation.Value("${registration.otp.sms.template}")
	    private String registrationSmsTemplateId;

    private OtpRepository otpRepository;
    private OtpSMSRepository otpSMSSender;
    private OtpEmailRepository otpEmailRepository;
    private UserRepository userRepository;
    
  

    @Autowired
    public OtpService(OtpRepository otpRepository, OtpSMSRepository otpSMSSender, OtpEmailRepository otpEmailRepository,
                      UserRepository userRepository) {
        this.otpRepository = otpRepository;
        this.otpSMSSender = otpSMSSender;
        this.otpEmailRepository = otpEmailRepository;
        this.userRepository = userRepository;
    }

    public void sendOtp(OtpRequest otpRequest) {
    	
        otpRequest.validate();
        //check for timeer
        if (otpRequest.isRegistrationRequestType() || otpRequest.isLoginRequestType() || otpRequest.isOwnerValidate()) {
        	otpRequest.setTemplateId(registrationSmsTemplateId);
            sendOtpForUserRegistration(otpRequest);
        } else {
            sendOtpForPasswordReset(otpRequest);
        }
    }

    private void sendOtpForUserRegistration(OtpRequest otpRequest) {
        final User matchingUser = userRepository.fetchUser(otpRequest.getMobileNumber(), otpRequest.getTenantId(),
                otpRequest.getUserType());

        if (otpRequest.isRegistrationRequestType() && null != matchingUser)
            throw new UserAlreadyExistInSystemException();
        else if (otpRequest.isLoginRequestType() && null == matchingUser)
            throw new UserNotExistingInSystemException();
        
        otpRepository.checkOtpTime(otpRequest);
        
        final String otpNumber = otpRepository.fetchOtp(otpRequest);
        System.out.println("Phone::::"+otpRequest.getMobileNumber()+"-------------otpNumber::::"+otpNumber);
        otpSMSSender.sendNew(otpRequest, otpNumber);
    }

    private void sendOtpForPasswordReset(OtpRequest otpRequest) {
        final User matchingUser = userRepository.fetchUser(otpRequest.getMobileNumber(), otpRequest.getTenantId(),
                otpRequest.getUserType());
        if (null == matchingUser) {
            throw new UserNotFoundException();
        }
        if (null == matchingUser.getMobileNumber() || matchingUser.getMobileNumber().isEmpty())
            throw new UserMobileNumberNotFoundException();
        try {
        	 otpRepository.checkOtpTime(otpRequest);
            final String otpNumber = otpRepository.fetchOtp(otpRequest);
            otpRequest.setMobileNumber(matchingUser.getMobileNumber());
            otpRequest.setTemplateId(registrationSmsTemplateId);
            otpSMSSender.send(otpRequest, otpNumber);
            System.out.println("Phone::::"+otpRequest.getMobileNumber()+"-------------otpNumber::::"+otpNumber);
            otpEmailRepository.send(matchingUser.getEmail(), otpNumber);
        } catch (Exception e) {
            log.error("Exception while fetching otp: ", e);
        }
    }

}
