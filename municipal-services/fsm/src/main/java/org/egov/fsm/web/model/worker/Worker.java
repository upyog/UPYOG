package org.egov.fsm.web.model.worker;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.egov.fsm.web.model.AuditDetails;
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.validation.annotation.Validated;


/**
 * Capture the Worker information in the system.
 */
@Validated
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Worker {

  @SafeHtml
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("tenantId")
  @SafeHtml
  @Size(max = 64)
  private String tenantId = null;

  @JsonProperty("applicationId")
  @SafeHtml
  @Size(max = 64)
  private String applicationId = null;

  @JsonProperty("individualId")
  @SafeHtml
  @Size(max = 64)
  private String individualId = null;

  @JsonProperty("workerType")
  private WorkerType workerType = null;

  @JsonProperty("status")
  private WorkerStatus status = null;

  @JsonProperty("additionalDetails")
  private Object additionalDetails = null;

  @JsonProperty("auditDetails")
  private AuditDetails auditDetails = null;
}
