package digit.web.models;

import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Contract class to receive request. Array of items are used in case of create,
 * whereas single item is used for update.
 */
@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SchemeApplicationRequest {

  @JsonProperty("RequestInfo")
  private RequestInfo requestInfo;

  @JsonProperty("SchemeApplications")
  @Builder.Default
  @Valid
  private List<SchemeApplication> schemeApplications = null;

  public SchemeApplicationRequest addSchemeApplicationItem(SchemeApplication schemeApplicationItem) {
    if (this.schemeApplications == null) {
      this.schemeApplications = new ArrayList<>();
    }
    this.schemeApplications.add(schemeApplicationItem);
    return this;
  }

  private Long schemeId;

  @JsonProperty("aadhardob")
  private Date aadhardob;

  private Double income;

  private String gender;

  private Long userId;

  private Double divyangPercent;

  

}
