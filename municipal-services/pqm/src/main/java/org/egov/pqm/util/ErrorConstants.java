package org.egov.pqm.util;

import org.apache.kafka.common.protocol.types.Field.Str;
import org.bouncycastle.pqc.crypto.newhope.NHOtherInfoGenerator.PartyU;

public class ErrorConstants {

  public static final String CREATE_ERROR = "CREATE_ERROR";
  public static final String UPDATE_ERROR = "UPDATE_ERROR";
  public static final String ID_CHANGED_ERROR = "ID_CHANGED_ERROR";
  public static final String ID_CHANGED_MESSAGE = "id_cannot_be_changed";


  public static final String PARSING_ERROR = "PARSING_ERROR";
  public static final String IDGEN_ERROR = "IDGEN_ERROR";
  public static final String TEST_NOT_IN_DB = "TEST_NOT_PRESENT_IN_DB";
  public static final String TEST_CRITERIA_NOT_PRESENT = "TEST_CRITERIA_NOT_PRESENT";
  public static final String TEST_TYPE_CAN_ONLY_BE_LAB_SCHEDULED_CODE = "TEST_TYPE_CAN_ONLY_BE_LAB_SCHEDULED";
  public static final String TEST_TYPE_CAN_ONLY_BE_LAB_ADHOC_CODE = "TEST_TYPE_CAN_ONLY_BE_LAB_ADHOC";
  public static final String TEST_TYPE_CAN_ONLY_BE_LAB_SCHEDULED_MESSAGE = "Test Type can only be Lab Scheduled";
  public static final String TEST_TYPE_CAN_ONLY_BE_LAB_ADHOC_MESSAGE = "Test Type can only be Lab Adhoc";
  public static final String FILE_STORE_ID_INVALID_CODE = "FILE_STORE_ID_INVALID";
  public static final String FILE_STORE_ID_INVALID_MESSAGE = "FileStoreId can only be small case alphabets and digits ";
  public static final String TEST_TYPE_INVALID_CODE = "TEST_TYPE_DIFFERENT";

  //ERROR CONSTANTS WHILE UPDATE API
  public static final String TEST_TYPE_INVALID_MESSAGE = "Test type did not match";
  public static final String PLANT_CODE_INVALID_CODE = "PLANT_CODE_DIFFERENT";
  public static final String PLANT_CODE_INVALID_MESSAGE = "Plant code did not match";
  public static final String PROCESS_CODE_INVALID_CODE = "PROCESS_CODE_DIFFERENT";
  public static final String PROCESS_CODE_INVALID_MESSAGE = "Process code did not match";
  public static final String MATERIAL_CODE_INVALID_CODE = "MATERIAL_CODE_DIFFERENT";
  public static final String MATERIAL_CODE_INVALID_MESSAGE = "Material code did not match";
  public static final String STAGE_CODE_INVALID_CODE = "STAGE_CODE_DIFFERENT";
  public static final String STAGE_CODE_INVALID_MESSAGE = "Stage code did not match";
  public static final String CRITERIA_CODE_INVALID_CODE = "CRITERIA_CODE_DIFFERENT";
  public static final String CRITERIA_CODE_INVALID_MESSAGE = "Criteria code did not match";
  public static final String STATUS_ERROR_CODE = "RESULT STATUS CAN ONLY BE PENDING IF WORKFLOW STATUS IS ANYHTING OTHER THAN SUBMITTED ";
  public static final String STATUS_ERROR_MESSAGE = "Result status can only be pending if workflow status is anything other than submitted";
  public static final String TEST_NOT_PRESENT_CODE= "TEST NOT PRESENT";
  public static final String TEST_NOT_PRESENT_MESSAGE= "test not present";

  public static final String INVALID_APPLICANT_ERROR = "INVALID_APPLICANT_ERROR";
  public static final String PQM_TP_OPERATOR_EMPLOYEE_INVALID_ERROR = "PQM_TP_OPERATOR_EMPLOYEE_INVALID_ERROR";
  public static final String PLANT_EMPLOYEE_MAP_EXISTS_ERROR = "PLANT_EMPLOYEE_MAP_EXISTS_ERROR";

  public static final String NO_TENANT_PRESENT_ERROR = "NO_TENANT_PRESENT_ERROR";
  public static final String NO_TENANT_PRESENT_ERROR_DESC = "No tenant found in MDMS for Scheduling of tests";
public static final String PLANT_USER_TYPE_INVALID_ERROR = "PLANT_USER_TYPE_INVALID_ERROR";

  public static final String PLANT_PLANT_CONFIG_DATA_NOT_PRESENT_ERROR = "PLANT_PLANT_CONFIG_DATA_NOT_PRESENT_ERROR";
  public static final String PLANT_PLANT_CONFIG_DATA_NOT_PRESENT_ERROR_DESC = "Plant or Plant Config Data not found in MDMS";

  public static final String PQM_ANOMALY_SEARCH_ERROR = "PQM_ANOMALY_SEARCH_ERROR";
  public static final String PQM_ANOMALY_SEARCH_ERROR_DESC = "No Anomalies returned from PQM Anomaly Service";

  public static final String PQM_SEARCH_ERROR = "PQM_SEARCH_ERROR";
  public static final String PQM_SEARCH_ERROR_DESC = "No Test found for the given testId in Database";


  public static final String PQM_LOCALIZATION_ERROR = "PQM_LOCALIZATION_ERROR";
  public static final String PQM_LOCALIZATION_ERROR_DESC_UNIT = "No Localization found for this Unit Code";
  public static final String PQM_LOCALIZATION_ERROR_DESC_QUALITYCRITERIA = "No Localization found for this QualityCriteria Code";

}