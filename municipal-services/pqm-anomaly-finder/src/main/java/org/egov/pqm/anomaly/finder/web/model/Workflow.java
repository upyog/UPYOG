package org.egov.pqm.anomaly.finder.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.SafeHtml;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Workflow {

  @SafeHtml
  @JsonProperty("action")
  private String action = null;

  @JsonProperty("assignes")
  @Valid
  private List<String> assignes = null;

  @SafeHtml
  @JsonProperty("comments")
  private String comments = null;

  @JsonProperty("verificationDocuments")
  @Valid
  private List<Document> verificationDocuments = null;

  @JsonProperty("rating")
  private Integer rating = null;
}
