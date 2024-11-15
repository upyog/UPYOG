package org.egov.pqm.util;

import java.util.regex.Pattern;
import org.springframework.stereotype.Component;


@Component
public class Constants {

  //MDMS constants
  public static final String MODULE_NAME = "TQM";

  public static final String MASTER_NAME_TESTING_STANDARD = "PQM.TestingStandard";

  public static final String MASTER_NAME_QUALITY_CRITERIA = "PQM.QualityCriteria";

  public static final String QUALITY_CRITERIA_NOT_PRESENT = "TestCriteria_Not_present";

  public static final String MASTER_NAME_BENCHMARK_RULES = "BenchmarkRule";

  public static final String FSTPO_EMPLOYEE = "FSM_EMP_FSTPO";

  public static final String PQM_MODULE_NAME = "PQM";

  public static final String PQM_BUSINESS_SERVICE = "PQM";

  //Benchmark Rule Constants

  public static final String GREATER_THAN = "GTR";

  public static final String LESS_THAN = "LST";

  public static final String BETWEEN = "BTW";

  public static final String OUTSIDE_RANGE = "OSD";

  public static final String EQUALS = "EQ";

  public static final String NOT_EQUAL = "NEQ";

  public static final String GREATER_THAN_EQUAL_TO = "GTROREQ";

  public static final String LESS_THAN_EQUAL_TO = "LSTOREQ";

  public static final String TENANT_ID_KEY = "tenantId";

  // Workflow Constants

  public static final String BUSINESS_SERVICE_KEY = "businessService";

  public static final String ACTION_KEY = "action";

  public static final String COMMENT_KEY = "comment";

  public static final String RATING = "rating";

  public static final String MODULE_NAME_KEY = "moduleName";

  public static final String BUSINESS_ID_KEY = "businessId";

  public static final String DOCUMENTS_KEY = "documents";

  public static final String ASSIGNEE_KEY = "assignes";

  public static final String MODULE_NAME_VALUE = "pqm";

  public static final String UUID_KEY = "uuid";

  public static final String WORKFLOW_REQUEST_ARRAY_KEY = "ProcessInstances";

  public static final String REQUEST_INFO_KEY = "RequestInfo";

  public static final String PROCESS_INSTANCES_JOSN_KEY = "$.ProcessInstances";

  public static final String BUSINESS_ID_JOSN_KEY = "$.businessId";

  public static final String STATUS_JSON_KEY = "$.state.applicationStatus";

  public static final String UPDATE_RESULT = "UPDATE_RESULT";
  public static final String SUBMIT_SAMPLE="SUBMIT_SAMPLE";
  public static final String SAVE_AS_DRAFT = "SAVE_AS_DRAFT";
  public static final String WFSTATUS_SUBMITTED = "SUBMITTED";
  public static final String WFSTATUS_PENDINGRESULTS = "PENDINGRESULTS";
  public static final String WFSTATUS_DRAFTED = "DRAFTED";

  public static final String WFSTATUS_SCHEDULED = "SCHEDULED";

  //Validation MDMS Constants
  public static final String PQM_SCHEMA_CODE_CRITERIA = "PQM.QualityCriteria";
  public static final String PQM_SCHEMA_CODE_PLANT = "PQM.Plant";
  public static final String PQM_SCHEMA_CODE_PROCESS = "PQM.Process";
  public static final String PQM_SCHEMA_CODE_STAGE = "PQM.Stage";
  public static final String PQM_SCHEMA_CODE_MATERIAL = "PQM.Material";
  public static final String SCHEMA_CODE_TEST_STANDARD = "PQM.TestStandard";
  public static final String SCHEMA_CODE_PLANTCONFIG = "PQM.PlantConfig";

  public static final String WF_ACTION_SCHEDULE = "SCHEDULE";

  public static final String MDMS_MODULE_TENANT = "tenant";
  public static final String MDMS_MASTER_TENANTS = "tenants";

  public static final Pattern REGEX_METACHARACTER_PATTERN = Pattern.compile(
      "^[a-z0-9]+(-[a-z0-9]+)*$");

  public static final String PQM_PDF_KEY = "pqm-adhoctest";
  public static final Object SEARCH_MODULE_MDMS = "rainmaker-mdms";
  public static final Object TQM_LOC_SEARCH_MODULE = "rainmaker-tqm";



}
