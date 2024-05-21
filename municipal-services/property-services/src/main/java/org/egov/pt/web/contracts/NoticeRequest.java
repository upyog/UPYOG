package org.egov.pt.web.contracts;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.models.Notice;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NoticeRequest {
	
	@JsonProperty("RequestInfo")
	  private  RequestInfo requestInfo;

	  @Valid
	  @JsonProperty("Notice")
	  private Notice notice ;

}
