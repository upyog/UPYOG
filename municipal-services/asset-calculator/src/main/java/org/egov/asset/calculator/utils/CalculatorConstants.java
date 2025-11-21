package org.egov.asset.calculator.utils;

public class CalculatorConstants {




    private CalculatorConstants() {
	}

	public static final String INVALID_TENANT_ID_MDMS_KEY = "INVALID_TENANT_ID_MDMS_KEY";
	public static final String INVALID_TENANT_ID_MDMS_MSG = "INVALID_TENANT_ID_MDMS_MSG";
	public static final String INVALID_PROPERTY_TYPE = "INVALID_PROPERTY_TYPE";
	public static final  String SLM = "SLM";
	public static final  String DBM = "DBM";
	public static final String USER = "SYSTEM";
	public static final Long SECONDS_IN_A_DAY = 86400L;
	public static final Long MILLISECONDS_IN_A_SECOND = 1000L;



	public static final String MDMS_EGF_MASTER = "egf-master";

	public static final String MDMS_FINANCIALYEAR = "FinancialYear";

	public static final String MDMS_FINACIALYEAR_PATH = "$.MdmsRes.egf-master.FinancialYear[?(@.code==\"{}\")]";

	public static final String MDMS_STARTDATE = "startingDate";

	public static final String MDMS_ENDDATE = "endingDate";

	public static final String MDMS_CALCULATIONTYPE_FINANCIALYEAR_PATH = "$.MdmsRes.BPA.CalculationType[?(@.financialYear=='{}')]";

	public static final String APPLICATION_FEE = "APPLICATION_FEE";

	public static final String MODULE_CODE = "FSM";

	public static final String FSM_JSONPATH_CODE = "$.MdmsRes.FSM";

	// mdms master names

	public static final String PROPERTY_TYPE = "PropertyType";
	public static final String FSM_CONFIG = "Config";
	public static final String FSM_ADVANCEPAYMENT = "AdvancePayment";
	public static final String FSM_CANCELLATIONFEE = "CancellationFee";

	// Error messages in FSM Calculator
	public static final String PARSING_ERROR = "PARSING ERROR";
	public static final String PROCESSING_ERROR = "PROCESSING ERROR ";

	public static final String INVALID_PRICE = "INVALID PRICE";
	public static final String INVALID_PRAMS = "INVALID PRICE";


	public static final String SUCCESS_MESSAGE = "Depreciation calculated successfully ";
	public static final String CREATE = "CREATE";
	public static final String UPDATE = "UPDATE";
	public static final String SEARCH = "SEARCH";

	public static final String CODE = "code";
	public static final String ACTIVE = "active";


}
