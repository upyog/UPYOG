package org.upyog.adv.web.models.workflow;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.upyog.adv.web.models.DocumentDetail;

import javax.validation.Valid;

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
