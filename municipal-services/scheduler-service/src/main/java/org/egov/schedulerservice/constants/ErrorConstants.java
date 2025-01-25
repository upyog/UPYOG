package org.egov.schedulerservice.constants;

import org.springframework.stereotype.Component;

@Component
public class ErrorConstants {

	public static final String ERR_INPUT_VALIDATION = "ERR_INPUT_VALIDATION";
	public static final String ERR_INPUT_VALIDATION_MSG = "Input Invalid.";

	public static final String ERR_TECHNICAL = "ERR_TECHNICAL";
	public static final String ERR_TECHNICAL_MSG = "Internal server error occurred.";

	public static final String ERR_HTTP_CLIENT = "ERR_HTTP_CLIENT";
	public static final String ERR_HTTP_CLIENT_MSG = "HTTP Client error occured.";

	public static final String ERR_GARBAGE_SERVICE_ERROR = "ERR_GARBAGE_SERVICE_ERROR";
	public static final String ERR_GARBAGE_SERVICE_ERROR_MSG = "Error occured in garbage service.";

	public static final String ERR_BILL_SERVICE_ERROR = "ERR_BILL_SERVICE_ERROR";
	public static final String ERR_BILL_SERVICE_ERROR_MSG = "Error occured in bill service.";

}
