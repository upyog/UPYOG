package org.egov.notice.web.model;

import java.util.List;

import javax.json.bind.annotation.JsonbProperty;

import org.egov.common.contract.request.RequestInfo;
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
