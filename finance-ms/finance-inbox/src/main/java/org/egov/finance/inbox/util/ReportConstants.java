/**
 * Created on May 30, 2025.
 * 
 * @author bikashdhal
 */
package org.egov.finance.inbox.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ReportConstants {

	private ReportConstants() {
		super();
	}

	public static final String REQUEST_INFO = "RequestInfo";
	public static final String REQUEST_TENANT_SPLIT_REGEX = "\\.";
	public static final String ORG_EGOV_FINANCE = "org.egov.finance.*";
	public static final String EGOV_PERSISTENCE_UNIT = "EgovPersistenceUnit";
	public static final String TRANSACTION_MANAGER = "transactionManager";
	public static final String ENTITY_MANAGER_FACTORY = "entityManagerFactory";
	public static final String NAME_IS_ALREADY_EXISTS_MSG = "Name is already exists";

	public static final String NAME_FUND_ALREADY_EXISTS_MSG = "Scheme with same name and fund already exists";

	public static final String CODE_IS_ALREADY_EXISTS_MSG = "Code is already exists";
	public static final String NAME_NOT_UNIQUE = "NAME_NOT_UNIQUE";
	public static final String DUPLICATE_SCHEME = "DUPLICATE_SCHEME";

	public static final String CODE_NOT_UNIQUE = "CODE_NOT_UNIQUE";
	public static final String ID_CANNOT_BE_PASSED_IN_CREATION_MSG = "Id cannot be passed in creation";
	public static final String INVALID_ID_PASSED = "INVALID_ID_PASSED";
	public static final String INVALID_ID_PASSED_MESSAGE = "Please pass correct id in case of update";
	public static final String INVALID_PARENT_ID = "INVALID_PARENT_ID";
	public static final String INVALID_PARENT_ID_MSG = "Please Provide a valid parent Parent Id";
	public static final String INVALID_SCHEME_ID = "INVALID_SCHEME_ID";
	public static final String INVALID_SCHEME_ID_MSG = "Please Provide a valid scheme Id";
	public static final String INVALID_TEXT_CONTAINS_HTML_TAGS_MSG = "Invalid Text, contains HTML Tags";
	public static final String FUND_SEARCH_REDIS_KEY_GENERATOR = "fundSearchKeyGenerator";
	public static final String SCHEME_SEARCH_REDIS_KEY_GENERATOR = "schemeSearchKeyGenerator";
	public static final String FUND_SEARCH_REDIS_CACHE_NAME = "fundSearchCache";
	public static final String SCHEME_SEARCH_REDIS_CACHE_NAME = "schemeSearchCache";
	public static final String FUND_SEARCH_REDIS_CACHE_VERSION_KEY = "fundSearchCacheVersion::";

	public static final String SCHEME_SEARCH_REDIS_CACHE_VERSION_KEY = "schemeSearchCacheVersion::";

	public static final String REDIS_SEARCH_VERSION_TAG = "::version=";

	public static final String REDIS_SEARCH_TENANT_TAG = "::tenant=";
	public static final String REDIS_START_VERSION_V0 = "v0";
	public static final String CODE_NAME_NOT_UNIQUE = "CODE_NAME_NOT_UNIQUE";
	public static final String CODE_NAME_NOT_UNIQUE_MSG = "Code Or Name Provided already exist ";

	public static final String INVALID_PARAMETERS = "INVALID_PARAMETERS";
	public static final String INVALID_PARAMETERS_MSG = "Invlaid Parameters Passed";
	public static final String EXCEPTION_FROM_REPORT_SERVICE_MSG = "Exception From report Service---- {}";
	public static final String INVALID_NAME = "INVALID_NAME";
	public static final String INVALID_NAME_MSG = "Please Provide a valid name";
	public static final String INVALID_CODE = "INVALID_CODE";
	public static final String INVALID_CODE_MSG = "Please Provide a valid code";

	public static final String INVALID_FUND = "INVALID_FUND";
	public static final String INVALID_FUND_ASSOCIATED_MSG = "Invalid fund associated with scheme.";

	public static final String FUNCTION_SEARCH_REDIS_KEY_GENERATOR = "functionSearchKeyGenerator";
	public static final String FUNCTION_SEARCH_REDIS_CACHE_VERSION_KEY = "fuctionSearchCacheVersion::";
	public static final String FUNCTION_SEARCH_REDIS_CACHE_NAME = "functionSearchCache";
	public static final String SUBSCHEME_SEARCH_REDIS_KEY_GENERATOR = "subschemeSearchKeyGenerator";
	public static final String SUBSCHEME_SEARCH_REDIS_CACHE_NAME = "subschemeSearchCache";
	public static final String SUBSCHEME_SEARCH_REDIS_CACHE_VERSION_KEY = "subschemeSearchCacheVersion::";
	public static final String CODE_SCHEMEID_NOT_UNIQUE = "CODE_SCHEMEID_NOT_UNIQUE";
	public static final String CODE_SCHEMEID_NOT_UNIQUE_MESSAGE = "Code and SchemeID already exists";

	public static final String INVALID_PARAMETERS_FILE_TYPE = "INVALID_PARAMETERS_FILE_TYPE";
	public static final String INVALID_PARAMETERS_FILE_TYPE_MSG = "Please Select a valid file type PDF/XLS";
	public static final String JASPER_REPORT_TEMPLATE_NOT_FOUND_AT_PATH = "JASPER_REPORT_TEMPLATE_NOT_FOUND_AT_PATH";
	public static final String JASPER_REPORT_TEMPLATE_NOT_FOUND_AT_PATH_MSG = "Jasper report template not found at path:";

	public static final String SUBSCHEMES = "subSchemes";
	public final static Integer INT_ZERO = 0;
	public final static Long LONG_ZERO = Long.valueOf(0);
	public final static Long LONG_ONE = Long.valueOf(1);
	public static final String SEARCH_CRITERIA_KEY = "searchCriteria";
	public static final String MODE = "mode";
	public static final String LIST = "list";
	public static final String DETAILLIST = "detailList";
	public static final String BUDGETS = "budgets";
	public static final String MODIFYLIST = "modifyList";
	public static final String APPROVE = "approve";
	public static final String MODIFY = "modify";
	public static final String ACTION = "action";
	public static final String ASONDATE = "asondate";
	public static final String FINANCIALYEARID = "financialyearid";
	public static final String SAVE_AND_NEW = "save_and_new";
	public static final String SAVED_DATA = "savedData";
	public static final String INPUT_STREAM = "inputStream";
	public static final String INPUT_NAME = "inputName";
	public static final String EGF = "EGF";
	public static final String CONTENT_TYPE = "contentType";
	public static final String CONTENT_DISPOSITION = "contentDisposition";
	public static final String DEPARTMENT = "department";
	public static final String FUND = "fund";
	public static final String SCHEME = "scheme";
	public static final String SUBSCHEME = "subscheme";
	public static final String FUNCTION = "function";
	public static final String FUNCTIONARY = "functionary";
	public static final String FUNDSOURCE = "fundsource";
	public static final String FIELD = "field";
	public static final String MASTER_DATA_DELETED = "This Entity Missing(Deleted) in Masters *";
	public static final String VOUCHERERRORMESSAGE = "Loaded with Errors refer the * ";
	public static final String GLCODE = "glcode";
	public static final String DEBITAMOUNT = "debitamount";
	public static final String CREDITAMOUNT = "creditamount";
	public static final String DETAILKEY = "detailkey";
	public static final String DETAILCODE = "detailcode";
	public static final String DETAILTYPE_NAME = "detailname";
	public static final Locale LOCALE = Locale.forLanguageTag("en-IN");
	public static final String BOUNDARY = "boundary";
	public static final String SUB_SCHEME = "subScheme";
	public static final String EXECUTING_DEPARTMENT = "executingDepartment";
	public static final String COMMONSMANAGERHOME = "CommonsServiceHome";

	public static final String DEPTID = "deptid";
	public static final String FUNDID = "fundid";
	public static final String SCHEMEID = "schemeid";
	public static final String SUBSCHEMEID = "subschemeid";
	public static final String FUNCTIONID = "functionid";
	public static final String FUNCTIONARYID = "functionaryid";
	public static final String BOUNDARYID = "boundaryid";
	public static final String BUDGET_GROUP = "budgetGroup";
	public static final String BUDGET = "budget";
	public static final String STATE = "state";
	public static final String TOTAL_ASSETS = "Total ASSETS";
	public static final String TOTAL_LIABILITIES = "Total LIABILITIES";
	public static final String TOTAL = "Total";
	public static final String LIABILITIES = "LIABILITIES";
	public static final String ASSETS = "ASSETS";
	public static final String RE = "RE";
	public static final String BE = "BE";
	public static final String ZERO = "0.0";
	public static final String BANK = "bank";
	public static final String CHEQUE = "cheque";
	public static final String RTGS = "rtgs";
	public static final String BANKENTRY = "Bank Entry";
	public static final String DATEFORMAT = "dd-MMM-yyyy";
	public static final String EGFCONFIGXML = "egf_config.xml";
	public static final String VOUCHERNUMBER = "voucherNumber";
	public static final String VOUCHERDATEFROM = "voucherDateFrom";
	public static final String VOUCHERDATETO = "voucherDateTo";
	public static final String VOUCHERHEADER = "voucherHeader";
	public static final String GLDEATILLIST = "glDetailList";
	public static final String VOUCHERDATE = "voucherDate";
	public static final String CREATED = "CREATED";

	public static final String DEPARTMENTID = "departmentId";

	public static final SimpleDateFormat DD_MON_YYYYFORMAT = new SimpleDateFormat("dd-MMM-yyyy", LOCALE);
	public static final SimpleDateFormat DDMMYYYYFORMAT1 = new SimpleDateFormat("dd-MMM-yyyy", LOCALE);
	public static final SimpleDateFormat DDMMYYYYFORMAT2 = new SimpleDateFormat("dd/MM/yyyy", LOCALE);
	public static final String END = "END";

	// These are the standard buttons used in transactions accrross the product
	public static final String SAVECLOSE = "Save & Close";
	public static final String SAVEVIEW = "Save & View";
	public static final String SAVENEW = "Save & New";

	public static final String EDIT = "edit";
	public static final String VIEW = "view";
	public static final String PURCHASE_BILL_PURPOSE_IDS = "purchaseBillPurposeIds";
	public static final String WORKS_BILL_PURPOSE_IDS = "worksBillPurposeIds";
	public static final String CONTINGENCY_BILL_PURPOSE_IDS = "contingencyBillPurposeIds";
	public static final String PENSION_BILL_PURPOSE_IDS = "pensionBillPurposeIds";
	public static final String CHEQUE_NO_GENERATION_APPCONFIG_KEY = "Cheque_no_generation_auto";
	public static final String INCOME = "INCOME";
	public static final String EXPENDITURE = "EXPENDITURE";
	public static final String TOTAL_INCOME = "Total INCOME";
	public static final String TOTAL_EXPENDITURE = "Total EXPENDITURE";
	public static final String ADVANCE_PAYMENT = "Advance Payment";
	public static final String MODULEID = "module id";
	public static final String CONSUMEORRELEASE = "consume or release";
	public static final String REFERENCENUMBER = "reference number";
	public static final String BUDGETHEAD = "budget heed";
	public static final String AMOUNT = "amount";
	public static final String APPROPRIATIONNUMBER = "budget appr number";
	public static final String RECOVERY = "recovery";
	public static final String PERIOD_FIRSTHALF = "I-Half";
	public static final String PERIOD_SECONDHALF = "II-Half";
	public static final String PERIOD_QUARTER1 = "Quarter-I";
	public static final String PERIOD_QUARTER2 = "Quarter-II";
	public static final String PERIOD_QUARTER3 = "Quarter-III";
	public static final String PERIOD_QUARTER4 = "Quarter-IV";
	public static final String PERIOD_MONTH1 = "Jan";
	public static final String PERIOD_MONTH2 = "Feb";
	public static final String PERIOD_MONTH3 = "Mar";
	public static final String PERIOD_MONTH4 = "Apr";
	public static final String PERIOD_MONTH5 = "May";
	public static final String PERIOD_MONTH6 = "Jun";
	public static final String PERIOD_MONTH7 = "Jul";
	public static final String PERIOD_MONTH8 = "Aug";
	public static final String PERIOD_MONTH9 = "Sep";
	public static final String PERIOD_MONTH10 = "Oct";
	public static final String PERIOD_MONTH11 = "Nov";
	public static final String PERIOD_MONTH12 = "Dec";
	public static final String GRANT_TYPE_SFC = "State Finance Commission";
	public static final String GRANT_TYPE_CFC = "Central Finance Commission";
	public static final String GRANT_TYPE_ET = "Entertainment Tax";
	public static final String GRANT_TYPE_SD = "Stamp Duty";

	public static final String VIEW_COA = "view coa";
	public static final String VIEW_MODIFY_COA = "view-modify coa";

	public static final String INCOME_TYPE = "Income";
	public static final String EXPENSE_TYPE = "Expense";
	public static final String LIABILITY_TYPE = "Liability";
	public static final String ASSET_TYPE = "Asset";
	
	public static final String BUDGETHEADID = "budgetheadid";
	public static final String BUDGETARY_CHECK_GROUPBY_VALUES = "budgetaryCheck_groupby_values";
	public static final String GLCODEID = "glcodeid";

}
