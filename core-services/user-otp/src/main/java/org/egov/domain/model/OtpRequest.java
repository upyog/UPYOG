package org.egov.domain.model;

import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.egov.domain.exception.InvalidOtpRequestException;

import static org.springframework.util.StringUtils.isEmpty;

@Getter
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@NoArgsConstructor
public class OtpRequest {
	@Setter
    private String mobileNumber;
	@Setter
	private String emailId;
    private String tenantId;
    private OtpRequestType type;
    private String userType;
    private Boolean isThirdParty;
    public void validate() {
        if(isTenantIdAbsent()
				|| isMobileNumberAbsent()
				|| isInvalidType()
				|| isMobileNumberNumeric()
				|| isMobileNumberValidLength()
				|| isEmailValid()) {
            throw new InvalidOtpRequestException(this);
        }
    }

	public boolean isMobileNumberNumeric() {
		// TODO Auto-generated method stub
		if(!(type!=null && type.toString().equalsIgnoreCase(OtpRequestType.PASSWORD_RESET.toString())))
		return !StringUtils.isNumeric(mobileNumber);
		return false;
	}

	public boolean isMobileNumberValidLength() {
		// TODO Auto-generated method stub
		if(!(type!=null && type.toString().equalsIgnoreCase(OtpRequestType.PASSWORD_RESET.toString())))
		return !(mobileNumber != null && mobileNumber.matches("^[0-9]{10,13}$"));
		return false;
	}
    
	public boolean isEmailValid() {

	    // Email is optional
	    if (emailId == null || emailId.trim().isEmpty()) {
	        return false;
	    }

	    // return TRUE only when invalid
	    return !emailId.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
	}

	public boolean isRegistrationRequestType() {
    	return OtpRequestType.REGISTER.equals(getType());
	}
	
	public boolean isLoginRequestType() {
    	return OtpRequestType.LOGIN.equals(getType());
	}

	public boolean isInvalidType() {
    	return isEmpty(type);
	}

	public boolean isTenantIdAbsent() {
        return isEmpty(tenantId);
    }

    public boolean isMobileNumberAbsent() {
        return isEmpty(mobileNumber);
    }
}
