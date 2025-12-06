package org.egov.pqm.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestSearchCriteria {

  @JsonProperty("ids")
  private List<String> ids = null;

  @JsonProperty("testIds")
  @Valid
  private List<String> testIds = null;

  @JsonProperty("testId")
  private String testId = null;

  @JsonProperty("tenantId")
  private String tenantId = null;

  @JsonProperty("testCode")
  private List<String> testCode = null;

  @JsonProperty("plantCodes")
  @Valid
  private List<String> plantCodes = null;

  @JsonProperty("processCodes")
  @Valid
  private List<String> processCodes = null;

  @JsonProperty("stageCodes")
  @Valid
  private List<String> stageCodes = null;

  @JsonProperty("materialCodes")
  @Valid
  private List<String> materialCodes = null;

  @JsonProperty("deviceCodes")
  @Valid
  private List<String> deviceCodes = null;

  @JsonProperty("wfStatus")
  private List<String> wfStatus = null;

  @JsonProperty("status")
  private List<String> status = null;

  @JsonProperty("testType")
  private List<String> sourceType = null;

  @JsonProperty("fromDate")
  private Long fromDate = null;

  @JsonProperty("toDate")
  private Long toDate = null;
  
  @JsonProperty("offset")
  private Integer offset;

  @JsonProperty("limit")
  private Integer limit;

  @JsonProperty("labAssignedTo")
  private String labAssignedTo = null;

}
