/**
 * Created on May 30, 2025.
 * 
 * @author bikashdhal
 */
package org.egov.finance.master.util;

public class MasterConstants {
	
	private MasterConstants() {
		super();
	}
	public static final String REQUEST_INFO = "RequestInfo";
	public static final String REQUEST_TENANT_SPLIT_REGEX = "\\.";
	public static final String ORG_EGOV_FINANCE = "org.egov.finance.*";
	public static final String EGOV_PERSISTENCE_UNIT = "EgovPersistenceUnit";
	public static final String TRANSACTION_MANAGER = "transactionManager";
	public static final String ENTITY_MANAGER_FACTORY = "entityManagerFactory";
	public static final String NAME_IS_ALREADY_EXISTS_MSG = "Name is already exists";
	public static final String CODE_IS_ALREADY_EXISTS_MSG = "Code is already exists";
	public static final String NAME_NOT_UNIQUE = "NAME_NOT_UNIQUE";
	public static final String CODE_NOT_UNIQUE = "CODE_NOT_UNIQUE";
	public static final String ID_CANNOT_BE_PASSED_IN_CREATION_MSG = "Id cannot be passed in creation";
	public static final String INVALID_ID_PASSED = "INVALID_ID_PASSED";
	public static final String INVALID_ID_PASSED_MESSAGE = "Please pass correct id in case of update";
	public static final String INVALID_PARENT_ID = "INVALID_PARENT_ID";
	public static final String INVALID_PARENT_ID_MSG = "Please Provide a valid parent Parent Id";
	public static final String INVALID_TEXT_CONTAINS_HTML_TAGS_MSG = "Invalid Text, contains HTML Tags";
	public static final String FUND_SEARCH_REDIS_KEY_GENERATOR = "fundSearchKeyGenerator";
	public static final String FUND_SEARCH_REDIS_CACHE_NAME = "fundSearchCache";
	public static final String FUND_SEARCH_REDIS_CACHE_VERSION_KEY = "fundSearchCacheVersion::";
	public static final String REDIS_SEARCH_VERSION_TAG= "::version=";
	public static final String REDIS_SEARCH_TENANT_TAG = "::tenant=";
	public static final String REDIS_START_VERSION_V1 = "v1";
	public static final String CODE_NAME_NOT_UNIQUE = "CODE_NAME_NOT_UNIQUE";
	public static final String CODE_NAME_NOT_UNIQUE_MSG = "Code Or Name Provided already exist ";
	public static final String INVALID_PARAMETERS = "INVALID_PARAMETERS";
	public static final String INVALID_PARAMETERS_MSG="Invlaid Parameters Passed";

}
