package org.egov.pqm.validator;


import static org.egov.pqm.util.Constants.REGEX_METACHARACTER_PATTERN;
import static org.egov.pqm.util.Constants.SAVE_AS_DRAFT;
import static org.egov.pqm.util.Constants.UPDATE_RESULT;
import static org.egov.pqm.util.ErrorConstants.CRITERIA_CODE_INVALID_CODE;
import static org.egov.pqm.util.ErrorConstants.CRITERIA_CODE_INVALID_MESSAGE;
import static org.egov.pqm.util.ErrorConstants.FILE_STORE_ID_INVALID_CODE;
import static org.egov.pqm.util.ErrorConstants.FILE_STORE_ID_INVALID_MESSAGE;
import static org.egov.pqm.util.ErrorConstants.ID_CHANGED_ERROR;
import static org.egov.pqm.util.ErrorConstants.ID_CHANGED_MESSAGE;
import static org.egov.pqm.util.ErrorConstants.MATERIAL_CODE_INVALID_CODE;
import static org.egov.pqm.util.ErrorConstants.MATERIAL_CODE_INVALID_MESSAGE;
import static org.egov.pqm.util.ErrorConstants.PLANT_CODE_INVALID_CODE;
import static org.egov.pqm.util.ErrorConstants.PLANT_CODE_INVALID_MESSAGE;
import static org.egov.pqm.util.ErrorConstants.PROCESS_CODE_INVALID_CODE;
import static org.egov.pqm.util.ErrorConstants.PROCESS_CODE_INVALID_MESSAGE;
import static org.egov.pqm.util.ErrorConstants.STAGE_CODE_INVALID_CODE;
import static org.egov.pqm.util.ErrorConstants.STAGE_CODE_INVALID_MESSAGE;
import static org.egov.pqm.util.ErrorConstants.STATUS_ERROR_CODE;
import static org.egov.pqm.util.ErrorConstants.STATUS_ERROR_MESSAGE;
import static org.egov.pqm.util.ErrorConstants.TEST_CRITERIA_NOT_PRESENT;
import static org.egov.pqm.util.ErrorConstants.TEST_TYPE_CAN_ONLY_BE_LAB_ADHOC_CODE;
import static org.egov.pqm.util.ErrorConstants.TEST_TYPE_CAN_ONLY_BE_LAB_ADHOC_MESSAGE;
import static org.egov.pqm.util.ErrorConstants.TEST_TYPE_CAN_ONLY_BE_LAB_SCHEDULED_CODE;
import static org.egov.pqm.util.ErrorConstants.TEST_TYPE_CAN_ONLY_BE_LAB_SCHEDULED_MESSAGE;
import static org.egov.pqm.util.ErrorConstants.TEST_TYPE_INVALID_CODE;
import static org.egov.pqm.util.ErrorConstants.TEST_TYPE_INVALID_MESSAGE;
import static org.egov.pqm.util.PlantUserConstants.PQM_ADMIN;
import static org.egov.pqm.util.PlantUserConstants.PQM_TP_OPERATOR;

