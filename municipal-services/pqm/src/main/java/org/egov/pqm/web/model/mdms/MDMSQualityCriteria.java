package org.egov.pqm.web.model.mdms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MDMSQualityCriteria {

  @JsonProperty("code")
  private String code;

  @JsonProperty("parameter")
  private String parameter;

  @JsonProperty("unit")
  private String unit;

  @JsonProperty("benchmarkRule")
  private String benchmarkRule;

  @JsonProperty("benchmarkValues")
  private List<BigDecimal> benchmarkValues;

  @JsonProperty("allowedDeviation")
  private BigDecimal allowedDeviation;

  @JsonProperty("active")
  private Boolean active;

}
