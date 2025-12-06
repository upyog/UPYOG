package org.egov.pqm.anomaly.finder.web.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Test {

	  @JsonProperty("id")
	  private String id;

	  @JsonProperty("testId")
	  private String testId;

	  @JsonProperty("testCode")
	  private String testCode;

	  @JsonProperty("tenantId")
	  private String tenantId;

	  @JsonProperty("plantCode")
	  private String plantCode;

	  @JsonProperty("processCode")
	  private String processCode;

	  @JsonProperty("stageCode")
	  private String stageCode;

	  @JsonProperty("materialCode")
	  private String materialCode;

	  @JsonProperty("deviceCode")
	  private String deviceCode;

	  @JsonProperty("testCriteria")
	  @Valid
	  private List<QualityCriteria> qualityCriteria = new ArrayList<>();

	  @JsonProperty("status")
	  private TestResultStatus status;

	  @JsonProperty("wfStatus")
	  private String wfStatus;

	  @JsonProperty("testType")
	  private SourceType sourceType;

	  @JsonProperty("scheduledDate")
	  private Long scheduledDate;

	  @JsonProperty("isActive")
	  private Boolean isActive;

	  @JsonProperty("type")
	  private TypeEnum type = null;

	  @JsonProperty("documents")
	  @Valid
	  private List<Document> documents;

	  @JsonProperty("additionalDetails")
	  private Object additionalDetails;

	  @JsonProperty("auditDetails")
	  private AuditDetails auditDetails;

	  @JsonProperty("workflow")
	  private Workflow workflow;
	  
	  @JsonProperty("labAssignedTo")
	  private String labAssignedTo;
	}