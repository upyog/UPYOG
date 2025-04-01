package org.egov.pqm.service;


import static org.egov.pqm.util.Constants.BETWEEN;
import static org.egov.pqm.util.Constants.EQUALS;
import static org.egov.pqm.util.Constants.GREATER_THAN;
import static org.egov.pqm.util.Constants.GREATER_THAN_EQUAL_TO;
import static org.egov.pqm.util.Constants.LESS_THAN;
import static org.egov.pqm.util.Constants.LESS_THAN_EQUAL_TO;
import static org.egov.pqm.util.Constants.MASTER_NAME_QUALITY_CRITERIA;
import static org.egov.pqm.util.Constants.NOT_EQUAL;
import static org.egov.pqm.util.Constants.OUTSIDE_RANGE;
import static org.egov.pqm.util.MDMSUtils.parseJsonToMap;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.egov.pqm.config.ServiceConfiguration;
import org.egov.pqm.util.ErrorConstants;
import org.egov.pqm.util.MDMSUtils;
import org.egov.pqm.validator.PqmValidator;
import org.egov.pqm.web.model.QualityCriteria;
import org.egov.pqm.web.model.Test;
import org.egov.pqm.web.model.TestRequest;
import org.egov.pqm.web.model.TestResultStatus;
import org.egov.pqm.web.model.mdms.MDMSQualityCriteria;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class QualityCriteriaEvaluationService {

  @Autowired
  private PqmValidator pqmValidator;
  @Autowired
  private MDMSUtils mdmsUtil;

  @Autowired
  private ServiceConfiguration config;

  /**
   * Evaluates QualityCriteria list for a Test Object
   *
   * @param testRequest The Test Request Object
   */
  public void evalutateQualityCriteria(TestRequest testRequest) {
    Test test = testRequest.getTests().get(0);

    //fetch mdms data for QualityCriteria Master
    Object jsondata = mdmsUtil.mdmsCallV2(testRequest.getRequestInfo(),
        testRequest.getTests().get(0).getTenantId().split("\\.")[0], MASTER_NAME_QUALITY_CRITERIA, new ArrayList<>());
    String jsonString = "";

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      jsonString = objectMapper.writeValueAsString(jsondata);
    } catch (Exception e) {
      throw new CustomException(ErrorConstants.PARSING_ERROR,
          "Unable to parse QualityCriteria mdms data ");
    }

    // Parse JSON Response and create the map for QualityCriteria
    Map<String, MDMSQualityCriteria> codeToQualityCriteriaMap = parseJsonToMap(jsonString);

    //evaluate Quality Criteria
    List<QualityCriteria> evaluatedqualityCriteriaList = new ArrayList<>();
    for (QualityCriteria qualityCriteria : test.getQualityCriteria()) {
      if (qualityCriteria.getResultValue() == null) {
        throw new CustomException("RESULT_VALUE_INVALID", "result value should not be null");
      }
      QualityCriteria evaluatedqualityCriteria = enrichQualityCriteriaFields(
          codeToQualityCriteriaMap.get(qualityCriteria.getCriteriaCode()),
          qualityCriteria);

      evaluatedqualityCriteriaList.add(evaluatedqualityCriteria);
    }
    test.setQualityCriteria(evaluatedqualityCriteriaList);
  }
  
  public void validateQualityCriteriaResult(TestRequest testRequest) {
	    testRequest.getTests().stream()
	            .flatMap(test -> test.getQualityCriteria().stream())
	            .filter(qualityCriteria -> qualityCriteria.getResultValue() != null)
	            .findAny()
	            .ifPresent(qualityCriteria -> {
	                throw new CustomException("RESULT_VALUE_INVALID", "Invalid state to submit qualityCriteria result value");
	            });
	}
  /**
   * returns a qualityCriteria with enriched status and allowedDeviation
   *
   * @param mdmsQualityCriteria     MDMS Quality Criteria
   * @param incomingQualityCriteria Quality Criteria from request
   * @return QualityCriteria
   */
  public QualityCriteria enrichQualityCriteriaFields(MDMSQualityCriteria mdmsQualityCriteria,
      QualityCriteria incomingQualityCriteria) {
    BigDecimal resultValue = incomingQualityCriteria.getResultValue();
    String criteriaCode = mdmsQualityCriteria.getCode();
    String benchmarkRule = mdmsQualityCriteria.getBenchmarkRule();
    List<BigDecimal> benchmarkValues = mdmsQualityCriteria.getBenchmarkValues();
    BigDecimal allowedDeviation = mdmsQualityCriteria.getAllowedDeviation();

    boolean areBenchmarkRulesMet = isBenchmarkMet(resultValue, benchmarkRule,
        benchmarkValues.toArray(new BigDecimal[0]),
        allowedDeviation);

    QualityCriteria qualityCriteria = QualityCriteria.builder()
        .id(incomingQualityCriteria.getId())
        .testId(incomingQualityCriteria.getTestId())
        .criteriaCode(criteriaCode)
        .resultValue(resultValue)
        .isActive(Boolean.TRUE)
        .allowedDeviation(mdmsQualityCriteria.getAllowedDeviation())
        .resultStatus(TestResultStatus.PENDING).build();

    if (areBenchmarkRulesMet) {
      qualityCriteria.setResultStatus(TestResultStatus.PASS);
    } else {
      qualityCriteria.setResultStatus(TestResultStatus.FAIL);
    }

    return qualityCriteria;
  }


  /**
   * Parsing Json Data to a Code-QualityCriteria Map
   *
   * @param valueToCheck     Value to Test
   * @param benchmarkRule    Benchmark Rule to test against
   * @param benchmarkValues  Benchmark Values to test against
   * @param allowedDeviation Allowed Deviation
   * @return isBenchmarkMet - true if benchmark is met
   */
  private boolean isBenchmarkMet(BigDecimal valueToCheck, String benchmarkRule,
      BigDecimal[] benchmarkValues, BigDecimal allowedDeviation) {

    BigDecimal lowerBound;
    BigDecimal upperBound;

    if (allowedDeviation == null) {
      allowedDeviation = BigDecimal.valueOf(0);
    }

    switch (benchmarkRule) {
      case EQUALS:
        lowerBound = benchmarkValues[0].subtract(allowedDeviation);
        upperBound = benchmarkValues[0].add(allowedDeviation);
        return (valueToCheck.compareTo(lowerBound) >= 0 && valueToCheck.compareTo(upperBound) <= 0);

      case NOT_EQUAL:
        lowerBound = benchmarkValues[0].subtract(allowedDeviation);
        upperBound = benchmarkValues[0].add(allowedDeviation);
        return (valueToCheck.compareTo(lowerBound) < 0 || valueToCheck.compareTo(upperBound) > 0);

      case LESS_THAN:
        upperBound = benchmarkValues[0].add(allowedDeviation);
        return (valueToCheck.compareTo(upperBound) < 0);

      case LESS_THAN_EQUAL_TO:
        upperBound = benchmarkValues[0].add(allowedDeviation);
        return (valueToCheck.compareTo(upperBound) <= 0);

      case GREATER_THAN:
        lowerBound = benchmarkValues[0].subtract(allowedDeviation);
        return (valueToCheck.compareTo(lowerBound) > 0);

      case GREATER_THAN_EQUAL_TO:
        lowerBound = benchmarkValues[0].subtract(allowedDeviation);
        return (valueToCheck.compareTo(lowerBound) >= 0);

      case BETWEEN:
        lowerBound = benchmarkValues[0].subtract(allowedDeviation);
        upperBound = benchmarkValues[1].add(allowedDeviation);
        return (valueToCheck.compareTo(lowerBound) >= 0 && valueToCheck.compareTo(upperBound) <= 0);

      case OUTSIDE_RANGE:
        lowerBound = benchmarkValues[0].add(allowedDeviation);
        upperBound = benchmarkValues[1].subtract(allowedDeviation);
        return (valueToCheck.compareTo(lowerBound) < 0 || valueToCheck.compareTo(upperBound) > 0);

      default:
        return false;
    }
  }

}
