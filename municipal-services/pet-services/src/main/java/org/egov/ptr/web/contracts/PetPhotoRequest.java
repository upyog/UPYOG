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
public class PetPhotoRequest {

	private RequestInfo RequestInfo;

	@Builder.Default
	private String serviceName = "PTR";

	@NotEmpty
	private String objectId;
	
	@Builder.Default
	private String documentId="9";

	@Builder.Default
	private String documentType = "CITIZ";
}
