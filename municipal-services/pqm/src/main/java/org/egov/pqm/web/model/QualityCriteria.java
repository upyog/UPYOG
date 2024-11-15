package org.egov.pqm.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class QualityCriteria {

   @JsonProperty("id")
   private String id;

   @JsonProperty("testId")
   private String testId;

  @JsonProperty("criteriaCode")
  private String criteriaCode = null;

  @JsonProperty("criteriaName")
  private String criteriaName;

  @JsonProperty("uom")
  private String uom;

  @JsonProperty("benchmarkValue")
  private List<BigDecimal> benchmarkValue;

  @JsonProperty("resultValue")
  private BigDecimal resultValue = null;

  @JsonProperty("allowedDeviation")
  private BigDecimal allowedDeviation = null;

  @JsonProperty("resultStatus")
  private TestResultStatus resultStatus = null;

  @JsonProperty("isActive")
  private Boolean isActive = Boolean.TRUE;

  @JsonProperty("auditDetails")
  private AuditDetails auditDetails;
}
