package org.egov.ptr.web.contracts;

import java.util.Map;

import javax.validation.constraints.NotEmpty;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PDFRequest {

	private RequestInfo RequestInfo;

	@NotEmpty
	private String key;

	@NotEmpty
	private String tenantId;

	@JsonProperty("data")
	private Map data;

	private String htmlTemplateContent;

	@Builder.Default
	private Boolean isHeaderFooterSkip = false;
}
