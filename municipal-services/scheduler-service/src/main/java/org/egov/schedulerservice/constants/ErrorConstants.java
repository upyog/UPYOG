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

	public static final String ERR_USER_SERVICE_ERROR = "ERR_USER_SERVICE_ERROR";
	public static final String ERR_USER_SERVICE_ERROR_MSG = "Error occured in user service.";

	public static final String ERR_GARBAGE_SERVICE_ERROR = "ERR_GARBAGE_SERVICE_ERROR";
	public static final String ERR_GARBAGE_SERVICE_ERROR_MSG = "Error occured in garbage service.";

	public static final String ERR_BILL_SERVICE_ERROR = "ERR_BILL_SERVICE_ERROR";
	public static final String ERR_BILL_SERVICE_ERROR_MSG = "Error occured in bill service.";

	public static final String ERR_PROPERTY_SERVICE_ERROR = "ERR_PROPERTY_SERVICE_ERROR";
	public static final String ERR_PROPERTY_SERVICE_ERROR_MSG = "Error occured in property service.";

	public static final String ERR_PGR_SERVICE_ERROR = "ERR_PGR_SERVICE_ERROR";
	public static final String ERR_PGR_SERVICE_ERROR_MSG = "Error occured in PGR service.";

	public static final String ERR_PG_SERVICE_ERROR = "ERR_PG_SERVICE_ERROR";
	public static final String ERR_PG_SERVICE_ERROR_MSG = "Error occured in pg service.";

	public static final String ERR_ADRVCANOPY_SITE_BOOKING_SERVICE_ERROR = "ERR_ADRVCANOPY_SITE_BOOKING_SERVICE_ERROR";
	public static final String ERR_ADRVCANOPY_SITE_BOOKING_SERVICE_ERROR_MSG = "Error occured in adrvcanopy site booking service.";

	public static final String ERR_TL_SERVICE_ERROR = "ERR_TL_SERVICE_ERROR";
	public static final String ERR_TL_SERVICE_ERROR_MSG = "Error occured in tl service.";

}
