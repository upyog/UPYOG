package org.egov.web.error;

import org.egov.domain.model.OtpRequest;
import org.egov.web.contract.Error;
import org.egov.web.contract.ErrorField;
import org.egov.web.contract.ErrorResponse;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

public class OtpNumberTimeOutErrorAdapter implements ErrorAdapter<Void> {

    private static final String TOO_MANY_OTP_REQUEST = "TOO MANY OTP REQUEST";

	private static final String INVALID_OTP_REQUEST = "Invalid OTP request ..........";

    private static final String TENANT_MANDATORY_CODE = "OTP.TENANT_ID_MANDATORY";
    private static final String TENANT_MANDATORY_MESSAGE = "Tenant field is mandatory";
    private static final String TENANT_FIELD = "otp.tenantId";

    private static final String MOBILE_MANDATORY_CODE = "OTP.MOBILE_NUMBER_MANDATORY";
    private static final String MOBILE_MANDATORY_MESSAGE = "Mobile number field is mandatory";
    private static final String MOBILE_FIELD = "otp.mobileNumber";

	private static final String TYPE_INVALID_CODE = "OTP.REQUEST_TYPE_MANDATORY";
	private static final String TYPE_INVALID_MESSAGE = "Request type (register, passwordreset,login) is mandatory";
	private static final String TYPE_FIELD = "otp.type";
	
	private static final String MOBILE_INVALID_CODE = "OTP.MOBILE_NUMBER_INVALID";
	private static final String MOBILE_INVALID_MESSAGE = "Mobile number field should be numeric.";
	private static final String MOBILE_INVALID_FIELD = "otp.mobileNumber";

	private static final String MOBILE_INVALIDLENGTH_CODE = "OTP.MOBILE_NUMBER_INVALIDLENGTH";
	private static final String MOBILE_INVALIDLENGTH_MESSAGE = "Mobile number length should be min 10 and max 13 digits";
	private static final String MOBILE_INVALIDLENGTH_FIELD = "otp.mobileNumber";
	
	private static final String OTP_TIMED_OUT = "OTP.OTP_TIMED_OUT";
	private static final String OTP_TIMED_OUT_MESSAGE = "Please wait 60 secs to resend OTP ";

	@Override
	public ErrorResponse adapt(Void model) {
		final Error error = getError();
		return new ErrorResponse(null, error);
	}

    private Error getError() {
        List<ErrorField> errorFields = getErrorFields();
        return Error.builder()
        		.code(HttpStatus.BAD_REQUEST.value())
                .message(INVALID_OTP_REQUEST)
                .fields(errorFields)
                .build();
    }
    private List<ErrorField> getErrorFields() {
		List<ErrorField> errorFields = new ArrayList<>();
		final ErrorField latitudeErrorField = ErrorField.builder()
				.code(OTP_TIMED_OUT)
				.message(OTP_TIMED_OUT_MESSAGE)
				.field(TOO_MANY_OTP_REQUEST)
				.build();
		errorFields.add(latitudeErrorField);
		return errorFields;

    }

	
	

}
