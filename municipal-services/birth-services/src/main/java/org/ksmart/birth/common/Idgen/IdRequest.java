package org.ksmart.birth.common.Idgen;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdRequest {

	@JsonProperty("idName")
	@NotNull
	private String idName;

	@NotNull
	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("moduleCode")
	@NotNull
	private String moduleCode;

	@JsonProperty("fnType")
	@NotNull
	private String fnType;

//	@JsonProperty("format")
//	private String format;

}
