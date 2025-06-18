/**
 * Created on May 30, 2025.
 * 
 * @author bikashdhal
 */
package org.egov.finance.report.util;

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

}
