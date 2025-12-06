package org.egov.pqm.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.Valid;

import lombok.*;
import org.hibernate.validator.constraints.SafeHtml;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
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