import digit.models.coremodels.UserDetailResponse;
import digit.models.coremodels.UserSearchRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.pqm.config.ServiceConfiguration;
import org.egov.pqm.error.Error;
import org.egov.pqm.service.UserService;
import org.egov.pqm.web.model.Document;
import org.egov.pqm.web.model.QualityCriteria;
import org.egov.pqm.web.model.SourceType;
import org.egov.pqm.web.model.Test;
import org.egov.pqm.web.model.TestRequest;
import org.egov.pqm.web.model.TestResultStatus;
import org.egov.pqm.web.model.TestSearchRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class PqmValidator {

  @Autowired
  private ServiceConfiguration config;

  @Autowired
  private UserService userService;

  public void validateTestCriteriaAndDocument(TestRequest testRequest) {
    validateTestCriteria(testRequest);
    if (Objects.equals(testRequest.getTests().get(0).getSourceType(), SourceType.LAB_ADHOC)
        || (testRequest.getTests().get(0).getWorkflow() != null && (Objects.equals(
        testRequest.getTests().get(0).getWorkflow().getAction(), UPDATE_RESULT) || Objects.equals(
        testRequest.getTests().get(0).getWorkflow().getAction(), SAVE_AS_DRAFT)))
    ) {
      validateDocumentFields(testRequest);
    } else {
      validateDocumentPresence(testRequest);
    }
  }

  private void validateDocumentPresence(TestRequest testRequest) {
    if (testRequest.getTests().get(0).getDocuments() != null && (!testRequest.getTests().get(0)
        .getDocuments().isEmpty())) {
      throw new CustomException("DOCUMENT IS ONLY ATTACHED IN UPDATE_RESULT ACTION ",
          "Document is only attached in update result action");

    }
  }

  public void validateTestTypeAdhocCreate(TestRequest testRequest) {
    if (testRequest.getTests().get(0).getSourceType() == null
        || testRequest.getTests().get(0).getSourceType() == SourceType.IOT_SCHEDULED
        || testRequest.getTests().get(0).getSourceType() == SourceType.LAB_SCHEDULED) {
      throw new CustomException(TEST_TYPE_CAN_ONLY_BE_LAB_ADHOC_CODE,
          TEST_TYPE_CAN_ONLY_BE_LAB_ADHOC_MESSAGE);
    }
  }

  public void validateTestTypeScheduleCreateAndUpdate(TestRequest testRequest) {
    if (testRequest.getTests().get(0).getSourceType() == null
        || testRequest.getTests().get(0).getSourceType() == SourceType.IOT_SCHEDULED
        || testRequest.getTests().get(0).getSourceType() == SourceType.LAB_ADHOC) {
      throw new CustomException(TEST_TYPE_CAN_ONLY_BE_LAB_SCHEDULED_CODE,
          TEST_TYPE_CAN_ONLY_BE_LAB_SCHEDULED_MESSAGE);
    }
  }

  private void validateTestCriteria(TestRequest testRequest) {
    if (testRequest.getTests().get(0).getQualityCriteria() == null || testRequest.getTests()
        .get(0)
        .getQualityCriteria().isEmpty()) {
      throw new CustomException(TEST_CRITERIA_NOT_PRESENT, "test criteria not present");
    }
  }

  private void validateDocumentFields(TestRequest testRequest) {
    boolean isValid = true;
    if (testRequest.getTests().get(0).getDocuments() != null && !testRequest.getTests().get(0)
        .getDocuments().isEmpty()) {
      for (Document document : testRequest.getTests().get(0).getDocuments()) {
        if (document.getFileStoreId() != null) {
          if (!REGEX_METACHARACTER_PATTERN.matcher(document.getFileStoreId()).find()) {
            throw new CustomException(FILE_STORE_ID_INVALID_CODE, FILE_STORE_ID_INVALID_MESSAGE);
          }
        } else {
          continue;
        }
      }
    }
  }

  public void validateTestRequestFieldsWhileupdate
      (List<Test> testList, List<Test> oldTestList) {
    Test test = testList.get(0);
    Test oldTest = oldTestList.get(0);
    if (test.getSourceType() != oldTest.getSourceType()) {
      throw new CustomException(TEST_TYPE_INVALID_CODE, TEST_TYPE_INVALID_MESSAGE);
    }
    if (!Objects.equals(test.getPlantCode(), oldTest.getPlantCode())) {
      throw new CustomException(PLANT_CODE_INVALID_CODE, PLANT_CODE_INVALID_MESSAGE);
    }
    if (!Objects.equals(test.getProcessCode(), oldTest.getProcessCode())) {
      throw new CustomException(PROCESS_CODE_INVALID_CODE, PROCESS_CODE_INVALID_MESSAGE);
    }
    if (!Objects.equals(test.getMaterialCode(), oldTest.getMaterialCode())) {
      throw new CustomException(MATERIAL_CODE_INVALID_CODE, MATERIAL_CODE_INVALID_MESSAGE);
    }
    if (!Objects.equals(test.getStageCode(), oldTest.getStageCode())) {
      throw new CustomException(STAGE_CODE_INVALID_CODE, STAGE_CODE_INVALID_MESSAGE);
    }

    Map<String, String> criteriaMap = new HashMap<>();
    for (QualityCriteria oldCriteria : oldTest.getQualityCriteria()) {  // Populate the criteriaMap with criteria codes and associated IDs from oldTest
      criteriaMap.put(oldCriteria.getCriteriaCode(), oldCriteria.getId());
    }
    for (QualityCriteria criteria : test.getQualityCriteria()) {        // Check criteria codes and associated IDs in test against the criteriaMap
      String associatedId = criteriaMap.get(criteria.getCriteriaCode());
      if (associatedId == null || !associatedId.equals(criteria.getId())) {
        throw new CustomException(CRITERIA_CODE_INVALID_CODE, CRITERIA_CODE_INVALID_MESSAGE);
      } else {
        criteriaMap.remove(criteria.getCriteriaCode());    // Remove the criteria code from the map to mark it as processed
      }
    }
    if (!criteriaMap.isEmpty()) {        // Check if there are any remaining criteria codes in the map
      throw new CustomException(CRITERIA_CODE_INVALID_CODE, CRITERIA_CODE_INVALID_MESSAGE);
    }

    if (test.getStatus() != TestResultStatus.PENDING) {
      throw new CustomException(STATUS_ERROR_CODE, STATUS_ERROR_MESSAGE);
    }

    if (!test.getId().equals(oldTest.getId())) {
      throw new CustomException(ID_CHANGED_ERROR, ID_CHANGED_MESSAGE);
    }
  }

  public void validateSearchRequest(TestSearchRequest testSearchRequest, RequestInfo requestInfo) {
    validateUserRole(requestInfo, Arrays.asList(PQM_TP_OPERATOR, PQM_ADMIN));
    validateSearchCriteria(testSearchRequest);
  }

  private void validateSearchCriteria(TestSearchRequest testSearchRequest) {
    if (Objects.isNull(testSearchRequest.getTestSearchCriteria())) {
      throw Error.mandatory_field_missing.getBuilder("TestSearchRequest", "testSearchCriteria",
          "TestSearchCriteria").build();
    }
  }

  private void validateUserRole(RequestInfo requestInfo, List<String> roles) {
    User user = checkAndGetUserDetails(requestInfo);
    Set<String> roleCodes = user.getRoles().stream().map(Role::getCode).collect(Collectors.toSet());
    if (!CollectionUtils.containsAny(roleCodes, roles)) {
      throw Error.invalid_applicant_error.getBuilder("Search", requestInfo.getUserInfo().getUuid(),
          Arrays.asList(PQM_TP_OPERATOR, PQM_ADMIN).toString()).build();
    }

    requestInfo.getUserInfo().setRoles(user.getRoles());
  }

  private User checkAndGetUserDetails(RequestInfo requestInfo) {
    if (requestInfo.getUserInfo() == null) {
      throw Error.mandatory_field_missing.getBuilder("RequestInfo", "userInfo",
          User.class.getName()).build();
    }

    UserSearchRequest userSearchRequest = new UserSearchRequest();
    userSearchRequest.setUuid(Collections.singletonList(requestInfo.getUserInfo().getUuid()));

    UserDetailResponse userDetailResponse = userService.userCall(userSearchRequest,
        new StringBuilder(config.getUserHost()).append(config.getUserSearchEndpoint()));

    if (CollectionUtils.isEmpty(userDetailResponse.getUser())) {
      throw Error.record_not_found.getBuilder(User.class.getName(),
          requestInfo.getUserInfo().getUuid()).build();
    } else {
      return userDetailResponse.getUser().get(0);
    }
  }
}
