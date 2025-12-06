package org.egov.pqm.web.model.mdms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.pqm.web.model.QualityCriteria;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MdmsTest {

  @JsonProperty("code")
  private String code = null;

  @JsonProperty("plant")
  private String plant = null;

  @JsonProperty("process")
  private String process = null;

  @JsonProperty("stage")
  private String stage = null;

  @JsonProperty("material")
  private String material = null;

  @JsonProperty("frequency")
  private String frequency = null;

  @JsonProperty("sourceType")
  private String testType = null;

  @JsonProperty("qualityCriteria")
  @Valid
  private List<String> qualityCriteria = new ArrayList<>();

  @JsonProperty("active")
  private Boolean active = null;

}
