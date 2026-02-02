package org.egov.pt.calculator.web.models.property;

import java.util.List;

import org.egov.common.contract.response.ResponseInfo;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeResponse {

	@JsonProperty("responseInfo")
	private ResponseInfo responseInfo;
	
	@JsonProperty("Notice")
	List<Notice> notice;
}
