package org.egov.asset.web.models.calcontract;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

<<<<<<< HEAD
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
=======
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
>>>>>>> master-LTS

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalulationCriteria {

	@JsonProperty("assetId")
	@Size(min = 2, max = 64)
	private String assetId = null;

	@JsonProperty("tenantId")
	@NotNull
	@Size(min = 2, max = 256)
	private String tenantId = null;

	@JsonProperty("serviceType")
	@Size(min = 2, max = 256)
	private String serviceType = null;



}
