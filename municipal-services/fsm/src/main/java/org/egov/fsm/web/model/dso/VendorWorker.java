package org.egov.fsm.web.model.dso;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.egov.fsm.web.model.AuditDetails;
import org.egov.fsm.web.model.worker.WorkerStatus;
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.validation.annotation.Validated;

/**
 * Capture the Vendor Tagged Worker information in the system.
 */
@Validated
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class VendorWorker {

  @SafeHtml
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("tenantId")
  @SafeHtml
  @Size(max = 64)
  private String tenantId = null;

  @JsonProperty("vendorId")
  @SafeHtml
  @Size(max = 64)
  private String vendorId = null;

  @JsonProperty("individualId")
  @SafeHtml
  @Size(max = 64)
  private String individualId = null;

  @JsonProperty("additionalDetails")
  private Object additionalDetails = null;

  @JsonProperty("auditDetails")
  private AuditDetails auditDetails = null;

  @JsonProperty("vendorWorkerStatus")
  private WorkerStatus vendorWorkerStatus = null;

}

