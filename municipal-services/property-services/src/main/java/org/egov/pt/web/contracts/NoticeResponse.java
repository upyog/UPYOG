package org.egov.pt.web.contracts;

import java.util.List;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.pt.models.Notice;

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
