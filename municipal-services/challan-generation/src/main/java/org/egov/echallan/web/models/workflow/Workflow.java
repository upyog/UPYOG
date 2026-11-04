package org.egov.echallan.web.models.workflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.echallan.model.DocumentDetail;


import java.util.List;

/**
 * Minimal workflow payload used to pass action/comment/assignees from API to
 * WF.
 * Documents can be added later if required.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Workflow {

  @JsonProperty("action")
  private String action;

  @JsonProperty("assignes")
  private List<String> assignes;

  @JsonProperty("comment")
  private String comment;

  @JsonProperty("documents")
  private List<DocumentDetail> documents = null;

}
