package org.egov.pt.web.contracts;

import java.util.List;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.pt.models.Appeal;
import org.egov.pt.models.Property;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Contains the ResponseHeader and the created/updated property
 */

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppealResponse   {

	@JsonProperty("ResponseInfo")
  private ResponseInfo responseInfo;

  @JsonProperty("Appeals")
  private List<Appeal> Appeals;
  
  @JsonProperty("count")
  private Integer count;
}
