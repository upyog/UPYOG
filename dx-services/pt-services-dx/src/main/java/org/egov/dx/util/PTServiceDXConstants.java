package org.egov.dx.util;

public class PTServiceDXConstants {

	private PTServiceDXConstants() {

	}
	
	public static final String PROPERTY_TAX_SERVICE_CODE = "PT";

	public static final String TENANT_ID_FIELD_FOR_SEARCH_URL = "tenantId=";

	public static final String SERVICE_FIELD_FOR_SEARCH_URL = "service=";
	
	public static final String BUSINESSSERVICE_FIELD_FOR_SEARCH_URL = "businessService=";

	public static final String BUSINESSSERVICES_FIELD_FOR_SEARCH_URL = "businessServices=";

	public static final String RECEIPTNUMBER_FIELD_FOR_SEARCH_URL = "receiptNumbers=";

	public static final String SERVICE_FIELD_VALUE_PT = "PT";
	
	public static final String BUSINESSSERVICE_FIELD_FOR_FILESTORE_SEARCH_URL="bussinessService=";

	public static final String SERVICE_FIELD_VALUE_PT_MUTATION = "PT.MUTATION";

	public static final String URL_PARAMS_SEPARATER = "?";

	public static final String SEPARATER = "&";

	public static final String CONSUMER_CODE_SEARCH_FIELD_NAME_PAYMENT = "consumerCodes=";
	
	public static final String KEY = "key=";
	
	public static final String PDF_KEY_PT = "property-receipt";
	
	public static final String STATE_TENANT = "pg";




	//public static final String DIGILOCKER_ISSUER_ID = "upyog.niua.org";
	
	//public static final String ORIGIN="https://apisetu.gov.in";
	public static final String ORIGIN="https://partners.digitallocker.gov.in";
	
	public static final String DIGILOCKER_ISSUER_ID = "in.niua";
	
	public static final String DIGILOCKER_DOCTYPE = "PRTAX";

	public static final String DIGILOCKER_NO_DATA_FOUND = "NO_DATA_FOUND";
	
	public static final String DIGILOCKER_DOCTYPE_NOT_SUPPORTED = "DOCTYPE_NOT_SUPPORTED";
	
	public static final String DIGILOCKER_ORIGIN_NOT_SUPPORTED = "REQUEST IS NOT FROM SUPPORTED ORIGIN";

	public static final String EXCEPTION_TEXT_VALIDATION = "Either no payment found or  input payer name/mobile is not matching with latest payment details of this property id";

	/*
	 * exceptions
	 */
	public static final String CONNECT_EXCEPTION_KEY = "CONNECTION_FAILED";

	}
