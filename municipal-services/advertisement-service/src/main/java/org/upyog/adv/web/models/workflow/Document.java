package org.upyog.adv.web.models.workflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Minimal Document payload used inside {@link ProcessInstance} for
 * egov-workflow-v2.
 * Mirrors the {@code fileStoreId} / {@code documentType} fields expected by the
 * workflow service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Document {

  @JsonProperty("fileStoreId")
  private String fileStoreId;

  @JsonProperty("documentType")
  private String documentType;
}
